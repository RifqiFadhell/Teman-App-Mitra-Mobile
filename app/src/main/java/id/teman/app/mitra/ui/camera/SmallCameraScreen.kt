package id.teman.app.mitra.ui.camera

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.TransparentClipLayout
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlin.math.roundToInt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun SmallCameraScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<String>,
    cameraSpec: CameraSpec,
    viewModel: CameraViewModel = hiltViewModel()
) {

    val permission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        listOf(Manifest.permission.CAMERA)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permission)

    if (!permissionState.allPermissionsGranted) {
        SideEffect {
            permissionState.launchMultiplePermissionRequest()
        }
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val previewView = remember { PreviewView(context) }
    val previewHeight = configuration.screenHeightDp * cameraSpec.cameraCrop.second
    val previewWidth = configuration.screenHeightDp * cameraSpec.cameraCrop.first

    Column {
        if (permissionState.allPermissionsGranted) {
            if (viewModel.showPhoto.value != Uri.EMPTY) {
                ImagePreview(
                    uri = viewModel.showPhoto.value,
                    title = cameraSpec.title,
                    onContinue = {
                        resultNavigator.navigateBack(
                            Json.encodeToString(
                                CameraResult(
                                    cameraType = cameraSpec.cameraType,
                                    uri = viewModel.showPhoto.value.toString()
                                )
                            )
                        )
                    },
                    onRetakePhoto = {
                        viewModel.showPhoto.value = Uri.EMPTY
                    },
                    onCLoseCameraClick = {
                        navigator.popBackStack()
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = UiColor.black)
                ) {
                    AndroidView(
                        factory = {
                            viewModel.showCameraPreview(previewView, lifecycleOwner, previewHeight.roundToInt(), previewWidth.roundToInt())
                            previewView
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(screenWidth * cameraSpec.cameraCrop.first)
                            .height(screenHeight * cameraSpec.cameraCrop.second)
                    )
                    TransparentClipLayout(
                        modifier = Modifier.fillMaxSize(),
                        width = screenWidth * cameraSpec.cameraCrop.first,
                        height = screenHeight * cameraSpec.cameraCrop.second,
                        offsetY = screenHeight * 0.35f
                    )

                    Column(
                        modifier = Modifier.padding(Theme.dimension.size_16dp)
                    ) {
                        Icon(
                            Icons.Default.Close, "close icon", tint = UiColor.white,
                            modifier = Modifier
                                .size(Theme.dimension.size_24dp)
                                .noRippleClickable {
                                    navigator.popBackStack()
                                }
                        )
                        Spacer(modifier = Modifier.height(Theme.dimension.size_18dp))
                        Text(
                            stringResource(id = R.string.take_camera_photo_title, cameraSpec.title),
                            style = UiFont.poppinsH4SemiBold.copy(color = UiColor.white)
                        )
                        Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                        Text(
                            stringResource(
                                id = R.string.take_camera_photo_subtitle,
                                cameraSpec.title
                            ),
                            style = UiFont.poppinsP1Medium.copy(color = UiColor.white)
                        )
                    }

                    GlideImage(
                        imageModel = R.drawable.ic_take_picture,
                        modifier = Modifier
                            .padding(bottom = Theme.dimension.size_24dp)
                            .align(Alignment.BottomCenter)
                            .size(Theme.dimension.size_60dp)
                            .noRippleClickable {
                                if (permissionState.allPermissionsGranted) {
                                    viewModel.captureAndSave(context)
                                }
                            }
                    )

                    Spacer(modifier = Modifier.height(Theme.dimension.size_40dp))
                }
            }
        }
    }
}

@Composable
private fun ImagePreview(
    uri: Uri,
    title: String,
    onRetakePhoto: () -> Unit,
    onContinue: () -> Unit,
    onCLoseCameraClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = UiColor.black)
            .padding(horizontal = Theme.dimension.size_16dp)
    ) {
        GlideImage(
            imageModel = uri,
            modifier = Modifier
                .align(Alignment.Center)
                .width(screenWidth * 0.9f)
                .height(screenHeight * 0.3f),
        )
        Column(modifier = Modifier.padding(top = Theme.dimension.size_16dp)) {
            Icon(
                Icons.Default.Close, "close icon", tint = UiColor.white,
                modifier = Modifier
                    .size(Theme.dimension.size_24dp)
                    .noRippleClickable { onCLoseCameraClick() }
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_18dp))
            Text(
                stringResource(id = R.string.take_camera_photo_title, title),
                style = UiFont.poppinsH4SemiBold.copy(color = UiColor.white)
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = Theme.dimension.size_24dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
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
                    color = UiColor.neutral50,
                    width = Theme.dimension.size_1dp
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = Theme.dimension.size_0dp
                ),
                content = {
                    Text(
                        "Ulang",
                        style = UiFont.poppinsP1SemiBold.copy(color = UiColor.neutral50),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    onRetakePhoto()
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
                onContinue()
            }
        }
    }
}