package com.TrackManInc.mytracker.Filters;

import android.text.InputFilter;
import android.text.Spanned;
//https://stackoverflow.com/questions/5357455/limit-decimal-places-in-android-edittext
/**
 * Input filter that limits the number of decimal digits that are allowed to be
 * entered.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    private final int decimalDigits;
    private final int digits;

    /**
     * Constructor.
     *
     * @param decimalDigits maximum decimal digits
     */
    public DecimalDigitsInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
        this.digits = 10;
    }
    public DecimalDigitsInputFilter(int decimalDigits,int digits) {
        this.decimalDigits = decimalDigits;
        this.digits = digits;
    }
    @Override
    public CharSequence filter(CharSequence source,
                               int start,
                               int end,
                               Spanned dest,
                               int dstart,
                               int dend) {


        int dotPos = -1;
        int len = dest.length();
        for (int i = 0; i < len; i++) {
            char c = dest.charAt(i);
            if (c == '.' || c == ',') {
                dotPos = i;
                break;
            }
        }
        if(dend>=digits){
            return "";
        }
        if (dotPos >= 0) {

            // protects against many dots
            if (source.equals(".") || source.equals(","))
            {
                return "";
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null;
            }
            if (len - dotPos > decimalDigits) {
                return "";
            }
        }

        return null;
    }

}
