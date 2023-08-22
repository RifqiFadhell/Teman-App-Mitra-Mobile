package id.teman.app.mitra.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.PHONE_NUMBER_REGEX
import id.teman.app.mitra.common.convertToAllowedIndonesianNumber
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.ui.MainViewModel
import id.teman.app.mitra.ui.PermissionState
import id.teman.app.mitra.ui.destinations.LocationPermissionUIDestination
import id.teman.app.mitra.ui.destinations.OtpScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationPhoneScreenDestination
import id.teman.app.mitra.ui.destinations.WithdrawalBankInformationScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    var phoneNumber by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })
    val isLoginButtonEnabled by remember {
        derivedStateOf {
            PHONE_NUMBER_REGEX.toRegex().matches(phoneNumber)
        }
    }
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.SuccessLogin) {
        uiState.SuccessLogin?.consumeOnce {
            navigator.navigate(OtpScreenDestination(phoneNumber.convertToAllowedIndonesianNumber()))
        }
    }

    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    renderState(state = state, navigator = navigator)

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(bottom = Theme.dimension.size_100dp)
                ) {
                    TemanLogo(modifier = Modifier.padding(top = Theme.dimension.size_16dp))
                    Title()
                    Spacer(modifier = Modifier.height(Theme.dimension.size_48dp))
                    EnterPhoneNumberWidget(
                        modifier = Modifier.focusRequester(focusRequester),
                        errorMessage = uiState.error, value = phoneNumber
                    ) { newValue ->
                        phoneNumber = newValue
                        viewModel.updateErrorMessage(uiState.error)
                    }
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
            Column(modifier = Modifier
                .background(color = UiColor.white)
                .fillMaxWidth()
                .padding(bottom = Theme.dimension.size_16dp)) {
                TemanFilledButton(
                    content = "Masuk",
                    isEnabled = isLoginButtonEnabled,
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    borderRadius = Theme.dimension.size_32dp,
                    activeTextColor = UiColor.white,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimension.size_16dp)
                ) {
                    viewModel.doLogin(phoneNumber)
                }
                RegisterText {
                    navigator.navigate(RegistrationPhoneScreenDestination())
                }
            }
        }
    )
}

@Composable
private fun renderState(state: PermissionState, navigator: DestinationsNavigator) {
    when (state) {
        PermissionState.LocationDenied -> {
            navigator.navigate(LocationPermissionUIDestination)
        }
        PermissionState.StartLocationPermission -> {
        }
        else -> Unit
    }
}

@Composable
private fun RegisterText(onNotRegisteredClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        append("Belum ada akun? ")
        pushStringAnnotation(tag = "register", annotation = "")
        withStyle(
            style = SpanStyle(
                color = UiColor.primaryRed500, fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                fontFamily = UiFont.Poppins
            )
        ) {
            append("Daftar dulu")
        }
        pop()
    }

    ClickableText(modifier = Modifier
        .fillMaxWidth()
        .padding(
            top = Theme.dimension.size_16dp
        ), text = annotatedText, style = UiFont.poppinsP2SemiBold.copy(
        textAlign = TextAlign.Center
    ), onClick = { offset ->
        annotatedText.getStringAnnotations(tag = "register", start = offset, end = offset)
            .firstOrNull()?.let {
                onNotRegisteredClick()
            }
    })
}

@Composable
private fun ColumnScope.Title() {
    Spacer(modifier = Modifier.height(Theme.dimension.size_56dp))
    Text("Selamat Datang, Mitra!", style = UiFont.cabinH2sSemiBold)
    Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
    Text("Welcome back, you’ve been missed!", style = UiFont.poppinsP2Medium)
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
    modifier: Modifier = Modifier,
    value: String,
    errorMessage: String? = "",
    onEnterNumber: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(horizontal = Theme.dimension.size_16dp)
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
                imeAction = ImeAction.Done
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