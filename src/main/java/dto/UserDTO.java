package dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDTO implements Serializable{

    private int userID;
    private String userName;
    private String ini;
    private String cpr;
    private String password;
    private List<String> roles;

    public UserDTO() {
        this.roles = new ArrayList<>();
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIni() {
        return ini;
    }
    public void setIni(String ini) {
        this.ini = ini;
    }

    public String getCpr() {
        return cpr;
    }
    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    public void addRole(String role) {
        this.roles.add(role);
    }
    public String getArrayAsString() {
        String string = "";
        for(int i = 0; i < roles.size(); i++) {
            if(i == roles.size()-1) {
                string += roles.get(i);
            } else {
                string += roles.get(i) + ", ";
            }
        }
        return string;
    }

    /**
     *
     * @param role
     * @return true if role existed, false if not
     */
    public boolean removeRole(String role){
        return this.roles.remove(role);
    }

    @Override
    public String toString() {
        return "UserDTO [userId=" + userID + ", userName=" + userName + ", ini=" + ini + ", roles=" + roles + "]";
    }

}