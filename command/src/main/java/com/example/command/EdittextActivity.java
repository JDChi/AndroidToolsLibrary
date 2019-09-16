package com.example.command;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EdittextActivity extends AppCompatActivity {

    private EditText editText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittext);

        editText = findViewById(R.id.et_input);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //设置字数范围限制
                if (s.length() <= 0) {
//                    mTvLetterCount.setVisibility(View.GONE);
                } else {
//                    mTvLetterCount.setVisibility(View.VISIBLE);
//                    if (s.length() <= 500) {
//                        isExceedWordCount = false;
//                        mTvLetterCount.setText(s.length() + "/500");
//                    } else {
//                        isExceedWordCount = true;
//                        spannable = new SpannableStringBuilder(s.length() + "/500");
//                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8080")), 0,
//                                String.valueOf(s.length()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        mTvLetterCount.setText(spannable);
//                    }
                }
            }
        });


        //edittext拦截滑动冲突，让自己可以上下滚动
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (v.getId() == R.id.et_input && canVerticalScroll(mEtInput)) {
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                }
                return false;
            }
        });

    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param contentEt 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText contentEt) {
        //滚动的距离
        int scrollY = contentEt.getScrollY();
        //控件内容的总高度
        int scrollRange = contentEt.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = contentEt.getHeight() - contentEt.getCompoundPaddingTop() - contentEt.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }
}
