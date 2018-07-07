package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Objects;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerCommunicator {
        void onUpdateDate(String year, String month, String day, String tag);
    }

    private DatePickerCommunicator datePickerCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return  new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month + 1, day);

    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        datePickerCommunicator.onUpdateDate(String.valueOf(year), String.valueOf(month),String.valueOf(day), getTag());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.datePickerCommunicator = (DatePickerCommunicator)activity;
    }
}
