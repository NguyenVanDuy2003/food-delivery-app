package com.example.food;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllRestaurantsActivity extends AppCompatActivity {
    
    // UI Components
    private RecyclerView restaurantRecyclerView;
    private EditText etSearch;
    private CardView btnSort;
    private ImageView ivSortIcon;
    
    // Data
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants;
    private boolean isAscending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_restaurants);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchAndSort();
    }

    private void initializeViews() {
        restaurantRecyclerView = findViewById(R.id.rv_all_restaurants);
        etSearch = findViewById(R.id.et_search);
        btnSort = findViewById(R.id.btn_sort);
        ivSortIcon = findViewById(R.id.iv_sort_icon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.all_restaurants);
        }
    }

    private void setupRecyclerView() {
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurants = getRestaurantsFromIntent();
        restaurantAdapter = new RestaurantAdapter(restaurants);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
    }

    @SuppressWarnings("unchecked")
    private List<Restaurant> getRestaurantsFromIntent() {
        List<Restaurant> intentRestaurants = (List<Restaurant>) getIntent().getSerializableExtra("restaurants");
        return intentRestaurants != null ? intentRestaurants : new ArrayList<>();
    }

    private void setupSearchAndSort() {
        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchRestaurants(s.toString());
            }
        });

        btnSort.setOnClickListener(v -> {
            isAscending = !isAscending;
            sortByRating();
            updateSortIcon();
        });
    }

    private void updateSortIcon() {
        ivSortIcon.setRotation(isAscending ? 180f : 0f);
    }

    /**
     * Filters restaurants based on search query
     * @param query The search term to filter restaurants
     */
    private void searchRestaurants(String query) {
        if (query.isEmpty()) {
            restaurantAdapter = new RestaurantAdapter(restaurants);
            restaurantRecyclerView.setAdapter(restaurantAdapter);
            return;
        }

        List<Restaurant> filteredList = restaurants.stream()
            .filter(restaurant -> 
                restaurant.getName().toLowerCase().contains(query.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
        restaurantAdapter = new RestaurantAdapter(filteredList);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
    }

    private void sortByRating() {
        Collections.sort(restaurants, (r1, r2) -> 
            isAscending ? 
            Float.compare(r1.getRating(), r2.getRating()) : 
            Float.compare(r2.getRating(), r1.getRating())
        );
        restaurantAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
} 