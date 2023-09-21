package com.app.bustracking.presentation.views.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentMapsBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.location2


class MapsFragment : BaseFragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var navController: NavController
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { _ -> }
        getRoute()

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

    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var mLocationRequest: com.google.android.gms.location.LocationRequest? = null
    var mLocationCallback: LocationCallback? = null
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
//                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//                    val location: Location? = task.result
//                    if (location != null) {
//                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
//                        val list: List<Address> =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
////                        mainBinding.apply {
////                            tvLatitude.text = "Latitude\n${list[0].latitude}"
////                            tvLongitude.text = "Longitude\n${list[0].longitude}"
////                            tvCountryName.text = "Country Name\n${list[0].countryName}"
////                            tvLocality.text = "Locality\n${list[0].locality}"
////                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
////                        }
//                        latitude =list[0].latitude
//                        longitude = list[0].longitude
//
//                        addAnnotationToMap()
//                    }
//                }
                locationrequestfunct()
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        latitude =locationResult.lastLocation!!.latitude
                        longitude = locationResult.lastLocation!!.longitude


                        if (!annotationsAdded) {
                            annotationsAdded = true
                            addAnnotationToMap()
                        }
                    }
                }
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                mFusedLocationClient.requestLocationUpdates(mLocationRequest!!, mLocationCallback!!, Looper.myLooper());

            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }



//the call back will not work if is not in on create

//the call back will not work if is not in on create

    }

    private fun locationrequestfunct() {
        mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 10000
        mLocationRequest!!.priority = com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
    }


    private fun addAnnotationToMap() {

        binding.mapView.location2.enabled = true
        binding.mapView.location2.pulsingEnabled = true
        binding.mapView.location2.locationPuck = LocationPuck2D(
            null, bearingImage = AppCompatResources.getDrawable(
                requireContext(),
               R.drawable.abc
            ),
        )
        Log.e("error","$latitude $longitude")
        val mapAnimationOptions = MapAnimationOptions.Builder().build()
        binding.mapView.camera.easeTo(
            CameraOptions.Builder().center(Point.fromLngLat(longitude, latitude))
               // .padding(EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build(),
            mapAnimationOptions
        )


        // Add 5 random points around your current location
        for (i in 1..5) {
            val offsetLatitude = (Math.random() - 0.5) * 0.01 // Adjust the offset range as needed
            val offsetLongitude = (Math.random() - 0.5) * 0.01 // Adjust the offset range as needed

            val latitude = latitude + offsetLatitude
            val longitude = longitude + offsetLongitude

            bitmapFromDrawableRes(
                requireActivity(),
                R.drawable.ic_marker
            )?.let {
                val annotationApi = binding.mapView.annotations
                val pointAnnotationManager =
                    annotationApi.createPointAnnotationManager()
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(it)
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }
    }


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

    @Throws(ServicesException::class)
    private fun getRoute(){
        MapboxDirections.builder()
            .accessToken(requireContext().resources
                .getString(R.string.mapbox_access_token))
            .routeOptions(
                RouteOptions.builder()
                    .coordinatesList(listOf(
                        Point.fromLngLat(40.7128, 74.0060), // origin
                        Point.fromLngLat(40.71289898898, 74.006088888) // destination
                    ))
                    .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                    .overview(DirectionsCriteria.OVERVIEW_FULL)
                    .build()
            )
            .build()
    }


}