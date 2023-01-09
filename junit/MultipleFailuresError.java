package junit;

import java.io.PrintStream;
import java.util.List;

public class MultipleFailuresError extends AssertionError {
    private static final String DEFAULT_HEADING = "Multiple Failures";
    List<Throwable> throwables;

    public MultipleFailuresError(List<Throwable> throwables) {
        this(DEFAULT_HEADING, throwables);
    }

    public MultipleFailuresError(String heading, List<Throwable> throwables) {
        super(formatMessage(heading, throwables));
        this.throwables = throwables;
    }

    private static String formatMessage(String heading, List<Throwable> throwables) {
        StringBuilder sb = new StringBuilder();
        sb.append(heading).append("(").append(throwables.size()).append(" failures)");
        for (Throwable throwable : throwables) {
            sb.append(System.lineSeparator()).append("  |  +-- ").append(throwable.getMessage());
        }

        return sb.toString();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        for (Throwable t : throwables) {
            t.printStackTrace(s);
            s.println();
        }
    }
}
