package com.app.bustracking.presentation.views.fragments.home;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.app.bustracking.R;
import com.app.bustracking.app.AppService;
import com.app.bustracking.data.local.RoutesDao;
import com.app.bustracking.data.local.StopsDao;
import com.app.bustracking.data.local.TravelDao;
import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.databinding.FragmentRoutesMapBinding;
import com.app.bustracking.presentation.ui.RoutesMapAdapter;
import com.app.bustracking.presentation.views.fragments.BaseFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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


public class RoutesMapFragment extends BaseFragment implements OnMapReadyCallback, PermissionsListener {

    private NavController navController;
    private FragmentRoutesMapBinding binding;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private String SOURCE_ID = "SOURCE_ID";
    private String ICON_ID = "ICON_ID";
    private String LAYER_ID = "LAYER_ID";
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private List<LatLng> coordinatesList = new ArrayList<>();
    //    List<Marker> markers = new ArrayList<>();
    SymbolManager symbolManager;
    Symbol locationMarker;
    //    DirectionsRoute directionsRoute;
//    RouteMapModalSheet routeMapModalSheet;
    //    Style styless;
    private Double latitudeBus = 31.5300229;
    private Double longitudeBus = 74.3077318;
    Double latitude = 0.0;
    Double longitude = 0.0;
    private String routeColor;

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

//                        locationMarker.setLatLng(new LatLng(lat, lon));
//                        symbolManager.update(locationMarker);

                        updateMarkerOnMap(lat, lon);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void updateMarkerOnMap(double lat, double lon) {
        LatLng newLatLng = new LatLng(lat, lon);
        marker.setPosition(newLatLng);
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
    }

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutesMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Route route;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter filter = new IntentFilter("My_Action_Event");
        requireActivity().registerReceiver(broadcastReceiver, filter);


        //init dao
        RoutesDao routesDao = appDb().routesDao();
        StopsDao stopsDao = appDb().stopsDao();
        TravelDao travelDao = appDb().travelDao();


        //fetch data from db
        int routeId = requireArguments().getInt(RoutesFragmentKt.ARGS);
         route = routesDao.fetchRoute(routeId);

        if (route == null) {
            showMessage("No route found!");
            navController.popBackStack();
            return;
        }

        routeColor = route.getColor();

        //map box
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

        for (Stop stop : route.getStop()) {
            String tag = String.valueOf(stop.getStopId());  // Replace this with the actual way you get the tag for each stop
            Feature feature = Feature.fromGeometry(Point.fromLngLat(
                    Double.parseDouble(stop.getLng()),
                    Double.parseDouble(stop.getLat())));
            feature.addStringProperty("tag", tag);
            symbolLayerIconFeatureList.add(
                    feature
            );
            coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
        }


        handleBottomSheet(route);

    }

    private Marker marker;

    public void setupMap() {
        Bitmap customBusIcon = BitmapFactory.decodeResource(getResources(), R.drawable.abc);
        marker = mapboxMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Bus Marker")
                        .icon(IconFactory.getInstance(requireContext()).fromBitmap(customBusIcon))
        );
    }

    private void handleBottomSheet(Route route) {
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(binding.llParent.bottomLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        binding.llParent.tvTitle.setText(route.getRoute_title());
        binding.llParent.lvMsg.setVisibility(route.getDescription().isEmpty() ? View.GONE : View.VISIBLE);
        binding.llParent.tvText.setText(route.getDescription());


        if (!route.getStop().isEmpty()) {
            binding.llParent.rvMapRoutes.setVisibility(View.VISIBLE);
        } else binding.llParent.rvMapRoutes.setVisibility(View.GONE);

        binding.llParent.rvMapRoutes.setHasFixedSize(true);
        binding.llParent.rvMapRoutes.setAdapter(new RoutesMapAdapter(route.getStop(), (stop, integer) -> {

            Bundle bundle = new Bundle();
            bundle.putInt(RoutesFragmentKt.ARGS, stop.getStopId());
            navController.navigate(R.id.action_routesMapFragment_to_stopsMapFragment, bundle);

            return null;
        }));
    }

    Style style;

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        if (!coordinatesList.isEmpty()) {

            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                this.style = style;

                setupMap();
                //components
                LocationComponent locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(requireActivity(), style);
//                                locationComponent.setLocationComponentEnabled(true);
                locationComponent.setCameraMode(CameraMode.TRACKING);
                locationComponent.setRenderMode(RenderMode.NORMAL);

                //
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

                List<Feature> features = mapboxMap.queryRenderedFeatures(
                        mapboxMap.getProjection().toScreenLocation(point),
                        LAYER_ID
                );

                if (features != null && !features.isEmpty()) {
                    // A marker was clicked, get its properties
                    Feature clickedFeature = features.get(0);
                    String tag = clickedFeature.getStringProperty("tag");

                    // Use the tag as needed
                    Log.d("MapboxActivity", "Clicked Marker - Tag: " + tag);
                    for (int i = 0; i< route.getStop().size();i++){
                        if (route.getStop().get(i).getStopId() == Integer.parseInt(tag)){
                            Log.e("error",route.getStop().get(i).getStop_title().toLowerCase());
                        }
                    }

                }

                if (coordinatesList.contains(point)) {
                    Log.e("mmmTAG", "" + point + ":: matched");
                } else {

                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(coordinatesList.get(0)) // Northeast
                            .include(coordinatesList.get(coordinatesList.size() - 1)) // Southwest
                            .build();

                    mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);

                }


                return true;
            });

        } else {

            //when coordinate list is empty
            Toast.makeText(requireActivity(), "No route found!", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                navController.popBackStack();
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
//                PropertyFactory.lineColor(Color.RED),
                PropertyFactory.lineColor(Color.parseColor(routeColor)),
                PropertyFactory.lineWidth(3f)
        ));

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
        if (mapView != null)
            mapView.onDestroy();
        super.onDestroy();
    }
}
