package id.teman.app.mitra.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.MainActivity
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonState
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.backgroundColorState
import id.teman.app.mitra.ui.MainViewModel
import id.teman.app.mitra.ui.PermissionState
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalLifecycleComposeApi::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class
)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://mitra.com/location")
    ]
)
@Composable
fun LocationPermissionUI(navigator: DestinationsNavigator, mainViewModel: MainViewModel) {
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val context = LocalContext.current as Activity
    val uiState: PermissionState by mainViewModel.uiState.collectAsStateWithLifecycle()

    val permission =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    val locationPermissionState = rememberMultiplePermissionsState(permissions = permission)

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        (context as MainActivity).startLocationUpdates(MainActivity.navigationLocationRequest)
    }

    when (uiState) {
        is PermissionState.LocationActive -> {
            navigator.popBackStack()
        }
        else -> {}
    }

    BackHandler {
        /* no-op */
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        content = {
            Scaffold(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            GlideImage(
                                imageModel = R.drawable.ic_location_permission_bg,
                                modifier = Modifier.aspectRatio(1f)
                            )

                            GlideImage(
                                imageModel = R.drawable.ic_location_permission_house,
                                modifier = Modifier
                                    .padding(
                                        horizontal = Theme.dimension.size_30dp
                                    )
                                    .align(Alignment.BottomCenter)
                                    .width((screenWidth * 0.8).dp)
                                    .height((screenHeight * 0.3).dp)

                            )
                        }
                        Spacer(modifier = Modifier.height(Theme.dimension.size_80dp))
                        Text(
                            "Izinkan akses ke lokasi",
                            style = UiFont.poppinsH3Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
                        )
                        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                        Text(
                            caption, style = UiFont.poppinsSubHMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
                        )
                    }
                },
                bottomBar = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TemanFilledButton(
                            activeColor = UiColor.primaryRed500,
                            activeTextColor = Color.White,
                            content = "Lanjutkan",
                            buttonType = ButtonType.Large,
                            borderRadius = Theme.dimension.size_30dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Theme.dimension.size_16dp,
                                    vertical = Theme.dimension.size_40dp
                                ),
                            onClicked = {
                                if (!locationPermissionState.shouldShowRationale) {
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                } else {
                                    (context as MainActivity).startLocationUpdates(MainActivity.navigationLocationRequest)
                                }
                            }
                        )
                    }
                }
            )
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
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                Text(
                    "Aplikasi ini memerlukan lokasi untuk berjalan dengan lancar. Silahkan memberikan" +
                            "izin untuk akses lokasi dan gps", style =
                    UiFont.poppinsP1SemiBold
                )
                LocationPermissionBottomUI(
                    onAccept = {
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    },
                    onDismiss = {
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                    }
                )
            }
        },
    )
}

@Composable
private fun LocationPermissionBottomUI(onAccept: () -> Unit, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_16dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
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
                onDismiss()
            }
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        Button(
            modifier = Modifier.weight(1f),
            elevation = null,
            shape = RoundedCornerShape(Theme.dimension.size_30dp),
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColorState(
                    activeColor = UiColor.primaryRed500,
                    buttonState = ButtonState.Active
                ),
                disabledBackgroundColor = backgroundColorState(
                    UiColor.primaryRed500,
                    buttonState = ButtonState.Disabled
                ),
                disabledContentColor = Color.Gray
            ),
            content = {
                Text(
                    "Iya",
                    style = UiFont.poppinsP1SemiBold.copy(color = UiColor.white),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = {
                onAccept()
            }
        )

    }
}

val caption =
    "Pencarian alamat akan menjadi lebih tepat. Fitur ini dapat menghemat waktu dan mempercepat pemenuhan order"
