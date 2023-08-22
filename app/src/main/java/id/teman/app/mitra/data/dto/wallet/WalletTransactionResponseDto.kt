package id.teman.app.mitra.data.dto.wallet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WalletTransactionResponseDto(
    val data: List<WalletTransactionItemDto>? = null
)

@Keep
@Serializable
data class WalletTransactionItemDto(
    @SerialName("id")
    val transactionId: String? = null,
    val title: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val description: String? = null,
    val url: String? = null,
    val amount: Double? = null
)