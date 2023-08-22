package id.teman.app.mitra.data.remote

import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.teman.app.mitra.data.dto.BaseResponse
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import retrofit2.HttpException

fun <T : Any> handleRequestOnFlow(
    requestFunc: suspend () -> T
): Flow<T> {
    return flow {
        try {
            emit(requestFunc.invoke())
        } catch (ex: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(ex)
               val exception = when (ex) {
               is HttpException -> {
                   val errorResponse = JSONObject(ex.response()?.errorBody()?.string().orEmpty())
                   val convertToErrorMessage = try {
                       Json.decodeFromString<BaseResponse>(errorResponse.toString())
                   } catch (e: Exception) {
                       null
                   }
                   ApiException(convertToErrorMessage?.message.orEmpty(), ex.response()?.code())
               }
               is UnknownHostException,
               is SSLException -> {
                   ApiException("Tidak ditemukan Internet. Silahkan coba lagi beberapa saat")
               }
               else -> {
                   ApiException("Terjadi Kesalahan")
               }
           }
            throw exception
        }
    }.flowOn(Dispatchers.IO)
}

open class ApiException(message: String, val code: Int? = null): Exception(message)
