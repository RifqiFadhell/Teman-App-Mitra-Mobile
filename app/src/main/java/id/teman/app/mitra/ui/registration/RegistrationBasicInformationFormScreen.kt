package id.teman.app.mitra.ui.registration

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.ui.destinations.RegistrationDocumentScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationFoodPartnerTypeScreenDestination
import id.teman.app.mitra.ui.registration.uimodel.PartnerType
import id.teman.app.mitra.ui.registration.uimodel.T_BIKE_TITLE
import id.teman.app.mitra.ui.registration.uimodel.T_CAR_TITLE
import id.teman.app.mitra.ui.registration.uimodel.UserBasicRegistrationRequestUiModel
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun RegistrationBasicInformationFormScreen(
    navigator: DestinationsNavigator,
    selectedPartnerTitle: String,
    viewModel: RegistrationViewModel = hiltViewModel()
) {

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    var nameField by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }

    var selectedPartnerType by remember { mutableStateOf(
        when(selectedPartnerTitle) {
            T_BIKE_TITLE -> PartnerType.PartnerBike
            T_CAR_TITLE -> PartnerType.PartnerCar
            else -> PartnerType.PartnerFood
        }
    ) }
    val partnerTypes = remember {
        mutableListOf(
            PartnerType.PartnerBike,
            PartnerType.PartnerCar,
            PartnerType.PartnerFood
        )
    }
    val unSelectedPartnerTypes by remember {
        derivedStateOf { partnerTypes.filterNot { it == selectedPartnerType } }
    }

    var isValidEmail by remember {
        mutableStateOf(true)
    }

    val keyboard = LocalSoftwareKeyboardController.current

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        content = {
            Column {
                Card(modifier = Modifier.fillMaxWidth(), elevation = Theme.dimension.size_8dp) {
                    Box(modifier = Modifier.padding(Theme.dimension.size_8dp)) {
                        GlideImage(
                            imageModel = R.drawable.ic_arrow_left_long,
                            modifier = Modifier.size(Theme.dimension.size_24dp).noRippleClickable {
                                navigator.popBackStack()
                            }
                        )
                        Text(
                            "Lengkapi Profil Kamu", style = UiFont.poppinsH5SemiBold,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
                PartnerSelectedWidget(selectedPartnerType) {
                    coroutineScope.launch {
                        modalSheetState.show()
                    }
                }
                EnterNameFieldWidget(nameField) {
                    nameField = it
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                EnterEmailWidget(emailField, isValidEmail) {
                    isValidEmail = true
                    emailField = it
                }
                Spacer(modifier = Modifier.weight(1f))
                TemanFilledButton(
                    content = "Lanjutkan",
                    isEnabled = nameField.isNotEmpty() && emailField.isNotEmpty(),
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = UiColor.white,
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_24dp,
                            vertical = Theme.dimension.size_16dp
                        ),
                    onClicked = {
                        keyboard?.hide()
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailField).matches()) {
                            if (selectedPartnerType == PartnerType.PartnerFood) {
                                navigator.navigate(RegistrationFoodPartnerTypeScreenDestination(
                                    fullName = nameField,
                                    email = emailField
                                ))
                            } else {
                                navigator.navigate(
                                    RegistrationDocumentScreenDestination(
                                        basicInformationBundle = UserBasicRegistrationRequestUiModel(
                                            email = emailField,
                                            fullName = nameField,
                                            partnerType = selectedPartnerType.title
                                        )
                                    )
                                )
                            }
                        } else {
                            isValidEmail = false
                        }
                    }
                )
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = UiColor.white,
                        shape = RoundedCornerShape(Theme.dimension.size_16dp)
                    )
                    .padding(horizontal = Theme.dimension.size_16dp)
            ) {
                Spacer(modifier = Modifier.padding(top = Theme.dimension.size_36dp))
                Text("Mau daftar jadi Mitra apa?", style = UiFont.poppinsP3SemiBold)

                repeat(unSelectedPartnerTypes.size) { index ->
                    Spacer(modifier = Modifier.padding(top = Theme.dimension.size_24dp))
                    UnselectedPartnerSingleRowBottomSheetWidget(partnerType = unSelectedPartnerTypes[index]) { selectedPartner ->
                        selectedPartnerType = selectedPartner
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.dimension.size_16dp),
                        color = UiColor.neutral100,
                        thickness = Theme.dimension.size_1dp
                    )
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_48dp))
            }
        }
    )
}

@Composable
fun UnselectedPartnerSingleRowBottomSheetWidget(
    partnerType: PartnerType,
    onSelectedPartner: (PartnerType) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .noRippleClickable {
            onSelectedPartner(partnerType)
        }) {
        TemanCircleButton(
            icon = partnerType.icon,
            circleModifier = Modifier.size(Theme.dimension.size_56dp),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            circleBackgroundColor = partnerType.backgroundColor
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(partnerType.title, style = UiFont.poppinsP2SemiBold)
            Spacer(modifier = Modifier.height(Theme.dimension.size_6dp))
            Text(partnerType.subtitle, style = UiFont.poppinsCaptionMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        GlideImage(
            imageModel = R.drawable.ic_arrow_right,
            modifier = Modifier.size(Theme.dimension.size_24dp)
        )
    }
}

@Composable
fun PartnerSelectedWidget(partnerType: PartnerType, onPartnerChangeClicked: () -> Unit) {
    Text(
        "Mendaftar sebagai:",
        style = UiFont.poppinsP1SemiBold,
        modifier = Modifier.padding(
            horizontal = Theme.dimension.size_16dp
        )
    )
    Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Theme.dimension.size_16dp
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        TemanCircleButton(
            icon = partnerType.icon,
            circleModifier = Modifier.size(Theme.dimension.size_56dp),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            circleBackgroundColor = partnerType.backgroundColor
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Text(partnerType.title, style = UiFont.poppinsP1SemiBold)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Ganti", modifier = Modifier
                .noRippleClickable {
                    onPartnerChangeClicked()
                }
                .background(
                    color = UiColor.primaryRed500,
                    shape = RoundedCornerShape(Theme.dimension.size_30dp)
                )
                .padding(
                    vertical = Theme.dimension.size_8dp,
                    horizontal = Theme.dimension.size_12dp
                ), style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.white)
        )
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_20dp),
        color = UiColor.neutral100,
        thickness = Theme.dimension.size_1dp
    )
}

@Composable
fun EnterNameFieldWidget(nameValue: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)) {
        Text(
            "Nama Lengkap",
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = nameValue,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            placeholder = {
                Text(
                    "Harap isi nama kamu sesuai dengan KTP",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
                )
            },
            onValueChange = {
                val newValue = it.filter { text-> !text.isDigit() }
                onValueChange(newValue)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
fun EnterEmailWidget(emailValue: String, isValidEmail: Boolean,  onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)) {
        Text(
            "Email",
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = emailValue,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            placeholder = {
                Text(
                    "Harap isi email kamu",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
                )
            },
            onValueChange = {
                onValueChange(it)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        )
        if (!isValidEmail) {
            Text(
                "Email tidak valid",
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
    }
}