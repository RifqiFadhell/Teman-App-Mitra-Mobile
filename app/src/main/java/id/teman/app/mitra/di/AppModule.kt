package id.teman.app.mitra.di

import android.app.Application
import android.location.Geocoder
import android.provider.Settings
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.location.LocationRequest
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.R
import id.teman.app.mitra.camera.CustomCamera
import id.teman.app.mitra.camera.DefaultCustomCamera
import id.teman.app.mitra.device.DeviceInformation
import id.teman.app.mitra.manager.DefaultUserManager
import id.teman.app.mitra.manager.UserManager
import id.teman.app.mitra.preference.DefaultPreferences
import id.teman.app.mitra.preference.Preference
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    @Provides
    @Singleton
    fun provideCameraProvider(application: Application): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(application).get()
    }

    @Provides
    @Singleton
    fun provideLocationRequest() = LocationRequest.create()
        .setInterval(3000)
        .setFastestInterval(1500)
        .setSmallestDisplacement(5f)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    @Provides
    @Singleton
    fun provideCameraPreview(): Preview {
        return Preview.Builder()
            .setTargetResolution(Size(325, 205))
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setTargetResolution(Size(325, 205))
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    @Provides
    @Singleton
    fun provideCamera(
        cameraProvider: ProcessCameraProvider,
        selector: CameraSelector,
        imageCapture: ImageCapture,
        preview: Preview
    ): CustomCamera {
        return DefaultCustomCamera(cameraProvider, selector, preview, imageCapture)
    }

    @Singleton
    @Provides
    fun provideDeviceInformation(application: Application): DeviceInformation {
        return DeviceInformation(
            deviceId = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID),
            deviceName = android.os.Build.BRAND
        )
    }

    @Singleton
    @Provides
    fun providePreferenceHelper(dataStore: DataStore<Preferences>): Preference {
        return DefaultPreferences(dataStore)
    }

    @Singleton
    @Provides
    fun provideUserManager(): UserManager = DefaultUserManager()

    @Singleton
    @Provides
    fun provideGeocoder(application: Application): Geocoder {
        return Geocoder(application)
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { application.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
    @Singleton
    @Provides
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance().apply {
            val setting = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                    0
                } else {
                    600
                }
            }
            setConfigSettingsAsync(setting)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }
}