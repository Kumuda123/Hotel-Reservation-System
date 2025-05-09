package hotel;

import java.util.HashMap;

import model.Room;
import model.User;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserManager {
    private HashMap<String, User> users = new HashMap<>();
    private User currentUser = null;
    
    private static final String PEPPER = "SuperSecretPepper123!"; // should be stored securely
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom random = new SecureRandom();
    

    private String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            KeySpec spec = new PBEKeySpec((password + PEPPER).toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public boolean register(String username, String password, String name, String funFact) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
            String sql = "INSERT INTO users (username, password, salt, name, fun_fact) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, salt);
                stmt.setString(4, name);
                stmt.setString(5, funFact);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean login(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        String salt = rs.getString("salt");
                        String computedHash = hashPassword(password, salt);

                        if (storedHash.equals(computedHash)) {
                            currentUser = new User(
                                rs.getString("username"),
                                storedHash,
                                rs.getString("name"),
                                rs.getString("fun_fact")
                            );
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getUser(String username) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("fun_fact")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public synchronized boolean storeBooking(String username, Room room, LocalDate checkIn, LocalDate checkOut) {
        String checkSql = "SELECT COUNT(*) FROM bookings " +
                          "WHERE room_number = ? AND " +
                          "(? < check_out AND ? > check_in)";  // Overlapping range

        String insertSql = "INSERT INTO bookings (username, room_number, check_in, check_out) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, room.getRoomNumber());
                checkStmt.setString(2, checkOut.toString());  // end > existing.start
                checkStmt.setString(3, checkIn.toString());   // start < existing.end

                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int conflictCount = rs.getInt(1);

                if (conflictCount > 0) {
                    conn.rollback();
                    return false; // Conflict found, prevent booking
                }

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setInt(2, room.getRoomNumber());
                    insertStmt.setString(3, checkIn.toString());
                    insertStmt.setString(4, checkOut.toString());
                    insertStmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback(); // Rollback on error
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
