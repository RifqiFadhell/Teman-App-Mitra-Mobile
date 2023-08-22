package id.teman.app.mitra.ui.wallet

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.domain.model.wallet.WalletDataTransferSpec
import id.teman.app.mitra.ui.destinations.WalletPinOtpValidationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletScreenDestination
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
fun WithdrawPinConfirmationScreen(
    navigator: DestinationsNavigator,
    spec: WalletDataTransferSpec,
    viewModel: WalletPinViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    var pinValue by remember { mutableStateOf("") }

    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(key1 = focusRequester, block = {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    })

    LaunchedEffect(key1 = uiState.successWithdraw, block = {
        uiState.successWithdraw?.consumeOnce {
            Toast.makeText(context, "Sukses withdraw", Toast.LENGTH_SHORT).show()
            navigator.popBackStack(WalletScreenDestination.route, false)
        }
    })

    LaunchedEffect(key1 = uiState.errorSetupPin, block = {
        uiState.errorSetupPin?.consumeOnce {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
                        text = "Konfirmasi Pin Keamanan", modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_44dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally), style = UiFont.poppinsH2SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Masukkan PIN 6 digit nomor untuk memverifikasi bahwa ini benar Kamu.",
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
                                pinValue = it
                            }
                        )
                    }
                    Text(
                        text = "Lupa Pin Anda ?",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Theme.dimension.size_26dp,
                                start = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_16dp
                            ).clickable {
                                        navigator.navigate(WalletPinOtpValidationScreenDestination(uiState.userInfo?.phoneNumber.orEmpty(), true))
                            },
                        style = UiFont.poppinsP2Medium,
                        color = UiColor.tertiaryBlue500,
                        textAlign = TextAlign.Center
                    )
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
                    .wrapContentHeight()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Konfirmasi",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white
            ) {
                viewModel.withdrawBalance(pinValue, spec)
            }
        }
    )
}