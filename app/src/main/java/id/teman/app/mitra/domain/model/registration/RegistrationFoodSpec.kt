package id.teman.app.mitra.domain.model.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationFoodSpec(
    val ownerIdentity: OwnerIdentitySpec? = null,
    val bankInformation: BankInformationSpec? = null,
    val businessStoreName: String? = null,
    val businessCategory: String? = null,
    val outletInformation: OutletInformationSpec? = null
)