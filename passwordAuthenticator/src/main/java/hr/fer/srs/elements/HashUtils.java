package hr.fer.srs.elements;

import hr.fer.srs.exceptions.PasswordAuthenticatorException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;

import static java.util.Objects.requireNonNull;

public class HashUtils {
    private static final String HASHING_FUNCTION = "SHA-256";
    private static final int BUFFER_SIZE = 4096;

    public static byte[] generateUsernameHash(String username) {
        requireNonNull(username);

        try {
            return MessageDigest.getInstance(HASHING_FUNCTION).digest(username.getBytes(StandardCharsets.US_ASCII));
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    public static byte[] calculateDatabaseChecksum(String pathString) {
        requireNonNull(pathString);

        try {
            MessageDigest sha256 = MessageDigest.getInstance(HASHING_FUNCTION);
            try (InputStream is = new BufferedInputStream(Files.newInputStream(Path.of(pathString)))) {
                byte[] buf = new byte[BUFFER_SIZE];

                do {
                    int k = is.read(buf);
                    if (k == -1) break;

                    sha256.update(buf, 0, k);
                } while (true);
            }

            return sha256.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }
}
