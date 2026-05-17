package DBpackage;
import java.sql.*;

public class DBconnect {

    public static Connection con = null;

    /**
     * Establishes a connection to the MySQL database.
     */
    public static void connection() {
        String url      = "jdbc:mysql://localhost:3306/AppDB";
        String username = "root";
        String pass     = "yourpassword";
        try {
            con = DriverManager.getConnection(url, username, pass);
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Validates login credentials and fetches the user role.
     * @param Email the user's email address
     * @param Password the user's password
     * @return the user's role ("user" or "admin"), or null if login fails
     */
    public static String loginUser(String Email, String Password) {
        try {
            String sql = "SELECT Role FROM Users WHERE Email = ? AND Password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Email);
            ps.setString(2, Password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Role");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a new user into the database with the default role of "user".
     * @param Name the user's full name
     * @param Email the user's email address
     * @param Password the user's password
     */
    public static void registerUser(String Name, String Email, String Password) {
        try {
            String sql = "INSERT INTO Users (Name, Email, Password, Role) VALUES (?, ?, ?, 'user')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Name);
            ps.setString(2, Email);
            ps.setString(3, Password);
            ps.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }
}