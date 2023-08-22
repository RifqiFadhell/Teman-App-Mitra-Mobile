package id.teman.app.mitra.ui.myaccount.restaurant

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomChip
import id.teman.app.mitra.common.TopBar
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.domain.model.camera.CameraType
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.destinations.LargeCameraScreenDestination
import id.teman.app.mitra.ui.destinations.SmallCameraScreenDestination
import id.teman.app.mitra.ui.myaccount.viewmodel.MyAccountRestaurantViewModel
import id.teman.app.mitra.ui.registration.driver.SingleTextFormField
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun EditMyProfileScreen(
    navigator: DestinationsNavigator,
    userInfo: UserInfo,
    result: ResultRecipient<LargeCameraScreenDestination, String>,
    resultSmallCam: ResultRecipient<SmallCameraScreenDestination, String>,
    resultNavigator: ResultBackNavigator<String>,
    viewModel: MyAccountRestaurantViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val title by remember { mutableStateOf(if (userInfo.restaurantInfo != null) "Ubah Profil Toko" else "Ubah Profil") }
    val userPhoto by remember {
        mutableStateOf(
            if (userInfo.restaurantInfo != null) userInfo.restaurantInfo.restaurantPhoto else
                userInfo.driverInfo!!.photo
        )
    }

    var newUserPhoto by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var profileImagePath by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf(userInfo.name) }
    var phoneNumber by rememberSaveable { mutableStateOf(userInfo.phoneNumber) }
    var address by rememberSaveable { mutableStateOf(userInfo.restaurantInfo?.completedAddress.orEmpty()) }
    var email by rememberSaveable { mutableStateOf(userInfo.restaurantInfo?.email.orEmpty()) }
    var isValid by rememberSaveable { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )

    result.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(it.value)
                newUserPhoto = Uri.parse(value.uri)
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        }
    }

    resultSmallCam.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(it.value)
                newUserPhoto = Uri.parse(value.uri)
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        }
    }

    val openDialog = remember { mutableStateOf("") }

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Ok",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""

                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }

    LaunchedEffect(key1 = uiState.updateSuccess, block = {
        uiState.updateSuccess?.consumeOnce {
            Toast.makeText(context, "Sukses memperbaharui profil", Toast.LENGTH_SHORT).show()
        }
    })

    LaunchedEffect(key1 = uiState.updateFailed, block = {
        uiState.updateFailed?.consumeOnce {
            openDialog.value = it
        }
    })
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { results ->
        if (results.isSuccessful) {
            newUserPhoto = results.uriContent
            profileImagePath = results.getUriFilePath(context).orEmpty()
            coroutineScope.launch {
                modalSheetState.hide()
            }
        } else {
            results.error
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            val cropOptions = CropImageContractOptions(uri, CropImageOptions())
            imageCropLauncher.launch(cropOptions)
        }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            TopBar(title = "Mau upload foto dari mana?", icon = R.drawable.ic_round_close) {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    GlideImage(
                        imageModel = R.drawable.ic_gallery,
                        modifier = Modifier
                            .padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Galeri",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            if (userInfo.restaurantInfo != null) {
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
                            } else {
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
                        }
                ) {
                    GlideImage(
                        imageModel = R.drawable.ic_camera,
                        modifier = Modifier
                            .padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Kamera",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopNavigation(title = title) {
                        navigator.popBackStack()
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .padding(horizontal = Theme.dimension.size_16dp)
                        ) {
                            Row(modifier = Modifier.padding(top = Theme.dimension.size_16dp)) {
                                GlideImage(
                                    imageModel = if (newUserPhoto == Uri.EMPTY) userPhoto else newUserPhoto,
                                    modifier = Modifier
                                        .size(Theme.dimension.size_48dp)
                                        .clip(CircleShape),
                                    failure = {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_person),
                                            contentDescription = "failed"
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                                CustomChip(
                                    title = "Upload Foto",
                                    backgroundColor = UiColor.white,
                                    borderColor = UiColor.tertiaryBlue500,
                                    contentColor = UiColor.white,
                                    textColor = UiColor.tertiaryBlue500,
                                    onClick = {
                                        coroutineScope.launch {
                                            modalSheetState.show()
                                        }
                                    }
                                )
                            }
                            EnterPhoneNumberWidget(
                                value = phoneNumber,
                                errorMessage = if (!isValid && phoneNumber.isEmpty()) "Harap isi nomor telepon" else null,
                                onEnterNumber = {
                                    phoneNumber = it
                                },
                                isEnable = false
                            )
                            SingleTextFormField(
                                textFieldValue = name,
                                title = if (userInfo.restaurantInfo != null) "Nama Toko" else "Nama",
                                keyboardType = KeyboardType.Text,
                                errorMessage = if (!isValid && name.isEmpty()) "Harap isi nama" else null
                            ) {
                                name = it
                            }
                            if (userInfo.restaurantInfo != null) {
                                SingleTextFormField(
                                    textFieldValue = email,
                                    title = "Email",
                                    keyboardType = KeyboardType.Text,
                                    errorMessage = if (!isValid && email.isEmpty()) "Harap isi email" else null
                                ) {
                                    email = it
                                }
                                SingleTextFormField(
                                    textFieldValue = address,
                                    title = "Alamat Lengkap",
                                    keyboardType = KeyboardType.Text,
                                    errorMessage = if (!isValid && address.isEmpty()) "Harap isi alamat" else null
                                ) {
                                    address = it
                                }
                            }
                            val rating = userInfo.driverInfo?.rating.orZero()
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = Theme.dimension.size_18dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Rating Kamu ★$rating",
                                    style = UiFont.poppinsP3SemiBold,
                                    modifier = Modifier
                                        .padding(
                                            bottom = Theme.dimension.size_12dp,
                                            top = Theme.dimension.size_10dp
                                        ),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (i in 0 until 5) {
                                    if (i >= rating) {
                                        GlideImage(
                                            imageModel = R.drawable.ic_star,
                                            imageOptions = ImageOptions(
                                                colorFilter = ColorFilter.tint(
                                                    color = UiColor.neutral100
                                                )
                                            ),
                                            modifier = Modifier
                                                .size(Theme.dimension.size_30dp)
                                        )
                                    } else {
                                        GlideImage(
                                            imageModel = R.drawable.ic_star,
                                            imageOptions = ImageOptions(
                                                colorFilter = ColorFilter.tint(
                                                    color = UiColor.primaryYellow500
                                                )
                                            ),
                                            modifier = Modifier
                                                .size(Theme.dimension.size_30dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (uiState.isLoading) {
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
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_16dp
                            ),
                        content = "Simpan Perubahan",
                        buttonType = ButtonType.Medium,
                        isEnabled = !uiState.isLoading,
                        activeColor = UiColor.primaryRed500,
                        borderRadius = Theme.dimension.size_30dp,
                        activeTextColor = UiColor.white
                    ) {
                        if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                            if (userInfo.restaurantInfo != null && email.isNotEmpty() && address.isNotEmpty()) {
                                viewModel.updateRestaurantProfile(
                                    name,
                                    address,
                                    email,
                                    phoneNumber,
                                    newUserPhoto,
                                    uriPath = profileImagePath
                                )
                            } else {
                                viewModel.updateDriverProfile(
                                    name, phoneNumber, newUserPhoto,
                                    uriPath = profileImagePath
                                )
                            }
                        } else {
                            isValid = false
                        }
                    }
                }
            )
        })
}

@Composable
private fun EnterPhoneNumberWidget(
    modifier: Modifier = Modifier,
    value: String,
    errorMessage: String? = "",
    onEnterNumber: (String) -> Unit,
    isEnable: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(top = Theme.dimension.size_24dp)
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
            enabled = isEnable,
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