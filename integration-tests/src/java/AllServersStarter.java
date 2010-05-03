import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.server.ValzServerConfiguration;
import org.valz.test.ServerUtils;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.RoundRobinWriteBackend;
import org.valz.util.backends.WriteBackend;
import org.valz.viewer.ValzWebServer;

import java.util.ArrayList;
import java.util.List;

public class AllServersStarter {
    private static final Logger log = Logger.getLogger(AllServersStarter.class);


    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        int[] ports = {8800, 8801};
        List<ValzServerConfiguration> listConfigs = ServerUtils.getServerConfigs(ports);
        List<Server> listServers = ServerUtils.startServers(listConfigs);

        Server valzWebServer = ValzWebServer
                .startServer(ValzWebServer.getWebServerConfiguration(8900, ServerUtils.portsToLocalAddresses(ports)));


        // init client
        {
            List<WriteBackend> listWriteBackends = new ArrayList<WriteBackend>();
            for (ValzServerConfiguration config : listConfigs) {
                listWriteBackends.add(config.writeBackend);
            }
            WriteBackend clientWriteBackend = new RoundRobinWriteBackend(listWriteBackends);
            Valz.init(clientWriteBackend);
        }

        // send data
        {
            String name = "counter" + Math.random();
            Val<Long> counter = Valz.register(name, new LongSum());

            final int SUBMITS_COUNT = 100;
            for (int i = 0; i < SUBMITS_COUNT; i++) {
                counter.submit(1L);
            }
        }

        try {
            valzWebServer.join();
        } catch (InterruptedException e) {
            log.error("Could not stop valz web server", e);
        }
        ServerUtils.stopServers(listServers);
    }

    private AllServersStarter() {
    }
}
