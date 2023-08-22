package id.teman.app.mitra.device

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInformation(
    val deviceName: String,
    val deviceId: String
)