package com.jdnew.library.hotfix.andfix;

import android.content.Context;

import com.alipay.euler.andfix.patch.PatchManager;

import java.io.IOException;

/**
 * AndFix 管理工具
 */
public class AndFixManager {


    private static volatile AndFixManager mInstance = null;
    private PatchManager mPatchManager;
    private AndFixManager(){
    }

    public static AndFixManager Instance(){
        if(mInstance == null){
            synchronized (AndFixManager.class){
                if (mInstance == null) {
                    mInstance = new AndFixManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     * @param context
     * @param version
     */
    public void initPatch(Context context , String version){
        mPatchManager = new PatchManager(context);
        mPatchManager.init(version);
        mPatchManager.loadPatch();

    }

    /**
     * 加载 patch 文件
     * @param path
     */
    public void addPatch(String path){
        if (mPatchManager != null) {
            try {
                mPatchManager.addPatch(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
