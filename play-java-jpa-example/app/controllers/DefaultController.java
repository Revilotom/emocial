package controllers;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import repositories.person.PersonRepository;

import javax.inject.Inject;

public abstract class DefaultController extends Controller {
     final FormFactory formFactory;
    final PersonRepository repository;
    final HttpExecutionContext ec;

    @Inject
    public DefaultController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.repository = repository;
        this.ec = ec;
    }
}
