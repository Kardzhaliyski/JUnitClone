package junit;

import java.util.ArrayList;
import java.util.List;

public class Result {
    List<Failure> failures = null;

    public boolean wasSuccessful() {
        return failures == null;
    }

    void addFailure(Failure failure) {
        if (failures == null) {
            failures = new ArrayList<>();
        }

        failures.add(failure);
    }

    public List<Failure> getFailures() {
        return failures;
    }
}
