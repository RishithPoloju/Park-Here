package com.project.rmss.parkhere;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by Rishith on 21-02-2018.
 */

public class DBContract {
    public static final String host = "jdbc:mysql://parkingdb.cvevz0bc7iia.us-east-2.rds.amazonaws.com:3306/parkingdatabase";
    public static final String dbUsername = "Rishith";
    public static final String dbPassword = "8897007981";

    public static final String dbAdminTable = "administrators";
    public static final String dbAdminUname = "db_admin_uname";
    public static final String dbAdminPasswd = "db_admin_passwd";
    public static final String dbAdminId = "db_admin_id";
    public static final String dbAdminContact = "db_admin_contact";
    public static final String dbAdminAge = "db_admin_age";
    public static final String dbAdminFname = "db_admin_fullname";

    public static final String dbStaffTable = "staff";
    public static final String dbStaffUname = "db_staff_uname";
    public static final String dbStaffPasswd = "db_staff_passwd";
    public static final String dbStaffId = "db_staff_id";
    public static final String dbStaffContact = "db_staff_contact";
    public static final String dbStaffAge = "db_staff_age";
    public static final String dbStaffFname = "db_staff_fullname";


    public static final String dbParkingTable = "parking";
    public static final String dbVehicleNumber = "db_vehicle_number";
    public static final String dbVehicleType = "db_vehicle_type";
    public static final String dbVehicleInTimeHours = "db_vehicle_intime_hours";
    public static final String dbVehicleInTimeMinutes = "db_vehicle_intime_minutes";
    public static final String dbVehicleOutTimeHours = "db_vehicle_outtime_hours";
    public static final String dbVehicleOutTimeMinutes = "db_vehicle_outtime_minutes";
    public static final String dbVehicleSlot = "db_vehicle_slot";

    public static final String dbParkingTableDB = "parkingdb";
    public static final String dbVehicleNumberDB = "db_vehicle_number_db";
    public static final String dbVehicleTypeDB = "db_vehicle_type_db";
    public static final String dbVehicleInTimeDB = "db_vehicle_intime_db";
    public static final String dbVehicleOutTimeDB = "db_vehicle_outtime_db";
    public static final String dbVehicleSlotDB = "db_vehicle_slot_db";


    public static Connection connection;
    public static int count;
    public static int slotcount;

}
