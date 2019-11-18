package controllers;

import repositories.person.PersonRepository;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class HomeController extends DefaultController {


    @Inject
    public HomeController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result home() {
        return ok(views.html.old.home.render());
    }
}
