
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import java.util.HashMap;
import java.math.BigDecimal;
import javafx.util.Pair;
import java.util.Map;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;

public class SecondPublicModuleTest
{
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private UserOperations userOperations;
    private CourierRequestOperation courierRequestOperation;
    private VehicleOperations vehicleOperations;
    private CourierOperations courierOperation;
    private StockroomOperations stockroomOperations;
    private PackageOperations packageOperations;
    private DriveOperation driveOperation;
    private TestHandler testHandler;
    Map<Integer, Pair<Integer, Integer>> addressesCoords;
    Map<Integer, BigDecimal> packagePrice;
    
    public SecondPublicModuleTest() {
        this.addressesCoords = new HashMap<Integer, Pair<Integer, Integer>>();
        this.packagePrice = new HashMap<Integer, BigDecimal>();
    }
    
    @Before
    public void setUp() {
        Assert.assertNotNull(this.testHandler = TestHandler.getInstance());
        Assert.assertNotNull(this.cityOperations = this.testHandler.getCityOperations());
        Assert.assertNotNull(this.addressOperations = this.testHandler.getAddressOperations());
        Assert.assertNotNull(this.userOperations = this.testHandler.getUserOperations());
        Assert.assertNotNull(this.courierRequestOperation = this.testHandler.getCourierRequestOperation());
        Assert.assertNotNull(this.courierOperation = this.testHandler.getCourierOperations());
        Assert.assertNotNull(this.vehicleOperations = this.testHandler.getVehicleOperations());
        Assert.assertNotNull(this.stockroomOperations = this.testHandler.getStockroomOperations());
        Assert.assertNotNull(this.packageOperations = this.testHandler.getPackageOperations());
        Assert.assertNotNull(this.driveOperation = this.testHandler.getDriveOperation());
        Assert.assertNotNull(this.generalOperations = this.testHandler.getGeneralOperations());
        this.generalOperations.eraseAll();
    }
    
    @After
    public void tearUp() {
        //this.testHandler.getGeneralOperations().eraseAll();
    }
    
