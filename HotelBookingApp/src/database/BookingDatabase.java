package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class BookingDatabase {
	
	   
	   public boolean isRoomAvailable(Connection conn, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
		    String sql = "SELECT * FROM bookings WHERE room_number = ?";
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, roomNumber);
		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                LocalDate bookedIn = LocalDate.parse(rs.getString("check_in"));
		                LocalDate bookedOut = LocalDate.parse(rs.getString("check_out"));
		                // Check overlap
		                if (!(checkOut.isEqual(bookedIn) || checkIn.isEqual(bookedOut) ||
		                      checkOut.isBefore(bookedIn) || checkIn.isAfter(bookedOut))) {
		                    return false; // Conflict
		                }
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return true;
		}
	   

	   public void initDatabase() {
	       try {
	           Class.forName("org.sqlite.JDBC");
	           Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db");
	           Statement stmt = conn.createStatement();

	           
	           stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
	                   "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                   "username TEXT NOT NULL UNIQUE, " +
	                   "password TEXT NOT NULL, " +
	                   "salt TEXT NOT NULL, " +  // Added this line
	                   "name TEXT, " +
	                   "fun_fact TEXT)");

	           // Create rooms table with additional hotel details
	           stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
	                        "room_number INTEGER PRIMARY KEY, " +
	                        "type TEXT NOT NULL, " +
	                        "price REAL NOT NULL, " +
	                        "state TEXT NOT NULL, " +
	                        "hotel_name TEXT NOT NULL, " +
	                        "address TEXT NOT NULL, " +
	                        "phone TEXT NOT NULL)");
	           
	           // Create bookings table
	           stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
	                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                        "username TEXT NOT NULL, " +
	                        "room_number INTEGER NOT NULL, " +
	                        "check_in TEXT NOT NULL, " +
	                        "check_out TEXT NOT NULL, " +
	                        "FOREIGN KEY (username) REFERENCES users(username), " +
	                        "FOREIGN KEY (room_number) REFERENCES rooms(room_number))");
	           
	           System.out.println("Tables created successfully.");
	           insertInitialRooms();
	           conn.close();
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	   }
	  
	   public void insertInitialRooms() {
	       List<Object[]> rooms = Arrays.asList(
	    		   new Object[]{101, "Single", 100.00, "New York", "The Grand Horizon", "123 Broadway, NYC", "(123) 456-7890"},
	    		   new Object[]{102, "Double", 150.00, "California", "Sunset Suites", "456 Sunset Blvd, LA", "(987) 654-3210"},
	    		   new Object[]{103, "Suite", 300.00, "Texas", "The Luxe Stay", "789 Hill St, Dallas", "(555) 123-4567"},
	    		   new Object[]{104, "Single", 110.00, "Florida", "The Oceanview Inn", "123 Beach Rd, Miami", "(321) 765-4321"},
	    		   new Object[]{105, "Double", 160.00, "Nevada", "Golden Sands Resort", "234 Desert Ave, Las Vegas", "(654) 987-6543"},
	    		   new Object[]{106, "Suite", 350.00, "Oregon", "The Emerald Retreat", "890 Forest Rd, Portland", "(666) 432-9876"},
	    		   new Object[]{107, "Single", 120.00, "Washington", "Mountain Peak Resort", "432 Mountain Ave, Seattle", "(333) 555-1234"},
	    		   new Object[]{108, "Double", 170.00, "Colorado", "Crystal Springs Inn", "876 Mountain Rd, Denver", "(444) 678-4321"},
	    		   new Object[]{109, "Suite", 320.00, "Illinois", "Windy City Lodge", "101 Michigan Ave, Chicago", "(222) 111-2222"},
	    		   new Object[]{110, "Single", 140.00, "Georgia", "Peachtree Hotel", "88 Peachtree St, Atlanta", "(777) 888-9999"},
	    		   new Object[]{111, "Double", 180.00, "Arizona", "Desert Bloom Inn", "456 Cactus Rd, Phoenix", "(999) 444-1111"},
	    		   new Object[]{112, "Suite", 360.00, "Massachusetts", "Beacon Hill Inn", "12 Beacon St, Boston", "(617) 345-9876"},
	    		   new Object[]{113, "Single", 105.00, "North Carolina", "Blue Ridge Lodge", "321 Blue Ridge Pkwy, Asheville", "(828) 222-3333"},
	    		   new Object[]{114, "Double", 155.00, "Michigan", "Great Lakes Hotel", "678 Lake Shore Dr, Detroit", "(313) 987-1234"},
	    		   new Object[]{115, "Suite", 380.00, "Pennsylvania", "Liberty Stay", "123 Independence Blvd, Philadelphia", "(215) 555-6789"},
	    		   new Object[]{116, "Single", 125.00, "Ohio", "Buckeye Inn", "456 High St, Columbus", "(614) 444-5566"},
	    		   new Object[]{117, "Double", 165.00, "Minnesota", "Twin Lakes Resort", "789 Lakeside Rd, Minneapolis", "(612) 333-2222"},
	    		   new Object[]{118, "Suite", 395.00, "Tennessee", "Smoky Mountain Lodge", "101 Smoky Rd, Knoxville", "(865) 678-1234"},
	    		   new Object[]{119, "Single", 135.00, "Utah", "Zion Vista Inn", "987 Canyon Rd, Salt Lake City", "(801) 111-2223"},
	    		   new Object[]{120, "Double", 175.00, "Wisconsin", "Cheese Country Hotel", "159 Green Bay Rd, Madison", "(920) 123-4567"}, 
	    		   new Object[]{101, "Single", 100.00, "New York", "The Grand Horizon", "123 Broadway, NYC", "(123) 456-7890"},
	    		   new Object[]{102, "Double", 150.00, "California", "Sunset Suites", "456 Sunset Blvd, LA", "(987) 654-3210"},
	    		   new Object[]{103, "Suite", 300.00, "Texas", "The Luxe Stay", "789 Hill St, Dallas", "(555) 123-4567"},
	    		   new Object[]{104, "Single", 110.00, "Florida", "The Oceanview Inn", "123 Beach Rd, Miami", "(321) 765-4321"},
	    		   new Object[]{105, "Double", 160.00, "Nevada", "Golden Sands Resort", "234 Desert Ave, Las Vegas", "(654) 987-6543"},
	    		   new Object[]{106, "Suite", 350.00, "Oregon", "The Emerald Retreat", "890 Forest Rd, Portland", "(666) 432-9876"},
	    		   new Object[]{107, "Single", 120.00, "Washington", "Mountain Peak Resort", "432 Mountain Ave, Seattle", "(333) 555-1234"},
	    		   new Object[]{108, "Double", 170.00, "Colorado", "Crystal Springs Inn", "876 Mountain Rd, Denver", "(444) 678-4321"},
	    		   new Object[]{109, "Suite", 320.00, "Illinois", "Windy City Lodge", "101 Michigan Ave, Chicago", "(222) 111-2222"},
	    		   new Object[]{110, "Single", 140.00, "Georgia", "Peachtree Hotel", "88 Peachtree St, Atlanta", "(777) 888-9999"},
	    		   new Object[]{111, "Double", 180.00, "Arizona", "Desert Bloom Inn", "456 Cactus Rd, Phoenix", "(999) 444-1111"},
	    		   new Object[]{112, "Suite", 360.00, "Massachusetts", "Beacon Hill Inn", "12 Beacon St, Boston", "(617) 345-9876"},
	    		   new Object[]{113, "Single", 105.00, "North Carolina", "Blue Ridge Lodge", "321 Blue Ridge Pkwy, Asheville", "(828) 222-3333"},
	    		   new Object[]{114, "Double", 155.00, "Michigan", "Great Lakes Hotel", "678 Lake Shore Dr, Detroit", "(313) 987-1234"},
	    		   new Object[]{115, "Suite", 380.00, "Pennsylvania", "Liberty Stay", "123 Independence Blvd, Philadelphia", "(215) 555-6789"},
	    		   new Object[]{116, "Single", 125.00, "Ohio", "Buckeye Inn", "456 High St, Columbus", "(614) 444-5566"},
	    		   new Object[]{117, "Double", 165.00, "Minnesota", "Twin Lakes Resort", "789 Lakeside Rd, Minneapolis", "(612) 333-2222"},
	    		   new Object[]{118, "Suite", 395.00, "Tennessee", "Smoky Mountain Lodge", "101 Smoky Rd, Knoxville", "(865) 678-1234"},
	    		   new Object[]{119, "Single", 135.00, "Utah", "Zion Vista Inn", "987 Canyon Rd, Salt Lake City", "(801) 111-2223"},
	    		   new Object[]{120, "Double", 175.00, "Wisconsin", "Cheese Country Hotel", "159 Green Bay Rd, Madison", "(920) 123-4567"}

	            );


	        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
	            String sql = "INSERT OR IGNORE INTO rooms (room_number, type, price, state, hotel_name, address, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                for (Object[] room : rooms) {
	                    addRoomToBatch(stmt, (int) room[0], (String) room[1], (double) room[2], (String) room[3], (String) room[4], (String) room[5], (String) room[6]);
	                }
	                stmt.executeBatch();
	                System.out.println("Initial rooms added to database.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}

	    private void addRoomToBatch(PreparedStatement stmt, int number, String type, double price, String state, String hotelName, String address, String phone) throws SQLException {
	        stmt.setInt(1, number);
	        stmt.setString(2, type);
	        stmt.setDouble(3, price);
	        stmt.setString(4, state);
	        stmt.setString(5, hotelName);
	        stmt.setString(6, address);
	        stmt.setString(7, phone);
	        stmt.addBatch();
	    }  

}
