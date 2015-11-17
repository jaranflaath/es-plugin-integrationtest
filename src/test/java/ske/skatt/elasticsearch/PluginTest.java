package ske.skatt.elasticsearch;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.test.ElasticsearchIntegrationTest.Scope.TEST;

@ClusterScope(scope = TEST, numDataNodes = 1)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class PluginTest extends ElasticsearchIntegrationTest {

    @Override protected Settings nodeSettings(int nodeOrdinal) {
        return settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("plugin.types", Plugin.class.getName())
                .put("force.http.enabled", true)
                .build();
    }

    @Test
    public void pluginShouldReturnClusterStatusGreen() throws Exception {

        ensureGreen();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:9501/cluster_status")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
        assertEquals("GREEN", response.body().string());

    }
}
