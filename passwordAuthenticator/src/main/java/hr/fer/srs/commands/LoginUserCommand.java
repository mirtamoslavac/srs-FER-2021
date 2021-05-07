package hr.fer.srs.commands;

import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.InvalidEntryException;
import hr.fer.srs.exceptions.ResetPasswordException;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class LoginUserCommand implements ICommand {
    @Override
    public void execute(String username) {
        String usernameHash = HexUtils.bytetohex(HashUtils.generateUsernameHash(requireNonNull(username)));

        try {
            int invalidEntries = 0;
            String password;
            String[] rowContent = null;

            Optional<String> userRow = FileUtils.getUserRow(usernameHash);
            boolean containingUsername = userRow.isPresent();
            if (containingUsername) rowContent = FileUtils.parseUserRowContent(userRow.get());
            while (invalidEntries < 3) {
                try {
                    password = InputUtils.getPassword(false, false);

                    if (!containingUsername) throw new InvalidEntryException();

                    String passwordOldHash = rowContent[1].substring(0, CryptoUtils.PASSWORD_HASH_LENGTH_BYTES * 2);
                    String oldSalt = rowContent[2];

                    if (!HexUtils.bytetohex(CryptoUtils.generatePasswordHash(password.toCharArray(), HexUtils.hextobyte(oldSalt))).equals(passwordOldHash))
                        throw new InvalidEntryException();
                    break;
                } catch (InvalidEntryException e) {
                    invalidEntries++;
                    System.out.println("Username or password incorrect.");
                }
            }
            if (invalidEntries == 3 || rowContent == null) return;

            if (rowContent[3].equals(FileUtils.HEX_TRUE)) {
                try {
                    password = InputUtils.getPassword(true, true);
                } catch (InvalidEntryException e) {
                    throw new ResetPasswordException(e.getMessage());
                }

                HashMap<String, HashSet<String>> oldHashedPasswords = FileUtils.deserializeOldHashedPasswords();
                oldHashedPasswords.get(usernameHash).add(rowContent[1] + rowContent[2]);
                InputUtils.checkReusedPassword(usernameHash, password, oldHashedPasswords);
                byte[] salt = CryptoUtils.generateSalt();

                FileUtils.changeUserRow(usernameHash,
                        FileUtils.generateHexUserRow(usernameHash,
                                HexUtils.bytetohex(CryptoUtils.generatePasswordHash(password.toCharArray(), salt)),
                                HexUtils.bytetohex(salt),
                                false),
                        oldHashedPasswords);
            }

            System.out.println("Login successful.");
        } catch (ResetPasswordException e) {
            throw new InvalidEntryException("Password change failed.");
        } catch (InvalidEntryException e) {
            throw new InvalidEntryException("Username or password incorrect.");
        }
    }
}
