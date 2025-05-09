package hotel;
import javax.swing.*;
import javax.swing.table.*;

import database.BookingDatabase;
import model.Room;
import model.User;
import utils.ButtonEditor;
import utils.ButtonRenderer;
import utils.DeleteButtonEditor;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;



public class HotelBookingApp {
   private JFrame frame;
   private CustomPanel mainPanel;
   private ArrayList<Room> rooms;
   private User currentUser; 
   private UserManager userManager = new UserManager();
   BookingDatabase bookingDatabase = new BookingDatabase(); 

   public HotelBookingApp() {
	   bookingDatabase.initDatabase();  // Ensure the database is initialized first
       initializeRooms();  // Then load the rooms data
       initializeGUI();
       showLoginPanel();
   }

   private void initializeRooms() {
       rooms = new ArrayList<>();
       Map<Integer, Room> roomMap = new HashMap<>();
       try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
           // Step 1: Load all rooms and populate list + map
           String roomSql = "SELECT * FROM rooms";
           try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(roomSql)) {
               while (rs.next()) {
                   int roomNumber = rs.getInt("room_number");
                   String type = rs.getString("type");
                   double price = rs.getDouble("price");
                   String state = rs.getString("state");
                   String hotelName = rs.getString("hotel_name");
                   String hotelAddress = rs.getString("address");
                   String phone = rs.getString("phone");
                   Room room = new Room(roomNumber, type, price, state, hotelName, hotelAddress, phone);
                   rooms.add(room);
                   roomMap.put(roomNumber, room);
               }
           }
           // Step 2: Load all bookings and associate with room
           String bookingSql = "SELECT * FROM bookings";
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }
   
   private void initializeGUI() {
	    frame = new JFrame("Hotel Booking System");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(800, 500);
	    frame.setLayout(new BorderLayout());

	    // Title
	    JPanel titlePanel = new JPanel(new BorderLayout());
	    titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
	    JLabel titleLabel = new JLabel("Hotel Booking System", SwingConstants.CENTER);
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
	    titlePanel.add(titleLabel, BorderLayout.CENTER);
	    frame.add(titlePanel, BorderLayout.NORTH);

	    // Main panel
	    mainPanel = new CustomPanel();
	    frame.add(mainPanel, BorderLayout.CENTER);

	    // Buttons at the bottom
	    JPanel buttonPanel = new JPanel();
	    JButton homeButton = new JButton("Search Rooms");
	    JButton myBookingsButton = new JButton("My Bookings");
	    JButton viewProfileButton = new JButton("View Profile"); // NEW BUTTON
	    JButton logoutButton = new JButton("Logout");

	    buttonPanel.add(homeButton);
	    buttonPanel.add(myBookingsButton);
	    buttonPanel.add(viewProfileButton); // Add to panel
	    buttonPanel.add(logoutButton);
	    frame.add(buttonPanel, BorderLayout.SOUTH);

	    // Event listeners
	    homeButton.addActionListener(e -> showSearchPanel());
	    myBookingsButton.addActionListener(e -> showMyBookings());
	    viewProfileButton.addActionListener(e -> showUserProfile()); // NEW ACTION
	    logoutButton.addActionListener(e -> {
	        userManager.logout();
	        JOptionPane.showMessageDialog(frame, "Logged out successfully.");
	        showLoginPanel();
	    });

	    frame.setVisible(true);
	}

   private void showUserProfile() {
	    mainPanel.removeAll();
	    mainPanel.setLayout(new GridBagLayout()); // Center content
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.insets = new Insets(20, 20, 20, 20); // Add padding around elements

	    User currentUser = userManager.getCurrentUser();

	    if (currentUser != null) {
	        JPanel profileCard = new JPanel();
	        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
	        profileCard.setPreferredSize(new Dimension(350, 300));
	        profileCard.setBackground(Color.WHITE);
	        profileCard.setAlignmentX(Component.CENTER_ALIGNMENT);
	        profileCard.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
	            BorderFactory.createEmptyBorder(25, 30, 25, 30)
	        ));

	        JLabel heading = createLabel("👤 User Profile", Font.BOLD, 24);
	        JLabel usernameLabel = createLabel("Username: " + currentUser.getUsername(), Font.PLAIN, 18);
	        JLabel nameLabel = createLabel("Name: " + currentUser.getName(), Font.PLAIN, 18);
	        JLabel funFactLabel = createLabel("<html><div style='text-align: center;'>Fun Fact: " + currentUser.getFunFact() + "</div></html>", Font.ITALIC, 16);

	        profileCard.add(heading);
	        profileCard.add(Box.createVerticalStrut(20));
	        profileCard.add(usernameLabel);
	        profileCard.add(Box.createVerticalStrut(10));
	        profileCard.add(nameLabel);
	        profileCard.add(Box.createVerticalStrut(10));
	        profileCard.add(funFactLabel);
	        profileCard.add(Box.createVerticalGlue());

	        JButton backButton = new JButton("Back");
	        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	        backButton.addActionListener(e -> showSearchPanel()); // or main menu

	        profileCard.add(Box.createVerticalStrut(20));
	        profileCard.add(backButton);

	        mainPanel.add(profileCard, gbc);
	    } else {
	        // No user logged in
	        JPanel noUserPanel = new JPanel();
	        noUserPanel.setLayout(new BoxLayout(noUserPanel, BoxLayout.Y_AXIS));
	        noUserPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

	        JLabel noUserLabel = createLabel("No user is currently logged in.", Font.PLAIN, 18);
	        JButton loginButton = new JButton("Go to Login Page");

	        loginButton.addActionListener(e -> showLoginPanel()); // Button action to go to login page

	        noUserPanel.add(noUserLabel);
	        noUserPanel.add(Box.createVerticalStrut(20));
	        noUserPanel.add(loginButton);

	        // Add the noUserPanel to the main panel with proper constraints for alignment
	        mainPanel.add(noUserPanel, gbc);
	    }

	    mainPanel.revalidate();
	    mainPanel.repaint();
	}

	private JLabel createLabel(String text, int style, int size) {
	    JLabel label = new JLabel(text);
	    label.setFont(new Font("Arial", style, size));
	    label.setAlignmentX(Component.CENTER_ALIGNMENT);
	    return label;
	}



	private void showMyBookings() {
	    if (!userManager.isLoggedIn()) {
	        JOptionPane.showMessageDialog(mainPanel,
	            "You must be logged in to view your bookings.",
	            "Not Logged In", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    mainPanel.removeAll();
	    mainPanel.setLayout(new BorderLayout());

	    // Include hotel info
	    String[] columnNames = {
	        "ID", "Room Number", "Type", "Price", "State", 
	        "Hotel Name", "Address", "Phone", 
	        "Check‑In", "Check‑Out", "Action"
	    };

	    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
	        public boolean isCellEditable(int row, int col) {
	            return col == 10;  // Only “Action” column editable
	        }
	    };

	    String sql = 
	        "SELECT b.id, b.room_number, b.check_in, b.check_out, " +
	        "r.type, r.price, r.state, r.hotel_name, r.address, r.phone " +
	        "FROM bookings b JOIN rooms r ON b.room_number = r.room_number " +
	        "WHERE b.username = ?";

	    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db");
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, userManager.getCurrentUser().getUsername());
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int id          = rs.getInt("id");
	                int roomNumber  = rs.getInt("room_number");
	                String type     = rs.getString("type");
	                double price    = rs.getDouble("price");
	                String state    = rs.getString("state");
	                String hotel    = rs.getString("hotel_name");
	                String address  = rs.getString("address");
	                String phone    = rs.getString("phone");
	                String in       = rs.getString("check_in");
	                String out      = rs.getString("check_out");

	                model.addRow(new Object[]{
	                    id, roomNumber, type, price, state, 
	                    hotel, address, phone, in, out, "Delete"
	                });
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(mainPanel,
	            "Error loading bookings from database.",
	            "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    JTable table = new JTable(model);
	    // hide the ID column
	    table.getColumnModel().getColumn(0).setMinWidth(0);
	    table.getColumnModel().getColumn(0).setMaxWidth(0);

	    table.getColumn("Action").setCellRenderer(new ButtonRenderer());
	    table.getColumn("Action").setCellEditor(
	        new DeleteButtonEditor(new JCheckBox(), model, userManager));

	    mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}

   
   private void showSearchPanel() {
	    mainPanel.removeAll();
	    mainPanel.setLayout(new BorderLayout());

	    // Create a well-padded, vertically stacked panel
	    JPanel datePanel = new JPanel();
	    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
	    datePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
	    datePanel.setBackground(new Color(245, 245, 250)); // light background

	    // State filter panel
	    JPanel statePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
	    statePanel.setOpaque(false);
	    JLabel stateLabel = new JLabel("Select State:");
	    stateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
	    JComboBox<String> stateComboBox = new JComboBox<>(new String[]{
	        "All", "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
	        "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois",
	        "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
	        "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana",
	        "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
	        "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
	        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah",
	        "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
	    });
	    stateComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
	    statePanel.add(stateLabel);
	    statePanel.add(stateComboBox);

	    // Date input panel
	    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
	    inputPanel.setOpaque(false);
	    inputPanel.add(new JLabel("Check-In:"));
	    JSpinner checkInSpinner = new JSpinner(new SpinnerDateModel());
	    checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
	    checkInSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
	    inputPanel.add(checkInSpinner);

	    inputPanel.add(new JLabel("Check-Out:"));
	    JSpinner checkOutSpinner = new JSpinner(new SpinnerDateModel());
	    checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
	    checkOutSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(2)));
	    inputPanel.add(checkOutSpinner);

	    // Search button panel
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	   
	    
	    JButton searchButton = new JButton("Search Available Rooms");
	    searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
	    searchButton.setBackground(new Color(70, 130, 180)); // steel blue
	    searchButton.setForeground(Color.WHITE);
	    searchButton.setFocusPainted(false);
	    searchButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

	    // Force background color to show on all platforms
	    searchButton.setOpaque(true);
	    searchButton.setContentAreaFilled(true);
	    searchButton.setBorderPainted(false);

	    buttonPanel.add(searchButton);

	    // Add sub-panels to main input panel
	    datePanel.add(statePanel);
	    datePanel.add(inputPanel);
	    datePanel.add(Box.createVerticalStrut(10));
	    datePanel.add(buttonPanel);

	    mainPanel.add(datePanel, BorderLayout.NORTH);

	    searchButton.addActionListener(e -> {
	        LocalDate checkIn = ((java.util.Date) checkInSpinner.getValue())
	            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        LocalDate checkOut = ((java.util.Date) checkOutSpinner.getValue())
	            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        String selectedState = (String) stateComboBox.getSelectedItem();

	        if (!checkIn.isBefore(checkOut)) {
	            JOptionPane.showMessageDialog(frame, "Check-out must be after check-in.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        if (ChronoUnit.MONTHS.between(checkIn, checkOut) > 3) {
	            JOptionPane.showMessageDialog(frame, "Booking cannot exceed 3 months.", "Invalid Duration", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        showAvailableRooms(checkIn, checkOut, selectedState);
	    });

	    mainPanel.revalidate();
	    mainPanel.repaint();
	}


   private void showAvailableRooms(LocalDate checkIn, LocalDate checkOut, String selectedState) {
	    mainPanel.removeAll();
	    mainPanel.setLayout(new BorderLayout());

	    String[] columnNames = {
	    	    "Room Number", "Type", "Price", "Location", "Available",
	    	    "Hotel Name", "Hotel Address", "Hotel Phone", "Action"
	    	};
	    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
	        public boolean isCellEditable(int row, int column) {
	            return column == 8;
	        }
	    };

	    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db")) {
	        String sql = "SELECT * FROM rooms";
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int roomNumber = rs.getInt("room_number");
	                String type = rs.getString("type");
	                double price = rs.getDouble("price");
	                String state = rs.getString("state");
	                String hotelName = rs.getString("hotel_name");
	                String address = rs.getString("address");
	                String phone = rs.getString("phone");
	                // Apply filter: Skip rooms that don't match the selected state
	                if (!selectedState.equals("All") && !state.equals(selectedState)) {
	                    continue;
	                }

	                if (bookingDatabase.isRoomAvailable(conn, roomNumber, checkIn, checkOut)) {
	                    model.addRow(new Object[]{
	                            roomNumber, type, price, state, "Yes",
	                            hotelName, address, phone, "Book"
	                    });
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    JTable table = new JTable(model);
	    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
	    table.setRowSorter(sorter);
	    sorter.setComparator(0, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));
	    sorter.setComparator(2, Comparator.comparingDouble(o -> Double.parseDouble(o.toString())));
	    table.getColumn("Action").setCellRenderer(new ButtonRenderer());
	    table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), null, model, checkIn, checkOut, this.currentUser, this.userManager, this));

	    JScrollPane scrollPane = new JScrollPane(table);
	    mainPanel.add(scrollPane, BorderLayout.CENTER);

	    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    filterPanel.setBorder(BorderFactory.createTitledBorder("Sort & Filter"));
	    filterPanel.add(new JLabel("State:"));
	    String[] states = {
	    	    "All", "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
	    	    "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois",
	    	    "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
	    	    "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana",
	    	    "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
	    	    "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
	    	    "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah",
	    	    "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
	    	};
	    JComboBox<String> stateComboBox = new JComboBox<>(states);

	    // Add ActionListener to update the filter when the state is changed
	    stateComboBox.addActionListener(e -> {
	        String g = (String) stateComboBox.getSelectedItem();
	        showAvailableRooms(checkIn, checkOut, g);  // Re-apply the filter with the new state
	    });

	    filterPanel.add(stateComboBox);
	    mainPanel.add(filterPanel, BorderLayout.SOUTH);

	    mainPanel.revalidate();
	    mainPanel.repaint();
	}
  
   private void showRegisterPanel() {
	    mainPanel.removeAll();
	    mainPanel.setLayout(new BorderLayout());

	    JPanel formPanel = new JPanel();
	    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
	    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

	    JTextField usernameField = new JTextField(15);
	    JPasswordField passwordField = new JPasswordField(15);
	    JTextField nameField = new JTextField(15);
	    JTextField funFactField = new JTextField(15);

	    JButton registerButton = new JButton("Register");
	    JButton backButton = new JButton("Back to Login");

	    formPanel.add(createLabeledField("Username", usernameField));
	    formPanel.add(Box.createVerticalStrut(10));
	    formPanel.add(createLabeledField("Password", passwordField));
	    formPanel.add(Box.createVerticalStrut(10));
	    formPanel.add(createLabeledField("Full Name", nameField));
	    formPanel.add(Box.createVerticalStrut(10));
	    formPanel.add(createLabeledField("Fun Fact", funFactField));
	    formPanel.add(Box.createVerticalStrut(20));

	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	    buttonPanel.add(registerButton);
	    buttonPanel.add(backButton);
	    formPanel.add(buttonPanel);

	    // Actions
	    registerButton.addActionListener(e -> {
	        String username = usernameField.getText().trim();
	        String password = new String(passwordField.getPassword());
	        String name = nameField.getText().trim();
	        String funFact = funFactField.getText().trim();

	        if (userManager.register(username, password, name, funFact)) {
	            JOptionPane.showMessageDialog(frame, "Registration successful.");
	            showLoginPanel();
	        } else {
	            JOptionPane.showMessageDialog(frame, "Username already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    backButton.addActionListener(e -> showLoginPanel());

	    // Center the form
	    JPanel centerPanel = new JPanel(new GridBagLayout());
	    centerPanel.add(formPanel);

	    mainPanel.add(centerPanel, BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}
   
// Custom JPanel with background image

   public void showLoginPanel() {
	    mainPanel.removeAll();
	    mainPanel.setLayout(new BorderLayout());
	    
	    // Create the login panel
	    JPanel loginPanel = new JPanel();
	    loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
	    loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

	    JTextField usernameField = new JTextField(15);
	    JPasswordField passwordField = new JPasswordField(15);
	    JButton loginButton = new JButton("Login");
	    JButton registerButton = new JButton("Register");

	    loginPanel.add(createLabeledField("Username", usernameField));
	    loginPanel.add(Box.createVerticalStrut(10));
	    loginPanel.add(createLabeledField("Password", passwordField));
	    loginPanel.add(Box.createVerticalStrut(20));

	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	    buttonPanel.add(loginButton);
	    buttonPanel.add(registerButton);
	    loginPanel.add(buttonPanel);

	    // Actions
	    registerButton.addActionListener(e -> showRegisterPanel());
	    loginButton.addActionListener(e -> {
	        String username = usernameField.getText().trim();
	        String password = new String(passwordField.getPassword());
	        if (userManager.login(username, password)) {
	            this.currentUser = userManager.getUser(username);
	            JOptionPane.showMessageDialog(frame, "Login successful.");
	            showSearchPanel();
	        } else {
	            JOptionPane.showMessageDialog(frame, "Invalid credentials, please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    // Center the login panel
	    JPanel centerPanel = new JPanel(new GridBagLayout());
	    centerPanel.add(loginPanel);

	    mainPanel.add(centerPanel, BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}

	private JPanel createLabeledField(String labelText, JComponent field) {
	    JPanel panel = new JPanel(new BorderLayout());
	    JLabel label = new JLabel(labelText);
	    panel.add(label, BorderLayout.NORTH);
	    panel.add(field, BorderLayout.CENTER);
	    return panel;
	}

   public static void main(String[] args) {
       SwingUtilities.invokeLater(HotelBookingApp::new);
   }
}

