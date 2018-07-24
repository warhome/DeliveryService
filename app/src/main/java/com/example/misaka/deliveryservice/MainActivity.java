package com.example.misaka.deliveryservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.misaka.deliveryservice.firebase.FirebaseAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.misaka.deliveryservice.Consts.ASSIGNED_TO_ME_BUNDLE;
import static com.example.misaka.deliveryservice.Consts.FILTER;
import static com.example.misaka.deliveryservice.Consts.IS_ADMIN;
import static com.example.misaka.deliveryservice.Consts.KEY_BUNDLE;
import static com.example.misaka.deliveryservice.Consts.UPLOADED_BY_ME_BUNDLE;

public class MainActivity extends AppCompatActivity {

    private static final int FIREBASE_AUTH_TAG = 7800;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.nav_view)
    NavigationView nav;

    View header;
    TextView emailText;
    TextView statusText;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ServicesChecker servicesChecker = new ServicesChecker();
        servicesChecker.isServicesOK(getApplicationContext(), this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
            startActivityForResult(new Intent(this, FirebaseAuthActivity.class), FIREBASE_AUTH_TAG);

        // Toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.app_bar);
        }
        View appBar = LayoutInflater.from(this).inflate(R.layout.app_bar, null);
        TextView appBarHead = appBar.findViewById(R.id.app_bar_head_text);

        // Fab
        fab.setOnClickListener(view -> startActivity(new Intent(view.getContext(), AddParcel.class)));

        // Tabs
        if (mSharedPreferences.getBoolean(IS_ADMIN, false)) setViewPager(viewPager);
        else setupViewPager(viewPager, ASSIGNED_TO_ME_BUNDLE);
        tabLayout.setupWithViewPager(viewPager);

        // Nav
        header = nav.getHeaderView(0);
        emailText = header.findViewById(R.id.nav_email_text);
        statusText = header.findViewById(R.id.nav_user_status_text);
        updateUI();
        nav.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_sign_in:
                    startActivityForResult(new Intent(this, FirebaseAuthActivity.class), FIREBASE_AUTH_TAG);
                    break;
                case R.id.nav_my_parcels:
                    if (mSharedPreferences.getBoolean(IS_ADMIN, false)) setViewPager(viewPager);
                    else setupViewPager(viewPager, ASSIGNED_TO_ME_BUNDLE);
                    tabLayout.setupWithViewPager(viewPager);
                    appBarHead.setText(R.string.my_parcels);
                    break;
                case R.id.nav_added_by_me:
                    if (mSharedPreferences.getBoolean(IS_ADMIN, false)) setViewPager(viewPager);
                    else setupViewPager(viewPager, UPLOADED_BY_ME_BUNDLE);
                    tabLayout.setupWithViewPager(viewPager);
                    appBarHead.setText(R.string.added_by_me);
                    break;
            }
            return true;
        });
    }

    public void setViewPager(ViewPager viewPager) {
        Fragment f = new Fragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(f, getString(R.string.new_and_canceled));
        viewPager.setAdapter(adapter);
    }

    public void setupViewPager(ViewPager viewPager, String filter) {
        Resources res = getResources();
        String[] statuses_RU = res.getStringArray(R.array.status_name_RU);
        List<Fragment> fragmentList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Bundle bundle = new Bundle();
            if (filter != null && !filter.isEmpty()) {
                bundle.putString(FILTER, filter);
            }
            bundle.putInt(KEY_BUNDLE, i);
            Fragment f = new Fragment();
            f.setArguments(bundle);
            fragmentList.add(f);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < 3; i++) {
            adapter.addFragment(fragmentList.get(i), statuses_RU[i]);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FIREBASE_AUTH_TAG && resultCode == RESULT_OK) {
            updateUI();
        } else finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateUI() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            emailText.setText(currentUser.getEmail());
            if (mSharedPreferences.getBoolean(IS_ADMIN, false)) {
                statusText.setText(R.string.ADMIN);
                fab.setVisibility(View.INVISIBLE);
            } else {
                statusText.setText(R.string.COURIER);
                fab.setVisibility(View.VISIBLE);
            }
            if (mSharedPreferences.getBoolean(IS_ADMIN, false)) setViewPager(viewPager);
            else setupViewPager(viewPager, ASSIGNED_TO_ME_BUNDLE);
        }
    }
}
