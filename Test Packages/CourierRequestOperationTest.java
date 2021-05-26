// 
// Decompiled by Procyon v0.5.36
// 


import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;

public class CourierRequestOperationTest
{
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private UserOperations userOperations;
    private CourierRequestOperation courierRequestOperation;
    private TestHandler testHandler;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.userOperations = this.testHandler.getUserOperations());
        Assert.assertNotNull(this.courierRequestOperation = this.testHandler.getCourierRequestOperation());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }
    
    String insertUser() {
        final String street = "Bulevar kralja Aleksandra";
        final int number = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, idAddress);
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }
    
    String insertUser2() {
        final String street = "Vojvode Stepe";
        final int number = 73;
        final int idCity = this.cityOperations.insertCity("Nis", "70000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 100, 100);
        Assert.assertNotEquals(-1L, idAddress);
        final String username = "crno.dete.2";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }
    
    @Test
    public void insertCourierRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void insertCourierRequest_NoUser() {
        final String username = "crno.dete";
        final String driverLicenceNumber = "1234567";
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void insertCourierRequest_RequestExists() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void insertCourierRequest_AlreadyCourier() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void grantRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void grantRequest_NoRequest() {
        final String username = "crno.dete";
        final String driverLicenceNumber = "1234567";
        Assert.assertFalse(this.courierRequestOperation.grantRequest(username));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void insertCourierRequest_multipleDifferentLicence() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        final String username2 = this.insertUser2();
        final String driverLicenceNumber2 = "1234561";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username2, driverLicenceNumber2));
        Assert.assertEquals(2L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username2));
    }
    
    @Test
    public void insertCourierRequest_multipleSameLicence() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        final String username2 = this.insertUser2();
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username2, driverLicenceNumber));
        Assert.assertEquals(1L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username2));
    }
    
    @Test
    public void deleteCourierRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertTrue(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void deleteCourierRequest_NoRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertFalse(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertEquals(0L, this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }
    
    @Test
    public void changeLicenceInCourierRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        final String newDriverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username, newDriverLicenceNumber));
    }
    
    @Test
    public void changeLicenceInCourierRequest_NoUser() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        final String newDriverLicenceNumber = "1234567";
        final String username2 = "crno.dete.2";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username2, newDriverLicenceNumber));
    }
    
    @Test
    public void changeLicenceInCourierRequest_NoRequest() {
        final String username = this.insertUser();
        final String driverLicenceNumber = "1234567";
        final String newDriverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertFalse(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username, newDriverLicenceNumber));
    }
}
