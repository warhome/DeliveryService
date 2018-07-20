package com.example.misaka.deliveryservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.deliveryservice.db.Parcel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.misaka.deliveryservice.Consts.ASSIGNED;
import static com.example.misaka.deliveryservice.Consts.CANCELED;
import static com.example.misaka.deliveryservice.Consts.COMPLETED;
import static com.example.misaka.deliveryservice.Consts.COORDINATES;
import static com.example.misaka.deliveryservice.Consts.ID_EXTRA;
import static com.example.misaka.deliveryservice.Consts.IN_PROCESS;
import static com.example.misaka.deliveryservice.Consts.IS_ADMIN;
import static com.example.misaka.deliveryservice.Consts.IS_RECYCLER_INTENT;
import static com.example.misaka.deliveryservice.Consts.NEW;
import static com.example.misaka.deliveryservice.Consts.PARCELS;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ParcelViewHolder> {

    private static final String NEW_CLOLR = "#1abc9c";
    private static final String ASSIGNED_COLOR = "#27ae60";
    private static final String IN_PROCESS_COLOR = "#f39c12";
    private static final String COMPLETED_COLOR = "#95a5a6";
    private static final String CANCELED_COLOR = "#c0392b";

    private List<Parcel> parcels;

    static class ParcelViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.parcelName)
        TextView ParcelName;
        @BindView(R.id.deliveryDate)
        TextView DeliveryDate;
        @BindView(R.id.destinationAddress)
        TextView DestinationAddress;
        @BindView(R.id.showInMapButton)
        Button   ShowInMapButton;
        @BindView(R.id.recyclerStatusValue)
        TextView Status;

        ParcelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    RecyclerViewAdapter(List<Parcel> parcels) {
        this.parcels = parcels;
        Collections.sort(this.parcels);
    }

    @NonNull
    @Override
    public ParcelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler, parent, false);
        return new ParcelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ParcelViewHolder holder, int position) {
        final int currPosition = position;

        // Start AddParcel activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddParcel.class);
            intent.putExtra(ID_EXTRA, parcels.get(currPosition).getId());
            v.getContext().startActivity(intent);
        });

        // Show parcel in map
        holder.ShowInMapButton.setOnClickListener(v -> {
            if(parcels.get(position).getCoordinates() != null && !parcels.get(position).getCoordinates().isEmpty()) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtra(IS_RECYCLER_INTENT, true);
                intent.putExtra(COORDINATES, parcels.get(position).getCoordinates());
                v.getContext().startActivity(intent);
            }
            else {
                Toast.makeText(v.getContext(), R.string.coordinates_not_found_error, Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        if(sharedPreferences.getBoolean(IS_ADMIN,false)) {
            // Delete parcel
            holder.itemView.setOnLongClickListener(view -> {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(PARCELS);
                databaseReference.child(parcels.get(position).getId()).removeValue();
                return false;
            });
        }

        holder.ParcelName.setText(parcels.get(position).getParcelName());
        holder.DeliveryDate.setText(parcels.get(position).getDeliveryDate());
        holder.DestinationAddress.setText(parcels.get(position).getDestinationAddress());
        holder.Status.setText(parcels.get(position).getStatus());

        switch (parcels.get(position).getStatus()) {
            case NEW:
                holder.Status.setTextColor(Color.parseColor(NEW_CLOLR));
                break;
            case ASSIGNED:
                holder.Status.setTextColor(Color.parseColor(ASSIGNED_COLOR));
                break;
            case IN_PROCESS:
                holder.Status.setTextColor(Color.parseColor(IN_PROCESS_COLOR));
                break;
            case CANCELED:
                holder.Status.setTextColor(Color.parseColor(CANCELED_COLOR));
                break;
            case COMPLETED:
                holder.Status.setTextColor(Color.parseColor(COMPLETED_COLOR));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return parcels.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
