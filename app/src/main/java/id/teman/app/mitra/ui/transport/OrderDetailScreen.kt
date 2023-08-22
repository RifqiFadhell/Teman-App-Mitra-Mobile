package id.teman.app.mitra.ui.transport

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ResponsiveText
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.transport.PaymentType
import id.teman.app.mitra.domain.model.transport.TransportOrderPaymentSpec
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.domain.model.transport.TransportRequestType
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.ui.destinations.ChatScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.transport.common.OrderDetailSingleItemUI
import id.teman.app.mitra.ui.transport.common.OrderDetailSingleItemUIClickable
import id.teman.app.mitra.ui.transport.common.PaymentItemRow
import id.teman.app.mitra.ui.transport.common.getCurrentTimeFormat
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Destination
@Composable
fun OrderDetailScreen(
    navigator: DestinationsNavigator,
    item: TransportOrderSpec,
    viewModel: TransportViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<Boolean>,
) {
    var isShowCallClient by remember { mutableStateOf(false) }
    var isShowCancelledOrder by remember { mutableStateOf(false) }
    var dropOffCaption by remember { mutableStateOf("") }
    var pickUpCaption by remember { mutableStateOf("") }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val call = Intent(Intent.ACTION_CALL, Uri.parse("tel:081231233697"))
            context.startActivity(call)
        } else {
            // Permission Denied: Do something
        }
    }

    val uiState = viewModel.transportUiState

    if (isShowCallClient) {
        showCallDialog(item.customerPhoneNumber, onClick = {
            isShowCallClient = it
        })
    }

    if (isShowCancelledOrder) {
        ShowCancelledOrder(onClick = { isCancel ->
            isShowCancelledOrder = isCancel
            if (isCancel) {
                viewModel.rejectOrderRequest(item)
            }
        })
    }

    LaunchedEffect(key1 = uiState.redirectToTransportStart, block = {
        uiState.redirectToTransportStart?.consumeOnce {
            resultNavigator.navigateBack(true)
        }
    })

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(Theme.dimension.size_16dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                GlideImage(
                    imageModel = R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .size(Theme.dimension.size_24dp)
                        .noRippleClickable {
                            navigator.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
                Text(
                    "Detail Pesanan", style = UiFont.poppinsP3SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
            if (item.orderStatus == TransportRequestType.FINISHED || item.orderStatus == TransportRequestType.ARRIVED) {
                dropOffCaption = "Tiba di titik Tujuan ${getCurrentTimeFormat(item.dropOffTime, "HH:mm, dd MMM yyyy")}"
                pickUpCaption = "Tiba di titik Penjemputan ${getCurrentTimeFormat(item.pickUpTime, "HH:mm, dd MMM yyyy")}"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.dimension.size_8dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Rating dari Pelanggan untukmu",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier
                            .padding(
                                bottom = Theme.dimension.size_12dp,
                                top = Theme.dimension.size_10dp
                            ),
                        textAlign = TextAlign.Center,
                    )
                }
                val rating = item.rating.orZero()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0 until 5) {
                        if (i >= rating) {
                            GlideImage(
                                imageModel = R.drawable.ic_star,
                                imageOptions = ImageOptions(
                                    colorFilter = ColorFilter.tint(
                                        color = UiColor.neutral100
                                    )
                                ),
                                modifier = Modifier
                                    .size(Theme.dimension.size_30dp)
                            )
                        } else {
                            GlideImage(
                                imageModel = R.drawable.ic_star,
                                imageOptions = ImageOptions(
                                    colorFilter = ColorFilter.tint(
                                        color = UiColor.primaryYellow500
                                    )
                                ),
                                modifier = Modifier
                                    .size(Theme.dimension.size_30dp)
                            )
                        }
                    }
                }
                Text(
                    "Catatan dari Pelanggan untukmu",
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier
                        .padding(
                            bottom = Theme.dimension.size_12dp,
                            top = Theme.dimension.size_10dp
                        ),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = item.notesRating.orEmpty().ifEmpty { "Tidak ada catatan" },
                    shape = RoundedCornerShape(Theme.dimension.size_12dp),
                    placeholder = {},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = UiColor.neutral100,
                        cursorColor = UiColor.black,
                        unfocusedBorderColor = UiColor.neutral100,
                        textColor = UiColor.black
                    ),
                    onValueChange = {}, enabled = false
                )
            } else {
                dropOffCaption = "Titik Tujuan Pengiriman"
                pickUpCaption = "Titik Penjemputan"
            }
            if (item.packageType.isNotNullOrEmpty()) {
                Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                Text(
                    "Penerima", style = UiFont.poppinsH5Bold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                OrderDetailSingleItemUIClickable(
                    icon = R.drawable.ic_person,
                    title = item.receiverName.ifEmpty { "Tidak Ada Catatan" },
                    subtitle = "62${item.receiverNumber}"
                ) {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) -> {
                            // Some works that require permission
                            val call = Intent(
                                Intent.ACTION_CALL,
                                Uri.parse("tel:62${item.receiverNumber}")
                            )
                            context.startActivity(call)
                        }
                        else -> {
                            launcher.launch(Manifest.permission.CALL_PHONE)
                        }
                    }
                }
                Row {
                    InfoDetailSend(icon = R.drawable.box_fix, title = item.packageType)
                }
                Column(modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)) {
                    Text(
                        "Berat Total",
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_8dp,
                            top = Theme.dimension.size_8dp
                        )
                    )
                    Text(
                        "Maks. ${item.packageWeight}Kg",
                        style = UiFont.poppinsP2SemiBold,
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_4dp,
                            top = Theme.dimension.size_0dp
                        )
                    )
                }
                Divider(
                    color = UiColor.neutral50,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_8dp, bottom = Theme.dimension.size_16dp)
                        .height(Theme.dimension.size_1dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
            OrderDetailSingleItemUI(
                icon = R.drawable.location, title = item.pickupAddress,
                subtitle = pickUpCaption
            )
            OrderDetailSingleItemUI(
                icon = R.drawable.location, title = item.destinationAddress,
                subtitle = dropOffCaption
            )
            OrderDetailSingleItemUI(
                icon = R.drawable.ic_notes, title = item.notes.ifEmpty { "Tidak Ada Catatan" },
                subtitle = "Catatan"
            )
            if (item.paymentMethod == "cash") {
                OrderDetailSingleItemUI(
                    icon = R.drawable.ic_dollar, title = item.totalPrice.convertToRupiah(),
                    subtitle = "Penagihan ke Pelanggan"
                )
            }
            OrderDetailSingleItemUI(
                icon = R.drawable.ic_dollar, title = item.driverIncome.convertToRupiah(),
                subtitle = "Pendapatan Bersih Kamu"
            )
            if (item.driverType == DriverMitraType.FOOD) {
                OrderDetailSingleItemUI(
                    icon = R.drawable.ic_dollar,
                    title = item.totalPriceResto.convertToRupiah(),
                    subtitle = "DiBayarkan ke Restoran via ${item.paymentMethod}",
                    showDivider = false
                )
            }
            PaymentDetail(
                isCash = item.paymentMethod == "cash",
                item.paymentBreakdown,
                item.itemsFood
            )
            if (!item.orderStatus.isTerminalStatus()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(
                        top = Theme.dimension.size_32dp
                    )
                ) {
                    OrderDetailBottomButton(R.drawable.ic_call, "Telepon", UiColor.success500) {
                        isShowCallClient = true
                    }
                    Spacer(modifier = Modifier.padding(end = Theme.dimension.size_16dp))
                    OrderDetailBottomButton(
                        R.drawable.ic_chat,
                        "Kirim Pesan",
                        UiColor.tertiaryBlue500
                    ) {
                        navigator.navigate(ChatScreenDestination(item))
                    }
                    Spacer(modifier = Modifier.padding(end = Theme.dimension.size_16dp))
                    if (item.orderStatus != TransportRequestType.ONROUTE) {
                        OrderDetailBottomButton(
                            R.drawable.ic_order_cancel,
                            "Batalkan",
                            UiColor.neutral300
                        ) {
                            isShowCancelledOrder = true
                        }
                    }
                }
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        }
    }
}

