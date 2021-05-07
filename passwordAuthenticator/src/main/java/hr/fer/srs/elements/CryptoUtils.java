package hr.fer.srs.elements;

import hr.fer.srs.exceptions.PasswordAuthenticatorException;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

import static java.util.Objects.requireNonNull;

public class CryptoUtils {
    public static final int SALT_SIZE_BYTES = 16;
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 100000;
    private static final int KEY_SIZE = 256;
    public static final int USERNAME_LENGTH_BYTES = 32;
    public static final int PASSWORD_HASH_LENGTH_BYTES = 32;

    public static byte[] generateSalt() {
        byte[] nonce = new byte[SALT_SIZE_BYTES];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static byte[] generatePasswordHash(char[] password, byte[] salt) {
        requireNonNull(password);
        requireNonNull(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_SIZE);
            return SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

}
