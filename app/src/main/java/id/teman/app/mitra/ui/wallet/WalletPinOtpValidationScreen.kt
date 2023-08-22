package id.teman.app.mitra.ui.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.ui.destinations.WalletPinOtpValidationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletPinSetupScreenDestination
import id.teman.app.mitra.ui.destinations.WithdrawPinConfirmationScreenDestination
import id.teman.app.mitra.ui.otp.PinView
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.app.mitra.ui.wallet.viewmodel.WalletOtpVerificationViewModel
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun WalletPinOtpValidationScreen(
    navigator: DestinationsNavigator,
    phone: String,
    isResetPin: Boolean = false,
    viewModel: WalletOtpVerificationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var pinValue by remember { mutableStateOf("") }

    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var isApiCalled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = focusRequester, block = {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    })

    LaunchedEffect(key1 = uiState.successRedirectSetPinPage, block = {
        uiState.successRedirectSetPinPage?.consumeOnce {
            navigator.navigate(WalletPinSetupScreenDestination(it, isReset = isResetPin)) {
                popUpTo(WalletPinOtpValidationScreenDestination.route) {
                    inclusive = true
                }
            }
        }
    })

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        if (!isApiCalled) {
            viewModel.requestOtp()
            isApiCalled = true
        }
    }

    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Pendapatan Mitra") {
                navigator.popBackStack()
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Autentikasi OTP", modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_44dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally), style = UiFont.poppinsH2SemiBold
                    )
                    Text(
                        text = "Pesan dengan kode telah dikirimkan ke nomor telepon $phone",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_12dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral600)
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
                                    viewModel.verifyOtp(pinValue)
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
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_16dp
                            ),
                        content = "Minta Kode Sekali Lagi",
                        buttonType = ButtonType.Medium,
                        activeColor = UiColor.primaryRed500,
                        borderRadius = Theme.dimension.size_30dp,
                        activeTextColor = UiColor.white
                    ) {
                        viewModel.requestOtp()
                        pinValue = ""
                    }
                }
            }
        }
    )
}