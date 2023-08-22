package id.teman.app.mitra.ui.transport

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.distance
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.getSnapLatLng
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.domain.model.transport.DriverOrderSummarySpec
import id.teman.app.mitra.domain.model.transport.TransportEnRouteSpec
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.domain.model.transport.TransportRequestType
import id.teman.app.mitra.domain.model.transport.TransportTopBarType
import id.teman.app.mitra.domain.model.transport.isTerminalStatus
import id.teman.app.mitra.domain.model.transport.nextLevel
import id.teman.app.mitra.domain.model.user.DriverInfo
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.domain.model.user.DriverStatus
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.getVehicleType
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.transport.TransportRepository
import id.teman.app.mitra.repository.user.UserRepository
import id.teman.app.mitra.repository.wallet.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import java.net.UnknownHostException
import javax.net.ssl.SSLException

@HiltViewModel
class TransportViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val transportRepository: TransportRepository,
    private val walletRepository: WalletRepository,
    private val preference: Preference,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    var transportUiState by mutableStateOf(TransportUiStateSpec())
        private set

    var currentLocation = mutableStateOf<Location?>(null)
        private set

    var currentPolyline by mutableStateOf<List<LatLng>>(emptyList())
        private set

    var currentWalletBalance by mutableStateOf(0.0)
        private set

    var orderRequestActiveness by mutableStateOf(true)
        private set

    fun updateUserLocation(value: Location) {
        if (currentPolyline.isNotEmpty()) {
            val getCurrentLatLngSnapped =
                getSnapLatLng(currentPolyline, LatLng(value.latitude, value.longitude))
            val newLocation = Location("").apply {
                latitude = getCurrentLatLngSnapped.latitude
                longitude = getCurrentLatLngSnapped.longitude
                bearing = value.bearing
            }
            currentLocation.value = newLocation
            transportUiState = transportUiState.copy(
                updateDriverLocation = Event(newLocation),
                latestDriverPosition = getCurrentLatLngSnapped
            )
        } else {
            currentLocation.value = value
            transportUiState = transportUiState.copy(
                updateDriverLocation = Event(value),
                latestDriverPosition = LatLng(value.latitude, value.longitude)
            )
        }
        checkRadius()
        if (currentWalletBalance > 20000) {
            requestActiveOrderStatus()
        }
    }

    fun checkRadiusRemote() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val distance = remoteConfig.getLong("distance")
                if (distance > 0) {
                    transportUiState = transportUiState.copy(radius = distance.toInt())
                }
            }
        }
    }

    fun checkRadius() {
        val radius = 120 * 0.001
        transportUiState = transportUiState.copy(
            isDriverOnPoint = distance(
                lat1 = currentLocation.value?.latitude.orZero(),
                lng1 = currentLocation.value?.longitude.orZero(),
                lat2 = transportUiState.customerPosition.latitude,
                lng2 = transportUiState.customerPosition.longitude
            ) < radius
        )
    }

    private fun getWalletInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.getWalletBalance()
                .catch {exception ->
                    /* no-op */
                }
                .collect {
                    currentWalletBalance = it
                }
        }
    }

    fun resetMapData() {
        transportUiState = TransportUiStateSpec(resetMap = Event(Unit))
    }

    private fun minimumBalance(): Double {
        val minimumBalance =
            if (transportUiState.driverInactiveUI?.driverInfo?.mitraType == DriverMitraType.CAR) {
                20000.0
            } else {
                10000.0
            }
//        return if (transportUiState.driverInactiveUI?.userInfo?.minimumBalance.orZero() <= 1.0) minimumBalance else transportUiState.driverInactiveUI?.userInfo?.minimumBalance.orZero()
        return transportUiState.driverInactiveUI?.userInfo?.minimumBalance.orZero()
    }

    fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val zippedFlow: Flow<Pair<Double, UserInfo>> = walletRepository.getWalletBalance()
                .zip(userRepository.getUserProfile()) { walletBalance, profile -> walletBalance to profile }
            zippedFlow
                .onStart {
                    transportUiState =
                        TransportUiStateSpec(
                            isLoading = true,
                            exceptionCardUI = null,
                            topBarType = TransportTopBarType.DEFAULT_TOP_BAR
                        )
                }.catch { exception ->
                    transportUiState = TransportUiStateSpec(
                        isLoading = false,
                        topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                        exceptionCardUI = exception.message ?: "Telah Terjadi Kesalahan")
                }.collect { (walletBalance, userInfo) ->
                    currentWalletBalance = walletBalance
                    if (userInfo.driverInfo != null) {
                        if (minimumBalance() != 0.0 && minimumBalance() > 0 && currentWalletBalance < minimumBalance() || userInfo.driverInfo.status == DriverStatus.OFFLINE) {
                            getDriverOrderSummary(userInfo)
                        } else {
                            requestActiveOrderStatus()
                        }
                    } else {
                        transportUiState = transportUiState.copy(
                            isLoading = false, exceptionCardUI = "Data Driver Tidak Di temukan",
                            topBarType = TransportTopBarType.DEFAULT_TOP_BAR
                        )
                    }
                }
        }
    }

    fun changeDriverStatus(active: Boolean) {
        getWalletInformation()
        viewModelScope.launch {
            if (!active) {
                orderRequestActiveness = false
            }
            if (active && minimumBalance() < 0.0 && currentWalletBalance <= minimumBalance()) {
                transportUiState = transportUiState.copy(
                    walletAlert = Event(
                        MinimumBalance(
                            isShow = true,
                            value = minimumBalance().convertToRupiah()
                        )
                    )
                )
                return@launch
            }
            transportUiState = transportUiState.copy(isLoading = true)
            userRepository.updateDriverStatus(
                status = if (active) DriverStatus.ONLINE.statusName else DriverStatus.OFFLINE.statusName
            ).catch { exception ->
                transportUiState = transportUiState.copy(
                    isLoading = false,
                    exceptionCardUI = exception.message.orEmpty(),
                    topBarType = TransportTopBarType.DEFAULT_TOP_BAR
                )
            }.collect { userInfo ->
                if (active) {
                    transportUiState = transportUiState.copy(
                        topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                        isDriverActive = true, driverInactiveUI = null
                    )
                    requestActiveOrderStatus()
                } else {
                    getDriverOrderSummary(userInfo)
                }
            }
        }
    }

    private fun getDriverOrderSummary(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            transportRepository.getDriverOrderSummary().catch { exception ->
                transportUiState = transportUiState.copy(
                    topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                    isLoading = false,
                    exceptionCardUI = exception.message.orEmpty(),
                    isDriverActive = false
                )
            }.collect { driverSpec ->
                transportUiState = TransportUiStateSpec(
                    topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                    isDriverActive = false,
                    isLoading = false,
                    driverInactiveUI = InactiveUiSpec(
                        driverOrderSummary = driverSpec,
                        userInfo = userInfo,
                        driverInfo = userInfo.driverInfo!!
                    )
                )
            }
        }
    }

    @Synchronized
    fun requestActiveOrderStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!orderRequestActiveness) orderRequestActiveness = true
            while (orderRequestActiveness) {
                delay(2000)
                if (minimumBalance() == 0.0 || minimumBalance() < 0.0) {
                    transportRepository.getDriverStatus().catch { exception ->
                        transportUiState = transportUiState.copy(
                            topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                            driverCustomerRequestUI = null,
                            isDriverActive = true,
                            pushPolyLine = Event(emptyList())
                        )
                    }.collect { orderSpec ->
                        orderSpec.orderStatus.stateRendering(orderSpec)
                    }
                } else {
                    if (orderRequestActiveness && currentWalletBalance > minimumBalance()) {
                        transportRepository.getDriverStatus().catch { exception ->
                            transportUiState = transportUiState.copy(
                                topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                                driverCustomerRequestUI = null,
                                isDriverActive = currentWalletBalance > minimumBalance(),
                                pushPolyLine = Event(emptyList())
                            )
                        }.collect { orderSpec ->
                            orderSpec.orderStatus.stateRendering(orderSpec)
                        }
                    }
                }
            }
        }
    }

    private fun TransportRequestType.stateRendering(order: TransportOrderSpec) {
        val latLngPickup = LatLng(order.pickupLatitude, order.pickupLongitude)
        val latLngCustomer = LatLng(order.destinationLatitude, order.destinationLongitude)
        return when (this) {
            TransportRequestType.REQUESTING -> {
                orderRequestActiveness = false
                if (order.orderStatus.isTerminalStatus()) {
                    transportUiState = transportUiState.copy(
                        topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                        driverCustomerRequestUI = null,
                        isDriverActive = true
                    )
                } else {
                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        driverCustomerRequestUI = order,
                        isDriverActive = true,
                        topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                        emptyCardUI = false
                    )
                }
            }

            TransportRequestType.ACCEPTED -> {
                orderRequestActiveness = false
                getMapsDirection(order)
                transportUiState = transportUiState.copy(customerPosition = latLngPickup)
                checkRadius()
            }

            TransportRequestType.ONROUTE -> {
                orderRequestActiveness = false
                getMapsDirection(order)
                transportUiState = transportUiState.copy(customerPosition = latLngCustomer)
                checkRadius()
            }

            else -> {
                transportUiState = transportUiState.copy(
                    topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                    isDriverActive = true
                )
            }
        }
    }

    fun updateRequestStatus(selectedOrder: TransportOrderSpec) {
        transportUiState = transportUiState.copy(isLoading = true)
        if (selectedOrder.itemsFood.isNotEmpty() && selectedOrder.orderStatus == TransportRequestType.ACCEPTED) {
            if (selectedOrder.restaurantOrderStatus == RestaurantOrderStatus.PROCESS
                || selectedOrder.restaurantOrderStatus == RestaurantOrderStatus.NEW
            ) {
                transportUiState = transportUiState.copy(
                    isLoading = false,
                    exceptionCardUI = "Kamu tidak bisa memulai trip selanjutnya sebelum resto menandakan pesanan siap!"
                )
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            transportRepository.updateRequestStatus(
                selectedOrder, selectedOrder.orderStatus.nextLevel().value
            ).catch { exception ->
                transportUiState = transportUiState.copy(
                    isLoading = false, exceptionCardUI = exception.message ?: "Telah Terjadi Kesalahan"
                )
            }.collect { order ->
                if (order.isTerminalStatus()) {
                    currentPolyline = emptyList()
                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        driverCompletedTripUI = if (order.paymentMethod == "cash") order.totalPrice else order.driverIncome,
                        topBarType = TransportTopBarType.FINISHED_TOP_BAR,
                        driverDirectionSpec = Event(TransportEnRouteSpec(emptyList(), null, null)),
                        driverOnRouteRequestUI = null,
                        pushPolyLine = Event(emptyList())
                    )
                } else {
                    requestActiveOrderStatus()
                }
            }
        }
        checkRadius()
    }

    private fun getDriverCancelledCountJson(): Int {
        return runBlocking { preference.getDriverCancelledCount.first() }
    }

    fun rejectOrderRequest(selectedOrder: TransportOrderSpec) {
        transportUiState = transportUiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            if (getDriverCancelledCountJson() > 3) {
                transportUiState = transportUiState.copy(rejectCountAlert = Event(true))
            }
            transportRepository.updateRequestStatus(
                selectedOrder,
                TransportRequestType.REJECTED.value
            ).catch { exception ->
                transportUiState = transportUiState.copy(
                    isLoading = false, exceptionCardUI = exception.message ?: "Telah Terjadi Kesalahan",
                    pushPolyLine = Event(emptyList())
                )
            }.collect {
                if (getDriverCancelledCountJson() == 0) {
                    val count = getDriverCancelledCountJson()
                    val newCount = count + 1
                    if (newCount >= 3) {
                        transportUiState = transportUiState.copy(rejectCountAlert = Event(true))
                    }
                    preference.setDriverCancelledCount(newCount)
                    preference.setDriverTimestampCount(System.currentTimeMillis())

                } else {
                    val count = getDriverCancelledCountJson()
                    val newCount = count + 1
                    preference.setDriverCancelledCount(newCount)
                    preference.setDriverTimestampCount(System.currentTimeMillis())
                }
                transportUiState =
                    transportUiState.copy(
                        redirectToTransportStart = Event(Unit),
                        pushPolyLine = Event(emptyList())
                    )
                requestActiveOrderStatus()
            }
        }
    }

    fun getMapsDirection(spec: TransportOrderSpec) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            val latLngPickup = LatLng(spec.pickupLatitude, spec.pickupLongitude)
            val latLngCustomer = LatLng(spec.destinationLatitude, spec.destinationLongitude)
            val originLatLng = LatLng(
                currentLocation.value?.latitude.orZero(),
                currentLocation.value?.longitude.orZero()
            )
            val destinationLatLng =
                if (spec.orderStatus == TransportRequestType.ONROUTE) latLngCustomer else latLngPickup
            val pairAddress = getOriginDestinationLatLngAddress(originLatLng, destinationLatLng)
            transportRepository.getMapDirection(
                pairAddress.first,
                pairAddress.second,
                spec.driverType.getVehicleType()
            )
                .catch { exception ->
                    transportUiState = transportUiState
                        .copy(
                            isLoading = false,
                            exceptionCardUI = exception.message ?: "Telah Terjadi Kesalahan",
                            topBarType = TransportTopBarType.DEFAULT_TOP_BAR,
                        )
                }.collect { directionSpec ->
                    currentPolyline = directionSpec.points
                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        pushPolyLine = Event(directionSpec.points),
                        driverDirectionSpec = Event(
                            TransportEnRouteSpec(
                                points = directionSpec.points,
                                bounds = directionSpec.bounds,
                                destinationLatLng = destinationLatLng
                            )
                        ),
                        driverOnRouteRequestUI = if (spec.orderStatus == TransportRequestType.ONROUTE) spec else null,
                        driverAcceptedRequestUI = if (spec.orderStatus == TransportRequestType.ACCEPTED) spec else null,
                        driverCustomerRequestUI = null,
                        isDriverActive = true,
                        topBarType = TransportTopBarType.ONGOING_ORDER_TOP_BAR,
                        emptyCardUI = false
                    )
                }
        }
    }

    private fun getOriginDestinationLatLngAddress(
        origin: LatLng,
        destination: LatLng
    ): Pair<String, String> {
        val originAddress = "${origin.latitude},${origin.longitude}"
        val destinationAddress = "${destination.latitude},${destination.longitude}"
        return Pair(originAddress, destinationAddress)
    }

    data class TransportUiStateSpec(
        val isLoading: Boolean = false,
        val driverInactiveUI: InactiveUiSpec? = null,
        val driverCustomerRequestUI: TransportOrderSpec? = null,
        val driverAcceptedRequestUI: TransportOrderSpec? = null,
        val driverOnRouteRequestUI: TransportOrderSpec? = null,
        val driverEmptyCustomerRequestUI: Boolean = false,
        val driverCompletedTripUI: Double? = null,
        val topBarType: TransportTopBarType = TransportTopBarType.START_TOP_BAR,
        val exceptionCardUI: String? = null,
        val emptyCardUI: Boolean = false,
        val isDriverActive: Boolean = false,
        val isDriverOnPoint: Boolean = false,
        val driverDirectionSpec: Event<TransportEnRouteSpec>? = null,
        val updateDriverLocation: Event<Location>? = null,
        val redirectToTransportStart: Event<Unit>? = null,
        val pushPolyLine: Event<List<LatLng>>? = null,
        val latestDriverPosition: LatLng = LatLng(-6.7428609,108.5128394),
        val customerPosition: LatLng = LatLng(-6.7428609,108.5128394),
        val pickUpPosition: LatLng = LatLng(-6.7428609,108.5128394),
        val walletAlert: Event<MinimumBalance>? = null,
        val rejectCountAlert: Event<Boolean>? = null,
        val resetMap: Event<Unit>? = null,
        val mitraType: DriverMitraType? = null,
        val radius: Int? = 250
    )

    data class InactiveUiSpec(
        val driverOrderSummary: DriverOrderSummarySpec,
        val userInfo: UserInfo,
        val driverInfo: DriverInfo
    )

    data class MinimumBalance(
        val isShow: Boolean,
        val value: String
    )
}