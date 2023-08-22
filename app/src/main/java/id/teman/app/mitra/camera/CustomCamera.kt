package id.teman.app.mitra.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Rational
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CustomCamera {

    suspend fun captureAndSaveImage(context: Context): Flow<Uri>
    suspend fun showCameraPreview(previewView: PreviewView, lifecycleOwner: LifecycleOwner, height:Int, width: Int)
}

class DefaultCustomCamera @Inject constructor(
    private val cameraProvider: ProcessCameraProvider,
    private val selector: CameraSelector,
    private val preview: Preview,
    private val imageCapture: ImageCapture
) : CustomCamera {
    override suspend fun captureAndSaveImage(context: Context): Flow<Uri> = flow {
        val name  = SimpleDateFormat("yyy-MM-dd-HH-mm-ss-SSS").format(System.currentTimeMillis())

        // for storing
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT > 28){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/My-Camera-App-Images")
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

        val call = suspendCoroutine { continuation ->
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        outputFileResults.savedUri?.let {
                            continuation.resume(it)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resume(exception)
                    }
                }
            )
        }

        if (call is Uri) {
            emit(call)
        } else {
            throw (call as ImageCaptureException)
        }

    }

    override suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        height: Int,
        width: Int
    ) {
        val viewPort = ViewPort.Builder(Rational(
            width, height
        ), preview.targetRotation).build()

        preview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                useCaseGroup
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}