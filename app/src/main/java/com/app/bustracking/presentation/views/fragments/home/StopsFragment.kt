package com.app.bustracking.presentation.views.fragments.home

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.bustracking.databinding.FragmentStopsBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.optimization.v1.MapboxOptimization
import com.mapbox.api.optimization.v1.models.OptimizationResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val GEOJSON_SOURCE_ID = "line"
private const val ZOOM = 14.0
private val TAG = StopsFragment::class.simpleName.toString()

class StopsFragment : BaseFragment(), OnMapReadyCallback, MapboxMap.OnMapClickListener,
    MapboxMap.OnMapLongClickListener {

    private lateinit var binding: FragmentStopsBinding
    private lateinit var navController: NavController
    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"
    private val LINE_SOURCE_ID = "LINE_SOURCE_ID"
    private val LINE_LAYER_ID = "LINE_LAYER_ID"
    private val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()
    private val ICON_GEOJSON_SOURCE_ID = "icon-source-id"
    private val FIRST = "first"
    private val ANY = "any"
    private val TEAL_COLOR = "#23D2BE"
    private val POLYLINE_WIDTH = 5f
    private val mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null

    //    private var optimizedRoute: DirectionsRoute? = null
    private var optimizedClient: MapboxOptimization? = null
    private val stops = mutableListOf<Point>()
    private var origin: Point? = null
//    private lateinit var navigationMapRoute: NavigationMapRoute


    //    private lateinit var routeMapModalSheet: RouteMapModalSheet
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var annotationsAdded = false
    var mContext: Context? = null

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
    ): View {
        binding = FragmentStopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add the origin Point to the list
        addFirstStopToStopsList();

        binding.mapView.getMapAsync(this)


    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap


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

        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-57.560533, -31.583266)
            )
        )

        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-58.990533, -31.483266)
            )
        )

        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-57.260533, -31.583266)
            )
        )

        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-58.100533, -31.483266)
            )
        )


