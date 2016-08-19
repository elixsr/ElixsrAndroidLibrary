package com.elixsr.core.common.widgets;

/**
 * Created by Niall on 27/07/2016.
 */
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

public class ToggleSwitch extends SwitchCompat {

    private ToggleSwitch.OnBeforeCheckedChangeListener mOnBeforeListener;

    public static interface OnBeforeCheckedChangeListener {
        public boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean checked);
    }

    public ToggleSwitch(Context context) {
        super(context);
    }

    public ToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener listener) {
        mOnBeforeListener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mOnBeforeListener != null
                && mOnBeforeListener.onBeforeCheckedChanged(this, checked)) {
            return;
        }
        super.setChecked(checked);
    }

    public void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
    }
}