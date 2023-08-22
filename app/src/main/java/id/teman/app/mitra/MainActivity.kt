package id.teman.app.mitra

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Route
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import id.teman.app.mitra.common.getActivity
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.data.remote.DefaultLocationClient
import id.teman.app.mitra.data.remote.LocationClient
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.UserKycStatus
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.ui.MainViewModel
import id.teman.app.mitra.ui.NavGraphs
import id.teman.app.mitra.ui.PermissionState
import id.teman.app.mitra.ui.UpdatePage
import id.teman.app.mitra.ui.appCurrentDestinationAsState
import id.teman.app.mitra.ui.bottomnav.BottomBar
import id.teman.app.mitra.ui.destinations.Destination
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderListScreenDestination
import id.teman.app.mitra.ui.destinations.LoginScreenDestination
import id.teman.app.mitra.ui.destinations.MenuScreenDestination
import id.teman.app.mitra.ui.destinations.MyAccountScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationMitraSelectionScreenDestination
import id.teman.app.mitra.ui.destinations.RegistrationPhoneScreenDestination
import id.teman.app.mitra.ui.destinations.TransportScreenDestination
import id.teman.app.mitra.ui.registration.viewmodel.RegistrationFoodViewModel
import id.teman.app.mitra.ui.startAppDestination
import id.teman.app.mitra.ui.theme.TemanUlaUserAppTheme
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preference: Preference

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var locationClient: LocationClient

    companion object {
        val navigationLocationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            smallestDisplacement = 8f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val balanceLocationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    var locationRequest: LocationRequest = navigationLocationRequest

    private var serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var json: Json

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionGranted = permissions.values.all { it }
        if (allPermissionGranted) {
            startLocationUpdates(locationRequest)
        } else {
            mainViewModel.changeUiState(PermissionState.LocationDenied)
        }
    }

    private val gpsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startLocationUpdates(locationRequest)
        } else {
            mainViewModel.changeUiState(PermissionState.LocationDenied)
        }
    }


    fun stopLocationUpdates() {
        serviceScope.cancel()
    }

    fun startLocationUpdates(newRequest: LocationRequest) {
        locationRequest = newRequest
        if (!serviceScope.isActive) {
            serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
        mainViewModel.showLoadingLocationUIState()
        locationClient.getLocationUpdates(locationRequest)
            .catch { exception ->
                if (exception is LocationClient.GpsLocationException) {
                    getCurrentLocationSettings(exception.locationRequest)
                } else if (exception is LocationClient.PermissionLocationException) {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
                stopLocationUpdates()
            }.onEach { location ->
                mainViewModel.captureLocationLatLng(location)
            }.launchIn(serviceScope)
    }

    private fun getCurrentLocationSettings(request: LocationRequest) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intent = IntentSenderRequest.Builder(exception.resolution).build()
                        gpsLauncher.launch(intent)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore Error
                    }
                }
            }
            .addOnSuccessListener {
                startLocationUpdates(locationRequest)
            }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.checkPreferences()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = DefaultLocationClient(
            application,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        mainViewModel.checkFromDeeplink(intent)
        askNotificationPermission()
        setContent {
            val uiState = mainViewModel.locationUiState
            LaunchedEffect(uiState.showMockLocationBlock) {
                uiState.showMockLocationBlock?.consumeOnce {
                    serviceScope.cancel()
                }
            }
            TemanUlaUserAppTheme {
                ScaffoldNavHost(preference, mainViewModel, this, json)
            }
        }
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Akses Notifikasi Berhasil", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Akses Notifikasi DiTolak", Toast.LENGTH_LONG).show()
        }
    }
}



@Composable
private fun RenderState(state: PermissionState, navigator: NavHostController) {
    when (state) {
        PermissionState.LocationDenied -> {
            navigator.navigate("https://mitra.com/location".toUri())
        }

        PermissionState.StartLocationPermission -> {
        }

        else -> Unit
    }
}