    int insertCity(final String name, final String postalCode) {
        final int idCity = this.cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1L, idCity);
        Assert.assertTrue(this.cityOperations.getAllCities().contains(idCity));
        return idCity;
    }
    
    int insertAddress(final String street, final int number, final int idCity, final int x, final int y) {
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, x, y);
        Assert.assertNotEquals(-1L, idAddress);
        Assert.assertTrue(this.addressOperations.getAllAddresses().contains(idAddress));
        this.addressesCoords.put(idAddress, (Pair<Integer, Integer>)new Pair((Object)x, (Object)y));
        return idAddress;
    }
    
    String insertUser(final String username, final String firstName, final String lastName, final String password, final int idAddress) {
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }
    
    String insertCourier(final String username, final String firstName, final String lastName, final String password, final int idAddress, final String driverLicenceNumber) {
        this.insertUser(username, firstName, lastName, password, idAddress);
        Assert.assertTrue(this.courierOperation.insertCourier(username, driverLicenceNumber));
        return username;
    }
    
    public void insertAndParkVehicle(final String licencePlateNumber, final BigDecimal fuelConsumption, final BigDecimal capacity, final int fuelType, final int idStockroom) {
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }
    
    public int insertStockroom(final int idAddress) {
        final int stockroomId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, stockroomId);
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(stockroomId));
        return stockroomId;
    }
    
    int insertAndAcceptPackage(final int addressFrom, final int addressTo, final String userName, final int packageType, final BigDecimal weight) {
        final int idPackage = this.packageOperations.insertPackage(addressFrom, addressTo, userName, packageType, weight);
        Assert.assertNotEquals(-1L, idPackage);
        Assert.assertTrue(this.packageOperations.acceptAnOffer(idPackage));
        Assert.assertTrue(this.packageOperations.getAllPackages().contains(idPackage));
        Assert.assertEquals(1L, this.packageOperations.getDeliveryStatus(idPackage));
        final BigDecimal price = Util.getPackagePrice(packageType, weight, Util.getDistance(this.addressesCoords.get(addressFrom), this.addressesCoords.get(addressTo)));
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(1.05))) < 0);
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(0.95))) > 0);
        this.packagePrice.put(idPackage, price);
        System.out.println("cena " + price);
        return idPackage;
    }
    @Test
    public void publicTwo() {
        final int BG = this.insertCity("Belgrade", "11000");
        final int KG = this.insertCity("Kragujevac", "550000");
        final int VA = this.insertCity("Valjevo", "14000");
        final int CA = this.insertCity("Cacak", "32000");
        final int idAddressBG1 = this.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        final int idAddressBG2 = this.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        final int idAddressBG3 = this.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        final int idAddressBG4 = this.insertAddress("Takovska", 7, BG, 11, 12);
        final int idAddressBG5 = this.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        final int idAddressKG1 = this.insertAddress("Daniciceva", 1, KG, 4, 310);
        final int idAddressKG2 = this.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        final int idAddressVA1 = this.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        final int idAddressVA2 = this.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        final int idAddressVA3 = this.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        final int idAddressCA1 = this.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        final int idAddressCA2 = this.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        final int idStockroomBG = this.insertStockroom(idAddressBG1);
        final int idStockroomVA = this.insertStockroom(idAddressVA1);
        this.insertAndParkVehicle("BG1675DA", new BigDecimal(6.3), new BigDecimal(1000.5), 2, idStockroomBG);
        this.insertAndParkVehicle("VA1675DA", new BigDecimal(7.3), new BigDecimal(500.5), 1, idStockroomVA);
        final String username = "crno.dete";
        this.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        final String courierUsernameBG = "postarBG";
        this.insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");
        final String courierUsernameVA = "postarVA";
        this.insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressVA2, "123456");
        final int type = 1;
        final BigDecimal weight = new BigDecimal(4);
        final int idPackage1 = this.insertAndAcceptPackage(idAddressBG2, idAddressKG1, username, type, weight);
        final int idPackage2 = this.insertAndAcceptPackage(idAddressKG2, idAddressBG4, username, type, weight);
        final int idPackage3 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        final int idPackage4 = this.insertAndAcceptPackage(idAddressCA2, idAddressBG4, username, type, weight);
        Assert.assertEquals(0L, this.courierOperation.getCouriersWithStatus(1).size());
        this.driveOperation.planingDrive(courierUsernameBG);
        this.driveOperation.planingDrive(courierUsernameVA);
        Assert.assertEquals(2L, this.courierOperation.getCouriersWithStatus(1).size());
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(idPackage1, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(idPackage3, this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));  //zavrsio je KurirBG
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2)); //u magacinu je paket 2
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(KG).contains(idPackage1));
        Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameVA));    //zavrsio je KurirVA
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage4)); //u magacinu je paket 4
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage4)); //puca,paket nije u magacin ubacen
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(CA).contains(idPackage3));
        final int idPackage5 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        final int idPackage6 = this.insertAndAcceptPackage(idAddressBG3, idAddressVA3, username, type, weight);
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertFalse(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage6));
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage2));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage6));
        Assert.assertEquals(idPackage2, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(idPackage6, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertEquals(0L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage6));
        Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, this.packageOperations.getAllUndeliveredPackagesFromCity(BG).size());
        Assert.assertEquals(3L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage5));
        this.driveOperation.planingDrive(courierUsernameBG); //da vrati pakete 4,5
        Assert.assertEquals(0L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(idPackage4, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(idPackage5, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, this.packageOperations.getAllUndeliveredPackages().size());
        Assert.assertEquals(2L, this.courierOperation.getCouriersWithStatus(0).size());
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(1).compareTo(new BigDecimal(0)) > 0);
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(5).compareTo(new BigDecimal(0)) > 0);
    }
    
}