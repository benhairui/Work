package com.ic.connect;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class ES {
    private static TransportClient client = null;

    private static final String HOST = "192.168.199.231";
    private static final int PORT = 9300;
    private static final String CLUSTER_NAME="my-application";

    /**
     * 获得es的单例对象
     * @return
     * @throws Exception
     */
    public static TransportClient getSingleClient() throws Exception {

        Settings settings = Settings.builder()
                .put("client.transport.sniff", false)
                .put("cluster.name", CLUSTER_NAME).build();
        if (client == null) {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
        }

        return client;
    }



    public static void main(String[] args) throws Exception{


    }
}
