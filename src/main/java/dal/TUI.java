package dal;

import dto.UserDTO;

import javax.swing.*;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
        String tempID = "";
        int userID = 0;
        String userName = "";
        String ini = "";
        String cpr = "";
        String password = "";
        String roles = ""; // FIXME: 27-02-2019 skal man selv vælge roles? hvad med userID?
        System.out.println("--- Opret en bruger ---");
        //id
        do {
            try {
                System.out.println("Vælg et brugerID mellem 11-99");
                tempID = input.next();
                userID = Integer.parseInt(tempID);
                if(userID >= 11 && userID <= 99) {
                    int res = msgBox("Er du sikker på dit ID skal være: "+userID);
                    if(IDchecker(userID) && res == 0) {
                        success = true;
                    } else {
                        System.out.println("Dette ID er allerede taget. Vælg et nyt.");
                    }
                } else {
                    System.out.println("ID skal være mellem 11-99");
                }
            } catch (NumberFormatException e) {
                System.out.println("Skriv et tal.");
            }
            // TODO: 01-03-2019 skriv pass -> check ledighed -> giv dem id
        } while (!success);

        //username
        success = false;
        do {
            try {
                System.out.print("Vælg brugernavn (2-20 tegn): ");
                userName = input.next();
                int res = msgBox("Er du sikker på dit brugernavn skal være: "+userName);
                if(userName.length() >= 2 && userName.length() <= 20 && res == 0) {
                    success = true;
                } else {
                    System.out.println("Brugernavn skal være mellem 2-20 tegn.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (!success);

        //ini
        success = false;
        do {
            try {
                System.out.print("Skriv initialer(2-4 tegn): ");
                ini = input.next();
                int res = msgBox("Er du sikker på dine initialer skal være: "+ini);
                if(ini.length() >= 2 && ini.length() <= 4 && res == 0) {
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
                Long.parseLong(cpr);
                int res = msgBox("Er du sikker på dit CPR-nummer skal være: "+cpr);
                if(cpr.length() == 10 && res == 0) {
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
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890.-_+!?=";
        StringBuilder pass = new StringBuilder();
        Random rnd = new Random();
        while (pass.length() < 8) {
            int nextChar = (int) (rnd.nextDouble() * chars.length());
            pass.append(chars.charAt(nextChar));
        }
        password = pass.toString();



        // FIXME: 28-02-2019 lav fix, hvis man vil starte helt forfra

        //roles
        success = false;

        //confirmation
        String passEncrypt = encryptPassword(password);
        int res = msgBox("\nBrugernavn: " + userName +
                "\nBruger-ID: " + userID +
                "\nInitialer: " + ini +
                "\nCPR-nummer: " + cpr.substring(0, 6) + "-xxxx" +
                "\nPassword: " + passEncrypt +
                "\n\n Er ovenstående info korrekt?");


        if(res == 0) {
            user.setUserID(userID);
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
            System.out.println("Printing users...");
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
        boolean firstgo = false;
        String tempID;
        int userID = 0;
        String password;
        String userName;
        String ini;
        String cpr;
        String roles;
        UserDTO userToUpdate;

        System.out.println("--- Ret bruger ---");
        do {
            do {
                try {
                    System.out.print("Skriv dit brugerID for at rette: ");
                    tempID = input.next();
                    userID = Integer.parseInt(tempID);
                    firstgo = true;
                } catch (NumberFormatException e) {
                    System.out.println("ID skal være et tal.");
                }
            } while(!firstgo);

            try {
                if(userDAO.getUser(userID).getUserName() != null) {
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
            password = input.next();
            if(password.equals("-") || password.length() >= 6) { success = true; }
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
                userName = userDAO.getUser(userID).getUserName();
            }
            if(ini.equals("-")) {
                ini = userDAO.getUser(userID).getIni();
            }
            if(cpr.equals("-")) {
                cpr = userDAO.getUser(userID).getCpr();
            }
            if(password.equals("-")) {
                password = userDAO.getUser(userID).getPassword();
            }
            passEncrypt = encryptPassword(password);
            if(roles.equals("-")) {
                roles = userDAO.getUser(userID).getArrayAsString();
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
                userToUpdate = userDAO.getUser(userID);
                userToUpdate.setUserName(userName);
                userToUpdate.setIni(ini);
                userToUpdate.setCpr(cpr);
                userToUpdate.setPassword(password);
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
        int userID = 0;
        String tempUserID;
        System.out.println("--- Slet bruger ---");

        do{
            System.out.print("Skriv userID[int] til den bruger der skal slettes: ");
            tempUserID = input.next();

            if(isParsableInt(tempUserID)){
                userID = Integer.parseInt(tempUserID);
            }else{
                System.out.println("Angiv et userID af typen int!");
                System.out.println(/*Empty line for aesthetics*/);
            }
        }while(!isParsableInt(tempUserID));

        System.out.print("Indtast koden til denne bruger: ");
        String pw = input.next();
        try{

            if(!IDchecker(userID) && userDAO.getUser(userID).getPassword().equals(pw)){
                System.out.println("Er du sikker på, at du vil slette brugeren: " + userID + "?");

                String validation = input.next();
                if(validation.equalsIgnoreCase("yes") || validation.equalsIgnoreCase("ja")){
                    userDAO.deleteUser(userID);
                    System.out.println("Brugeren med userID: " + userID + " er nu blevet slettet..");
                }
            }else{
                System.out.println("Brugeren eksisterer enten ikke, eller også matchers koden ikke til userID'et");
                System.out.println(/*Empty line for aesthetics*/);
            }

        } catch (IUserDAO.DALException e) {
            System.out.println("Der må være en fejl i forbindelsen til databasen..");
        }
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

    //checks if the ID is free to use
    public boolean IDchecker(int userID) {
        try {
            for (UserDTO users : userDAO.getUserList()) {
                if (users.getUserID() == userID) {
                    return false;
                }
            }
        } catch (IUserDAO.DALException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public  int msgBox(String infoMessage)
    {
        final JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        parent.setSize(400,280);
        return JOptionPane.showConfirmDialog( parent ,infoMessage,"Message", JOptionPane.YES_NO_OPTION);

    }

    private boolean isParsableInt(String input) {
        boolean parsable = true;
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            parsable = false;
        }
        return parsable;
    }
}
