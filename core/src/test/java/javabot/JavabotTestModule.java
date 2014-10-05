package javabot;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import javax.inject.Named;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JavabotTestModule extends JavabotModule {
    private Provider<TestJavabot> botProvider;

    @Override
    protected void configure() {
        botProvider = binder().getProvider(TestJavabot.class);
        super.configure();
    }

    @Override
    public Properties getProperties() throws IOException {
        return loadProperties("test-javabot.properties");
    }

    @Provides
    @Singleton
    public Javabot getJavabot() {
        TestJavabot testJavabot = botProvider.get();
        testJavabot.start();
        return testJavabot;
    }
}
