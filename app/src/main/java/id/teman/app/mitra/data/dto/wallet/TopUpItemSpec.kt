package id.teman.app.mitra.data.dto.wallet

enum class TopUpItemSpec(
    val value: Int?
) {
    Price_10000(10000),
    Price_25000(25000),
    Price_50000(50000),
    Price_100000(100000),
    Price_200000(200000),
    Price_400000(400000),
    Price_Input(0)
}