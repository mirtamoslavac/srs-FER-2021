package hr.fer.srs.elements;

import hr.fer.srs.exceptions.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class FileUtils {
    public static final String PATH_STRING = "database.txt";
    public static final String INTEGRITY_PATH_STRING = "databaseHash.txt";
    public static final String HEX_TRUE = "01";
    public static final String HEX_FALSE = "00";

    public static String generateHexUserRow(String usernameHashHex, String passwordHashHex, String
            saltHex, boolean toBeReset) {
        requireNonNull(usernameHashHex);
        requireNonNull(passwordHashHex);
        requireNonNull(saltHex);

        return usernameHashHex + passwordHashHex + saltHex + (toBeReset ? HEX_TRUE : HEX_FALSE);
    }

    public static void addUserRow(String newLine, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(newLine);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        String toWrite = addNewFileContent(newLine, oldHashedPasswords);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_STRING, false))) {
            bw.write(toWrite);
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    public static void changeUserRow(String usernameHash, String newLine, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(newLine);
        requireNonNull(usernameHash);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        Optional<String> existingUserLine = getUserRow(usernameHash);

        String toWrite;
        if (existingUserLine.isPresent()) {
            toWrite = changeRowInFile(newLine, existingUserLine.get(), oldHashedPasswords);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_STRING, false))) {
                bw.write(toWrite);
            } catch (IOException e) {
                throw new PasswordAuthenticatorException(e.getMessage());
            }
        }
    }

    public static void removeUserRow(String usernameHash, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(usernameHash);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        String toWrite = removeRowInFile(usernameHash, oldHashedPasswords);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_STRING, false))) {
            bw.write(toWrite);
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    public static Optional<String> getUserRow(String usernameHash) {
        requireNonNull(usernameHash);

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STRING))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith(usernameHash)) {
                    return Optional.of(line);
                }
            }
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }

        return Optional.empty();
    }

    public static String[] parseUserRowContent(String userRow) {
        requireNonNull(userRow);

        String usernameHash = userRow.substring(0, CryptoUtils.USERNAME_LENGTH_BYTES * 2);
        int passwordEndingIndex = CryptoUtils.USERNAME_LENGTH_BYTES * 2 + CryptoUtils.PASSWORD_HASH_LENGTH_BYTES * 2;
        String passwordHash = userRow.substring(CryptoUtils.USERNAME_LENGTH_BYTES * 2, passwordEndingIndex);
        int saltEndingIndex = passwordEndingIndex + CryptoUtils.SALT_SIZE_BYTES * 2;
        String salt = userRow.substring(passwordEndingIndex, saltEndingIndex);
        String toBeReset = userRow.substring(saltEndingIndex);

        return new String[]{usernameHash, passwordHash, salt, toBeReset};
    }

    public static void initializeFile() {
        String toWrite = HexUtils.bytetohex(serializeOldHashedPasswords(new HashMap<>())) + "\n";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(PATH_STRING).toString(), false))) {
            bw.write(toWrite);
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }

        storeChecksumOfFile();
    }

    public static void storeChecksumOfFile() {
        String toWrite = HexUtils.bytetohex(HashUtils.calculateDatabaseChecksum(PATH_STRING));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(INTEGRITY_PATH_STRING).toString(), false))) {
            bw.write(toWrite);
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    public static void checkCompromised() {
        try (BufferedReader br = new BufferedReader(new FileReader(INTEGRITY_PATH_STRING))) {
            if (!Arrays.equals(HashUtils.calculateDatabaseChecksum(PATH_STRING), HexUtils.hextobyte(br.readLine())))
                throw new PasswordAuthenticatorCompromisedException();
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    private static String addNewFileContent(String newLine, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(newLine);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        byte[] hashedPasswordsByteArray = serializeOldHashedPasswords(oldHashedPasswords);

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STRING))) {
            StringBuilder sb = new StringBuilder();
            List<String> oldLines = br.lines().collect(Collectors.toList());
            for (int i = 0, numberOfLines = oldLines.size(); i < numberOfLines; i++) {
                if (i == 0)
                    sb.append(HexUtils.bytetohex(hashedPasswordsByteArray)).append(System.lineSeparator());
                else
                    sb.append(oldLines.get(i)).append(System.lineSeparator());
            }

            sb.append(newLine).append(System.lineSeparator());

            return sb.toString();
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    private static String changeRowInFile(String newLine, String existingUserLine, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(newLine);
        requireNonNull(existingUserLine);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        byte[] hashedPasswordsByteArray = serializeOldHashedPasswords(oldHashedPasswords);

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STRING))) {
            StringBuilder sb = new StringBuilder();
            List<String> oldLines = br.lines().collect(Collectors.toList());
            for (int i = 0, numberOfLines = oldLines.size(); i < numberOfLines; i++) {
                if (i == 0)
                    sb.append(HexUtils.bytetohex(hashedPasswordsByteArray)).append(System.lineSeparator());
                else
                    sb.append(oldLines.get(i).equals(existingUserLine) ? newLine : oldLines.get(i)).append(System.lineSeparator());
            }

            return sb.toString();
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    private static String removeRowInFile(String usernameHash, HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(usernameHash);
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        byte[] hashedPasswordsByteArray = serializeOldHashedPasswords(oldHashedPasswords);

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STRING))) {
            StringBuilder sb = new StringBuilder();
            List<String> oldLines = br.lines().collect(Collectors.toList());
            for (int i = 0, numberOfLines = oldLines.size(); i < numberOfLines; i++) {
                if (i == 0)
                    sb.append(HexUtils.bytetohex(hashedPasswordsByteArray)).append(System.lineSeparator());
                else if (!oldLines.get(i).startsWith(usernameHash))
                    sb.append(oldLines.get(i)).append(System.lineSeparator());
            }

            return sb.toString();
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    private static byte[] serializeOldHashedPasswords(HashMap<String, HashSet<String>> oldHashedPasswords) {
        requireNonNull(oldHashedPasswords);
        oldHashedPasswords.values().forEach(Objects::requireNonNull);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(oldHashedPasswords);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashSet<String>> deserializeOldHashedPasswords() {
        byte[] hashedPasswordsByteArray;
        try (BufferedReader br = new BufferedReader(new FileReader(FileUtils.PATH_STRING))) {
            hashedPasswordsByteArray = HexUtils.hextobyte(br.readLine());
        } catch (IOException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(hashedPasswordsByteArray); ObjectInput in = new ObjectInputStream(bis)) {
            return (HashMap<String, HashSet<String>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PasswordAuthenticatorException(e.getMessage());
        }
    }
}