fun TransportRequestType.isTerminalStatus() =
    this == TransportRequestType.ARRIVED || this == TransportRequestType.FINISHED
            || this == TransportRequestType.REJECTED

@Composable
private fun ShowCancelledOrder(onClick: (Boolean) -> Unit) {
    GeneralDialogPrompt(
        title = "Batalkan Pesanan?",
        subtitle = "Kamu dapat terkena penalti bila membatalkan tanpa ada alasan",
        actionButtons = {
            GeneralActionButton(
                text = "Iya, Batalkan",
                textColor = UiColor.primaryRed500,
                isFirstAction = true
            ) {
                onClick(true)
            }
            GeneralActionButton(
                text = "Tidak, Kembali",
                textColor = UiColor.neutral900,
                isFirstAction = false
            ) {
                onClick(false)
            }
        },
        onDismissRequest = { onClick(false) }
    )
}

@Composable
private fun showCallDialog(phoneNumber: String, onClick: (Boolean) -> Unit) {
    val context = LocalContext.current
    GeneralDialogPrompt(
        title = "Telepon Pelanggan?",
        subtitle = "Kamu akan terkena tarif pulsa sesuai dengan operator yang Kamu gunakan",
        actionButtons = {
            GeneralActionButton(
                text = "Batal",
                textColor = UiColor.primaryRed500,
                isFirstAction = true
            ) {
                onClick(false)
            }
            GeneralActionButton(
                text = "Telepon",
                textColor = UiColor.neutral900,
                isFirstAction = false
            ) {
                onClick(false)

                val uri = Uri.parse("tel:$phoneNumber")
                val intent = Intent(Intent.ACTION_DIAL, uri)
                context.startActivity(intent)
            }
        },
        onDismissRequest = { onClick(false) }
    )
}

