package hr.fer.srs.exceptions;

import static java.util.Objects.requireNonNull;

public class PasswordManagerException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 6135846512313246546L;

    public PasswordManagerException() {
        super();
    }

    public PasswordManagerException(String message) {
        super(requireNonNull(message));
    }
}
