package com.example.misaka.deliveryservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView textView;
    Button button;
    String date;
    Boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        button = findViewById(R.id.calendarButtonSave);
        textView = findViewById(R.id.textViewDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "-" + (month + 1) + "-" + year;
            isPressed = true;
            textView.setText(date);
        });

        button.setOnClickListener(v -> {
            if(isPressed) {
                Intent intent = new Intent();
                intent.putExtra("data", date);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
