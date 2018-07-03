package com.example.misaka.deliveryservice.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "parcels")
public class Parcel  implements Comparable<Parcel> {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String customerAddress;
    private String customerFullName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerType;
    private String customerCompanyName;

    private String destinationAddress;
    private String destinationFullName;
    private String destinationEmail;
    private String destinationPhoneNumber;
    private String destinationType;
    private String destinationCompanyName;

    private String parcelName;
    private String parcelSize;
    private String parcelWeigh;
    private String deliveryDate;
    private String price;
    private String status;
    private String coordinates;
    private String comment;


    public Parcel() {
        this.customerType = "Person";
        this.destinationType = "Person";
        this.status = "Active";
    }

    @Override
    public int compareTo(@NonNull Parcel p) {
        return Integer.valueOf(deliveryDate.replaceAll("\\D+",""))
                - Integer.valueOf(p.getDeliveryDate().replaceAll("\\D+",""));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerCompanyName() {
        return customerCompanyName;
    }

    public void setCustomerCompanyName(String customerCompanyName) {
        this.customerCompanyName = customerCompanyName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationFullName() {
        return destinationFullName;
    }

    public void setDestinationFullName(String destinationFullName) {
        this.destinationFullName = destinationFullName;
    }

    public String getDestinationEmail() {
        return destinationEmail;
    }

    public void setDestinationEmail(String destinationEmail) {
        this.destinationEmail = destinationEmail;
    }

    public String getDestinationPhoneNumber() {
        return destinationPhoneNumber;
    }

    public void setDestinationPhoneNumber(String destinationPhoneNumber) {
        this.destinationPhoneNumber = destinationPhoneNumber;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getDestinationCompanyName() {
        return destinationCompanyName;
    }

    public void setDestinationCompanyName(String destinationCompanyName) {
        this.destinationCompanyName = destinationCompanyName;
    }

    public String getParcelName() {
        return parcelName;
    }

    public void setParcelName(String parcelName) {
        this.parcelName = parcelName;
    }

    public String getParcelSize() {
        return parcelSize;
    }

    public void setParcelSize(String parcelSize) {
        this.parcelSize = parcelSize;
    }

    public String getParcelWeigh() {
        return parcelWeigh;
    }

    public void setParcelWeigh(String parcelWeigh) {
        this.parcelWeigh = parcelWeigh;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