val SupportedNavigationDestination = listOf(
    FoodHomeScreenDestination, FoodOrderListScreenDestination, MenuScreenDestination,
    MyAccountScreenDestination
)
private val Destination.shouldShowScaffoldElements
    get() = SupportedNavigationDestination.contains(this)

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
private fun ScaffoldNavHost(
    preference: Preference,
    mainViewModel: MainViewModel,
    activity: MainActivity,
    json: Json
) {
    val engine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        )
    )
    var isShow by remember { mutableStateOf(true) }
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    val uiState = mainViewModel.locationUiState
    val navController = engine.rememberNavController()
    val userInfo = runBlocking { preference.getUserInfo.first() }
    val isSeenOnBoardingPage = runBlocking { preference.getHasSeenOnBoarding.first() }
    val packageName = BuildConfig.APPLICATION_ID

    LaunchedEffect(Unit) {
        delay(3.seconds)
        isShow = false
        mainViewModel.checkIsAppUpToDate()
    }

    KeepScreenOn(activity)

    val startRoute = if (!isSeenOnBoardingPage) {
        if (mainViewModel.locationUiState.successGetReferral.isNotNullOrEmpty()) {
            RegistrationPhoneScreenDestination
        } else {
            NavGraphs.root.startRoute
        }
    } else if (userInfo.isNotEmpty()) {
        val userInfoData = json.decodeFromString<UserInfo>(userInfo)
        with(FirebaseCrashlytics.getInstance()) {
            setUserId(userInfoData.userId)
            setCrashlyticsCollectionEnabled(true)
        }
        if (userInfoData.isVerified && userInfoData.userKycStatus == UserKycStatus.APPROVED) {
            if (userInfoData.restaurantInfo != null) {
                activity.stopLocationUpdates()
                FoodHomeScreenDestination
            } else {
                TransportScreenDestination
            }
        } else {
            if (userInfoData.isVerified) {
                RegistrationMitraSelectionScreenDestination
            } else {
                if (mainViewModel.locationUiState.successGetReferral.isNotNullOrEmpty()) {
                    RegistrationPhoneScreenDestination
                } else {
                    LoginScreenDestination
                }
            }
        }
    } else {
        if (mainViewModel.locationUiState.successGetReferral.isNotNullOrEmpty()) {
            RegistrationPhoneScreenDestination
        } else {
            LoginScreenDestination
        }
    }
    RenderState(state, navController)

    LaunchedEffect(key1 = uiState.logoutUser, block = {
        uiState.logoutUser?.consumeOnce {
            navController.navigate(LoginScreenDestination) {
                popUpTo(NavGraphs.root)
                launchSingleTop
            }
        }
    })

    if (uiState.isNeedUpdate.orFalse()) {
        UpdatePage {
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    } else {
        if (isShow) {
            SplashScreen()
        } else {
            ScaffoldWrapper(
                startRoute = startRoute,
                navController = navController,
                bottomBar = { destination ->
                    if (destination.shouldShowScaffoldElements) {
                        BottomBar(navController = navController, destination)
                    }
                },
                content = {
                    DestinationsNavHost(
                        engine = engine,
                        navController = navController,
                        navGraph = NavGraphs.root,
                        modifier = Modifier.padding(it),
                        startRoute = startRoute,
                        dependenciesContainerBuilder = {
                            // declare all shared view model that is tight to activity in here
                            dependency(hiltViewModel<MainViewModel>(activity))
                            dependency(hiltViewModel<RegistrationFoodViewModel>(activity))
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun ScaffoldWrapper(
    startRoute: Route,
    navController: NavHostController,
    bottomBar: @Composable (Destination) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val destination =
        navController.appCurrentDestinationAsState().value ?: startRoute.startAppDestination
    val navBackStackEntry = navController.currentBackStackEntry

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(Theme.dimension.size_16dp)
    ) {
        Scaffold(
            bottomBar = { bottomBar(destination) },
            content = content
        )
    }
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(UiColor.white),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = R.drawable.ic_splash,
            modifier = Modifier
                .padding()
                .size(280.dp),
            imageOptions = ImageOptions(contentScale = ContentScale.Fit)
        )
    }
}

@Composable
fun KeepScreenOn(activity: Activity) {
    DisposableEffect(Unit) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}