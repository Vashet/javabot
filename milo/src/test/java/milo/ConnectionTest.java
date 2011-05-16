package milo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.testng.annotations.Test;

@Test
public class ConnectionTest {
    private int port = 6667;

    public void connect() throws IOException, ExecutionException, TimeoutException, InterruptedException {
        final FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new IrcFilter());

        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
        transport.setProcessor(filterChainBuilder.build());

        transport.bind(port);
        transport.start();

        final Future<Connection> future = transport.connect("irc.freenode.net", port);
        final Connection connection = future.get(30, TimeUnit.SECONDS);

        Thread.sleep(10000);
    }
}
