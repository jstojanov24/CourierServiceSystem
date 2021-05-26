// 
// Decompiled by Procyon v0.5.36
// 


import com.sun.istack.internal.NotNull;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.sj160179_AddressOperation;
import rs.etf.sab.student.sj160179_CityOperations;
import rs.etf.sab.student.sj160179_CourierOperations;
import rs.etf.sab.student.sj160179_CourierRequestOperation;
import rs.etf.sab.student.sj160179_DriveOperation;
import rs.etf.sab.student.sj160179_GeneralOperations;
import rs.etf.sab.student.sj160179_PackageOperations;
import rs.etf.sab.student.sj160179_StockroomOperations;
import rs.etf.sab.student.sj160179_UserOperations;
import rs.etf.sab.student.sj160179_VehicleOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.AddressOperations;

public class TestHandler
{
    private static TestHandler testHandler = new TestHandler(new sj160179_AddressOperation(), new sj160179_CityOperations(), new sj160179_CourierOperations(),
    		new sj160179_CourierRequestOperation(), new sj160179_DriveOperation(), new sj160179_GeneralOperations(), new sj160179_PackageOperations(), new sj160179_StockroomOperations(), new sj160179_UserOperations(), new sj160179_VehicleOperations());
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private CourierOperations courierOperations;
    private CourierRequestOperation courierRequestOperation;
    private DriveOperation driveOperation;
    private GeneralOperations generalOperations;
    private PackageOperations packageOperations;
    private StockroomOperations stockroomOperations;
    private UserOperations userOperations;
    private VehicleOperations vehicleOperations;
    
    private TestHandler(@NotNull final AddressOperations addressOperations, @NotNull final CityOperations cityOperations, @NotNull final CourierOperations courierOperations, @NotNull final CourierRequestOperation courierRequestOperation, @NotNull final DriveOperation driveOperation, @NotNull final GeneralOperations generalOperations, @NotNull final PackageOperations packageOperations, @NotNull final StockroomOperations stockroomOperations, @NotNull final UserOperations userOperations, @NotNull final VehicleOperations vehicleOperations) {
        this.addressOperations = addressOperations;
        this.cityOperations = cityOperations;
        this.courierOperations = courierOperations;
        this.courierRequestOperation = courierRequestOperation;
        this.driveOperation = driveOperation;
        this.generalOperations = generalOperations;
        this.packageOperations = packageOperations;
        this.stockroomOperations = stockroomOperations;
        this.userOperations = userOperations;
        this.vehicleOperations = vehicleOperations;
    }
    
    public static void createInstance(@NotNull final AddressOperations addressOperations, @NotNull final CityOperations cityOperations, @NotNull final CourierOperations courierOperations, @NotNull final CourierRequestOperation courierRequestOperation, @NotNull final DriveOperation driveOperation, @NotNull final GeneralOperations generalOperations, @NotNull final PackageOperations packageOperations, @NotNull final StockroomOperations stockroomOperations, @NotNull final UserOperations userOperations, @NotNull final VehicleOperations vehicleOperations) {
       // TestHandler.testHandler = new TestHandler(addressOperations, cityOperations, courierOperations, courierRequestOperation, driveOperation, generalOperations, packageOperations, stockroomOperations, userOperations, vehicleOperations);
    }
    
    static TestHandler getInstance() {
        return TestHandler.testHandler;
    }
    
    public AddressOperations getAddressOperations() {
        return this.addressOperations;
    }
    
    public CityOperations getCityOperations() {
        return this.cityOperations;
    }
    
    public CourierOperations getCourierOperations() {
        return this.courierOperations;
    }
    
    public CourierRequestOperation getCourierRequestOperation() {
        return this.courierRequestOperation;
    }
    
    public DriveOperation getDriveOperation() {
        return this.driveOperation;
    }
    
    public GeneralOperations getGeneralOperations() {
        return this.generalOperations;
    }
    
    public PackageOperations getPackageOperations() {
        return this.packageOperations;
    }
    
    public StockroomOperations getStockroomOperations() {
        return this.stockroomOperations;
    }
    
    public UserOperations getUserOperations() {
        return this.userOperations;
    }
    
    public VehicleOperations getVehicleOperations() {
        return this.vehicleOperations;
    }
    
    /*
    static {
        TestHandler.testHandler = null;
    }
    */
}
