package hr.fer.srs.commands;

import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.InvalidEntryException;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ForcePasswordChangeCommand implements ICommand {
    @Override
    public void execute(String username) {
        InputUtils.checkUsernameContent(requireNonNull(username));
        String usernameHash = HexUtils.bytetohex(HashUtils.generateUsernameHash(username));

        Optional<String> userRow = FileUtils.getUserRow(usernameHash);
        if (userRow.isEmpty())
            throw new InvalidEntryException("The entered username does not exist. Add it using the \"add\" command.");

        String[] rowContent = FileUtils.parseUserRowContent(userRow.get());

        FileUtils.changeUserRow(usernameHash,
                FileUtils.generateHexUserRow(usernameHash, rowContent[1], rowContent[2], true),
                FileUtils.deserializeOldHashedPasswords());

        System.out.println("User will be requested to change password on next login.");
    }
}
