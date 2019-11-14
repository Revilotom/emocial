package controllers;

import models.Person;
import repositories.person.PersonRepository;
import models.SignUp;
import repositories.signUp.SignUpRepository;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SignUpController extends Controller {

    private final FormFactory formFactory;
    private final SignUpRepository signUpRepository;
    private final PersonRepository personRepository;
    private final HttpExecutionContext ec;

    @Inject
    public SignUpController(FormFactory formFactory, SignUpRepository signUpRepository, PersonRepository personRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.signUpRepository = signUpRepository;
        this.personRepository = personRepository;
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

        SignUp s = signUpForm.get();

        return signUpRepository.isTaken(s.getUsername()).thenApplyAsync(taken -> {

            if (taken){
                return badRequest(views.html.signUp.render(signUpForm.withError("Alert", "USERNAME TAKEN")));
            }

            Person p = new Person(s.getName(), s.getUsername(), s.getPassword1());
            personRepository.add(p);
            return redirect(routes.PersonController.getPersons());
        }, ec.current());
    }
}
