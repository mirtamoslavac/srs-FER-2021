package hr.fer.srs.elements;

import hr.fer.srs.exceptions.PasswordManagerException;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class CryptoUtils {
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SYMMETRIC_ALGORITHM = "AES";
    private static final int ITERATION_COUNT = 100000;
    private static final int AES_KEY_SIZE = 256;
    public static final int IV_LENGTH = 12;
    public static final int SALT_LENGTH = 16;
    private static final String AE_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int AE_TAG_LENGTH = 128;
    public static final String PADDING_CHARACTER = "ž";
    public static final String EXTRA_PADDING_CHARACTER = "0";
    public static final int PADDING_CHARACTER_BYTE_SIZE = PADDING_CHARACTER.getBytes(UTF_8).length;
    public static final String DELIMITER_CHARACTER = "š";
    public static final int DELIMITER_CHARACTER_BYTE_SIZE = DELIMITER_CHARACTER.getBytes(UTF_8).length;

    public static byte[] generateSaltOrIV(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static SecretKey deriveAESKeyFromMasterPassword(char[] masterPassword, byte[] salt) {
        requireNonNull(masterPassword);
        requireNonNull(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(masterPassword, salt, ITERATION_COUNT, AES_KEY_SIZE);
            return new SecretKeySpec(SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM).generateSecret(spec).getEncoded(), SYMMETRIC_ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordManagerException(e.getMessage());
        }
    }

    public static byte[] AEEncrypt(byte[] plaintext, SecretKey secretKey, byte[] IV) {
        requireNonNull(plaintext);
        requireNonNull(secretKey);
        requireNonNull(IV);

        try {
            Cipher cipher = Cipher.getInstance(AE_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(AE_TAG_LENGTH, IV));

            byte[] encryptedCiphertext = cipher.doFinal(plaintext);
            byte[] aeOutput = new byte[IV.length + encryptedCiphertext.length];
            System.arraycopy(IV, 0, aeOutput, 0, IV.length);
            System.arraycopy(encryptedCiphertext, 0, aeOutput, IV.length, encryptedCiphertext.length);

            return aeOutput;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new PasswordManagerException(e.getMessage());
        }
    }

    public static String AEDecrypt(byte[] input, SecretKey secretKey) {
        requireNonNull(input);
        requireNonNull(secretKey);

        byte[] IV = new byte[IV_LENGTH];
        System.arraycopy(input, 0, IV, 0, IV_LENGTH);

        byte[] ciphertext = new byte[input.length - IV_LENGTH];
        System.arraycopy(input, IV_LENGTH, ciphertext, 0, ciphertext.length);

        try {
            Cipher cipher = Cipher.getInstance(AE_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AE_TAG_LENGTH, IV));
            byte[] plainText = cipher.doFinal(ciphertext);
            return new String(plainText, UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new PasswordManagerException(e.getMessage());
        }
    }

    public static String[] retrieveRawDecryptedPair(String masterPasswordRaw, String line) {
        requireNonNull(masterPasswordRaw);
        requireNonNull(line);

        byte[] hexEncryptedValues = HexUtils.hextobyte(line);

        byte[] Salt = new byte[SALT_LENGTH];
        System.arraycopy(hexEncryptedValues, 0, Salt, 0, SALT_LENGTH);

        int IVAndCiphertextLength = hexEncryptedValues.length - SALT_LENGTH;
        byte[] IVAndCiphertext = new byte[IVAndCiphertextLength];
        System.arraycopy(hexEncryptedValues, SALT_LENGTH, IVAndCiphertext, 0, IVAndCiphertextLength);

        return unpadPair(AEDecrypt(IVAndCiphertext, CryptoUtils.deriveAESKeyFromMasterPassword(masterPasswordRaw.toCharArray(), Salt))).split(DELIMITER_CHARACTER);
    }

    public static String padPair(int maxPaddedPairByteSize, String unpaddedPair) {
        int leftoverBytes = maxPaddedPairByteSize - unpaddedPair.getBytes(UTF_8).length;
        int overflow = leftoverBytes % PADDING_CHARACTER_BYTE_SIZE;
        int n = leftoverBytes / PADDING_CHARACTER_BYTE_SIZE;

        return unpaddedPair + PADDING_CHARACTER.repeat(n) + EXTRA_PADDING_CHARACTER.repeat(overflow);
    }

    private static String unpadPair(String plaintext) {
        if (plaintext.contains(PADDING_CHARACTER)) {
            while (plaintext.endsWith(EXTRA_PADDING_CHARACTER)) plaintext = plaintext.substring(0, plaintext.length() - 1);
            plaintext = plaintext.replaceAll(PADDING_CHARACTER, "");
        }
        return plaintext;
    }
}
