package id.teman.app.mitra.ui.wallet

import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.destinations.WalletPinOtpValidationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletPinSetupScreenDestination
import id.teman.app.mitra.ui.destinations.WalletScreenDestination
import id.teman.app.mitra.ui.destinations.WithdrawalSummaryScreenDestination
import id.teman.app.mitra.ui.otp.PinView
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.app.mitra.ui.wallet.viewmodel.WalletPinViewModel
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun WalletPinSetupScreen(
    navigator: DestinationsNavigator,
    token: String,
    isReset: Boolean = false,
    viewModel: WalletPinViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.initToken(token)
    }
    var pinValue by remember { mutableStateOf("") }

    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

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

    LaunchedEffect(key1 = focusRequester, block = {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    })

    LaunchedEffect(key1 = uiState.successSetupPin, block = {
        uiState.successSetupPin?.consumeOnce {
            Toast.makeText(context, "Sukses atur pin", Toast.LENGTH_SHORT).show()
            if (isReset) {
                navigator.popBackStack(WithdrawalSummaryScreenDestination.route, true)
            } else {
                navigator.navigate(WalletScreenDestination) {
                    popUpTo(WalletPinSetupScreenDestination.route) {
                        inclusive = true
                    }
                }
            }
        }
    })

    LaunchedEffect(key1 = uiState.errorSetupPin, block = {
        uiState.errorSetupPin?.consumeOnce {
            openDialog.value = it
        }
    })

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
                        text = "Atur PIN Keamanan", modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_44dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally), style = UiFont.poppinsH2SemiBold
                    )
                    Text(
                        text = "Nomor PIN diperlukan saat Kamu akan masuk halaman Pendapatan Mitra",
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
                            digitCount = 6,
                            onPinTextChange = {
                                if (it.isDigitsOnly()) {
                                    pinValue = it
                                }
                            }
                        )
                    }
                    Text(
                        text = "Demi keamanan, hindari angka beruntun atau berulang. Jangan gunakan tanggal lahir kamu" +
                                " ataupun PIN ATM sebagai PIN Pendapatan Mitra",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_16dp
                            ),
                        style = UiFont.poppinsP2Medium,
                        color = UiColor.neutral600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Atur Pin",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white
            ) {
                viewModel.setupWalletPin(pinValue)
            }
        }
    )
}