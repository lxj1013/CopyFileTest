package cn.com.hisistar.copyfiletest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by lixinjian on 2018/3/29.
 */

public class StorageUtil {
    private static final String TAG = StorageUtil.class.getSimpleName();

    public static String getDefaultStoragePath() {
        String storagePathLast;
        SharedPreferences settingsPref = MyApplication.getContext().getSharedPreferences("histar_show_settingsPref", Context.MODE_PRIVATE);
        storagePathLast = settingsPref.getString("storage_path", Environment.getExternalStorageDirectory().getPath());
//        Log.e(TAG, "getDefaultStoragePath: " + storagePathLast);
        ArrayList<Volume> listVolume = StorageUtil.getVolume(MyApplication.getContext());
        for (int i = 0; i < listVolume.size(); i++) {
            if (storagePathLast.equals(listVolume.get(i).getPath())) {
                return storagePathLast;
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    /*
       获取全部存储设备信息封装对象
        */
    public static ArrayList<Volume> getVolume(Context context) {
        ArrayList<Volume> listStorageVolume = new ArrayList<Volume>();

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");

            method_volumeList.setAccessible(true);

            Object[] volumeList = (Object[]) method_volumeList.invoke(storageManager);
            if (volumeList != null) {
                Volume volume;
                for (int i = 0; i < volumeList.length; i++) {
                    try {
                        volume = new Volume();
                        volume.setPath((String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]));
                        volume.setRemovable((boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]));
                        volume.setState((String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]));
                        listStorageVolume.add(volume);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Log.e(TAG, "null-------------------------------------");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return listStorageVolume;
    }


    /*
     存储设备信息封装类
     */
    public static class Volume {
        protected String path;
        protected boolean removable;
        protected String state;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isRemovable() {
            return removable;
        }

        public void setRemovable(boolean removable) {
            this.removable = removable;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
