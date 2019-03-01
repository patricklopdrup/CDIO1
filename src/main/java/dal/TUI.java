package dal;

import dto.UserDTO;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TUI {
    private int function = 0;
    Scanner input = new Scanner(System.in);
    UserDTO user = new UserDTO();
    UserDAOImpl userDAO = new UserDAOImpl();

    public void showMenu() {
        String tempFunc;
        boolean success = false;

        System.out.println("Hovedmenu:");
        System.out.println(
                "1.\tOpret ny bruger\n" +
                        "2.\tList brugere\n" +
                        "3.\tRet bruger\n" +
                        "4.\tSlet bruger\n" +
                        "5.\tAfslut program");
        do {
            try {
                System.out.print("Vælg en funktion: ");
                tempFunc = input.next();
                System.out.println();
                function = Integer.parseInt(tempFunc);
                if(function >= 1 && function <= 5) {
                    success = true;
                } else {
                    System.out.println("Tallet skal være mellem 1-5.");
                }
            } catch (Exception e) {
                System.out.println("Vælg en funktion vha. heltal.");
            }
        } while(!success);
    }


    public void menu() {
        boolean endProgram = false;
        while(!endProgram) {
            showMenu();
            switch (function) {
                case 1: createUser(); break;
                case 2: listUsers(userDAO); break;
                case 3: updateUser(); break;
                case 4: deleteUser(); break;
                case 5: System.out.println("Programmet afsluttes..."); endProgram = true;
            }
        }
    }

    private void createUser() {
        boolean success = false;
        String userName = "";
        String ini = "";
        String cpr = "";
        String password = "";
        String roles = ""; // FIXME: 27-02-2019 skal man selv vælge roles? hvad med userID?
        System.out.println("--- Opret en bruger ---");
        //username
        do {
            try {
                System.out.print("Vælg brugernavn (2-20 tegn): ");
                userName = input.next();
                if(userName.length() >= 2 && userName.length() <= 20) {
                    success = true;
                } else {
                    System.out.println("Brugernavn skal være mellem 2-20 tegn.");
                }
            } catch (Exception e) { // FIXME: 27-02-2019 måske skal det være DALException??
                System.out.println(e.getMessage());
            }
        } while (!success);

        //ini
        success = false;
        do {
            try { // FIXME: 27-02-2019 hvis vi ikke vil tjekke for space, skal try/catch slettes
                System.out.print("Skriv initialer(2-4 tegn): ");
                ini = input.next();
                if(ini.length() >= 2 && ini.length() <= 4) {
                    success = true;
                } else {
                    System.out.println("Initialer skal være mellem 2-4 tegn.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (!success);

        //cpr
        success = false;
        do {
            try {
                System.out.print("Skriv CPR-nummer(kun tal): ");
                cpr = input.next();
                if(cpr.length() == 10) {
                    success = true;
                } else {
                    System.out.println("Skriv alle 10 tal. Fx 1234567890");
                }
            } catch (NumberFormatException e) {
                System.out.println("Skriv kun tal.");
            }
        } while(!success);

        //password
        success = false;
        boolean tempSuccess = false;
        String temppass;
        do {
            do {
                System.out.print("Vælg password(mindst 6 tegn): ");
                password = input.next();
                if(password.length() >= 6) {
                    tempSuccess = true;
                } else {
                    System.out.println("Password skal mindst være 6 tegn");
                }
            } while (!tempSuccess);

            System.out.print("Gentag password: ");
            temppass = input.next();
            if(temppass.equals(password)) {
                success = true;
            } else {
                System.out.println("Password mathcer ikke. Prøv igen.");
            }
        } while (!success);

        // FIXME: 28-02-2019 lav fix, hvis man vil starte helt forfra

        //roles
        success = false;

        //confimation
        String passEncrypt = encryptPassword(password);
        System.out.println("\nEr du sikker på, du vil oprette brugeren: " +
                "\nBrugernavn: " + userName +
                "\nInitialer: " + ini +
                "\nCPR-nummer: " + cpr.substring(0, 6) + "-xxxx" +
                "\nPassword: " + passEncrypt);
        String confirme;
        System.out.print("[Ja], [Nej]");
        confirme = input.next();
        if(confirme.equalsIgnoreCase("ja") || confirme.equalsIgnoreCase("yes")) {
            user.setUserName(userName);
            user.setIni(ini);
            user.setCpr(cpr);
            user.setPassword(password);
            try {
                userDAO.createUser(user);
                System.out.println("Brugeren er oprettet!\n");
            } catch (IUserDAO.DALException e) {
                System.out.println("Password findes allerede eller ingen internetforbindelse.\n");
            }
        } else {
            System.out.println("Brugeren er ikke oprettet.\n");
        }
    }

    private void listUsers(UserDAOImpl iDAO) {
        try{
            System.out.println("Printing users....");
            List<UserDTO> userList = iDAO.getUserList();
            for(UserDTO userDTO : userList){
                System.out.println(userDTO);
            }
        } catch (IUserDAO.DALException e) {
            System.out.println("Der er ingen internetforbindelse");
        }
    }

    private void updateUser() {
        boolean success = false;
        String password;
        String userName;
        String ini;
        String cpr;
        String passwordToUpdate;
        String roles;
        UserDTO userToUpdate;

        System.out.println("--- Ret bruger ---");
        do {
            System.out.print("Skriv dit password for at rette: ");
            password = input.next();
            try {
                if(userDAO.getUser(password).getUserName() != null) {
                    success = true;
                } else {
                    System.out.println("Brugeren findes ikke.");
                }
                System.out.println(/*empty line*/);
            } catch (IUserDAO.DALException e) {
                System.out.println("Brugeren findes ikke eller ingen internetforbindelse\n");
            }
        }while (!success);

        success = false;
        System.out.println("Skriv \"-\" [bindestreg] for at springe over felt, hvis du ikke vil rette alle oplysninger.");
        do {
            System.out.print("Nyt brugernavn: ");
            userName = input.next();
            if(userName.equals("-") || userName.length() >= 2 && userName.length() <= 20) { success = true; }
        } while (!success);
        success = false;
        do {
            System.out.print("Nye initialer: ");
            ini = input.next();
            if(ini.equals("-") || ini.length() >= 2 && ini.length() <=4) { success = true; }
        } while (!success);
        success = false;
        do {
            System.out.print("Nyt CPR-nummer: ");
            cpr = input.next();
            if(cpr.equals("-")) {
                success = true;
            } else if(cpr.length() == 10) {
                try {
                    Integer.parseInt(cpr);
                    success = true;
                } catch (NumberFormatException e) {
                    System.out.println("Skriv kun tal.");
                }
            }
        } while (!success);
        success = false;
        do {
            System.out.print("Ny adgangskode: ");
            passwordToUpdate = input.next();
            if(passwordToUpdate.equals("-") || passwordToUpdate.length() >= 6) { success = true; }
        } while (!success);
        success = false;
        do {
            System.out.print("Nye roller(skriv ', ' mellem roller [komma mellemrum]: )");
            roles = input.next();
            String[] tempRoles = roles.split(", ");
            int successCounter = 0;
            if(roles.equals("-")) {
                success = true;
            } else {
                for(int i = 0; i < tempRoles.length; i++) {
                    if(tempRoles[i].equalsIgnoreCase("/*SKRIV HER*/")) { // TODO: 01-03-2019 sammenlign med de roller man må være
                        successCounter++;
                    }
                }
                if(successCounter == tempRoles.length) { success = true; }
            }
        } while (!success);

        try {
            String passEncrypt;
            if(userName.equals("-")) {
                userName = userDAO.getUser(password).getUserName();
            }
            if(ini.equals("-")) {
                ini = userDAO.getUser(password).getIni();
            }
            if(cpr.equals("-")) {
                cpr = userDAO.getUser(password).getCpr();
            }
            if(passwordToUpdate.equals("-")) {
                passwordToUpdate = password;
            }
            passEncrypt = encryptPassword(passwordToUpdate);
            if(roles.equals("-")) {
                roles = userDAO.getUser(password).getArrayAsString();
            }

            System.out.println("\nEr du sikker på, du vil rette brugeren: " +
                    "\nBrugernavn: " + userName +
                    "\nInitialer: " + ini +
                    "\nCPR-nummer: " + cpr.substring(0, 6) + "-xxxx" +
                    "\nPassword: " + passEncrypt);
            String confirme;
            System.out.print("[Ja], [Nej]");
            confirme = input.next();
            if(confirme.equalsIgnoreCase("ja") || confirme.equalsIgnoreCase("yes")) {
                userToUpdate = userDAO.getUser(password);
                userToUpdate.setUserName(userName);
                userToUpdate.setIni(ini);
                userToUpdate.setCpr(cpr);
                userToUpdate.setPassword(passwordToUpdate);
                // FIXME: 28-02-2019 ordne roles, så den virker, når man ikke skriver noget i den står også højere oppe
                userToUpdate.setRoles(Arrays.asList(roles.split(", ")));
                userDAO.updateUser(userToUpdate);
                System.out.println("Brugeren er blevet rettet.\n");
            } else {
                System.out.println("Brugeren er ikke blevet rettet.\n");
            }
        } catch (IUserDAO.DALException e) {
            System.out.println("Brugeren er ikke blevet rettet.\n");
        }
    }

    private void deleteUser() {

    }

    private String encryptPassword(String password) {
        String passStart = password.substring(0,2);
        String passEnd = password.substring(password.length()-2);
        String passMid = "";
        for(int i = 2; i < password.length()-2; i++) {
            passMid += "*";
        }
        return passStart + passMid + passEnd;
    }
}
