package id.teman.app.mitra.ui.registration.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.createMultipartImageFromUri
import id.teman.app.mitra.common.createPartFromString
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.domain.model.registration.BankInformationSpec
import id.teman.app.mitra.domain.model.registration.CategoriesRestaurantSpec
import id.teman.app.mitra.domain.model.registration.OutletInformationSpec
import id.teman.app.mitra.domain.model.registration.OwnerIdentitySpec
import id.teman.app.mitra.domain.model.registration.RegistrationFoodSpec
import id.teman.app.mitra.domain.model.wallet.ItemBankSpec
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.user.UserRepository
import id.teman.app.mitra.repository.wallet.WalletRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

@HiltViewModel
class RegistrationFoodViewModel @Inject constructor(
    private val preference: Preference,
    private val json: Json,
    private val userRepository: UserRepository,
    private val application: Application,
    private val walletRepository: WalletRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegistrationFoodUiState())
        private set

    init {
        val rawJson = runBlocking {  preference.getRegistrationFoodInfo.first() }
        if (rawJson.isNotNullOrEmpty()) {
            val foodInfoSpec = json.decodeFromString<RegistrationFoodSpec>(rawJson)
            uiState = uiState.copy(
                ownerIdentity = foodInfoSpec.ownerIdentity,
                bankInformation = foodInfoSpec.bankInformation,
                businessStoreName = foodInfoSpec.businessStoreName,
                outletInformation = foodInfoSpec.outletInformation
            )
        }
        viewModelScope.launch {
            delay(200)
            getListBank()
            getListCategoriesRestaurant()
        }
    }

    fun setBusinessStoreName(storeName: String) {
        uiState = uiState.copy(businessStoreName = storeName)
        saveIntoLocalDb()
    }

    fun setCategoryRestaurant(category: String) {
        uiState = uiState.copy(businessCategory = category)
        saveIntoLocalDb()
    }

    fun getListBank() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            walletRepository.getListBank()
                .catch { exception ->
                    uiState = uiState.copy(loading = false, generalError = Event(exception.message ?: "Terjadi Kesalahan"))
                }
                .collect {
                    uiState = uiState.copy(loading = false, listBank = it)
                }
        }
    }

    fun getListCategoriesRestaurant() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            userRepository.getListCategoriesRestaurant()
                .catch { exception ->
                    uiState = uiState.copy(loading = false, generalError = Event(exception.message ?: "Terjadi Kesalahan"))
                }
                .collect {
                    uiState = uiState.copy(loading = false, listCategories = it)
                }
        }
    }

    fun setOwnerIdentity(ktpNumber: String, ktpName: String, ktpImage: Uri) {
        uiState = uiState.copy(
            ownerIdentity = OwnerIdentitySpec(
                ktpImage = ktpImage,
                ktpName = ktpName,
                ktpNumber = ktpNumber
            )
        )
        saveIntoLocalDb()
    }

    fun setBankInformation(
        bankName: String,
        bankAccountNumber: String,
        bankOwnerName: String,
        bankAccountBookImage: Uri
    ) {
        uiState = uiState.copy(
            bankInformation = BankInformationSpec(
                bookImage = bankAccountBookImage,
                accountNumber = bankAccountNumber,
                name = bankName,
                ownerName = bankOwnerName,
            )
        )
        saveIntoLocalDb()
    }

    fun setOutletInformation(
        postalCode: String,
        outletPhoneNumber: String,
        outletCompleteAddress: String,
        outletLatitude: Double,
        outletLongitude: Double,
        outletOptionalAddress: String,
        outletPhoto: Uri,
        outletNearbyHint: String
    ) {
        uiState = uiState.copy(
            outletInformation = OutletInformationSpec(
                postalCode = postalCode,
                outletPhoneNumber = outletPhoneNumber,
                outletCompleteAddress = outletCompleteAddress,
                outletLatitude = outletLatitude,
                outletLongitude = outletLongitude,
                outletOptionalAddress = outletOptionalAddress,
                outletPhoto = outletPhoto,
                outletNearbyHint = outletNearbyHint
            )
        )
        saveIntoLocalDb()
    }

    private fun saveIntoLocalDb() {
        val registrationFoodSpec = RegistrationFoodSpec(
            ownerIdentity = uiState.ownerIdentity,
            bankInformation = uiState.bankInformation,
            businessStoreName = uiState.businessStoreName,
            outletInformation = uiState.outletInformation,
            businessCategory = uiState.businessCategory
        )
        val convertToRawJson = json.encodeToString(registrationFoodSpec)
        viewModelScope.launch {
            preference.setRegistrationFoodInfo(convertToRawJson)
        }
    }

    fun setRestaurantType(type: String) {
        uiState = uiState.copy(restaurantType = type)
    }

    fun isAllFieldFilled(): Boolean =
        uiState.ownerIdentity != null && uiState.businessStoreName != null
                && uiState.bankInformation != null && uiState.outletInformation != null

    fun registerFoodUser() = viewModelScope.launch(Dispatchers.IO) {
        uiState = uiState.copy(loading = true)
        userRepository.completeRestaurantRegistration(
            textFieldPart = getRegistrationTextFieldValues(),
            restaurantPhoto = uiState.outletInformation?.let { outlet ->
                createMultipartImageFromUri(application, outlet.outletPhoto, "restaurant_photo", )
            },
            ktpPhoto = uiState.ownerIdentity?.let { owner ->
                createMultipartImageFromUri(application, owner.ktpImage, "ktp")
            },
            bankAccountPhoto = uiState.bankInformation?.let { bank ->
                createMultipartImageFromUri(application, bank.bookImage, "bank_account_photo")
            }
        ).catch { exception ->
            uiState = uiState.copy(
                loading = false,
                registerFoodError = Event(exception.message.orEmpty())
            )
        }.collect {
            preference.setRegistrationFoodInfo("")
            uiState = uiState.copy(loading = false, registerFoodSuccess = Event(Unit))
        }
    }

    private fun getRegistrationTextFieldValues(): MutableMap<String, RequestBody> {
        val map: MutableMap<String, RequestBody> = mutableMapOf()
        uiState.ownerIdentity?.let { owner ->
            map["name"] = createPartFromString(uiState.fullName)
            map["email"] = createPartFromString(uiState.email)
            map["id_card_number"] = createPartFromString(owner.ktpNumber)
            map["id_card_full_name"] = createPartFromString(owner.ktpName)
        }
        uiState.bankInformation?.let { bank ->
            map["bank_name"] = createPartFromString(bank.name)
            map["bank_account_name"] = createPartFromString(bank.ownerName)
            map["bank_account_number"] = createPartFromString(bank.accountNumber)
        }
        uiState.businessStoreName?.let { storeName ->
            map["restaurant_name"] = createPartFromString(storeName)
        }
        uiState.outletInformation?.let { outlet ->
            map["postal_code"] = createPartFromString(outlet.postalCode)
            map["address"] = createPartFromString(outlet.outletCompleteAddress)
            map["optional_address"] = createPartFromString(outlet.outletNearbyHint)
            map["lat"] = createPartFromString("${outlet.outletLatitude}")
            map["lng"] = createPartFromString("${outlet.outletLongitude}")
            map["description"] = createPartFromString(outlet.outletOptionalAddress)
            map["phone_number"] = createPartFromString(outlet.outletPhoneNumber)
        }
        uiState.restaurantType?.let { map["type"] = createPartFromString(it) }
        uiState.businessCategory?.let { map["categories"] = createPartFromString(it) }
        return map
    }

    fun setFullNameAndEmail(email: String, fullName: String) {
        uiState = uiState.copy(email = email, fullName = fullName)
    }
}

data class RegistrationFoodUiState(
    val loading: Boolean = false,
    val ownerIdentity: OwnerIdentitySpec? = null,
    val bankInformation: BankInformationSpec? = null,
    val businessStoreName: String? = null,
    val businessCategory: String? = null,
    val outletInformation: OutletInformationSpec? = null,
    val restaurantType: String? = null,
    val registerFoodError: Event<String>? = null,
    val registerFoodSuccess: Event<Unit>? = null,
    val email: String = "",
    val fullName: String = "",
    val listBank: List<ItemBankSpec> = emptyList(),
    val listCategories: List<CategoriesRestaurantSpec> = emptyList(),
    val generalError: Event<String>? = null
)