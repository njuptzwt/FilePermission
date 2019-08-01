package com.example.filedemo;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 内存和外置SD卡的存放位置
 * 可否在非APP的目录下进行读写访问
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 这个是当判断手机没有外置的SD卡的时候调用内存存取文件，/data/user/0/com.example.filedemo/files
        Log.i("fileDemo", String.valueOf(getApplicationContext().getFilesDir()));
        Log.i("fileDemo", String.valueOf(getApplicationContext().getCacheDir()));
        // 外置的SD卡（需要运行时动态权限）/storage/emulated/0
        Log.i("fileDemo", String.valueOf(Environment.getExternalStorageDirectory()));
        // 每个运用程序自己的存储空间，删除删除应用及清空（不需要运行时动态权限）/storage/emulated/0/Android/data/com.example.filedemo/files
        Log.i("fileDemo",String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        Log.i("fileDemo", String.valueOf(getApplicationContext().getExternalFilesDir(null)));
        // 动态权限授予
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        try {
                            writeExternal(getApplicationContext(), "wentian1.txt", "hello world");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String result= null;
                        try {
                            result = readExternal(getApplicationContext(),"wentian1.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("fileDemo",result);


                        try {
                            writeInternal(getApplicationContext(),"zheng.txt","你好");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Log.d("fileDemo",readInternal(getApplicationContext(),"zheng.txt"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        try {
                            writeExternalWithoutPermission(getApplicationContext(), "wentian.txt", "hello world");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String result= null;
                        try {
                            result = readExternalWithoutPermission(getApplicationContext(),"wentian.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("fileDemo",result);
                    }
                });

    }


    /**
     * 向Sd卡中写入内容,需要申请权限就写入文件
     */
    public void writeExternal(Context context, String filename, String content) throws IOException {

        //获取外部存储卡的可用状态
        String storageState = Environment.getExternalStorageState();

        //判断是否存在可用的的SD Card
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            // getExternalFilesDir获取App的当前目录，APP的私有目录
            // 属于APP的私有空间，可以建立文件
            filename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filename;

            // Android Q中不再适用，需要再适配
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
    }


    /**
     * 向sd卡中读取内容
     */
    public String readExternal(Context context, String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            filename = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + filename;
//            //打开文件输入流
            filename =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filename;
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while (len > 0) {
                sb.append(new String(buffer, 0, len));

                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        }
        return sb.toString();
    }


    /**
     * 不需要书写权限，在应用的沙盒中写入文件，不是访问系统的读写权限
     */
    public void writeExternalWithoutPermission(Context context, String filename, String content) throws IOException {

        //获取外部存储卡的可用状态
        String storageState = Environment.getExternalStorageState();

        //判断是否存在可用的的SD Card
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            // getExternalFilesDir获取App的当前目录，APP的私有目录
            // 属于APP的私有空间，可以建立文件
            filename = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + filename;

            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
    }

    /**
     * 不需要书写权限读取沙盒内的运用数据
     */
    public String readExternalWithoutPermission(Context context, String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + filename;
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while (len > 0) {
                sb.append(new String(buffer, 0, len));

                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        }
        return sb.toString();
    }

    /**
     * 向内存卡中写文件的相关函数
     */
    public static void writeInternal(Context context, String filename, String content) throws IOException{
        //获取文件在内存卡中files目录下的路径
        File file = context.getFilesDir();
        filename = file.getAbsolutePath() + File.separator + filename;

        //打开文件输出流
        FileOutputStream outputStream = new FileOutputStream(filename);

        //写数据到文件中
        outputStream.write(content.getBytes());
        outputStream.close();
    }

    /**
     * 读内存中的文件
     * @param context
     * @param filename
     * @return
     * @throws IOException
     */
    public static String readInternal(Context context,String filename) throws IOException{
        StringBuilder sb = new StringBuilder("");

        //获取文件在内存卡中files目录下的路径
        File file = context.getFilesDir();
        filename = file.getAbsolutePath() + File.separator + filename;

        //打开文件输入流
        FileInputStream inputStream = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
        //读取文件内容
        while(len > 0){
            sb.append(new String(buffer,0,len));

            //继续将数据放到buffer中
            len = inputStream.read(buffer);
        }
        //关闭输入流
        inputStream.close();
        return sb.toString();
    }

}
