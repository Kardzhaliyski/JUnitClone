package junit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Assertions {

    public static void assertEquals(long expected, long actual) {
        assertEquals(expected, actual, (String) null);
    }

    public static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionFailedError(expected, actual, message);
        }
    }

    public static void assertEquals(long expected, long actual, Supplier<String> message) {
        assertEquals(expected, actual, message != null ? message.get() : null);
    }

    public static void assertEquals(int expected, int actual) {
        assertEquals(expected, actual, (String) null);
    }

    public static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionFailedError(expected, actual, message);
        }
    }

    public static void assertEquals(int expected, int actual, Supplier<String> message) {
        assertEquals(expected, actual, message != null ? message.get() : null);
    }

    public static void assertEquals(double expected, double actual) {
        assertEquals(expected, actual, (String) null);
    }

    public static void assertEquals(double expected, double actual, String message) {
        if (expected != actual) {
            throw new AssertionFailedError(expected, actual, message);
        }
    }

    public static void assertEquals(double expected, double actual, Supplier<String> message) {
        assertEquals(expected, actual, message != null ? message.get() : null);
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, (String) null);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionFailedError(expected, actual, message);
        }
    }

    public static void assertEquals(Object expected, Object actual, Supplier<String> message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionFailedError(expected, actual, message != null ? message.get() : null);
        }
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, (String) null);
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionFailedError(true, false, message);
        }
    }

    public static void assertTrue(boolean condition, Supplier<String> message) {
        if (!condition) {
            throw new AssertionFailedError(true, false, message != null ? message.get() : null);
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, (String) null);
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionFailedError(false, true, message);
        }
    }

    public static void assertFalse(boolean condition, Supplier<String> message) {
        if (condition) {
            throw new AssertionFailedError(false, true, message != null ? message.get() : null);
        }
    }

    public static void assertNull(Object actual) {
        assertNull(actual, (String) null);
    }

    public static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionFailedError(null, actual, message);
        }
    }

    public static void assertNull(Object actual, Supplier<String> message) {
        if (actual != null) {
            throw new AssertionFailedError(null, actual, message != null ? message.get() : null);
        }
    }

    public static void assertNotNull(Object actual) {
        assertNotNull(actual, (String) null);
    }

    public static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionFailedError("not <null>", null, message);
        }
    }

    public static void assertNotNull(Object actual, Supplier<String> message) {
        if (actual == null) {
            throw new AssertionFailedError("not <null>", null, message != null ? message.get() : null);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Executable executable) {
        assertThrows(expectedType, executable, (String) null);
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Executable executable, String message) {
        try {
            executable.execute();
        } catch (Throwable e) {
            if (e.getClass().equals(expectedType)) {
                return;
            }

            throw new AssertionFailedError("Unexpected exception type thrown, expected: <"
                    + expectedType.getName()
                    + "> but was: <" + e.getClass().getName() + ">");
        }

        throw new AssertionFailedError("Expected " + expectedType.getName() + " to be thrown, but nothing was thrown.");
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Executable executable, Supplier<String> message) {
        assertThrows(expectedType, executable, message != null ? message.get() : null);
    }

    public static void assertTimeout(Duration timeout, Executable executable) throws Throwable {
        assertTimeout(timeout, executable, (String) null);
    }

    public static void assertTimeout(Duration timeout, Executable executable, String message) throws Throwable {
        long start = System.currentTimeMillis();
        long timeoutMs = timeout.toMillis();
        long expectedEnd = start + timeout.toMillis();
        executable.execute();
        long end = System.currentTimeMillis();

        if (expectedEnd >= end) {
            return;
        }

        long difference = end - expectedEnd;
        String msg = String.format("%s execution exceeded timeout of %d ms by %d ms",
                message == null ? "" : message + " ==> ",
                timeoutMs,
                difference - timeoutMs);
        throw new AssertionFailedError(msg);
    }

    public static void assertTimeout(Duration timeout, Executable executable, Supplier<String> message) throws Throwable {
        assertTimeout(timeout, executable, message != null ? message.get() : null);
    }

    public static <T> void assertTimeout(Duration timeout, ThrowingSupplier<T> supplier) throws Throwable {
        assertTimeout(timeout, supplier, (String) null);
    }

    public static <T> void assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, String message) throws Throwable {
        assertTimeout(timeout, () -> {
            supplier.get();
        }, message);
    }

    public static <T> void assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, Supplier<String> message) throws Throwable {
        assertTimeout(timeout, supplier, message != null ? message.get() : null);
    }

    public static void assertTimeoutPreemptively(Duration timeout, Executable executable) throws ExecutionException, InterruptedException {
        assertTimeoutPreemptively(timeout, executable, (String) null);
    }

    public static void assertTimeoutPreemptively(Duration timeout, Executable executable, String message) throws ExecutionException, InterruptedException {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            Future<Boolean> future = executorService.submit(() -> {
                try {
                    executable.execute();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }, true);

            future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
        } catch (TimeoutException e) {
            throw new AssertionFailedError(String.format("execution timed out after %d ms", timeout.toMillis()));
        }

    }

    public static void assertTimeoutPreemptively(Duration timeout, Executable executable, Supplier<String> message) throws ExecutionException, InterruptedException {
        assertTimeoutPreemptively(timeout, executable, message != null ? message.get() : null);
    }

    public static <T> void assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier) throws ExecutionException, InterruptedException {
        assertTimeoutPreemptively(timeout, supplier, (String) null);
    }
    public static <T> void assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier, String message) throws ExecutionException, InterruptedException {
        assertTimeoutPreemptively(timeout, () -> {
            supplier.get();
        }, message);
    }

    public static <T> void assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier, Supplier<String> message) throws ExecutionException, InterruptedException {
        assertTimeoutPreemptively(timeout, supplier, message != null ? message.get() : null);
    }

    public static void assertAll(Executable... executables) {
        assertAll(null, executables);
    }

    public static void assertAll(String heading, Executable... executables) {
        List<Throwable> throwableList = new ArrayList<>();
        for (Executable executable : executables) {
            try {
                executable.execute();
            } catch (Throwable e) {
                throwableList.add(e);
            }
        }

        if(throwableList.size() == 0) {
            return;
        }

        throw new MultipleFailuresError(heading, throwableList);
    }
}