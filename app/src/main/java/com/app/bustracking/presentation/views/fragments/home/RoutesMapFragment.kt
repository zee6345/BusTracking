package com.app.bustracking.presentation.views.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.FragmentRoutesMapBinding
import com.app.bustracking.presentation.model.Routes
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.presentation.views.fragments.bottomsheets.RouteMapModalSheet
import com.app.bustracking.utils.Converter
import com.app.bustracking.utils.SharedModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style

private const val GEOJSON_SOURCE_ID = "line"
private const val ZOOM = 16.0
private val TAG = RoutesMapFragment::class.simpleName.toString()
class RoutesMapFragment : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentRoutesMapBinding
    private lateinit var routeMapModalSheet: RouteMapModalSheet
    private val sharedModel:SharedModel by viewModels()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    var mContext: Context? = null
    private val stopList = mutableListOf<Stop>()

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

        route.stop.forEach {
            stopList.apply {
                clear()
                addAll(route.stop)
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        routeMapModalSheet = RouteMapModalSheet(route)
        routeMapModalSheet.show(requireActivity().supportFragmentManager, routeMapModalSheet.tag)


        binding.mapView.getMapboxMap()

//        binding.mapView.setOnClickListener {
//            if (!routeMapModalSheet.isVisible) {
//                routeMapModalSheet.show(
//                    requireActivity().supportFragmentManager,
//                    routeMapModalSheet.tag
//                )
//            }
//        }

        // getRoute()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
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
        DrawGeoJson()

    }

    private fun DrawGeoJson() {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(
                Point.fromLngLat(
                    longitude,
                    latitude
                )
            ).zoom(ZOOM).build()
        )

        binding.mapView.getMapboxMap().loadStyle(
            (
                    style(styleUri = Style.MAPBOX_STREETS) {
                        +geoJsonSource(GEOJSON_SOURCE_ID) {
                            url("asset://from_crema_to_council_crest.geojson")
                        }
                        +lineLayer("linelayer", GEOJSON_SOURCE_ID) {
                            lineCap(LineCap.ROUND)
                            lineJoin(LineJoin.ROUND)
                            lineOpacity(0.7)
                            lineWidth(8.0)
                            lineColor("#C50000")
                        }
                    }
                    )
        )
    }

}