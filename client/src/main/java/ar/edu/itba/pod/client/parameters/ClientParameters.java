package ar.edu.itba.pod.client.parameters;

import org.jeasy.props.PropertiesInjectorBuilder;
import org.jeasy.props.annotations.SystemProperty;

public abstract class ClientParameters {
    @SystemProperty(value = "serverAddress")
    private String serverAddress;

    /*Package private*/
    ClientParameters() {
        PropertiesInjectorBuilder.aNewPropertiesInjector().injectProperties(this);
    }

    public final String getServerAddress() {
        return serverAddress;
    }

    public final void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    abstract void validate() throws Exception;
}
