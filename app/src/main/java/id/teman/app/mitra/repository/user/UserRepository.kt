package id.teman.app.mitra.repository.user

import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.remote.user.UserRemoteDataSource
import id.teman.app.mitra.domain.model.registration.CategoriesRestaurantSpec
import id.teman.app.mitra.domain.model.registration.convertToCategoriesRestaurantSpec
import id.teman.app.mitra.domain.model.user.DriverStatus
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.UserInfoTokenUIModel
import id.teman.app.mitra.domain.model.user.toUserInfo
import id.teman.app.mitra.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody


class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val preference: Preference,
    private val json: Json
) {
    suspend fun login(number: String, fcm: String): Flow<UserInfoTokenUIModel> = flow {
        userRemoteDataSource.login(number, fcm)
            .flowOn(Dispatchers.IO)
            .map {
                UserInfoTokenUIModel(it.accessToken.orEmpty())
            }.catch { exception ->
                throw exception
            }.collect {
                preference.setBearerToken(it.token)
                userRemoteDataSource.sendOtp()
                emit(it)
            }
    }

    suspend fun logout(): Flow<String> = userRemoteDataSource.logout()
        .flowOn(Dispatchers.IO)
        .map { it.message.orEmpty() }

    suspend fun verifyOtp(otpCode: String): Flow<UserInfo> = flow {
        userRemoteDataSource.verifyOtp(otpCode)
            .flowOn(Dispatchers.IO)
            .catch { exception ->
                throw exception
            }.collect {
                val userInfo = it.toUserInfo()
                preference.setBearerToken(it.accessToken.orEmpty())
                preference.setRefreshToken(it.refreshToken.orEmpty())
                preference.setUserInfo(json.encodeToString(userInfo))
                emit(userInfo)
            }
    }

    suspend fun getUserProfile(): Flow<UserInfo> = flow {
        userRemoteDataSource.getUserProfile()
            .catch { exception ->
                val userInfoLocalJson = preference.getUserInfo.first()
                if (userInfoLocalJson.isNotNullOrEmpty()) {
                    val userInfo = Json.decodeFromString<UserInfo>(userInfoLocalJson)
                    emit(userInfo)
                } else {
                    throw exception
                }
            }.flowOn(Dispatchers.IO).collect {
                val userInfo = it.toUserInfo()
                preference.setUserInfo(json.encodeToString(userInfo))
                emit(userInfo)
            }
    }

    suspend fun updateUserLocation(latitude: Double, longitude: Double, bearing: Float): Flow<UserInfo> = flow {
        userRemoteDataSource.updateUserLocation(
            longitude = longitude,
            latitude = latitude,
            bearing = bearing
        ).catch { exception ->
            throw exception
        }.flowOn(Dispatchers.IO).collect {
            val userInfoJson =  preference.getUserInfo.first()
            val userInfo = Json.decodeFromString<UserInfo>(userInfoJson)
            val newUserInfo = userInfo.copy(
                driverInfo = userInfo.driverInfo?.copy(
                    currentLatitude = it.lat.orZero(),
                    currentLongitude = it.lng.orZero(),
                )
            )
            preference.setUserInfo(json.encodeToString(newUserInfo))
            emit(newUserInfo)
        }
    }

    suspend fun updateDriverStatus(status: String): Flow<UserInfo> = flow {
        userRemoteDataSource.updateDriverStatus(status)
            .catch { exception ->
                throw exception
            }.flowOn(Dispatchers.IO).collect { userInfoDto ->
                val userInfoJson =  preference.getUserInfo.first()
                val userInfo = Json.decodeFromString<UserInfo>(userInfoJson)
                val newUserInfo = userInfo.copy(
                    driverInfo = userInfo.driverInfo?.copy(
                        status = DriverStatus.from(userInfoDto.status)
                    )
                )
                preference.setUserInfo(json.encodeToString(newUserInfo))
                emit(newUserInfo)
            }
    }

    suspend fun basicPhoneRegistration(phone: String, fcm: String, referral: String? = ""): Flow<UserInfo> = flow {
        userRemoteDataSource.basicPhoneRegistration(phone, fcm, referral = referral.orEmpty())
            .catch { exception -> throw exception }
            .collect { userInfoDto ->
                val userInfo = userInfoDto.toUserInfo()
                preference.setUserInfo(json.encodeToString(userInfo))
                preference.setBearerToken(userInfoDto.accessToken.orEmpty())
                userRemoteDataSource.sendOtp()
                emit(userInfo)
            }
    }

    suspend fun completeDriverRegistration(
        partMap: MutableMap<String, RequestBody>,
        imageList: List<MultipartBody.Part>
    ): Flow<UserInfo> = flow {
        userRemoteDataSource.completeDriverRegistration(
            partMap, imageList
        ).catch { exception ->
            throw exception
        }.collect { userInfoDto ->
            val userInfo = userInfoDto.toUserInfo()
            preference.setUserInfo(json.encodeToString(userInfo))
            emit(userInfo)
        }
    }

    suspend fun completeRestaurantRegistration(
        textFieldPart: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part?,
        ktpPhoto: MultipartBody.Part?,
        bankAccountPhoto: MultipartBody.Part?
    ): Flow<UserInfo> =
        userRemoteDataSource.completeRestaurantRegistration(
            partMap = textFieldPart,
            restaurantPhoto = restaurantPhoto,
            ktpPhoto = ktpPhoto,
            bankAccountPhoto = bankAccountPhoto
        ).map { userInfoDto ->
            val userInfo = userInfoDto.toUserInfo()
            preference.setUserInfo(json.encodeToString(userInfo))
            userInfo
        }

    suspend fun sendOtp(): Flow<Int> =
        userRemoteDataSource.sendOtp().map { it.attempt.orZero() }

    suspend fun updateDriverProfile(
        partMap: MutableMap<String, RequestBody>,
        driverPhoto: MultipartBody.Part? = null
    ): Flow<UserInfo> = flow {
        userRemoteDataSource.updateDriverProfile(partMap, driverPhoto)
            .flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect { userData ->
                val rawJson =  preference.getUserInfo.first()
                var userInfo = json.decodeFromString<UserInfo>(rawJson)
                userInfo = userInfo.copy(
                    name = userData.name.orEmpty(), phoneNumber = userData.phoneNumber.orEmpty(),
                    driverInfo = userInfo.driverInfo?.copy(photo = userData.userPhotoDto?.url.orEmpty())
                )
                preference.setUserInfo(json.encodeToString(userInfo))
                emit(userInfo)
            }
    }

    suspend fun updateRestaurantProfile(
        partMap: MutableMap<String, RequestBody>,
        restaurantPhoto: MultipartBody.Part? = null
    ): Flow<UserInfo> = flow {
        userRemoteDataSource.updateRestaurantProfile(partMap, restaurantPhoto)
            .flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect { userData ->
                val rawJson =  preference.getUserInfo.first()
                var userInfo = json.decodeFromString<UserInfo>(rawJson)
                userInfo = userInfo.copy(
                    name = userData.name.orEmpty(), phoneNumber = userData.phoneNumber.orEmpty(),
                    driverInfo = userInfo.driverInfo?.copy(photo = userData.userPhotoDto?.url.orEmpty())
                )
                preference.setUserInfo(json.encodeToString(userInfo))
                emit(userInfo)
            }
    }

    suspend fun getListCategoriesRestaurant(): Flow<List<CategoriesRestaurantSpec>> = flow {
        userRemoteDataSource.getListCategoriesRestaurant()
            .flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect {
                emit(it.data.convertToCategoriesRestaurantSpec())
            }
    }
}