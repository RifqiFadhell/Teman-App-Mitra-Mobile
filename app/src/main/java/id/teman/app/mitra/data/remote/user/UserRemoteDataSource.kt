package id.teman.app.mitra.data.remote.user

import id.teman.app.mitra.data.dto.BaseResponse
import id.teman.app.mitra.data.dto.restaurant.CategoriesResponseDto
import id.teman.app.mitra.data.dto.user.request.DriverStatusUpdateRequestDto
import id.teman.app.mitra.data.dto.user.request.LocationUpdateRequestDto
import id.teman.app.mitra.data.dto.user.request.LoginRequestDto
import id.teman.app.mitra.data.dto.user.request.OtpRequestDto
import id.teman.app.mitra.data.dto.user.response.BasicUserResponseDto
import id.teman.app.mitra.data.dto.user.response.DriverBasicInfoDto
import id.teman.app.mitra.data.dto.user.response.LoginResponseDto
import id.teman.app.mitra.data.dto.user.response.OtpResponseDto
import id.teman.app.mitra.data.dto.user.response.UserResponseDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRemoteDataSource {
    suspend fun login(number: String, fcm: String): Flow<LoginResponseDto>
    suspend fun logout(): Flow<BaseResponse>
    suspend fun basicPhoneRegistration(number: String, fcm: String, referral: String?): Flow<BasicUserResponseDto>
    suspend fun verifyOtp(code: String): Flow<BasicUserResponseDto>
    suspend fun getUserProfile(): Flow<BasicUserResponseDto>
    suspend fun updateUserLocation(longitude: Double, latitude: Double, bearing: Float): Flow<DriverBasicInfoDto>
    suspend fun updateDriverStatus(status: String): Flow<DriverBasicInfoDto>
    suspend fun completeDriverRegistration(
        partMap: MutableMap<String, RequestBody>,
        imageList: List<MultipartBody.Part>
    ): Flow<BasicUserResponseDto>
    suspend fun getListCategoriesRestaurant(): Flow<CategoriesResponseDto>

    suspend fun completeRestaurantRegistration(
        partMap: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part?,
        ktpPhoto: MultipartBody.Part?,
        bankAccountPhoto: MultipartBody.Part?
    ): Flow<BasicUserResponseDto>

    suspend fun sendOtp(): Flow<OtpResponseDto>
    suspend fun updateDriverProfile(
        partMap: MutableMap<String, RequestBody>,
        driverPhoto: MultipartBody.Part?
    ): Flow<UserResponseDto>

    suspend fun updateRestaurantProfile(
        partMap: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part?
    ): Flow<UserResponseDto>
}

class DefaultUserRemoteDataSource(
    private val httpClient: ApiServiceInterface
) : UserRemoteDataSource {

    override suspend fun login(number: String, fcm: String): Flow<LoginResponseDto> =
        handleRequestOnFlow {
            httpClient.login(LoginRequestDto(number, fcm))
        }

    override suspend fun logout(): Flow<BaseResponse> =
        handleRequestOnFlow {
            httpClient.logoutUser()
        }

    override suspend fun basicPhoneRegistration(number: String, fcm: String, referral: String?): Flow<BasicUserResponseDto> =
        handleRequestOnFlow {
            httpClient.phoneRegistration(LoginRequestDto(number, fcm, referral.orEmpty()))
        }

    override suspend fun verifyOtp(code: String): Flow<BasicUserResponseDto> =
        handleRequestOnFlow {
            httpClient.verifyOtpCode(OtpRequestDto(code))
        }

    override suspend fun getUserProfile(): Flow<BasicUserResponseDto> =
        handleRequestOnFlow {
            httpClient.getUserProfile()
        }

    override suspend fun updateUserLocation(
        longitude: Double,
        latitude: Double,
        bearing: Float
    ): Flow<DriverBasicInfoDto> =
        handleRequestOnFlow {
            httpClient.updateMitraLocation(
                LocationUpdateRequestDto(latitude, longitude, bearing)
            )
        }

    override suspend fun updateDriverStatus(status: String): Flow<DriverBasicInfoDto> =
        handleRequestOnFlow {
            httpClient.updateDriverStatus(DriverStatusUpdateRequestDto(status))
        }

    override suspend fun completeDriverRegistration(
        partMap: MutableMap<String, RequestBody>,
        imageList: List<MultipartBody.Part>
    ): Flow<BasicUserResponseDto> {
        val profile = imageList.getOrNull(0)
        val ktp = imageList.getOrNull(1)
        val sim = imageList.getOrNull(2)
        val skck = imageList.getOrNull(3)
        val stnk = imageList.getOrNull(4)
        val vehiclePhoto = imageList.getOrNull(5)
        return handleRequestOnFlow {
            httpClient.completeProfileRegistration(
                partMap,
                profileImageFile = profile,
                ktpImageFile = ktp,
                simImageFile = sim,
                skckImageFile = skck,
                stnkImageFile = stnk,
                vehicleImageFile = vehiclePhoto
            )
        }
    }

    override suspend fun completeRestaurantRegistration(
        partMap: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part?,
        ktpPhoto: MultipartBody.Part?,
        bankAccountPhoto: MultipartBody.Part?
    ): Flow<BasicUserResponseDto> = handleRequestOnFlow {
        httpClient.completeRestaurantRegistration(
            partMap = partMap,
            restaurantPhoto = restaurantPhoto,
            ktpPhoto = ktpPhoto,
            bankAccountPhoto = bankAccountPhoto
        )
    }

    override suspend fun sendOtp(): Flow<OtpResponseDto> =
        handleRequestOnFlow { httpClient.sendOtp() }

    override suspend fun updateDriverProfile(
        partMap: MutableMap<String, RequestBody>,
        driverPhoto: MultipartBody.Part?
    ): Flow<UserResponseDto> =
        handleRequestOnFlow {
            httpClient.updateDriverProfile(partMap, driverPhoto)
        }

    override suspend fun updateRestaurantProfile(
        partMap: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part?
    ): Flow<UserResponseDto> = handleRequestOnFlow {
        httpClient.updateRestaurantProfile(partMap, restaurantPhoto)
    }

    override suspend fun getListCategoriesRestaurant(): Flow<CategoriesResponseDto> =
        handleRequestOnFlow {
            httpClient.getListCategoriesRestaurant()
        }
}