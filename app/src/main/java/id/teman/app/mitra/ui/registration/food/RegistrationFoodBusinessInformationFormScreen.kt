package id.teman.app.mitra.ui.registration.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CategoriesRestaurantFormField
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun RegistrationFoodBusinessInformationFormScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationFoodViewModel
) {
    var businessName by rememberSaveable { mutableStateOf(viewModel.uiState.businessStoreName.orEmpty()) }
    var isValid by rememberSaveable { mutableStateOf(true) }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getListCategoriesRestaurant()
    }

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
                    text = "OK",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""
                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }

    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Informasi Usaha") {
                navigator.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(bottom = Theme.dimension.size_56dp)
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(Theme.dimension.size_16dp)
            ) {
                FormTextField(
                    title = "Nama Usaha",
                    hint = "Masukkan nama usaha",
                    bottomHint = "Harap baca panduan terlebih dahulu",
                    textFieldValue = businessName,
                    errorMessage = if (!isValid && businessName.isEmpty()) "Harap masukkan nama usaha" else null
                ) {
                    businessName = it
                }
                CategoriesRestaurantFormField(value = selectedCategory, onSelected = {
                    viewModel.setCategoryRestaurant(it.id)
                    selectedCategory = it.name
                }, listBank = viewModel.uiState.listCategories)
                Text(
                    "Panduan menulis nama usaha",
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier.padding(top = Theme.dimension.size_32dp)
                )
                Text(
                    stringResource(id = R.string.registration_business_form_text),
                    style = UiFont.poppinsP2Medium
                )
                Text(
                    "Resto Abah Genteng, Cawas",
                    style = UiFont.poppinsP3SemiBold.copy(color = UiColor.tertiaryBlue500),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Theme.dimension.size_32dp
                        )
                        .background(
                            color = UiColor.tertiaryBlue50,
                        )
                        .padding(
                            vertical = Theme.dimension.size_12dp,
                            horizontal = Theme.dimension.size_36dp
                        )
                )
                Text(
                    "Yang perlu dihindari",
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier.padding(
                        top = Theme.dimension.size_32dp,
                        bottom = Theme.dimension.size_8dp
                    )
                )
                Text(
                    stringResource(id = R.string.registration_business_form_avoid_hint),
                    style = UiFont.poppinsP2Medium
                )
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Simpan Perubahan",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white
            ) {
                if (businessName.isNotEmpty()) {
                    viewModel.setBusinessStoreName(businessName)
                    navigator.popBackStack()
                } else {
                    isValid = false
                }
            }
        }
    )
}

val description =
    "<b>Yang perlu hindari</b>:\n1. Penggunaan nama usaha yang terlalu umum seperti ‘Kuliner’, ‘Warung Makan’, ‘Makanan’, dll.\n2. Penggunaan emoji, simbol, atau ikon.\n\n3. Harap tidak menulis dengan huruf kapital semua"