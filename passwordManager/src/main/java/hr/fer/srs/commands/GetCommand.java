package hr.fer.srs.commands;

import hr.fer.srs.PasswordManager;
import hr.fer.srs.elements.CryptoUtils;
import hr.fer.srs.exceptions.PasswordManagerException;

import java.io.*;

import static java.util.Objects.requireNonNull;

public class GetCommand {
    private static GetCommand getInstance = null;

    private GetCommand() {}

    public static GetCommand getInstance() {
        if (getInstance == null) getInstance = new GetCommand();
        return getInstance;
    }

    public void get(String masterPasswordRaw, String domainRaw) {
        requireNonNull(masterPasswordRaw, "The given master password cannot be null!");
        requireNonNull(domainRaw, "The given domain cannot be null!");

        try(BufferedReader br = new BufferedReader(new FileReader(PasswordManager.PATH_STRING))) {
            String passwordRaw = null;
            for(String line; (line = br.readLine()) != null; ) {
                String[] rawDecryptedValues = CryptoUtils.retrieveRawDecryptedPair(masterPasswordRaw, line);

                if (rawDecryptedValues[0].equals(domainRaw)) {
                    passwordRaw = rawDecryptedValues[1];
                    break;
                }
            }

            if (passwordRaw == null) System.out.print("No password stored for the domain \"" + domainRaw + "\"!");
            else System.out.print("The password for the \"" + domainRaw + "\" domain is: " + passwordRaw);
        } catch (IOException e) {
           throw new PasswordManagerException(e.getMessage());
        }
    }


}
