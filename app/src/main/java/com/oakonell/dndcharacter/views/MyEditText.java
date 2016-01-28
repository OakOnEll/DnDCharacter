package com.oakonell.dndcharacter.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MyEditText extends EditText {

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addDoneHandler();

    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addDoneHandler();

    }

    public MyEditText(Context context) {
        super(context);
        addDoneHandler();
    }

    protected void addDoneHandler() {
        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(@NonNull TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager in = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    @Override
    public View focusSearch(int direction) {
        View v = super.focusSearch(direction);
        if (v != null) {
            if (v.isEnabled()) {
                return v;
            } else {
                // keep searching
                return v.focusSearch(direction);
            }
        }
        return v;
    }

}