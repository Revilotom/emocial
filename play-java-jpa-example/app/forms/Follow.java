package forms;

import play.data.validation.Constraints;

public class Follow{

    public Follow(String nameOfPersonToFollow) {
        this.nameOfPersonToFollow = nameOfPersonToFollow;
    }

    public Follow() {
    }

    public String getUsernameOfPersonToFollow() {
        return this.nameOfPersonToFollow;
    }

    public void setNameOfPersonToFollow(String nameOfPersonToFollow) {
        this.nameOfPersonToFollow = nameOfPersonToFollow;
    }

    @Constraints.Required
    private
    String nameOfPersonToFollow;
}
