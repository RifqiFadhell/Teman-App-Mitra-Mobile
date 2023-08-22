package id.teman.app.mitra.ui.registration.food

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
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
fun RegistrationFoodOwnerFormScreen(
    navigator: DestinationsNavigator,
    resultSmallPhoto: ResultRecipient<SmallCameraScreenDestination, String>,
    viewModel: RegistrationFoodViewModel
) {

    var ktpName by rememberSaveable { mutableStateOf(viewModel.uiState.ownerIdentity?.ktpName.orEmpty()) }
    var ktpNumber by rememberSaveable { mutableStateOf(viewModel.uiState.ownerIdentity?.ktpNumber.orEmpty()) }
    var ktpImage by rememberSaveable { mutableStateOf(viewModel.uiState.ownerIdentity?.ktpImage ?: Uri.EMPTY) }
    var isValid by rememberSaveable { mutableStateOf(true) }

    resultSmallPhoto.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(result.value)
                when (value.cameraType) {
                    CameraType.KTP -> {
                        ktpImage = Uri.parse(value.uri)
                    }
                    else -> Unit
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Identitas Pemilik") {
                navigator.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Theme.dimension.size_16dp)
            ) {
                SingleImageFormField(
                    title = "KTP",
                    hint = "Unggah Foto KTP",
                    icon = R.drawable.ic_camera,
                    imagePath = ktpImage.path.orEmpty(),
                    errorMessage = if (!isValid && ktpImage == Uri.EMPTY) "Harap unggah foto KTP" else null
                ) {
                    navigator.navigate(
                        SmallCameraScreenDestination(
                            cameraSpec = CameraSpec("KTP", false, CameraType.KTP)
                        )
                    )
                }
                FormTextField(
                    title = "Nama Sesuai KTP",
                    hint = "Masukkan nama sesuai KTP",
                    textFieldValue = ktpName,
                    errorMessage = if (!isValid && ktpName.isEmpty()) "Harap isi nama kamu sesuai KTP" else null
                ) {
                    ktpName = it
                }
                FormTextField(
                    title = "Nomor KTP",
                    hint = "Masukkan nomor KTP",
                    textFieldValue = ktpNumber,
                    maxLength = 16,
                    keyboardType = KeyboardType.Number,
                    errorMessage = if (!isValid && ktpNumber.isEmpty()) "Harap isi nomer KTP kamu" else null
                ) {
                    ktpNumber = it
                }
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
                if (ktpName.isNotEmpty() && ktpNumber.isNotEmpty() && ktpImage != Uri.EMPTY) {
                    viewModel.setOwnerIdentity(
                        ktpName = ktpName,
                        ktpNumber = ktpNumber,
                        ktpImage = ktpImage
                    )
                    navigator.popBackStack()
                } else {
                    isValid = false
                }
            }
        }
    )
}