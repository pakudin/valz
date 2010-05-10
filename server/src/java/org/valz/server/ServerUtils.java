package org.valz.server;

import org.mortbay.jetty.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerUtils {

    public static List<String> portsToLocalAddresses(int... ports) {
        List<String> list = new ArrayList<String>();
        for (int item : ports) {
            list.add(String.format("http://localhost:%d", item));
        }
        return list;
    }

    public static List<InternalConfig> getServerConfigs(int delayForCaching, int... ports) {
        List<InternalConfig> listConfigs = new ArrayList<InternalConfig>();
        for (int port : ports) {
            listConfigs.add(ValzServer.getServerConfig("h2store" + port, port, delayForCaching));
        }
        return listConfigs;
    }

    public static List<Server> startServers(List<InternalConfig> listConfigs) throws Exception {
        List<Server> listServers = new ArrayList<Server>();
        for (InternalConfig config : listConfigs) {
            listServers.add(ValzServer.startServer(config));
        }
        return listServers;
    }

    public static void stopServers(List<Server> collection) {
        for (Server server : collection) {
            try {
                server.stop();
                server.join();
            } catch (Exception e) {
                // Ignore
            }
        }
    }


    private ServerUtils() {
    }
}
