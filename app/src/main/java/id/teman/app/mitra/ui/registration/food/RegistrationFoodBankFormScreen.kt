package id.teman.app.mitra.ui.registration.food

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.mitra.R
import id.teman.app.mitra.common.BankTextFormField
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.domain.model.camera.CameraType
import id.teman.app.mitra.ui.destinations.SmallCameraScreenDestination
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.registration.driver.SingleImageFormField
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Destination
@Composable
fun RegistrationFoodBankFormScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationFoodViewModel,
    resultLargePhoto: ResultRecipient<SmallCameraScreenDestination, String>
) {
    val uiState = viewModel.uiState
    var bankBookAccountPhoto by rememberSaveable {
        mutableStateOf(
            viewModel.uiState.bankInformation?.bookImage ?: Uri.EMPTY
        )
    }
    var bankName by rememberSaveable { mutableStateOf(viewModel.uiState.bankInformation?.name.orEmpty()) }
    var bankAccountNumber by rememberSaveable { mutableStateOf(viewModel.uiState.bankInformation?.accountNumber.orEmpty()) }
    var bankOwnerName by rememberSaveable { mutableStateOf(viewModel.uiState.bankInformation?.ownerName.orEmpty()) }
    var isValid by rememberSaveable { mutableStateOf(true) }
    val scrollState = rememberScrollState()
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
                    text = "Coba Lagi",
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

    resultLargePhoto.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(result.value)
                when (value.cameraType) {
                    CameraType.BANKACCOUNT -> {
                        bankBookAccountPhoto = Uri.parse(value.uri)
                    }
                    else -> Unit
                }
            }
        }
    }
    Scaffold(topBar = {
        CenteredTopNavigation(title = "Identitas Rekening Bank") {
            navigator.popBackStack()
        }
    }, content = {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp,
                    top = Theme.dimension.size_16dp,
                    bottom = Theme.dimension.size_68dp
                )
        ) {
            SingleImageFormField(
                title = "Foto Buku Rekening",
                hint = "Unggah foto buku rekening",
                imagePath = bankBookAccountPhoto.path.orEmpty(),
                icon = R.drawable.ic_camera,
                errorMessage = if (!isValid && bankBookAccountPhoto == Uri.EMPTY) "Mohon Unggah foto buku rekening" else null
            ) {
                navigator.navigate(
                    SmallCameraScreenDestination(
                        cameraSpec = CameraSpec(
                            title = "Buku Rekening",
                            largeCamera = false,
                            cameraType = CameraType.BANKACCOUNT,
                            description = "Silahkan pas kan foto outlet mu dari luar dan fotokan outlet mu sebaik mungkin",
                            cameraCrop = Pair(1f, 0.3f)
                        )
                    )
                )
            }
            BankTextFormField(value = bankName, listBank = uiState.listBank, onSelected = {
                bankName = it
            })
            FormTextField(
                title = "Nomor Rekening",
                hint = "Masukkan nomor rekening",
                keyboardType = KeyboardType.Number,
                textFieldValue = bankAccountNumber,
                errorMessage = if (!isValid && bankAccountNumber.isEmpty()) "Mohon masukkan nomor rekening" else null
            ) {
                bankAccountNumber = it
            }
            FormTextField(
                title = "Nama Pemilik Buku Rekening",
                hint = "Masukkan nama pemilik buku rekening",
                textFieldValue = bankOwnerName,
                errorMessage = if (!isValid && bankOwnerName.isEmpty()) "Mohon masukkan nama pemilik buku rekening" else null
            ) {
                bankOwnerName = it
            }
        }
    }, bottomBar = {
        TemanFilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Theme.dimension.size_16dp, vertical = Theme.dimension.size_16dp
                ),
            content = "Simpan Perubahan",
            buttonType = ButtonType.Medium,
            activeColor = UiColor.primaryRed500,
            borderRadius = Theme.dimension.size_30dp,
            activeTextColor = UiColor.white
        ) {
            if (bankAccountNumber.isNotEmpty() && bankOwnerName.isNotEmpty() && bankName.isNotEmpty() && bankBookAccountPhoto != Uri.EMPTY) {
                viewModel.setBankInformation(
                    bankName = bankName,
                    bankAccountNumber = bankAccountNumber,
                    bankAccountBookImage = bankBookAccountPhoto,
                    bankOwnerName = bankOwnerName
                )
                navigator.popBackStack()
            } else {
                isValid = false
            }
        }
    })
}