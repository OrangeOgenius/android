package com.orange.tpms.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;


public final class FormatConvert {
    private FormatConvert() {
        // this is just a helper class.
    }
    public  static String getCRC16(String source) {
        int crc = 0x0000; 		 	 // 初始值
        int polynomial = 0x1021;	         // 校验公式 0001 0000 0010 0001
        byte[] bytes = StringHexToByte(source.substring(2, source.length()-6));  //把普通字符串转换成十六进制字符串

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        StringBuffer result = new StringBuffer(Integer.toHexString(crc));
        while (result.length() < 4) {		 //CRC检验一般为4位，不足4位补0
            result.insert(0, "0");
        }
        return source.replace("XXXX", result.toString().toUpperCase());}
    public static CharSequence StringToByteMsg(CharSequence cs){
        StringBuffer sb = new StringBuffer();
        int x=0;
        for (int i=0;i<cs.length();i++){
            x++;
            sb.append(cs.charAt(i));
            if (x==2){
                x=0;
                sb.append(" ");
            }
        }
        return sb;
    }

    // byte转char
    public static char[] getChars (byte[] bytes) {
        Charset cs = Charset.forName ("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
        bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);

        return cb.array();
    }
    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }
    //ASCII中的16進位符號轉換成byte
    public static byte[] StringHexToByte(CharSequence cs){
        byte[] bytes = new byte[cs.length()/2];
        for (int i=0;i<(cs.length()/2);i++)
            bytes[i] = (byte) Integer.parseInt(cs.toString().substring(2*i,2*i+2),16);
        return bytes;
    }
    public static byte[] StringHexToByte(StringBuilder sb){
        byte[] bytes = new byte[sb.length()/2];
        for (int i=0;i<(sb.length()/2);i++)
            bytes[i] = (byte) Integer.parseInt(sb.toString().substring(2*i,2*i+2),16);
        return bytes;
    }


    public static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public static byte[] InttoByte(int Num, int ByteSize) {
        byte[] bytes = ByteBuffer.allocate(ByteSize).putInt(Num).array();
        for (byte b : bytes) {
            System.out.format("0x%x ", b);
        }
        return bytes;
    }

    public static int ByteArray2Int(byte[] b){
        int value=0;
        for(int i=3;i>-1;i--){
            value+=(b[i]&0x000000ff)<<(8*(4-1-i));
        }
        return value;
    }

    public static String convertASCII(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    public static byte[] StringToASCIIbyte(String data){
        byte[] B = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
            B[i]= (byte) data.charAt(i);
        }
        return B;
    }


    public static String DisplayTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss.SS");
        SimpleDateFormat formatter_D = new SimpleDateFormat("yyyy-hh:mm:ss");
        String date = formatter.format(new java.util.Date());
        return date;
    }

    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    public static int twoByteToInt(byte h,byte l){
        int H = h;
        if(l<0){
            H = h+1;
        }
        return (H << 8) + l;
    }
    public static String getBit(byte by){
        StringBuffer sb = new StringBuffer();
        sb.append((by>>7)&0x1);
        sb.append((by>>6)&0x1);
        sb.append((by>>5)&0x1);
        sb.append((by>>4)&0x1);
        sb.append((by>>3)&0x1);
        sb.append((by>>2)&0x1);
        sb.append((by>>1)&0x1);
        sb.append((by>>0)&0x1);
        return sb.toString();
    }

}
