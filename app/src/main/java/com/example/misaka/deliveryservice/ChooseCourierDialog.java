package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.misaka.deliveryservice.Consts.COURIERS;


public class ChooseCourierDialog extends DialogFragment {


    public interface ChooseCourierCommunicator {
        void onChooseCourier(String which, String tag);
    }

    private ChooseCourierCommunicator chooseCourierCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        ArrayList<String> couriers = getArguments().getStringArrayList(COURIERS);
        if (couriers != null) {
            CharSequence[] cs = couriers.toArray(new CharSequence[couriers.size()]);
            builder.setItems(cs, (dialogInterface, which) -> chooseCourierCommunicator.onChooseCourier(couriers.get(which), getTag()));
        }
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.chooseCourierCommunicator = (ChooseCourierCommunicator)activity;
    }
}
