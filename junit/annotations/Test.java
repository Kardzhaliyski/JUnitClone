package junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    public class NULL_ANNOTATION extends Throwable{
        private NULL_ANNOTATION() {}
    }

    long timeout() default 0L;

    ChronoUnit timeoutUnit() default ChronoUnit.MILLIS;

    Class<? extends Throwable> expectedException() default NULL_ANNOTATION.class;

    String[] dependsOnMethods() default {};
}
