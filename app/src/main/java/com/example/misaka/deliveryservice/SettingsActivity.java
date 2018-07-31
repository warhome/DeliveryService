package com.example.misaka.deliveryservice;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.misaka.deliveryservice.Consts.CENTIMETER;
import static com.example.misaka.deliveryservice.Consts.FOOT;
import static com.example.misaka.deliveryservice.Consts.GRAM;
import static com.example.misaka.deliveryservice.Consts.KILOGRAM;
import static com.example.misaka.deliveryservice.Consts.LB;
import static com.example.misaka.deliveryservice.Consts.METER;
import static com.example.misaka.deliveryservice.Consts.MILIMETER;
import static com.example.misaka.deliveryservice.Consts.SIZE;
import static com.example.misaka.deliveryservice.Consts.WEIGH;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.size_spinner)
    Spinner sizeSpinner;
    @BindView(R.id.weight_spinner)
    Spinner weight_spinner;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Adapters
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.size_units, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);
        sizeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> weighAdapter = ArrayAdapter.createFromResource(this,
                R.array.weigh_units, android.R.layout.simple_spinner_item);
        weighAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Spinners
        sizeSpinner.setAdapter(sizeAdapter);
        sizeSpinner.setOnItemSelectedListener(this);

        weight_spinner.setAdapter(weighAdapter);
        weight_spinner.setOnItemSelectedListener(this);

        updateUI();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        if (adapterView.getId() == R.id.size_spinner) {
            switch (position) {
                case 0:
                    mEditor.putString(SIZE, METER);
                    break;
                case 1:
                    mEditor.putString(SIZE, CENTIMETER);
                    break;
                case 2:
                    mEditor.putString(SIZE, MILIMETER);
                    break;
                case 3:
                    mEditor.putString(SIZE, FOOT);
                    break;

            }
        }

        if (adapterView.getId() == R.id.weight_spinner) {
            switch (position) {
                case 0:
                    mEditor.putString(WEIGH, KILOGRAM);
                    break;
                case 1:
                    mEditor.putString(WEIGH, GRAM);
                    break;
                case 2:
                    mEditor.putString(WEIGH, LB);
                    break;
            }
        }

        mEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void updateUI() {
            switch (mSharedPreferences.getString(WEIGH, KILOGRAM)) {
                case KILOGRAM:
                    weight_spinner.setSelection(0);
                    break;
                case GRAM:
                    weight_spinner.setSelection(1);
                    break;
                case LB:
                    weight_spinner.setSelection(2);
                    break;
            }

            switch (mSharedPreferences.getString(SIZE, METER)) {
                case METER:
                    sizeSpinner.setSelection(0);
                    break;
                case CENTIMETER:
                    sizeSpinner.setSelection(1);
                    break;
                case MILIMETER:
                    sizeSpinner.setSelection(2);
                    break;
                case FOOT:
                    sizeSpinner.setSelection(3);
                    break;
            }
    }
}

