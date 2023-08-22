package id.teman.app.mitra.ui.myaccount.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.transport.OrderHistoryItemSpec
import id.teman.app.mitra.domain.model.transport.OrderHistoryUISection
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.destinations.OrderDetailScreenDestination
import id.teman.app.mitra.ui.myaccount.viewmodel.OrderHistoryViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun OrderHistoryScreen(navigator: DestinationsNavigator, viewModel: OrderHistoryViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getOrderHistory()
    }
    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.errorMessage, block = {
        uiState.errorMessage?.consumeOnce {
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
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                BasicTopNavigation(title = "Pesanan") {
                    navigator.popBackStack()
                }
                LazyColumn(
                    modifier = Modifier.padding(bottom = Theme.dimension.size_24dp)
                ) {
                    items(uiState.items) { orderSection ->
                        when (orderSection) {
                            is OrderHistoryUISection.SectionItem -> OrderHistoryRowItem(orderSection.item) {
                                navigator.navigate(OrderDetailScreenDestination(
                                    item = viewModel.getOrderHistoryDetailSpec(it)
                                ))
                            }
                            is OrderHistoryUISection.SectionTitle -> Text(
                                orderSection.title,
                                style = UiFont.poppinsP2SemiBold,
                                modifier = Modifier.padding(
                                    start = Theme.dimension.size_16dp,
                                    top = Theme.dimension.size_24dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun OrderHistoryRowItem(items: List<OrderHistoryItemSpec>, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
                    .clickable { onItemClick(item.id) }
            ) {
                GlideImage(
                    imageModel = item.image,
                    modifier = Modifier
                        .size(Theme.dimension.size_40dp)
                        .clip(RoundedCornerShape(Theme.dimension.size_6dp))
                )
                Column(
                    modifier = Modifier
                        .padding(start = Theme.dimension.size_8dp)
                        .weight(1f)
                ) {
                    Text(
                        item.title,
                        style = UiFont.poppinsP2SemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.subtitle,
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(Theme.dimension.size_12dp)
                                .background(color = item.status.orderStatusColor)
                        )
                        Text(
                            item.status.orderStatus,
                            modifier = Modifier.padding(start = Theme.dimension.size_8dp),
                            style = UiFont.poppinsCaptionMedium.copy(
                                color = item.status.orderStatusColor,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    item.price.convertToRupiah(), style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.blue),
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
            }
        }
    }
}