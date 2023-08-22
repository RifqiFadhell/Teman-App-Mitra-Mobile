package id.teman.app.mitra.ui.registration.uimodel

import kotlinx.serialization.Serializable

@Serializable
data class UserBasicRegistrationRequestUiModel(
    val partnerType: String,
    val fullName: String,
    val email: String
)