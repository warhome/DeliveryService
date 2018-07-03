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
import com.example.misaka.deliveryservice.db.ParcelDao;

public class Fragment extends android.support.v4.app.Fragment {

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
            parcelStatus = bundle.getInt("key");
        }

        switch (parcelStatus) {
            case 0:
                adapter = new RecyclerViewAdapter(parcelDao.getActive());
                break;
            case 1:
                adapter = new RecyclerViewAdapter(parcelDao.getCompleted());
                break;
            case 2:
                adapter = new RecyclerViewAdapter(parcelDao.getAll());
                break;
            default:
        }
        recyclerView.setAdapter(adapter);
        return view;
    }
}
