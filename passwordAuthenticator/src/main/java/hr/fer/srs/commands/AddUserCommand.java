package hr.fer.srs.commands;

import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.InvalidEntryException;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class AddUserCommand implements ICommand {
    @Override
    public void execute(String username) {
        InputUtils.checkUsernameContent(requireNonNull(username));
        String usernameHash = HexUtils.bytetohex(HashUtils.generateUsernameHash(username));

        if (FileUtils.getUserRow(usernameHash).isPresent())
            throw new InvalidEntryException("The entered username already exists. Change its password using the \"passwd\" command.");

        try {
            String password = InputUtils.getPassword(true, false);
            byte[] salt = CryptoUtils.generateSalt();

            HashMap<String, HashSet<String>> oldHashedPasswords = FileUtils.deserializeOldHashedPasswords();
            oldHashedPasswords.put(usernameHash, new HashSet<>());

            FileUtils.addUserRow(FileUtils.generateHexUserRow(usernameHash,
                    HexUtils.bytetohex(CryptoUtils.generatePasswordHash(password.toCharArray(), salt)),
                    HexUtils.bytetohex(salt),
                    false), oldHashedPasswords);

            System.out.println("User successfully added.");
        } catch (InvalidEntryException e) {
            throw new InvalidEntryException("User addition failed. " + e.getMessage());
        }
    }
}
