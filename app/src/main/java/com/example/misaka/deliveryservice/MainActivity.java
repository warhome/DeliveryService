package com.example.misaka.deliveryservice;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // TODO: Сделать проверку GooglePlayServices
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        ServicesChecker servicesChecker = new ServicesChecker();
        servicesChecker.isServicesOK(getApplicationContext(), this);
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.app_bar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(view.getContext(), AddParcel.class)));

        // Tabs
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager) {
        Resources res = getResources();
        String[] statuses_RU = res.getStringArray(R.array.status_name_RU);
        List<Fragment> fragmentList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("key", i);
            Fragment f = new Fragment();
            f.setArguments(bundle);
            fragmentList.add(f);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i = 0; i < 3; i++) {
            adapter.addFragment(fragmentList.get(i), statuses_RU[i]);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

}