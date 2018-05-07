package cn.com.hisistar.copyfiletest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyFileThread extends Thread {
    private static final String TAG = "CopyFileThread";

    private String srcPath;//原文件地址
    private String destPath;//目标文件地址
    private ProgressDialog mProgressDialog;
    private Handler mHandler;
    private Context mContext;

    private LocalBroadcastManager localBroadcastManager;

    public CopyFileThread(String srcPath, String destPath, Context context) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.mContext = context;
        mHandler = new Handler();

        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.setTitle("正在导入节目，请稍等，勿作其他动作！");

    }

    @Override
    public void run() {
        int flag = copy(srcPath, destPath);
        if (flag == -1) {
            Log.e(TAG, "run: " + "源文件不存在");
        } else {
            Log.e(TAG, "run: " + "cn.com.hisistar.copyfiletest.TEST");
            Intent intent = new Intent("cn.com.hisistar.copyfiletest.TEST");
            localBroadcastManager.sendBroadcast(intent);
        }
        mProgressDialog.dismiss();
        super.run();
    }

    /**
     * 根据文件路径拷贝文件
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     * @return boolean 成功true、失败false
     */
    public boolean copyFile(final String srcPath, String destPath) {
        boolean result = false;
        if ((srcPath == null) || (destPath == null)) {
            return result;
        }
        File destFile = new File(destPath);
        if (destFile.exists()) {
            destFile.delete(); // delete file
        }
        final File srcFile = new File(srcPath);

        try {
            final FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
            final FileChannel dstChannel = new FileOutputStream(destFile).getChannel();
            Log.i(TAG, "copyFile: " + srcFile.getPath() + "  srcChannel.size() = " + srcChannel.size());

            Log.i(TAG, "copyFile: dstChannel.size() = " + dstChannel.size());

            for (int i = 1; i <= 100; i++) {
                if (i < 100) {
                    srcChannel.transferTo(dstChannel.size(), srcChannel.size() / 99, dstChannel);
                    Log.i(TAG, "copyFile: dstChannel.size() " + i + " = " + dstChannel.size());
                } else {
                    srcChannel.transferTo(dstChannel.size(), srcChannel.size() % 99, dstChannel);
                    Log.i(TAG, "copyFile: dstChannel.size() " + i + " = " + dstChannel.size());
                }
//                mProgressDialog.setTitle("this is message");
                final int count = i;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage(srcFile.getPath());
                        mProgressDialog.setProgress(count);
                        mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        mProgressDialog.show();
                    }
                });
            }
            srcChannel.close();
            dstChannel.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录

        File targetDir = new File(toFile);

        deleteDirWihtFile(targetDir);

        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                copyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }


}
