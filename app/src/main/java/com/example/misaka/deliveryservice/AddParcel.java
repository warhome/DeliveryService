package com.example.misaka.deliveryservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.misaka.deliveryservice.db.App;
import com.example.misaka.deliveryservice.db.AppDatabase;
import com.example.misaka.deliveryservice.db.Parcel;
import com.example.misaka.deliveryservice.db.ParcelDao;
import io.reactivex.Observable;
import com.jakewharton.rxbinding2.widget.RxTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.Calendar;

public class AddParcel extends AppCompatActivity implements View.OnClickListener,
        TypeDialog.TypeDialogCommunicator,
        StatusDialog.StatusDialogCommunicator,
        SaveParcelDialog.SaveDialogCommunicator,
        NotificationDialog.NotificationDialogCommunicator {

    public static final int CALENDAR_VIEW_REQUEST_CODE = 1010;
    public static final int MAP_ACTIVITY_REQUEST_CODE = 9090;

    @BindView(R.id.customerAddressEditText)
    EditText customerAddressEdit;
    @BindView(R.id.customerFullNameEditText)
    EditText customerFullNameEdit;
    @BindView(R.id.customerEmailEditText)
    EditText customerEmailEdit;
    @BindView(R.id.customerPhoneNumberEditText)
    EditText customerPhoneNumberEdit;
    @BindView(R.id.customerTypeEditText)
    EditText customerCompanyNameEdit;
    @BindView(R.id.destinationAddressEditText)
    EditText destinationAddressEdit;
    @BindView(R.id.destinationFullNameEditText)
    EditText destinationFullNameEdit;
    @BindView(R.id.destinationEmailEditText)
    EditText destinationEmailEdit;
    @BindView(R.id.destinationPhoneNumberEditText)
    EditText destinationPhoneNumberEdit;
    @BindView(R.id.destinationTypeEditText)
    EditText destinationCompanyNameEdit;
    @BindView(R.id.parcelNameEditText)
    EditText parcelNameEdit;
    @BindView(R.id.parcelSizeEditText)
    EditText parcelSizeEdit;
    @BindView(R.id.parcelWeighEditText)
    EditText parcelWeighEdit;
    @BindView(R.id.deliveryDateEditText)
    EditText deliveryDateEdit;
    @BindView(R.id.commentEditText)
    EditText commentEditText;
    // TODO: Заменить на BindViews
    @BindView(R.id.buttonSetCustomerType)
    Button setCustomerType;
    @BindView(R.id.buttonSetDestinationType)
    Button setDestinationType;
    @BindView(R.id.buttonSetCoordinates)
    Button setCoordinate;
    @BindView(R.id.buttonChangeStatus)
    Button setStatus;
    @BindView(R.id.buttonSave)
    Button saveParcel;
    @BindView(R.id.buttonNotification)
    Button sendNotification;
    @BindView(R.id.priceTextViewValue)
    TextView priceTextView;

    Parcel parcel;
    PriceCalculator priceCalculator;
    boolean isNewParcel = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        parcel = new Parcel();
        priceCalculator = new PriceCalculator();

        setCustomerType.setOnClickListener(this);
        setDestinationType.setOnClickListener(this);
        setCoordinate.setOnClickListener(this);
        setStatus.setOnClickListener(this);
        saveParcel.setOnClickListener(this);
        sendNotification.setOnClickListener(this);

        deliveryDateEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CalendarActivity.class);
            startActivityForResult(intent, CALENDAR_VIEW_REQUEST_CODE);
        });

        Observable.combineLatest(RxTextView.textChanges(parcelSizeEdit),
                RxTextView.textChanges(parcelWeighEdit),
                RxTextView.textChanges(deliveryDateEdit),
                (s1, s2, s3) -> s1.length() > 0 && s2.length() > 0 && s3.length() > 0
        ).subscribe(aBoolean -> {
            if (aBoolean)
                onUpdatePrice(parcelSizeEdit.getText().toString(), parcelWeighEdit.getText().toString(), deliveryDateEdit.getText().toString());
        });

        // Берём данные о посылке из intent
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            isNewParcel = false;
            AppDatabase database = App.getInstance().getDatabase();
            final ParcelDao parcelDao = database.lessonDao();
            parcel = parcelDao.getById(intent.getIntExtra("id", 0));
            setViews(parcel);
        } else {
            setDefaultDeliveryDate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSetCustomerType:
                TypeDialog setCustomerTypeDialog = new TypeDialog();
                setCustomerTypeDialog.show(getFragmentManager(), "SET_CUSTOMER_TYPE_TAG");
                break;
            case R.id.buttonSetDestinationType:
                TypeDialog setDestinationTypeDialog = new TypeDialog();
                setDestinationTypeDialog.show(getFragmentManager(), "SET_DESTINATION_TYPE_TAG");
                break;
            case R.id.buttonSetCoordinates:
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                if (parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
                    intent.putExtra("coordinates", parcel.getCoordinates());
                }
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.buttonChangeStatus:
                StatusDialog statusDialog = new StatusDialog();
                statusDialog.show(getFragmentManager(), "SET_PARCEL_TYPE");
                break;
            case R.id.buttonSave:
                if (!customerAddressEdit.getText().toString().isEmpty()
                        && !customerFullNameEdit.getText().toString().isEmpty()
                        && !customerPhoneNumberEdit.getText().toString().isEmpty()
                        && !destinationAddressEdit.getText().toString().isEmpty()
                        && !destinationFullNameEdit.getText().toString().isEmpty()
                        && !destinationPhoneNumberEdit.getText().toString().isEmpty()
                        && !parcelNameEdit.getText().toString().isEmpty()
                        && !parcelSizeEdit.getText().toString().isEmpty()
                        && !parcelWeighEdit.getText().toString().isEmpty()) {

                    onUpdateParcelFromEditTexts(parcel);

                    if (parcel.getStatus().equals("Completed") && commentEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Введите комментарий", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        parcel.setComment(commentEditText.getText().toString());
                    }

                    if (parcel.getCoordinates() == null || parcel.getCoordinates().isEmpty()) {
                        SaveParcelDialog saveParcelDialog = new SaveParcelDialog();
                        saveParcelDialog.show(getFragmentManager(), "SAVE_PARCEL_TAG");
                        break;
                    }

                    AppDatabase database = App.getInstance().getDatabase();
                    final ParcelDao parcelDao = database.lessonDao();
                    if (isNewParcel)
                        parcelDao.insert(parcel);
                    else
                        parcelDao.update(parcel);
                    finish();

                } else {
                    // TODO: Вывести AssistiveText(?) для TextInputLayout
                    Toast.makeText(getApplicationContext(), "Заполните все необходимые поля", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonNotification:
                if(customerEmailEdit.getText().toString().isEmpty() && !customerPhoneNumberEdit.getText().toString().isEmpty()) {
                    sendNotification("SMS");
                }
                else{
                   NotificationDialog notificationDialog = new NotificationDialog();
                   notificationDialog.show(getFragmentManager(), "NOTIFICATION_DIALOG");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateType(int which, String tag) {
        switch (tag) {
            case "SET_CUSTOMER_TYPE_TAG":
                if (which == 0) {
                    parcel.setCustomerType("Company");
                    customerCompanyNameEdit.setVisibility(View.VISIBLE);
                } else {
                    parcel.setCustomerType("Person");
                    customerCompanyNameEdit.setVisibility(View.INVISIBLE);
                }
                break;
            case "SET_DESTINATION_TYPE_TAG":
                if (which == 0) {
                    parcel.setDestinationType("Company");
                    destinationCompanyNameEdit.setVisibility(View.VISIBLE);

                } else {
                    parcel.setDestinationType("Person");
                    destinationCompanyNameEdit.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    public void onUpdatePrice(String size, String weigh, String deliveryDate) {
        String days = priceCalculator.CalculateDeliveryDays(getDate(0), deliveryDate);
        priceTextView.setText(String.valueOf(priceCalculator.CalculateInKgM(size, weigh, days)));
    }

    @Override
    public void onUpdateStatus(int which, String tag) {
        switch (which) {
            case 0:
                parcel.setStatus("Active");
                commentEditText.setVisibility(View.INVISIBLE);
                sendNotification.setVisibility(View.INVISIBLE);
                break;
            case 1:
                parcel.setStatus("Completed");
                commentEditText.setVisibility(View.VISIBLE);
                sendNotification.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void isSaveParcel(boolean b, String tag) {
        AppDatabase database = App.getInstance().getDatabase();
        final ParcelDao parcelDao = database.lessonDao();
        if(b) {
            if (isNewParcel)
                parcelDao.insert(parcel);
            else
                parcelDao.update(parcel);
            finish();
        }
    }

    @Override
    public void onChoiceNotification(int which, String tag) {
        switch (which) {
            case 0:
                sendNotification("SMS");
                break;
            case 1:
                sendNotification("Email");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALENDAR_VIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            String returnString = data.getStringExtra("data");
            deliveryDateEdit.setText(returnString);
        }
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String returnString = data.getStringExtra("coordinates");
            parcel.setCoordinates(returnString);
        }
    }

    public void setViews(Parcel parcel) {
        customerAddressEdit.setText(parcel.getCustomerAddress());
        customerFullNameEdit.setText(parcel.getCustomerFullName());
        customerEmailEdit.setText(parcel.getCustomerEmail());
        customerPhoneNumberEdit.setText(parcel.getCustomerPhoneNumber());
        customerCompanyNameEdit.setText(parcel.getCustomerCompanyName());

        destinationAddressEdit.setText(parcel.getDestinationAddress());
        destinationFullNameEdit.setText(parcel.getDestinationFullName());
        destinationEmailEdit.setText(parcel.getDestinationEmail());
        destinationPhoneNumberEdit.setText(parcel.getDestinationPhoneNumber());
        destinationCompanyNameEdit.setText(parcel.getDestinationCompanyName());

        parcelNameEdit.setText(parcel.getParcelName());
        parcelSizeEdit.setText(parcel.getParcelSize());
        parcelWeighEdit.setText(parcel.getParcelWeigh());
        deliveryDateEdit.setText(parcel.getDeliveryDate());
        priceTextView.setText(parcel.getPrice());

        if (parcel.getCustomerType().equals("Company")) {
            customerCompanyNameEdit.setVisibility(View.VISIBLE);
        }
        if (parcel.getDestinationType().equals("Company")) {
            destinationCompanyNameEdit.setVisibility(View.VISIBLE);
        }
        if (parcel.getStatus().equals("Completed")) {
            commentEditText.setVisibility(View.VISIBLE);
            sendNotification.setVisibility(View.VISIBLE);
            commentEditText.setText(parcel.getComment());
        }
        if(!isNewParcel) {
            setStatus.setVisibility(View.VISIBLE);
        }
    }

    public void onUpdateParcelFromEditTexts(Parcel parcel) {
        parcel.setCustomerAddress(customerAddressEdit.getText().toString());
        parcel.setCustomerFullName(customerFullNameEdit.getText().toString());
        parcel.setCustomerEmail(customerEmailEdit.getText().toString());
        parcel.setCustomerPhoneNumber(customerPhoneNumberEdit.getText().toString());
        parcel.setCustomerCompanyName(customerCompanyNameEdit.getText().toString());
        parcel.setDestinationAddress(destinationAddressEdit.getText().toString());
        parcel.setDestinationFullName(destinationFullNameEdit.getText().toString());
        parcel.setDestinationEmail(destinationEmailEdit.getText().toString());
        parcel.setDestinationPhoneNumber(destinationPhoneNumberEdit.getText().toString());
        parcel.setDestinationCompanyName(destinationCompanyNameEdit.getText().toString());
        parcel.setParcelName(parcelNameEdit.getText().toString());
        parcel.setParcelSize(parcelSizeEdit.getText().toString());
        parcel.setParcelWeigh(parcelWeighEdit.getText().toString());
        parcel.setPrice(priceTextView.getText().toString());
        parcel.setDeliveryDate(deliveryDateEdit.getText().toString());
    }

    public String getDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        int Date = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);
        return String.valueOf(Date) + '-' + String.valueOf(Month + 1) + '-' + String.valueOf(Year);
    }

    public void setDefaultDeliveryDate() {
        deliveryDateEdit.setText(getDate(1));
    }

    public void sendNotification(String type) {
        if(type.equals("SMS")) {
            String toSms = "smsto:" + customerPhoneNumberEdit.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(toSms));
            intent.putExtra("sms_body", "Доставка DeliveryService!");
            startActivity(intent);
        }
        else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",customerEmailEdit.getText().toString(), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DeliveryService");
            startActivity(Intent.createChooser(emailIntent, "Доставка DeliveryService!"));
        }
    }
}
