package ske.skatt.elasticsearch;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class PluginRestEndpoint extends BaseRestHandler {
    private static final Logger logger = LoggerFactory.getLogger(PluginRestEndpoint.class);
    public static final String PATH = "/cluster_status";

    private Client client;

    @Inject
    public PluginRestEndpoint(Settings settings, RestController restController, Client client){
        super(settings, restController, client);
        this.client = client;
        restController.registerHandler(GET, PATH, this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, Client client) {
        String response = null;
        try {
            ClusterHealthResponse clusterIndexHealths = client.admin().cluster().prepareHealth().execute().get();
            response = clusterIndexHealths.getStatus().name();
        } catch (Exception e) {
            logger.error("Failed to get cluster status.", e);
            response = e.getMessage();
        }
        channel.sendResponse(new BytesRestResponse(OK, response));
    }
}
