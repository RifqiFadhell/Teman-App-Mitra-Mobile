package id.teman.app.mitra.ui.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.camera.CustomCamera
import java.text.SimpleDateFormat
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val camera: CustomCamera
) : ViewModel() {

    var showPhoto: MutableState<Uri> = mutableStateOf(Uri.EMPTY)

    fun showCameraPreview(preview: PreviewView, lifecycleOwner: LifecycleOwner, height: Int, width: Int) {
        viewModelScope.launch {
            camera.showCameraPreview(preview, lifecycleOwner, height, width)
        }
    }

    fun captureAndSave(context: Context) {
        viewModelScope.launch {
            camera.captureAndSaveImage(context)
                .catch { exception ->

                }
                .collect { uri ->
                    showPhoto.value = uri
                }
        }
    }

    fun takePhoto(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor
    ) {
        val name  = SimpleDateFormat("yyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis())

        // for storing
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT > 28){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/TemanApp")
            }
        }

        // for capture output
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                print(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { uri ->
                    showPhoto.value = uri
                }
            }
        })
    }
}