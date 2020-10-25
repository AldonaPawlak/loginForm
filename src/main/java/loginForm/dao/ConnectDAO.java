package loginForm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDAO {
    private final String URL = System.getenv("URL");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");

    protected Connection connection = null;

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to DB");
        } catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Not connected");
        }
    }
}
