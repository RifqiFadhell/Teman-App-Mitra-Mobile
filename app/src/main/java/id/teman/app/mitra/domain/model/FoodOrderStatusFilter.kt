package id.teman.app.mitra.domain.model

enum class FoodOrderStatusFilter(val title: String) {
    ALL("Semua"),
    NEW("Baru"),
    PROCESS("Sedang Diproses"),
    DONE("Selesai"),
    CANCELLED("Dibatalkan")
}