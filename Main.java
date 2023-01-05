import junit.Failure;
import junit.JUnitCore;
import junit.Result;
import test.CalculatorTest;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Result result = JUnitCore.runClasses(CalculatorTest.class);

        List<Failure> failures = result.getFailures();
        System.out.println();
        System.out.printf("Failures (%d):%n", failures.size());
        for (Failure failure : failures) {
            failure.printStackTrace();
        }
    }
}
