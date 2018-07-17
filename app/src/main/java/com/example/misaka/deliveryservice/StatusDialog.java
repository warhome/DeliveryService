package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.Objects;

import static com.example.misaka.deliveryservice.Consts.PARCEL_TYPE_TAG_COURIER;

public class StatusDialog extends DialogFragment {

    public interface StatusDialogCommunicator {
        void onUpdateStatus(int which, String tag);
    }

    private StatusDialogCommunicator statusDialogCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        if(getTag().equals(PARCEL_TYPE_TAG_COURIER))
            builder.setItems(R.array.status_types_courier, (dialog, which) -> statusDialogCommunicator.onUpdateStatus(which, getTag()));
        else
            builder.setItems(R.array.status_types_admin, (dialog, which) -> statusDialogCommunicator.onUpdateStatus(which, getTag()));
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.statusDialogCommunicator = (StatusDialogCommunicator)activity;
    }
}
