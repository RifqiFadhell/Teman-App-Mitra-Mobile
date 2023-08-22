package id.teman.app.mitra.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preference: Preference
): ViewModel() {

    fun setUserHasSeenOnboarding() {
        viewModelScope.launch {
            preference.setHasSeenOnBoarding(true)
        }
    }
}