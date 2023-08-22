package id.teman.app.mitra.data.remote

import id.teman.app.mitra.data.dto.user.request.RefreshTokenRequestDto
import id.teman.app.mitra.data.dto.user.response.RefreshTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshServiceInterface {

    @POST("auth/refresh_token")
    suspend fun refreshToken(
        @Body token: RefreshTokenRequestDto
    ): RefreshTokenResponseDto
}