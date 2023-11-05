package com.app.bustracking.presentation.views.fragments.home;

import static com.app.bustracking.app.BusTracking.context;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.app.bustracking.R;
import com.app.bustracking.app.AppService;
import com.app.bustracking.data.preference.AppPreference;
import com.app.bustracking.data.responseModel.GetTravelRoutes;
import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.databinding.FragmentRoutesMapBinding;
import com.app.bustracking.databinding.FragmentStopsBinding;
import com.app.bustracking.presentation.views.fragments.BaseFragment;
import com.app.bustracking.presentation.views.fragments.bottomsheets.RouteMapModalSheet;
import com.app.bustracking.utils.Converter;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StopsJFragment extends BaseFragment implements OnMapReadyCallback, PermissionsListener {

    public static String ARGS = "data";

    private NavController navController;
    private FragmentStopsBinding binding;


    private GetTravelRoutes stopsList;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private String SOURCE_ID = "SOURCE_ID";
    private String ICON_ID = "ICON_ID";
    private String LAYER_ID = "LAYER_ID";
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private List<LatLng> coordinatesList = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    SymbolManager symbolManager;
    Symbol locationMarker;
    DirectionsRoute directionsRoute;
    RouteMapModalSheet routeMapModalSheet;
    Style styless;
    private Double latitudeBus = 31.5300229;
    private Double longitudeBus = 74.3077318;
    Double latitude = 0.0;
    Double longitude = 0.0;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("My_Action_Event")) {
                String jsonData = intent.getStringExtra("json_data");
                if (jsonData != null || !jsonData.isEmpty()) {
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
                    }
                }
            }
        }
    };

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStopsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter filter = new IntentFilter("My_Action_Event");
        requireActivity().registerReceiver(broadcastReceiver, filter);



        String data = AppPreference.INSTANCE.getString("routeList");
        stopsList = Converter.INSTANCE.fromJson(data, GetTravelRoutes.class);
        Route route = stopsList.getRoute_list().get(0);

        Mapbox.getInstance(
                requireActivity(),
                getString(com.app.bustracking.R.string.mapbox_access_token)
        );
        binding.mapView.getMapAsync(this);
        binding.mapView.onSaveInstanceState(savedInstanceState);

        mapView = binding.mapView;

        //
        Intent intent = new Intent(requireActivity(), AppService.class);
        intent.putExtra("bus_id", route.getBus_id() + "");
        requireActivity().startService(intent);


        //
//        routeMapModalSheet = new RouteMapModalSheet(route);
//        routeMapModalSheet.show(requireActivity().getSupportFragmentManager(), routeMapModalSheet.getTag());


        //
        for (Stop stop : route.getStop()) {
            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(stop.getLng()), Double.parseDouble(stop.getLat()))));
            coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
        }

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        if (!coordinatesList.isEmpty()) {

            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

                symbolManager = new SymbolManager(binding.mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);

                // Add a marker at the initial position
                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(new LatLng(latitude, longitude))
                        .withIconImage(String.valueOf(R.drawable.abc)) // You should provide your own marker icon
                        .withIconSize(2.0f);
                locationMarker = symbolManager.create(symbolOptions);

                //
//                enableLocationComponent(style);

                style.addImage(ICON_ID, BitmapFactory.decodeResource(
                        requireActivity().getResources(),
                        com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
                ));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : coordinatesList) {
                    builder.include(latLng);
                }
                List<Point> pointsList = new ArrayList<>();
                for (LatLng latLng : coordinatesList) {
                    pointsList.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                }

                // Add a GeoJson source for markers
                style.addSource(new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));
                // Add a SymbolLayer to display markers
                style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                );

                // Use Mapbox Directions API to create a route
                MapboxDirections directionsClient = MapboxDirections.builder()
                        .origin(Point.fromLngLat(pointsList.get(0).longitude(), pointsList.get(0).latitude()))
                        .destination(Point.fromLngLat(pointsList.get(pointsList.size() - 1).longitude(), pointsList.get(pointsList.size() - 1).latitude()))
                        .waypoints(pointsList.subList(1, pointsList.size() - 1))
                        .accessToken(getString(R.string.mapbox_access_token))
                        .overview("full")
                        .profile("driving-traffic")
                        .steps(true)
                        .build();

                // Draw the route on the map
                directionsClient.enqueueCall(new Callback<>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() != null && !response.body().routes().isEmpty()) {

                            DirectionsRoute route = response.body().routes().get(0);

                            drawRouteOnMap(style, route);
//
                            LocationComponent locationComponent = mapboxMap.getLocationComponent();
                            locationComponent.activateLocationComponent(requireActivity(), style);
//                                locationComponent.setLocationComponentEnabled(true);
                            locationComponent.setCameraMode(CameraMode.TRACKING);
                            locationComponent.setRenderMode(RenderMode.NORMAL);



                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        t.printStackTrace();

                    }
                });


                LatLngBounds bounds = builder.build();

                // Padding to control the space around the bounds (in pixels)
                int padding = 100;
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));


            });

            mapboxMap.addOnMapClickListener(point -> {

                Log.e("mmmTAG", "" + point);

                if (coordinatesList.contains(point)){
                    Log.e("mmmTAG", "" + point + ":: matched");
                } else {

                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(coordinatesList.get(0)) // Northeast
                            .include(coordinatesList.get(coordinatesList.size() - 1)) // Southwest
                            .build();

                    mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);

                }

//                routeMapModalSheet.show(requireActivity().getSupportFragmentManager(), "");

                return true;
            });

        } else {

            //when coordinate list is empty
            Toast.makeText(requireActivity(), "No route found!", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                requireActivity().finish();
            }, 500);
        }

    }


    private void drawRouteOnMap(@NonNull Style style, DirectionsRoute route) {
        LineString lineString = LineString.fromPolyline(route.geometry(), 6);

        List<Point> points = lineString.coordinates();
        List<LatLng> latLngs = new ArrayList<>();

        for (Point point : points) {
            latLngs.add(new LatLng(point.latitude(), point.longitude()));
        }

        GeoJsonSource geoJsonSource = new GeoJsonSource("route-source", FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(LineString.fromLngLats(points))
        }));

        style.addSource(geoJsonSource);

        style.addLayer(new LineLayer("route-layer", "route-source").withProperties(
                PropertyFactory.lineColor(Color.RED),
                PropertyFactory.lineWidth(3f)
        ));


    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(requireActivity())
                    .pulseEnabled(true)
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

            // Enable to make component visible
            //  locationComponent.setLocationComponentEnabled(true);


            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);


            //foreground service
            Intent serviceIntent = new Intent(requireActivity(), AppService.class);
            serviceIntent.putExtra("track", "");
            context.startService(serviceIntent);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(requireActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
//            mapboxMap.getStyle(style -> enableLocationComponent(style));
        } else {
            Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {

        mapView.onDestroy();

        super.onDestroy();
    }
}
