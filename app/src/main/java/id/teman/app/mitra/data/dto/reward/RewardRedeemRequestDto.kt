package id.teman.app.mitra.data.dto.reward

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RewardRedeemRequestDto(
    val reward_id: String?
)