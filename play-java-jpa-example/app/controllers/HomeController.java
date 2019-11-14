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

public class HomeController extends Controller {

    private final FormFactory formFactory;
    private final SignUpRepository signUpRepository;
    private final PersonRepository personRepository;
    private final HttpExecutionContext ec;

    @Inject
    public HomeController(FormFactory formFactory, SignUpRepository signUpRepository, PersonRepository personRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.signUpRepository = signUpRepository;
        this.personRepository = personRepository;
        this.ec = ec;
    }

    public Result home() {
        return ok(views.html.home.render());
    }
}
