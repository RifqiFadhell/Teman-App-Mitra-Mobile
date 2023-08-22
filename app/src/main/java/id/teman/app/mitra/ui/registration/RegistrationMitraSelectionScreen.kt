package id.teman.app.mitra.ui.registration

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.MainActivity
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.convertToSp
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationBasicInformationFormScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationIllustrationScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationVerificationProgressScreenDestination
import id.teman.app.mitra.ui.destinations.TransportScreenDestination
import id.teman.app.mitra.ui.registration.uimodel.PartnerType
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun RegistrationMitraSelectionScreen(
    navigator: DestinationsNavigator,
    registerViewModel: RegistrationViewModel = hiltViewModel()
) {

    val openDialog = remember { mutableStateOf("") }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        registerViewModel.initMitraPreference()
    }
    val uiState = registerViewModel.uiState

    LaunchedEffect(key1 = uiState.registrationError, block = {
        uiState.registrationError?.consumeOnce {
            openDialog.value = it
        }
    })
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var selectedMitraType by remember { mutableStateOf<PartnerType>(PartnerType.PartnerFood) }
    val mitraInProgress by remember { mutableStateOf(uiState.mitraList.singleOrNull { it.verifyState != VerifyProcess.DEFAULT }) }
    BackHandler {
        if (modalSheetState.isVisible) {
            coroutineScope.launch {
                modalSheetState.hide()
            }
        } else {
            (context as MainActivity).finish()
        }
    }

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Coba Lagi",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""
                    registerViewModel.initMitraPreference()
                }
            }, dismissible = false) {}
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
            ) {
                GlideImage(
                    imageModel = R.drawable.ic_question,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(screenWidth * 0.6f)
                        .height(screenHeight * 0.3f),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
                Text(
                    "Kamu yakin akan mengganti Mitra Kamu?", style = UiFont.poppinsH3sSemiBold.copy(
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                Text(
                    "Jika kamu pindah ke pilihan Mitra lain, maka akun kamu di ${mitraInProgress?.partnerType?.title.orEmpty()} akan diberhentikan sementara. \n" +
                            "\n" +
                            "Tenang saja, data ${mitraInProgress?.partnerType?.title.orEmpty()} kamu akan tetap tersimpan.",
                    style = UiFont.poppinsP3Medium
                )
                Row(
                    modifier = Modifier
                        .padding(
                            bottom = Theme.dimension.size_24dp,
                            top = Theme.dimension.size_50dp
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        shape = RoundedCornerShape(
                            Theme.dimension.size_30dp
                        ),
                        border = BorderStroke(
                            color = UiColor.primaryRed500,
                            width = Theme.dimension.size_1dp
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = Theme.dimension.size_0dp
                        ),
                        content = {
                            Text(
                                "Tidak",
                                style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    TemanFilledButton(
                        modifier = Modifier.weight(1f),
                        content = "Lanjut", buttonType = ButtonType.Medium,
                        activeColor = UiColor.primaryRed500,
                        borderRadius = Theme.dimension.size_30dp,
                        isEnabled = true,
                        activeTextColor = UiColor.white
                    ) {
                        coroutineScope.launch {
                            modalSheetState.hide()
                            when (selectedMitraType) {
                                PartnerType.PartnerBike,
                                PartnerType.PartnerCar -> {
                                    navigator.navigate(RegistrationIllustrationScreenDestination(
                                        selectedPartnerTitle = selectedMitraType.title
                                    ))
                                }
                                else -> {
                                    navigator.navigate(
                                        RegistrationBasicInformationFormScreenDestination(
                                            selectedPartnerTitle = selectedMitraType.title
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Theme.dimension.size_16dp)
                ) {
                    if (uiState.mitraList.any { it.verifyState != VerifyProcess.DEFAULT }) {
                        Text(
                            "Menu Mitra",
                            style = UiFont.poppinsH3sSemiBold,
                            modifier = Modifier.padding(
                                top = Theme.dimension.size_48dp,
                                bottom = Theme.dimension.size_24dp
                            )
                        )
                    } else {
                        Text(
                            "Selamat datang di pendaftaran aplikasi Mitra Teman",
                            style = UiFont.poppinsH3sSemiBold,
                            modifier = Modifier.padding(
                                top = Theme.dimension.size_48dp
                            )
                        )
                        Text(
                            "Ikuti langkah-langkah aplikasi ini untuk mendaftar jadi Mitra Teman.",
                            style = UiFont.poppinsP3Medium,
                            modifier = Modifier.padding(
                                top = Theme.dimension.size_16dp,
                            )
                        )

                        Text(
                            "Mau daftar jadi Mitra apa?",
                            style = UiFont.poppinsP3SemiBold,
                            modifier = Modifier.padding(
                                top = Theme.dimension.size_40dp,
                                bottom = Theme.dimension.size_24dp
                            )
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        uiState.mitraList.forEach { spec ->
                            SingleMitraRow(
                                title = spec.partnerType.title,
                                subtitle = spec.partnerType.subtitle,
                                verifyingProcess = spec.verifyState,
                                icon = spec.partnerType.icon
                            ) {
                                selectedMitraType = spec.partnerType
                                coroutineScope.launch {
                                    when (spec.verifyState) {
                                        VerifyProcess.VERIFYING -> {
                                            navigator.navigate(
                                                RegistrationVerificationProgressScreenDestination(
                                                    title = spec.partnerType.title
                                                )
                                            )
                                        }
                                        VerifyProcess.VERIFIED -> {
                                            if (spec.partnerType == PartnerType.PartnerFood) {
                                                navigator.navigate(FoodHomeScreenDestination)
                                            } else {
                                                navigator.navigate(TransportScreenDestination)
                                            }
                                        }
                                        VerifyProcess.DEFAULT -> {
                                            // check if other 2 type of mitra is not default status.
                                            val mitraListContainsNotDefaultStatus =
                                                uiState.mitraList.any { it.verifyState != VerifyProcess.DEFAULT }
                                            if (mitraListContainsNotDefaultStatus) {
                                                modalSheetState.show()
                                            } else {
                                                if (spec.partnerType == PartnerType.PartnerFood) {
                                                    navigator.navigate(
                                                        RegistrationBasicInformationFormScreenDestination(
                                                            selectedPartnerTitle = spec.partnerType.title
                                                        )
                                                    )
                                                } else {
                                                    navigator.navigate(
                                                        RegistrationIllustrationScreenDestination(
                                                            selectedPartnerTitle = spec.partnerType.title
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    )
}

@Composable
private fun SingleMitraRow(
    title: String,
    subtitle: String,
    verifyingProcess: VerifyProcess,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    val verifyText =
        if (verifyingProcess == VerifyProcess.VERIFYING) " (Sedang diverifikasi)" else ""
    val verifyIcon = when (verifyingProcess) {
        VerifyProcess.VERIFYING -> R.drawable.ic_mitra_verify
        VerifyProcess.VERIFIED -> R.drawable.ic_mitra_verified
        VerifyProcess.DEFAULT -> R.drawable.ic_arrow_right
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemanCircleButton(
                icon = icon,
                circleModifier = Modifier.size(Theme.dimension.size_56dp),
                iconModifier = Modifier.size(Theme.dimension.size_28dp),
                circleBackgroundColor = UiColor.primaryRed50
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = Theme.dimension.size_16dp)
                    .weight(8f)
            ) {
                Text(
                    buildAnnotatedString {
                        append(title)
                        withStyle(
                            style = SpanStyle(
                                color = UiColor.tertiaryBlue500, fontSize = 12.convertToSp(),
                                fontFamily = UiFont.Poppins, fontWeight = FontWeight.W600
                            )
                        ) {
                            append(verifyText)
                        }
                    },
                    style = UiFont.poppinsP2SemiBold
                )
                Text(
                    subtitle, style = UiFont.poppinsCaptionMedium, modifier = Modifier.padding(
                        top = Theme.dimension.size_6dp
                    )
                )
            }
            GlideImage(
                imageModel = verifyIcon,
                modifier = Modifier.size(Theme.dimension.size_22dp),
                imageOptions = ImageOptions(
                    colorFilter = if (verifyingProcess == VerifyProcess.DEFAULT) ColorFilter.tint(
                        UiColor.neutral500
                    ) else null
                )
            )
        }
        Divider(
            modifier = Modifier
                .padding(vertical = Theme.dimension.size_16dp),
            color = UiColor.neutral100
        )
    }
}

@Serializable
enum class VerifyProcess {
    VERIFYING,
    VERIFIED,
    DEFAULT
}