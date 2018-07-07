package com.example.misaka.deliveryservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
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
import butterknife.BindFont;
import io.reactivex.Observable;
import com.jakewharton.rxbinding2.widget.RxTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.Calendar;

public class AddParcel extends AppCompatActivity implements View.OnClickListener,
        TypeDialog.TypeDialogCommunicator,
        StatusDialog.StatusDialogCommunicator,
        SaveParcelDialog.SaveDialogCommunicator,
        NotificationDialog.NotificationDialogCommunicator,
        DatePicker.DatePickerCommunicator{

    //region ButterKnife binds
    @BindFont(R.font.thin)
    Typeface mRobotoThinTypeface;
    @BindFont(R.font.regular)
    Typeface mRobotoRegularTypeface;
    @BindView(R.id.headTextCustomer)
    TextView headTextCustomer;
    @BindView(R.id.headTextDestination)
    TextView headTextDestination;
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
    @BindView(R.id.customerAddressEditTextLayout)
    TextInputLayout customerAddressEditTextLayout;
    @BindView(R.id.destinationAddressEditTextLayout)
    TextInputLayout destinationAddressEditTextLayout;
    @BindView(R.id.customerFullNameEditTextLayout)
    TextInputLayout customerFullNameEditTextLayout;
    @BindView(R.id.destinationFullNameEditTextLayout)
    TextInputLayout destinationFullNameEditTextLayout;
    @BindView(R.id.customerPhoneNumberEditTextLayout)
    TextInputLayout customerPhoneNumberEditTextLayout;
    @BindView(R.id.destinationPhoneNumberEditTextLayout)
    TextInputLayout destinationPhoneNumberEditTextLayout;
    @BindView(R.id.customerEmailEditTextLayout)
    TextInputLayout customerEmailEditTextLayout;
    @BindView(R.id.destinationEmailEditTextLayout)
    TextInputLayout destinationEmailEditTextLayout;
    @BindView(R.id.customerTypeEditTextLayout)
    TextInputLayout customerCompanyNameEditLayout;
    @BindView(R.id.destinationTypeEditTextLayout)
    TextInputLayout destinationCompanyNameEditLayout;
    @BindView(R.id.parcelNameEditTextLayout)
    TextInputLayout parcelNameEditLayout;
    @BindView(R.id.parcelSizeEditTextLayout)
    TextInputLayout parcelSizeEditLayout;
    @BindView(R.id.parcelWeighEditTextLayout)
    TextInputLayout parcelWeighEditLayout;
    @BindView(R.id.deliveryDateEditTextLayout)
    TextInputLayout deliveryDateEditLayout;
    @BindView(R.id.commentEditTextLayout)
    TextInputLayout commentEditTextLayout;
    @BindView(R.id.latTextValue)
    TextView latTextValue;
    @BindView(R.id.lngTextValue)
    TextView lngTextValue;
    //endregion

    public static final int CALENDAR_VIEW_REQUEST_CODE = 1010;
    public static final int MAP_ACTIVITY_REQUEST_CODE = 9090;

    Parcel parcel;
    PriceCalculator priceCalculator;
    boolean isNewParcel = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAddParcel);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.app_bar_addparcel);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        parcel = new Parcel();
        priceCalculator = new PriceCalculator();

        setCustomerType.setOnClickListener(this);
        setDestinationType.setOnClickListener(this);
        setDestinationType.setText(parcel.getDestinationType());
        setCustomerType.setText(parcel.getCustomerType());
        setCoordinate.setOnClickListener(this);
        setStatus.setOnClickListener(this);
        saveParcel.setOnClickListener(this);
        sendNotification.setOnClickListener(this);

        deliveryDateEdit.setOnClickListener(v -> {
            DatePicker datePicker = new DatePicker();
            datePicker.show(getFragmentManager(), getString(R.string.DATE_PICKER_TAG));
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
        if (intent.hasExtra(getString(R.string.id))) {
            isNewParcel = false;
            AppDatabase database = App.getInstance().getDatabase();
            final ParcelDao parcelDao = database.lessonDao();
            parcel = parcelDao.getById(intent.getIntExtra(getString(R.string.id), 0));
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
                setCustomerTypeDialog.show(getFragmentManager(), getString(R.string.SET_CUSTOMER_TYPE_TAG));
                break;
            case R.id.buttonSetDestinationType:
                TypeDialog setDestinationTypeDialog = new TypeDialog();
                setDestinationTypeDialog.show(getFragmentManager(), getString(R.string.SET_DESTINATION_TYPE_TAG));
                break;
            case R.id.buttonSetCoordinates:
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                if (parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
                    intent.putExtra(getString(R.string.coordinates), parcel.getCoordinates());
                }
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.buttonChangeStatus:
                StatusDialog statusDialog = new StatusDialog();
                statusDialog.show(getFragmentManager(), getString(R.string.SET_PARCEL_TYPE));
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

                    if (parcel.getStatus().equals(getString(R.string.Completed)) && commentEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.Enter_comment, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        parcel.setComment(commentEditText.getText().toString());
                    }

                    if (parcel.getCoordinates() == null || parcel.getCoordinates().isEmpty()) {
                        SaveParcelDialog saveParcelDialog = new SaveParcelDialog();
                        saveParcelDialog.show(getFragmentManager(), getString(R.string.SAVE_PARCEL_TAG));
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
                    sendNotification(getString(R.string.SMS));
                }
                else{
                   NotificationDialog notificationDialog = new NotificationDialog();
                   notificationDialog.show(getFragmentManager(), getString(R.string.NOTIFICATION_DIALOG));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateType(int which, String tag) {
        if(tag.equals(getString(R.string.SET_CUSTOMER_TYPE_TAG))) {
            if (which == 0) {
                parcel.setCustomerType(getString(R.string.Company));
                customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setCustomerType.setText(R.string.Company);
                    } else {
                        parcel.setCustomerType(getString(R.string.Person));
                        customerCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                        setCustomerType.setText(R.string.Person);
                    }
        } else {
            if (which == 0) {
                parcel.setDestinationType(getString(R.string.Company));
                destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setDestinationType.setText(R.string.Company);
            } else {
                parcel.setDestinationType(getString(R.string.Person));
                destinationCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                setDestinationType.setText(R.string.Person);
            }
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
                parcel.setStatus(getString(R.string.Active));
                commentEditTextLayout.setVisibility(View.INVISIBLE);
                sendNotification.setVisibility(View.INVISIBLE);
                break;
            case 1:
                parcel.setStatus(getString(R.string.Completed));
                commentEditTextLayout.setVisibility(View.VISIBLE);
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
                sendNotification(getString(R.string.SMS));
                break;
            case 1:
                sendNotification(getString(R.string.Email));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALENDAR_VIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            String returnString = data.getStringExtra(getString(R.string.data));
            deliveryDateEdit.setText(returnString);
        }
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String returnString = data.getStringExtra(getString(R.string.coordinates));
            parcel.setCoordinates(returnString);
            String[] latlng = returnString.split(getString(R.string.coordinates_delimiter));
            latTextValue.setText(latlng[0]);
            lngTextValue.setText(latlng[1]);
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

        if (parcel.getCustomerType().equals(getString(R.string.Company))) {
            customerCompanyNameEdit.setVisibility(View.VISIBLE);
        }
        if (parcel.getDestinationType().equals(getString(R.string.Company))) {
            destinationCompanyNameEdit.setVisibility(View.VISIBLE);
        }
        if (parcel.getStatus().equals(getString(R.string.Completed))) {
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
        return String.valueOf(Date) + getString(R.string.date_delimiter)
                + String.valueOf(Month + 1) + getString(R.string.date_delimiter)
                + String.valueOf(Year);
    }

    public void setDefaultDeliveryDate() {
        deliveryDateEdit.setText(getDate(1));
    }

    public void sendNotification(String type) {
        if(type.equals(getString(R.string.SMS))) {
            String toSms = getString(R.string.smsto) + customerPhoneNumberEdit.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(toSms));
            intent.putExtra(getString(R.string.sms_body), getString(R.string.notification_title));
            startActivity(intent);
        }
        else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(getString(R.string.mailto),customerEmailEdit.getText().toString(), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.EXTRA_SUBJECT_VALUE));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.notification_title)));
        }
    }

    @Override
    public void onUpdateDate(String year, String month, String day, String tag) {
        String date = day + getString(R.string.date_delimiter) + month + getString(R.string.date_delimiter) + year;
        parcel.setDeliveryDate(date);
        deliveryDateEdit.setText(date);
    }
}
