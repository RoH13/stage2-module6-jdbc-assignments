package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
    CustomDataSource cds = CustomDataSource.getInstance();
    private Connection connection;

    {
        try {
            connection = cds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;
    private PreparedStatement ps;


    private Statement st = null;





    private static final String createUserSQL = "insert into myuser (id, firstname, lastname, age)\n" +
            "values ((select max(id) from myuser) + 1, ?, ?, ?); select max(id) from myuser";
    private static final String updateUserSQL = "update myuser\n" +
            "set firstname = ?, lastname = ?, age = ?\n" +
            "where id = ?";
    private static final String deleteUser = "DELETE FROM myuser WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myuser WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myuser WHERE firstname = ?, lastname = ?";
    private static final String findAllUserSQL = "SELECT * from myuser";

    public Long createUser(String fn, String ln, int age) {
        Long res = 0L;
        try {
            ps = connection.prepareStatement(createUserSQL);
            ps.setString(1, fn);
            ps.setString(2, ln);
            ps.setInt(3, age);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                res = rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public User findUserById(Long userId) {
        User user = new User();
        try {
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setInt(1, userId.intValue());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int age = rs.getInt("age");
                user = new User(id, firstname, lastname, age);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        String[] n = userName.split(" ");
        User user = new User();
        try {
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, n[0]);
            ps.setString(2, n[1]);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                int age = rs.getInt("age");
                user = new User(id, n[0], n[1], age);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<User>();
        {
            try {
                ps = connection.prepareStatement(findAllUserSQL);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    long id = rs.getLong("id");
                    String firstname = rs.getString("firstname");
                    String lastname = rs.getString("lastname");
                    int age = rs.getInt("age");
                    users.add(new User(id, firstname, lastname, age));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return users;
    }

    public User updateUser(Long userId, String fn, String ln, int age) {
        try {
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, fn);
            ps.setString(2, ln);
            ps.setInt(3, age);
            ps.setLong(4, userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new User(userId, fn, ln, age);
    }

     private void deleteUser(Long userId) {
        try {
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
