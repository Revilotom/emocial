package forms;

import play.data.validation.Constraints;

public class Login {

    public Login(){}

    public Login(@Constraints.Required String username, @Constraints.Required String password) {
        this.username = username;
        this.password = password;
    }

    @Constraints.Required
    private String username;

    @Constraints.Required
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
