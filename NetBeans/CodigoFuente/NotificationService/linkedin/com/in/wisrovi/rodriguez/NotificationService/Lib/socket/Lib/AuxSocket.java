/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SenInt2.fcv.org.NotificationService.linkedin.com.in.wisrovi.rodriguez.NotificationService.Lib.socket.Lib;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 *
 * @author williamrodriguez
 */
public class AuxSocket {
    public static String encodeUrlBase64(String str) {
        byte[] bytesEncoded = Base64.encodeBase64URLSafe(str.getBytes());
        return new String(bytesEncoded);
    }

    public static String decodeUrlBase64(String str) {
        Base64 decoder = new Base64(true);
        byte[] decodedBytes = decoder.decode(str);
        return new String(decodedBytes);
    }
}
