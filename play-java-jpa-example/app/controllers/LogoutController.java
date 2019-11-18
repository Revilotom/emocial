package controllers;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class LogoutController extends Controller {

    private final FormFactory formFactory;
    private final PersonRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public LogoutController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.repository = repository;
        this.ec = ec;
    }

    public Result logoutPage() {
        return ok(views.html.old.logout.render());
    }

    public CompletionStage<Result> logout() {
        return CompletableFuture.supplyAsync(() ->
                        redirect(routes.LoginController.index())
                                .withNewSession()
                ,
                ec.current());
    }
}
