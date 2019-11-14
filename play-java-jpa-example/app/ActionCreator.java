import models.Login;
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

        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Request req) {
                List<String> publics = new ArrayList<>(Arrays.asList("/signUp", "/login", "/logout"));
                return req.getHeaders().get("Raw-Request-URI").filter(publics::contains)
                        .map(uri -> delegate.call(req))
                        .orElseGet(() -> req.session().getOptional("loggedIn")
                                .map(x -> delegate.call(req))
                                .orElseGet(() ->
                                        CompletableFuture
                                                .supplyAsync(() ->
                                                        ok(views.html.login.render(formFactory.form(Login.class))), ec.current()))
                        );
            }
        };
    }
}