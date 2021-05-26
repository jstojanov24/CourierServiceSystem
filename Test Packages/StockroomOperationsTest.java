// 
// Decompiled by Procyon v0.5.36
// 


import java.util.Random;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;

public class StockroomOperationsTest
{
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private StockroomOperations stockroomOperations;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.stockroomOperations = this.testHandler.getStockroomOperations());
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
    
    int insertAddress_SameCity() {
        final String street = "Kraljice Natalije";
        final int number = 37;
        Assert.assertEquals(1L, this.cityOperations.getAllCities().size());
        final int idCity = this.cityOperations.getAllCities().get(0);
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 30, 30);
        Assert.assertNotEquals(-1L, idAddress);
        return idAddress;
    }
    
    int insertAddress_DifferentCity() {
        final String street = "Vojvode Stepe";
        final int number = 73;
        final int idCity = this.cityOperations.insertCity("Nis", "700000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 100, 100);
        Assert.assertNotEquals(-1L, idAddress);
        return idAddress;
    }
    
    @Test
    public void insertStockroom_OnlyOne() {
        final int idAddress = this.insertAddress();
        final int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(1L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }
    
    @Test
    public void insertStockrooms_SameCity() {
        final int idAddress = this.insertAddress();
        final int idAddress2 = this.insertAddress_SameCity();
        final int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(-1L, this.stockroomOperations.insertStockroom(idAddress2));
        Assert.assertEquals(1L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }
    
    @Test
    public void insertStockrooms_DifferentCity() {
        final int idAddress = this.insertAddress();
        final int idAddress2 = this.insertAddress_DifferentCity();
        final int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, rowId);
        final int rowId2 = this.stockroomOperations.insertStockroom(idAddress2);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(2L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId2));
    }
    
    @Test
    public void deleteStockroom() {
        final int idAddress = this.insertAddress();
        final int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(1L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertTrue(this.stockroomOperations.deleteStockroom(rowId));
        Assert.assertEquals(0L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertFalse(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }
    
    @Test
    public void deleteStockroom_NoStockroom() {
        final Random random = new Random();
        final int rowId = random.nextInt();
        Assert.assertFalse(this.stockroomOperations.deleteStockroom(rowId));
        Assert.assertEquals(0L, this.stockroomOperations.getAllStockrooms().size());
    }
    
    @Test
    public void deleteStockroomFromCity() {
        final int idAddress = this.insertAddress();
        final int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(1L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertEquals(1L, this.cityOperations.getAllCities().size());
        final int idCity = this.cityOperations.getAllCities().get(0);
        Assert.assertEquals(rowId, this.stockroomOperations.deleteStockroomFromCity(idCity));
        Assert.assertEquals(0L, this.stockroomOperations.getAllStockrooms().size());
        Assert.assertFalse(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }
    
    @Test
    public void deleteStockroomFromCity_NoCity() {
        final Random random = new Random();
        final int rowId = random.nextInt();
        Assert.assertEquals(-1L, this.stockroomOperations.deleteStockroomFromCity(rowId));
        Assert.assertEquals(0L, this.stockroomOperations.getAllStockrooms().size());
    }
    
    @Test
    public void deleteStockroomFromCity_NoStockroom() {
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        Assert.assertEquals(-1L, this.stockroomOperations.deleteStockroomFromCity(idCity));
        Assert.assertEquals(0L, this.stockroomOperations.getAllStockrooms().size());
    }
}
