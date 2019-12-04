package forms;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class SignUp implements Constraints.Validatable<List<ValidationError>> {

    // Alphanumeric + space
    private final static String regexPattern = "^[a-zA-Z0-9 ]*$";

    @Constraints.MaxLength(20)
    @Constraints.MinLength(5)
    @Constraints.Required
    private String name;

    @Constraints.MaxLength(20)
    @Constraints.Pattern(SignUp.regexPattern)
    @Constraints.MinLength(5)
    @Constraints.Required
    private String username;

    @Constraints.MaxLength(100)
    @Constraints.Pattern(SignUp.regexPattern)
    @Constraints.MinLength(8)
    @Constraints.Required
    private String password1;

    @Constraints.MaxLength(100)
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
    public List<ValidationError> validate() {

        if (password1 == null || password2 == null){
            return null;
        }

        List<ValidationError> errorList = new ArrayList<>();

        if (!password1.equals(password2)) {
            errorList.add(new ValidationError("password1", "Passwords dont match!"));
            errorList.add(new ValidationError("password2", "Passwords dont match!"));
            return errorList;
        }
        return null;
    }
}
