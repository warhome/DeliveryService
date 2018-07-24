package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.Objects;

public class SaveParcelDialog extends DialogFragment {

    public interface SaveDialogCommunicator {
        void isSaveParcel(boolean b, String tag);
    }

    private SaveDialogCommunicator saveDialogCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(getResources().getString(R.string.coordinates_not_found_error))
                .setPositiveButton(R.string.Save_whatever, (dialog, id) -> saveDialogCommunicator.isSaveParcel(true, getTag()))
                .setNegativeButton(R.string.cancel, (dialog, id) -> saveDialogCommunicator.isSaveParcel(false, getTag()));
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.saveDialogCommunicator = (SaveDialogCommunicator) activity;
    }
}

