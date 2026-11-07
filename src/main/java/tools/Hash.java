package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;


public class Hash {

    public static String md5(File file) throws IOException {
        MessageDigest messageDigest;
        try (InputStream fis = new FileInputStream(file)) {
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[128 * 1024];
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    messageDigest.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            byte[] bytes = messageDigest.digest();
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
