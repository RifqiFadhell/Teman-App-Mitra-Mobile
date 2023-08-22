package id.teman.app.mitra.data.remote

import id.teman.app.mitra.device.DeviceInformation
import id.teman.app.mitra.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor @Inject constructor(
    private val deviceInformation: DeviceInformation,
    private val preference: Preference
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val bearerToken = runBlocking { preference.getBearerToken.first() }
        val requestBuilder = originalRequest.newBuilder()
            .header("x-device-id", deviceInformation.deviceId)
            .header("x-device-name", deviceInformation.deviceName)
            .header("Authorization", "Bearer $bearerToken")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}