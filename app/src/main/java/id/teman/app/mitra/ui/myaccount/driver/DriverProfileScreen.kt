package id.teman.app.mitra.ui.myaccount.driver

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.common.redirectToPlayStore
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.ui.NavGraphs
import id.teman.app.mitra.ui.destinations.AboutUsScreenDestination
import id.teman.app.mitra.ui.destinations.EditMyProfileScreenDestination
import id.teman.app.mitra.ui.destinations.LoginScreenDestination
import id.teman.app.mitra.ui.destinations.OrderHistoryScreenDestination
import id.teman.app.mitra.ui.destinations.ReferralScreenDestination
import id.teman.app.mitra.ui.destinations.RewardScreenDestination
import id.teman.app.mitra.ui.destinations.WalletPinOtpValidationScreenDestination
import id.teman.app.mitra.ui.destinations.WalletScreenDestination
import id.teman.app.mitra.ui.destinations.WebviewScreenDestination
import id.teman.app.mitra.ui.myaccount.viewmodel.ProfileViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun DriverProfileScreen(
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState

    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            openDialog.value = it
        }
    })

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

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getUserProfile()
    }

    LaunchedEffect(key1 = uiState.redirectToLogin, block = {
        uiState.redirectToLogin?.consumeOnce {
            navigator.navigate(LoginScreenDestination) {
                popUpTo(NavGraphs.root)
            }
        }
    })
    Box(modifier = Modifier.fillMaxSize()) {
        uiState.userInfo?.let { userInfo ->
            Scaffold(
                topBar = {
                    MyAccountTopBar()
                }
            ) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    HeaderSection(userInfo) {
                        navigator.navigate(EditMyProfileScreenDestination(userInfo))
                    }
                    AccountSection(navigator = navigator, userInfo = userInfo)
                    OtherInformationSection(
                        navigator = navigator,
                        onLogout = {
                            viewModel.logout()
                        }
                    )
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
}

@Composable
private fun MyAccountTopBar() {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.White,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
    ) {
        Text(
            "Profile",
            style = UiFont.poppinsH3SemiBold,
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HeaderSection(userInfo: UserInfo, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.padding(
                top = Theme.dimension.size_40dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GlideImage(
                imageModel = userInfo.driverInfo?.photo.orEmpty(),
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
            Column(
                modifier = Modifier
                    .weight(8f)
                    .padding(start = Theme.dimension.size_20dp)
            ) {
                Text(
                    userInfo.name,
                    modifier = Modifier.padding(bottom = Theme.dimension.size_4dp),
                    style = UiFont.poppinsH3Bold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.LastLineBottom
                        )
                    )
                )
                Text(userInfo.phoneNumber)
            }
            GlideImage(
                modifier = Modifier
                    .size(Theme.dimension.size_20dp)
                    .noRippleClickable {
                        onClick()
                    },
                imageModel = R.drawable.ic_edit
            )
        }
    }
}

@Composable
fun AccountSection(navigator: DestinationsNavigator, userInfo: UserInfo) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_32dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        ),
        content = {
            Text("Akun", style = UiFont.poppinsP3SemiBold, modifier = Modifier.fillMaxWidth())
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .noRippleClickable {
                        if (userInfo.isPinAlreadySet) {
                            navigator.navigate(WalletScreenDestination)
                        } else {
                            navigator.navigate(WalletPinOtpValidationScreenDestination(phone = userInfo.phoneNumber))
                        }
                    },
                title = "Kantong & Tarik Penghasilan", icon = R.drawable.teman_wallet_new)
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .noRippleClickable {
                        navigator.navigate(RewardScreenDestination)
                    },
                title = "Reward", icon = R.drawable.ic_giftcard,
                iconColor = UiColor.success500
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .noRippleClickable {
                        navigator.navigate(OrderHistoryScreenDestination)
                    },
                title = "Riwayat",
                icon = R.drawable.ic_order,
                iconColor = UiColor.tertiaryBlue500
            )
        }
    )
}

@Composable
private fun OtherInformationSection(onLogout: () -> Unit, navigator: DestinationsNavigator) {
    val context = LocalContext.current
    var showLogoutSheet by remember { mutableStateOf(false) }
    if (showLogoutSheet) {
        GeneralDialogPrompt(
            title = "Yakin Mau Logout?",
            subtitle = "Kamu nanti harus masukin nomor handphone lagi untuk masuk",
            actionButtons = {
                GeneralActionButton(
                    text = "Ga jadi",
                    textColor = UiColor.neutral900,
                    isFirstAction = true,
                    onClick = {
                        showLogoutSheet = false
                    }
                )
                GeneralActionButton(
                    text = "Logout",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = false,
                    onClick = {
                        showLogoutSheet = false
                        onLogout()
                    }
                )
            },
            onDismissRequest = {
                showLogoutSheet = false
            }
        )
    }
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_32dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        ),
        content = {
            Text(
                "Informasi Lainnya",
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp)
                    .clickable {
                        navigator.navigate(ReferralScreenDestination)
                    },
                title = "Referral",
                icon = R.drawable.referral_icon
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp)
                    .clickable {
                        navigator.navigate(AboutUsScreenDestination)
                    },
                title = "Tentang Kami",
                icon = R.drawable.information
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .clickable {
                        navigator.navigate(WebviewScreenDestination(url = "https://www.temanofficial.co.id/privacy-policy"))
                    },
                title = "Kebijakan Privasi",
                icon = R.drawable.privacy,
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .clickable {
                        context.redirectToPlayStore()
                    },
                title = "Beri Rating",
                icon = R.drawable.rating
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp)
                    .noRippleClickable {
                        showLogoutSheet = true
                    }, title = "Logout",
                circleBackgroundColor = UiColor.primaryRed50, icon = R.drawable.ic_logout
            )
        }
    )
}

@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    circleBackgroundColor: Color = UiColor.neutralGray0,
    @DrawableRes icon: Int,
    iconColor: Color = UiColor.warning500,
    title: String,
) {
    Column {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemanCircleButton(
                icon = icon,
                iconColor = iconColor,
                circleBackgroundColor = circleBackgroundColor,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp),
                iconModifier = Modifier
                    .size(Theme.dimension.size_24dp)
            )
            Text(
                title,
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier
                    .weight(8f)
                    .padding(start = Theme.dimension.size_16dp)
            )

            GlideImage(
                imageModel = R.drawable.ic_arrow_right,
                modifier = Modifier
                    .weight(1f)
                    .width(Theme.dimension.size_6dp)
                    .height(Theme.dimension.size_12dp)
            )
        }
        Divider(
            color = UiColor.neutral100, thickness = 1.dp,
            modifier = Modifier.padding(
                start = Theme.dimension.size_64dp,
                top = Theme.dimension.size_16dp
            )
        )
    }
}