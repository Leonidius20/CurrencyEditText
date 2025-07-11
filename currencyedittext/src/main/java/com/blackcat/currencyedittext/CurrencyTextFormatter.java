package com.blackcat.currencyedittext;

import java.text.DecimalFormat;
import java.util.Locale;

public final class CurrencyTextFormatter {

    private CurrencyTextFormatter(){}

    public static String formatText(String val, DecimalFormat formatter){
        return formatText(val, formatter, Locale.US, 2);
    }

    public static String formatText(String val, DecimalFormat formatter, Locale defaultLocale){
        return formatText(val, formatter, defaultLocale, 2);
    }

    public static String formatText(String val, DecimalFormat formatter, Locale defaultLocale, int decimalDigits){
        //special case for the start of a negative number
        if(val.equals("-")) return val;

        int currencyDecimalDigits = decimalDigits;

        DecimalFormat currencyFormatter = formatter;

        //retain information about the negativity of the value before stripping all non-digits
        boolean isNegative = false;
        if (val.contains("-")){
            isNegative = true;
        }

        //strip all non-digits so the formatter always has a 'clean slate' of numbers to work with
        val = val.replaceAll("[^\\d]", "");
        //if there's nothing left, that means we were handed an empty string. Also, cap the raw input so the formatter doesn't break.
        if(!val.equals("")) {

            //if we're given a value that's smaller than our decimal location, pad the value.
            if (val.length() <= currencyDecimalDigits){
                String formatString = "%" + currencyDecimalDigits + "s";
                val = String.format(formatString, val).replace(' ', '0');
            }

            //place the decimal in the proper location to construct a double which we will give the formatter.
            //This is NOT the decimal separator for the currency value, but for the double which drives it.
            String preparedVal = new StringBuilder(val).insert(val.length() - currencyDecimalDigits, '.').toString();

            //Convert the string into a double, which will be passed into the currency formatter
            double newTextValue = Double.valueOf(preparedVal);

            //reapply the negativity
            newTextValue *= isNegative ? -1 : 1;

            //finally, do the actual formatting
            currencyFormatter.setMinimumFractionDigits(currencyDecimalDigits);
            val = currencyFormatter.format(newTextValue);
        }
        else {
            throw new IllegalArgumentException("Invalid amount of digits found (either zero or too many) in argument val");
        }
        return val;
    }

}
