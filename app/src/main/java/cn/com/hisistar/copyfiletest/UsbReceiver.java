package cn.com.hisistar.copyfiletest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class UsbReceiver extends BroadcastReceiver {
    private static final String TAG = UsbReceiver.class.getSimpleName();
    private final static String TO_PATH = "/mnt/sdcard/histarProgram/";
    CopyFileThread copyFileThread;
    AlertDialog.Builder mBuilder;
    AlertDialog mAlertDialog;
    CountDownTimer countDownTimer;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Log.d(TAG, "onReceive: MOUNTED");
            final String programPath = getStorage() + "/histarProgram";
            File programDir = new File(programPath);

            if (programDir.exists()) {
                Toast.makeText(context, "发现节目文件！", Toast.LENGTH_SHORT).show();
                countDownTimer = new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long l) {
                        mAlertDialog.setMessage(l / 1000 + "s后自动导入");
                        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

                        mAlertDialog.show();
                        Log.i(TAG, "onTick: " + l);
                    }

                    @Override
                    public void onFinish() {
                        mAlertDialog.dismiss();
                        copyFileThread = new CopyFileThread(programPath, TO_PATH, context);
                        copyFileThread.start();
                    }
                };
                countDownTimer.start();
                mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle("检测到外部节目，是否自动导入节目？");
                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(MainActivity.this, "YES!!!!!!", Toast.LENGTH_SHORT).show();
                        countDownTimer.cancel();
                        copyFileThread = new CopyFileThread(programPath, TO_PATH, context);
                        copyFileThread.start();
                    }
                });

                mBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "NO!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                        countDownTimer.cancel();
                    }
                });

                mAlertDialog = mBuilder.create();
            }
        }
        if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            Log.d(TAG, "onReceive: REMOVED");
        }
        if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            Log.d(TAG, "onReceive: UNMOUNTED");
        }
//        if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
//            Toast.makeText(context, "usb change", Toast.LENGTH_SHORT).show();
//        }
    }

    private String getStorage() {
        ArrayList<StorageUtil.Volume> list_volume = StorageUtil.getVolume(MyApplication.getContext());

        String path = list_volume.get(list_volume.size() - 1).getPath();

        Log.i(TAG, "getStorage: = " + path);

        return path;

        /*for (int i = 0; i < list_volume.size(); i++) {
            Log.e(TAG + " " + i, "path:" + list_volume.get(i).getPath() + "----" +
                    "removable:" + list_volume.get(i).isRemovable() + "---" +
                    "state:" + list_volume.get(i).getState());
        }*/
    }

}

