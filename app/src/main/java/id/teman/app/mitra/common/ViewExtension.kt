package id.teman.app.mitra.common

import android.Manifest
import android.app.Application
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import id.teman.app.mitra.ui.camera.getUriPath
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.*

@Composable
fun Int.convertToDp(): Dp {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp <= 360) {
        (this * .875).dp
    } else {
        this.dp
    }
}

@Composable
fun Int.convertToSp(): TextUnit {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp <= 360) {
        (this * .875).sp
    } else {
        this.sp
    }
}

fun Boolean?.orFalse(): Boolean = this ?: false

fun Int?.orZero(): Int = this ?: 0

fun Double?.orZero(): Double = this ?: 0.0

fun String?.isNotNullOrEmpty(): Boolean = this != null && this.isNotEmpty()

fun Context.getActivity(): ComponentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is AppCompatActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun createPartFromString(stringData: String): RequestBody {
    return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
}

suspend fun createMultipartImageFromUri(
    context: Context,
    uri: Uri,
    key: String,
    resolution: Pair<Int, Int> = Pair(256, 256)
): MultipartBody.Part? {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        if (uri.path == null) return null
        val file = File(getUriPath(context, uri))
        return if (file.exists()) {
            val compressedImageFile = Compressor.compress(context, file) {
                quality(100)
                resolution(resolution.first, resolution.second)
                format(Bitmap.CompressFormat.JPEG)
            }
            val requestFile = compressedImageFile.asRequestBody("image/jpg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(key, file.name, requestFile)
        } else {
            null
        }
    } else {
        val file = copyFileToInternalStorage(context, uri)
        return if (file != null && file.exists()) {
            val compressedImageFile = Compressor.compress(context, file) {
                quality(100)
                resolution(resolution.first, resolution.second)
                format(Bitmap.CompressFormat.JPEG)
            }
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", compressedImageFile)
            val requestFile = context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
            }
            requestFile?.let {
                MultipartBody.Part.createFormData(key, file.name, it)
            }
        } else {
            null
        }
    }
}

fun createMultipartImageFromUriGallery(application: Application, uri: Uri, key: String, uriPath: String): MultipartBody.Part? {
    if (uri.path == null) return null
    val file = File(uriPath.ifEmpty { getPath(application, uri) })
    val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        key, file.name,
        requestFile
    )
}
private fun getPath(context: Context, uri: Uri): String {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        context.contentResolver.query(uri, projection, null, null, null) ?: return ""
    val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(columnIndex)
    cursor.close()
    return s
}

fun String.parseBold(): AnnotatedString {
    val parts = this.split("<b>", "</b>")
    return buildAnnotatedString {
        var bold = false
        for (part in parts) {
            if (bold) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
            bold = !bold
        }
    }
}

fun Double.convertToKilometre(): String {
    if (this < 1000) return "$this m"
    val result = (this / 1000f).toFloat()
    return "${String.format("%.2f", result)} Km"
}

fun Long.convertToKilometre(): String {
    if (this < 1000) return "$this m"
    val result = (this / 1000f).toFloat()
    return "${String.format("%.2f", result)} Km"
}

fun Double.convertToRupiah(): String {
    val rupiahFormat = DecimalFormat.getCurrencyInstance(Locale("in", "ID"))
    rupiahFormat.minimumFractionDigits = 0
    return rupiahFormat.format(this)
}

fun String.convertToAllowedIndonesianNumber(): String {
    val indoPrefixNumber = this.take(2)
    return if (indoPrefixNumber.contains("62")) {
        this
    } else if (this.first().toString() == "0") {
        this.replaceRange(IntRange(0, 0), "+62")
    } else {
        "62$this"
    }
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.redirectToPlayStore() {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

private fun getFileFromUri(context: Context, uri: Uri): File? {
    var filePath: String? = null
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            filePath = getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        filePath = getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        filePath = uri.path
    }
    return filePath?.let { File(it) }
}

private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    uri ?: return null
    val column = "_data"
    val projection = arrayOf(column)
    context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    }
    return null
}

private fun copyFileToInternalStorage(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val copiedFile = File(context.filesDir, "temp_image.jpg")
    val outputStream = FileOutputStream(copiedFile)
    val buffer = ByteArray(4 * 1024) // 4 KB buffer

    try {
        while (true) {
            val bytesRead = inputStream.read(buffer)
            if (bytesRead == -1) {
                break // Reached end of stream
            }
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        return copiedFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        inputStream.close()
        outputStream.close()
    }
}