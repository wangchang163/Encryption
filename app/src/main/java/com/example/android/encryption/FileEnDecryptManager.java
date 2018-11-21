package com.example.android.encryption;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 加密解密管理类
 * <p>
 * 加密算法 : 将文件的数据流的每个字节与该字节的下标异或.
 * 解密算法 : 已经加密的文件再执行一次对文件的数据流的每个字节与该字节的下标异或
 *
 * @author Administrator
 */
public class FileEnDecryptManager {

    /**
     * 加密入口
     *
     * @param fileUrl 文件绝对路径
     * @return
     */
    public static void initEncrypt(Context context,String fileUrl) {
        if (!isEncrypt(context,fileUrl)) {
            encrypt(fileUrl);
            SPUtil.putBoolean(context, fileUrl, true);
            Log.e("TAG", "文件加密成功");
        } else {
            Log.e("TAG", "文件已加密");
        }
    }

    private static boolean isEncrypt(Context context,String fileUrl) {
        return SPUtil.getBoolean(context, fileUrl, false);
    }

    private static final int REVERSE_LENGTH = 56;

    /**
     * 加解密
     *
     * @param strFile 源文件绝对路径
     * @return
     */
    private static boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH)
                len = (int) totalLen;

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解密入口
     *
     * @param fileUrl 源文件绝对路径
     */
    public static void initdecrypt(Context context, String fileUrl) {
        try {
            if (isEncrypt(context,fileUrl)) {
                decrypt(fileUrl);
                SPUtil.putBoolean(context, fileUrl, false);
                Log.e("TAG", "文件解密成功");
            } else {
                Log.e("TAG", "文件已解密");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decrypt(String fileUrl) {
        encrypt(fileUrl);
    }

}
