package id.teman.app.mitra.ui.registration

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomLoading
import id.teman.app.mitra.common.PHONE_NUMBER_REGEX
import id.teman.app.mitra.common.TextInputField
import id.teman.app.mitra.common.convertToAllowedIndonesianNumber
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.ui.MainViewModel
import id.teman.app.mitra.ui.destinations.OtpScreenDestination
import id.teman.app.mitra.ui.destinations.WebviewScreenDestination
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination(
    route = "referral",
    deepLinks = [
        DeepLink(
            uriPattern = "https://www.temanofficial.co.id/referral?={code}"
        )
    ]
)
@Composable
fun RegistrationPhoneScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
) {
    var phoneNumber by remember { mutableStateOf("") }
    var referral by remember { mutableStateOf(mainViewModel.locationUiState.successGetReferral.orEmpty()) }
    val context = LocalContext.current
    val isRegisterButtonEnabled by remember {
        derivedStateOf {
            PHONE_NUMBER_REGEX.toRegex().matches(phoneNumber)
        }
    }
    val uiState = viewModel.uiState
    LaunchedEffect(key1 = uiState.registrationError, block = {
        uiState.registrationError?.consumeOnce {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    })

    LaunchedEffect(key1 = uiState.registrationSuccess, block = {
        uiState.registrationSuccess?.consumeOnce {
            navigator.navigate(OtpScreenDestination(phoneNumber.convertToAllowedIndonesianNumber()))
        }
    })
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    Scaffold(scaffoldState = scaffoldState, content = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            TemanLogo(modifier = Modifier.padding(top = Theme.dimension.size_16dp))
            Title()
            EnterPhoneNumberWidget(value = phoneNumber, onEnterNumber = {
                phoneNumber = it
            })
            TextInputField(
                title = "Referral *jika ada",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                onChange = {
                    referral = it

                },
                modifier = Modifier.padding(
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp,
                    top = Theme.dimension.size_6dp
                ),
                placeholders = "TMN23",
                text = referral,
                isEnabled = mainViewModel.locationUiState.successGetReferral.orEmpty().isEmpty()
            )
            if (uiState.loading) {
                Dialog(
                    onDismissRequest = { },
                    DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    CustomLoading(modifier = Modifier.fillMaxSize())
                }
            }
        }

    }, bottomBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TemanFilledButton(
                content = "Lanjutkan",
                isEnabled = isRegisterButtonEnabled,
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_32dp,
                activeTextColor = UiColor.white,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.dimension.size_16dp)
            ) {
                viewModel.registerPhoneNumber(phoneNumber, referral)
            }
            TermsConditionText {
                navigator.navigate(
                    WebviewScreenDestination(
                        "https://www.temanofficial.co.id/term-condition"
                    )
                )
            }
        }
    })
}

@Composable
private fun Title() {
    Spacer(modifier = Modifier.height(Theme.dimension.size_56dp))
    Text("Kenalan dulu yuk", style = UiFont.cabinH2sSemiBold)
    Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
    Text(
        "Masukkan nomor HP Kamu biar Teman bantuin daftar ke aplikasi",
        style = UiFont.poppinsP2Medium.copy(
            color = UiColor.neutral600,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(
            top = Theme.dimension.size_12dp,
            bottom = Theme.dimension.size_30dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        )
    )
}

@Composable
private fun ColumnScope.TemanLogo(modifier: Modifier = Modifier) {
    GlideImage(
        imageModel = R.drawable.ic_revamped_teman,
        modifier = Modifier
            .size(Theme.dimension.size_120dp)
            .align(Alignment.CenterHorizontally),
        imageOptions = ImageOptions(contentScale = ContentScale.Fit)
    )
}

@Composable
private fun EnterPhoneNumberWidget(
    value: String,
    errorMessage: String? = "",
    onEnterNumber: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
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
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
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

@Composable
private fun TermsConditionText(onTncClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        append("Dengan Register, anda menyetujui ")
        pushStringAnnotation(tag = "register", annotation = "")
        withStyle(
            style = SpanStyle(
                color = UiColor.primaryRed500, fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                fontFamily = UiFont.Poppins
            )
        ) {
            append("Terms & Conditions")
        }
        pop()
    }

    ClickableText(modifier = Modifier
        .fillMaxWidth()
        .padding(
            top = Theme.dimension.size_16dp,
            bottom = Theme.dimension.size_48dp
        ), text = annotatedText, style = UiFont.poppinsP2Medium.copy(
        textAlign = TextAlign.Center,
        color = UiColor.neutral600
    ), onClick = { offset ->
        annotatedText.getStringAnnotations(tag = "register", start = offset, end = offset)
            .firstOrNull()?.let {
                onTncClick()
            }
    })
}