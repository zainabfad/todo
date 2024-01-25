package project.Todolist.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AESGCMNoPadding {
    @Value("${encrypt.password}")
    private String password;
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private String encrypt(String text, String password) {

        try {
            byte[] decodedKey = Base64.getDecoder().decode(password);
            byte[] iv = getRandomNonce(IV_LENGTH);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Cipher aesCipher = Cipher.getInstance(ENCRYPT_ALGO);
            aesCipher.init(Cipher.ENCRYPT_MODE, originalKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] byteCipherText = aesCipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + byteCipherText.length).put(iv).put(byteCipherText).array();
            return Base64.getEncoder().encodeToString(cipherTextWithIv);

        } catch (Exception e) {
            log.error("Error occurred in encryption method", e);
            return null;
        }
    }

    private String decrypt(String cText, String password) {

        try {
            byte[] decodedKey = Base64.getDecoder().decode(password);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            byte[] decode = Base64.getDecoder().decode(cText.getBytes(StandardCharsets.UTF_8));
            ByteBuffer bb = ByteBuffer.wrap(decode);
            byte[] iv = new byte[IV_LENGTH];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, originalKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error occurred in decryption method", e);
            return null;
        }
    }

    protected static byte[] getRandomNonce(int len) {
        byte[] nonce = new byte[len];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public String decrypt(String text) {
        try {
            return decrypt(text, password);
        } catch (Exception e) {
            return null;
        }
    }

    public String encrypt(String plainText) {
        try {
            log.info("Encrypting...");
            return encrypt(plainText, password);
        } catch (Exception e) {
            log.error("Error Decrypting: ", e);
        }
        return null;
    }
}
