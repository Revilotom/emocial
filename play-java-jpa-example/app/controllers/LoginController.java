package controllers;

import forms.Login;
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


public class LoginController extends DefaultController {

    @Inject
    public LoginController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result index() {
        return ok(views.html.old.login.render(formFactory.form(Login.class)));
    }

    public CompletionStage<Result> submitLogin(final Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest(request);

//        loginForm.field()

        if (loginForm.hasErrors() || loginForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.old.login.render(loginForm)), ec.current());
        }

        Login login = loginForm.get();

        return repository.credentialsAreValid(login.getUsername(), login.getPassword()).thenApplyAsync(isValid -> {
            if (isValid){
                return redirect(routes.HomeController.home()).addingToSession(request, "loggedIn", login.getUsername());
            }

            String invalidCredentials = "INVALID CREDENTIALS!";

            return badRequest(
                    views.html.old.login.render(loginForm
                        .withError("username", invalidCredentials)
                        .withError("password", invalidCredentials)
                    )
            );
        }, ec.current());
    }
}
