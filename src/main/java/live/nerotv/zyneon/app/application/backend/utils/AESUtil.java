package live.nerotv.zyneon.app.application.backend.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static SecretKeySpec deriveAESKeyFromPassphrase(String passphrase, byte[] salt, int iterations, int keyLength) throws InvalidKeySpecException, NoSuchAlgorithmException {
        char[] passphraseChars = passphrase.toCharArray();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(passphraseChars, salt, iterations, keyLength);
        byte[] derivedKeyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(derivedKeyBytes, "AES");
    }

    public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
        Key secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encode(cipher.doFinal(data));
    }

    public static byte[] decrypt(byte[] key, byte[] encryptedData) throws Exception {
        encryptedData = Base64.getDecoder().decode(encryptedData);
        Key secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }
}