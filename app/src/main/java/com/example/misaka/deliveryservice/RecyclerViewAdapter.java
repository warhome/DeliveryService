package com.example.misaka.deliveryservice;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.deliveryservice.db.Parcel;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ParcelViewHolder> {

    private List<Parcel> parcels;

    static class ParcelViewHolder extends RecyclerView.ViewHolder {

        TextView ParcelName;
        TextView DeliveryDate;
        TextView DestinationAddress;
        Button   ShowInMapButton;
        TextView Status;

        ParcelViewHolder(View itemView) {
            super(itemView);
            ParcelName = itemView.findViewById(R.id.parcelName);
            DeliveryDate = itemView.findViewById(R.id.deliveryDate);
            DestinationAddress = itemView.findViewById(R.id.destinationAddress);
            ShowInMapButton = itemView.findViewById(R.id.showInMapButton);
            Status = itemView.findViewById(R.id.recyclerStatusValue);
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
            intent.putExtra("id", parcels.get(currPosition).getId());
            v.getContext().startActivity(intent);
        });

        // Show parcel in map
        holder.ShowInMapButton.setOnClickListener(v -> {
            if(parcels.get(position).getCoordinates() != null && !parcels.get(position).getCoordinates().isEmpty()) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtra("isRecyclerIntent", true);
                intent.putExtra("coordinates", parcels.get(position).getCoordinates());
                v.getContext().startActivity(intent);
            }
            else {
                Toast.makeText(v.getContext(),"Координаты посылки не установлены", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ParcelName.setText(parcels.get(position).getParcelName());
        holder.DeliveryDate.setText(parcels.get(position).getDeliveryDate());
        holder.DestinationAddress.setText(parcels.get(position).getDestinationAddress());
        holder.Status.setText(parcels.get(position).getStatus());

        switch (parcels.get(position).getStatus()) {
            case "Active":
                holder.Status.setTextColor(Color.parseColor("#27ae60"));
                break;
            case "Completed":
                holder.Status.setTextColor(Color.parseColor("#95a5a6"));
                break;
            default:
                holder.Status.setTextColor(Color.parseColor("#e74c3c"));
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
