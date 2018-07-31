package com.example.misaka.deliveryservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.misaka.deliveryservice.Consts.CENTIMETER;
import static com.example.misaka.deliveryservice.Consts.FOOT;
import static com.example.misaka.deliveryservice.Consts.GRAM;
import static com.example.misaka.deliveryservice.Consts.KILOGRAM;
import static com.example.misaka.deliveryservice.Consts.LB;
import static com.example.misaka.deliveryservice.Consts.METER;
import static com.example.misaka.deliveryservice.Consts.MILIMETER;

public class PriceCalculator {
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String ZERO = "0";

    private final double MIN_PRICE = 500;

    public double CalculatePrice(String size, String weight, String days, String sizeUnit, String weightUnit) {
        double _size;
        double _weight;
        double price;

        if(!sizeUnit.equals(METER)){
            _size = SizeToMeters(size, sizeUnit);
        } else _size = Double.valueOf(size);

        if(!weight.equals(KILOGRAM)) {
            _weight = WeighToKilograms(weight,weightUnit);
        } else _weight = Double.valueOf(weight);

        price = _size * 100 + _weight * 100 + Double.valueOf(days) * 10;
        if (price < MIN_PRICE) return MIN_PRICE;
        else return Math.ceil(price);
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

    public double SizeToMeters(String size, String sizeUnit) {
        switch (sizeUnit) {
            case CENTIMETER:
                return Math.floor(Double.valueOf(size) / 100);
            case MILIMETER:
                return Math.floor(Double.valueOf(size) / 1000);
            case FOOT:
                return Math.floor(Double.valueOf(size) / 3.2808);
            default:
                return Math.floor(Double.valueOf(size));
        }
    }

    public double WeighToKilograms(String weigh, String weighUnit) {
        switch (weighUnit) {
            case GRAM:
                return Math.floor(Double.valueOf(weigh) / 1000);
            case LB:
                return Math.floor(Double.valueOf(weigh) / 2.2046);
            default:
                return Math.floor(Double.valueOf(weigh));
        }
    }

    public double SizeFromMeters(String size, String sizeUnit) {
        switch (sizeUnit) {
            case CENTIMETER:
                return Math.floor(Double.valueOf(size) * 100);
            case MILIMETER:
                return Math.floor(Double.valueOf(size) * 1000);
            case FOOT:
                return Math.floor(Double.valueOf(size) * 3.2808);
            default:
                return Math.floor(Double.valueOf(size));
        }
    }

    public double WeighFromKilograms(String weigh, String weighUnit) {
        switch (weighUnit) {
            case GRAM:
                return Math.floor(Double.valueOf(weigh) * 1000);
            case LB:
                return Math.floor(Double.valueOf(weigh) * 2.2046);
            default:
                return Math.floor(Double.valueOf(weigh));
        }
    }
}
