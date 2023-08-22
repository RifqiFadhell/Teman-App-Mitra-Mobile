package id.teman.app.mitra.data.dto.transport

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DrivingSummaryDto(
    val income: Double? = null,
    @SerialName("online_hours")
    val onlineHours: Int? = null,
    @SerialName("total_distance")
    val totalDistance: Double? = null,
    @SerialName("total_jobs")
    val totalJobs: Int? = null
)