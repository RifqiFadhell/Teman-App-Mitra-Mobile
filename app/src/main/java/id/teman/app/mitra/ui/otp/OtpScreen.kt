package id.teman.app.mitra.ui.otp

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.ui.NavGraphs
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationMitraSelectionScreenDestination
import id.teman.app.mitra.ui.destinations.TransportScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun OtpScreen(
    navigator: DestinationsNavigator,
    phoneNumber: String,
    viewModel: OtpViewModel = hiltViewModel()
) {
    var pinValue by remember { mutableStateOf("") }

    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val uiState = viewModel.uiState

    LaunchedEffect(key1 = focusRequester, block = {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    })

    LaunchedEffect(key1 = uiState.successRedirectLoginDriver, block = {
        uiState.successRedirectLoginDriver?.consumeOnce {
            navigator.navigate(TransportScreenDestination) {
                popUpTo(NavGraphs.root) {
                    saveState = true
                }
            }
        }
    })

    LaunchedEffect(key1 = uiState.successRedirectLoginRestaurant, block = {
        uiState.successRedirectLoginRestaurant?.consumeOnce {
            navigator.navigate(FoodHomeScreenDestination) {
                popUpTo(NavGraphs.root) {
                    saveState = true
                }
            }
        }
    })

    LaunchedEffect(key1 = uiState.successRedirectRegistration, block = {
        uiState.successRedirectRegistration?.consumeOnce {
            navigator.navigate(RegistrationMitraSelectionScreenDestination)
        }
    })

    LaunchedEffect(key1 = uiState.successSendOtp, block = {
        uiState.successSendOtp?.consumeOnce {
            Toast.makeText(context, "Sukses Mengirim OTP", Toast.LENGTH_LONG).show()
            viewModel.startOtpTimer(it)
        }
    })

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = Theme.dimension.size_20dp)
                ) {
                    TemanLogo()
                    Text(
                        text = "Autentikasi OTP", modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_16dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally), style = UiFont.cabinH2sBold
                    )
                    Text(
                        text = "Pesan dengan kode telah dikirimkan ke nomor telepon $phoneNumber",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        style = UiFont.poppinsP2Medium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .padding(
                                start = Theme.dimension.size_24dp,
                                end = Theme.dimension.size_24dp,
                                top = Theme.dimension.size_40dp
                            )
                    ) {
                        PinView(
                            pinText = pinValue,
                            onPinTextChange = {
                                pinValue = it
                                if (pinValue.length >= 4) {
                                    viewModel.verifyOtpCode(pinValue)
                                }
                            }
                        )
                    }
                    AnimatedVisibility(visible = uiState.errorMessage.isNotEmpty()) {
                        Text(
                            text = uiState.errorMessage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    start = Theme.dimension.size_16dp,
                                    bottom = Theme.dimension.size_16dp
                                ),
                            style = UiFont.poppinsCaptionMedium,
                            color = UiColor.error500,
                            textAlign = TextAlign.Center
                        )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (uiState.otpTimer != null) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = "Permintaan ulang kode: ",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    start = Theme.dimension.size_16dp,
                                    bottom = Theme.dimension.size_16dp
                                ),
                            style = UiFont.poppinsCaptionSmallMedium
                        )
                        Text(
                            text = "00:${uiState.otpTimer}",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    end = Theme.dimension.size_16dp
                                ),
                            style = UiFont.poppinsCaptionSmallSemiBold,
                            color = Color.Blue
                        )
                    }
                } else {
                    TemanFilledButton(
                        content = "Minta Kode Sekali Lagi",
                        buttonType = ButtonType.Large,
                        activeColor = UiColor.primaryRed500,
                        activeTextColor = Color.White,
                        borderRadius = Theme.dimension.size_30dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_24dp
                            ),
                        onClicked = {
                            viewModel.resendOtp()
                            pinValue = ""
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun PinView(
    pinText: String,
    onPinTextChange: (String) -> Unit,
    digitColor: Color = MaterialTheme.colors.onBackground,
    digitCount: Int = 4,
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    BasicTextField(
        value = pinText,
        onValueChange = {
            if (it.length <= digitCount) {
                onPinTextChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (pinText.length == 4) ImeAction.Done else ImeAction.Next
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(digitCount) { index ->
                    val isCursorVisible = pinText.length == index

                    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
                        if (isCursorVisible) {
                            scope.launch {
                                delay(350)
                                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
                            }
                        }
                    }

                    val text = if (index >= pinText.length) "" else pinText[index].toString()
                    Text(
                        text = if (isCursorVisible) cursorSymbol else text,
                        color = digitColor,
                        modifier = Modifier
                            .size(Theme.dimension.size_52dp)
                            .border(
                                width = 1.dp,
                                color = UiColor.neutral100,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(Theme.dimension.size_12dp),
                        style = UiFont.poppinsP3SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_2dp))
                }
            }
        })
}

@Composable
private fun ColumnScope.TemanLogo() {
    GlideImage(
        imageModel = R.drawable.ic_revamped_teman,
        modifier = Modifier
            .size(Theme.dimension.size_120dp)
            .align(Alignment.CenterHorizontally),
        imageOptions = ImageOptions(contentScale = ContentScale.Fit)
    )
}