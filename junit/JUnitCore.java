package junit;

public class JUnitCore {
    public static Result runClasses(Class<?>... classes) throws Exception {
        Result result = new Result();
        for (Class<?> clazz : classes) {
            TestClass testClass = new TestClass(clazz);
            testClass.test(result);
        }

        return result;
    }
}
