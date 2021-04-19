package com.kemriwellcome.dm.prisms.dependencies;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fxn.stash.Stash;

public class PrismsApplication extends Application {

    public static final String TAG = PrismsApplication.class.getSimpleName();

    private static PrismsApplication instance;
    private static Context appContext;


    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    private RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Stash.init(this);

        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public static synchronized PrismsApplication getInstance() {
        return instance;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
//
//    public static String getDeviceId(Context context) {
//
//        String deviceId;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            deviceId = Settings.Secure.getString(
//                    context.getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
//        } else {
//            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                if (mTelephony.getDeviceId() != null) {
//                    deviceId = mTelephony.getDeviceId();
//                } else {
//                    deviceId = Settings.Secure.getString(
//                            context.getContentResolver(),
//                            Settings.Secure.ANDROID_ID);
//                }
//            }else {
//                deviceId = "";
//            }
//
//        }
//
//        return deviceId;
//    }
}