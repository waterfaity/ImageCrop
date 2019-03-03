package com.xueduoduo.imagecrop;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LoadingDialog extends Dialog implements DialogInterface.OnDismissListener {
    private ImageView imageView;
    private RotateAnimation rotateAnimation;

    public LoadingDialog(Context context) {
        super(context,R.style.dialog_theme);
        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.loading);
        addContentView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setCancelable(false);
        rotateAnimation = new RotateAnimation(Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        setOnDismissListener(this);
    }

    @Override
    public void show() {
        super.show();
        imageView.startAnimation(rotateAnimation);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        imageView.clearAnimation();
    }
}
