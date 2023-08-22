package id.teman.app.mitra.ui.food.rating

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.food.foodcommon.CustomerInfoWidget
import id.teman.app.mitra.ui.food.foodcommon.OrderDetailRowItemWidget
import id.teman.app.mitra.ui.food.order.FoodOrderDetailViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun FoodOrderDetailRatingScreen(
    navigator: DestinationsNavigator, requestId: String,
    viewModel: FoodOrderDetailViewModel = hiltViewModel()
) {
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getRestaurantOrderDetail(requestId)
    }
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
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
    Box(modifier = Modifier.fillMaxSize()) {
        uiState.successGetDetail?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                BasicTopNavigation(title = "Penilaian & Ulasan") {
                    navigator.popBackStack()
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
                CustomerInfoWidget(item)
                if (item.rating != 0) RatingWidget(item.rating) {}
                Text(
                    "Daftar Pesanan",
                    style = UiFont.poppinsSubHSemiBold,
                    modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
                )
                item.orderItems.map {
                    OrderDetailRowItemWidget(it)
                }
                PaymentSectionWidget(item)
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
private fun RatingWidget(rating: Int, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = Theme.dimension.size_40dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..5) {
            GlideImage(
                imageModel = R.drawable.ic_star,
                imageOptions = ImageOptions(
                    colorFilter = ColorFilter.tint(
                        color = if (i >= rating) UiColor.neutral100 else UiColor.primaryYellow500
                    )
                ),
                modifier = Modifier
                    .size(Theme.dimension.size_30dp)
//                    .clickable { onClick(i + 1) }
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PaymentSectionWidget(item: RestaurantOrderSpec) {
    Column(
        modifier = Modifier.padding(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_40dp
        )
    ) {
        Text("Metode Pembayaran", style = UiFont.poppinsP2SemiBold)
        Spacer(Modifier.height(Theme.dimension.size_20dp))
        Row(
            verticalAlignment = Alignment.Top
        ) {
            TemanCircleButton(
                icon = R.drawable.ic_wallet,
                circleBackgroundColor = Color.Transparent,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .border(
                        BorderStroke(Theme.dimension.size_1dp, color = UiColor.neutral100),
                        shape = CircleShape
                    ),
                iconModifier = Modifier
                    .size(Theme.dimension.size_24dp)
            )
            Spacer(Modifier.width(Theme.dimension.size_16dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(
                    if (item.isWalletPayment) "T-Wallet" else "Cash",
                    style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral900),
                    maxLines = 1,
                    modifier = Modifier
                        .padding(start = Theme.dimension.size_16dp)
                )
//
//                Text(
//                    "Bayar secara instan pakai T-Wallet",
//                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
//                    maxLines = 1,
//                    modifier = Modifier
//                        .padding(start = Theme.dimension.size_16dp),
//                    overflow = TextOverflow.Ellipsis
//                )
            }
        }
        Spacer(Modifier.height(Theme.dimension.size_30dp))
        Text(
            "Rincian Pesanan", style = UiFont.poppinsH5Bold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Spacer(Modifier.height(Theme.dimension.size_20dp))
//        item.paymentSpec.map {
//            PaymentItemRow(
//                titleText = it.name, valueText = it.price.convertToRupiah(),
//                textColor = if (it.paymentType == PaymentType.DISCOUNT) UiColor.success500 else UiColor.neutral900
//            )
//        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Total yang dibayarkan", style = UiFont.poppinsP1SemiBold)
            Text(item.restaurantFare.convertToRupiah(), style = UiFont.poppinsP1SemiBold)
        }
    }
}