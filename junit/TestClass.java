package junit;

import junit.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestClass {
    Class<?> clazz;
    Object instance;

    List<Method> beforeAllMethods = new ArrayList<>();
    List<Method> beforeEachMethods = new ArrayList<>();
    List<Method> testMethods = new ArrayList<>();
    List<Method> afterEachMethods = new ArrayList<>();
    List<Method> afterAllMethods = new ArrayList<>();

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

    public Result test() throws Exception {
        System.out.printf("'-- %s [OK]%n", clazz.getSimpleName());
        Result result = new Result();
        invokeMethods(instance, beforeAllMethods);
        for (Method method : testMethods) {
            RepeatedTest rt = method.getAnnotation(RepeatedTest.class);
            if(rt != null && rt.value() <= 0){
                String message = String.format("Configuration error: @RepeatedTest on method [%s %s.%s()] must be declared with a positive 'value'.",
                        method.getReturnType(), clazz.getName(), method.getName());
                Failure failure = new Failure(method, new IllegalArgumentException(message));
                printNoRepeatResult(method, failure);
                result.addFailure(failure);
                continue;
            }

            int repeats = rt != null ? rt.value() : 1;
            testMethod(result, method, repeats);
        }

        invokeMethods(instance, afterAllMethods);
        return result;
    }

    private void testMethod(Result result, Method method, int repeats) throws InvocationTargetException, IllegalAccessException {
        if(repeats > 1) {
            DisplayName ann = method.getAnnotation(DisplayName.class);
            System.out.printf("  +-- %s() [OK]%n", ann != null ? ann.value() : method.getName());
        }

        for (int i = 1; i <= repeats; i++) {
            invokeMethods(instance, beforeEachMethods);
            Failure failure = null;
            try {
                method.invoke(instance);
            } catch (Exception e) {
                Throwable exception = e.getCause();
                if (exception instanceof AssertionFailedError ex) {
                    failure = new Failure(method, ex);
                } else {
                    failure = new Failure(method, new Exception(exception.getClass().getName(), exception));
                }
            }

            if(repeats == 1) {
                printNoRepeatResult(method, failure);
            } else {
                printRepeatResult(failure, repeats, i);
            }

            if(failure != null) {
                result.addFailure(failure);
            }

            invokeMethods(instance, afterEachMethods);
        }
    }

    private void printRepeatResult(Failure failure, int max, int count) {
        if (failure == null) {
            System.out.printf("  |  +-- repetition %d of %d [OK]%n", count, max);
        } else {
            System.out.printf("  |  +-- repetition %d of %d [X] %s%n", count, max, failure.error.getMessage());
        }
    }

    private static void printNoRepeatResult(Method method, Failure failure) {
        DisplayName ann = method.getAnnotation(DisplayName.class);
        if (failure == null) {
            System.out.printf("  +-- %s() [OK]%n", ann != null ? ann.value() : method.getName());
        } else {
            System.out.printf("  +-- %s() [X] %s%n", ann != null ? ann.value() : method.getName(), failure.error.getMessage());
        }
    }

    private Failure testMethod(Method method) throws InvocationTargetException, IllegalAccessException {
        invokeMethods(instance, beforeEachMethods);
        Failure failure = null;
        try {
            method.invoke(instance);
        } catch (Exception e) {
            Throwable exception = e.getCause();
            if (exception instanceof AssertionFailedError ex) {
                failure = new Failure(method, ex);
            } else {
                failure = new Failure(method, new Exception(exception.getClass().getName(), exception));
            }
        }

        return failure;
    }

    private void extractMethods() {
        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
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
                } else {
                    continue;
                }

                method.setAccessible(true);
            }
        }
    }

    private static void invokeMethods(Object instance, List<Method> beforeAllMethods) throws InvocationTargetException, IllegalAccessException {
        for (Method method : beforeAllMethods) {
            method.invoke(instance);
        }
    }
}
