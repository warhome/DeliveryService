package com.example.misaka.deliveryservice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ServicesChecker {
    private static final int ERROR_DIALOG_REQUEST = 9192;
    public boolean isServicesOK(Context context, Activity activity) {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if(available == ConnectionResult.SUCCESS) {
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available,
                    ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(context, R.string.GPS_unavailable_error, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
