package com.example.chen.sample;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BBlackB on 2016/10/5.
 */
public class PasswWd {
    private static byte[] prefix= {(byte)0xb3, (byte)0xc2, (byte)0xc3, (byte)0xf7, (byte)0xbc, (byte)0xfc};
    private static byte[] suffix = {(byte)0xc2, (byte)0xe3, (byte)0xcc, (byte)0xe5};
    public static String getPasswd(String wifi_ssid){
        return stringMD5(wifi_ssid);
    }

    public static String stringMD5(String input) {


        // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        // 输入的字符串转换成字节数组

        byte[] inputByteArray = input.getBytes();


        // inputByteArray是输入字符串转换得到的字节数组

        messageDigest.update(prefix);
        messageDigest.update(inputByteArray);
        messageDigest.update(suffix);

        // 转换并返回结果，也是字节数组，包含16个元素

        byte[] resultByteArray = messageDigest.digest();

        //重置messagedigest
        messageDigest.reset();

        messageDigest.update(prefix);
        messageDigest.update(resultByteArray);
        messageDigest.update(suffix);

        resultByteArray = messageDigest.digest();

        // 字符数组转换成字符串返回

        return byteArrayToHex(resultByteArray).toLowerCase();


    }

    //下面这个函数用于将字节数组换成成16进制的字符串

    public static String byteArrayToHex(byte[] byteArray) {

        // 首先初始化一个字符数组，用来存放每个16进制字符

        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };



        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

        char[] resultCharArray =new char[byteArray.length * 2];



        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去

        int index = 0;

        for (byte b : byteArray) {

            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];

            resultCharArray[index++] = hexDigits[b& 0xf];

        }



        // 字符数组组合成字符串返回

        return new String(resultCharArray);

    }

}
