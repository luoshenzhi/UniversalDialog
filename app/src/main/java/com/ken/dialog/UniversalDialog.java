package com.ken.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
 * 通用对话框
 * @author Ken Luo
 * @version 1.0
 */
public class UniversalDialog extends DialogFragment {

    private DialogParams params;
    private ViewHolder viewHolder;
    private static final String SAVE_STATE_KEY = "params";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialog();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_STATE_KEY, params);                     // 横坚屏切换时保存数据
        if (params.saveStateListener != null) {
            params.saveStateListener.onSaveInstanceState(outState, viewHolder, false);
        }
    }

    private void setParams(DialogParams params) {
        this.params = params;
    }

    private View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 恢复横坚屏切换时保存的对话框数据
        if (params == null && savedInstanceState != null) {
            params = savedInstanceState.getParcelable(SAVE_STATE_KEY);
        }

        if (params == null || params.layoutResId <= 0)
            return null;

        View view = inflater.inflate(params.layoutResId, container, false);
        viewHolder = new ViewHolder(getContext(), view);

        // 设置布局
        if (params.bindViewListener != null) {
            params.bindViewListener.onBindView(this, viewHolder);
        }

        // 恢复 View 状态, 如 EditText 的数据
        if (params.saveStateListener != null && savedInstanceState != null) {
            params.saveStateListener.onSaveInstanceState(savedInstanceState, viewHolder, true);
        }
        return view;
    }


    private void setDialog() {

        if (params == null || getContext() == null || getDialog() == null || getDialog().getWindow() == null)
            return;

        // 圆角背景
        Window window = getDialog().getWindow();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(params.roundRadius > 0 ? params.roundRadius : dp2px(getContext(), 5));
        drawable.setStroke(0, Color.TRANSPARENT);
        window.setBackgroundDrawable(drawable);

        // 背景变暗
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(params.dimAmount);

        // 设置宽高和位置
        window.setLayout(params.width > 0 ? params.width : getScreenWidth(getContext()) / 4 * 3,
                         params.height > 0 ? params.height : WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(params.gravity);

        // 设置显示和退出动画
        if (params.animation > 0)
            window.setWindowAnimations(params.animation);

        // 是否为强制对话框
        this.setCancelable(params.cancelable);
    }

    private static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private static int dp2px(Context context, int dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }


    /**
     * 通用对话框构建器
     */
    public static class Builder {

        private final DialogParams P;

        private Builder(@LayoutRes  int layoutResId) {
            P = new DialogParams();
            P.layoutResId = layoutResId;
        }

        /**
         * 设置对话框布局
         * @param layoutResId : 布局文件
         * @return 对话框构建器
         */
        public static Builder setView(@LayoutRes  int layoutResId) {
            return new Builder(layoutResId);
        }

        /**
         * 设置对话框构宽度
         * @param width : 对话框宽度, 默认为屏幕宽度的 3/4
         * @return 对话框构建器
         */
        public Builder setWidth(int width) {
            P.width = width;
            return this;
        }

        /**
         * 设置对话框构高度
         * @param height 高度, 默认 WRAP_CONTENT
         * @return 对话框构建器
         */
        public Builder setHeight(int height) {
            P.height = height;
            return this;
        }

        /**
         * 设置对话框显示位置
         * @param gravity 显示位置, 默认 Gravity.CENTER
         * @return 对话框构建器
         */
        public Builder setGravity(int gravity) {
            P.gravity = gravity;
            return this;
        }

        /**
         * 设置对话框构圆角大小
         * @param roundRadius 圆角大小, 默认 5dip
         * @return 对话框构建器
         */
        public Builder setRoundRadius(int roundRadius) {
            P.roundRadius = roundRadius;
            return this;
        }

        /**
         * 设置显示和退出动画
         * @param resId 资源文件
         * @return 对话框构建器
         *
         * <pre>
         *     <style name="Animation.Translucent">
         *         <item name="windowEnterAnimation">@anim/translucent_enter</item>
         *         <item name="windowExitAnimation">@anim/translucent_exit</item>
         *     </style>
         *
         *      <!-- translucent_enter.xml -->
         *      <set xmlns:android="http://schemas.android.com/apk/res/android" android:interpolator="@interpolator/decelerate_quad">
         *          <translate android:fromXDelta="75%" android:toXDelta="0" android:duration="@android:integer/config_shortAnimTime"/>
         *          <alpha android:fromAlpha="0.0" android:toAlpha="1.0" android:duration="@android:integer/config_shortAnimTime"/>
         *      </set>
         *
         *      <!-- translucent_exit.xml -->
         *      <set xmlns:android="http://schemas.android.com/apk/res/android" android:interpolator="@interpolator/accelerate_quad">
         *          <translate android:fromXDelta="0%" android:toXDelta="75%" android:duration="@android:integer/config_shortAnimTime"/>
         *          <alpha android:fromAlpha="1.0" android:toAlpha="0" android:duration="@android:integer/config_shortAnimTime"/>
         *      </set>
         *
         * </pre>
         */
        public Builder setAnimation(@StyleRes int resId) {
            P.animation = resId;
            return this;
        }

        /**
         * 设置窗口透明度
         * @param dimAmount 范围 0~1.0, 0-完全透明, 1-全黑; 默认 0.5f
         * @return 对话框构建器
         */
        public Builder setDimAmount(float dimAmount) {
            P.dimAmount = dimAmount;
            return this;
        }

        /**
         * 设置是否为强制对话框
         * @param cancelable true 非强制, false 强制; 默认为非强制
         * @return 对话框构建器
         */
        public Builder setCancelable(boolean cancelable) {
            P.cancelable = cancelable;
            return this;
        }

        /**
         * 设置布局监听器
         * @param listener 对话框布局设置监听器
         * @return 对话框构建器
         */
        public Builder setOnBindViewListener(OnBindViewListener listener) {
            P.bindViewListener = listener;
            return this;
        }

        public Builder setOnSaveStateListener(OnSaveStateListener listener) {
            P.saveStateListener = listener;
            return this;
        }

        /**
         * 创建对话框
         * @return 创建好的对话框
         * @exception NullPointerException 如果布局文件 ID 小于等于 0
         */
        public DialogFragment create() {

            if (P.layoutResId <= 0)
                throw new NullPointerException("The dialog without layout!");

            final UniversalDialog dialog = new UniversalDialog();
            dialog.setParams(P);
            return dialog;
        }
    }


    private static class DialogParams implements Parcelable {

        private int layoutResId    = 0;
        private int width          = 0;
        private int height         = 0;
        private int gravity        = Gravity.CENTER;
        private int roundRadius    = 0;
        private int animation      = 0;
        private float dimAmount    = 0.5f;
        private boolean cancelable = true;
        private OnBindViewListener bindViewListener;
        private OnSaveStateListener saveStateListener;

        private DialogParams() {
        }

        DialogParams(Parcel in) {
            layoutResId = in.readInt();
            width = in.readInt();
            height = in.readInt();
            gravity = in.readInt();
            roundRadius = in.readInt();
            animation = in.readInt();
            dimAmount = in.readFloat();
            cancelable = in.readByte() != 0;
        }

        public static final Creator<DialogParams> CREATOR = new Creator<DialogParams>() {
            @Override
            public DialogParams createFromParcel(Parcel in) {
                return new DialogParams(in);
            }

            @Override
            public DialogParams[] newArray(int size) {
                return new DialogParams[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(layoutResId);
            dest.writeInt(width);
            dest.writeInt(height);
            dest.writeInt(gravity);
            dest.writeInt(roundRadius);
            dest.writeInt(animation);
            dest.writeFloat(dimAmount);
            dest.writeByte((byte) (cancelable ? 1 : 0));
        }
    }


    public interface OnBindViewListener {
        void onBindView(DialogFragment dialog, ViewHolder holder);
    }

    public interface OnSaveStateListener {
        /**
         * 控件状态保存回调函数
         * @param bundle 保存或恢复数据
         * @param holder ViewHolder
         * @param resume true 表示恢复数据, false 表示保存数据
         */
        void onSaveInstanceState(@NonNull Bundle bundle, ViewHolder holder, boolean resume);
    }
}
