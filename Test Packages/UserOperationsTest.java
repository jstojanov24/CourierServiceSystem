// 
// Decompiled by Procyon v0.5.36
// 


import java.util.Random;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.GeneralOperations;

public class UserOperationsTest
{
    private GeneralOperations generalOperations;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private UserOperations userOperations;
    private TestHandler testHandler;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.userOperations = this.testHandler.getUserOperations());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }
    
    int insertAddress() {
        final String street = "Bulevar kralja Aleksandra";
        final int number = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, idAddress);
        Assert.assertEquals(1L, this.addressOperations.getAllAddresses().size());
        return idAddress;
    }
    
    @Test
    public void insertUser_Good() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(1L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void insertUser_UniqueUsername() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(1L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void insertUser_BadFirstname() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void insertUser_BadLastName() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "kisprdilov";
        final String password = "Test_123";
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void insertUser_BadAddress() {
        final Random random = new Random();
        final int idAddress = random.nextInt();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void insertUser_BadPassword() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password1 = "test_123";
        final String password2 = "Test123";
        final String password3 = "Test_test";
        final String password4 = "TEST_123";
        final String password5 = "Test_1";
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password1, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password2, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password3, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password4, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
        Assert.assertFalse(this.userOperations.insertUser(username, firstName, lastName, password5, idAddress));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username));
        Assert.assertEquals(0L, this.userOperations.getAllUsers().size());
    }
    
    @Test
    public void declareAdmin() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.declareAdmin(username));
    }
    
    @Test
    public void declareAdmin_AlreadyAdmin() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.declareAdmin(username));
        Assert.assertFalse(this.userOperations.declareAdmin(username));
    }
    
    @Test
    public void declareAdmin_NoSuchUser() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        final String username2 = "crno.dete.2";
        Assert.assertFalse(this.userOperations.declareAdmin(username2));
    }
    
    @Test
    public void getSentPackages_userExisting() {
        final int idAddress = this.insertAddress();
        final String username = "crno.dete";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertEquals(0L, this.userOperations.getSentPackages(username));
    }
    
    @Test
    public void getSentPackages_userNotExisting() {
        final String username = "crno.dete";
        Assert.assertEquals(-1L, this.userOperations.getSentPackages(username));
    }
    
    @Test
    public void deleteUsers() {
        final int idAddress = this.insertAddress();
        final String username1 = "crno.dete1";
        final String username2 = "crno.dete2";
        final String username3 = "crno.dete3";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username1, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.insertUser(username2, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.insertUser(username3, firstName, lastName, password, idAddress));
        Assert.assertEquals(2L, this.userOperations.deleteUsers(username1, username2));
        Assert.assertEquals(1L, this.userOperations.getAllUsers().size());
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username1));
        Assert.assertFalse(this.userOperations.getAllUsers().contains(username2));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username3));
    }
    
    @Test
    public void getAllUsers() {
        final int idAddress = this.insertAddress();
        final String username1 = "crno.dete1";
        final String username2 = "crno.dete2";
        final String firstName = "Svetislav";
        final String lastName = "Kisprdilov";
        final String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username1, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.insertUser(username2, firstName, lastName, password, idAddress));
        Assert.assertEquals(2L, this.userOperations.getAllUsers().size());
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username1));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username2));
    }
}
