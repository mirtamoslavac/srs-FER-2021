package hr.fer.srs.exceptions;

import static java.util.Objects.requireNonNull;

public class PasswordAuthenticatorException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 6135846512313246546L;

    public PasswordAuthenticatorException() {
        super();
    }

    public PasswordAuthenticatorException(String message) {
        super(requireNonNull(message));
    }
}
