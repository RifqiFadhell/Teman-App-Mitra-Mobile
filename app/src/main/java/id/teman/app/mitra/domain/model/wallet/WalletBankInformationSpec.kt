package id.teman.app.mitra.domain.model.wallet

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.reward.Reward
import id.teman.app.mitra.data.dto.reward.RewardRedeemed
import id.teman.app.mitra.data.dto.reward.RewardTransaction
import id.teman.app.mitra.data.dto.wallet.ItemBankDto
import id.teman.app.mitra.data.dto.wallet.WalletBankAccountDto
import id.teman.app.mitra.ui.transport.common.getCurrentTimeFormat
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class WalletBankInformationSpec(
    val bankName: String,
    val accountNumber: String,
    val accountName: String
)

fun WalletBankAccountDto.toWalletBankInformationSpec(): WalletBankInformationSpec {
    return WalletBankInformationSpec(
        bankName = bankName.orEmpty(),
        accountName = accountName.orEmpty(),
        accountNumber = accountNumber.orEmpty()
    )
}

@Serializable
data class ItemBankSpec(
    val bankName: String,
    val bankCode: String
)

fun List<ItemBankDto>?.convertToListBank(): List<ItemBankSpec> {
    return this?.map {
        ItemBankSpec(
            bankName = it.bankName.orEmpty(),
            bankCode = it.bankCode.orEmpty()
        )
    }.orEmpty()
}

@Serializable
data class WalletDataTransferSpec(
    val bankName: String,
    val accountNumber: String,
    val accountName: String,
    val withdrawalAmount: Double
)

@Serializable
@Keep
data class WalletHistoryTransactionDetail(
    val id: String? = null,
    val type: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val status: String? = null,
    val sn: String? = null,
    val amount: Double? = 0.0,
    val title: String? = null,
    val description: String? = null,
    val provider: String? = null,
    val url: String? = null,
    val category: String? = null,
    val customer_no: String? = null,
)

@Parcelize
data class WalletItemDetailSpec(
    val id: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String,
    val status: String,
    val amount: String,
    val title: String,
    val description: String,
    val provider: String,
    val url: String,
    val button: String? = "",
    val serialNumber: String? = "",
    val caption: String? = "",
    val category: String? = "",
    val number: String? = "",
) : Parcelable

fun WalletHistoryTransactionDetail.toWalletDetailItem(): WalletItemDetailSpec {
    val statusTransaction: String
    val caption: String
    val button: String
    when (status) {
        "success" -> {
            statusTransaction = "Berhasil"
            caption = "Terimakasih telah percaya kepada kami"
            button = "Kembali ke T-Bill"
        }
        "failed" -> {
            statusTransaction = "Gagal"
            caption = "Transaksi Gagal silahkan kembali lagi"
            button = "Kembali ke T-Bill"
        }
        else -> {
            statusTransaction = "DiProses"
            caption = "Transaksi DiProses silahkan Muat Ulang"
            button = "Muat Ulang Status"
        }
    }

    val finalAmount =
        if (type == "topup" && status == "success" || type == "topup" && status == "pending") {
            "+${amount.orZero().convertToRupiah()}"
        } else if (type == "topup" && status == "failed") {
            amount.orZero().convertToRupiah()
        } else {
            amount.orZero().convertToRupiah()
        }

    return WalletItemDetailSpec(
        id = id.orEmpty(),
        type = type.orEmpty(),
        createdAt = created_at.orEmpty(),
        updatedAt = updated_at.orEmpty(),
        status = status.orEmpty(),
        amount = finalAmount,
        title = title.orEmpty(),
        description = statusTransaction,
        provider = provider.orEmpty(),
        url = url.orEmpty(),
        button = button,
        caption = caption,
        serialNumber = sn, category = category, number = customer_no
    )
}

@Serializable
@Keep
data class ItemReward(
    val id: String,
    val title: String,
    val description: String,
    val point: Int,
    val type: String,
    val startDate: String,
    val endDate: String,
    val url: String
)

fun List<Reward>?.toListRewards(): List<ItemReward> {
    return this?.map {
        ItemReward(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            description = it.description.orEmpty(),
            point = it.point.orZero(),
            type = it.type.orEmpty(),
            startDate = getCurrentTimeFormat(it.start_at),
            endDate = getCurrentTimeFormat(it.end_at),
            url = it.image?.url.orEmpty(),
        )
    }.orEmpty()
}

@Serializable
@Keep
data class ItemRewardRedeemed(
    val id: String,
    val status: String,
    val date: String,
    val reward: ItemReward
)

fun List<RewardRedeemed>?.toListRewardRedeemed(): List<ItemRewardRedeemed> {
    return this?.map {
        ItemRewardRedeemed(
            id = it.id.orEmpty(),
            status = it.status.orEmpty(),
            date = getCurrentTimeFormat(it.updated_at),
            reward = ItemReward(
                id = it.reward?.id.orEmpty(),
                title = it.reward?.title.orEmpty(),
                description = it.reward?.description.orEmpty(),
                point = it.reward?.point.orZero(),
                type = it.reward?.type.orEmpty(),
                startDate = getCurrentTimeFormat(it.reward?.start_at),
                endDate = getCurrentTimeFormat(it.reward?.end_at),
                url = it.reward?.image?.url.orEmpty(),
            )
        )
    }.orEmpty()
}

fun List<RewardTransaction>?.toHistoryPoint(): List<ItemRewardTransaction> {
    return this?.map {
        val icon = if (it.key == "customer_point" || it.key == "register_point") {
            R.drawable.ic_giftcard
        } else {
            R.drawable.ic_money_withdraw
        }
        ItemRewardTransaction(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            description = it.description.orEmpty(),
            amount = it.amount.orZero(),
            key = it.key.orEmpty(),
            date = it.created_at.orEmpty(),
            icon = icon,
        )
    }.orEmpty()
}

@Serializable
@Keep
data class ItemRewardTransaction(
    val id: String,
    val title: String,
    val description: String,
    val amount: Int,
    val key: String,
    val date: String,
    @DrawableRes val icon: Int
)