package hr.fer.srs.commands;

import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.InvalidEntryException;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class DeleteUserCommand implements ICommand {
    @Override
    public void execute(String username) {
        InputUtils.checkUsernameContent(requireNonNull(username));
        String usernameHash = HexUtils.bytetohex(HashUtils.generateUsernameHash(username));

        if (FileUtils.getUserRow(usernameHash).isEmpty())
            throw new InvalidEntryException("The entered username does not exist.");

        HashMap<String, HashSet<String>> oldHashedPasswords = FileUtils.deserializeOldHashedPasswords();
        oldHashedPasswords.remove(usernameHash);

        FileUtils.removeUserRow(usernameHash, oldHashedPasswords);
        System.out.println("User successfully removed.");
    }
}
