package com.example.misaka.deliveryservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.deliveryservice.db.Parcel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import butterknife.BindFont;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static com.example.misaka.deliveryservice.Consts.ADDRESS;
import static com.example.misaka.deliveryservice.Consts.ASSIGNED;
import static com.example.misaka.deliveryservice.Consts.CANCELED;
import static com.example.misaka.deliveryservice.Consts.COMPANY_TAG;
import static com.example.misaka.deliveryservice.Consts.COMPLETED;
import static com.example.misaka.deliveryservice.Consts.COORDINATES;
import static com.example.misaka.deliveryservice.Consts.COORDINATES_DELIMITER;
import static com.example.misaka.deliveryservice.Consts.COURIERS;
import static com.example.misaka.deliveryservice.Consts.DELIMITER;
import static com.example.misaka.deliveryservice.Consts.ID_EXTRA;
import static com.example.misaka.deliveryservice.Consts.IN_PROCESS;
import static com.example.misaka.deliveryservice.Consts.IS_ADMIN;
import static com.example.misaka.deliveryservice.Consts.KILOGRAM;
import static com.example.misaka.deliveryservice.Consts.METER;
import static com.example.misaka.deliveryservice.Consts.NEW;
import static com.example.misaka.deliveryservice.Consts.PARCELS;
import static com.example.misaka.deliveryservice.Consts.PARCEL_TYPE_TAG_ADMIN;
import static com.example.misaka.deliveryservice.Consts.PARCEL_TYPE_TAG_COURIER;
import static com.example.misaka.deliveryservice.Consts.PERSON_TAG;
import static com.example.misaka.deliveryservice.Consts.SIZE;
import static com.example.misaka.deliveryservice.Consts.WEIGH;

