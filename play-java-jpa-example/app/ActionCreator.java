import controllers.routes;
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

        List<String> loginSignUp = new ArrayList<>(Arrays.asList("/signUp", "/", "/login"));

        return new Action.Simple() {

            CompletionStage<Result> getRoute(Http.Request req) {
                String uri = req.getHeaders().get("Raw-Request-URI").get();

                // If the user is logged in..
                if (req.session().getOptional("loggedIn").isPresent()) {
                    // If the url points to either login or signup then redirect the user to the home page
                    if (loginSignUp.contains(uri)) {
                        return CompletableFuture
                                .supplyAsync(() ->
                                        redirect(routes.HomeController.home()), ec.current());
                    }
                    // Otherwise pass the request to the relevant controller
                    return delegate.call(req);
                }

                // If the user is not logged in then let access the login, signup and logout pages
                if (uri.equals("/logout") || loginSignUp.contains(uri)){
                    return delegate.call(req);
                }

                // Otherwise redirect to the login page.
                return CompletableFuture
                        .supplyAsync(() ->
                                redirect(routes.LoginController.index()), ec.current());
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