package junit;

import junit.annotations.DisplayName;

import java.lang.reflect.Method;

public class Failure {
    public TestMethod method;
    public Throwable error;

    public Failure(TestMethod method, Throwable e) {
        this.method = method;
        this.error = e;
    }

    @Override
    public String toString() {
        DisplayName dnAnn = method.getDeclaredAnnotation(DisplayName.class);
        return String.format("%s() [X] %s",
                dnAnn != null ? dnAnn.value() : method.getName(),
                error.getMessage());
    }

    public void printStackTrace() {
        System.out.printf("%s:%s()%n",
                method.getDeclaringClass().getSimpleName(), method.getName());
        System.out.printf(" MethodSource [className = '%s', methodName = '%s', methodParameterTypes = '']%n     ",
                method.getDeclaringClass().getName(), method.getName());
        error.printStackTrace(System.out);
    }
}
