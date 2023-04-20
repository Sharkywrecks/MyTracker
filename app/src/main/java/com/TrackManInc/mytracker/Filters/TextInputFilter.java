package com.TrackManInc.mytracker.Filters;

import android.text.InputFilter;
import android.text.Spanned;

public class TextInputFilter implements InputFilter {
    private final int characterLength;

    public TextInputFilter() {
        characterLength = 100;
    }
    /**
     * Constructor.
     *
     * @param characterLength maximum characters
     */
    public TextInputFilter(int characterLength) {
        this.characterLength = characterLength;
    }

    @Override
    public CharSequence filter(CharSequence source,
                               int start,
                               int end,
                               Spanned dest,
                               int dstart,
                               int dend) {

        if (dend>=characterLength){
            return "";
        }

        return null;
    }

}