public class AddParcel extends AppCompatActivity implements View.OnClickListener,
        TypeDialog.TypeDialogCommunicator,
        StatusDialog.StatusDialogCommunicator,
        SaveParcelDialog.SaveDialogCommunicator,
        NotificationDialog.NotificationDialogCommunicator,
        DatePicker.DatePickerCommunicator,
        ChooseCourierDialog.ChooseCourierCommunicator {

    //region const
    private static final int MAP_ACTIVITY_REQUEST_CODE = 9090;
    private static final String CUSTOMER_TAG = "CUSTOMER";
    private static final String DESTINATION_TAG = "DESTINATION";
    private static final String DATE_PICKER_TAG = "DATE_PICKER";
    private static final String SAVE_PARCEL_TAG = "SAVE_PARCEL";
    private static final String NOTIFICATION_TAG = "NOTIFICATION";
    private static final String CHOOSE_COURIER_TAG = "CHOOSE_COURIER";
    private static final String ZER0 = "0";
    private static final String SMS = "SMS";
    private static final String EMAIL = "Email";
    private static final String SMSTO = "smsto:";
    private static final String SMS_BODY = "sms_body";
    private static final String MAILTO = "mailto";
    private static final String PHONE_NUMBER_REGEX = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    private static final String ZEROS_REGEX = "^0+$";
    private static final String EMPTY_STRING = "";
    //endregion

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
    @BindView(R.id.courierChooseEditText)
    EditText chooseCourierEditText;
    @BindView(R.id.courierChooseEditTextLayout)
    TextInputLayout chooseCourierEditTextLayout;
    @BindView(R.id.latTextValue)
    TextView latTextValue;
    @BindView(R.id.lngTextValue)
    TextView lngTextValue;
    //endregion

    Parcel parcel;
    PriceCalculator priceCalculator;
    boolean isNewParcel = true;
    boolean isAdmin;
    SharedPreferences mSharedPref;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String FirebaseKey;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isAdmin = mSharedPref.getBoolean(IS_ADMIN, false);

        parcel = new Parcel();
        priceCalculator = new PriceCalculator();

        setCustomerType.setOnClickListener(this);
        setDestinationType.setOnClickListener(this);
        setCoordinate.setOnClickListener(this);
        setStatus.setOnClickListener(this);
        saveParcel.setOnClickListener(this);
        sendNotification.setOnClickListener(this);
        chooseCourierEditText.setOnClickListener(this);

        // DatePicker
        deliveryDateEdit.setOnClickListener(v -> {
            DatePicker datePicker = new DatePicker();
            datePicker.show(getFragmentManager(), DATE_PICKER_TAG);
        });

        // Расчёт цены доставки
        Observable.combineLatest(RxTextView.textChanges(parcelSizeEdit),
                RxTextView.textChanges(parcelWeighEdit),
                RxTextView.textChanges(deliveryDateEdit),
                (s1, s2, s3) -> s1.length() > 0 && s2.length() > 0 && s3.length() > 0
        ).subscribe(aBoolean -> {
            if (aBoolean)
                onUpdatePrice(parcelSizeEdit.getText().toString(), parcelWeighEdit.getText().toString(), deliveryDateEdit.getText().toString());
            else
                priceTextView.setText(DELIMITER);
        });

        setTextWatcher(parcelSizeEdit);
        setTextWatcher(parcelWeighEdit);

        // Берём данные о посылке из intent
        Intent intent = getIntent();
        if (intent.hasExtra(ID_EXTRA)) {
            isNewParcel = false;
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child(PARCELS);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if (mDataSnapshot.getValue(Parcel.class).getId().equals(intent.getStringExtra(ID_EXTRA))) {
                            parcel = mDataSnapshot.getValue(Parcel.class);
                            FirebaseKey = mDataSnapshot.getKey();
                            setViews(parcel);
                            saveParcel.setEnabled(true);
                            onCheckInstanceState(savedInstanceState);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            setTitle(getString(R.string.edit_parcel));
        } else {
            setDefaultDeliveryDate();
            onCheckInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSetCustomerType:
                TypeDialog setCustomerTypeDialog = new TypeDialog();
                setCustomerTypeDialog.show(getFragmentManager(), CUSTOMER_TAG);
                break;
            case R.id.buttonSetDestinationType:
                TypeDialog setDestinationTypeDialog = new TypeDialog();
                setDestinationTypeDialog.show(getFragmentManager(), DESTINATION_TAG);
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
                if (isAdmin)
                    statusDialog.show(getFragmentManager(), PARCEL_TYPE_TAG_ADMIN);
                else
                    statusDialog.show(getFragmentManager(), PARCEL_TYPE_TAG_COURIER);
                break;
            case R.id.buttonSave:
                if (isValidEditTexts()) {
                    onUpdateParcelFromEditTexts(parcel);

                    // Check for empty comment
                    if ((parcel.getStatus().equals(COMPLETED) || parcel.getStatus().equals(CANCELED))
                            && commentEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.Enter_comment, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        parcel.setComment(commentEditText.getText().toString());
                    }

                    // Check for empty coordinates
                    if (parcel.getCoordinates() == null || parcel.getCoordinates().isEmpty()) {
                        SaveParcelDialog saveParcelDialog = new SaveParcelDialog();
                        saveParcelDialog.show(getFragmentManager(), SAVE_PARCEL_TAG);
                        break;
                    }

                    // Check if courier doesn't choose
                    if (parcel.getStatus().equals(ASSIGNED) && (parcel.getAssignedTo() == null || parcel.getAssignedTo().isEmpty())) {
                        Toast.makeText(this, R.string.courier_does_not_choose_error, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    // Save parcel
                    if (mUser != null) {
                        if (!isNewParcel) {
                            DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference().child(PARCELS).child(FirebaseKey);
                            deleteRef.removeValue();
                        } else {
                            parcel.setAddedBy(mUser.getEmail());
                            parcel.setAssignedTo(EMPTY_STRING);
                        }
                        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(PARCELS);
                        String key = mDatabaseReference.push().getKey();
                        mDatabaseReference.child(key).setValue(parcel);
                        mDatabaseReference.child(key).child(ID_EXTRA).setValue(key);
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.invalid_fields_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonNotification:
                if (customerEmailEdit.getText().toString().isEmpty() && !customerPhoneNumberEdit.getText().toString().isEmpty()) {
                    sendNotification(SMS);
                } else {
                    NotificationDialog notificationDialog = new NotificationDialog();
                    notificationDialog.show(getFragmentManager(), NOTIFICATION_TAG);
                }
                break;
            case R.id.courierChooseEditText:
                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child(COURIERS);
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> couriers = new ArrayList<>();
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                            couriers.add(mDataSnapshot.getValue(String.class));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList(COURIERS, couriers);
                        ChooseCourierDialog chooseCourierDialog = new ChooseCourierDialog();
                        chooseCourierDialog.setArguments(bundle);
                        chooseCourierDialog.show(getFragmentManager(), CHOOSE_COURIER_TAG);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateType(int which, String tag) {
        if (tag.equals(CUSTOMER_TAG)) {
            if (which == 0) {
                parcel.setCustomerType(COMPANY_TAG);
                customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setCustomerType.setText(R.string.btn_company);
            } else {
                parcel.setCustomerType(PERSON_TAG);
                customerCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                setCustomerType.setText(R.string.btn_person);
            }
        } else {
            if (which == 0) {
                parcel.setDestinationType(COMPANY_TAG);
                destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
                setDestinationType.setText(R.string.btn_company);
            } else {
                parcel.setDestinationType(PERSON_TAG);
                destinationCompanyNameEditLayout.setVisibility(View.INVISIBLE);
                setDestinationType.setText(R.string.btn_person);
            }
        }
    }

    public void onUpdatePrice(String size, String weigh, String deliveryDate) {
        String days = priceCalculator.CalculateDeliveryDays(getDate(0), deliveryDate);
        priceTextView.setText(String.valueOf(priceCalculator.CalculatePrice(
                size,
                weigh,
                days,
                mSharedPref.getString(SIZE, METER),
                mSharedPref.getString(WEIGH, KILOGRAM))));
    }

    @Override
    public void onUpdateStatus(int which, String tag) {
        switch (which) {
            case 0:
                if (tag.equals(PARCEL_TYPE_TAG_COURIER)) {
                    parcel.setStatus(IN_PROCESS);
                    commentEditTextLayout.setVisibility(View.INVISIBLE);
                    sendNotification.setVisibility(View.INVISIBLE);
                } else {
                    parcel.setStatus(ASSIGNED);
                    commentEditTextLayout.setVisibility(View.INVISIBLE);
                    sendNotification.setVisibility(View.INVISIBLE);
                    chooseCourierEditTextLayout.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (tag.equals(PARCEL_TYPE_TAG_COURIER)) {
                    parcel.setStatus(CANCELED);
                    commentEditTextLayout.setVisibility(View.VISIBLE);
                    sendNotification.setVisibility(View.INVISIBLE);
                } else {
                    parcel.setStatus(COMPLETED);
                    commentEditTextLayout.setVisibility(View.VISIBLE);
                    sendNotification.setVisibility(View.VISIBLE);
                    chooseCourierEditTextLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case 2:
                parcel.setStatus(COMPLETED);
                commentEditTextLayout.setVisibility(View.VISIBLE);
                sendNotification.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void isSaveParcel(boolean b, String tag) {
        if (b) {
            // Save parcel with empty coordinates
            if (mUser != null) {
                if (!isNewParcel) {
                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference().child(PARCELS).child(FirebaseKey);
                    deleteRef.removeValue();
                } else {
                    parcel.setAddedBy(mUser.getEmail());
                    parcel.setAssignedTo(EMPTY_STRING);
                }
                parcel.setAddedBy(mUser.getEmail());
                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(PARCELS);
                String key = mDatabaseReference.push().getKey();
                mDatabaseReference.child(key).setValue(parcel);
                mDatabaseReference.child(key).child(ID_EXTRA).setValue(key);
            }
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
            if (returnStringAddress != null && !returnStringAddress.isEmpty()) {
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
        parcelSizeEdit.setText(String.valueOf(priceCalculator.SizeFromMeters(parcel.getParcelSize(), mSharedPref.getString(SIZE,METER))));
        parcelWeighEdit.setText(String.valueOf(priceCalculator.WeighFromKilograms(parcel.getParcelWeigh(), mSharedPref.getString(WEIGH, KILOGRAM))));
        parcelSizeEditLayout.setHelperText(mSharedPref.getString(SIZE, METER));
        parcelSizeEditLayout.setHelperTextEnabled(true);
        parcelWeighEditLayout.setHelperText(mSharedPref.getString(WEIGH, METER));
        parcelWeighEditLayout.setHelperTextEnabled(true);
        deliveryDateEdit.setText(parcel.getDeliveryDate());
        priceTextView.setText(parcel.getPrice());

        if (parcel.getCustomerType().equals(COMPANY_TAG)) {
            customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
            setCustomerType.setText(R.string.btn_company);
        } else {
            setCustomerType.setText(R.string.btn_person);
        }
        if (parcel.getDestinationType().equals(COMPANY_TAG)) {
            destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
            setDestinationType.setText(R.string.btn_company);
        } else {
            setDestinationType.setText(R.string.btn_person);
        }

        if (parcel.getStatus().equals(COMPLETED) || parcel.getStatus().equals(CANCELED)) {
            commentEditTextLayout.setVisibility(View.VISIBLE);
            commentEditText.setText(parcel.getComment());
        }

        if (parcel.getStatus().equals(COMPLETED)) {
            sendNotification.setVisibility(View.VISIBLE);
        }

        if (isAdmin) setStatus.setVisibility(View.VISIBLE);
        else if (!isNewParcel && !parcel.getStatus().equals(NEW) && !parcel.getStatus().equals(COMPLETED) && !parcel.getStatus().equals(CANCELED)) {
            setStatus.setVisibility(View.VISIBLE);
        }
        if (parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
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
        parcel.setParcelSize(String.valueOf(priceCalculator.SizeToMeters(parcelSizeEdit.getText().toString(),
                    mSharedPref.getString(SIZE, METER))));
        parcel.setParcelWeigh(String.valueOf(priceCalculator.WeighToKilograms(parcelWeighEdit.getText().toString(),
                mSharedPref.getString(WEIGH, KILOGRAM))));
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
        if (type.equals(SMS)) {
            String toSms = SMSTO + customerPhoneNumberEdit.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(toSms));
            intent.putExtra(SMS_BODY, getString(R.string.notification_title));
            startActivity(intent);
        } else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(MAILTO, customerEmailEdit.getText().toString(), null));
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
    protected void onSaveInstanceState(Bundle outState) {
        if (parcel.getCoordinates() != null && !parcel.getCoordinates().isEmpty()) {
            outState.putString(COORDINATES, parcel.getCoordinates());
        }
        outState.putString(CUSTOMER_TAG, setCustomerType.getText().toString());
        outState.putString(DESTINATION_TAG, setDestinationType.getText().toString());
        super.onSaveInstanceState(outState);

    }

    public void onCheckInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(COORDINATES) != null && !savedInstanceState.getString(COORDINATES).isEmpty()) {
                String[] latlng = Objects.requireNonNull(savedInstanceState.getString(COORDINATES)).split(COORDINATES_DELIMITER);
                parcel.setCoordinates(savedInstanceState.getString(COORDINATES));
                latTextValue.setText(latlng[0]);
                lngTextValue.setText(latlng[1]);
            }
            if (savedInstanceState.getString(CUSTOMER_TAG).equals(getString(R.string.btn_company)))
                customerCompanyNameEditLayout.setVisibility(View.VISIBLE);
            if (savedInstanceState.getString(DESTINATION_TAG).equals(getString(R.string.btn_company)))
                destinationCompanyNameEditLayout.setVisibility(View.VISIBLE);
            setCustomerType.setText(savedInstanceState.getString(CUSTOMER_TAG));
            setDestinationType.setText(savedInstanceState.getString(DESTINATION_TAG));
        }
    }

    @Override
    public void onChooseCourier(String which, String tag) {
        if (which != null && !which.isEmpty()) {
            parcel.setAssignedTo(which);
            parcel.setAddedBy(EMPTY_STRING);
            chooseCourierEditText.setText(which);
        }
    }

    public boolean isValidEditTexts() {
        boolean isValid = true;
        if (customerAddressEdit.getText().toString().isEmpty()) {
            customerAddressEditTextLayout.setErrorEnabled(true);
            customerAddressEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else customerAddressEditTextLayout.setErrorEnabled(false);

        if (customerFullNameEdit.getText().toString().isEmpty()) {
            customerFullNameEditTextLayout.setErrorEnabled(true);
            customerFullNameEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else customerFullNameEditTextLayout.setErrorEnabled(false);

        if (!Pattern.compile(PHONE_NUMBER_REGEX).matcher(customerPhoneNumberEdit.getText().toString()).matches()) {
            customerPhoneNumberEditTextLayout.setErrorEnabled(true);
            customerPhoneNumberEditTextLayout.setError(getString(R.string.error_phone_number));
            isValid = false;
        } else customerPhoneNumberEditTextLayout.setErrorEnabled(false);

        if (!customerEmailEdit.getText().toString().isEmpty()&&!Patterns.EMAIL_ADDRESS.matcher(customerEmailEdit.getText().toString()).matches()) {
            customerEmailEditTextLayout.setErrorEnabled(true);
            customerEmailEditTextLayout.setError(getString(R.string.error_email_format));
            isValid = false;
        } else customerEmailEditTextLayout.setErrorEnabled(false);

        if (destinationAddressEdit.getText().toString().isEmpty()) {
            destinationAddressEditTextLayout.setErrorEnabled(true);
            destinationAddressEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else destinationAddressEditTextLayout.setErrorEnabled(false);

        if (destinationFullNameEdit.getText().toString().isEmpty()) {
            destinationFullNameEditTextLayout.setErrorEnabled(true);
            destinationFullNameEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else destinationFullNameEditTextLayout.setErrorEnabled(false);

        if (!Pattern.compile(PHONE_NUMBER_REGEX).matcher(destinationPhoneNumberEdit.getText().toString()).matches()) {
            destinationPhoneNumberEditTextLayout.setErrorEnabled(true);
            destinationPhoneNumberEditTextLayout.setError(getString(R.string.error_phone_number));
            isValid = false;
        } else destinationPhoneNumberEditTextLayout.setErrorEnabled(false);

        if (!destinationEmailEdit.getText().toString().isEmpty()&&!Patterns.EMAIL_ADDRESS.matcher(destinationEmailEdit.getText().toString()).matches()) {
            destinationEmailEditTextLayout.setErrorEnabled(true);
            destinationEmailEditTextLayout.setError(getString(R.string.error_email_format));
            isValid = false;
        } else destinationEmailEditTextLayout.setErrorEnabled(false);

        if (parcelNameEdit.getText().toString().isEmpty()) {
            parcelNameEditLayout.setErrorEnabled(true);
            parcelNameEditLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else parcelNameEditLayout.setErrorEnabled(false);

        if (parcelWeighEdit.getText().toString().isEmpty()) {
            parcelWeighEditLayout.setErrorEnabled(true);
            isValid = false;
        } else parcelWeighEditLayout.setErrorEnabled(false);

        if (parcelSizeEdit.getText().toString().isEmpty()) {
            parcelSizeEditLayout.setErrorEnabled(true);
            isValid = false;
        } else parcelSizeEditLayout.setErrorEnabled(false);

        if (chooseCourierEditTextLayout.getVisibility() == View.VISIBLE && chooseCourierEditText.getText().toString().isEmpty()) {
            chooseCourierEditTextLayout.setErrorEnabled(true);
            chooseCourierEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else chooseCourierEditTextLayout.setErrorEnabled(false);

        if (commentEditTextLayout.getVisibility() == View.VISIBLE && commentEditText.getText().toString().isEmpty()) {
            commentEditTextLayout.setErrorEnabled(true);
            commentEditTextLayout.setError(getString(R.string.empty_string_error));
            isValid = false;
        } else commentEditTextLayout.setErrorEnabled(false);

        return isValid;
    }

    public void setTextWatcher(EditText editText) {
        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Pattern.compile(ZEROS_REGEX).matcher(editable.toString()).matches())
                    editText.setText(EMPTY_STRING);
            }
        };
        editText.addTextChangedListener(mTextWatcher);
    }

    // TODO: Реализовать scroll к первому не прошедшему валидацию полю
    public void onScroll() {

    }
}
