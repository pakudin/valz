package org.valz.examples;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.server.InternalConfig;
import org.valz.server.ServerUtils;
import org.valz.model.LongSum;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;
import org.valz.viewer.ValzWebServer;
import org.valz.viewer.ViewerConfig;
import org.valz.viewer.ViewerInternalConfig;

import java.util.ArrayList;
import java.util.List;

public class OldTutorial {
    private static final Logger log = Logger.getLogger(OldTutorial.class);


    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        int[] ports = {8800, 8801};
        int delayForCaching = 100;
        int chunkSize = 100;
        List<InternalConfig> configs = ServerUtils.getServerConfigs(delayForCaching, chunkSize, ports);
        List<Server> servers = ServerUtils.startServers(configs);

        Server valzWebServer = ValzWebServer
                .startServer(8900, ViewerInternalConfig.getConfig(ViewerConfig.read().urls, 0));


        // init client
        {
            List<WriteBackend> listWriteBackends = new ArrayList<WriteBackend>();
            for (InternalConfig config : configs) {
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
        ServerUtils.stopServers(servers);
    }

    private OldTutorial() {
    }
}