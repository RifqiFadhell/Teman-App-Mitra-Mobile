package id.teman.app.mitra.domain.model.camera

import kotlinx.serialization.Serializable

@Serializable
data class CameraSpec(
    val title: String,
    val largeCamera: Boolean = false,
    val cameraType: CameraType,
    val description: String = "",
    val cameraCrop: Pair<Float, Float> = Pair(0.9f, 0.3f)
)

@Serializable
enum class CameraType {
    KTP, SIM, STNK, SKCK, PROFILE, VEHICLE, BANKACCOUNT, STOREPHOTOOUTLET
}

@Serializable
data class CameraResult(
    val cameraType: CameraType,
    val uri: String
)