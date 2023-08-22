package id.teman.app.mitra.domain.model.transport

data class DriverOrderSummarySpec(
    val income: Double,
    val onlineHours: Int,
    val totalDistance: Long,
    val totalJobs: Int
)
