package hr.fer.srs.commands;

import hr.fer.srs.PasswordManager;
import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.PasswordManagerException;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static hr.fer.srs.elements.CryptoUtils.padPair;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class PutCommand {
    private static final int MAX_ELEMENT_BYTE_SIZE = 256;
    private static final int MAX_PAIR_BYTE_SIZE = MAX_ELEMENT_BYTE_SIZE * 2 + CryptoUtils.DELIMITER_CHARACTER_BYTE_SIZE;
    private static final int MAX_PADDED_PAIR_BYTE_SIZE = MAX_PAIR_BYTE_SIZE + 128;

    private static PutCommand putInstance = null;

    private PutCommand() {}

    public static PutCommand getInstance() {
        if (putInstance == null) putInstance = new PutCommand();
        return putInstance;
    }

    public void put(String masterPasswordRaw, String domainRaw, String passwordRaw) {
        requireNonNull(masterPasswordRaw, "The given master password cannot be null!");
        requireNonNull(domainRaw, "The given domain cannot be null!");
        requireNonNull(passwordRaw, "The given password cannot be null!");

        if (domainRaw.length() > MAX_ELEMENT_BYTE_SIZE)
            throw new IllegalArgumentException("Given a domain that is larger than 256 characters!");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(domainRaw))
            throw new IllegalArgumentException("Given non-ASCII characters within the domain!");
        if (passwordRaw.length() > MAX_ELEMENT_BYTE_SIZE)
            throw new IllegalArgumentException("Given a password that is larger than 256 characters!");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(passwordRaw))
            throw new IllegalArgumentException("Given non-ASCII characters within the password!");

        byte[] salt = CryptoUtils.generateSaltOrIV(CryptoUtils.SALT_LENGTH);
        SecretKey derivedKey = CryptoUtils.deriveAESKeyFromMasterPassword(masterPasswordRaw.toCharArray(), salt);
        byte[] IV = CryptoUtils.generateSaltOrIV(CryptoUtils.IV_LENGTH);

        String pairToBeEncrypted = domainRaw + CryptoUtils.DELIMITER_CHARACTER + passwordRaw;
        pairToBeEncrypted = padPair(MAX_PADDED_PAIR_BYTE_SIZE, pairToBeEncrypted);

        byte[] encryptedCiphertextWithIV = CryptoUtils.AEEncrypt((pairToBeEncrypted).getBytes(UTF_8), derivedKey, IV);
        String newLine = HexUtils.bytetohex(salt) + HexUtils.bytetohex(encryptedCiphertextWithIV);

        addLineToManager(newLine, masterPasswordRaw, domainRaw);
        System.out.print("Stored password for \"" + domainRaw + "\".");
    }

    private void addLineToManager(String newLine, String masterPasswordRaw, String domainRaw) {
        requireNonNull(newLine);
        requireNonNull(masterPasswordRaw);
        requireNonNull(domainRaw);

        String existingDomainLine = null;
        try (BufferedReader br = new BufferedReader(new FileReader(PasswordManager.PATH_STRING))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] rawDecryptedValues = CryptoUtils.retrieveRawDecryptedPair(masterPasswordRaw, line);

                if (rawDecryptedValues[0].equals(domainRaw)) {
                    existingDomainLine = line;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PasswordManagerException(e.getMessage());
        }

        String toWrite;
        if (existingDomainLine == null) toWrite = newLine + System.lineSeparator();
        else toWrite = setNewFileContent(newLine, existingDomainLine);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PasswordManager.PATH_STRING, existingDomainLine == null))) {
            bw.write(toWrite);
        } catch (IOException e) {
            throw new PasswordManagerException(e.getMessage());
        }

    }

    private String setNewFileContent(String newLine, String existingDomainLine) {
        requireNonNull(newLine);
        requireNonNull(existingDomainLine);

        try (BufferedReader br = new BufferedReader(new FileReader(PasswordManager.PATH_STRING))) {
            StringBuilder sb = new StringBuilder();

            br.lines().forEach(line -> sb.append(line.equals(existingDomainLine) ? newLine : line).append(System.lineSeparator()));

            return sb.toString();
        } catch (IOException e) {
            throw new PasswordManagerException(e.getMessage());
        }
    }
}
