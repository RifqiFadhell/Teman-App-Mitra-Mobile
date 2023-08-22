package id.teman.app.mitra.ui.food.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.CustomChip
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.domain.model.FoodOrderStatusFilter
import id.teman.app.mitra.domain.model.chipBackgroundColor
import id.teman.app.mitra.domain.model.chipTextColor
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.domain.model.restaurant.isTerminalStatus
import id.teman.app.mitra.ui.destinations.FoodOrderDetailRatingScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderDetailScreenDestination
import id.teman.app.mitra.ui.food.foodcommon.EmptyRestaurantOrder
import id.teman.app.mitra.ui.food.foodcommon.FoodSingleOrderRowItemWidget
import id.teman.app.mitra.ui.food.foodcommon.SearchWidget
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun FoodOrderListScreen(navigator: DestinationsNavigator, viewModel: FoodOrderListViewModel = hiltViewModel()) {
    var longClickItemIndexes = remember {
        mutableStateListOf<Int>()
    }
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getRestaurantOrders()
    }
    var searchQuery by remember { mutableStateOf("")}
    var restaurantOrderStatus by remember {mutableStateOf<RestaurantOrderStatus?>(null)}
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AnimatedVisibility(visible = longClickItemIndexes.isEmpty()) {
                    Text(
                        "Pesanan",
                        style = UiFont.poppinsH3SemiBold,
                        modifier = Modifier.padding(Theme.dimension.size_16dp)
                    )
                }
                AnimatedVisibility(visible = longClickItemIndexes.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Theme.dimension.size_16dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${longClickItemIndexes.count()} Terpilih",
                            style = UiFont.poppinsP1SemiBold,
                        )
                        Row {
                            Text(
                                "Pesanan",
                                style = UiFont.poppinsP1SemiBold.copy(color = UiColor.tertiaryBlue500),
                            )
                            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                            Text(
                                "Batalkan",
                                style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                            )
                        }
                    }
                }
            }
            item {
                SearchWidget(searchQuery) {
                    searchQuery = it
                    viewModel.searchDebounced(it, restaurantOrderStatus)
                }
            }
            item {
                FilterWidget {
                    restaurantOrderStatus = it
                    viewModel.getRestaurantOrders(
                        query = searchQuery,
                        status = it,
                    )
                }
            }
            if (uiState.restaurantOrders.isNotEmpty()) {
                itemsIndexed(uiState.restaurantOrders) { index, item ->
                    FoodSingleOrderRowItemWidget(
                        item = item,
                        onLongClick = {
                            if (!longClickItemIndexes.contains(index)) {
                                longClickItemIndexes += index
                            } else {
                                val tempArr = longClickItemIndexes
                                tempArr.remove(index)
                                longClickItemIndexes = tempArr
                            }
                        },
                        onDetailClick = {
                            if (it.orderStatus.isTerminalStatus()) {
                                navigator.navigate(FoodOrderDetailRatingScreenDestination(
                                    requestId = it.id
                                ))
                            } else {
                                navigator.navigate(FoodOrderDetailScreenDestination(
                                    requestId = it.id
                                ))
                            }
                        },
                        isSelected = longClickItemIndexes.contains(index)
                    )
                }
            } else {
                item {
                    EmptyRestaurantOrder()
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

@Composable
private fun FilterWidget(onFilterChanged: (RestaurantOrderStatus?) -> Unit) {
    var activeCategory by remember { mutableStateOf(FoodOrderStatusFilter.ALL) }
    LazyRow(modifier = Modifier.padding(top = Theme.dimension.size_20dp)) {
        itemsIndexed(FoodOrderStatusFilter.values()) { index, item ->
            CustomChip(
                title = item.title,
                backgroundColor = (item == activeCategory).chipBackgroundColor,
                textColor = (item == activeCategory).chipTextColor,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(
                        end = Theme.dimension.size_8dp,
                        start = if (index == 0) Theme.dimension.size_16dp else Theme.dimension.size_0dp
                    ),
                textModifier = Modifier.padding(
                    vertical = Theme.dimension.size_8dp,
                    horizontal = Theme.dimension.size_12dp
                )
            ) {
                if (item == activeCategory) {
                    return@CustomChip
                }
                activeCategory = item
                when (activeCategory) {
                    FoodOrderStatusFilter.ALL -> onFilterChanged(null)
                    FoodOrderStatusFilter.NEW -> onFilterChanged(RestaurantOrderStatus.NEW)
                    FoodOrderStatusFilter.PROCESS -> onFilterChanged(RestaurantOrderStatus.PROCESS)
                    FoodOrderStatusFilter.DONE -> onFilterChanged(RestaurantOrderStatus.FINISHED)
                    FoodOrderStatusFilter.CANCELLED -> onFilterChanged(RestaurantOrderStatus.CANCELLED)
                }
            }
        }
    }
}