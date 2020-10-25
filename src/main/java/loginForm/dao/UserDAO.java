package loginForm.dao;

import loginForm.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final ConnectDAO connectDAO;
    private final User mockUser = new User(0, "", "");

    public UserDAO(ConnectDAO connectDAO) {
        this.connectDAO = connectDAO;
    }

    public User getByCredentials(String login, String password) {
        User user = null;
        connectDAO.connect();
        try {
            PreparedStatement ps = connectDAO.connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                user = new User(id, login, password);
            }
            if (user != null) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mockUser;
    }
}