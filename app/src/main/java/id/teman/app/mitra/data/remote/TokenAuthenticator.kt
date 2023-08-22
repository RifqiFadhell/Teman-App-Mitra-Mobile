package id.teman.app.mitra.data.remote

import id.teman.app.mitra.data.dto.user.request.RefreshTokenRequestDto
import id.teman.app.mitra.device.DeviceInformation
import id.teman.app.mitra.manager.UserManager
import id.teman.app.mitra.manager.UserState
import id.teman.app.mitra.preference.Preference
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val httpApiClient: RefreshServiceInterface,
    private val preference: Preference,
    private val userManager: UserManager,
    private val deviceInformation: DeviceInformation
): Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request {
        var request: Request = response.request

        try {
            runBlocking(Dispatchers.Main) {
                val refreshToken = preference.getRefreshToken.first()
                if (refreshToken.isBlank()) {
                    userManager.changeUserState(UserState.Revoked)
                } else {
                    val result = httpApiClient.refreshToken(
                        RefreshTokenRequestDto(refreshToken)
                    )

                    if (result.accessToken == null) {
                        throw IOException()
                    } else {
                        preference.setBearerToken(result.accessToken)
                        preference.setRefreshToken(result.refreshToken.orEmpty())

                        request = response.request.newBuilder()
                            .header("Authorization", "Bearer ${result.accessToken}")
                            .addHeader("x-device-id", deviceInformation.deviceId)
                            .addHeader("x-device-name", deviceInformation.deviceName)
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            runBlocking {
                preference.setRefreshToken("")
                preference.setUserInfo("")
                userManager.changeUserState(UserState.Revoked)
            }
        }

        return request
    }
}

val Response.responseCount: Int
    get() = generateSequence(this) { it.priorResponse }.count()