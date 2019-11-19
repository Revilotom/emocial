package forms;

import play.data.validation.Constraints;

import javax.validation.Constraint;

public class Follow{

    public Follow(String nameOfPersonToFollow) {
        this.nameOfPersonToFollow = nameOfPersonToFollow;
    }

    public Follow() {
    }

    public String getNameOfPersonToFollow() {
        return nameOfPersonToFollow;
    }

    public void setNameOfPersonToFollow(String nameOfPersonToFollow) {
        this.nameOfPersonToFollow = nameOfPersonToFollow;
    }

    @Constraints.Required
    private
    String nameOfPersonToFollow;
}
