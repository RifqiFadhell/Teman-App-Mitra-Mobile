package id.teman.app.mitra.ui.registration.driver

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomLoading
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.domain.model.camera.CameraType
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.destinations.LargeCameraScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationMitraSelectionScreenDestination
import id.teman.app.mitra.ui.destinations.SmallCameraScreenDestination
import id.teman.app.mitra.ui.registration.uimodel.UserBasicRegistrationRequestUiModel
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun RegistrationDocumentScreen(
    navigator: DestinationsNavigator,
    basicInformationBundle: UserBasicRegistrationRequestUiModel,
    resultSmallPhoto: ResultRecipient<SmallCameraScreenDestination, String>,
    resultLargePhoto: ResultRecipient<LargeCameraScreenDestination, String>,
    registrationViewModel: RegistrationViewModel = hiltViewModel()
) {

    val scrollState = rememberScrollState()
    var ktpImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var simImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var stnkImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var skckImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var profileImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var vehicleImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var ktpValue by rememberSaveable { mutableStateOf("") }
    var simValue by rememberSaveable { mutableStateOf("") }
    var brand by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var fuel by rememberSaveable { mutableStateOf("") }
    var platNumber by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    var errorKtpImage by rememberSaveable { mutableStateOf<String?>(null) }
    var errorSimImage by rememberSaveable { mutableStateOf<String?>(null) }
    var errorStnkImage by rememberSaveable { mutableStateOf<String?>(null) }
    var errorProfileImage by rememberSaveable { mutableStateOf<String?>(null) }
    var errorVehicleImage by rememberSaveable { mutableStateOf<String?>(null) }
    var errorKtpValue by rememberSaveable { mutableStateOf<String?>(null) }
    var errorSimValue by rememberSaveable { mutableStateOf<String?>(null) }
    var errorBrand by rememberSaveable { mutableStateOf<String?>(null) }
    var errorYear by rememberSaveable { mutableStateOf<String?>(null) }
    var errorType by rememberSaveable { mutableStateOf<String?>(null) }
    var errorFuel by rememberSaveable { mutableStateOf<String?>(null) }
    var errorPlatNumber by rememberSaveable { mutableStateOf<String?>(null) }
    var errorCity by rememberSaveable { mutableStateOf<String?>(null) }

    resultSmallPhoto.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                // no-op
            }

            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(result.value)
                when (value.cameraType) {
                    CameraType.KTP -> {
                        errorKtpImage = null
                        ktpImage = Uri.parse(value.uri)
                    }

                    CameraType.SIM -> {
                        errorSimImage = null
                        simImage = Uri.parse(value.uri)
                    }

                    CameraType.STNK -> {
                        errorStnkImage = null
                        stnkImage = Uri.parse(value.uri)
                    }

                    CameraType.SKCK -> {
                        skckImage = Uri.parse(value.uri)
                    }

                    CameraType.PROFILE -> {
                        errorProfileImage = null
                        profileImage = Uri.parse(value.uri)
                    }

                    CameraType.VEHICLE -> {
                        errorVehicleImage = null
                        vehicleImage = Uri.parse(value.uri)
                    }

                    else -> Unit
                }
            }
        }
    }

    resultLargePhoto.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(result.value)
                when (value.cameraType) {
                    CameraType.PROFILE -> {
                        errorProfileImage = null
                        profileImage = Uri.parse(value.uri)
                    }

                    else -> {
                        errorVehicleImage = null
                        vehicleImage = Uri.parse(value.uri)
                    }
                }
            }
        }
    }

    val uiState = registrationViewModel.uiState
    val openDialog = remember { mutableStateOf("") }

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
    LaunchedEffect(key1 = uiState.registrationError, block = {
        uiState.registrationError?.consumeOnce {
            openDialog.value = it
        }

    })
    LaunchedEffect(key1 = uiState.registrationSuccess, block = {
        uiState.registrationSuccess?.consumeOnce {
            navigator.popBackStack(RegistrationMitraSelectionScreenDestination, false)
        }
    })

    fun validateAllFields(): Boolean {
        errorProfileImage = if (profileImage != Uri.EMPTY) null else "Harap unggah foto profil"
        errorKtpImage = if (ktpImage != Uri.EMPTY) null else "Harap unggah foto KTP"
        errorSimImage = if (simImage != Uri.EMPTY) null else "Harap unggah foto SIM"
        errorStnkImage = if (stnkImage != Uri.EMPTY) null else "Harap unggah foto STNK"
        errorVehicleImage = if (vehicleImage != Uri.EMPTY) null else "Harap unggah foto kendaraan"
        errorKtpValue = if (ktpValue.isNotNullOrEmpty()) null else "Harap input nomor KTP"
        errorSimValue = if (simValue.isNotNullOrEmpty()) null else "Harap input nomor SIM"
        errorBrand = if (brand.isNotNullOrEmpty()) null else "Harap input merek kendaraan"
        errorType = if (type.isNotNullOrEmpty()) null else "Harap input tipe kendaraan"
        errorFuel = if (fuel.isNotNullOrEmpty()) null else "Harap input tipe bahan bakar kendaraan"
        errorPlatNumber = if (platNumber.isNotNullOrEmpty()) null else "Harap input plat nomor"
        return errorProfileImage == null && errorKtpImage == null && errorSimImage == null &&
                errorStnkImage == null && errorVehicleImage == null &&
                errorKtpValue == null && errorSimValue == null && errorBrand == null &&
                errorType == null && errorFuel == null && errorPlatNumber == null
    }

    Scaffold(
        topBar = {
            BasicTopNavigation(title = "Unggah Dokumen") {
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
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp,
                            bottom = Theme.dimension.size_80dp
                        )
                ) {
                    Spacer(modifier = Modifier.height(Theme.dimension.size_28dp))
                    Text("Unggah Berkas", style = UiFont.poppinsP3SemiBold)
                    Spacer(modifier = Modifier.height(Theme.dimension.size_14dp))
                    Text(
                        "Mohon unggah foto dari berkas-berkas berikut dan isi informasi yang dibutuhkan",
                        style =
                        UiFont.poppinsP2Medium
                    )
                    SingleImageFormField(
                        title = "Foto Profil",
                        hint = "Unggah Foto",
                        errorMessage = errorProfileImage,
                        icon = R.drawable.ic_camera,
                        imagePath = profileImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            LargeCameraScreenDestination(
                                cameraSpec = CameraSpec(
                                    "Unggah Foto Profil Kamu",
                                    false,
                                    CameraType.PROFILE
                                )
                            )
                        )
                    }
                    SingleImageFormField(
                        title = "KTP",
                        hint = "Unggah Foto KTP",
                        errorMessage = errorKtpImage,
                        icon = R.drawable.ic_camera,
                        imagePath = ktpImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            SmallCameraScreenDestination(
                                cameraSpec = CameraSpec("KTP", false, CameraType.KTP)
                            )
                        )
                    }
                    SingleTextFormField(
                        textFieldValue = ktpValue,
                        title = "Nomor KTP",
                        maxLength = 16,
                        keyboardType = KeyboardType.Number,
                        errorMessage = errorKtpValue
                    ) {
                        if (errorKtpValue != null && it.isNotNullOrEmpty()) {
                            errorKtpValue = null
                        }
                        ktpValue = it
                    }
                    SingleImageFormField(
                        title = "SIM",
                        hint = "Unggah Foto SIM",
                        errorMessage = errorSimImage,
                        icon = R.drawable.ic_camera,
                        imagePath = simImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            SmallCameraScreenDestination(
                                cameraSpec = CameraSpec("SIM", false, CameraType.SIM)
                            )
                        )
                    }
                    SingleTextFormField(
                        textFieldValue = simValue,
                        title = "SIM",
                        errorMessage = errorSimValue,
                        keyboardType = KeyboardType.Number
                    ) {
                        if (errorSimValue != null && it.isNotNullOrEmpty()) {
                            errorSimValue = null
                        }
                        simValue = it
                    }
                    SingleImageFormField(
                        title = "STNK",
                        hint = "Unggah Foto STNK",
                        errorMessage = errorStnkImage,
                        icon = R.drawable.ic_download,
                        imagePath = stnkImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            SmallCameraScreenDestination(
                                cameraSpec = CameraSpec("STNK", false, CameraType.STNK)
                            )
                        )
                    }
                    SingleImageFormField(
                        title = "SKCK *)Jika belum memiliki,bisa menggunakan dokumen STNK",
                        hint = "Unggah Foto SKCK",
                        icon = R.drawable.ic_download,
                        imagePath = skckImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            SmallCameraScreenDestination(
                                cameraSpec = CameraSpec("SKCK", false, CameraType.SKCK)
                            )
                        )
                    }
                    SingleImageFormField(
                        title = "Foto Kendaraan *)Dianjurkan untuk mengambil gambar dalam posisi serong tampak depan",
                        hint = "Unggah Foto Kendaraan Mu",
                        icon = R.drawable.ic_download,
                        errorMessage = errorVehicleImage,
                        imagePath = vehicleImage.path.orEmpty()
                    ) {
                        navigator.navigate(
                            LargeCameraScreenDestination(
                                cameraSpec = CameraSpec(
                                    "Unggah Foto Kendaraan Kamu",
                                    false,
                                    CameraType.VEHICLE
                                )
                            )
                        )
                    }
                    SingleTextFormField(
                        textFieldValue = brand,
                        title = "Brand",
                        errorMessage = errorBrand
                    ) {
                        if (errorBrand != null && it.isNotNullOrEmpty()) {
                            errorBrand = null
                        }
                        brand = it
                    }
                    SingleTextFormField(
                        textFieldValue = type,
                        title = "Type",
                        errorMessage = errorType
                    ) {
                        if (errorType != null && it.isNotNullOrEmpty()) {
                            errorType = null
                        }
                        type = it
                    }
                    YearTextFormField(year) { year = it }
                    SingleTextFormField(
                        textFieldValue = fuel,
                        title = "Jenis Bahan Bakar",
                        errorMessage = errorFuel
                    ) {
                        fuel = it
                        if (errorFuel != null && it.isNotNullOrEmpty()) {
                            errorFuel = null
                        }
                    }
                    SingleTextFormField(
                        textFieldValue = platNumber,
                        errorMessage = errorPlatNumber,
                        title = "Plat Nomor"
                    ) {
                        platNumber = it
                        if (errorPlatNumber != null && it.isNotNullOrEmpty()) {
                            errorPlatNumber = null
                        }
                    }
                    SingleTextFormField(
                        textFieldValue = city,
                        errorMessage = errorCity,
                        title = "Kota Domisili"
                    ) {
                        city = it
                        if (errorCity != null && it.isNotNullOrEmpty()) {
                            errorCity = null
                        }
                    }
                }
                if (uiState.loading) {
                    Dialog(
                        onDismissRequest = { },
                        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                    ) {
                        CustomLoading(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                content = "Daftar Menjadi Mitra",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = UiColor.white,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.dimension.size_16dp)
            ) {

                if (validateAllFields()) {
                    registrationViewModel.uploadCompleteRegistrationData(
                        ktpImage = ktpImage,
                        simImage = simImage,
                        stnkImage = stnkImage,
                        skckImage = skckImage,
                        profileImage = profileImage,
                        vehicleImage = vehicleImage,
                        ktpValue = ktpValue,
                        simValue = simValue,
                        brand = brand,
                        year = year,
                        type = type,
                        fuel = fuel,
                        platNumber = platNumber,
                        city = city,
                        basicInfo = basicInformationBundle
                    )
                }
            }
        }
    )
}