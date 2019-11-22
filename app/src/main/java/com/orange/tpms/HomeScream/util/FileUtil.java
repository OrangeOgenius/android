package com.orange.tpms.HomeScream.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtil {

    private String path = Environment.getExternalStorageDirectory().toString() + "/";

    private final String TAG = FileUtil.class.getName();
    private String SDPATH;
    private Context context;

    public FileUtil(Context context) {
        SDPATH = Environment.getExternalStorageDirectory() + "/" ;
    }

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtil(String SDPATH){
        //得到外部存储设备的目录（/SDCARD）
        SDPATH = Environment.getExternalStorageDirectory() + "/" ;
    }

    /**
     * 覆盖复制单个文件
     *
     * @param oldPathName String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPathName String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return 复制结果
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean copyFile(String oldPathName, String newPathName){
        File oldFile = new File(oldPathName);
        File newFile = new File(newPathName);
        try {
            //删除目标文件
            if(newFile.exists()){
                newFile.delete();
            }
            //创建目标文件
            if(!newFile.exists()){
                newFile.createNewFile();
            }
            FileInputStream inStream = new FileInputStream(oldFile);
            FileOutputStream outStream = new FileOutputStream(newFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            //复制完删除源文件
            oldFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在SD卡上创建文件
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        Log.d(TAG, "createSDFile: "+SDPATH+fileName);
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName 目录名字
     * @return 文件目录
     */
    public File createDir(String dirName){
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public interface FilePrograss {
        public abstract void progress(int total, int progress);
        public abstract void finish(int total);
        public abstract void start(int total);
        public abstract void fail(String msg);
    }

    public FileUtil() {
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String dir, String FileName) {
        String myPath = path+dir;
        Log.d(TAG, "download myPath:"+myPath+",filename:"+FileName);
        return new File(myPath, FileName);
    }

    /**
     * 获取字节流
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName, InputStream input, FilePrograss filePrograss){
        File file = null;
        OutputStream output = null;

        Log.d(TAG, String.format("path:%s,fileName:%s", path, fileName));
        try {
            int totalByte = input.available();
            filePrograss.start(totalByte);

            createDir(path);
            file =createSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte [] buffer = new byte[4 * 1024];
            int readByte = 0;       // 已经读到的字节数
            int hasReadByte = 0;        // 已经完成的字节数

            while((readByte=input.read(buffer)) != -1){
                hasReadByte+=readByte;
                output.write(buffer);
                output.flush();
                filePrograss.progress(totalByte, hasReadByte);
            }

            filePrograss.finish(totalByte);
        } catch (IOException e) {
            filePrograss.fail(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                output.close();
            } catch (IOException e) {
                filePrograss.fail(e.getMessage());
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
