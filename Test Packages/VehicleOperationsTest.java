// 
// Decompiled by Procyon v0.5.36
// 


import java.util.Random;
import org.junit.Test;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.GeneralOperations;

public class VehicleOperationsTest
{
    private GeneralOperations generalOperations;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private StockroomOperations stockroomOperations;
    private VehicleOperations vehicleOperations;
    private TestHandler testHandler;
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.stockroomOperations = this.testHandler.getStockroomOperations());
        Assert.assertNotNull(this.vehicleOperations = this.testHandler.getVehicleOperations());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }
    
    int insertStockroom() {
        final String street = "Bulevar kralja Aleksandra";
        final int number = 73;
        final int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, idCity);
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, idAddress);
        final int idStockroom = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, idStockroom);
        Assert.assertEquals(1L, this.stockroomOperations.getAllStockrooms().size());
        return idStockroom;
    }
    
    @Test
    public void insertVehicle() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }
    
    @Test
    public void insertVehicle_UniqueLicencePlateNumber() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertFalse(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }
    
    @Test
    public void deleteVehicles() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
        Assert.assertEquals(1L, this.vehicleOperations.deleteVehicles(licencePlateNumber));
        Assert.assertEquals(0L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }
    
    @Test
    public void parkVehicle() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        final int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }
    
    @Test
    public void parkVehicle_NoVehicle() {
        final String licencePlateNumber = "BG1675DA";
        final int idStockroom = this.insertStockroom();
        Assert.assertFalse(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }
    
    @Test
    public void parkVehicle_NoStockroom() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        final Random random = new Random();
        final int idStockroom = random.nextInt();
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }
    
    @Test
    public void changeFuelType() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
        final int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
    }
    
    @Test
    public void changeConsumption() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
        final int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
    }
    
    @Test
    public void changeCapacity() {
        final String licencePlateNumber = "BG1675DA";
        final BigDecimal fuelConsumption = new BigDecimal(6.3);
        final BigDecimal capacity = new BigDecimal(100.5);
        final int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
        final int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
    }
}
