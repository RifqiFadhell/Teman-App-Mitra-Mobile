package id.teman.app.mitra.domain.model.registration

import android.net.Uri
import id.teman.app.mitra.common.UriSerializer
import kotlinx.serialization.Serializable

@Serializable
data class OwnerIdentitySpec(
    @Serializable(UriSerializer::class)
    val ktpImage: Uri,
    val ktpName: String,
    val ktpNumber: String
)