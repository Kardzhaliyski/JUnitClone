package junit;

public class JUnitCore {
    public static Result runClasses(Class<?>... classes) throws Exception {
        Class<?> clazz = classes[0];
        TestClass testClass = new TestClass(clazz);
        return testClass.test();
    }
}
