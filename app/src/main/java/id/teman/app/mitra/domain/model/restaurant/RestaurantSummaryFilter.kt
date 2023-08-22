package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.domain.model.TransactionFilter

enum class RestaurantSummaryFilter(val value: String) {
    TODAY("today"),
    THIS_WEEK("this_week"),
    THIS_MONTH("this_month");
}

fun TransactionFilter.toRestaurantSummaryFilterRequest(): RestaurantSummaryFilter? {
    return when (this) {
        TransactionFilter.ALL -> null
        TransactionFilter.DAILY -> RestaurantSummaryFilter.TODAY
        TransactionFilter.WEEKLY -> RestaurantSummaryFilter.THIS_WEEK
        TransactionFilter.MONTHLY -> RestaurantSummaryFilter.THIS_MONTH
    }
}