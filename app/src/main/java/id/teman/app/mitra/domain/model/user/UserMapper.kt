package id.teman.app.mitra.domain.model.user

import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.user.response.BasicUserResponseDto
import id.teman.app.mitra.data.dto.user.response.DriverBasicInfoDto
import id.teman.app.mitra.data.dto.user.response.MitraRestaurantBasicInfoDto

fun BasicUserResponseDto.toUserInfo(): UserInfo {
    return UserInfo(
        userId = user?.id.orEmpty(),
        name = user?.name.orEmpty(),
        email = user?.email.orEmpty(),
        phoneNumber = user?.phoneNumber.orEmpty(),
        isVerified = user?.verified.orFalse(),
        driverInfo = driver?.toDriverInfo(),
        restaurantInfo = restaurant?.toMitraRestaurantInfo(),
        userKycStatus = UserKycStatus.from(user?.kycStatus),
        isPinAlreadySet = user?.pinStatus.orFalse(),
        point = user?.point.orZero(),
        minimumBalance = user?.minimumBalance.orZero(),
        referralCode = user?.referralCode.orEmpty()
    )
}

fun DriverBasicInfoDto?.toDriverInfo(): DriverInfo? {
    if (this == null) return null
    return DriverInfo(
        id = id.orEmpty(),
        currentLatitude = lat.orZero(),
        currentLongitude = lng.orZero(),
        rating = rating.orZero(),
        status = DriverStatus.from(status),
        isVerified = verified.orFalse(),
        isVaccine = vaccine.orFalse(),
        city = city.orEmpty(),
        mitraType = DriverMitraType.from(type),
        vehicleNumber = vehicleNumber.orEmpty(),
        vehicleBrand = vehicleBrand.orEmpty(),
        vehicleYear = vehicleYear.orEmpty(),
        vehicleType = vehicleType.orEmpty(),
        vehicleFuel = vehicleFuel.orEmpty(),
        photo = driverPhoto?.url.orEmpty()
    )
}

fun MitraRestaurantBasicInfoDto.toMitraRestaurantInfo(): MitraRestaurantInfo {
    return MitraRestaurantInfo(
        id = id.orEmpty(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        userTypedAddress = address.orEmpty(),
        completedAddress = description.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        email = email.orEmpty(),
        restaurantLatitude = lat.orZero(),
        restaurantLongitude = lng.orZero(),
        restaurantOrderStatus = RestaurantStatus.from(status),
        rating = rating.orZero(),
        isVerified = verified.orFalse(),
        restaurantPhoto = restaurantPhoto?.url.orEmpty(),
        restaurantPhotoId = restaurantPhoto?.id.orEmpty(),
        totalIncome = totalRating.orZero(),
        totalOrder = totalSales.orZero()
    )
}