package hr.fer.srs.commands;

import hr.fer.srs.PasswordManager;
import hr.fer.srs.exceptions.PasswordManagerException;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import static java.util.Objects.requireNonNull;

public class InitCommand {
    private static InitCommand initInstance = null;

    private InitCommand() {}

    public static InitCommand getInstance() {
        if (initInstance == null) initInstance = new InitCommand();
        return initInstance;
    }

    public void initialize() {
        Path path = Paths.get(PasswordManager.PATH_STRING);
        if (Files.exists(path)) checkOverwrite(path);
        else createNewFile(path);
    }

    private void createNewFile(Path path) {
        try {
            new FileWriter(requireNonNull(path).toString(), false);
            System.out.print("Password manager initialized.");
        } catch (IOException e) {
            throw new PasswordManagerException(e.getMessage());
        }
    }

    private void checkOverwrite(Path path) {
        requireNonNull(path);

        System.out.print("A password manager has already been created. ");
        Scanner in = new Scanner(System.in);

        int invalidResponseCounter = 0;
        inputCheck:
        while (invalidResponseCounter < 3) {
            System.out.print("Would you like to overwrite the old password manager with a new one? Write y(es)/n(o): ");
            String response = in.nextLine().trim();
            switch (response.toLowerCase()) {
                case "y", "yes" -> {
                    createNewFile(path);
                    return;
                }
                case "n", "no" -> {
                    break inputCheck;
                }
                default -> System.out.println("(" + ++invalidResponseCounter + "/3) Invalid response! Expected y/yes/n/no, got \"" + response + "\"!");
            }
        }
        System.out.println("No new password manager initialized!");
    }
}
