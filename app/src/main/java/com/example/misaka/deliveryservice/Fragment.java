package com.example.misaka.deliveryservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.misaka.deliveryservice.db.Parcel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.example.misaka.deliveryservice.Consts.ASSIGNED_TO_ME_BUNDLE;
import static com.example.misaka.deliveryservice.Consts.CANCELED;
import static com.example.misaka.deliveryservice.Consts.COMPLETED;
import static com.example.misaka.deliveryservice.Consts.FILTER;
import static com.example.misaka.deliveryservice.Consts.IS_ADMIN;
import static com.example.misaka.deliveryservice.Consts.KEY_BUNDLE;
import static com.example.misaka.deliveryservice.Consts.NEW;
import static com.example.misaka.deliveryservice.Consts.UPLOADED_BY_ME_BUNDLE;

public class Fragment extends android.support.v4.app.Fragment {

    private RecyclerViewAdapter adapter;
    private int parcelStatus;
    private String filter;
    private List<Parcel> parcels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        parcels = new ArrayList<>();

        Bundle bundle = getArguments();
        if(bundle != null) {
            parcelStatus = bundle.getInt(KEY_BUNDLE);
            filter = bundle.getString(FILTER);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(currentUser != null) {
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("parcels");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    parcels.clear();

                    // Get parcels from Firebase
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if (!mSharedPreferences.getBoolean(IS_ADMIN, false)) {
                            if (filter != null && filter.equals(UPLOADED_BY_ME_BUNDLE)) {
                                if (Objects.requireNonNull(mDataSnapshot.getValue(Parcel.class)).getAddedBy().equals(currentUser.getEmail())) {
                                    parcels.add(mDataSnapshot.getValue(Parcel.class));
                                }
                            }
                            if (filter != null && filter.equals(ASSIGNED_TO_ME_BUNDLE)) {
                                if (Objects.requireNonNull(mDataSnapshot.getValue(Parcel.class)).getAssignedTo().equals(currentUser.getEmail())) {
                                    parcels.add(mDataSnapshot.getValue(Parcel.class));
                                }
                            }
                        }
                        else {
                            parcels.add(mDataSnapshot.getValue(Parcel.class));
                        }
                    }

                    // Set adapters (for couriers)
                    if(filter != null && !mSharedPreferences.getBoolean(IS_ADMIN,false)) {
                        switch (parcelStatus) {
                            case 0:
                                Iterator<Parcel> active_iter = parcels.iterator();
                                while (active_iter.hasNext()) {
                                    if(active_iter.next().getStatus().equals(COMPLETED) ) {
                                        active_iter.remove();
                                    }
                                }
                                adapter = new RecyclerViewAdapter(parcels);
                                break;
                            case 1:
                                Iterator<Parcel> completed_iter = parcels.iterator();
                                while (completed_iter.hasNext()) {
                                    if(!completed_iter.next().getStatus().equals(COMPLETED)) {
                                        completed_iter.remove();
                                    }
                                }
                                adapter = new RecyclerViewAdapter(parcels);
                                break;
                            case 2:
                                adapter = new RecyclerViewAdapter(parcels);
                                break;
                            default:
                        }
                    }

                    // Set adapter(for admins)
                    else {
                        Iterator<Parcel> admin_iter = parcels.iterator();
                            Parcel curr;
                            while (admin_iter.hasNext()) {
                                curr = admin_iter.next();
                                if (!curr.getStatus().equals(NEW) && !curr.getStatus().equals(CANCELED)) {
                                    admin_iter.remove();
                            }
                        }
                        adapter = new RecyclerViewAdapter(parcels);
                    }
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getCode() + ':' + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}
