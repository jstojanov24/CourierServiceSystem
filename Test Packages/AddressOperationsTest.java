// 
// Decompiled by Procyon v0.5.36
// 



import java.util.Random;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.GeneralOperations;

public class AddressOperationsTest
{
    private GeneralOperations generalOperations;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private TestHandler testHandler;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }
    
    @Test
    public void insertAddress_ExistingCity() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        Assert.assertNotEquals(-1L, idAddress);
        Assert.assertTrue(this.addressOperations.getAllAddresses().contains(idAddress));
    }
    
    @Test
    public void insertAddress_MissingCity() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final Random random = new Random();
        final int idCity = random.nextInt();
        final int idAddress = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        Assert.assertEquals(-1L, idAddress);
        Assert.assertEquals(0L, this.addressOperations.getAllAddresses().size());
    }
    
    @Test
    public void deleteAddress_existing() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        Assert.assertEquals(1L, this.addressOperations.getAllAddresses().size());
        Assert.assertTrue(this.addressOperations.deleteAdress(idAddress));
        Assert.assertEquals(0L, this.addressOperations.getAllAddresses().size());
    }
    
    @Test
    public void deleteAddress_missing() {
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        Assert.assertTrue(this.cityOperations.getAllCities().contains(new Integer(idCity)));
        final Random random = new Random();
        final int idAddress = random.nextInt();
        Assert.assertFalse(this.addressOperations.deleteAdress(idAddress));
    }
    
    @Test
    public void deleteAddresses_multiple_existing() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddressOne = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        final int idAddressTwo = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 100, 100);
        Assert.assertEquals(2L, this.addressOperations.getAllAddresses().size());
        Assert.assertEquals(2L, this.addressOperations.deleteAddresses(streetOne, numberOne));
        Assert.assertEquals(0L, this.addressOperations.getAllAddresses().size());
    }
    
    @Test
    public void deleteAddresses_multiple_missing() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        Assert.assertEquals(0L, this.addressOperations.deleteAddresses(streetOne, numberOne));
        Assert.assertEquals(0L, this.addressOperations.getAllAddresses().size());
    }
    
    @Test
    public void getAllAddressesFromCity() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final String streetTwo = "Kraljice Natalije";
        final int numberTwo = 37;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddressOne = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        final int idAddressTwo = this.addressOperations.insertAddress(streetTwo, numberTwo, idCity, 100, 100);
        Assert.assertNotEquals(-1L, idAddressOne);
        Assert.assertNotEquals(-1L, idAddressTwo);
        Assert.assertEquals(2L, this.addressOperations.getAllAddressesFromCity(idCity).size());
        Assert.assertNull(this.addressOperations.getAllAddressesFromCity(idCity + 1));
        Assert.assertTrue(this.addressOperations.getAllAddressesFromCity(idCity).contains(idAddressOne));
        Assert.assertTrue(this.addressOperations.getAllAddressesFromCity(idCity).contains(idAddressTwo));
    }
    
    @Test
    public void deleteAllAddressesFromCity() {
        final String streetOne = "Bulevar kralja Aleksandra";
        final int numberOne = 73;
        final String streetTwo = "Kraljice Natalije";
        final int numberTwo = 37;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddressOne = this.addressOperations.insertAddress(streetOne, numberOne, idCity, 10, 10);
        final int idAddressTwo = this.addressOperations.insertAddress(streetTwo, numberTwo, idCity, 100, 100);
        Assert.assertNotEquals(-1L, idAddressOne);
        Assert.assertNotEquals(-1L, idAddressTwo);
        Assert.assertEquals(0L, this.addressOperations.deleteAllAddressesFromCity(idCity + 1));
        Assert.assertEquals(2L, this.addressOperations.getAllAddresses().size());
        Assert.assertEquals(2L, this.addressOperations.deleteAllAddressesFromCity(idCity));
        Assert.assertEquals(0L, this.addressOperations.getAllAddresses().size());
    }
}
