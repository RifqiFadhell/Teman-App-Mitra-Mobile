package id.teman.app.mitra.ui.registration.food

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.ui.destinations.RegistrationFoodOnBoardingScreenDestination
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun RegistrationFoodPartnerTypeScreen(
    navigator: DestinationsNavigator,
    fullName: String? = "",
    email: String? = "",
    viewModel: RegistrationFoodViewModel
) {

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.setFullNameAndEmail(email.orEmpty(), fullName.orEmpty())
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.dimension.size_16dp)
        ) {
            Text(
                "Pilih jenis Mitra T-Food",
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier.padding(
                    top = Theme.dimension.size_16dp,
                    bottom = Theme.dimension.size_32dp,
                )
            )
            RestaurantPartnerTypeWidget(
                icon = R.drawable.ic_food_registration_owned_restaurant,
                title = "Usaha milik pribadi",
                subtitle = "Kamu bisa ambil orderan T-Bike, T-Food, dan T-Send.",
                onClick = {
                    viewModel.setRestaurantType("personal")
                    navigator.navigate(RegistrationFoodOnBoardingScreenDestination)
                }
            )
            Divider(
                modifier = Modifier.padding(
                    top = Theme.dimension.size_16dp,
                    bottom = Theme.dimension.size_16dp,
                    start = Theme.dimension.size_56dp
                ),
                thickness = Theme.dimension.size_1dp,
                color = UiColor.neutral200
            )
            RestaurantPartnerTypeWidget(
                icon = R.drawable.ic_food_registration_company_restaurant,
                title = "Usaha milik perusahaan",
                subtitle = "Untuk perusahaan dengan izin usaha (PT, CV, Yayasan, dll) yang ingin mulai jualan dan mengelola usahanya pakai Mitra T-Food.",
                onClick = {
                    viewModel.setRestaurantType("company")
                    navigator.navigate(RegistrationFoodOnBoardingScreenDestination)
                }
            )
            Divider(
                modifier = Modifier.padding(
                    top = Theme.dimension.size_16dp,
                    start = Theme.dimension.size_56dp
                ),
                thickness = Theme.dimension.size_1dp,
                color = UiColor.neutral200
            )
        }
    }
}

@Composable
private fun RestaurantPartnerTypeWidget(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TemanCircleButton(
            icon = icon,
            circleModifier = Modifier.size(Theme.dimension.size_56dp),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            circleBackgroundColor = UiColor.primaryRed50
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = UiFont.poppinsP2SemiBold)
            Spacer(modifier = Modifier.height(Theme.dimension.size_6dp))
            Text(subtitle, style = UiFont.poppinsCaptionMedium)
        }
        GlideImage(
            imageModel = R.drawable.ic_arrow_right,
            modifier = Modifier.size(Theme.dimension.size_24dp),
            imageOptions = ImageOptions(
                colorFilter = ColorFilter.tint(
                    color = UiColor.primaryRed500
                )
            )
        )
    }
}