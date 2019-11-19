import forms.Login;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ActionCreator implements play.http.ActionCreator {

    private final FormFactory formFactory;
    private final HttpExecutionContext ec;


    @Inject
    public ActionCreator(FormFactory formFactory, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.ec = ec;
    }

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {

        List<String> publics = new ArrayList<>(Arrays.asList("/signUp", "/login", "/logout"));

        return new Action.Simple() {

            CompletionStage<Result> getRoute(Http.Request req){
                return req.getHeaders().get("Raw-Request-URI")
                        // Does the requested page require authentication?
                        .filter(uri -> publics.contains(uri) ||
                                // Is the user logged in?
                                req.session().getOptional("loggedIn").isPresent())
                        // If either the user is logged or the page does not require authentication then forward the request.
                        .map(uri -> delegate.call(req))
                        // Otherwise send the user to the login page.
                        .orElseGet(() ->
                                CompletableFuture
                                        .supplyAsync(() ->
                                                ok(views.html.old.login.render(formFactory.form(Login.class))), ec.current()));
            }


            @Override
            public CompletionStage<Result> call(Http.Request req) {
                return getRoute(req);
//                try{
//                    return getRoute(req);
//                }
//                catch (Exception e){
//                    System.out.println("HERER");
//                }
//
//                return CompletableFuture
//                        .supplyAsync(() ->
//                                ok(views.html.old.login.render(formFactory.form(Login.class))), ec.current());
            }
        };

        /// Make a Logger here that shows the request parameters.
    }
}