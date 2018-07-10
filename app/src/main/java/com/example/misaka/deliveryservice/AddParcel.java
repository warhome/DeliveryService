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
import android.util.Patterns;
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
import io.reactivex.Completable;
import io.reactivex.Observable;
import com.jakewharton.rxbinding2.widget.RxTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.Calendar;

public class AddParcel extends AppCompatActivity implements View.OnClickListener,
        TypeDialog.TypeDialogCommunicator,
        StatusDialog.StatusDialogCommunicator,
        SaveParcelDialog.SaveDialogCommunicator,
        NotificationDialog.NotificationDialogCommunicator,
        DatePicker.DatePickerCommunicator,
        View.OnFocusChangeListener{

    private static final String ID_EXTRA = "id";
    private static final int MAP_ACTIVITY_REQUEST_CODE = 9090;
    private static final String CUSTOMER_TAG = "CUSTOMER";
    private static final String DESTINATION_TAG = "DESTINATION";
    private static final String DATE_PICKER_TAG = "DATE_PICKER";
    private static final String COORDINATES = "coordinates";
    private static final String PARCEL_TYPE_TAG = "PARCEL_TYPE";
    private static final String SAVE_PARCEL_TAG = "SAVE_PARCEL";
    private static final String NOTIFICATION_TAG = "NOTIFICATION";
    private static final String COMPANY = "Company";
    private static final String PERSON = "Person";
    private static final String ACTIVE = "Active";
    private static final String COMPLETED = "Completed";
    private static final String ZER0 = "0";
    private static final String DELIMITER = "-";
    private static final String COORDINATES_DELIMITER = ",";
    private static final String SMS = "SMS";
    private static final String EMAIL = "Email";
    private static final String ADDRESS = "address";
    private static final String SMSTO = "smsto:";
    private static final String SMS_BODY = "sms_body";
    private static final String MAILTO = "mailto";

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

    Parcel parcel;
    PriceCalculator priceCalculator;
    boolean isNewParcel = true;
    boolean isParamsOk;

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
        setCoordinate.setOnClickListener(this);
        setStatus.setOnClickListener(this);
        saveParcel.setOnClickListener(this);
        sendNotification.setOnClickListener(this);
        customerAddressEdit.setOnFocusChangeListener(this);
        customerFullNameEdit.setOnFocusChangeListener(this);
        customerEmailEdit.setOnFocusChangeListener(this);
        customerPhoneNumberEdit.setOnFocusChangeListener(this);
        destinationAddressEdit.setOnFocusChangeListener(this);
        destinationFullNameEdit.setOnFocusChangeListener(this);
        destinationEmailEdit.setOnFocusChangeListener(this);
        destinationPhoneNumberEdit.setOnFocusChangeListener(this);
        parcelNameEdit.setOnFocusChangeListener(this);
        parcelWeighEdit.setOnFocusChangeListener(this);
        parcelSizeEdit.setOnFocusChangeListener(this);

        // DatePicker
        deliveryDateEdit.setOnClickListener(v -> {
            DatePicker datePicker = new DatePicker();
            datePicker.show(getFragmentManager(),DATE_PICKER_TAG);
        });

        // Валидация
        Observable.combineLatest(
                RxTextView.textChanges(customerAddressEdit),
                RxTextView.textChanges(customerFullNameEdit),
                RxTextView.textChanges(customerPhoneNumberEdit),
                RxTextView.textChanges(customerEmailEdit),
                RxTextView.textChanges(destinationAddressEdit),
                RxTextView.textChanges(destinationFullNameEdit),
                RxTextView.textChanges(destinationPhoneNumberEdit),
                RxTextView.textChanges(destinationEmailEdit),
                RxTextView.textChanges(parcelNameEdit),
                (s1,s2,s3,s4,s5,s6,s7,s8,s9)
                        -> s1.length() > 0 && s2.length() > 0 && Patterns.PHONE.matcher(s3).matches()
                        && (s4.length() == 0 || Patterns.EMAIL_ADDRESS.matcher(s4).matches())
                        && s5.length() > 0 && s6.length() > 0 && Patterns.PHONE.matcher(s7).matches()
                        && (s8.length() == 0 || Patterns.EMAIL_ADDRESS.matcher(s8).matches())
                        &&  s9.length() > 0
        ).subscribe(aBoolean -> isParamsOk = aBoolean);

        Observable.combineLatest(
                RxTextView.textChanges(parcelWeighEdit),
                RxTextView.textChanges(parcelSizeEdit),
                (s1,s2) ->  s1.length() > 0 && !s1.toString().equals(ZER0) && s2.length() > 0 && !s2.toString().equals(ZER0)
        ).subscribe(aBoolean -> {
            if(isParamsOk) saveParcel.setEnabled(aBoolean);
        });

        // Расчёт цены доставки
        Observable.combineLatest(RxTextView.textChanges(parcelSizeEdit),
                RxTextView.textChanges(parcelWeighEdit),
                RxTextView.textChanges(deliveryDateEdit),
                (s1, s2, s3) -> s1.length() > 0  && !s1.toString().equals(ZER0) && s2.length() > 0  && !s2.toString().equals(ZER0) && s3.length() > 0
        ).subscribe(aBoolean -> {
            if (aBoolean)
                onUpdatePrice(parcelSizeEdit.getText().toString(), parcelWeighEdit.getText().toString(), deliveryDateEdit.getText().toString());
            else
                priceTextView.setText(DELIMITER);
        });

        // Берём данные о посылке из intent
        Intent intent = getIntent();
        if (intent.hasExtra(ID_EXTRA)) {
            isNewParcel = false;
            AppDatabase database = App.getInstance().getDatabase();
            final ParcelDao parcelDao = database.lessonDao();

            parcelDao.getById(intent.getIntExtra(ID_EXTRA, 0))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getParcel -> {
                        parcel = getParcel;
                        setViews(parcel);
                    });
            saveParcel.setEnabled(true);
        } else {
            setDefaultDeliveryDate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSetCustomerType:
                TypeDialog setCustomerTypeDialog = new TypeDialog();
                setCustomerTypeDialog.show(getFragmentManager(),CUSTOMER_TAG);
                break;
            case R.id.buttonSetDestinationType:
                TypeDialog setDestinationTypeDialog = new TypeDialog();
                setDestinationTypeDialog.show(getFragmentManager(),DESTINATION_TAG);
                break;
            case R.id.buttonSetCoordinates:
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                if (parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
                    intent.putExtra(COORDINATES, parcel.getCoordinates());
                }
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.buttonChangeStatus:
                StatusDialog statusDialog = new StatusDialog();
                statusDialog.show(getFragmentManager(),PARCEL_TYPE_TAG);
                break;
            case R.id.buttonSave:
                    onUpdateParcelFromEditTexts(parcel);
                    if (parcel.getStatus().equals(COMPLETED) && commentEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.Enter_comment, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        parcel.setComment(commentEditText.getText().toString());
                    }

                    if (parcel.getCoordinates() == null || parcel.getCoordinates().isEmpty()) {
                        SaveParcelDialog saveParcelDialog = new SaveParcelDialog();
                        saveParcelDialog.show(getFragmentManager(),SAVE_PARCEL_TAG);
                        break;
                    }
                    AppDatabase database = App.getInstance().getDatabase();
                    final ParcelDao parcelDao = database.lessonDao();
                    Completable.fromRunnable(() -> parcelDao.insert(parcel))
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::finish);
                break;
            case R.id.buttonNotification:
                if(customerEmailEdit.getText().toString().isEmpty() && !customerPhoneNumberEdit.getText().toString().isEmpty()) {
                    sendNotification(SMS);
                }
                else{
                   NotificationDialog notificationDialog = new NotificationDialog();
                   notificationDialog.show(getFragmentManager(), NOTIFICATION_TAG);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateType(int which, String tag) {
        if(tag.equals(CUSTOMER_TAG)) {
            if (which == 0) {
                parcel.setCustomerType(COMPANY);
                customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setCustomerType.setText(R.string.btn_company);
                    } else {
                        parcel.setCustomerType(PERSON);
                        customerCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                        setCustomerType.setText(R.string.btn_person);
                    }
        } else {
            if (which == 0) {
                parcel.setDestinationType(COMPANY);
                destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setDestinationType.setText(R.string.btn_company);
            } else {
                parcel.setDestinationType(PERSON);
                destinationCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                setDestinationType.setText(R.string.btn_person);
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
                parcel.setStatus(ACTIVE);
                commentEditTextLayout.setVisibility(View.INVISIBLE);
                sendNotification.setVisibility(View.INVISIBLE);
                break;
            case 1:
                parcel.setStatus(COMPLETED);
                commentEditTextLayout.setVisibility(View.VISIBLE);
                sendNotification.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void isSaveParcel(boolean b, String tag) {
        AppDatabase database = App.getInstance().getDatabase();
        final ParcelDao parcelDao = database.lessonDao();
        if(b) {
            Completable.fromRunnable(() -> parcelDao.insert(parcel))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            finish();
        }
    }

    @Override
    public void onChoiceNotification(int which, String tag) {
        switch (which) {
            case 0:
                sendNotification(SMS);
                break;
            case 1:
                sendNotification(EMAIL);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String returnStringCoordinates = data.getStringExtra(COORDINATES);
            String returnStringAddress = data.getStringExtra(ADDRESS);
            parcel.setCoordinates(returnStringCoordinates);
            if(returnStringAddress != null && !returnStringAddress.isEmpty()) {
                destinationAddressEdit.setText(returnStringAddress);
            }
            String[] latlng = returnStringCoordinates.split(COORDINATES_DELIMITER);
            latTextValue.setText(latlng[0]);
            lngTextValue.setText(latlng[1]);
            destinationAddressEditTextLayout.setErrorEnabled(false);
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

        if (parcel.getCustomerType().equals(COMPANY)) {
            customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
            setCustomerType.setText(R.string.btn_company);
        } else {
            setCustomerType.setText(R.string.btn_person);
        }
        if (parcel.getDestinationType().equals(COMPANY)) {
            destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
            setDestinationType.setText(R.string.btn_company);
        } else {
            setDestinationType.setText(R.string.btn_person);
        }
        if (parcel.getStatus().equals(COMPLETED)) {
            commentEditTextLayout.setVisibility(View.VISIBLE);
            sendNotification.setVisibility(View.VISIBLE);
            commentEditText.setText(parcel.getComment());
        }
        if(!isNewParcel) {
            setStatus.setVisibility(View.VISIBLE);
        }
        if(parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
            String[] latlng = parcel.getCoordinates().split(COORDINATES_DELIMITER);
            latTextValue.setText(latlng[0]);
            lngTextValue.setText(latlng[1]);
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
        return String.valueOf(Date) + DELIMITER
                + String.valueOf(Month + 1) + DELIMITER
                + String.valueOf(Year);
    }

    public void setDefaultDeliveryDate() {
        deliveryDateEdit.setText(getDate(1));
    }

    public void sendNotification(String type) {
        if(type.equals(SMS)) {
            String toSms = SMSTO + customerPhoneNumberEdit.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(toSms));
            intent.putExtra(SMS_BODY, getString(R.string.notification_title));
            startActivity(intent);
        }
        else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(MAILTO,customerEmailEdit.getText().toString(), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.EXTRA_SUBJECT_VALUE));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.notification_title)));
        }
    }

    @Override
    public void onUpdateDate(String year, String month, String day, String tag) {
        String date = day + DELIMITER + month + DELIMITER + year;
        parcel.setDeliveryDate(date);
        deliveryDateEdit.setText(date);
    }

    @Override
    public void onFocusChange(View view, boolean isFocused) {
        if (isFocused) return;
        switch (view.getId()) {
            case R.id.customerAddressEditText:
                if(customerAddressEdit.getText().toString().isEmpty()) {
                    customerAddressEditTextLayout.setErrorEnabled(true);
                    customerAddressEditTextLayout.setError(getString(R.string.empty_string_error));
                } else customerAddressEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.customerFullNameEditText:
                if(customerFullNameEdit.getText().toString().isEmpty()) {
                    customerFullNameEditTextLayout.setErrorEnabled(true);
                    customerFullNameEditTextLayout.setError(getString(R.string.empty_string_error));
                } else customerFullNameEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.customerPhoneNumberEditText:
                if(!Patterns.PHONE.matcher(customerPhoneNumberEdit.getText().toString()).matches()) {
                    customerPhoneNumberEditTextLayout.setErrorEnabled(true);
                    customerPhoneNumberEditTextLayout.setError(getString(R.string.error_phone_number));
                }
                else customerPhoneNumberEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.customerEmailEditText:
                if( !customerEmailEdit.getText().toString().isEmpty()
                        && !Patterns.EMAIL_ADDRESS.matcher(customerEmailEdit.getText().toString()).matches() ) {
                    customerEmailEditTextLayout.setErrorEnabled(true);
                    customerEmailEditTextLayout.setError(getString(R.string.error_email_format));
                } else customerEmailEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.destinationAddressEditText:
                if(destinationAddressEdit.getText().toString().isEmpty()) {
                    destinationAddressEditTextLayout.setErrorEnabled(true);
                    destinationAddressEditTextLayout.setError(getString(R.string.empty_string_error));
                } else destinationAddressEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.destinationFullNameEditText:
                if(destinationFullNameEdit.getText().toString().isEmpty()) {
                    destinationFullNameEditTextLayout.setErrorEnabled(true);
                    destinationFullNameEditTextLayout.setError(getString(R.string.empty_string_error));
                } else destinationFullNameEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.destinationPhoneNumberEditText:
                if(!Patterns.PHONE.matcher(destinationPhoneNumberEdit.getText().toString()).matches()) {
                    destinationPhoneNumberEditTextLayout.setErrorEnabled(true);
                    destinationPhoneNumberEditTextLayout.setError(getString(R.string.error_phone_number));
                }
                else destinationPhoneNumberEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.destinationEmailEditText:
                if( !destinationEmailEdit.getText().toString().isEmpty()
                        && !Patterns.EMAIL_ADDRESS.matcher(destinationEmailEdit.getText().toString()).matches() ) {
                    destinationEmailEditTextLayout.setErrorEnabled(true);
                    destinationEmailEditTextLayout.setError(getString(R.string.error_email_format));
                } else destinationEmailEditTextLayout.setErrorEnabled(false);
                break;
            case R.id.parcelNameEditText:
                if(parcelNameEdit.getText().toString().isEmpty()) {
                    parcelNameEditLayout.setErrorEnabled(true);
                    parcelNameEditLayout.setError(getString(R.string.empty_string_error));
                } else parcelNameEditLayout.setErrorEnabled(false);
                break;
            case R.id.parcelWeighEditText:
                if(parcelWeighEdit.getText().toString().isEmpty()||parcelWeighEdit.getText().toString().equals("0")) {
                    parcelWeighEditLayout.setErrorEnabled(true);
                } else parcelWeighEditLayout.setErrorEnabled(false);
                break;
            case R.id.parcelSizeEditText:
                if(parcelSizeEdit.getText().toString().isEmpty()||parcelSizeEdit.getText().toString().equals("0")) {
                    parcelSizeEditLayout.setErrorEnabled(true);
                } else parcelSizeEditLayout.setErrorEnabled(false);
                break;
        }
    }
}
