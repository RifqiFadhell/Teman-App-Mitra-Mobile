package id.teman.app.mitra.service

//class LocationService @Inject constructor(
//    private val
//): Service() {
//
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//    private lateinit var locationClient: LocationClient
//
//    override fun onBind(p0: Intent?): IBinder? {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        locationClient = DefaultLocationClient(this.application,  LocationServices.getFusedLocationProviderClient(applicationContext))
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when (intent?.action) {
//            ACTION_START -> start()
//            ACTION_STOP -> stop()
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private fun start() {
//        locationClient.getLocationUpdates(MainActivity.navigationLocationRequest)
//            .catch { exception ->
//
//            }.onEach {
//
//            }.launchIn(serviceScope)
//    }
//
//    private fun stop() {
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        serviceScope.cancel()
//    }
//
//    companion object {
//        const val ACTION_START = "ACTION_START"
//        const val ACTION_STOP = "ACTION_STOP"
//    }
//}