//        val lineString = LineString.fromLngLats(symbolLayerIconFeatureList.map { feature -> feature.geometry() as Point })

        val coordinates = mutableListOf(
            LatLng(-33.213144, -57.225365),
            LatLng(-33.981818, -54.14164),
            LatLng(-30.583266, -56.990533),
            LatLng(-31.583266, -57.560533),
            LatLng(-31.483266, -58.990533),
        )

        // Convert the LatLng coordinates to Point objects
        val points = coordinates.map { Point.fromLngLat(it.longitude, it.latitude) }

        // Create a LineString from the list of points
        val lineString = LineString.fromLngLats(points)

        // Create a GeoJSON Feature with the LineString
        val feature = Feature.fromGeometry(lineString)

        // Create a FeatureCollection with the Feature
        val featureCollection = FeatureCollection.fromFeature(feature)


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
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                            )
                    )

                    //poly line
                    .withSource(GeoJsonSource(LINE_SOURCE_ID, Feature.fromGeometry(lineString)))
                    .withLayer(
                        LineLayer(LINE_LAYER_ID, LINE_SOURCE_ID)
                            .withProperties(
                                lineColor(Color.parseColor("#e55e5e")),
                                lineWidth(2f)
                            )
                    )


            ) {
                initMarkerIconSymbolLayer(it)
                initOptimizedRouteLineLayer(it)
                mapboxMap.addOnMapClickListener(this)
                mapboxMap.addOnMapLongClickListener(this)

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun initMarkerIconSymbolLayer(loadedMapStyle: Style) {
        // Add the marker image to map
        loadedMapStyle.addImage(
            "icon-image", BitmapFactory.decodeResource(
                this.resources, com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
            )
        )

        // Add the source to the map
        loadedMapStyle.addSource(
            GeoJsonSource(
                ICON_GEOJSON_SOURCE_ID,
                Feature.fromGeometry(Point.fromLngLat(origin!!.longitude(), origin!!.latitude()))
            )
        )
        loadedMapStyle.addLayer(
            SymbolLayer("icon-layer-id", ICON_GEOJSON_SOURCE_ID).withProperties(
                iconImage("icon-image"),
                iconSize(1f),
                iconAllowOverlap(true),
                iconIgnorePlacement(true),
                iconOffset(arrayOf<Float>(0f, -7f))
            )
        )
    }

    private fun initOptimizedRouteLineLayer(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource("optimized-route-source-id"))
        loadedMapStyle.addLayerBelow(
            LineLayer("optimized-route-layer-id", "optimized-route-source-id")
                .withProperties(
                    lineColor(Color.parseColor(TEAL_COLOR)),
                    lineWidth(POLYLINE_WIDTH)
                ), "icon-layer-id"
        )
    }

    override fun onMapClick(point: LatLng): Boolean {
        // Optimization API is limited to 12 coordinate sets
        if (alreadyTwelveMarkersOnMap()) {

        } else {
            val style = mapboxMap!!.style
            if (style != null) {
                addDestinationMarker(style, point)
                addPointToStopsList(point)
                getOptimizedRoute(style, stops)
            }
        }
        return true
    }

    override fun onMapLongClick(point: LatLng): Boolean {
        stops.clear()
        if (mapboxMap != null) {
            val style = mapboxMap!!.style
            if (style != null) {
                resetDestinationMarkers(style)
                removeOptimizedRoute(style)
                addFirstStopToStopsList()
                return true
            }
        }
        return false
    }

    private fun resetDestinationMarkers(style: Style) {
        val optimizedLineSource = style.getSourceAs<GeoJsonSource>(ICON_GEOJSON_SOURCE_ID)
        optimizedLineSource?.setGeoJson(Point.fromLngLat(origin!!.longitude(), origin!!.latitude()))
    }

    private fun removeOptimizedRoute(style: Style) {
        val optimizedLineSource = style.getSourceAs<GeoJsonSource>("optimized-route-source-id")
        optimizedLineSource?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
    }

    private fun alreadyTwelveMarkersOnMap(): Boolean {
        return stops.size == 12
    }

    private fun addDestinationMarker(style: Style, point: LatLng) {
        val destinationMarkerList: MutableList<Feature> = ArrayList()
        for (singlePoint in stops) {
            destinationMarkerList.add(
                Feature.fromGeometry(
                    Point.fromLngLat(singlePoint.longitude(), singlePoint.latitude())
                )
            )
        }
        destinationMarkerList.add(
            Feature.fromGeometry(
                Point.fromLngLat(
                    point.longitude,
                    point.latitude
                )
            )
        )
        val iconSource = style.getSourceAs<GeoJsonSource>(ICON_GEOJSON_SOURCE_ID)
        iconSource?.setGeoJson(FeatureCollection.fromFeatures(destinationMarkerList))
    }

    private fun addPointToStopsList(point: LatLng) {
        stops.add(Point.fromLngLat(point.longitude, point.latitude))
    }

    private fun addFirstStopToStopsList() {
        // Set first stop
        origin = Point.fromLngLat(30.335098600000038, 59.9342802)
        stops.add(origin!!)
    }

    private fun getOptimizedRoute(style: Style, coordinates: List<Point>) {
        optimizedClient = MapboxOptimization.builder()
            .source(FIRST)
            .destination(ANY)
            .coordinates(coordinates)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(
                (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(
                    com.app.bustracking.R.string.mapbox_access_token
                ))!!
            ).build()


        optimizedClient?.enqueueCall(object : Callback<OptimizationResponse> {
            override fun onResponse(
                call: Call<OptimizationResponse>,
                response: Response<OptimizationResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body() != null) {

                        val routes = response.body()!!.trips()

                        if (routes != null) {
                            if (routes.isNotEmpty()) {
                                // Get most optimized route from API response
                                val optimizedRoute = routes[0]
                                drawOptimizedRoute(style, optimizedRoute)
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "onResponseError: ${response.errorBody()!!.string()}")
                }
            }

            override fun onFailure(call: Call<OptimizationResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun drawOptimizedRoute(style: Style, route: DirectionsRoute) {
        val optimizedLineSource = style.getSourceAs<GeoJsonSource>("optimized-route-source-id")
        optimizedLineSource?.setGeoJson(
            FeatureCollection.fromFeature(
                Feature.fromGeometry(
                    LineString.fromPolyline(route.geometry()!!, PRECISION_6)
                )
            )
        )
    }

}