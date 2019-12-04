package controllers;

import models.Person;
import forms.SignUp;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;
import views.html.old.signUp;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class SignUpController extends DefaultController {

    @Inject
    public SignUpController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result signUp() {
        return ok(views.html.old.signUp.render(formFactory.form(SignUp.class)));
    }

    public CompletionStage<Result> submitSignUp(final Http.Request request) {

        Form<SignUp> signUpForm = formFactory.form(SignUp.class).bindFromRequest(request);

        if (hasFormBadRequestError(signUpForm)){
            return supplyAsyncBadRequest(signUp.render(signUpForm));
        }

        SignUp signUpFields = signUpForm.get();

        return repository.isTaken(signUpFields.getUsername()).thenApplyAsync(taken -> {

            if (taken){
                return badRequest(views.html.old.signUp.render(signUpForm.withError("username", "USERNAME TAKEN")));
            }

            Person p = new Person(signUpFields.getName(), signUpFields.getUsername(), signUpFields.getPassword1());
            repository.update(p);
            return redirect(routes.LoginController.index());

        }, ec.current());
    }
}
