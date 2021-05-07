package hr.fer.srs.exceptions;

import static java.util.Objects.requireNonNull;

public class ResetPasswordException extends InvalidEntryException {
    @java.io.Serial
    private static final long serialVersionUID = 7465349867986551369L;

    public ResetPasswordException() {
        super();
    }

    public ResetPasswordException(String message) {
        super(requireNonNull(message));
    }
}
