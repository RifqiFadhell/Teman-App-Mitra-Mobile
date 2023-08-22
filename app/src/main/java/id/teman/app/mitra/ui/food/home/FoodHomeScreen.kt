package id.teman.app.mitra.ui.food.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.convertUtcIso8601ToLocalTimeAgo
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.domain.model.restaurant.colorStatus
import id.teman.app.mitra.domain.model.restaurant.isTerminalStatus
import id.teman.app.mitra.domain.model.restaurant.textValue
import id.teman.app.mitra.domain.model.restaurant.toRestaurantSummaryFilterRequest
import id.teman.app.mitra.domain.model.user.MitraRestaurantInfo
import id.teman.app.mitra.domain.model.user.RestaurantStatus
import id.teman.app.mitra.domain.model.user.colorStatus
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderDetailRatingScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderDetailScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderListScreenDestination
import id.teman.app.mitra.ui.destinations.NotificationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletPinOtpValidationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun FoodHomeScreen(
    navigator: DestinationsNavigator,
    viewModel: FoodHomeViewModel = hiltViewModel()
) {

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getRestaurantDetail()
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getUserProfile()
    }
    val uiState = viewModel.uiState
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
        if (uiState.restaurantDetail != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    FoodHomeHeader(
                        uiState.loading,
                        uiState.restaurantDetail,
                        onNotificationClick = {
                            navigator.navigate(NotificationScreenDestination)
                        }, onChangeStatusClick = { status ->
                            viewModel.updateRestaurantStatus(status)
                        }, onSearchClick = {
                            navigator.navigate(FoodOrderListScreenDestination) {
                                popUpTo(FoodHomeScreenDestination) {
                                    saveState = true
                                }
                            }
                        })
                }
                item {
                    TransactionItem(uiState.restaurantSummary,
                    onChangeFilterCLick = {
                        viewModel.getRestaurantSummary(it.toRestaurantSummaryFilterRequest())
                    },
                    onDetailClick = {
                        val userInfo = viewModel.getUserInfo()
                        if (userInfo?.isPinAlreadySet == true) {
                            navigator.navigate(WalletScreenDestination)
                        } else {
                            navigator.navigate(WalletPinOtpValidationScreenDestination(phone = userInfo?.phoneNumber.orEmpty()))
                        }
                    })
                }
                item {
                    FoodHomeOrderItemTitle {
                        navigator.navigate(FoodOrderListScreenDestination) {
                            popUpTo(FoodHomeScreenDestination) {
                                saveState = true
                            }
                        }
                    }
                }
                items(uiState.restaurantOrders) { item ->
                    FoodHomeOrderItem(item) {
                        if (it.orderStatus.isTerminalStatus()) {
                            navigator.navigate(
                                FoodOrderDetailRatingScreenDestination(
                                    requestId = it.id
                                )
                            )
                        } else {
                            navigator.navigate(
                                FoodOrderDetailScreenDestination(
                                    requestId = it.id
                                )
                            )
                        }
                    }
                }
            }
        }

        if (uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        }
    }
}

@Composable
private fun FoodHomeOrderItemTitle(onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimension.size_16dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Pesanan", style = UiFont.poppinsP3SemiBold)
        Text("Lihat Semua", style = UiFont.poppinsP3Medium.copy(color = UiColor.tertiaryBlue500),
            modifier = Modifier.clickable {
                onSeeAllClick()
            })
    }
}

@Composable
private fun FoodHomeHeader(
    isLoading: Boolean,
    restaurant: MitraRestaurantInfo,
    onNotificationClick: () -> Unit,
    onChangeStatusClick: (RestaurantStatus) -> Unit,
    onSearchClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFiledSize by remember { mutableStateOf(Size.Zero) }

    val restaurantStatus = remember { mutableListOf("Buka", "Istirahat", "Tutup") }
    var selectedRestaurantStatus by remember { mutableStateOf(restaurant.restaurantOrderStatus) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(restaurant.name, style = UiFont.poppinsH3SemiBold)
            Row(
                modifier = Modifier
                    .background(
                        color = restaurant.restaurantOrderStatus.colorStatus().second,
                        shape = RoundedCornerShape(Theme.dimension.size_32dp)
                    )
                    .padding(
                        vertical = Theme.dimension.size_8dp,
                        horizontal = Theme.dimension.size_16dp
                    )
                    .onGloballyPositioned { coordinates ->
                        textFiledSize = coordinates.size.toSize()
                    }
                    .clickable {
                        expanded = !expanded
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    restaurant.restaurantOrderStatus.value,
                    style = UiFont.poppinsP2SemiBold.copy(color = restaurant.restaurantOrderStatus.colorStatus().first)
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                GlideImage(
                    R.drawable.ic_arrow_down, modifier = Modifier.size(Theme.dimension.size_16dp),
                    imageOptions = ImageOptions(colorFilter = ColorFilter.tint(color = restaurant.restaurantOrderStatus.colorStatus().first))
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
            ) {
                restaurantStatus.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        onClick = {
                            if (!isLoading) {
                                val newStatus = when (index) {
                                    0 -> RestaurantStatus.OPEN
                                    1 -> RestaurantStatus.REST
                                    else -> RestaurantStatus.CLOSE
                                }
                                selectedRestaurantStatus = newStatus
                                expanded = false
                                onChangeStatusClick(selectedRestaurantStatus)
                            }
                        }
                    ) {
                        Text(item, style = UiFont.poppinsP2SemiBold)
                    }
                }
            }
        }
        GlideImage(
            R.drawable.ic_notification,
            modifier = Modifier
                .size(Theme.dimension.size_24dp)
                .noRippleClickable {
                    onNotificationClick()
                }
        )
    }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                top = Theme.dimension.size_12dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
            .border(
                width = Theme.dimension.size_2dp,
                color = UiColor.neutral100,
                shape = RoundedCornerShape(Theme.dimension.size_30dp)
            )
            .padding(
                vertical = Theme.dimension.size_16dp,
                horizontal = Theme.dimension.size_24dp
            )
            .noRippleClickable {
                onSearchClick()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlideImage(
                R.drawable.ic_search,
                modifier = Modifier.size(Theme.dimension.size_24dp)
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
            Text(
                "Cari...",
                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun FoodHomeOrderItem(
    item: RestaurantOrderSpec,
    onFoodDetailClick: (RestaurantOrderSpec) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(Theme.dimension.size_16dp)
            .border(
                border = BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp)
            .clickable {
                onFoodDetailClick(item)
            }
    ) {
        Row {
            GlideImage(
                imageModel = item.userPhoto,
                modifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .clip(RoundedCornerShape(Theme.dimension.size_12dp)),
                failure = {
                    painterResource(id = R.drawable.ic_person)
                },
                loading = {
                    painterResource(id = R.drawable.ic_person)
                }
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
            Column(
                modifier = Modifier
                    .padding(start = Theme.dimension.size_8dp)
                    .weight(1f)
            ) {
                Text(
                    item.userName,
                    style = UiFont.poppinsP3SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.productName,
                    modifier = Modifier.padding(vertical = Theme.dimension.size_4dp),
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral900),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.orderItems.count()} Menu • ${item.orderTime.convertUtcIso8601ToLocalTimeAgo()}",
                    style = UiFont.poppinsCaptionMedium.copy(
                        color = UiColor.neutral300,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_12dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.totalPrice.convertToRupiah(),
                        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500)
                    )
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
            }
        }
    }
}
