package core.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Encrypt {

    public static boolean initialized = false;
    public static final String ALGORITHM = "AES/ECB/PKCS7Padding";

    public static Key getKey(String keyRule) {
        Key key = null;
        byte [] keyByte = keyRule.getBytes(StandardCharsets.UTF_8);
        byte [] byteTemp = new byte[32];
        for (int i = 0; i <byteTemp.length && i <keyByte.length; i ++) {
            byteTemp [i] = keyByte [i];
        }
        key = new SecretKeySpec(byteTemp, "AES");
        return key;
    }

    /**
     * @param bytes array of bytes to be decrypted
     * @param key A 32-byte (256-bit) key to be used for encryption/decryption
     * @return String The decrypted string
     */
    public static String Aes256Decode(byte [] bytes, String key) {
        initialize ();
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance (ALGORITHM, "BC");
            cipher.init (Cipher.DECRYPT_MODE, getKey(key));
            byte [] decoded = cipher.doFinal (bytes);
            result = new String (decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return result;
    }

    public static void initialize () {
        if (initialized) return;
        Security.addProvider (new BouncyCastleProvider ());
        initialized = true;
    }
}
