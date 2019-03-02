package dal;

import dto.UserDTO;

public class Main {
    public static void main(String[] args) {
        UserDAOImpl test = new UserDAOImpl();
        UserDTO user = new UserDTO();
        TUI tui = new TUI();

        tui.menu();
    }
}
