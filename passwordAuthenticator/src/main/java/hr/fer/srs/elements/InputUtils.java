package hr.fer.srs.elements;

import hr.fer.srs.exceptions.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class InputUtils {
    private static final int MAX_ELEMENT_BYTE_SIZE = 256;

    public static String getPassword(boolean repeat, boolean user) {
        Console c = System.console();
        if (c == null) throw new PasswordAuthenticatorException("No system console available.");

        String password = String.valueOf(c.readPassword(user ? "New password: " : "Password: "));

        checkPasswordContent(password);
        checkPasswordComplexity(password);

        if (repeat) {
            if (!password.equals(String.valueOf(c.readPassword(user ? "Repeat new password: " : "Repeat password: "))))
                throw new InvalidEntryException("Password mismatch.");
        }

        return password;
    }

    public static void checkReusedPassword(String usernameHash, String
            password, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(usernameHash);
        requireNonNull(password);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        HashSet<String> oldPasswords = oldHashedPasswords.get(usernameHash);
        oldPasswords.forEach(passwordPair -> {
            String passwordHash = passwordPair.substring(0, CryptoUtils.PASSWORD_HASH_LENGTH_BYTES * 2);
            String salt = passwordPair.substring(CryptoUtils.PASSWORD_HASH_LENGTH_BYTES * 2);

            if (HexUtils.bytetohex(CryptoUtils.generatePasswordHash(password.toCharArray(), HexUtils.hextobyte(salt))).equals(passwordHash))
                throw new ResetPasswordException("Cannot reuse already used password for the same user.");
        });
    }

    public static void checkUsernameContent(String username) {
        requireNonNull(username);

        if (username.length() > MAX_ELEMENT_BYTE_SIZE)
            throw new PasswordAuthenticatorException("Username larger than 256 characters.");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(username))
            throw new PasswordAuthenticatorException("Non-ASCII characters within the username.");
    }

    private static void checkPasswordContent(String password) {
        requireNonNull(password);

        if (password.length() > MAX_ELEMENT_BYTE_SIZE)
            throw new PasswordAuthenticatorException("Password larger than 256 characters.");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(password))
            throw new PasswordAuthenticatorException("Non-ASCII characters within the password.");
    }

    private static void checkPasswordComplexity(String password) {
        requireNonNull(password);

        if (password.length() < 8)
            throw new InvalidEntryException("The password needs to be at least 8 characters long.");

        if (!password.matches("^.*[^a-zA-Z0-9].*$"))
            throw new InvalidEntryException("The password must contain at least one non-alphanumeric character.");

        if (!password.matches("^.*[A-Z].*$"))
            throw new InvalidEntryException("The password must contain at least one uppercase ASCII letter.");

        if (!password.matches("^.*[0-9].*$"))
            throw new InvalidEntryException("The password must contain at least one digit (0-9).");
    }
}
