package hr.fer.srs.exceptions;

import static java.util.Objects.requireNonNull;

public class PasswordAuthenticatorCompromisedException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 7846532135498456558L;

    public PasswordAuthenticatorCompromisedException() {
        super();
    }

    public PasswordAuthenticatorCompromisedException(String message) {
        super(requireNonNull(message));
    }
}
