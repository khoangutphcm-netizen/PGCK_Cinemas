package com.example.pgck2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Local draft with a live preview; Firestore is updated only by the film's Save action. */
final class TitleStyleEditor {
    private final TextInputEditText start, end;
    private final TextInputLayout startBox, endBox;
    private final Chip solid, gradient;
    private final MovieTitleView preview;

    TitleStyleEditor(Context context, LinearLayout parent, EditText title, Video video) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);
        parent.addView(section);
        TextView label = new TextView(context);
        label.setText("Màu tiêu đề · banner và chi tiết phim");
        label.setTextSize(13);
        label.setTextColor(Color.parseColor("#CBD5E1"));
        section.addView(label);

        ChipGroup modes = new ChipGroup(context);
        modes.setSingleSelection(true);
        modes.setSelectionRequired(true);
        solid = chip(context, "Màu đơn");
        gradient = chip(context, "Gradient →");
        modes.addView(solid);
        modes.addView(gradient);
        section.addView(modes);

        preview = new MovieTitleView(context);
        preview.setTextSize(20);
        preview.setTypeface(null, Typeface.BOLD);
        preview.setPadding(dp(context, 14), dp(context, 16), dp(context, 14), dp(context, 16));
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#101116"));
        background.setCornerRadius(dp(context, 12));
        preview.setBackground(background);
        section.addView(preview, new LinearLayout.LayoutParams(-1, -2));

        ChipGroup presets = new ChipGroup(context);
        section.addView(presets);
        String[][] colors = {{"Trắng", "#FFFFFF", ""}, {"Đỏ", "#FF6B75", ""},
                {"Xanh · Bạc", "#60A5FA", "#E2E8F0"}, {"Hồng · Tím", "#FDA4AF", "#C4B5FD"},
                {"Vàng · Cam", "#FDE68A", "#FDBA74"}};
        startBox = field(context, "Màu đầu / màu đơn (#RRGGBB)");
        endBox = field(context, "Màu cuối (#RRGGBB)");
        start = (TextInputEditText) startBox.getEditText();
        end = (TextInputEditText) endBox.getEditText();
        section.addView(startBox);
        section.addView(endBox);
        for (String[] preset : colors) {
            Chip choice = chip(context, preset[0]);
            choice.setCheckable(false);
            choice.setTextColor(Color.parseColor(preset[1]));
            choice.setOnClickListener(v -> {
                start.setText(preset[1]);
                end.setText(preset[2].isEmpty() ? "#FFFFFF" : preset[2]);
                (preset[2].isEmpty() ? solid : gradient).setChecked(true);
                refresh(title);
            });
            presets.addView(choice);
        }
        start.setText(video == null ? "#FFFFFF" : video.getTitleColor());
        String last = video == null ? "" : video.getTitleGradientEnd();
        end.setText(last.isEmpty() ? "#E2E8F0" : last);
        (last.isEmpty() ? solid : gradient).setChecked(true);
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int count, int after) { }
            public void onTextChanged(CharSequence s, int st, int before, int count) { refresh(title); }
            public void afterTextChanged(Editable value) { }
        };
        title.addTextChangedListener(watcher);
        start.addTextChangedListener(watcher);
        end.addTextChangedListener(watcher);
        modes.setOnCheckedStateChangeListener((group, ids) -> refresh(title));
        refresh(title);
    }

    private void refresh(EditText title) {
        preview.setText(title.getText().toString().trim().isEmpty() ? "Tên phim của bạn" : title.getText());
        preview.setTitleColors(startColor(), endColor());
        endBox.setVisibility(gradient.isChecked() ? View.VISIBLE : View.GONE);
        startBox.setError(null);
        endBox.setError(null);
    }

    boolean validate() {
        if (!MovieTitleView.validColor(startColor())) {
            startBox.setError("Nhập 6 ký tự màu, ví dụ #60A5FA");
            start.requestFocus();
            return false;
        }
        if (gradient.isChecked() && !MovieTitleView.validColor(endColor())) {
            endBox.setError("Nhập 6 ký tự màu, ví dụ #E2E8F0");
            end.requestFocus();
            return false;
        }
        return true;
    }

    String startColor() { return start.getText().toString().trim(); }
    String endColor() { return gradient.isChecked() ? end.getText().toString().trim() : ""; }

    private static Chip chip(Context context, String text) {
        Chip chip = new Chip(context);
        chip.setId(View.generateViewId());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setEnsureMinTouchTargetSize(true);
        return chip;
    }

    private static TextInputLayout field(Context context, String hint) {
        TextInputLayout box = new TextInputLayout(context);
        box.setHint(hint);
        box.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        float radius = dp(context, 12);
        box.setBoxCornerRadii(radius, radius, radius, radius);
        TextInputEditText input = new TextInputEditText(box.getContext());
        input.setBackground(null);
        input.setSingleLine(true);
        input.setTextSize(14);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(7)});
        box.addView(input, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(context, 8);
        params.bottomMargin = dp(context, 8);
        box.setLayoutParams(params);
        return box;
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
