package com.app.bustracking.presentation.views.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.databinding.FragmentMapsBinding
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


private val TAG = MapsFragment::class.simpleName.toString()

class MapsFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var navController: NavController
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    private val data: AppViewModel by viewModels()
//    private val dataList = mutableListOf<Route>()

    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"
    private val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        getLocation()
        val agentId = AppPreference.getInt("agent_route_id")
        data.getTravelRouteList(RouteRequest(agentId))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvTitle.text = "Maps"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


//        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { _ -> }

//        getRoute()


        data.getTravelRoutes.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }

                is DataState.Error -> {

                }

                is DataState.Success -> {

                    val data = it.data as GetTravelRoutes

//                    data.route_list.forEach {route->
//                        route.stop.forEach { stop->
//                            symbolLayerIconFeatureList.add(
//                                Feature.fromGeometry(
//                                    Point.fromLngLat(stop.lng.toDouble(), stop.lat.toDouble())
//                                )
//                            )
//
//                        }
//                    }


//                    dataList.apply {
//                        clear()
//                        dataList.addAll(data.route_list)
//                    }

                }

                else -> {}
            }

        }




        binding.mapView.getMapAsync(this)

//        binding.mapView.getMapAsync { mapboxMap ->
//            // Configure the map's initial position
//            val initialLatLng = LatLng(-33.213144, -57.225365) // New York City coordinates
//            val initialPosition = CameraPosition.Builder()
//                .target(initialLatLng)
//                .zoom(12.0) // You can adjust the zoom level
//                .tilt(0.0)  // Optional: Set the camera tilt
//                .bearing(0.0)  // Optional: Set the camera bearing
//                .build()
//
//            // Move the camera to the initial position
//            mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newCameraPosition(initialPosition))
//
//            // Set the map style (you can use your preferred style)
//            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
//                // Style is loaded, you can customize the map further here
//            }
//        }

    }

//    private fun checkPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//        return false
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            1010
//        )
//    }
//
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == 1010) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getLocation()
//            }
//        }
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLocationRequest: com.google.android.gms.location.LocationRequest? = null
    var mLocationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission", "SetTextI18n")
//    private fun getLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//
//                locationrequestfunct()
//
//                mLocationCallback = object : LocationCallback() {
//                    override fun onLocationResult(locationResult: LocationResult) {
//                        latitude = locationResult.lastLocation!!.latitude
//                        longitude = locationResult.lastLocation!!.longitude
//
//
//                        if (!annotationsAdded) {
//                            annotationsAdded = true
//
////                            addAnnotationToMap()
//
//                        }
//                    }
//                }
//                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//                mFusedLocationClient.requestLocationUpdates(
//                    mLocationRequest!!,
//                    mLocationCallback!!,
//                    Looper.myLooper()
//                );
//
//            } else {
//                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
//                    .show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermissions()
//        }
//    }

    private fun locationrequestfunct() {
        mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 10000
        mLocationRequest!!.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
    }


//    private fun addAnnotationToMap() {
//
//        binding.mapView.location2.enabled = true
//        binding.mapView.location2.pulsingEnabled = true
//        binding.mapView.location2.locationPuck = LocationPuck2D(
//            null,
//            bearingImage = AppCompatResources.getDrawable(
//                requireContext(),
//                R.drawable.abc
//            ),
//        )
//        Log.e("error", "$latitude $longitude")
//
//        val mapAnimationOptions = MapAnimationOptions.Builder().build()
//        binding.mapView.camera.easeTo(
//            CameraOptions.Builder().center(Point.fromLngLat(longitude, latitude))
//                // .padding(EdgeInsets(500.0, 0.0, 0.0, 0.0))
//                .build(),
//            mapAnimationOptions
//        )
//
//
//        dataList.forEach {route->
//            bitmapFromDrawableRes(
//                requireActivity(),
//                R.drawable.ic_marker
//            )?.let {
//                val annotationApi = binding.mapView.annotations
//                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
//                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
//                    .withPoint(Point.fromLngLat(route.longitude.toDouble(), route.latitude.toDouble()))
//                    .withIconImage(it)
//                pointAnnotationManager.create(pointAnnotationOptions)
//            }
//        }
//
//        // Add 5 random points around your current location
////        for (i in 1..5) {
////            val offsetLatitude = (Math.random() - 0.5) * 0.01 // Adjust the offset range as needed
////            val offsetLongitude = (Math.random() - 0.5) * 0.01 // Adjust the offset range as needed
////
////            val latitude = latitude + offsetLatitude
////            val longitude = longitude + offsetLongitude
////
////            bitmapFromDrawableRes(
////                requireActivity(),
////                R.drawable.ic_marker
////            )?.let {
////                val annotationApi = binding.mapView.annotations
////                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
////                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
////                    .withPoint(Point.fromLngLat(longitude, latitude))
////                    .withIconImage(it)
////                pointAnnotationManager.create(pointAnnotationOptions)
////            }
////        }
//    }


    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }

        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-57.225365, -33.213144)
            )
        )
        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-54.14164, -33.981818)
            )
        )
        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-56.990533, -30.583266)
            )
        )

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
                    ) // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                    // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                    // the coordinate point. This is offset is not always needed and is dependent on the image
                    // that you use for the SymbolLayer icon.
                    .withLayer(
                        SymbolLayer(LAYER_ID, SOURCE_ID)
                            .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                            )
                    )
            ) {
                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    @Throws(ServicesException::class)
//    private fun getRoute() {
//        MapboxDirections.builder()
//            .accessToken(
//                requireContext().resources
//                    .getString(R.string.mapbox_access_token)
//            )
//            .routeOptions(
//                RouteOptions.builder()
//                    .coordinatesList(
//                        listOf(
//                            Point.fromLngLat(40.7128, 74.0060), // origin
//                            Point.fromLngLat(40.71289898898, 74.006088888) // destination
//                        )
//                    )
//                    .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
//                    .overview(DirectionsCriteria.OVERVIEW_FULL)
//                    .build()
//            )
//            .build()
//    }


}