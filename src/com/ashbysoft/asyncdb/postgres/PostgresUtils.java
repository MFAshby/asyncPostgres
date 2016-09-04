package com.ashbysoft.asyncdb.postgres;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PostgresUtils {
    /**
     * Nicked from postgres JDBC driver.
     */
    public static byte[] encodeMd5Password(byte user[], byte password[], byte salt[]) throws NoSuchAlgorithmException {
        byte[] temp_digest, pass_digest;
        byte[] hex_digest = new byte[35];

        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(password);
        md.update(user);
        temp_digest = md.digest();

        bytesToHex(temp_digest, hex_digest, 0);
        md.update(hex_digest, 0, 32);
        md.update(salt);
        pass_digest = md.digest();

        bytesToHex(pass_digest, hex_digest, 3);
        hex_digest[0] = (byte) 'm';
        hex_digest[1] = (byte) 'd';
        hex_digest[2] = (byte) '5';

        return hex_digest;
    }

    private static void bytesToHex(byte[] bytes, byte[] hex, int offset) {
        final char lookup[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        int i, c, j, pos = offset;
        for (i = 0; i < 16; i++) {
            c = bytes[i] & 0xFF;
            j = c >> 4;
            hex[pos++] = (byte) lookup[j];
            j = (c & 0xF);
            hex[pos++] = (byte) lookup[j];
        }
    }

    public static byte[] asciiBytesWithNull(String s) {
        byte[] asciis = asciiBytes(s);
        return Arrays.copyOf(asciis, asciis.length + 1);
    }

    public static byte[] asciiBytes(String s) {
        return s.getBytes(Charset.forName("ASCII"));
    }

    public static String nullTerminated(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        char c = (char)bb.get();
        while (c != '\0') {
            sb.append(c);
            c = (char)bb.get();
        }
        return sb.toString();
    }
}
