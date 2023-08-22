package id.teman.app.mitra.domain.model.registration

import android.net.Uri
import id.teman.app.mitra.common.UriSerializer
import kotlinx.serialization.Serializable

@Serializable
data class BankInformationSpec(
    @Serializable(UriSerializer::class)
    val bookImage: Uri,
    val name: String,
    val accountNumber: String,
    val ownerName: String
)