
# 🏨 Final Project Documentation  
## Hotel Reservation System

### 📌 Project Description  
This is a Java-based **Hotel Reservation System** that enables users to register, log in, and manage room bookings through an intuitive graphical user interface (GUI). Users can view available rooms, make reservations, cancel bookings, and manage their account details. The system emphasizes user-friendliness and robustness, featuring real-time database operations and supporting concurrent user access.

## Demo Video  
Watch a walkthrough of the application in action:  
🔗 [Hotel Booking System Demo](https://drive.google.com/file/d/1SIBXra4SHpceaALfBVXx4FtPyydqhoPW/view?usp=sharing)  
*(PS: At 0:22 I don't actually select California — it's filtered later on)*---

### ✅ Core Features  

#### 1. User Account Management  
- New users can register for an account  
- Existing users can securely log in and log out  
- Users have personalized access to bookings and room actions  

#### 2. Room Reservation  
- View a list of available rooms with detailed information  
- Book rooms for specific dates with date validation  
- Availability is verified before booking to prevent double bookings  

#### 3. Booking Management  
- View all your personal bookings in a dedicated section  

---

### 🛠️ Technologies Used  

- **Swing (Java GUI Library)**  
  For building the graphical interface, including login/registration screens, booking panels, and dynamic tables  

- **SQLite (Embedded Database)**  
  Stores user, room, and booking data locally in a persistent `hotel.db` file  

- **JDBC (Java Database Connectivity)**  
  Handles SQL-based interactions between the Java application and the SQLite database  

- **Concurrency Handling**  
  Room booking operations are synchronized to prevent race conditions and ensure accurate availability checks

- **Salt & Pepper Password Hashing**  
  User passwords are stored securely using a cryptographic hash combined with unique salts and a fixed pepper value. This adds an extra layer of protection against attacks and ensures that even if the database is compromised, raw passwords are not exposed.


---

### ▶️ How to Run the Project  

#### Requirements  
- Java 8 or above  
- SQLite JDBC Driver (included in the project)  
- No need to install SQLite separately — the embedded database (`hotel.db`) is included

#### Steps  
1. Clone the repository  
   ```bash
   git clone https://github.com/Kumuda123/Hotel-Reservation-System
