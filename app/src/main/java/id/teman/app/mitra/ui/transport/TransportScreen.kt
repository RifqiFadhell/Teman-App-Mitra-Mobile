package id.teman.app.mitra.ui.transport

import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.gms.location.LocationRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.mitra.MainActivity
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.getActivity
import id.teman.app.mitra.ui.MainViewModel
import id.teman.app.mitra.ui.destinations.DriverProfileScreenDestination
import id.teman.app.mitra.ui.destinations.OrderDetailScreenDestination
import id.teman.app.mitra.ui.maps.MapScreen
import id.teman.app.mitra.ui.transport.active.completed.OrderCompleted
import id.teman.app.mitra.ui.transport.active.inprogress.AcceptedOrderUI
import id.teman.app.mitra.ui.transport.active.inprogress.ActiveOrderList
import id.teman.app.mitra.ui.transport.active.inprogress.EmptyOrderUi
import id.teman.app.mitra.ui.transport.active.inprogress.LoadingUI
import id.teman.app.mitra.ui.transport.common.ErrorOrderUI
import id.teman.app.mitra.ui.transport.header.TransportHeaderWidget
import id.teman.app.mitra.ui.transport.inactive.InactiveBottomUi
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val locationRequest by lazy { LocationRequest.create().apply {
    interval = 1000
    fastestInterval = 500
    smallestDisplacement = 8f
    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
} }

@Destination
@Composable
fun TransportScreen(
    navigator: DestinationsNavigator,
    viewModel: TransportViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    resultOrderDetail: ResultRecipient<OrderDetailScreenDestination, Boolean>
) {
    val transportUiState = viewModel.transportUiState
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // init the page
    var isApiCalled by remember { mutableStateOf(false) }
//    val isDriverOnPoint by remember { mutableStateOf(false) }

    resultOrderDetail.onNavResult {
        handleBackResult(it) {
            viewModel.resetMapData()
            coroutineScope.launch {
                delay(200)
                viewModel.loadInitialData()
            }
        }
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        coroutineScope.launch {
            viewModel.checkRadiusRemote()
            mainViewModel.getUserProfile()
            viewModel.checkRadius()
        }
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        coroutineScope.launch {
            mainViewModel.getUserProfile()
            if ( context is MainActivity) {
                context.startLocationUpdates(locationRequest)
            }
            viewModel.checkRadius()
        }
    }

    LaunchedEffect(key1 = mainViewModel.locationUiState.updatedLocation, block = {
        mainViewModel.locationUiState.updatedLocation?.consumeOnce { location ->
            viewModel.updateUserLocation(location)
            coroutineScope.launch {
                if (!isApiCalled) {
                    viewModel.loadInitialData()
                    isApiCalled = true
                    mainViewModel.stopLoadingLocationUIState()
                } else {
                    mainViewModel.stopLoadingLocationUIState()
                }
            }
        }
    })

    LaunchedEffect(key1 = transportUiState.pushPolyLine, block = {
        transportUiState.pushPolyLine?.consumeOnce {
            mainViewModel.setPolyline(it)
        }
    })

    LaunchedEffect(key1 = transportUiState.rejectCountAlert, block = {
        transportUiState.rejectCountAlert?.consumeOnce {
            Toast.makeText(context, "Kamu sudah cancel order 3x dalam sehari, akun anda akan kami suspend", Toast.LENGTH_SHORT).show()
        }
    })

    LaunchedEffect(key1 = transportUiState.walletAlert, block = {
        transportUiState.walletAlert?.consumeOnce {
            if (it.isShow) Toast.makeText(context, "Minimal saldo ${it.value} untuk mengaktifkan status", Toast.LENGTH_SHORT).show()
        }
    })
    Box(modifier = Modifier.fillMaxSize()) {
        mainViewModel.currentLocation?.let { userLocation ->
            MapScreen(viewModel = viewModel, value = userLocation)
        }
        TransportHeaderWidget(
            transportUiState,
            onChangeDriverStatus = { isActive ->
                viewModel.changeDriverStatus(isActive)
            }, onDrawerClick = {
                navigator.navigate(DriverProfileScreenDestination)
            })

        if (transportUiState.driverInactiveUI != null) {
            InactiveBottomUi(
                isDriverActive = transportUiState.isDriverActive,
                inactiveUiSpec = transportUiState.driverInactiveUI
            )
        }
        if (transportUiState.driverCustomerRequestUI != null) {
            ActiveOrderList(
                order = transportUiState.driverCustomerRequestUI,
                onAcceptJob = {
                    viewModel.updateRequestStatus(it)
                }, onSkipClick = {
                    viewModel.rejectOrderRequest(it)
                })
        }
        if (transportUiState.emptyCardUI) {
            EmptyOrderUi {
                viewModel.requestActiveOrderStatus()
            }
        }
        if (transportUiState.driverAcceptedRequestUI != null) {
            AcceptedOrderUI(
                transportUiState.driverAcceptedRequestUI,
                onDetailClick = {
                    navigator.navigate(OrderDetailScreenDestination.invoke(it))
                },
                onDeliverClick = {
                    viewModel.updateRequestStatus(it)
                },
                isDriverOnPoint = transportUiState.isDriverOnPoint
            )
        }
        if (transportUiState.driverOnRouteRequestUI != null) {
            AcceptedOrderUI(
                transportUiState.driverOnRouteRequestUI,
                onDetailClick = {
                    navigator.navigate(OrderDetailScreenDestination.invoke(it))
                },
                onDeliverClick = {
                    viewModel.updateRequestStatus(it)
                },
                isDriverOnPoint = transportUiState.isDriverOnPoint
            )
        }
        if (transportUiState.isLoading) {
            LoadingUI()
        }
        if (transportUiState.driverCompletedTripUI != null) {
            OrderCompleted(transportUiState.driverCompletedTripUI) {
                viewModel.loadInitialData()
            }
        }
        if (transportUiState.exceptionCardUI.orEmpty().isNotEmpty()) {
            ErrorOrderUI(message = transportUiState.exceptionCardUI.orEmpty()) {
                viewModel.loadInitialData()
            }
        }

        if (mainViewModel.locationUiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = UiColor.primaryRed500
                )
            }
        }

    }
}

fun handleBackResult(result: NavResult<Boolean>, renderBeginningState: () -> Unit) {
    when (result) {
        is NavResult.Canceled -> Unit
        is NavResult.Value -> {
            renderBeginningState()
        }
    }
}