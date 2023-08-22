package id.teman.app.mitra.ui.food.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.domain.model.restaurant.colorStatus
import id.teman.app.mitra.domain.model.restaurant.isTerminalStatus
import id.teman.app.mitra.domain.model.restaurant.textValue
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.food.foodcommon.CustomerInfoWidget
import id.teman.app.mitra.ui.food.foodcommon.OrderDetailRowItemWidget
import id.teman.app.mitra.ui.food.rating.PaymentSectionWidget
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont


@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://mitra.com/order_detail/{requestId}")
    ]
)
@Composable
fun FoodOrderDetailScreen(
    navigator: DestinationsNavigator,
    requestId: String,
    viewModel: FoodOrderDetailViewModel = hiltViewModel()
) {

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getRestaurantOrderDetail(requestId)
    }
    val uiState = viewModel.uiState
    var isShowCancelOrderDialog by remember { mutableStateOf(false) }
    if (isShowCancelOrderDialog) {
        RenderCancelOrderDialogWidget{ isCancelling ->
            isShowCancelOrderDialog = isCancelling
            if (uiState.successGetDetail != null && isCancelling) {
                viewModel.updateRestaurantOrderStatus(uiState.successGetDetail.id, RestaurantOrderStatus.CANCELLED)
            }
        }
    }
    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            openDialog.value = it
        }
    })

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Ok",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""

                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }
    val verticalScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        uiState.successGetDetail?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                BasicTopNavigation(title = "Detail Pesanan") {
                    navigator.popBackStack()
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Theme.dimension.size_24dp, bottom = Theme.dimension.size_26dp,
                            start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
                        )
                ) {
                    Text("Pesanan", style = UiFont.poppinsP2SemiBold)
                    Text(
                        "• ${item.orderStatus.textValue()}",
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(Theme.dimension.size_32dp),
                                color = item.orderStatus.colorStatus().second
                            )
                            .padding(
                                vertical = Theme.dimension.size_4dp,
                                horizontal = Theme.dimension.size_12dp
                            ),
                        style = UiFont.poppinsCaptionMedium.copy(color = item.orderStatus.colorStatus().first)
                    )
                }
                CustomerInfoWidget(item)
                Text(
                    "Daftar Pesanan",
                    style = UiFont.poppinsSubHSemiBold,
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp,
                        top = Theme.dimension.size_40dp, bottom = Theme.dimension.size_8dp
                    )
                )
                item.orderItems.map {
                    OrderDetailRowItemWidget(it)
                }
                PaymentSectionWidget(item)
                BottomActionButtonWidget(
                    item.orderStatus,
                    onCancelClick = {
                        isShowCancelOrderDialog = true
                    },
                    onAcceptOrder = {
                        viewModel.updateRestaurantOrderStatus(item.id, RestaurantOrderStatus.PROCESS)
                    },
                    onCompletedOrder = {
                        viewModel.updateRestaurantOrderStatus(item.id, RestaurantOrderStatus.FINISHED)
                    }
                )
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

@Composable
private fun RenderCancelOrderDialogWidget(onClick: (Boolean) -> Unit) {
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
fun BottomActionButtonWidget(
    orderStatus: RestaurantOrderStatus,
    onCancelClick: () -> Unit,
    onAcceptOrder: () -> Unit,
    onCompletedOrder: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (!orderStatus.isTerminalStatus()) {
            Text("Batalkan", style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                modifier = Modifier.noRippleClickable { onCancelClick() })
            Spacer(modifier = Modifier.height(Theme.dimension.size_28dp))
        }
        if (orderStatus == RestaurantOrderStatus.NEW) {
            TemanFilledButton(
                content = "Proses Pesanan",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = Theme.dimension.size_24dp, start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp
                    ),
                activeTextColor = UiColor.white
            ) {
                onAcceptOrder()
            }
        } else if (orderStatus == RestaurantOrderStatus.PROCESS) {
            TemanFilledButton(
                content = "Selesai",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = Theme.dimension.size_24dp, start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp
                    ),
                activeTextColor = UiColor.white
            ) {
                onCompletedOrder()
            }
        }
    }
}