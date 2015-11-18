package ske.skatt.elasticsearch;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class PluginRestEndpoint extends BaseRestHandler {
    private static final Logger logger = LoggerFactory.getLogger(PluginRestEndpoint.class);
    public static final String PATH = "/cluster_status";

    private Client client;

    @Inject
    public PluginRestEndpoint(Settings settings, RestController restController, Client client) {
        super(settings, restController, client);
        this.client = client;
        restController.registerHandler(GET, PATH, this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) throws Exception {

        Future<ClusterHealthResponse> future = Executors.newSingleThreadExecutor()
                .submit(new Callable<ClusterHealthResponse>() {
                    public ClusterHealthResponse call() throws Exception {
                        try {
                            return client.admin().cluster().prepareHealth().execute().get();
                        } catch (Exception e) {
                            logger.error("Failed to get cluster status.", e);
                            return null;
                        }
                    }
                });

        while(!future.isDone()) {
            Thread.sleep(10);
        }
        channel.sendResponse(new BytesRestResponse(OK, future.get().getStatus().name()));
    }
}
