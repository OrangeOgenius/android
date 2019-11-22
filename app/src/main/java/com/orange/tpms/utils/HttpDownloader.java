package com.orange.tpms.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Spring on 2015/11/7.
 * 下载工具类
 */
public class HttpDownloader {
    public static String ROOT_DIR = Environment.getExternalStorageDirectory().toString() + "/";
    public static String TPMS_DIR = "tpms/";      // sensor=>update.s19

    private URL url = null;
    private final String TAG = HttpDownloader.class.getName();

    /**
     * 读取文本文件
     * @param urlStr url路径
     * @return 文本信息
     * 根据url下载文件，前提是这个文件中的内容是文本，
     * 1.创建一个URL对象
     * 2.通过URL对象，创建一个Http连接
     * 3.得到InputStream
     * 4.从InputStream中得到数据
     */
    public String download(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader bufferedReader = null;

        try {
            url = new URL(urlStr);
            //创建http连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //使用IO流读取数据
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG",urlStr);
        Log.e("TAG",sb.toString());
        return sb.toString();
    }

    /**
     * 读取任何文件
     * 返回-1 ，代表下载失败。返回0，代表成功。返回1代表文件已经存在
     * @param urlStr
     * @param path
     * @param fileName
     * @return
     */
    public int downlaodFile(Context context, String urlStr, String path, String fileName, FileUtil.FilePrograss filePrograss) {
        InputStream input = null;

        try {
            FileUtil fileUtil = new FileUtil(context);
            if (fileUtil.isFileExist(path + fileName)) {
                return 1;
            } else {
                input = getInputStearmFormUrl(urlStr);
                File resultFile = fileUtil.write2SDFromInput(path,fileName,input, filePrograss);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (IOException e) {
            filePrograss.fail(e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                filePrograss.fail(e.getMessage());
                e.printStackTrace();
            }
        }
        return  0;
    }

    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param fileName 文件名字
     */
    public void download (String path, String dir, String fileName, FileUtil.FilePrograss filePrograss) {
        new Thread(() -> {
            try {
                URL url=new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(240*1000);
                InputStream is=conn.getInputStream();
                String filePath = ROOT_DIR+dir+fileName;
                Log.d(TAG, "download filePath:"+filePath);
                File fileHandler = new File (filePath);
                FileOutputStream fos=new FileOutputStream(fileHandler);
                int bufferSize = 8192;
                byte[] buf = new byte[bufferSize];
                int pass=0;
                while(true){
                    int read=is.read(buf);
                    pass+=read;
                    if(read==-1){  break;}
                    fos.write(buf, 0, read);
                    filePrograss.progress(19*1024,pass);
                }
                is.close();
                fos.close();
                filePrograss.finish(19*1024);
//                Log.d(TAG, "download: "+path+",dir:"+dir+",fileName:"+fileName);
//                URL url = new URL(path);
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setReadTimeout(1000*300);
//                con.setConnectTimeout(1000*300);
//                con.connect();
//                if (con.getResponseCode() == 200) {
//                    InputStream is = con.getInputStream();//获取输入流
//                    FileOutputStream fileOutputStream = null;//文件输出流
//                    if (is != null) {
//                        File dirFile = new File (ROOT_DIR+dir);
//                        if (!dirFile.exists()) {
//                            dirFile.mkdir();
//                        }
//                        String filePath = ROOT_DIR+dir+fileName;
//                        Log.d(TAG, "download filePath:"+filePath);
//
//                        File fileHandler = new File (filePath);
//
//                        fileOutputStream = new FileOutputStream(fileHandler);//指定文件保存路径，代码看下一步
//                        byte[] buf = new byte[8192];
//                        int total = is.available();
//                        filePrograss.start(total);
//                        int pass=0;
//                        while (true) {
//                            int read=is.read(buf);
//                            pass+=read;
//                            if(read==-1){  break;}
//                            fileOutputStream.write(buf, 0, read);
//                            filePrograss.progress(total,pass);
//                        }
//
//                        filePrograss.finish(total);
//                    }
//                    if (fileOutputStream != null) {
//                        fileOutputStream.flush();
//                        fileOutputStream.close();
//                    }
//                } else {
//                    filePrograss.fail(con.getResponseCode()+"");
//                }
            } catch (Exception e) {
                filePrograss.fail(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public InputStream getInputStearmFormUrl(String urlStr) throws IOException {
        url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream input = urlConn.getInputStream();
        return input;
    }
}
