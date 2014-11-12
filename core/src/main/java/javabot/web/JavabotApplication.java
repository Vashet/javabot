package javabot.web;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.oauth.OAuthProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import javabot.JavabotModule;
import org.eclipse.jetty.server.session.SessionHandler;
import org.mongodb.morphia.Datastore;

public class JavabotApplication extends Application<JavabotConfiguration> {
    @Inject
    private Datastore ds;

    @Inject
    private Injector injector;

    @Override
    public void initialize(final Bootstrap<JavabotConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets", null, "assets"));
        bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars", "/webjars", null, "webjars"));
    }

    @Override
    public void run(final JavabotConfiguration configuration, final Environment environment) throws Exception {
        environment.getApplicationContext().setSessionsEnabled(true);
        environment.getApplicationContext().setSessionHandler(new SessionHandler());

        environment.jersey().register(injector.getInstance(BotResource.class));
        environment.jersey().register(injector.getInstance(AdminResource.class));
        OAuthProvider<Object> provider = new OAuthProvider<>(JavabotApplication.this::authenticateUser, "realm");
        environment.jersey().getResourceConfig().getSingletons().add(provider);

        environment.healthChecks().register("javabot", new JavabotHealthCheck());
    }

    private Optional<Object> authenticateUser(final String credentials) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        Guice.createInjector(new JavabotModule())
             .getInstance(JavabotApplication.class)
             .run(new String[]{"server", "javabot.yml"});
    }

}
