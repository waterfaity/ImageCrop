package com.xueduoduo.imagecrop;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    public static File getSavePath( String extension) throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CropPictures");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) throw new IOException("文件夹创建失败!");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY_MM_dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        File saveFile = new File(file, format + "." + extension);
        if (!saveFile.exists()) {
            boolean newFile = false;
            try {
                newFile = saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!newFile) {
                throw new IOException("文件创建失败!");
            }
        }
        return saveFile;
    }

    public static void saveBitmap(Bitmap.CompressFormat format, Bitmap cropBitmap, File savePath) throws IOException {
        FileOutputStream fileOutputStream=new FileOutputStream(savePath);
        cropBitmap.compress(format,100,fileOutputStream);
        fileOutputStream.close();
    }
}
