package junit;

import junit.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestClass {
    Class<?> clazz;
    Object instance;
    Map<String, TestMethod> allMethods = new HashMap<>();
    List<TestMethod> beforeAllMethods = new ArrayList<>();
    List<TestMethod> beforeEachMethods = new ArrayList<>();
    List<TestMethod> testMethods = new ArrayList<>();
    List<TestMethod> afterEachMethods = new ArrayList<>();
    List<TestMethod> afterAllMethods = new ArrayList<>();

    public TestClass(Class<?> clazz) {
        this.clazz = clazz;
        this.instance = initClass(clazz);
        extractMethods();
    }

    private Object initClass(Class<?> clazz) {
        try {
            return instance = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException("TestClass should have empty public constructor.");//todo check original message
        }
    }

    public Result test(Result result) throws Exception {
        System.out.printf("'-- %s [OK]%n", clazz.getSimpleName());

        invokeMethods(instance, beforeAllMethods);
        for (TestMethod method : testMethods) {
            test(result, method);
        }

        invokeMethods(instance, afterAllMethods);
        return result;
    }

    private void test(Result result, TestMethod method) throws InvocationTargetException, IllegalAccessException {
        if (method.invoked) {
            return;
        }

        RepeatedTest rt = method.getAnnotation(RepeatedTest.class);
        if (!validateRepeatAnnotation(result, method, rt)) {
            return;
        }

        if (!invokeDependantMethods(result, method)) {
            return;
        }

        int repeats = rt != null ? rt.value() : 1;
        if (repeats > 1) {
            DisplayName ann = method.getAnnotation(DisplayName.class);
            System.out.printf("  +-- %s() [OK]%n", ann != null ? ann.value() : method.getName());
        }

        for (int i = 1; i <= repeats; i++) {
            invokeMethods(instance, beforeEachMethods);
            Failure failure = invokeTestMethod(method);
            if (repeats == 1) {
                printNoRepeatResult(method, failure);
            } else {
                printRepeatResult(failure, repeats, i);
            }

            if (failure != null) {
                result.addFailure(failure);
            }

            invokeMethods(instance, afterEachMethods);
        }
    }

    private Failure invokeTestMethod(TestMethod method) {
        Failure failure = null;
        Test testAnn = method.getAnnotation(Test.class);
        try {
            if (testAnn.timeout() > 0) {
                Duration duration = Duration.of(testAnn.timeout(), testAnn.timeoutUnit());
                Assertions.assertTimeoutPreemptively(duration, () -> method.invoke(instance));
            } else {
                method.invoke(instance);
            }
        } catch (Throwable e) {
            Throwable exception = e instanceof InvocationTargetException ? e.getCause() : e;
            if (exception instanceof AssertionFailedError || exception instanceof MultipleFailuresError) {
                return failure = new Failure(method, exception);
            }

            if (testAnn.expectedException() != Test.NULL_ANNOTATION.class) {
                if(testAnn.expectedException() != exception.getClass()) {
                    return new Failure(
                            method,
                            new AssertionFailedError(testAnn.expectedException(), exception.getClass(), null));
                }

                return null;
            }

            return new Failure(
                    method,
                    new Exception(exception.getClass().getName(), exception));
        }

        return null;
    }

    private boolean invokeDependantMethods(Result result, TestMethod method) throws InvocationTargetException, IllegalAccessException {
        Test testAnn = method.getAnnotation(Test.class);
        for (String mName : testAnn.dependsOnMethods()) {
            TestMethod depMethod = allMethods.get(mName);
            if (depMethod == null) {
                Failure failure = new Failure(method, new IllegalArgumentException("No depending method found with name: " + mName));
                result.addFailure(failure);
                printNoRepeatResult(method, failure);
                return false;
            }

            if (!depMethod.invoked) {
                test(result, depMethod);
            }

            if (!depMethod.successful) {
                Failure failure = new Failure(method, new IllegalStateException("Not successful depending method: " + depMethod.getName()));
                result.addFailure(failure);
                printNoRepeatResult(method, failure);
                return false;
            }
        }

        return true;
    }

    private static boolean validateRepeatAnnotation(Result result, TestMethod method, RepeatedTest rt) {
        boolean isValid = (rt == null || rt.value() > 0);
        if (isValid) {
            return true;
        }

        String message = String.format("Configuration error: @RepeatedTest on method [%s %s.%s()] must be declared with a positive 'value'.",
                method.getReturnType(), method.getDeclaringClass().getName(), method.getName());
        Failure failure = new Failure(method, new IllegalArgumentException(message));
        printNoRepeatResult(method, failure);
        result.addFailure(failure);
        return isValid;
    }

    private void printRepeatResult(Failure failure, int max, int count) {
        if (failure == null) {
            System.out.printf("  |  +-- repetition %d of %d [OK]%n", count, max);
        } else {
            System.out.printf("  |  +-- repetition %d of %d [X] %s%n", count, max, failure.error.getMessage());
        }
    }

    private static void printNoRepeatResult(TestMethod method, Failure failure) {
        DisplayName ann = method.getAnnotation(DisplayName.class);
        if (failure == null) {
            System.out.printf("  +-- %s() [OK]%n", ann != null ? ann.value() : method.getName());
        } else {
            System.out.printf("  +-- %s() [X] %s%n", ann != null ? ann.value() : method.getName(), failure.error.getMessage());
        }
    }

    private void extractMethods() {
        for (Method m : clazz.getDeclaredMethods()) {
            TestMethod method = new TestMethod(m);
            allMethods.put(method.getName(), method);
            for (Annotation annotation : m.getDeclaredAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                if (type.equals(BeforeAll.class)) {
                    beforeAllMethods.add(method);
                } else if (type.equals(BeforeEach.class)) {
                    beforeEachMethods.add(method);
                } else if (type.equals(AfterEach.class)) {
                    afterEachMethods.add(method);
                } else if (type.equals(AfterAll.class)) {
                    afterAllMethods.add(method);
                } else if (type.equals(Test.class)) {
                    testMethods.add(method);
                }
            }
        }
    }

    private static void invokeMethods(Object instance, List<TestMethod> beforeAllMethods) throws InvocationTargetException, IllegalAccessException {
        for (TestMethod method : beforeAllMethods) {
            method.invoke(instance);
        }
    }

}
