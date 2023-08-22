package id.teman.app.mitra.data.dto.reward

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RewardResponseDto(
    val data: List<Reward>?,
    val count: Int?,
    val total: Int?,
    val page: Int?,
    val pageCount: Int?
)

@Keep
@Serializable
data class RewardRedeemedResponse(
    val data: List<RewardRedeemed>?,
    val count: Int,
    val total: Int,
    val page: Int,
    val pageCount: Int
)

@Keep
@Serializable
data class RewardTransactionResponseDto(
    val data: List<RewardTransaction>?,
    val count: Int,
    val total: Int,
    val page: Int,
    val pageCount: Int
)

@Keep
@Serializable
data class Reward(
    val id: String?,
    val created_at: String?,
    val updated_at: String?,
    val title: String?,
    val description: String?,
    val point: Int?,
    val deposit: Int?,
    val type: String?,
    val status: String?,
    val start_at: String?,
    val end_at: String?,
    val image: Image? = null
)

@Keep
@Serializable
data class Image(
    @SerialName("id")
    val id: String?,
    @SerialName("url")
    val url: String?
)

@Keep
@Serializable
data class RewardRedeemed(
    val id: String?,
    val created_at: String?,
    val updated_at: String?,
    val user_id: String?,
    val reward_id: String?,
    val status: String?,
    val reward: Reward?
)

@Keep
@Serializable
data class RewardTransaction(
    val id: String?,
    val created_at: String?,
    val updated_at: String?,
    val user_id: String?,
    val driver_id: String?,
    val key: String?,
    val request_id: String?,
    val redeem_id: String?,
    val title: String?,
    val description: String?,
    val amount: Int?
)