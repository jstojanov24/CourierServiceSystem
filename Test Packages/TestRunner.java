// 
// Decompiled by Procyon v0.5.36
// 


import org.junit.runner.Result;
import org.junit.runner.Request;
import org.junit.runner.JUnitCore;

public final class TestRunner
{
    private static final int MAX_POINTS_ON_PUBLIC_TEST = 10;
    private static final Class[] UNIT_TEST_CLASSES;
    private static final Class[] UNIT_TEST_CLASSES_PRIVATE;
    private static final Class[] MODULE_TEST_CLASSES;
    private static final Class[] MODULE_TEST_CLASSES_PRIVATE;
    
    private static double runUnitTestsPublic() {
        int numberOfSuccessfulCases = 0;
        int numberOfAllCases = 0;
        double points = 0.0;
        final JUnitCore jUnitCore = new JUnitCore();
        for (final Class testClass : TestRunner.UNIT_TEST_CLASSES) {
            System.out.println("\n" + testClass.getName());
            final Request request = Request.aClass(testClass);
            final Result result = jUnitCore.run(request);
            numberOfAllCases = result.getRunCount();
            numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }
            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            final double points_curr = numberOfSuccessfulCases * 1.0 / numberOfAllCases;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }
        return points;
    }
    
    private static double runModuleTestsPublic() {
        int numberOfSuccessfulCases = 0;
        int numberOfAllCases = 0;
        double points = 0.0;
        final JUnitCore jUnitCore = new JUnitCore();
        for (final Class testClass : TestRunner.MODULE_TEST_CLASSES) {
            System.out.println("\n" + testClass.getName());
            final Request request = Request.aClass(testClass);
            final Result result = jUnitCore.run(request);
            numberOfAllCases = result.getRunCount();
            numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }
            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            final double points_curr = numberOfSuccessfulCases * 2.0;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }
        return points;
    }
    
    private static double runPublic() {
        double res = 0.0;
        res += runUnitTestsPublic();
        res += runModuleTestsPublic();
        return res;
    }
    
    private static double runUnitTestsPrivate() {
        int numberOfSuccessfulCases = 0;
        int numberOfAllCases = 0;
        double points = 0.0;
        final JUnitCore jUnitCore = new JUnitCore();
        for (final Class testClass : TestRunner.UNIT_TEST_CLASSES_PRIVATE) {
            System.out.println("\n" + testClass.getName());
            final Request request = Request.aClass(testClass);
            final Result result = jUnitCore.run(request);
            numberOfAllCases = result.getRunCount();
            numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }
            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            final double points_curr = numberOfSuccessfulCases * 1.0 / numberOfAllCases;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }
        return points;
    }
    
    private static double runModuleTestsPrivate() {
        int numberOfSuccessfulCases = 0;
        int numberOfAllCases = 0;
        double points = 0.0;
        final JUnitCore jUnitCore = new JUnitCore();
        for (final Class testClass : TestRunner.MODULE_TEST_CLASSES_PRIVATE) {
            System.out.println("\n" + testClass.getName());
            final Request request = Request.aClass(testClass);
            final Result result = jUnitCore.run(request);
            numberOfAllCases = result.getRunCount();
            numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }
            System.out.println("Successful:" + numberOfSuccessfulCases);
            System.out.println("All:" + numberOfAllCases);
            final double points_curr = numberOfSuccessfulCases * 2.0;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }
        return points;
    }
    
    private static double runPrivate() {
        double res = 0.0;
        res += runUnitTestsPrivate();
        res += runModuleTestsPrivate();
        return res;
    }
    
    public static void runTests() {
        final double resultsPublic = runPublic();
        System.out.println("Points won on public tests is: " + resultsPublic + " out of 10.0");
    }
    
    static {
        UNIT_TEST_CLASSES = new Class[] { CityOperationsTest.class, AddressOperationsTest.class, UserOperationsTest.class, CourierRequestOperationTest.class, StockroomOperationsTest.class, VehicleOperationsTest.class };
        UNIT_TEST_CLASSES_PRIVATE = new Class[0];
        MODULE_TEST_CLASSES = new Class[] { PublicModuleTest.class };
        MODULE_TEST_CLASSES_PRIVATE = new Class[0];
    }
}
