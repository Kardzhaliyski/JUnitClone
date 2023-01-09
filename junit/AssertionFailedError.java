package junit;

public class AssertionFailedError extends AssertionError {
    public AssertionFailedError(String message) {
        super(message);
    }

    public AssertionFailedError(Object expected, Object actual, String message) {
        super(formatMessage(expected, actual, message));
    }

    private static String formatMessage(Object expected, Object actual, String message) {
        return String.format("%s expected: <%s> but was: <%s>",
                message == null ? "" : message + " ==> ",
                expected,
                actual);

    }

}
