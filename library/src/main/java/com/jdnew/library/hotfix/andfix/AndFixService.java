package com.jdnew.library.hotfix.andfix;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import java.io.File;

/**
 * AndFix 服务
 */
public class AndFixService extends Service {

    private String mPatchFileDir;
    private String mPatchFile;
    private static final int UPDATE_PATCH = 0x02;
    private static final int DOWNLOAD_PATCH = 0x01;
    private static final String FILE_END = ".apatch";
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mPatchFileDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        File patchDir = new File(mPatchFileDir);

        try{
            if (patchDir == null || !patchDir.exists()) {
                patchDir.mkdir();
            }
        }catch (Exception e){
            e.printStackTrace();
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mHandler.sendEmptyMessage()



        return START_NOT_STICKY;
    }
}
