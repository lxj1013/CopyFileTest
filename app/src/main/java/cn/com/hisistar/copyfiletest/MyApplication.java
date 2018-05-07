package cn.com.hisistar.copyfiletest;

import android.app.Application;
import android.content.Context;

/**
 * Created by lixinjian on 2018/3/29.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }
    public static Context getContext(){
        return context;
    }
}
