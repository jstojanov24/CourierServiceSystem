package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new sj160179_AddressOperation(); // Change this to your implementation.
        CityOperations cityOperations = new sj160179_CityOperations(); // Do it for all classes.
        CourierOperations courierOperations = new sj160179_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new sj160179_CourierRequestOperation();
        DriveOperation driveOperation = new sj160179_DriveOperation();
        GeneralOperations generalOperations = new sj160179_GeneralOperations();
        PackageOperations packageOperations = new sj160179_PackageOperations();
        StockroomOperations stockroomOperations = new sj160179_StockroomOperations();
        UserOperations userOperations = new sj160179_UserOperations();
        VehicleOperations vehicleOperations = new sj160179_VehicleOperations();


        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    }
}
