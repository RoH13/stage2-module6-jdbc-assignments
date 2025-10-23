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





    private static final String createUserSQL = "insert into myusers (id, firstname, lastname, age)\n" +
            "values (?, ?, ?, ?);";
    private static final String updateUserSQL = "update myusers\n" +
            "set id = ?, firstname = ?, lastname = ?, age = ?\n" +
            "where id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?;";
    private static final String findAllUserSQL = "SELECT * from myusers";

    public Long createUser(User user) {
        try {
            ps = connection.prepareStatement(createUserSQL);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getAge());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user.getId();
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

        User user = new User();
        try {
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String ln = rs.getString("lastname");
                int age = rs.getInt("age");
                user = new User(id, userName, ln, age);
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

    public User updateUser(User user) {
        try {
            ps = connection.prepareStatement(updateUserSQL);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getAge());
            ps.setLong(5, user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

     public void deleteUser(Long userId) {
        try {
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
