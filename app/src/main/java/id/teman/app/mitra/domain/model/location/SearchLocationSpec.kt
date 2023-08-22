package id.teman.app.mitra.domain.model.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchLocationSpec(
    val title: String,
    val description: String,
    val placeId: String
): Parcelable