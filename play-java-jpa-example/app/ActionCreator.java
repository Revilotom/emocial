import controllers.routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ActionCreator implements play.http.ActionCreator {

    private final FormFactory formFactory;
    private final HttpExecutionContext ec;

    private static final Logger log = LoggerFactory.getLogger(ActionCreator.class);


    @Inject
    public ActionCreator(FormFactory formFactory, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.ec = ec;
    }

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {

        List<String> loginSignUp = new ArrayList<>(Arrays.asList("/signUp", "/", "/login"));

        return new Action.Simple() {

            CompletionStage<Result> getResult(Http.Request req) {

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
                if (uri.equals("/logout") || loginSignUp.contains(uri)) {
                    return delegate.call(req);
                }

                // Otherwise redirect to the login page.
                return CompletableFuture
                        .supplyAsync(() ->
                                redirect(routes.LoginController.index()), ec.current());
            }


            public String formatMap(Map<String, ?> m){

                return "\n{\n     " +
                        m.entrySet().stream().map(x -> x.getKey() + " : " + x.getValue())
                        .collect(Collectors.joining("\n     ")) +
                        "\n}";
            }

            @Override
            public CompletionStage<Result> call(Http.Request req) {

                Map<String, String[]> bodyMap = request.body().asFormUrlEncoded();

                Map<String, String> newMap = bodyMap == null ? null : bodyMap.entrySet().stream().map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(), Arrays.toString(entry.getValue())))
                        .filter(x -> !x.getKey().equals("csrfToken"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                try {
                    Result r = getResult(req).toCompletableFuture().get();
                    return CompletableFuture.supplyAsync(() -> r);

                } catch (Exception e) {
                    log.error(
                            "{} {} {} {}",
                            request.method(),
                            request.uri(),
                            newMap != null ? formatMap(newMap) : "",
                            formatMap(request.getHeaders().toMap())
                    );
                    return getResult(req);
                }
            }
        };
    }
}