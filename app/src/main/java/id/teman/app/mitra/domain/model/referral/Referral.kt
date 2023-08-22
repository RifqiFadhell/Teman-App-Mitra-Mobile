package id.teman.app.mitra.domain.model.referral

import androidx.annotation.Keep
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.referral.ItemReferralResponseDto
import id.teman.app.mitra.ui.transport.common.getCurrentTimeFormat
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ItemReferral(
    val id: String,
    val name: String,
    val benefit: String,
    val dateJoined: String,
    val status: String
)

fun List<ItemReferralResponseDto>?.toListReferral(): List<ItemReferral> {
    return this?.map {
        ItemReferral(
            id = it.id.orEmpty(),
            name = it.user_name.orEmpty(),
            benefit = it.benefit.orZero().toString(),
            dateJoined = getCurrentTimeFormat(it.created_at),
            status = it.status.orEmpty()
        )
    }.orEmpty()
}