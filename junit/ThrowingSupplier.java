package junit;

public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}