package id.teman.app.mitra.domain.model.transport

import androidx.compose.ui.graphics.Color
import id.teman.app.mitra.R
import id.teman.app.mitra.common.getOrderHistoryTimeFormat
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.transport.TransportDataResponseDto
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.coreui.typography.UiColor

data class OrderHistoryItemSpec(
    val id: String,
    val title: String,
    val subtitle: String,
    val image: Any,
    val isFinished: Boolean,
    val price: Double,
    val isFoodOrder: Boolean,
    val status: OrderHistoryStatus
)

sealed class OrderHistoryUISection {
    data class SectionTitle(val title: String) : OrderHistoryUISection()
    data class SectionItem(val item: List<OrderHistoryItemSpec>) : OrderHistoryUISection()
}

enum class OrderHistoryStatus(val orderStatus: String, val orderStatusColor: Color) {
    OrderInProgress("Sedang Berlangsung", UiColor.blue),
    OrderDelivered("Pesanan Sudah diantar", UiColor.success500),
    OrderCancelled("Pesanan dibatalkan", UiColor.primaryRed500),
    OrderRejected("Pesanan ditolak", UiColor.error500);

    companion object {
        fun from(value: String?) = when (value) {
            "process" -> OrderInProgress
            "finished" -> OrderDelivered
            "rejected" -> OrderRejected
            "canceled" -> OrderCancelled
            "arrived" -> OrderDelivered
            else -> OrderInProgress
        }
    }
}

fun TransportDataResponseDto.toOrderHistoryUISection(): List<OrderHistoryUISection> {
    val specItem = this.data?.toOrderHistoryItemSpec().orEmpty().toMutableList()
    if (specItem.isEmpty()) return emptyList()

    val orderHistorySections = arrayListOf<OrderHistoryUISection>()
    specItem.groupBy { it.isFinished }.entries.sortedBy { it.key }.forEach { (isFinished, item) ->
        orderHistorySections.add(OrderHistoryUISection.SectionTitle(if (isFinished) "SELESAI" else "Dalam Proses"))
        orderHistorySections.add(OrderHistoryUISection.SectionItem(item))
    }
    return orderHistorySections
}

fun List<TransportResponseDto>.toOrderHistoryItemSpec(): List<OrderHistoryItemSpec> {
    return map {
        OrderHistoryItemSpec(
            id = it.id.orEmpty(),
            title = getOrderHistoryTitle(it),
            subtitle = getOrderHistorySubtitle(it),
            image = getOrderHistoryImage(it),
            isFinished = it.status != "process",
            price = it.fare.orZero(),
            isFoodOrder = it.type == "food",
            status = OrderHistoryStatus.from(it.status)
        )
    }
}

fun getOrderHistoryTitle(item: TransportResponseDto): String = when (item.type) {
    "bike" -> "T-Bike ke ${item.dropOffAddress}"
    "car" -> "T-Car ke ${item.dropOffAddress}"
    "food" -> "T-Food ${item.items?.firstOrNull()?.product?.name.orEmpty()}"
    "send" -> "T-Send ke ${item.dropOffAddress}"
    else -> "Unknown"
}

fun getOrderHistoryImage(item: TransportResponseDto): Any = when (item.type) {
    "bike" -> R.drawable.ic_teman_bike
    "car" -> R.drawable.ic_teman_car
    "food" -> item.items?.firstOrNull()?.product?.productPhoto?.url ?: R.drawable.ic_teman_food
    "send" -> R.drawable.ic_teman_bike
    else -> R.drawable.ic_teman_bike
}

fun getOrderHistorySubtitle(item: TransportResponseDto): String {
    return if (item.type == "food") {
        "${getOrderHistoryTimeFormat(item.updatedAt.orEmpty())} • ${
            item.items.orEmpty().sumOf { it.quantity.orZero() }
        }"
    } else {
        "${getOrderHistoryTimeFormat(item.updatedAt.orEmpty())} ${
            if (item.packageType.isNotNullOrEmpty()) "• ${item.packageType.orEmpty()}" else ""
        }"
    }
}
