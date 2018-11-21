package com.example.android.encryption;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者    wangchang
 * 时间    2018/11/20 16:45
 * 文件    Encryption
 * 描述
 */
public class AESManager {

    private static String password = "123456789abcdefg";
    private static String TAG="AESManager";

    /**
     * 加密
     *
     * @param context
     * @param file
     */
    public static boolean encrypt(Context context, File file) {
        if (!isEncrypt(context,file)){
            FileInputStream fis = null;
            FileOutputStream fos=null;
            try {
                fis = new FileInputStream(file);
                byte[] oldByte = new byte[(int) file.length()];
                fis.read(oldByte);
                // 读取
                byte[] newByte = AESUtils.encrypt(context,oldByte,password);
                // 加密
                fos = new FileOutputStream(file);
                fos.write(newByte);
                Log.e(TAG,"加密成功");
                SPUtil.putBoolean(context,file.getAbsolutePath(),true);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }else {
            Log.e(TAG,"已经加过密");
            return true;
        }

    }

    /**
     * 解密
     *
     * @param context
     * @param oldFile
     */
    public static boolean decrypt(Context context, File oldFile) {
        if (isEncrypt(context,oldFile)){
            FileInputStream fis = null;
            FileOutputStream fos=null;
            byte[] oldByte = new byte[(int) oldFile.length()];
            try {
                fis = new FileInputStream(oldFile);
                fis.read(oldByte);
                byte[] newByte = AESUtils.decrypt(context,oldByte,password);
                // 解密
                fos = new FileOutputStream(oldFile);
                fos.write(newByte);
                Log.e(TAG,"解密成功");
                SPUtil.putBoolean(context,oldFile.getAbsolutePath(),false);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }finally {
                try {
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Log.e(TAG,"文件未加密");
            return false;
        }


    }

    private static boolean isEncrypt(Context context,File file){
        String path=file.getAbsolutePath();
        return SPUtil.getBoolean(context,path,false);
    }
}
