package ske.skatt.elasticsearch;

import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;

public class Plugin extends AbstractPlugin {
    public String name() {
        return "Plugin";
    }

    public String description() {
        return "Returns cluster status";
    }

    public void onModule(final RestModule module) {
        module.addRestAction(PluginRestEndpoint.class);
    }

}
