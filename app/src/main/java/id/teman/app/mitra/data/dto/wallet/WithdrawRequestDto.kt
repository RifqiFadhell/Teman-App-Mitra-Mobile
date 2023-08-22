package id.teman.app.mitra.data.dto.wallet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WithdrawRequestDto(
    val pin: String,
    val amount: String,
    @SerialName("bank_name")
    val bankName: String,
    @SerialName("account_name")
    val accountName: String,
    @SerialName("account_number")
    val accountNumber: String
)