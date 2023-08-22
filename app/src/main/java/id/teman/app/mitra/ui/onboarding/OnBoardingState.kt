package id.teman.app.mitra.ui.onboarding

import androidx.annotation.DrawableRes
import id.teman.app.mitra.R

sealed class OnBoardingState(
    @DrawableRes val topIcon: Int = R.drawable.ic_onboarding_top_icon,
    @DrawableRes val backgroundRes: Int,
    val topIconTitle: String = "Teman",
    val title: String,
    val subtitle: String
) {
    object FirstOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_onboarding_bg_1,
        title = "Halo, Mitra Teman!",
        subtitle = "Selamat Bergabung.\n" +
                "Yakinlah Hari Ini Lebih Baik dari Hari Kemarin ya!"
    )

    object SecondOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_onboarding_second,
        title = "Dapatkan penghasilan tambahan",
        subtitle = "Jangan Lupa Bersyukur dan Selalu Waspada Dalam Berkendara ya."
    )

    object FirstRegistrationOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_registration_illustration_1,
        title = "Isi data diri",
        subtitle = "Pertama, isi data diri singkat untuk profil Kamu."
    )

    object SecondRegistrationOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_registration_illustration_2,
        title = "Unggah dokumen pendaftaran",
        subtitle = "Siapkan KTP, SIM, STNK, dan SKCK untuk diunggah, serta isi formulirnya."
    )

    object ThirdRegistrationOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_registration_illustration_3,
        title = "Verifikasi data oleh Teman",
        subtitle = "Kami akan hubungi Kamu lewat SMS untuk info hasil verifikasi dan langkah selanjutnya."
    )

    object FourthRegistrationOnBoarding: OnBoardingState(
        backgroundRes = R.drawable.ic_registration_illustration_4,
        title = "Aktivasi akun Kamu",
        subtitle = "Kami akan hubungi Kamu jika akun Kamu sudah aktif. Setelah aktif, Kamu bisa langsung login dan terima order."
    )
}
