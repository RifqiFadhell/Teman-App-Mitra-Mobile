package id.teman.app.mitra.domain.model

import androidx.compose.ui.graphics.Color
import id.teman.coreui.typography.UiColor

enum class TransactionFilter(
    val title: String
) {
    ALL("Semua"),
    DAILY("Hari Ini"),
    WEEKLY("Pekan Ini"),
    MONTHLY("Bulan Ini")
}

val Boolean.chipTextColor
    get() = if (this) UiColor.white else UiColor.neutral500

val Boolean.chipBackgroundColor
    get() = if (this) UiColor.neutral900 else Color.Transparent