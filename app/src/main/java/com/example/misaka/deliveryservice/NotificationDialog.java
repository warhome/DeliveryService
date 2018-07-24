package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.Objects;

public class NotificationDialog extends DialogFragment {

    public interface NotificationDialogCommunicator {
        void onChoiceNotification(int which, String tag);
    }

    NotificationDialogCommunicator notificationDialogCommunicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setItems(R.array.notification_params, (dialog, which) -> notificationDialogCommunicator.onChoiceNotification(which, getTag()));
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.notificationDialogCommunicator = (NotificationDialogCommunicator) activity;
    }
}
