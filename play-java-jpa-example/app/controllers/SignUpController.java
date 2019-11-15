package controllers;

import models.Person;
import forms.SignUp;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SignUpController extends Controller {

    private final FormFactory formFactory;
    private final PersonRepository personRepository;
    private final HttpExecutionContext ec;

    @Inject
    public SignUpController(FormFactory formFactory, PersonRepository signUpRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.personRepository = signUpRepository;
        this.ec = ec;
    }

    public Result signUp() {
        return ok(views.html.signUp.render(formFactory.form(SignUp.class)));
    }

    public CompletionStage<Result> submitSignUp(final Http.Request request) {
        Form<SignUp> signUpForm = formFactory.form(SignUp.class).bindFromRequest(request);

        if (signUpForm.hasErrors() || signUpForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.signUp.render(signUpForm)), ec.current());
        }

        SignUp signUpFields = signUpForm.get();

        return personRepository.isTaken(signUpFields.getUsername()).thenApplyAsync(taken -> {

            if (taken){
                return badRequest(views.html.signUp.render(signUpForm.withError("Alert", "USERNAME TAKEN")));
            }

            Person p = new Person(signUpFields.getName(), signUpFields.getUsername(), signUpFields.getPassword1());
            personRepository.update(p);
            return redirect(routes.LoginController.index());

        }, ec.current());
    }
}
