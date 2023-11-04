package com.app.bustracking.presentation.views.fragments.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.databinding.FragmentMapsBinding
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Converter
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.R
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


private val TAG = MapsFragment::class.simpleName.toString()

class MapsFragment : BaseFragment(), OnMapReadyCallback, PermissionsListener {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var navController: NavController

    //    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    private val data: AppViewModel by viewModels()


    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView: MapView
    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"
    private val symbolLayerIconFeatureList: ArrayList<Feature> = ArrayList()
    private val coordinatesList: ArrayList<LatLng> = ArrayList()


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
//        val agentId = AppPreference.getInt("agent_route_id")
//        data.getTravelRouteList(RouteRequest(agentId))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvTitle.text = "Maps"


        Mapbox.getInstance(
            requireActivity(),
            getString(com.app.bustracking.R.string.mapbox_access_token)
        )

        binding.mapBoxView.getMapAsync(this)


        val data = AppPreference.getString("routeList")
        val obj = Converter.fromJson(data, GetTravelRoutes::class.java)


        obj.route_list.forEach {
            if (it.stop.isNotEmpty()) {
                it.stop.forEach { stop ->

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
            } else {

//                requireActivity().onBackPressed()
            }
        }


    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        if (coordinatesList.isNotEmpty()) {

            mapboxMap.setStyle(
                Style.MAPBOX_STREETS
            ) { style ->
                /*
                 *current user location
                 */
//                enableLocationComponent(style)

                /*
                 * add markers for stops
                 */
                // Create a SymbolLayer for markers
                style.addImage(
                    ICON_ID, BitmapFactory.decodeResource(
                        requireActivity().resources,
                        R.drawable.mapbox_marker_icon_default
                    )
                )


                // Add a GeoJson source for markers
                style.addSource(
                    GeoJsonSource(
                        SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
                    )
                )

                // Add a SymbolLayer to display markers
                style.addLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                            iconImage(ICON_ID),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                        )
                )

                /*
                 * move camera to all stops
                 */
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
//            navController.navigate(R.id.action_driverMap_to_driverMapDetails)
                true
            }
        } else {
            Toast.makeText(requireActivity(), "No stop available!", Toast.LENGTH_SHORT).show()

            requireActivity().finish()
        }
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            val customLocationComponentOptions = LocationComponentOptions.builder(requireActivity())
                .pulseEnabled(true)
                .build()

            // Get an instance of the component
            val locationComponent = mapboxMap!!.locationComponent

            // Activate with options
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()
            )

            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent.renderMode = RenderMode.NORMAL
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(requireActivity())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(
            requireActivity(),
            "Please allow location permission to use this app!",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(
                requireActivity(),
                "Please allow location permission to use this app!",
                Toast.LENGTH_LONG
            ).show()
        }
    }


}