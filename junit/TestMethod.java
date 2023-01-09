package junit;

import junit.annotations.DisplayName;
import junit.annotations.RepeatedTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestMethod {
    Method method;
    public boolean invoked = false;
    public boolean successful = false;

    public TestMethod(Method method) {
        this.method = method;
        this.method.setAccessible(true);
    }

    public Object invoke(Object instance, Object... args) throws InvocationTargetException, IllegalAccessException {
        invoked = true;
        Object o = method.invoke(instance, args);
        successful = true;
        return o;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public String getName() {
        return method.getName();
    }


    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return method.getDeclaredAnnotation(annotationClass);
    }


    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }
}
