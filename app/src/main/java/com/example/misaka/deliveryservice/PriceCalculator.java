package com.example.misaka.deliveryservice;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private static final String SIZE_AND_WEIGH_PATTERN = "##0.000";
    private DecimalFormat decimalFormat;
    private final double MIN_PRICE = 500;

    PriceCalculator() {
        decimalFormat = new DecimalFormat(SIZE_AND_WEIGH_PATTERN);
    }

    public double CalculatePrice(String size, String weight, String days, String sizeUnit, String weightUnit) {
        double _size;
        double _weight;
        double price;

        if(!sizeUnit.equals(METER)){
            _size = Double.valueOf(SizeToMeters(size, sizeUnit).replace(',', '.'));
        } else _size = Double.valueOf(size);

        if(!weight.equals(KILOGRAM)) {
            _weight = Double.valueOf(WeighToKilograms(weight,weightUnit).replace(',', '.'));
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

    public String SizeToMeters(String size, String sizeUnit) {
        switch (sizeUnit) {
            case CENTIMETER:
                return decimalFormat.format(Double.valueOf(size) / 100).replace(',', '.');
            case MILIMETER:
                return decimalFormat.format(Double.valueOf(size) / 1000).replace(',', '.');
            case FOOT:
                return decimalFormat.format(Double.valueOf(size) / 3.2808).replace(',', '.');
            default:
                return decimalFormat.format(Double.valueOf(size)).replace(',', '.');
        }
    }

    public String WeighToKilograms(String weigh, String weighUnit) {
        switch (weighUnit) {
            case GRAM:
                return decimalFormat.format(Double.valueOf(weigh) / 1000).replace(',', '.');
            case LB:
                return decimalFormat.format(Double.valueOf(weigh) / 2.2046).replace(',', '.');
            default:
                return decimalFormat.format(Double.valueOf(weigh)).replace(',', '.');
        }
    }

    public String SizeFromMeters(String size, String sizeUnit) {
        switch (sizeUnit) {
            case CENTIMETER:
                return decimalFormat.format(Double.valueOf(size) * 100).replace(',', '.');
            case MILIMETER:
                return decimalFormat.format(Double.valueOf(size) * 1000).replace(',', '.');
            case FOOT:
                return decimalFormat.format(Double.valueOf(size) * 3.2808).replace(',', '.');
            default:
                return decimalFormat.format(Double.valueOf(size)).replace(',', '.');
        }
    }

    public String WeighFromKilograms(String weigh, String weighUnit) {
        switch (weighUnit) {
            case GRAM:
                return decimalFormat.format(Double.valueOf(weigh) * 1000).replace(',', '.');
            case LB:
                return decimalFormat.format(Double.valueOf(weigh) * 2.2046).replace(',', '.');
            default:
                return decimalFormat.format(Double.valueOf(weigh)).replace(',', '.');
        }
    }
}
