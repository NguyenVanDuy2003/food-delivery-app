package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.Common.CommonKey;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView btnToggleMenu;
    private NavigationView navigationView;
    private RecyclerView restaurantRecyclerView;
    private EditText etSearch;
    private CardView btnSearch;

    // Data
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupNavigationDrawer();
        setupRecyclerView();
        setupSearchFunctionality();
        setupBottomNavigation();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_home_layout);
        btnToggleMenu = findViewById(R.id.btn_expand_menu);
        navigationView = findViewById(R.id.nav_view);
        restaurantRecyclerView = findViewById(R.id.rv_restaurants);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
    }

    private void setupNavigationDrawer() {
        btnToggleMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
    }

    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.my_orders) {
            // Handle My Orders action
        } else if (itemId == R.id.my_profile) {
            // Handle My Profile action
        } else if (itemId == R.id.delivery_address) {
            // Handle Delivery Address action
        } else if (itemId == R.id.payment_methods) {
            // Handle Payment Methods action
        } else if (itemId == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView() {
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurants = createSampleRestaurants();
        restaurantAdapter = new RestaurantAdapter(this,restaurants);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
    }

    private List<Restaurant> createSampleRestaurants() {
        List<Restaurant> sampleRestaurants = new ArrayList<>();
        sampleRestaurants.add(new Restaurant("McDonald's", R.drawable.ic_launcher_background, 4.5f, "15-25 min"));
        sampleRestaurants.add(new Restaurant("KFC", R.drawable.ic_launcher_background, 4.3f, "20-30 min"));
        sampleRestaurants.add(new Restaurant("Pizza Hut", R.drawable.ic_launcher_background, 4.7f, "25-35 min"));
        sampleRestaurants.add(new Restaurant("Subway", R.drawable.ic_launcher_background, 4.4f, "15-25 min"));
        sampleRestaurants.add(new Restaurant("Domino's Pizza", R.drawable.ic_launcher_background, 4.6f, "20-30 min"));
        sampleRestaurants.add(new Restaurant("Burger King", R.drawable.ic_launcher_background, 4.2f, "15-25 min"));
        sampleRestaurants.add(new Restaurant("Starbucks", R.drawable.ic_launcher_background, 4.8f, "10-20 min"));
        sampleRestaurants.add(new Restaurant("Sushi Express", R.drawable.ic_launcher_background, 4.5f, "25-35 min"));
        return sampleRestaurants;
    }

    private void setupSearchFunctionality() {
        btnSearch.setOnClickListener(v -> performSearch());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String searchQuery = etSearch.getText().toString().trim();
        searchRestaurants(searchQuery);
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btn_view_all).setOnClickListener(v -> navigateToAllRestaurants());
        findViewById(R.id.btn_home).setOnClickListener(v -> {/* Already on home screen */});
        findViewById(R.id.btn_cart).setOnClickListener(v -> {/* TODO: Navigate to cart */});
        findViewById(R.id.btn_profile).setOnClickListener(v -> {/* TODO: Navigate to profile */});
    }

    private void navigateToAllRestaurants() {
        Intent intent = new Intent(HomeActivity.this, AllRestaurantsActivity.class);
        intent.putExtra("restaurants", new ArrayList<>(restaurants));
        startActivity(intent);
    }

    /**
     * Filters restaurants based on search query
     * @param query The search term to filter restaurants
     */
    private void searchRestaurants(String query) {
        if (query.isEmpty()) {
            restaurantAdapter = new RestaurantAdapter(this,restaurants);
            restaurantRecyclerView.setAdapter(restaurantAdapter);
            return;
        }

        List<Restaurant> filteredList = restaurants.stream()
            .filter(restaurant ->
                restaurant.getName().toLowerCase().contains(query.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        restaurantAdapter = new RestaurantAdapter(this,filteredList);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
    }
}
