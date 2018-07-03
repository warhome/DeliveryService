package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import java.util.Objects;

public class TypeDialog extends DialogFragment {

    public interface TypeDialogCommunicator {
        void onUpdateType(int which, String tag);
    }

    private TypeDialogCommunicator typeDialogCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setItems(R.array.delivery_types, (dialog, which) -> typeDialogCommunicator.onUpdateType(which, getTag()));
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.typeDialogCommunicator = (TypeDialogCommunicator)activity;
    }
}

