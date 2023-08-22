package id.teman.app.mitra.ui.registration.food

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.domain.model.camera.CameraType
import id.teman.app.mitra.domain.model.location.PlaceDetailSpec
import id.teman.app.mitra.ui.destinations.LargeCameraScreenDestination
import id.teman.app.mitra.ui.destinations.SearchLocationScreenDestination
import id.teman.app.mitra.ui.destinations.SmallCameraScreenDestination
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.registration.driver.SingleImageFormField
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Destination
@Composable
fun RegistrationFoodOutletInformationFormScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationFoodViewModel,
    resultLargePhoto: ResultRecipient<SmallCameraScreenDestination, String>,
    resultSearchLocation: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
) {
    var postalCode by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.postalCode.orEmpty()) }
    var outletPhoneNumber by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletPhoneNumber.orEmpty()) }
    var completeOutletAddress by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletCompleteAddress.orEmpty()) }
    var nearbyHintAddress by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletNearbyHint.orEmpty()) }
    var optionalAddress by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletOptionalAddress.orEmpty()) }
    var outletPhoto by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletPhoto ?:Uri.EMPTY) }
    var isValid by rememberSaveable { mutableStateOf(true) }
    var latitude by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletLatitude.orZero()) }
    var longitude by rememberSaveable { mutableStateOf(viewModel.uiState.outletInformation?.outletLongitude.orZero()) }
    val scrollState = rememberScrollState()

    resultLargePhoto.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val jsonValue = Json.decodeFromString<CameraResult>(result.value)
                when (jsonValue.cameraType) {
                    CameraType.STOREPHOTOOUTLET -> {
                        outletPhoto = Uri.parse(jsonValue.uri)
                    }
                    else -> Unit
                }
            }
        }
    }

    resultSearchLocation.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                latitude = result.value.locationLatLng.latitude
                longitude = result.value.locationLatLng.longitude
                optionalAddress = result.value.formattedAddress
            }
        }
    }

    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Informasi Outlet") {
                navigator.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp,
                        bottom = Theme.dimension.size_80dp
                    )
            ) {
                FormTextField(
                    title = "Kode Pos Outlet",
                    hint = "Masukkan kode pos outlet",
                    textFieldValue = postalCode,
                    keyboardType = KeyboardType.Number,
                    errorMessage = if (!isValid && postalCode.isEmpty()) "Mohon masukkan kode pos outlet" else null,
                    onTextChanged = {
                        postalCode = it
                    })

                EnterPhoneNumberWidget(
                    value = outletPhoneNumber,
                    errorMessage = if (!isValid && outletPhoneNumber.isEmpty()) "Mohon masukkan nomor telepon" else null,
                    onEnterNumber = {
                        outletPhoneNumber = it
                    })

                Text(
                    "Jika menggunakan nomor telepon rumah, pastikan untuk menambahkan kode daerah, ya. Contoh +62 21xxxxxxxx untuk Jakarta.",
                    style = UiFont.poppinsCaptionMedium
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimension.size_16dp),
                    color = UiColor.neutral100,
                    thickness = Theme.dimension.size_2dp
                )

                FormTextField(
                    title = "Alamat Lengkap Outlet",
                    hint = "Masukkan alamat lengkap outlet Kamu",
                    textFieldValue = completeOutletAddress,
                    errorMessage = if (!isValid && completeOutletAddress.isEmpty()) "Mohon masukkan alamat lengkap outlet Kamu" else null,
                    onTextChanged = {
                        completeOutletAddress = it
                    })

                FormTextField(
                    title = "Patokan",
                    hint = "Nama jalan, gedung, foodcourt dll",
                    textFieldValue = nearbyHintAddress,
                    onTextChanged = {
                        nearbyHintAddress = it
                    })

                SearchOutletLocation(
                    value = optionalAddress,
                    errorMessage = if (!isValid && optionalAddress.isEmpty()) "Mohon masukkan lokasi outlet" else null
                ) {
                    navigator.navigate(
                        SearchLocationScreenDestination(
                            title = "Set Lokasi Outlet"
                        )
                    )
                }

                SingleImageFormField(
                    title = "Foto Outlet dari luar",
                    hint = "Unggah foto outlet dari luar",
                    imagePath = outletPhoto.path.orEmpty(),
                    icon = R.drawable.ic_camera,
                    errorMessage = if (!isValid && outletPhoto == Uri.EMPTY) "Mohon unggah foto outlet" else null
                ) {
                    navigator.navigate(
                        SmallCameraScreenDestination(
                            cameraSpec = CameraSpec(
                                title = "Outlet",
                                largeCamera = false,
                                cameraType = CameraType.STOREPHOTOOUTLET,
                                description = "Silahkan pas kan foto outlet mu dari luar dan fotokan outlet mu sebaik mungkin",
                                cameraCrop = Pair(1f, 0.35f)
                            )
                        )
                    )
                }
            }
        },
        bottomBar = {
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
                if (postalCode.isNotEmpty() && outletPhoneNumber.isNotEmpty() && completeOutletAddress.isNotEmpty()
                    && nearbyHintAddress.isNotEmpty() && optionalAddress.isNotEmpty() && outletPhoto != Uri.EMPTY
                ) {
                    viewModel.setOutletInformation(
                        postalCode = postalCode,
                        outletPhoneNumber = outletPhoneNumber,
                        outletCompleteAddress = completeOutletAddress,
                        outletNearbyHint = nearbyHintAddress,
                        outletPhoto = outletPhoto,
                        outletOptionalAddress = optionalAddress,
                        outletLongitude = longitude,
                        outletLatitude = latitude
                    )
                    navigator.popBackStack()
                } else {
                    isValid = false
                }
            }
        }
    )
}

@Composable
private fun SearchOutletLocation(value: String, errorMessage: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Theme.dimension.size_24dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Lokasi Outlet",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral900)
        )
        Text(
            "Set Lokasi",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }
    OutlinedTextField(
        value = value,
        enabled = false,
        isError = errorMessage.isNotNullOrEmpty(),
        placeholder = {
            Text(
                "Pilih Lokasi Outlet",
                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
            )
        },
        shape = RoundedCornerShape(Theme.dimension.size_4dp),
        onValueChange = {
            /* no-op */
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = UiColor.neutral100,
            cursorColor = UiColor.black,
            unfocusedBorderColor = UiColor.neutral100
        ),
        modifier = Modifier
            .padding(top = Theme.dimension.size_8dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
    )
    errorMessage?.let {
        Text(
            it,
            style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
            modifier = Modifier.padding(top = Theme.dimension.size_4dp)
        )
    }
}

@Composable
private fun EnterPhoneNumberWidget(
    value: String,
    errorMessage: String? = "",
    onEnterNumber: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(top = Theme.dimension.size_24dp)
    ) {
        Text(
            "Nomor HP",
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = UiFont.poppinsP2Medium,
            onValueChange = onEnterNumber,
            isError = errorMessage.isNotNullOrEmpty(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            maxLines = 1,
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(TextFieldDefaults.MinHeight)
                        .background(color = UiColor.neutral100)
                ) {
                    Text(
                        "+62", style = UiFont.poppinsCaptionSemiBold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
        errorMessage?.let { value ->
            Text(
                value,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
    }
}