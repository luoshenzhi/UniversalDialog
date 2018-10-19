package com.ken.universaldialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ken.dialog.UniversalDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setOnClickListener(this);
        button.setText("Hello");
        button.setLayoutParams(new ViewGroup.LayoutParams(200, 100));
        setContentView(button);
    }

    @Override
    public void onClick(View v) {

        UniversalDialog.Builder
                .setView(R.layout.dialog_layout)                        // 设置布局文件
                .setWidth(800)                                          // 设置宽
                .setHeight(500)                                         // 设置高
                .setGravity(Gravity.CENTER)                             // 设置位置
                .setRoundRadius(15)                                     // 设置圆角
                .setAnimation(android.R.style.Animation_Translucent)    // 设置动画
                .setDimAmount(0.5f)                                     // 设置背景透明度
                .setCancelable(false)                                   // 设置为强制对话框
                .setOnBindViewListener((dialog, holder) -> {            // 设置布局
                    holder.setOnClickListener(R.id.no_tv, (view) -> dialog.dismiss());
                    holder.setOnClickListener(R.id.yes_tv, (view) -> dialog.dismiss());
                })
                .setOnSaveStateListener((outState, holder, resume) -> { // 保存 View 状态

                    // 恢复数据
                    final String key = "password_et";
                    if (resume) {
                        String password = outState.getString(key);
                        holder.setText(R.id.password_et, password);
                        return;
                    }

                    // 保存数据
                    String password = holder.getView(R.id.password_et).toString().trim();
                    if (!TextUtils.isEmpty(password)) {
                        outState.putString(key, password);
                    }
                })
                .create()                                                // 创建对话框
                .show(getSupportFragmentManager(), "TAG");
    }
}
