package com.app.bustracking.presentation.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;

import com.app.bustracking.R;
import com.app.bustracking.data.local.RoutesDao;
import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.databinding.FragmentRoutesBinding;
import com.app.bustracking.presentation.ui.RoutesAdapter;
import com.app.bustracking.presentation.views.fragments.BaseFragment;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;


public class NewRouteFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    NavController navController;
    FragmentRoutesBinding binding;
    RoutesAdapter favAdapter;
    RoutesAdapter linesAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isFavExpand = false;
    private boolean isAllExpand = false;

    public NewRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewRouteFragment newInstance(String param1, String param2) {
        NewRouteFragment fragment = new NewRouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRoutesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoutesDao routeDao = appDb().routesDao();

        binding.rvLines.setHasFixedSize(true);
        binding.rvFavorite.setHasFixedSize(true);

        favAdapter = new RoutesAdapter(routeDao, new Function2<>() {
            @Override
            public Unit invoke(Route route, Integer integer) {

                Bundle args = new Bundle();
                args.putInt(RoutesFragmentKt.ARGS, route.getRouteId());
                navController.navigate(
                        R.id.action_routesFragment_to_routesMapFragment,
                        args,
                        navOptions()
                );

                return null;
            }
        });

        linesAdapter = new RoutesAdapter(routeDao, new Function2<>() {
            @Override
            public Unit invoke(Route route, Integer integer) {

                Bundle args = new Bundle();
                args.putInt(RoutesFragmentKt.ARGS, route.getRouteId());
                navController.navigate(
                        R.id.action_routesFragment_to_routesMapFragment,
                        args,
                        navOptions()
                );

                return null;
            }
        });

        binding.rvFavorite.setAdapter(favAdapter);
        binding.rvLines.setAdapter(linesAdapter);


        routeDao.fetchRoutes().observe(getViewLifecycleOwner(), new Observer<List<Route>>() {
            @Override
            public void onChanged(List<Route> routes) {
                linesAdapter.updateList(routes);
            }
        });

        routeDao.fetchFavouriteRoutes().observe(getViewLifecycleOwner(), new Observer<List<Route>>() {
            @Override
            public void onChanged(List<Route> routes) {
                favAdapter.updateList(routes);
            }
        });


        binding.ivExpandFav.setOnClickListener(v -> {
            binding.rlFav.setVisibility(isFavExpand ? View.VISIBLE : View.GONE);
            isFavExpand = !isFavExpand;
        });

        binding.ivExpandAll.setOnClickListener(v1 -> {
            binding.rlAll.setVisibility(isAllExpand ? View.VISIBLE : View.GONE);
            isAllExpand = !isAllExpand;
        });

    }

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }
}