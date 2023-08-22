package id.teman.app.mitra.domain.model.wallet

import androidx.annotation.DrawableRes
import id.teman.app.mitra.R
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.wallet.WalletTransactionItemDto
import id.teman.app.mitra.ui.transport.common.getCurrentTimeFormat
import kotlinx.serialization.Serializable

@Serializable
data class WalletHistoryItemSpec(
    val id: String,
    @DrawableRes val icon: Int,
    val title: String,
    val date: String,
    val subtitle: String,
    val type: String,
    val price: Double,
    val url: String,
)

fun WalletTransactionItemDto.toWalletHistoryItemSpec(): WalletHistoryItemSpec {

    val icon = when(type) {
        "bike" -> R.drawable.ic_teman_bike
        "car" -> R.drawable.ic_teman_car
        "food" -> R.drawable.ic_teman_food
        "topup" -> R.drawable.teman_wallet_history_new
        else -> R.drawable.ic_teman_bike
    }

    return WalletHistoryItemSpec(
        id = transactionId.orEmpty(),
        icon = icon,
        date = getCurrentTimeFormat(updated_at, "HH:mm, dd MMM yyyy"),
        type = type.orEmpty(),
        title = title.orEmpty(),
        subtitle = description.orEmpty(),
        price = amount.orZero(),
        url = url.orEmpty()
    )
}