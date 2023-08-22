package id.teman.app.mitra.data.remote

import id.teman.app.mitra.data.dto.BaseResponse
import id.teman.app.mitra.data.dto.chat.ChatRequestDto
import id.teman.app.mitra.data.dto.chat.ChatResponseDto
import id.teman.app.mitra.data.dto.chat.SendMessageResponseDto
import id.teman.app.mitra.data.dto.maps.DirectionResponseDto
import id.teman.app.mitra.data.dto.maps.GooglePredictionsDto
import id.teman.app.mitra.data.dto.maps.PlaceResponseDto
import id.teman.app.mitra.data.dto.notification.NotificationDto
import id.teman.app.mitra.data.dto.notification.NotificationReadRequestDto
import id.teman.app.mitra.data.dto.notification.NotificationReadResponseDto
import id.teman.app.mitra.data.dto.referral.ReferralResponseDto
import id.teman.app.mitra.data.dto.restaurant.AddProductCategoryRequestDto
import id.teman.app.mitra.data.dto.restaurant.CategoriesResponseDto
import id.teman.app.mitra.data.dto.restaurant.ProductResponseDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantCategoriesDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantHoursDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantMenuCategoryDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantSummaryDto
import id.teman.app.mitra.data.dto.restaurant.UpdateProductCategoryDto
import id.teman.app.mitra.data.dto.restaurant.UpdateProductDto
import id.teman.app.mitra.data.dto.restaurant.UpdateRestaurantOrderStatusDto
import id.teman.app.mitra.data.dto.restaurant.UpdateRestaurantStatusDto
import id.teman.app.mitra.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.mitra.data.dto.reward.RewardRedeemedResponse
import id.teman.app.mitra.data.dto.reward.RewardResponseDto
import id.teman.app.mitra.data.dto.reward.RewardTransactionResponseDto
import id.teman.app.mitra.data.dto.transport.DrivingSummaryDto
import id.teman.app.mitra.data.dto.transport.SnappedPointsDto
import id.teman.app.mitra.data.dto.transport.TransportDataResponseDto
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.app.mitra.data.dto.transport.UpdateRequestStatusDto
import id.teman.app.mitra.data.dto.user.request.DriverStatusUpdateRequestDto
import id.teman.app.mitra.data.dto.user.request.LocationUpdateRequestDto
import id.teman.app.mitra.data.dto.user.request.LoginRequestDto
import id.teman.app.mitra.data.dto.user.request.OtpRequestDto
import id.teman.app.mitra.data.dto.user.response.BasicUserResponseDto
import id.teman.app.mitra.data.dto.user.response.DriverBasicInfoDto
import id.teman.app.mitra.data.dto.user.response.LoginResponseDto
import id.teman.app.mitra.data.dto.user.response.MitraRestaurantBasicInfoDto
import id.teman.app.mitra.data.dto.user.response.OtpResponseDto
import id.teman.app.mitra.data.dto.user.response.UserResponseDto
import id.teman.app.mitra.data.dto.wallet.ItemBankDto
import id.teman.app.mitra.data.dto.wallet.OtpPinVerificationDto
import id.teman.app.mitra.data.dto.wallet.UpdatePinRequestDto
import id.teman.app.mitra.data.dto.wallet.VerifyOtpRequestDto
import id.teman.app.mitra.data.dto.wallet.WalletBalanceDto
import id.teman.app.mitra.data.dto.wallet.WalletBankAccountDto
import id.teman.app.mitra.data.dto.wallet.WalletRequestDto
import id.teman.app.mitra.data.dto.wallet.WalletTransactionResponseDto
import id.teman.app.mitra.data.dto.wallet.WithdrawRequestDto
import id.teman.app.mitra.domain.model.wallet.WalletHistoryTransactionDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceInterface {

    @POST("auth_mitra/login_with_phone")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("auth_mitra/profile")
    suspend fun getUserProfile(): BasicUserResponseDto

    @POST("auth_mitra/register_with_phone")
    suspend fun phoneRegistration(@Body request: LoginRequestDto): BasicUserResponseDto

    @POST("auth_mitra/verification_otp_code")
    suspend fun verifyOtpCode(@Body request: OtpRequestDto): BasicUserResponseDto

    @PATCH("auth_mitra/location")
    suspend fun updateMitraLocation(@Body request: LocationUpdateRequestDto): DriverBasicInfoDto

    @PATCH("auth_mitra/status")
    suspend fun updateDriverStatus(@Body request: DriverStatusUpdateRequestDto): DriverBasicInfoDto

    @GET("auth_mitra/requests/{requestId}/messages")
    suspend fun getChatMessages(
        @Path("requestId") requestId: String
    ): ChatResponseDto

    @POST("auth_mitra/requests/{requestId}/messages")
    suspend fun sendChatMessage(
        @Body request: ChatRequestDto,
        @Path("requestId") requestId: String
    ): SendMessageResponseDto

    @Multipart
    @POST("auth_mitra/complete_profile")
    @JvmSuppressWildcards
    suspend fun completeProfileRegistration(
        @PartMap partMap: Map<String, RequestBody>,
        @Part profileImageFile: MultipartBody.Part?,
        @Part ktpImageFile: MultipartBody.Part?,
        @Part simImageFile: MultipartBody.Part?,
        @Part stnkImageFile: MultipartBody.Part?,
        @Part skckImageFile: MultipartBody.Part?,
        @Part vehicleImageFile: MultipartBody.Part?
    ): BasicUserResponseDto

    @Multipart
    @POST("auth_mitra/complete_restaurant")
    @JvmSuppressWildcards
    suspend fun completeRestaurantRegistration(
        @PartMap partMap: Map<String, RequestBody>,
        @Part restaurantPhoto: MultipartBody.Part?,
        @Part ktpPhoto: MultipartBody.Part?,
        @Part bankAccountPhoto: MultipartBody.Part?
    ): BasicUserResponseDto

    @GET("auth_mitra/requests")
    suspend fun getCustomerOrder(): TransportDataResponseDto

    @GET("auth_mitra/driving_summary")
    suspend fun getDrivingSummary(): DrivingSummaryDto

    @GET("https://maps.googleapis.com/maps/api/directions/json")
    suspend fun getMapDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("vehicle") vehicleType: String,
        @Query("key") apiKey: String,
        @Query("avoid") avoid: String? = null
    ): DirectionResponseDto

    @PATCH("auth_mitra/requests/{requestId}/status")
    suspend fun updateDriverRequestStatus(
        @Path("requestId") requestId: String,
        @Body status: UpdateRequestStatusDto
    ): TransportResponseDto

    @GET("auth_mitra/requests/status/active")
    suspend fun getDriverStatus(): TransportResponseDto

    @POST("auth_mitra/send_otp_code")
    suspend fun sendOtp(): OtpResponseDto

    @GET("https://roads.googleapis.com/v1/nearestRoads")
    suspend fun getSnappedRoad(
        @Query("points") origin: String,
        @Query("key") apiKey: String
    ): SnappedPointsDto

    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    suspend fun searchLocation(
        @Query("key") key: String,
        @Query("input") input: String,
        @Query("location") location: String? = "",
        @Query("radius") radius: String? = "",
    ): GooglePredictionsDto

    @GET("https://maps.googleapis.com/maps/api/place/details/json")
    suspend fun getPlaceDetail(
        @Query("key") key: String,
        @Query("place_id") placeId: String,
    ): PlaceResponseDto

    @GET("restaurant")
    suspend fun getRestaurantDetail(): MitraRestaurantBasicInfoDto

    @PATCH("restaurant/status")
    suspend fun updateRestaurantStatus(@Body status: UpdateRestaurantStatusDto): MitraRestaurantBasicInfoDto

    @PATCH("restaurant/hours")
    suspend fun updateRestaurantHours(@Body status: RestaurantHoursDto): RestaurantHoursDto

    @GET("restaurant/hours")
    suspend fun getRestaurantHours(): RestaurantHoursDto

    @GET("restaurant/product_categories")
    suspend fun getRestaurantMenuCategories(
        @Query("s", encoded = true) search: String? = null
    ): RestaurantCategoriesDto

    @POST("restaurant/product_categories")
    suspend fun addRestaurantCategories(@Body request: AddProductCategoryRequestDto): RestaurantMenuCategoryDto

    @GET("restaurant_categories")
    suspend fun getListCategoriesRestaurant(): CategoriesResponseDto

    @Multipart
    @POST("restaurant/products")
    @JvmSuppressWildcards
    suspend fun addRestaurantProduct(
        @PartMap partMap: Map<String, RequestBody>,
        @Part productPhotoFile: MultipartBody.Part?
    ): ProductResponseDto

    @Multipart
    @PATCH("restaurant/products/{menu_id}")
    @JvmSuppressWildcards
    suspend fun updateRestaurantProduct(
        @PartMap partMap: Map<String, RequestBody>,
        @Path("menu_id") menuId: String,
        @Part productPhotoFile: MultipartBody.Part?
    ): ProductResponseDto

    @DELETE("restaurant/products/{menu_id}")
    suspend fun deleteRestaurantProduct(
        @Path("menu_id") menuId: String
    ): BaseResponse

    @PATCH("restaurant/products/{productId}")
    suspend fun updateProduct(
        @Path("productId", encoded = true) productId: String,
        @Body body: UpdateProductDto
    ): ProductResponseDto

    @GET("restaurant/requests")
    suspend fun getRestaurantOrderRequest(
        @Query("s", encoded = true) orderStatus: String? = null,
        @Query("search", encoded = true) query: String? = null
    ): TransportDataResponseDto

    @GET("restaurant/requests/{requestId}")
    suspend fun getRestaurantOrderDetail(
        @Path("requestId") requestId: String
    ): TransportResponseDto

    @GET("restaurant/summary")
    suspend fun getRestaurantSummary(
        @Query("start_date") dateFilter: String?
    ): RestaurantSummaryDto

    @PATCH("auth_mitra/requests/{requestId}/order_status")
    suspend fun updateRestaurantOrderStatus(
        @Path("requestId") requestId: String,
        @Body orderStatus: UpdateRestaurantOrderStatusDto
    ): TransportResponseDto

    @GET("notifications")
    suspend fun getNotifications(): NotificationDto

    @POST("notifications/read")
    suspend fun readNotification(@Body request: NotificationReadRequestDto): NotificationReadResponseDto

    @PATCH("restaurant/product_categories/{categoriesId}")
    suspend fun updateRestaurantMenuCategory(
        @Path("categoriesId") categoryId: String,
        @Body request: UpdateProductCategoryDto
    ): RestaurantMenuCategoryDto

    @Multipart
    @PATCH("auth/profile")
    @JvmSuppressWildcards
    suspend fun updateDriverProfile(
        @PartMap partMap: Map<String, RequestBody>,
        @Part productPhotoFile: MultipartBody.Part?
    ): UserResponseDto

    @Multipart
    @PATCH("restaurant")
    @JvmSuppressWildcards
    suspend fun updateRestaurantProfile(
        @PartMap partMap: Map<String, RequestBody>,
        @Part productPhotoFile: MultipartBody.Part?
    ): UserResponseDto

    @POST("auth/logout")
    suspend fun logoutUser(): BaseResponse

    @POST("wallet/send_otp_update_pin")
    suspend fun requestWalletOtpPin(): OtpResponseDto

    @POST("wallet/verification_otp_update_pin")
    suspend fun verifyWalletOtpPIN(
        @Body request: VerifyOtpRequestDto
    ): OtpPinVerificationDto

    @POST("wallet/update_pin")
    suspend fun updateWalletPIN(
        @Body request: UpdatePinRequestDto
    ): BaseResponse

    @GET("wallet/transactions")
    suspend fun getWalletTransactions(): WalletTransactionResponseDto

    @GET("wallet")
    suspend fun getWalletBalance(): WalletBalanceDto

    @GET("wallet/bank")
    suspend fun getBankInformation(): WalletBankAccountDto

    @GET("wallet/list_bank")
    suspend fun getListBank(): List<ItemBankDto>? = emptyList()

    @POST("wallet/topup")
    suspend fun topUpWalletAmount(@Body requestDto: WalletRequestDto): WalletHistoryTransactionDetail

    @Multipart
    @PATCH("wallet/bank")
    @JvmSuppressWildcards
    suspend fun updateWalletBankInformation(@PartMap partMap: Map<String, RequestBody>): WalletBankAccountDto

    @POST("wallet/withdraw")
    suspend fun withdrawMoney(@Body request: WithdrawRequestDto): BaseResponse

    @GET("auth_mitra/requests")
    suspend fun getRequestHistory(): TransportDataResponseDto

    @GET("driver/rewards")
    suspend fun getListRewards(): RewardResponseDto

    @GET("user/point_histories")
    suspend fun getListRewardTransaction(): RewardTransactionResponseDto

    @GET("user/redeems")
    suspend fun getListRewardRedeemed(): RewardRedeemedResponse

    @POST("auth_mitra/redeem")
    suspend fun redeemReward(@Body request: RewardRedeemRequestDto): BaseResponse

    @GET("user/referral_code_useds")
    suspend fun getHistoryReferral(): ReferralResponseDto
}