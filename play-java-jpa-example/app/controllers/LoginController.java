package controllers;

import com.mysql.jdbc.log.Log;
import models.Login;
import models.SignUp;
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


public class LoginController extends Controller {

    private final FormFactory formFactory;
    private final PersonRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public LoginController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.repository = repository;
        this.ec = ec;
    }

    public Result index() {
        return ok(views.html.login.render(formFactory.form(Login.class)));
    }

    public CompletionStage<Result> login(final Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest(request);

        if (loginForm.hasErrors() || loginForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.login.render(loginForm)), ec.current());
        }

        Login login = loginForm.get();

        return repository.credentialsAreValid(login.getUsername(), login.getPassword()).thenApplyAsync(isValid -> {
            if (isValid){
                return redirect(routes.HomeController.home()).addingToSession(request, "loggedIn", login.getUsername());
            }
            return badRequest(views.html.login.render(loginForm.withError("Alert", "INVALID CREDENTIALS!")));
        }, ec.current());

    }

    public Result logoutPage() {
        return ok(views.html.logout.render());

    }

    public CompletionStage<Result> logout(final Http.Request request) {
        return CompletableFuture.supplyAsync(() ->
                redirect(routes.LoginController.index()).removingFromSession(request, "loggedIn"), ec.current());
    }
}
