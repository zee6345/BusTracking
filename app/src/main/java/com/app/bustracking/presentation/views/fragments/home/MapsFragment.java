package com.app.bustracking.presentation.views.fragments.home;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
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
import com.app.bustracking.data.local.StopsDao;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.databinding.FragmentMapsBinding;
import com.app.bustracking.presentation.views.fragments.BaseFragment;
import com.app.bustracking.utils.Constants;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback, PermissionsListener {

    private NavController navController;
    private FragmentMapsBinding binding;

    //    private GetTravelRoutes stopsList;
    private List<Stop> stopsList;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private String SOURCE_ID = "SOURCE_ID";
    private String ICON_ID = "ICON_ID";
    private String LAYER_ID = "LAYER_ID";
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private List<LatLng> coordinatesList = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;


    double maxLat = -90;
    double maxLng = -180;
    double minLat = 90;
    double minLng = 180;

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
        binding.mapBoxView.getMapAsync(this);

        mapView = binding.mapBoxView;
        mapView.onCreate(savedInstanceState);

        binding.tvTitle.setText("Maps");

        //fetch data from db
        StopsDao stopsDao = appDb().stopsDao();


        int agencyId = Prefs.getInt(Constants.agencyId);
        stopsList = stopsDao.fetchAllStops(agencyId);


        initRouteLists();

        initPointsForMap();


        binding.fabCameraView.setOnClickListener(view1 -> {
            if (mapboxMap != null) {
                try {
                    // Padding to control the space around the bounds (in pixels)
                    int padding = 100;
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initRouteLists() {
        for (Stop stop : stopsList) {
            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(stop.getLng()), Double.parseDouble(stop.getLat()))));
            coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
        }
    }

    private void initPointsForMap() {
        builder = new LatLngBounds.Builder();
        for (LatLng latLng : coordinatesList) {
            builder.include(latLng);
        }
        bounds = builder.build();
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        if (!coordinatesList.isEmpty()) {

            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

//                enableLocationComponent(style);

                    style.addImage(ICON_ID, BitmapFactory.decodeResource(
                            requireActivity().getResources(),
                            com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
                    ));


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

                    //Use Mapbox Directions API to create a route
                    MapboxDirections directionsClient = MapboxDirections.builder()
                            .origin(Point.fromLngLat(pointsList.get(0).longitude(), pointsList.get(0).latitude()))
                            .destination(Point.fromLngLat(pointsList.get(pointsList.size() - 1).longitude(), pointsList.get(pointsList.size() - 1).latitude()))
                            .waypoints(pointsList.subList(1, pointsList.size() - 1))
                            .accessToken(getString(R.string.mapbox_access_token))
                            .overview("full")
                            .profile("driving-traffic")
                            .steps(true)
                            .build();

                    //Draw the route on the map
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
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });


//
                    // Padding to control the space around the bounds (in pixels)
                    int padding = 100;
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));


                }
            });

            mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public boolean onMapClick(@NonNull LatLng point) {

                    Log.e("mmmTAG", "" + point);

//                navController.navigate(R.id.action_driverMap_to_driverMapDetails);

                    return true;
                }
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

//        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);

        super.onDestroy();
    }
}
