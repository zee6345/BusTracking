package com.app.bustracking.presentation.views.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.app.AppService
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.FragmentRoutesMapBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.presentation.views.fragments.bottomsheets.RouteMapModalSheet
import com.app.bustracking.utils.Converter
import com.app.bustracking.utils.SharedModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

private val TAG = RoutesMapFragment::class.simpleName.toString()

class RoutesMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentRoutesMapBinding
    private lateinit var routeMapModalSheet: RouteMapModalSheet
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    var mContext: Context? = null
//    private val stopList = mutableListOf<Stop>()

    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"
    private val LINE_SOURCE_ID = "LINE_SOURCE_ID"
    private val LINE_LAYER_ID = "LINE_LAYER_ID"

    //    private val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()
    private val symbolLayerIconFeatureList: ArrayList<Feature> = ArrayList()
    private val coordinatesList: ArrayList<LatLng> = ArrayList()

//    val PUSHER_APP_ID = 1695142
//    val PUSHER_APP_KEY = "1f3eac61c1534d7ca731"
//    val PUSHER_APP_SECRET = "820dcb7d16632e710184"
//
//    val pusherOptions by lazy {
//        PusherOptions().setCluster("ap2")
//    }
//    val pusher by lazy {
//        Pusher(PUSHER_APP_KEY, pusherOptions)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutesMapBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = requireArguments().getString(ARGS)
        val route = Converter.fromJson(data!!, Route::class.java)

        Mapbox.getInstance(
            requireActivity(),
            getString(com.app.bustracking.R.string.mapbox_access_token)
        )
        binding.mapView.getMapAsync(this)

//        stopList.apply {
//            clear()
//            addAll(route.stop)
//        }


//        pusher.connect(this, ConnectionState.ALL)
////        val channel = pusher.subscribe("220.location")
//        val channel = pusher.subscribe("seentul-tracking")
//        channel.bind(route.bus_id.toString(), this)


//        if (!AppService.alreadyRunning) {
            val intent = Intent(requireActivity(), AppService::class.java)
//            intent.action = "com.app.tracking"
            intent.putExtra("bus_id", route.bus_id.toString())
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                requireActivity().startForegroundService(intent)
//            } else {
                requireActivity().startService(intent)
//            }
//        }



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        routeMapModalSheet = RouteMapModalSheet(route)
        routeMapModalSheet.show(requireActivity().supportFragmentManager, routeMapModalSheet.tag)


//        binding.mapView.getMapboxMap()

//        binding.mapView.setOnClickListener {
//            if (!routeMapModalSheet.isVisible) {
//                routeMapModalSheet.show(
//                    requireActivity().supportFragmentManager,
//                    routeMapModalSheet.tag
//                )
//            }
//        }

        // getRoute()


//        val routeData = route
//        val points = ArrayList<Point>()
//
//        for (stop in routeData.stop) {
//            val lat = stop.lat.toDouble()
//            val lng = stop.lng.toDouble()
//            points.add(Point.fromLngLat(lng, lat))
//        }


        route.stop.forEach { stop ->
            symbolLayerIconFeatureList.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        stop.lng.toDouble(),
                        stop.lat.toDouble()
                    )
                )
            )
            coordinatesList.add(
                LatLng(
                    stop.lng.toDouble(),
                    stop.lat.toDouble()
                )
            )
        }


    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1010
        )
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1010) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLocationRequest: com.google.android.gms.location.LocationRequest? = null
    var mLocationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationrequestfunct()
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        latitude = locationResult.lastLocation!!.latitude
                        longitude = locationResult.lastLocation!!.longitude


                        if (!annotationsAdded) {
                            annotationsAdded = true
                            drawPolylineOnMap()
                        }
                    }
                }
                mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback!!,
                    Looper.myLooper()
                );

            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun locationrequestfunct() {
        mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 10000
        mLocationRequest!!.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
    }

    private fun drawPolylineOnMap() {
//        DrawGeoJson(this,mContext!!).execute()
//        DrawGeoJson()

    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        // Convert the LatLng coordinates to Point objects
        val points = coordinatesList.map { Point.fromLngLat(it.longitude, it.latitude) }

        // Create a LineString from the list of points
        val lineString = LineString.fromLngLats(points)

        try {


            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41") // Add the SymbolLayer icon image to the map style
                    .withImage(
                        ICON_ID,
                        BitmapFactory.decodeResource(
                            requireActivity().resources,
                            com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
                        )
                    )
                    // Adding a GeoJson source for the SymbolLayer icons.
                    .withSource(
                        GeoJsonSource(
                            SOURCE_ID,
                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
                        )
                    )
                    .withLayer(
                        SymbolLayer(LAYER_ID, SOURCE_ID)
                            .withProperties(
                                PropertyFactory.iconImage(ICON_ID),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true)
                            )
                    )

                    //poly line
                    .withSource(GeoJsonSource(LINE_SOURCE_ID, Feature.fromGeometry(lineString)))
                    .withLayer(
                        LineLayer(LINE_LAYER_ID, LINE_SOURCE_ID)
                            .withProperties(
                                PropertyFactory.lineColor(Color.parseColor("#e55e5e")),
                                PropertyFactory.lineWidth(2f)
                            )
                    )



            ) {
                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.

                val builder = LatLngBounds.Builder()
                for (latLng in coordinatesList) {
                    builder.include(latLng)
                }
                val bounds = builder.build()
                // Padding to control the space around the bounds (in pixels)
                val padding = 100
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

            }


            mapboxMap.addOnMapClickListener { point ->
                Log.e("mmmTAG", "" + point)
                routeMapModalSheet.show(requireActivity().supportFragmentManager, routeMapModalSheet.tag)
                true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    override fun onConnectionStateChange(change: ConnectionStateChange?) {
//        Log.e("Pusher", "State changed from " + change!!.getPreviousState() + " to " + change!!.getCurrentState())
//
//
//    }
//
//    override fun onError(message: String?, code: String?, e: Exception?) {
//        Log.e("Pusher", "There was a problem connecting! " +
//                "\ncode: " + code +
//                "\nmessage: " + message +
//                "\nException: " + e
//        )
//    }
//
//    override fun onEvent(event: PusherEvent?) {
//        Log.e("Pusher", "Received event with data: " + event.toString());
//    }


}