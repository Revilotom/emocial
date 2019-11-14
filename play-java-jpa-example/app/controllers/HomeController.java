package controllers;

import repositories.person.PersonRepository;
import repositories.signUp.SignUpRepository;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

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
