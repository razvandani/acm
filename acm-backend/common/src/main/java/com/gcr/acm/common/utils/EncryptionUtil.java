package com.gcr.acm.common.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encryption util class.
 *
 * @author Razvan Dani
 */
@Component
public class EncryptionUtil {
    private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final Integer SECRET_KEY_RELEVANT_BYTES_LENGTH = 16;
    public static final String ENCRYPTION_UTIL_COMPONENT_NAME = "encryptionUtil";

    private SecretKeySpec secretKeySpec;

    @Value("${encryption.passphrase}")
    public void setEncryptionPassphrase(String encryptionPassphrase) {
        try {
            MessageDigest digest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
            byte[] passphraseBytes = encryptionPassphrase.getBytes();
            digest.update(passphraseBytes);
            secretKeySpec = new SecretKeySpec(digest.digest(), 0, SECRET_KEY_RELEVANT_BYTES_LENGTH, SECRET_KEY_ALGORITHM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Encrypts the specified source and returns the encryption result.
     *
     * @param source The source to encrypt
     * @return The encrypted result
     */
    public String encrypt(String source) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        if (source == null) {
            log.warn("Cannot encrypt null value");
            return null;
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] cipherBytes = cipher.doFinal(source.getBytes());

        return Base64.encodeBase64String(cipherBytes);
    }

    /**
     * Decrypts the specified encrypted text and returns the result.
     *
     * @param encryptedText The encrypted text
     * @return The decryption result
     */
    public String decrypt(String encryptedText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        if (encryptedText == null) {
            log.warn("Cannot decrypt null value");
            return null;
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = Base64.decodeBase64(encryptedText);

        return new String(cipher.doFinal(encryptedBytes), ENCODING_UTF8);
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext();

        EncryptionUtil encryptionUtil = (EncryptionUtil) applicationContext.getBean("encryptionUtil");

        if (args.length == 2) {
            String action = args[0];

            if (action.equals("encrypt")) {
                String textToEncrypt = args[1];
//                System.out.println("encrypted text = " + encryptionUtil.encrypt(textToEncrypt));
            } else if (action.equals("decrypt")) {
                String textToDecrypt = args[1];
//                System.out.println("decrypted text = " + encryptionUtil.decrypt(textToDecrypt));
            }
        }
    }
}
