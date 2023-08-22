package id.teman.app.mitra.ui.myaccount.referral

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.referral.ItemReferral
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.referral.ReferralRepository
import id.teman.app.mitra.ui.myaccount.referral.ReferralCodeConstant.DOMAIN_PREFIX_DEEP_LINK
import id.teman.app.mitra.ui.myaccount.referral.ReferralCodeConstant.URL_DEEP_LINK
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val application: Application,
    private val referralRepository: ReferralRepository,
    private val json: Json,
    private val preference: Preference
) : ViewModel() {

    var uiState by mutableStateOf(ReferralUiState())
        private set

    fun getHistoryReferral() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            referralRepository.getHistoryReferral().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    listReferral = it
                )
            }
        }
    }

    fun generateDynamicLink() {
        Firebase.dynamicLinks.shortLinkAsync {
            link =
                Uri.parse(URL_DEEP_LINK + "?referral=${getUserProfile()?.referralCode.orEmpty()}")
            domainUriPrefix = DOMAIN_PREFIX_DEEP_LINK
            androidParameters(BuildConfig.APPLICATION_ID) {
                minimumVersion = 28
            }
        }.addOnSuccessListener { (shortLink, _) ->
            uiState =
                uiState.copy(successReferralCode = Event(shortLink.toString()))
        }.addOnFailureListener { result ->
            result
        }
    }


    fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }


    data class ReferralUiState(
        val isLoading: Boolean = false,
        val error: Event<String>? = null,
        val successReferralCode: Event<String>? = null,
        val listReferral: List<ItemReferral> = emptyList()
    )
}