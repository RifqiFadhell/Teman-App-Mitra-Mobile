package id.teman.app.mitra.data.dto.referral

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ReferralResponseDto(
    val data: List<ItemReferralResponseDto>?
)

@Keep
@Serializable
data class ItemReferralResponseDto(
    val id: String? = "",
    val created_at: String? = "",
    val updated_at: String? = "",
    val deleted_at: String? = "",
    val user_name: String? = "",
    val user_id: String? = "",
    val benefit: Int? = 0,
    val status: String? = ""
)


