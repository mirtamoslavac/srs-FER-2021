package hr.fer.srs.exceptions;

import static java.util.Objects.requireNonNull;

public class InvalidEntryException extends PasswordAuthenticatorException {
    @java.io.Serial
    private static final long serialVersionUID = 5421867565555688478L;

    public InvalidEntryException() {
        super();
    }

    public InvalidEntryException(String message) {
        super(requireNonNull(message));
    }
}
