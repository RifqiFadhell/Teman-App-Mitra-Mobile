package id.teman.app.mitra.ui.registration.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.createMultipartImageFromUri
import id.teman.app.mitra.common.createPartFromString
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.UserKycStatus
import id.teman.app.mitra.repository.user.UserRepository
import id.teman.app.mitra.ui.registration.VerifyProcess
import id.teman.app.mitra.ui.registration.uimodel.PartnerType
import id.teman.app.mitra.ui.registration.uimodel.T_CAR_TITLE
import id.teman.app.mitra.ui.registration.uimodel.UserBasicRegistrationRequestUiModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiEvent())
        private set

    fun uploadCompleteRegistrationData(
        ktpImage: Uri, simImage: Uri, stnkImage: Uri, skckImage: Uri,
        profileImage: Uri, vehicleImage: Uri, ktpValue: String,
        simValue: String, brand: String, year: String,
        type: String, fuel: String, platNumber: String,city: String,
        basicInfo: UserBasicRegistrationRequestUiModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            val multipartBodyList: ArrayList<MultipartBody.Part> = arrayListOf()
            map["name"] = createPartFromString(basicInfo.fullName)
            map["email"] = createPartFromString(basicInfo.email)
            map["type"] = createPartFromString(getDriverType(basicInfo.partnerType))
            map["city"] = createPartFromString(city)
            map["id_card_number"] = createPartFromString(ktpValue)
            map["driver_license_number"] = createPartFromString(simValue)
            map["vehicle_brand"] = createPartFromString(brand)
            map["vehicle_type"] = createPartFromString(type)
            map["vehicle_year"] = createPartFromString(year)
            map["vehicle_number"] = createPartFromString(platNumber)
            map["vehicle_fuel"] = createPartFromString(fuel)

            createMultipartImageFromUri(application, profileImage, "profile_photo")?.let {
                multipartBodyList.add(it)
            }
            createMultipartImageFromUri(application, ktpImage, "ktp")?.let {
                multipartBodyList.add(
                    it
                )
            }
            createMultipartImageFromUri(application, simImage, "sim")?.let {
                multipartBodyList.add(
                    it
                )
            }
            createMultipartImageFromUri(
                application,
                skckImage,
                "skck"
            )?.let { multipartBodyList.add(it) }
            createMultipartImageFromUri(
                application,
                stnkImage,
                "stnk"
            )?.let { multipartBodyList.add(it) }
            createMultipartImageFromUri(application, vehicleImage, "vehicle_photo")?.let {
                multipartBodyList.add(
                    it
                )
            }
            userRepository.completeDriverRegistration(
                partMap = map,
                imageList = multipartBodyList
            ).catch { exception ->
                uiState = uiState.copy(
                    loading = false,
                    registrationError = Event(exception.message.orEmpty())
                )
            }.collect {
                uiState = uiState.copy(loading = false, registrationSuccess = Event(Unit))
            }
        }
    }

    private fun getDriverType(title: String): String {
        return when (title) {
            T_CAR_TITLE -> DriverMitraType.CAR.type
            else -> DriverMitraType.BIKE.type
        }
    }

    fun registerPhoneNumber(phoneNumber: String, referral: String? = "") {
        uiState = uiState.copy(loading = true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                val rootCauseMessage = when (task.exception) {
                    is java.io.IOException -> "Tidak di temukan internet. Silahkan coba lagi"
                    else -> task.exception?.message ?: "Gagal mendapatkan token"
                }
                uiState = uiState.copy(
                    loading = false,
                    registrationError = Event(rootCauseMessage)
                )
                return@OnCompleteListener
            }
            val token = task.result
            viewModelScope.launch {
                delay(1000)
                val allowedPhoneNumber = phoneNumber.toAllowedPhoneNumber()
                userRepository.basicPhoneRegistration(allowedPhoneNumber, token.orEmpty(), referral.orEmpty())
                    .catch { exception ->
                        uiState = uiState.copy(
                            loading = false,
                            registrationError = Event(exception.message.orEmpty())
                        )
                    }.collect {
                        uiState = uiState.copy(loading = false, registrationSuccess = Event(Unit))
                    }
            }
        })
    }

    private fun String.toAllowedPhoneNumber(): String {
        val indoPrefixNumber = this.take(2)
        return if (indoPrefixNumber.contains("62")) {
            this
        } else if (this.first().toString() == "0") {
            this.replaceRange(IntRange(0, 0), "+62")
        } else {
            "62$this"
        }
    }

    fun initMitraPreference() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            userRepository.getUserProfile()
                .catch { exception ->
                    uiState = uiState.copy(loading = false, registrationError = Event(
                        exception.message ?: "Terjadi Kesalahan"
                    ))
                }.collect { userInfo ->
                    uiState = uiState.copy(
                        loading = false,
                        mitraList = listOf(
                            MitraTypeSelectionSpec(
                                partnerType = PartnerType.PartnerBike,
                                verifyState = isDriverWithVerificationProcess(
                                    userInfo,
                                    DriverMitraType.BIKE
                                )
                            ),
                            MitraTypeSelectionSpec(
                                partnerType = PartnerType.PartnerCar,
                                verifyState = isDriverWithVerificationProcess(
                                    userInfo,
                                    DriverMitraType.CAR
                                )
                            ),
                            MitraTypeSelectionSpec(
                                partnerType = PartnerType.PartnerFood,
                                verifyState = getRestaurantVerifyStatus(userInfo)
                            )
                        )
                    )
                }
        }
    }

    private fun getVerifyStatus(userInfo: UserInfo): VerifyProcess {
        return when (userInfo.userKycStatus) {
            UserKycStatus.UNPROCESSED -> VerifyProcess.DEFAULT
            UserKycStatus.REQUESTING -> VerifyProcess.VERIFYING
            UserKycStatus.REJECTED -> VerifyProcess.DEFAULT
            UserKycStatus.APPROVED -> VerifyProcess.VERIFIED
        }
    }

    private fun isDriverWithVerificationProcess(
        userInfo: UserInfo,
        driverMitraType: DriverMitraType
    ): VerifyProcess {
        return if (userInfo.driverInfo != null) {
            if (driverMitraType == userInfo.driverInfo.mitraType) {
                getVerifyStatus(userInfo)
            } else {
                VerifyProcess.DEFAULT
            }
        } else {
            VerifyProcess.DEFAULT
        }
    }

    private fun getRestaurantVerifyStatus(userInfo: UserInfo): VerifyProcess {
        return if (userInfo.restaurantInfo != null) {
            getVerifyStatus(userInfo)
        } else {
            VerifyProcess.DEFAULT
        }
    }

}

data class RegisterUiEvent(
    val loading: Boolean = false,
    val registrationSuccess: Event<Unit>? = null,
    val registrationError: Event<String>? = null,
    val mitraList: List<MitraTypeSelectionSpec> = listOf()
)

data class MitraTypeSelectionSpec(
    val verifyState: VerifyProcess = VerifyProcess.DEFAULT,
    val partnerType: PartnerType
)