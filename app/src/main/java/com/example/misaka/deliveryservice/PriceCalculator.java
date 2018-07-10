package com.example.misaka.deliveryservice;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

public class PriceCalculator {
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String ZERO = "0";

    // TODO: Реализовать CalculatePrice через SharedPreferences(?) для разных единиц измерения

    private final double MIN_PRICE = 500;

    public double CalculateInKgM(String size, String weight, String days) {
        double price = Double.valueOf(size) * 100 + Double.valueOf(weight) * 100 + Double.valueOf(days) * 10;
        if (price < MIN_PRICE) return  MIN_PRICE;
        else return price;
    }

    public void CalculatePrice(double size, double weight, int deliveryData, String sizeUnit, String weightUnit) {

    }

    public String CalculateDeliveryDays(String current, String delivery) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        try {
            Date currentDate = simpleDateFormat.parse(current);
            Date deliveryDate = simpleDateFormat.parse(delivery);
            long difference = Math.abs(currentDate.getTime() - deliveryDate.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            return Long.toString(differenceDates);
        } catch (ParseException e) {
            e.printStackTrace();
            return ZERO;
        }
    }
}
