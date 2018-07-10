package com.example.misaka.deliveryservice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misaka.deliveryservice.db.App;
import com.example.misaka.deliveryservice.db.AppDatabase;
import com.example.misaka.deliveryservice.db.Parcel;
import com.example.misaka.deliveryservice.db.ParcelDao;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class Fragment extends android.support.v4.app.Fragment {

    private static final String KEY_BUNDLE = "key";
    private RecyclerViewAdapter adapter;
    private int parcelStatus;

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

        AppDatabase database = App.getInstance().getDatabase();
        final ParcelDao parcelDao = database.lessonDao();

        Bundle bundle = getArguments();
        if(bundle != null) {
            parcelStatus = bundle.getInt(KEY_BUNDLE);
        }

        switch (parcelStatus) {
            case 0:
                parcelDao.getActive()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(parcels -> {
                            adapter = new RecyclerViewAdapter(parcels);
                            recyclerView.setAdapter(adapter);
                        });
                break;
            case 1:
                parcelDao.getCompleted()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(parcels -> {
                            adapter = new RecyclerViewAdapter(parcels);
                            recyclerView.setAdapter(adapter);
                        });
                break;
            case 2:
                parcelDao.getAll()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(parcels -> {
                            adapter = new RecyclerViewAdapter(parcels);
                            recyclerView.setAdapter(adapter);
                        });
                break;
            default:
        }
        return view;
    }
}
