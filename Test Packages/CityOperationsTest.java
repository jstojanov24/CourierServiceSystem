// 
// Decompiled by Procyon v0.5.36
// 


import java.util.Random;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;

public class CityOperationsTest
{
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }
    
    @Test
    public void insertCity_OnlyOne() {
        final String name = "Tokyo";
        final String postalCode = "100";
        final int rowId = this.cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(1L, this.cityOperations.getAllCities().size());
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowId));
    }
    
    @Test
    public void insertCity_TwoCities_SameBothNameAndPostalCode() {
        final String name = "Tokyo";
        final String postalCode = "100";
        final int rowIdValid = this.cityOperations.insertCity(name, postalCode);
        final int rowIdInvalid = this.cityOperations.insertCity(name, postalCode);
        Assert.assertEquals(-1L, rowIdInvalid);
        Assert.assertEquals(1L, this.cityOperations.getAllCities().size());
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowIdValid));
    }
    
    @Test
    public void insertCity_TwoCities_SameName() {
        final String name = "Tokyo";
        final String postalCode1 = "100";
        final String postalCode2 = "1020";
        final int rowId1 = this.cityOperations.insertCity(name, postalCode1);
        final int rowId2 = this.cityOperations.insertCity(name, postalCode2);
        Assert.assertEquals(2L, this.cityOperations.getAllCities().size());
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowId1));
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowId2));
    }
    
    @Test
    public void insertCity_TwoCities_SamePostalCode() {
        final String name1 = "Tokyo";
        final String name2 = "Beijing";
        final String postalCode = "100";
        final int rowIdValid = this.cityOperations.insertCity(name1, postalCode);
        final int rowIdInvalid = this.cityOperations.insertCity(name2, postalCode);
        Assert.assertEquals(-1L, rowIdInvalid);
        Assert.assertEquals(1L, this.cityOperations.getAllCities().size());
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowIdValid));
    }
    
    @Test
    public void insertCity_MultipleCities() {
        final String name1 = "Tokyo";
        final String name2 = "Beijing";
        final String postalCode1 = "100";
        final String postalCode2 = "065001";
        final int rowId1 = this.cityOperations.insertCity(name1, postalCode1);
        final int rowId2 = this.cityOperations.insertCity(name2, postalCode2);
        Assert.assertEquals(2L, this.cityOperations.getAllCities().size());
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowId1));
        Assert.assertTrue(this.cityOperations.getAllCities().contains(rowId2));
    }
    
    @Test
    public void deleteCity_WithId_OnlyOne() {
        final String name = "Beijing";
        final String postalCode = "065001";
        final int rowId = this.cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertTrue(this.cityOperations.deleteCity(rowId));
        Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
    }
    
    @Test
    public void deleteCity_WithId_OnlyOne_NotExisting() {
        final Random random = new Random();
        final int rowId = random.nextInt();
        Assert.assertFalse(this.cityOperations.deleteCity(rowId));
        Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
    }
    
    @Test
    public void deleteCity_WithName_One() {
        final String name = "Beijing";
        final String postalCode = "065001";
        final int rowId = this.cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1L, rowId);
        Assert.assertEquals(1L, this.cityOperations.deleteCity(name));
        Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
    }
    
    @Test
    public void deleteCity_WithName_MultipleCities() {
        final String name1 = "Tokyo";
        final String name2 = "Beijing";
        final String postalCode1 = "100";
        final String postalCode2 = "065001";
        final int rowId1 = this.cityOperations.insertCity(name1, postalCode1);
        final int rowId2 = this.cityOperations.insertCity(name2, postalCode2);
        Assert.assertEquals(2L, this.cityOperations.getAllCities().size());
        Assert.assertEquals(2L, this.cityOperations.deleteCity(name1, name2));
    }
    
    @Test
    public void deleteCity_WithName_OnlyOne_NotExisting() {
        final String name = "Tokyo";
        Assert.assertEquals(0L, this.cityOperations.deleteCity(name));
        Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
    }
}
