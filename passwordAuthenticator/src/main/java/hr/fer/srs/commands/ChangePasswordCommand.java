package hr.fer.srs.commands;

import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.InvalidEntryException;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ChangePasswordCommand implements ICommand {
    @Override
    public void execute(String username) {
        InputUtils.checkUsernameContent(requireNonNull(username));
        String usernameHash = HexUtils.bytetohex(HashUtils.generateUsernameHash(username));

        Optional<String> userRow = FileUtils.getUserRow(usernameHash);
        if (userRow.isEmpty())
            throw new InvalidEntryException("The entered username does not exist. Add it using the \"add\" command.");

        try {
            String password = InputUtils.getPassword(true, false);
            byte[] salt = CryptoUtils.generateSalt();

            String[] rowContent = FileUtils.parseUserRowContent(userRow.get());

            HashMap<String, HashSet<String>> oldHashedPasswords = FileUtils.deserializeOldHashedPasswords();
            oldHashedPasswords.get(usernameHash).add(rowContent[1] + rowContent[2]);
            InputUtils.checkReusedPassword(usernameHash, password, oldHashedPasswords);

            FileUtils.changeUserRow(usernameHash,
                    FileUtils.generateHexUserRow(usernameHash,
                            HexUtils.bytetohex(CryptoUtils.generatePasswordHash(password.toCharArray(), salt)),
                            HexUtils.bytetohex(salt),
                            rowContent[3].equals(FileUtils.HEX_TRUE)),
                    oldHashedPasswords);

            System.out.println("Password change successful.");
        } catch (InvalidEntryException e) {
            throw new InvalidEntryException("Password change failed. " + e.getMessage());
        }
    }
}
