package com.dahuatech.netsdk.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.syyk.electronicclass2.R;


public class DialogProgress extends Dialog {

    public static final int FADED_ROUND_SPINNER = 0;
    public static final int GEAR_SPINNER = 1;
    public static final int SIMPLE_ROUND_SPINNER = 2;

    View view;
    TextView mTextViewMessage;
    ImageView mImageView;
    AnimationDrawable mAnimationDrawable;
    Context context;

    public DialogProgress(Context context) {
        super(context, R.style.DialogTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        this.context = context;
        view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        mTextViewMessage = (TextView) view.findViewById(R.id.textview_message);
        mImageView = (ImageView) view.findViewById(R.id.imageview_progress);

        setSpinnerType(FADED_ROUND_SPINNER);
        this.setContentView(view);
    }

    public void setSpinnerType(int spinnerType) {
        switch (spinnerType) {
            case 0:
                mImageView.setImageResource(R.drawable.round_spinner_fade);
                break;
            case 1:
                mImageView.setImageResource(R.drawable.gear_spinner);
                break;
            case 2:
                mImageView.setImageResource(R.drawable.round_spinner);
                break;
            default:
                mImageView.setImageResource(R.drawable.round_spinner_apple);
        }

        mAnimationDrawable = (AnimationDrawable) mImageView.getDrawable();
    }

    public void setMessage(String message) {
        mTextViewMessage.setText(message);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimationDrawable.start();
            }
        });
    }
}
