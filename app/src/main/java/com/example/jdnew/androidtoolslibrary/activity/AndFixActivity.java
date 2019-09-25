package com.example.jdnew.androidtoolslibrary.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jdnew.androidtoolslibrary.R;
import com.jdnew.library.hotfix.andfix.AndFixManager;

import java.io.File;

public class AndFixActivity extends AppCompatActivity {
    private Button mBtBug;
    private Button mBtFixBug;

    private static final String FILE_END = ".apatch";
    private String mPatchDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andfix);

        mBtBug = findViewById(R.id.bt_bug);
        mBtFixBug = findViewById(R.id.bt_fix_bug);

        mPatchDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        //初始化 AndFix
        AndFixManager.Instance().initPatch(this, "1.0");
        Log.d("AndFix", mPatchDir);
        File file = new File(mPatchDir);
        if (file == null || !file.exists()) {
            file.mkdir();
        }


        mBtBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBug();
            }


        });

        mBtFixBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixBug();
            }
        });

    }




    private void createBug() {
        String bug = "hello";
        Log.d("AndFix", bug);
    }

    private void fixBug() {


        AndFixManager.Instance().addPatch(getPatchName());
        Log.d("AndFix", "fixBug");
        Log.d("AndFix", getPatchName());

    }

    private String getPatchName() {
        return mPatchDir.concat("andFix").concat(FILE_END);
    }

}
