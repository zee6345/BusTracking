package com.app.bustracking.presentation.views.fragments.home;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.app.bustracking.R;
import com.app.bustracking.app.AppService;
import com.app.bustracking.data.local.RoutesDao;
import com.app.bustracking.data.local.StopsDao;
import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.databinding.FragmentStopsMapBinding;
import com.app.bustracking.presentation.ui.RoutesMapAdapter;
import com.app.bustracking.presentation.ui.StopMapTimeAdapter;
import com.app.bustracking.presentation.views.fragments.BaseFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StopsMapFragment extends BaseFragment {

    public static final String TAG = StopsMapFragment.class.getSimpleName();
    public static String ARGS = "data";
    public static double lat = 0.0;
    public static double lng = 0.0;
    MapboxMap mapbox;
    List<LatLng> coordinatesList = new ArrayList<>();
    SymbolManager symbolManager;
    Symbol locationMarker;
    int stopId = 0;
    RoutesDao routesDao;
    StopsDao stopsDao;
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    BottomSheetBehavior<LinearLayout> bottomStopTimeSheet = null;
    private NavController navController;
    private FragmentStopsMapBinding binding;
    private MapView mapView;
    private Double latitudeBus = 31.5300229;
    private Double longitudeBus = 74.3077318;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("My_Action_Event")) {
                String jsonData = intent.getStringExtra("json_data");
                if (jsonData != null && !jsonData.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String data = jsonObject.getString("data");
                        String location = new JSONObject(data).getString("location");
                        double lat = new JSONObject(location).getDouble("lat");
                        double lon = new JSONObject(location).getDouble("long");

                        latitudeBus = lat;
                        longitudeBus = lon;

                        locationMarker.setLatLng(new LatLng(lat, lon));
                        symbolManager.update(locationMarker);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (bottomStopTimeSheet != null && (bottomStopTimeSheet.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomStopTimeSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)) {

                    bottomStopTimeSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

//                    this.remove();
                } else {
                    navController.navigate(R.id.stopsFragment);
                }
            }
        });

    }

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStopsMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Mapbox.getInstance(requireActivity(), getString(com.app.bustracking.R.string.mapbox_access_token));

        IntentFilter filter = new IntentFilter(AppService.RECEIVER_ACTION);
        requireActivity().registerReceiver(broadcastReceiver, filter);

        //init dao
        routesDao = getAppDb().routesDao();
        stopsDao = getAppDb().stopsDao();


        mapView = binding.mapBoxView;
        mapView.onCreate(savedInstanceState);

        try {
            stopId = getArguments().getInt(RoutesFragmentKt.ARGS);

            //reset
            StopsFragment.Companion.setARGMAIN(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //fetch data from db

        Stop fetchedStop = stopsDao.fetchStop(stopId);

        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
                    style -> {
                        this.mapbox = mapboxMap;

//                        mapboxMap.setMinZoomPreference(6); // Set your minimum zoom level
//                        mapboxMap.setMaxZoomPreference(12);

                        LocationComponent locationComponent = mapboxMap.getLocationComponent();
                        locationComponent.activateLocationComponent(requireActivity(), style);
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.setRenderMode(RenderMode.NORMAL);


                        // Assuming routeList is a list of routes with stops
                        List<Route> routeList = routesDao.fetchAllRoutes(); // Replace with your actual method to get routes
                        for (Route route : routeList) {
                            if (route != null && route.getStop() != null && !route.getStop().isEmpty()) {
                                drawRouteOnMap(mapboxMap, style, route);

                                //animate camera
                                for (Stop stop : route.getStop()) {
                                    coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
                                }
                            }
                        }

                        animateCamera(mapboxMap, fetchedStop);

                    });

            mapboxMap.addOnMapClickListener(point -> {

                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));


                LatLng closestCoordinate = getClosestCoordinate(point);
                if (closestCoordinate != null) {
                    Stop stop = stopsDao.stopByLatLng(closestCoordinate.getLatitude(), closestCoordinate.getLongitude());

                    //refresh bottom sheet
                    try {
                        handleBottomSheet(stop);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            });
        });


        try {
            handleBottomSheet(fetchedStop);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            //
            LatLng latLng = new LatLng(lat, lng);
            // Padding to control the space around the bounds (in pixels)
            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } catch (Exception e) {

        }


        binding.fabCameraView.setOnClickListener(v -> {
            animateCamera(mapbox, coordinatesList);
        });

        binding.fabBack.setOnClickListener(v0 -> {
            if (bottomStopTimeSheet != null && (bottomStopTimeSheet.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomStopTimeSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)) {

                bottomStopTimeSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

            } else {
                navController.navigate(R.id.stopsFragment);
            }
        });
    }


    private void handleBottomSheet(Stop stops) throws Exception {
        List<LatLng> coordinatesList = new ArrayList<>();

        TypedValue typedValue = new TypedValue();
        int actionBarHeight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        int maxBottomSheetHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight;


        /**
         * show stop with route title
         */
        bottomSheetBehavior = BottomSheetBehavior.from(binding.llStop.bottomLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setPeekHeight(maxBottomSheetHeight);


        binding.llStop.rvMapRoutes.setHasFixedSize(true);
        binding.llStop.rvMapRoutes.setAdapter(new RoutesMapAdapter(Collections.singletonList(stops), stopsDao, (stop, integer) -> {
            animateCamera(mapbox, stop);
            return null;
        }));


        /**
         * show stop time for route
         */

        bottomStopTimeSheet = BottomSheetBehavior.from(binding.llStopTime.bottomTimeSheet);
        bottomStopTimeSheet.setHideable(true);
        bottomStopTimeSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomStopTimeSheet.setPeekHeight(maxBottomSheetHeight);


        int routeId = stopsDao.fetchRouteId(stops.getStopId());
        Route route = routesDao.fetchRouteById(routeId);

        for (Stop stop : route.getStop()) {
            coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
        }


        binding.llStop.tvTitle.setVisibility(route.getRoute_title() == null ? View.GONE : View.VISIBLE);
        binding.llStop.tvTitle.setText(route.getRoute_title());

        binding.llStop.tvDesc.setVisibility(route.getDescription() == null ? View.GONE : View.VISIBLE);
        binding.llStop.tvDesc.setText(route.getDescription());

        binding.llStop.llRoute.setOnClickListener(v -> {

            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            bottomStopTimeSheet.setState(BottomSheetBehavior.STATE_EXPANDED);


            //animate whole view
            animateCamera(mapbox, coordinatesList);

        });

        binding.llStopTime.tvStopTimes.setHasFixedSize(true);
        binding.llStopTime.tvStopTimes.setAdapter(new StopMapTimeAdapter(route.getStop(), (stop, integer) -> null));

    }

    private void animateCamera(MapboxMap mapboxMap, List<LatLng> coordinatesList) {
        try {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (LatLng latLng : coordinatesList) {
//                builder.include(latLng);
//            }
//
//            LatLngBounds bounds = builder.build();
//
//            // Padding to control the space around the bounds (in pixels)
//            int padding = 100;
//            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(coordinatesList.get(0))
                                    .zoom(11.5)
                                    .build()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawRouteOnMap(final MapboxMap mapboxMap, final Style style, final Route route) {
        final List<LatLng> coordinator = new ArrayList<>();
        final List<Feature> featuresList = new ArrayList<>();
        final List<Point> pointsList = new ArrayList<>();

        // Assuming route.getStop() contains a list of Stop objects with latitude and longitude
        List<Stop> stops = route.getStop();
        List<Point> stopPoints = new ArrayList<>();

        SymbolManager symbolManager = new SymbolManager(binding.mapBoxView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);

        // Add a marker at the initial position
//        style.addImage("icon-id-" + route.hashCode(), BitmapFactory.decodeResource(
//                requireActivity().getResources(),
//                com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
//        ));

        style.addImage("icon-id-" + route.hashCode(), requireActivity().getDrawable(R.drawable.ic_location_marker));


        for (Stop stop : stops) {
            LatLng latLng = new LatLng(Double.parseDouble(Objects.requireNonNull(stop.getLat())), Double.parseDouble(Objects.requireNonNull(stop.getLng())));
            coordinator.add(latLng);
            featuresList.add(Feature.fromGeometry(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude())));
            pointsList.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
            stopPoints.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
        }


        // Add a GeoJson source for markers
        style.addSource(new GeoJsonSource("source-id-" + route.hashCode(), FeatureCollection.fromFeatures(featuresList)));
        // Add a SymbolLayer to display markers
        style.addLayer(new SymbolLayer("layer-id-" + route.hashCode(), "source-id-" + route.hashCode())
                .withProperties(
                        iconImage("icon-id-" + route.hashCode()),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true)
                )
        );


        MapboxDirections directionsClient = MapboxDirections.builder()
                .origin(Point.fromLngLat(pointsList.get(0).longitude(), pointsList.get(0).latitude()))
                .destination(Point.fromLngLat(pointsList.get(pointsList.size() - 1).longitude(), pointsList.get(pointsList.size() - 1).latitude()))
                .accessToken(getString(R.string.mapbox_access_token))
                .overview("full")
                .profile("driving-traffic")
                .steps(true)
                .build();


        // Request directions for the stops
        directionsClient.enqueueCall(new Callback<>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                try {
                    if (response.body() != null && !response.body().routes().isEmpty()) {
                        DirectionsRoute directionsRoute = response.body().routes().get(0);

                        //
                        LineString lineString = LineString.fromPolyline(directionsRoute.geometry(), 6);
                        List<Point> points = lineString.coordinates();
                        GeoJsonSource geoJsonSource = new GeoJsonSource("route-source-" + route.hashCode(), FeatureCollection.fromFeatures(new Feature[]{
                                Feature.fromGeometry(LineString.fromLngLats(points))
                        }));

                        style.addSource(geoJsonSource);

                        // Add layer
                        style.addLayer(new LineLayer("route-layer-" + route.hashCode(),
                                "route-source-" + route.hashCode())
                                .withProperties(
                                        PropertyFactory.lineWidth(6f),
                                        PropertyFactory.lineColor(Color.parseColor(route.getColor()))
                                ));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }

    private void animateCamera(MapboxMap mapboxMap, Stop stop) {
        LatLng latLng = new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng()));
        // Padding to control the space around the bounds (in pixels)
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }

//        requireActivity().unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private boolean isPointCloseToAnyCoordinate(LatLng point) {
        // Define a threshold distance for proximity check
        double thresholdDistance = 0.01; // Adjust as needed

        // Check if the clicked point is close to any of your coordinates
        for (LatLng coordinate : coordinatesList) {
            double distance = calculateDistance(point, coordinate);
            if (distance < thresholdDistance) {
                return true; // The point is close to at least one coordinate
            }
        }
        return false; // The point is not close to any coordinate
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        // You can use a suitable formula to calculate the distance between two LatLng points
        // For simplicity, a basic formula is used here (not suitable for large distances)
        double latDiff = point1.getLatitude() - point2.getLatitude();
        double lonDiff = point1.getLongitude() - point2.getLongitude();
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    private LatLng getClosestCoordinate(LatLng point) {
        // Define a threshold distance for proximity check
        double thresholdDistance = 0.01; // Adjust as needed

        // Initialize variables to keep track of the closest coordinate
        LatLng closestCoordinate = null;
        double closestDistance = Double.MAX_VALUE;

        // Check if the clicked point is close to any of your coordinates
        for (LatLng coordinate : coordinatesList) {
            double distance = calculateDistance(point, coordinate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCoordinate = coordinate;
            }
        }

        // Check if the closest distance is within the threshold
        if (closestDistance < thresholdDistance) {
            return closestCoordinate; // Return the closest coordinate if within the threshold
        } else {
            return null; // Return null if no match is found within the threshold
        }
    }
}
