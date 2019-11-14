package forms;

import play.data.validation.Constraints;

@Constraints.Validate
public class SignUp implements Constraints.Validatable<String> {

    // Alphanumeric + space
    private final static String regexPattern = "^[a-zA-Z0-9 ]*$";

    @Constraints.MinLength(5)
    @Constraints.Required
    private String name;

    @Constraints.Pattern(SignUp.regexPattern)
    @Constraints.MinLength(5)
    @Constraints.Required
    private String username;

    @Constraints.Pattern(SignUp.regexPattern)
    @Constraints.MinLength(8)
    @Constraints.Required
    private String password1;

    @Constraints.Pattern(SignUp.regexPattern)
    @Constraints.MinLength(8)
    @Constraints.Required
    private String password2;

    public SignUp() {

    }

    public SignUp(@Constraints.Required String name, @Constraints.Required String username,
                  @Constraints.Required String password1, @Constraints.Required String password2) {
        this.name = name;
        this.username = username;
        this.password1 = password1;
        this.password2 = password2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    public String validate() {

        if (!password1.equals(password2)) {
            return "Passwords dont match!";
        }
        return null;
    }
}
