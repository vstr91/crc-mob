package br.com.vostre.circular.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Almir on 14/01/2015.
 */
public class Crypt {

    private String iv = "fbfac56366848a74";
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    private String SecretKey = "3c447059eba17a12";

    static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public Crypt() {
        ivspec = new IvParameterSpec(iv.getBytes());

        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception {
//        if (code == null || code.length() == 0) {
//            throw new Exception("Empty string");
//        }
//
//        byte[] decrypted = null;
//
//        try {
//            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
//
//            decrypted = cipher.doFinal(hexToBytes(code));
//        } catch (Exception e) {
//            throw new Exception("[decrypt] " + e.getMessage());
//        }
//        return decrypted;

        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            //Remove trailing zeroes
            if( decrypted.length > 0)
            {
                int trim = 0;
                for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                if( trim > 0 )
                {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e)
        {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;

    }

    public static String bytesToHex(byte[] data) {
//        if (data == null) {
//            return null;
//        }
//
//        int len = data.length;
//        String str = "";
//        for (int i = 0; i < len; i++) {
//            if ((data[i] & 0xFF) < 16) {
//                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
//            } else {
//                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
//            }
//        }
//        return str;

        char[] chars = new char[2 * data.length];
        for (int i = 0; i < data.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(data[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[data[i] & 0x0F];
        }
        return new String(chars);

    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    } //f63707b5e7684ee874888f2affb4398b

    private static String padString(String source) {
        //char paddingChar = ' '; ### original
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }

}
