package com.app.bustracking.presentation.views.fragments.home;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.app.bustracking.utils.OnLocationReceive;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StopsMapFragment extends BaseFragment implements OnLocationReceive {

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
    Double latitude = 0.0;
    Double longitude = 0.0;
    final List<Point> pointsList = new ArrayList<>();
    double minLat = Double.POSITIVE_INFINITY;
    double maxLat = Double.NEGATIVE_INFINITY;
    double minLng = Double.POSITIVE_INFINITY;
    double maxLng = Double.NEGATIVE_INFINITY;
    private Marker marker;
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
    private String LAYER_ID = "LAYER_ID";

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

//        IntentFilter filter = new IntentFilter(AppService.RECEIVER_ACTION);
//        requireActivity().registerReceiver(broadcastReceiver, filter);

        //init dao
        routesDao = getAppDb().routesDao();
        stopsDao = getAppDb().stopsDao();

        AppService.onLocationReceive = StopsMapFragment.this;


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
            mapboxMap.setStyle(new Style.Builder().fromUri(Style.TRAFFIC_DAY),
                    style -> {
                        this.mapbox = mapboxMap;

                        setupMap();

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
                    handleBottomSheet(stop);

                }

                return true;
            });
        });


        handleBottomSheet(fetchedStop);


        try {
            //
            LatLng latLng = new LatLng(lat, lng);
            // Padding to control the space around the bounds (in pixels)
            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        } catch (Exception e) {
            e.printStackTrace();
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


    private void handleBottomSheet(Stop stops) {
        try {
            List<LatLng> coordinatesList = new ArrayList<>();

            int routeId = stopsDao.fetchRouteId(stops.getStopId());
            Route route = routesDao.fetchRouteById(routeId);

            for (Stop stop : route.getStop()) {
                coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
            }

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


            binding.llStopTime.tvTitle.setVisibility(route.getRoute_title() == null ? View.GONE : View.VISIBLE);
            binding.llStopTime.tvTitle.setText(route.getRoute_title());

            binding.llStopTime.tvDesc.setVisibility(route.getDescription() == null ? View.GONE : View.VISIBLE);
            binding.llStopTime.tvDesc.setText(route.getDescription());
            binding.llStopTime.tvStopTimes.setHasFixedSize(true);


            //live tracking
            binding.llStopTime.trackingSeekbar.setCount(coordinatesList.size());


            stopsDao.fetchAllStopsWithObserver(routeId).observe(getViewLifecycleOwner(), stops1 -> {
                binding.llStopTime.tvStopTimes.setAdapter(new StopMapTimeAdapter(stops1, (stop, integer) -> null));
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void animateCamera(MapboxMap mapboxMap, List<LatLng> coordinatesList) {
        try {
            LatLng start = new LatLng(coordinatesList.get(0).getLatitude(), coordinatesList.get(0).getLongitude());
            LatLng end = new LatLng(coordinatesList.get(coordinatesList.size() - 1).getLatitude(), coordinatesList.get(coordinatesList.size() - 1).getLongitude());

            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(start)
                    .include(end)
                    .build();

            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100), 2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawRouteOnMap(final MapboxMap mapboxMap, final Style style, final Route route) {
        final List<Feature> featuresList = new ArrayList<>();
//        final List<Point> pointsList = new ArrayList<>();
        List<Stop> stops = route.getStop();

        // Add a marker at the initial position
        style.addImage("icon-id-" + route.hashCode(), requireActivity().getDrawable(R.drawable.ic_location_marker));


        for (Stop stop : stops) {
            LatLng latLng = new LatLng(Double.parseDouble(Objects.requireNonNull(stop.getLat())), Double.parseDouble(Objects.requireNonNull(stop.getLng())));
            featuresList.add(Feature.fromGeometry(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude())));
            pointsList.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
        }


        // Add a GeoJson source for markers
        style.addSource(new GeoJsonSource("source-id-" + route.hashCode(), FeatureCollection.fromFeatures(featuresList)));
        // Add a SymbolLayer to display markers
        style.addLayer(new SymbolLayer("layer-id-" + route.hashCode(), "source-id-" + route.hashCode())
                .withProperties(
                        iconImage("icon-id-" + route.hashCode()),
                        iconAllowOverlap(false),
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
                        List<LegStep> steps = directionsRoute.legs().get(0).steps();
//                        DirectionsRoute rout = response.body().routes().get(0); // Assuming the first route is the preferred one

//                        double distance = rout.distance(); // Distance in meters
//                        long duration = rout.duration().longValue(); // Duration in seconds

//                        System.out.println(distance + " meters");
//                        System.out.println(formatDuration(duration) + " seconds");

//                         now;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                            LocalTime now = LocalTime.now();
//                            LocalTime newTime = now.plusMinutes(duration);
//                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a");
//                            System.out.println(formatter.format(newTime) + "");
//
//
//
//                        }

//                        List<DirectionsRoute> routess = response.body().routes();
//                        List<Double> distancess = new ArrayList<>();
//                        List<Long> durationss = new ArrayList<>();
//
//                        for (DirectionsRoute route : routess) {
//                            double distancee = route.distance(); // Distance in meters
//                            long duratione = route.duration().longValue(); // Duration in seconds
//
////                            System.out.println(formatDuration(duratione) + " seconds");
//                            distancess.add(distancee);
//                            durationss.add(duratione);
//                        }




                        LineString lineString = LineString.fromPolyline(directionsRoute.geometry(), 6);
                        List<Point> points = lineString.coordinates();

                        GeoJsonSource geoJsonSource = new GeoJsonSource("route-source-" + route.hashCode(), FeatureCollection.fromFeatures(new Feature[]{
                                Feature.fromGeometry(LineString.fromLngLats(points))
                        }));

                        style.addSource(geoJsonSource);

                        // Add layer
                        style.addLayerBelow(new LineLayer("route-layer-" + route.hashCode(),
                                "route-source-" + route.hashCode())
                                .withProperties(
                                        PropertyFactory.lineWidth(6f),
                                        PropertyFactory.lineColor(Color.parseColor(route.getColor()))
                                ), LAYER_ID);


                        // for realtime time calculations
                        if (steps != null && !steps.isEmpty()) {
                            int minSize = Math.min(steps.size() - 1, stops.size() - 1);

                            for (int i = 0; i < minSize; i++) {
                                LegStep step = steps.get(i);
                                Stop startStop = stops.get(i);
                                Stop endStop = stops.get(i + 1);

                                // Calculate the duration between start and end stops
                                if (startStop != null && endStop != null) {
                                    double legDuration = step.duration();

                                    // Check if startStop.getDuration() is null or empty
                                    double previousDuration = (startStop.getStop_time() != null && !startStop.getStop_time().isEmpty()) ? Double.parseDouble(startStop.getStop_time()) : 0.0;
//                                    double stopDuration = previousDuration + legDuration;


                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        LocalTime now = LocalTime.now();
                                        LocalTime newTime = now.plusMinutes((long) legDuration);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                                        startStop.setStop_time(formatter.format(newTime));
                                        stopsDao.updateStop(startStop);


                                    }

                                    // Update the duration field of the start stop
//                                    startStop.setStop_time(Double.toString(stopDuration));


                                    // Assuming you have a method to update the stop in your database

//                                    Log.e(TAG, "onResponse: " + startStop.getStopId() + " name ==> " + startStop.getStop_title() + " startStop ==> " + startStop.getStop_time());

                                } else {
                                    Log.e(TAG, "onResponse: " + startStop.getStopId() + " name ==> " + startStop.getStop_title() + " is null ");
                                }
                            }

//                            for (int i = 0; i < minSize; i++) {
//                                LegStep step = steps.get(i);
//                                Stop startStop = stops.get(i);
//                                Stop endStop = stops.get(i + 1);
//
//                                if (startStop != null && endStop != null) {
//                                    double legDuration = step.duration(); // Duration in minutes
//
//                                    String currentStopTime = startStop.getStop_time();
//                                    if (currentStopTime != null && !currentStopTime.isEmpty()) {
//                                        try {
//                                            // Assuming the time format is something like "HH:mm"
//                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                                            Date stopTime = sdf.parse(currentStopTime);
//
//                                            // Add leg duration to the stop time
//                                            Calendar calendar = Calendar.getInstance();
//                                            calendar.setTime(stopTime);
//                                            calendar.add(Calendar.MINUTE, (int) legDuration);
//
//                                            // Format the new stop time back into a string
//                                            String newStopTime = sdf.format(calendar.getTime());
//
//                                            // Update the stop time
//                                            startStop.setStop_time(newStopTime + " minutes");
//                                            stopsDao.updateStop(startStop);
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace(); // Handle parse exception
//                                        }
//                                    } else {
//                                        Log.e(TAG, "onResponse: " + startStop.getStopId() + " name ==> " + startStop.getStop_title() + " is null ");
//                                    }
//                                }
//                            }

                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        MapboxMatrix matrixClient = MapboxMatrix.builder()
//                .accessToken(getString(R.string.mapbox_access_token))
//                .coordinates(pointsList)
//                .build();
//
//        matrixClient.enqueueCall(new Callback<MatrixResponse>() {
//            @Override
//            public void onResponse(Call<MatrixResponse> call, Response<MatrixResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    MatrixResponse matrixResponse = response.body();
//
//                    Log.e(TAG, "onResponse: " + matrixResponse.toString());
//
//                } else {
//                    try {
//                        Log.e(TAG, "onResponseError: " + response.errorBody().string());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MatrixResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }


    private void animateCamera(MapboxMap mapboxMap, Stop stop) {
        LatLng latLng = new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng()));
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

//    private String formatDuration(double durationSeconds) {
//        int hours = (int) durationSeconds / 3600;
//        int minutes = (int) (durationSeconds % 3600) / 60;
//        int seconds = (int) durationSeconds % 60;
//
//        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//    }

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

    public void setupMap() {
        Bitmap customBusIcon = BitmapFactory.decodeResource(getResources(), R.drawable.abc);
        marker = mapbox.addMarker(
                new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("On the way")
                        .icon(IconFactory.getInstance(requireContext()).fromBitmap(customBusIcon))
        );

        mapbox.setOnMarkerClickListener(marker -> {
//            Toast.makeText(requireContext(), "Bus Marker Clicked!", Toast.LENGTH_SHORT).show();
            // Or, perform other actions like:
            // - Displaying more information about the bus
            // - Zooming the map to the marker's location
            // - Triggering navigation to the bus's stop

            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            bottomStopTimeSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

            return true;
        });


    }

    @Override
    public void onLocationReceive(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String data = jsonObject.getString("data");
            String location = new JSONObject(data).getString("location");
            double lat = new JSONObject(location).getDouble("lat");
            double lon = new JSONObject(location).getDouble("long");

            latitudeBus = lat;
            longitudeBus = lon;

            double distanceToStart = TurfMeasurement.distance(Point.fromLngLat(lat, lon), pointsList.get(0), TurfConstants.UNIT_METERS);
            double distanceToEnd = TurfMeasurement.distance(Point.fromLngLat(lat, lon), pointsList.get(pointsList.size() - 1), TurfConstants.UNIT_METERS);
            double totalDistance = TurfMeasurement.distance(pointsList.get(0), pointsList.get(pointsList.size() - 1), TurfConstants.UNIT_METERS);
            int progress = (int) ((1 - (distanceToEnd / totalDistance)) * 100); // Calculate relative progress

            Log.e(TAG, "startDistance " + distanceToStart + "  ==>  endDistance  " + distanceToEnd + "  ==>  totalDistance  " + totalDistance);


            requireActivity().runOnUiThread(() -> {
                updateMarkerOnMap(lat, lon);

                binding.llStopTime.trackingSeekbar.setProgress(progress);

            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMarkerOnMap(double lat, double lon) {
        if (marker != null && mapbox != null) {
            LatLng newLatLng = new LatLng(lat, lon);
            marker.setPosition(newLatLng);
            mapbox.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
        }
    }

    public String formatDuration(double durationInSeconds) {
        int hours = (int) (durationInSeconds / 3600);
        int minutes = (int) ((durationInSeconds % 3600) / 60);
        int seconds = (int) (durationInSeconds % 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

//    private String formatDuration(double durationInSeconds) {
//        // Format the duration as needed (e.g., convert seconds to minutes, hours, etc.)
//        // Implement your logic to format the duration based on your requirements
//        return String.format(Locale.getDefault(), "%.2f minutes", durationInSeconds / 60);
//    }


}