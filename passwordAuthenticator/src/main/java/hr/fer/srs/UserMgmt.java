package hr.fer.srs;

import hr.fer.srs.commands.*;
import hr.fer.srs.elements.*;
import hr.fer.srs.exceptions.*;

import java.nio.file.*;

public class UserMgmt {
    public static void main(String[] args) {
        try {
            if (args.length != 2) throw new IllegalArgumentException("Invalid number of arguments.");

            if (!Files.exists(Paths.get(FileUtils.PATH_STRING))) FileUtils.initializeFile();
            FileUtils.checkCompromised();

            ICommand command = switch (args[0]) {
                case "add" -> new AddUserCommand();
                case "passwd" -> new ChangePasswordCommand();
                case "forcepass" -> new ForcePasswordChangeCommand();
                case "del" -> new DeleteUserCommand();
                default -> throw new IllegalArgumentException("Illegal command " + args[0] + ".");
            };
            command.execute(args[1]);

            FileUtils.storeChecksumOfFile();
        } catch (PasswordAuthenticatorCompromisedException e) {
            System.err.println("The password authenticator had been compromised.");
        } catch (IllegalArgumentException | NullPointerException | PasswordAuthenticatorException e) {
            System.err.println(e.getMessage());
        }
    }
}
