package id.teman.app.mitra.ui.registration.food

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.destinations.RegistrationFoodBankFormScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationFoodBusinessInformationFormScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationFoodOutletInformationFormScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationFoodOwnerFormScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationMitraSelectionScreenDestination
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun RegistrationFoodChecklistScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationFoodViewModel
) {


    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf("") }


    LaunchedEffect(key1 = uiState.generalError, block = {
        uiState.generalError?.consumeOnce {
            openDialog.value = it
        }
    })

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Ok",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""
                    viewModel.getListBank()
                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }
    LaunchedEffect(key1 = uiState.registerFoodSuccess, block = {
        uiState.registerFoodSuccess?.consumeOnce {
            Toast.makeText(context, "Successfully registered as food partner", Toast.LENGTH_SHORT).show()
            navigator.popBackStack(RegistrationMitraSelectionScreenDestination.route, false)
        }
    })

    LaunchedEffect(key1 = uiState.registerFoodError, block = {
        uiState.registerFoodError?.consumeOnce {
            openDialog.value = it
        }
    })
    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Pendaftaran Mitra T-Food") {
                navigator.popBackStack()
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(
                            bottom = Theme.dimension.size_56dp
                        )
                ) {
                    TemanCircleButton(
                        icon = R.drawable.ic_food_registration_owned_restaurant,
                        iconModifier = Modifier.size(Theme.dimension.size_28dp),
                        circleBackgroundColor = UiColor.primaryRed50,
                        circleModifier = Modifier
                            .padding(
                                top = Theme.dimension.size_16dp,
                                start = Theme.dimension.size_16dp
                            )
                            .size(Theme.dimension.size_56dp)
                    )

                    Text(
                        "Lengkapi data usaha Kamu, ya",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier.padding(
                            top = Theme.dimension.size_32dp,
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                    )

                    Text(
                        "Kalau sudah lengkap, mohon kirim data usahanya agar bisa diperiksa oleh tim kami.",
                        style = UiFont.poppinsP3Medium,
                        modifier = Modifier.padding(
                            top = Theme.dimension.size_16dp, start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Theme.dimension.size_24dp),
                        thickness = Theme.dimension.size_1dp,
                        color = UiColor.neutral300
                    )

                    RowChecklistWidget(
                        icon = R.drawable.ic_food_registration_id,
                        title = "Identitas Pemilik",
                        arrowIcon = if (viewModel.uiState.ownerIdentity != null) R.drawable.ic_mitra_verified else null,
                        onClick = {
                            navigator.navigate(RegistrationFoodOwnerFormScreenDestination)
                        }
                    )
                    RowChecklistWidget(
                        icon = R.drawable.ic_food_registration_bank_card,
                        title = "Informasi rekening bank",
                        arrowIcon = if (viewModel.uiState.bankInformation != null) R.drawable.ic_mitra_verified else null,
                        onClick = {
                            navigator.navigate(RegistrationFoodBankFormScreenDestination)
                        }
                    )
                    RowChecklistWidget(
                        icon = R.drawable.ic_food_registration_document,
                        title = "Informasi usaha",
                        arrowIcon = if (viewModel.uiState.businessStoreName != null) R.drawable.ic_mitra_verified else null,
                        onClick = {
                            navigator.navigate(
                                RegistrationFoodBusinessInformationFormScreenDestination
                            )
                        }
                    )
                    RowChecklistWidget(
                        icon = R.drawable.ic_food_registration_id,
                        title = "Informasi outlet",
                        arrowIcon = if (viewModel.uiState.outletInformation != null) R.drawable.ic_mitra_verified else null,
                        onClick = {
                            navigator.navigate(
                                RegistrationFoodOutletInformationFormScreenDestination
                            )
                        }
                    )

                }
                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.dimension.size_16dp),
                content = "Kirim Data Usaha",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white,
                isEnabled = viewModel.isAllFieldFilled() && !uiState.loading
            ) {
                viewModel.registerFoodUser()
            }
        }
    )
}

@Composable
private fun RowChecklistWidget(
    @DrawableRes icon: Int,
    @DrawableRes arrowIcon: Int? = null,
    title: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_28dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemanCircleButton(
                icon = icon,
                circleModifier = Modifier.size(Theme.dimension.size_56dp),
                iconModifier = Modifier.size(Theme.dimension.size_32dp),
                circleBackgroundColor = UiColor.primaryRed50
            )
            Spacer(modifier = Modifier.padding(start = Theme.dimension.size_16dp))
            Text(title, style = UiFont.poppinsP2SemiBold, modifier = Modifier.weight(8f))
            GlideImage(
                imageModel = arrowIcon ?: R.drawable.ic_arrow_right,
                modifier = Modifier.size(Theme.dimension.size_24dp),
                imageOptions = ImageOptions(
                    colorFilter = if (arrowIcon == null) ColorFilter.tint(color = UiColor.neutral500) else null
                )
            )
        }
        Spacer(modifier = Modifier.padding(top = Theme.dimension.size_16dp))
        Divider(
            modifier = Modifier
                .padding(start = Theme.dimension.size_48dp)
                .fillMaxWidth(),
            color = UiColor.dividerColor,
            thickness = Theme.dimension.size_1dp
        )
    }
}