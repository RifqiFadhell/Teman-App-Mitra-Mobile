package id.teman.app.mitra.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.common.orZero
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferenceKeys {
    val USER_INFO = stringPreferencesKey("user_info")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val REGISTRATION_FOOD_INFO = stringPreferencesKey("registration_food_info")
    val BEARER_TOKEN = stringPreferencesKey("bearer_token")
    val DRIVER_CANCEL_COUNT = intPreferencesKey("driver_cancel_count")
    val DRIVER_TIMESTAMP_COUNT = longPreferencesKey("driver_timestamp_count")
    val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
}

interface Preference {
    val getBearerToken: Flow<String>
    val getRefreshToken: Flow<String>
    val getUserInfo: Flow<String>
    val getRegistrationFoodInfo: Flow<String>
    val getDriverCancelledCount: Flow<Int>
    val getDriverTimestampCount: Flow<Long>
    val getHasSeenOnBoarding: Flow<Boolean>
    suspend fun setBearerToken(newValue: String)
    suspend fun setRefreshToken(newValue: String)
    suspend fun setRegistrationFoodInfo(newValue: String)
    suspend fun setUserInfo(newValue: String)
    suspend fun setDriverCancelledCount(newValue: Int)
    suspend fun setDriverTimestampCount(newValue: Long)
    suspend fun setHasSeenOnBoarding(newValue: Boolean)

}

class DefaultPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
): Preference {

    override val getBearerToken: Flow<String>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.BEARER_TOKEN].orEmpty() }
    override val getRefreshToken: Flow<String>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.REFRESH_TOKEN].orEmpty() }
    override val getUserInfo: Flow<String>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.USER_INFO].orEmpty() }
    override val getRegistrationFoodInfo: Flow<String>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.REGISTRATION_FOOD_INFO].orEmpty() }
    override val getDriverCancelledCount: Flow<Int>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.DRIVER_CANCEL_COUNT].orZero() }
    override val getDriverTimestampCount: Flow<Long>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.DRIVER_TIMESTAMP_COUNT] ?: 0L }
    override val getHasSeenOnBoarding: Flow<Boolean>
        get() = dataStore.data.map { preference -> preference[PreferenceKeys.HAS_SEEN_ONBOARDING].orFalse() }

    override suspend fun setBearerToken(newValue: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.BEARER_TOKEN] = newValue
        }
    }

    override suspend fun setRefreshToken(newValue: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.REFRESH_TOKEN] = newValue
        }
    }

    override suspend fun setRegistrationFoodInfo(newValue: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.REGISTRATION_FOOD_INFO] = newValue
        }
    }

    override suspend fun setUserInfo(newValue: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.USER_INFO] = newValue
        }
    }

    override suspend fun setDriverCancelledCount(newValue: Int) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.DRIVER_CANCEL_COUNT] = newValue
        }
    }

    override suspend fun setDriverTimestampCount(newValue: Long) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.DRIVER_TIMESTAMP_COUNT] = newValue
        }
    }

    override suspend fun setHasSeenOnBoarding(newValue: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.HAS_SEEN_ONBOARDING] = newValue
        }
    }
}