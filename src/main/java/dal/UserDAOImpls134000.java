package dal;

import dto.UserDTO;

import java.util.List;

//TODO Rename class
public class UserDAOImpls134000 implements IUserDAO {
    //TODO Make a connection to the database

    @Override
    public UserDTO getUser(int userId) throws DALException {
        //TODO Implement this
        return null;
    }

    @Override
    public List<UserDTO> getUserList() throws DALException {
        //TODO Implement this
        return null;
    }

    @Override
    public void createUser(UserDTO user) throws DALException {
        //TODO Implement this
    }

    @Override
    public void updateUser(UserDTO user) throws DALException {
        //TODO Implement this
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        //TODO Implement this
    }
}