@Composable
fun RowScope.InfoDetailSend(icon: Int, title: String) {
    GlideImage(
        imageModel = icon,
        modifier = Modifier
            .size(Theme.dimension.size_24dp)
    )
    Spacer(
        modifier = Modifier.width(Theme.dimension.size_12dp),
    )
    Text(
        title,
        maxLines = 1,
        modifier = Modifier.weight(1f),
        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral900)
    )
    Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun RowScope.OrderDetailBottomButton(
    @DrawableRes icon: Int,
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(Theme.dimension.size_64dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Theme.dimension.size_8dp)
            )
            .padding(
                vertical = Theme.dimension.size_8dp,
                horizontal = Theme.dimension.size_24dp
            )
            .noRippleClickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = icon,
            modifier = Modifier.size(Theme.dimension.size_24dp),
            imageOptions = ImageOptions(
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(color = UiColor.white)
            )
        )
        Spacer(modifier = Modifier.padding(top = Theme.dimension.size_2dp))
        ResponsiveText(
            text = title, textStyle = UiFont.poppinsP2SemiBold.copy(
                color = UiColor.white, platformStyle =
                PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun PaymentDetail(
    isCash: Boolean,
    spec: List<TransportOrderPaymentSpec>,
    itemsFood: List<TransportOrderPaymentSpec> = emptyList()
) {
    if (itemsFood.isNotEmpty()) {
        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        Text(
            "Rincian Pesanan Makanan", style = UiFont.poppinsH5Bold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            itemsFood.forEach { item ->
                PaymentItemRow(
                    titleText = item.name,
                    valueText = item.price.convertToRupiah(),
                    textColor = UiColor.neutral500,
                    quantity = item.quantity,
                    notes = item.notes
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
    if (isCash) {
        Text(
            "Rincian Harga", style = UiFont.poppinsH5Bold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            spec.forEach { item ->
                val textColor =
                    if (item.paymentType == PaymentType.DISCOUNT) UiColor.success500 else UiColor.neutral500
                PaymentItemRow(
                    titleText = item.name,
                    valueText = item.price.convertToRupiah(),
                    textColor = textColor,
                    quantity = ""
                )
            }
        }
    }
}