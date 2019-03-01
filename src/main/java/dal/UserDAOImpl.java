package dal;

import dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDAOImpl implements IUserDAO{
    private String database = "cdio1db ";

    public Connection createConnection() throws DALException {
        try {
            return DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185092?"
                    + "user=s185092&password=C7uzj8I1GztZQ40cOeE7f");
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
    @Override
    public UserDTO getUser(String password) throws DALException {
        Connection c = createConnection();
        try {
            Statement st = c.createStatement();
            ResultSet resultSet = st.executeQuery("select * from " + database + " where password = '" + password + "'");

            UserDTO user = new UserDTO();
            while(resultSet.next()) {
                user.setUserID(resultSet.getInt(1));
                user.setUserName(resultSet.getString(2));
                user.setIni(resultSet.getString(3));
                user.setCpr(resultSet.getString(4));
                user.setPassword(resultSet.getString(5));
                //string into arraylist
                user.setRoles(Arrays.asList(resultSet.getString(6).split(", ")));
            }
            c.close();
            return user;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public List<UserDTO> getUserList() throws DALException {
        Connection c = createConnection();

        List<UserDTO> users = new ArrayList<>();

        try{

            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + database);
            while(resultSet.next()) {
                UserDTO user = new UserDTO();

                List<String> roles = Arrays.asList(resultSet.getString(6).split("\\?\\?\\?"));

                user.setUserID(resultSet.getInt(1));
                user.setUserName(resultSet.getString(2));
                user.setIni(resultSet.getString(3));
                user.setRoles(roles);
                users.add(user);
            }
            c.close();
            return users;
        }catch(SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserDTO user) throws DALException {
        Connection c = createConnection();
        try {
            PreparedStatement st = c.prepareStatement("insert into " + database + "values('"
                    + user.getUserID() + "', '" + user.getUserName() + "', '" + user.getIni() + "', '" + user.getCpr()
                    + "', '" + user.getPassword() + "', '" + user.getArrayAsString() + "')");
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void updateUser(UserDTO user) throws DALException {
        Connection c = createConnection();
        try {
            PreparedStatement st = c.prepareStatement("update " + database +
                    "set userName = '" + user.getUserName() + "', ini = '" + user.getIni() +
                    "', cpr = '" + user.getCpr() + "', password = '" + user.getPassword() +
                    "', roles = '" + user.getArrayAsString() + "' where userID = " + user.getUserID());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        Connection c = createConnection();
        try {
            PreparedStatement st = c.prepareStatement("delete from " + database +
                    "where userID = " + userId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
}
