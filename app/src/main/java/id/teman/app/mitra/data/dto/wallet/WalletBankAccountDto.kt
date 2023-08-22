package id.teman.app.mitra.data.dto.wallet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WalletBankAccountDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val bankName: String? = null,
    @SerialName("account_number") val accountNumber: String? = null,
    @SerialName("account_name") val accountName: String? = null,
    @SerialName("image") val bankAccountImage: WalletBankImageDto? = null
)

@Keep
@Serializable
data class WalletBankImageDto(
    @SerialName("url") val url: String? = null
)

@Keep
@Serializable
data class ItemBankDto(
    @SerialName("bankCode") val bankCode: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("maxAmountTransfer") val maxAmountTransfer: String? = null,
)