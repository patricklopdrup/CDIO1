package dal;

import dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        UserDAOImpl test = new UserDAOImpl();
        UserDTO user = new UserDTO();
        TUI tui = new TUI();

        tui.menu();

        /*
        user.setUserID(1);
        user.setUserName("Patrick");
        user.setIni("PLH");
        user.setCpr("0502981111");
        user.setPassword("1234");
        List<String> testArr = new ArrayList<>();
        testArr.add("studerende");
        testArr.add("underviser");
        user.setRoles(testArr);

        try {
            test.updateUser(user);
        } catch (IUserDAO.DALException e) {
            System.out.println(e.getMessage());
        }
        */

    }
}
