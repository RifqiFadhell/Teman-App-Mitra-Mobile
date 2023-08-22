package id.teman.app.mitra.di

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.data.remote.ApiInterceptor
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.RefreshServiceInterface
import id.teman.app.mitra.data.remote.TokenAuthenticator
import id.teman.app.mitra.data.remote.chat.ChatRemoteDataSource
import id.teman.app.mitra.data.remote.chat.DefaultChatRemoteDataSource
import id.teman.app.mitra.data.remote.location.DefaultLocationDataSource
import id.teman.app.mitra.data.remote.location.LocationDataSource
import id.teman.app.mitra.data.remote.notification.DefaultNotificationDataSource
import id.teman.app.mitra.data.remote.notification.NotificationDataSource
import id.teman.app.mitra.data.remote.referral.DefaultReferralDataSource
import id.teman.app.mitra.data.remote.referral.ReferralRemoteDataSource
import id.teman.app.mitra.data.remote.restaurant.DefaultRestaurantRemoteDataSource
import id.teman.app.mitra.data.remote.restaurant.RestaurantRemoteDataSource
import id.teman.app.mitra.data.remote.transport.DefaultTransportRemoteDataSource
import id.teman.app.mitra.data.remote.transport.TransportRemoteDataSource
import id.teman.app.mitra.data.remote.user.DefaultUserRemoteDataSource
import id.teman.app.mitra.data.remote.user.UserRemoteDataSource
import id.teman.app.mitra.data.remote.wallet.DefaultWalletRemoteDataSource
import id.teman.app.mitra.data.remote.wallet.WalletRemoteDataSource
import id.teman.app.mitra.device.DeviceInformation
import id.teman.app.mitra.manager.UserManager
import id.teman.app.mitra.preference.Preference
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Qualifier
    annotation class RefreshTokenClient

    @Qualifier
    annotation class ApiClient

    @Singleton
    @Provides
    fun provideChuckerInterceptor(application: Application): ChuckerInterceptor {
        // Create the Collector
        val chuckerCollector = ChuckerCollector(
            context = application,
            // Toggles visibility of the notification
            showNotification = true,
            // Allows to customize the retention period of collected data
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(application)
            .collector(chuckerCollector)
            .alwaysReadResponseBody(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthenticator(
        httpClient: RefreshServiceInterface,
        preference: Preference,
        userManager: UserManager,
        deviceInformation: DeviceInformation
    ) = TokenAuthenticator(httpClient, preference, userManager, deviceInformation)

    @Singleton
    @Provides
    @ApiClient
    fun provideOkHttpClient(
        chuckerInterceptor: ChuckerInterceptor,
        apiInterceptor: ApiInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .authenticator(authenticator)
            .also {
                it.addInterceptor(chuckerInterceptor)
                it.addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                it.addInterceptor(apiInterceptor)
            }
            .build()
    }

    @Provides
    @Singleton
    @RefreshTokenClient
    fun provideOkHttpClientForRefreshToken(
        chuckerInterceptor: ChuckerInterceptor,
        apiInterceptor: ApiInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .callTimeout(10000, TimeUnit.SECONDS)
            .also {
                it.addInterceptor(apiInterceptor)
                it.addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                it.addInterceptor(chuckerInterceptor)
            }
            .retryOnConnectionFailure(false)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @RefreshTokenClient
    fun provideRetrofitForRefreshToken(
        @RefreshTokenClient okHttpClient: OkHttpClient,
        json: Json,
        BASE_URL: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    @ApiClient
    fun provideRetrofit(@ApiClient okHttpClient: OkHttpClient, BASE_URL: String, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRefreshApiService(@RefreshTokenClient retrofit: Retrofit): RefreshServiceInterface {
        return retrofit.create(RefreshServiceInterface::class.java)
    }

    @Provides
    @Singleton
    @ApiClient
    fun provideApiService(@ApiClient retrofit: Retrofit): ApiServiceInterface {
        return retrofit.create(ApiServiceInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideUserDataSource(@ApiClient httpClient: ApiServiceInterface): UserRemoteDataSource {
        return DefaultUserRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideTransportRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): TransportRemoteDataSource {
        return DefaultTransportRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideLocationDataSource(@ApiClient httpClient: ApiServiceInterface): LocationDataSource  {
        return DefaultLocationDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideRestaurantRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): RestaurantRemoteDataSource {
        return DefaultRestaurantRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideChatRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): ChatRemoteDataSource {
        return DefaultChatRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideNotificationRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): NotificationDataSource {
        return DefaultNotificationDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideWalletRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): WalletRemoteDataSource {
        return DefaultWalletRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideReferralRemoteDataSource(@ApiClient httpClient: ApiServiceInterface): ReferralRemoteDataSource {
        return DefaultReferralDataSource(httpClient)
    }

    @Singleton
    @Provides
    fun provideApiInterceptor(
        deviceInformation: DeviceInformation,
        preference: Preference
    ): ApiInterceptor {
        return ApiInterceptor(deviceInformation, preference)
    }

    @Singleton
    @Provides
    fun provideKotlinXJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}