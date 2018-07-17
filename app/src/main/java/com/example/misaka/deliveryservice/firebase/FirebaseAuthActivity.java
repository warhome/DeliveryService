package com.example.misaka.deliveryservice.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.misaka.deliveryservice.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static com.example.misaka.deliveryservice.Consts.IS_ADMIN;

public class FirebaseAuthActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.firebase_login)
    EditText firebaseLoginEditText;
    @BindView(R.id.firebase_password)
    EditText firebasePasswordEditText;
    @BindView(R.id.firebase_login_TextInputLayout)
    TextInputLayout firebaseLoginTextInput;
    @BindView(R.id.firebase_password_TextInputLayout)
    TextInputLayout firebasePasswordTextInput;
    @BindView(R.id.buttonSignIn)
    Button btn_SignIn;

    private static final String ADMINS = "admins";
    private FirebaseAuth mAuth;
    private static final int MIN_LOGIN_LENGTH = 8;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        firebaseLoginEditText.addTextChangedListener(this);
        firebasePasswordEditText.addTextChangedListener(this);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

        // Валидация
        Observable.combineLatest(RxTextView.textChanges(firebaseLoginEditText), RxTextView.textChanges(firebasePasswordEditText),
                (login, password) -> Patterns.EMAIL_ADDRESS.matcher(login).matches() && password.length() >= MIN_LOGIN_LENGTH
        ).subscribe(aBoolean -> {
            if(aBoolean) btn_SignIn.setVisibility(View.VISIBLE);
            else btn_SignIn.setVisibility(View.INVISIBLE);
        });

        // SignIn
        btn_SignIn.setOnClickListener(view -> mAuth.signInWithEmailAndPassword(firebaseLoginEditText.getText().toString(), firebasePasswordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        checkIsAdminAndFinish(user);
                    } else {
                        Toast.makeText(this, R.string.firebase_sign_in_error, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void updateUI(FirebaseUser currentUser) {
        firebaseLoginEditText.setText(currentUser.getEmail());
    }

    private void checkIsAdminAndFinish(FirebaseUser currentUser) {
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child(ADMINS);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> admins = new ArrayList<>();
                    for(DataSnapshot mDataSnapshot: dataSnapshot.getChildren()) {
                        admins.add(mDataSnapshot.getValue(String.class));
                    }
                    SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean(IS_ADMIN, admins.contains(currentUser.getEmail()));
                    mEditor.apply();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (firebasePasswordEditText.getText().toString().length() < MIN_LOGIN_LENGTH) {
            firebasePasswordTextInput.setErrorEnabled(true);
            firebasePasswordTextInput.setError(getString(R.string.wrong_password_error));
        } else firebasePasswordTextInput.setErrorEnabled(false);
        if (!Patterns.EMAIL_ADDRESS.matcher(firebaseLoginEditText.getText().toString()).matches()) {
            firebaseLoginTextInput.setErrorEnabled(true);
            firebaseLoginTextInput.setError(getString(R.string.error_auth_email_format));
        } else firebaseLoginTextInput.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
