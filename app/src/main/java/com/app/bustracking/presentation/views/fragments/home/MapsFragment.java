package com.app.bustracking.presentation.views.fragments.home;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.app.bustracking.R;
import com.app.bustracking.data.local.RoutesDao;
import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.databinding.FragmentMapsBinding;
import com.app.bustracking.presentation.views.fragments.BaseFragment;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends BaseFragment {

    private NavController navController;
    private FragmentMapsBinding binding;

    private MapView mapView;
    MapboxMap mapbox;
    List<LatLng> coordinatesList = new ArrayList<>();

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token));

        mapView = binding.mapBoxView;
        mapView.onCreate(savedInstanceState);

        binding.tvTitle.setText("Maps");

        //fetch data from db
        RoutesDao routesDao = appDb().routesDao();

        mapView.getMapAsync(mapboxMap ->
                mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
                        style -> {

                            this.mapbox = mapboxMap;



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


                            animateCamera(mapboxMap, coordinatesList);

                        }));

        binding.fabCameraView.setOnClickListener(v -> {
            animateCamera(mapbox, coordinatesList);
        });
    }

    private void animateCamera(MapboxMap mapboxMap, List<LatLng> coordinatesList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : coordinatesList) {
            builder.include(latLng);
        }

        LatLngBounds bounds = builder.build();

        // Padding to control the space around the bounds (in pixels)
        int padding = 100;
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
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

        //
        style.addImage("icon-id-" + route.hashCode(), BitmapFactory.decodeResource(
                requireActivity().getResources(),
                com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
        ));


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
                if (response.body() != null && !response.body().routes().isEmpty()) {
                    DirectionsRoute directionsRoute = response.body().routes().get(0);

                    //
                    LineString lineString = LineString.fromPolyline(directionsRoute.geometry(), 6);
                    List<Point> points = lineString.coordinates();
                    GeoJsonSource geoJsonSource = new GeoJsonSource("route-source-" + route.hashCode(), FeatureCollection.fromFeatures(new Feature[]{
                            Feature.fromGeometry(LineString.fromLngLats(points))
                    }));

                    //
                    LocationComponent locationComponent = mapboxMap.getLocationComponent();
                    locationComponent.activateLocationComponent(requireActivity(), style);
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.setRenderMode(RenderMode.NORMAL);

                    style.addSource(geoJsonSource);

                    // Add layer
                    style.addLayer(new LineLayer("route-layer-" + route.hashCode(),
                            "route-source-" + route.hashCode())
                            .withProperties(
                                    PropertyFactory.lineWidth(4f),
                                    PropertyFactory.lineColor(Color.parseColor(route.getColor()))
                            ));
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {

        if (mapView != null)
            mapView.onDestroy();

//        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver)

        super.onDestroy();
    }

}