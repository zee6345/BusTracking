package com.app.bustracking.presentation.views.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
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
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


private val TAG = RoutesMapFragment::class.simpleName.toString()

class RoutesMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentRoutesMapBinding
    private lateinit var routeMapModalSheet: RouteMapModalSheet
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    var mContext: Context? = null
    private val stopList = mutableListOf<Stop>()

    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"
    private val LINE_SOURCE_ID = "LINE_SOURCE_ID"
    private val LINE_LAYER_ID = "LINE_LAYER_ID"
    private val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()

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

        binding.mapView.getMapAsync(this)

        stopList.apply {
            clear()
            addAll(route.stop)
        }


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

        val routeData = route
        val points = ArrayList<Point>()

        for (stop in routeData.stop) {
            val lat = stop.lat.toDouble()
            val lng = stop.lng.toDouble()
            points.add(Point.fromLngLat(lng, lat))
        }

//        val lineString = LineString.fromLngLats(points)

//        binding.mapboxMap?.addPolyline(
//            LineOptions()
//                .withLatLngs(lineString.coordinates)
//                .withLineColor(Color.parseColor("#FF5733")) // Set the color of the polyline
//        )

        val lineString = LineString.fromLngLats(
            symbolLayerIconFeatureList.map { feature ->
                feature.geometry() as Point
            }
        )

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


//        val lineString = LineString.fromLngLats(symbolLayerIconFeatureList.map { feature -> feature.geometry() as Point })

        val coordinates = mutableListOf(
            LatLng(-33.213144, -57.225365),
            LatLng(-33.981818, -54.14164),
            LatLng(-30.583266, -56.990533)
        )

        // Convert the LatLng coordinates to Point objects
        val points = coordinates.map { Point.fromLngLat(it.longitude, it.latitude) }

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
                    .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
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

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



}