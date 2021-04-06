package hr.fer.srs;

import hr.fer.srs.commands.*;
import hr.fer.srs.exceptions.PasswordManagerException;

import java.nio.file.*;

public class PasswordManager {

    public static final String PATH_STRING = "passwordManager.txt";
    public static boolean initialized = false;

    public static void main(String[] args) {
        try {
            if (args.length < 1 || args.length > 4) throw new IllegalArgumentException("Invalid number of arguments!");

            checkInitializationStatus();

            switch (args[0]) {
                case "init" -> {
                    if (args.length > 1) throw new IllegalArgumentException("Invalid number of arguments given for initialization!");

                    InitCommand.getInstance().initialize();
                }

                case "put" -> {
                    if (!initialized) throw new IllegalArgumentException("Cannot store a password if the password manager had not yet been initialized!");
                    if (args.length != 4) throw new IllegalArgumentException("Invalid number of arguments given for setting the password!");

                    PutCommand.getInstance().put(args[1], args[2], args[3]);
                }

                case "get" -> {
                    if (!initialized) throw new IllegalArgumentException("Cannot store a password if the password manager had not yet been initialized!");
                    if (args.length != 3) throw new IllegalArgumentException("Invalid number of arguments given for retrieving the password!");
                    GetCommand.getInstance().get(args[1], args[2]);
                }

                default -> throw new IllegalArgumentException("Illegal command " + args[0] + "!");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        } catch (PasswordManagerException e) {
            System.err.println(e.getClass().getSimpleName() + ": The password manager had been compromised!");
        }
    }

    private static void checkInitializationStatus() {
        if (Files.exists(Paths.get(PATH_STRING))) initialized = true;
    }
}
