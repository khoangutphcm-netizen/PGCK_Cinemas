package com.example.pgck2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.ComponentDialog;
import androidx.activity.OnBackPressedCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Full-screen form with fixed actions and a body that resizes above the keyboard. */
final class AdminFormDialog extends ComponentDialog {
    private final MaterialButton save, close;
    private final View body;
    private final TextView caption;
    private final String actionLabel;
    private boolean busy, confirmDiscard;
    private Runnable closeAction;

    AdminFormDialog(Context context, String title, String description, View body, String actionLabel) {
        super(context, R.style.Theme_Cinema_AdminForm);
        this.body = body;
        this.actionLabel = actionLabel;
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xff101116);
        LinearLayout header = new LinearLayout(getContext());
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), dp(10), dp(16), dp(8));
        close = new MaterialButton(getContext());
        close.setText("‹"); close.setTextSize(26); close.setContentDescription("Quay lại");
        close.setMinWidth(0); close.setMinimumWidth(0); close.setPadding(0, 0, 0, 0);
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(52)));
        LinearLayout headings = new LinearLayout(getContext()); headings.setOrientation(1);
        headings.setPadding(dp(12), 0, 0, 0);
        TextView brand = new TextView(getContext()); brand.setText("PCGK CINEMA  /  QUẢN TRỊ");
        brand.setTextColor(0xffff7a87); brand.setTextSize(10); headings.addView(brand);
        TextView heading = new TextView(getContext()); heading.setText(title); heading.setTextSize(21);
        heading.setTextColor(Color.WHITE); heading.setTypeface(null, 1); headings.addView(heading);
        header.addView(headings, new LinearLayout.LayoutParams(0, -2, 1)); root.addView(header);
        root.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout footer = new LinearLayout(getContext()); footer.setOrientation(1);
        footer.setBackgroundColor(0xff191c25); footer.setPadding(dp(20), dp(8), dp(20), dp(10));
        caption = new TextView(getContext()); caption.setText(description);
        caption.setTextSize(11); caption.setTextColor(0xffb5bed0); footer.addView(caption);
        save = new MaterialButton(getContext()); save.setText(actionLabel); save.setAllCaps(false);
        save.setTextColor(Color.WHITE); save.setBackgroundTintList(ColorStateList.valueOf(0xffe50924));
        save.setCornerRadius(dp(12)); footer.addView(save, new LinearLayout.LayoutParams(-1, dp(56)));
        if (actionLabel == null) save.setVisibility(View.GONE);
        root.addView(footer); setContentView(root); CinemaStyle.applyInsets(root);
        setCanceledOnTouchOutside(false);
        close.setOnClickListener(v -> requestClose());
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { requestClose(); }
        });
    }

    void onSave(View.OnClickListener action) { save.setOnClickListener(action); }
    void confirmDiscard() { confirmDiscard = true; }
    void onClose(Runnable action) { closeAction = action; }
    private void requestClose() {
        if (busy) return;
        if (confirmDiscard) new MaterialAlertDialogBuilder(getContext())
                .setTitle("Rời màn hình chỉnh sửa?").setMessage("Các thay đổi chưa lưu sẽ bị bỏ.")
                .setNegativeButton("Tiếp tục chỉnh sửa", null)
                .setPositiveButton("Bỏ thay đổi", (d, w) -> closeForm()).show();
        else closeForm();
    }
    private void closeForm() { dismiss(); if (closeAction != null) closeAction.run(); }
    void setBusy(boolean value) {
        busy = value; save.setEnabled(!value); close.setEnabled(!value);
        save.setText(value ? "Đang lưu…" : actionLabel);
        enable(body, !value);
    }
    private static void enable(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            enable(((ViewGroup) view).getChildAt(i), enabled);
    }
    @Override protected void onStart() {
        super.onStart();
        if (getWindow() != null) {
            getWindow().setLayout(-1, -1);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }
    }
    private int dp(int value) { return Math.round(value * getContext().getResources().getDisplayMetrics().density); }
}
