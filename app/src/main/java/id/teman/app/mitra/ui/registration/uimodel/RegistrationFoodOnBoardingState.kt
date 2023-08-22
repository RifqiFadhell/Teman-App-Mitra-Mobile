package id.teman.app.mitra.ui.registration.uimodel

import androidx.annotation.DrawableRes
import id.teman.app.mitra.R

sealed class FoodOnBoardingState{
    data class Title(val value: String): FoodOnBoardingState()
    data class Subtitle(val value: String): FoodOnBoardingState()
    data class SectionTitle(val value: String): FoodOnBoardingState()
    data class Item(val value: FoodOnBoardingItem): FoodOnBoardingState()
}

data class FoodOnBoardingItem(
    @DrawableRes val icon: Int,
    val title: String
)

val firstOnBoardingUiModel = listOf(
    FoodOnBoardingState.Title("Pendaftaran data usaha"),
    FoodOnBoardingState.Subtitle("Lengkapi data & dokumen yang diperlukan."),
    FoodOnBoardingState.SectionTitle("Berikut data & dokumen yang perlu Kamu siapkan:"),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_id,
        title = "<b>KTP</b> pemilik usaha"
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_bank_card,
        title = "<b>Nomor rekening</b> untuk pencairan dana"
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_document,
        title = "<b>Dokumen pendukung</b> (buku tabungan/rekening koran), jika nama pemilik rekening bank tidak sesuai dengan nama pemilik usaha"
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_id,
        title = "<b>NPWP pemilik usaha</b> (jika memiliki tarif pajak restoran PB1)."
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_contact,
        title = "<b>Alamat lengkap & nomor telepon</b> outlet."
    ))
)

val secondOnBoardingUiModel = listOf(
    FoodOnBoardingState.Title("Verifikasi data usaha"),
    FoodOnBoardingState.Subtitle("Data usaha yang Kamu kirim akan kami periksa untuk memastikan semuanya sudah lengkap dan sesuai."),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_time,
        title = "Proses sekitar <b>2 hari kerja</b>"
    ))
)

val thirdOnBoardingUiModel = listOf(
    FoodOnBoardingState.Title("Aktivasi layanan Mitra T-Food"),
    FoodOnBoardingState.Subtitle("Jika semua data usaha Kamu sudah oke, layanan Mitra T-Food yang Kamu pilih akan mulai kami aktifkan."),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_time,
        title = "Proses sekitar <b>1-7 hari kerja</b>"
    )),
    FoodOnBoardingState.SectionTitle("Jika memilih layanan Mitra T-Food, berikut data yang perlu dilengkapi saat proses aktivasi:"),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_profile_photo,
        title = "<b>Foto profil restoran</b>: Untuk ditampilkan di T-Food, berupa makanan yang dijual."
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_time,
        title = "<b>Jam operasional</b>: Min. 3 hari seminggu dan 3 jam per hari."
    )),
    FoodOnBoardingState.Item(FoodOnBoardingItem(
        icon = R.drawable.ic_food_registration_menu,
        title = "<b>Daftar menu</b>: Min. 3 Jenis menu."
    )),
)