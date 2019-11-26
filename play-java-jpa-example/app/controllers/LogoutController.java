package controllers;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class LogoutController extends DefaultController {

    @Inject
    public LogoutController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result logoutPage() {
        return ok(views.html.old.logout.render());
    }

    public CompletionStage<Result> logout() {
        return CompletableFuture.supplyAsync(() ->
                        redirect(routes.LoginController.index())
                                .withNewSession(),
                ec.current());
    }
}

// TODO do we need to do https?