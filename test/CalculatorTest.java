package test;

import junit.annotations.*;
import junit.Assertions;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.Assertions.*;

public class CalculatorTest {
    Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() {
        int n1 = 3;
        int n2 = 5;
        long result = calculator.add(n1, n2);

//        assertEquals(78, result);
//        assertNull("test");
//        assertThrows(IllegalArgumentException.class, () -)
        assertEquals(n1, n2);
        assertEquals("0", "");
    }

    @RepeatedTest(5)
    @Test
    void testAdd() {
        int n1 = 3;
        int n2 = 5;
        long result = calculator.add(n1, n2);
        Assertions.assertEquals(new Random().nextInt(2) + 7, result);
//        Assertions.assertEquals(78, result, "Sometin wong");
    }

    @RepeatedTest(-5)
    @Test
    void testNegativeRepeatTestAnnotation() {
    }

    @Test
    void testSubtract() {
        int n1 = 3;
        int n2 = 5;
        long result = calculator.sub(n1, n2);
        assertEquals(-2, result);
//        Assertions.assertEquals(78, result);
    }

    @Test
    void unexpectedException() {
        throw new IllegalStateException();
    }

    @Test
    void wrongException() {
        assertThrows(IllegalStateException.class, () -> {
            throw new IllegalArgumentException();
        });
    }

    @Test
    void expectedException() {
        assertThrows(IllegalStateException.class, () -> {

        });
    }

    @Test
    @DisplayName("Display Name")
    void testSubtractWithDescription() throws Throwable {
        int n1 = 3;
        int n2 = 5;
        long result = calculator.sub(n1, n2);
        assertEquals(-1, result);
//        Assertions.assertEquals(78, result);
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            Object o = null;
        });
    }

    @Test
    void testFailedAssertTimeout() throws Throwable {
        assertTimeout(Duration.ofMillis(35), () -> {
            long result = 0;
            for (int i = 0; i < 1000000; i++) {
                String value = "" + i;
                result = Long.parseLong(value);
            }
            ;
        });
    }

    @Test
    void testFailedAssertTimeoutWithSupplier() throws Throwable {
        assertTimeout(Duration.ofMillis(35), () -> {
            long result = 0;
            for (int i = 0; i < 1000000; i++) {
                String value = "" + i;
                result = Long.parseLong(value);
            }
            ;
            return result;
        });
    }

    @Test
    void testFailedAssertTimeoutPreemptively() throws Throwable {
        assertTimeoutPreemptively(Duration.ofMillis(35), () -> {
            long result = 0;
            for (int i = 0; i < 1000000; i++) {
                String value = "" + i;
                result = Long.parseLong(value);
            }
        }, "");
    }

    @Test
    void testFailedAssertTimeoutPreemptivelyWithSupplier() throws Throwable {
        assertTimeoutPreemptively(Duration.ofMillis(35), () -> {
            long result = 0;
            for (int i = 0; i < 1000000; i++) {
                String value = "" + i;
                result = Long.parseLong(value);
            }
            return result;
        });
    }

    @Test(timeout = 35)
    void testFailedAssertTimeoutWithAnnotation() throws Throwable {
        assertTimeout(Duration.ofMillis(3000), () -> {
            long result = 0;
            for (int i = 0; i < 10000000; i++) {
                String value = "" + i;
                result = Long.parseLong(value);
            }
        });
    }

    @Test(dependsOnMethods = "testDependencyTwo")
    void testDependencyThree() {
        assertTrue(false);
    }

    @Test(dependsOnMethods = "testDependencyOne")
    void testDependencyTwo() {
    }

    @Test(dependsOnMethods = "testDependencyTwo")
    void testDependencyThreeAgain() {
    }

    @Test
    void testDependencyOne() {
    }

    @Test(dependsOnMethods = "testDependencyThree")
    void testDependencyFourExpectedToFail() {
    }

    @Test(expectedException = IllegalStateException.class)
    void testFailedExpectedExceptionAnnotation() {
        throw new IllegalArgumentException();
    }

    void testAssertAll() {
        assertAll(() -> assertEquals(3,3),
//                () -> assertEquals(3,4),
                () -> assertEquals(3,-2));
    }

    @Test
    void testAssertAllWithHeading() {
        assertAll("Some Heading",
                () -> assertEquals(3,3),
                () -> assertEquals(3,4),
                () -> assertEquals(3,-2));
    }

}
