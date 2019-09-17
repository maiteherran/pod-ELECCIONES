package ar.edu.itba.pod.client.parameters;
import ar.edu.itba.pod.client.util.ManagementClientAction;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class ManagementClientParameters extends ClientParameters {
    @SystemProperty(value = "action")
    private String action;
    private ManagementClientAction managementAction;

    public ManagementClientParameters() {
        super();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ManagementClientAction getManagementAction() {
        return managementAction;
    }

    public void validate() throws IllegalArgumentException {
        Properties properties = System.getProperties();

        if(!properties.containsKey("serverAddress") || !properties.containsKey("action") ||
                (!properties.getProperty("action").equals("open") &&
                !properties.getProperty("action").equals("state") &&
                !properties.getProperty("action").equals("close"))) {

            System.out.println("Invalid program arguments.\n" +
                    "Here's an example of how you should execute the ManagementClient from command line: \n" +
            "$> java -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName ar.edu.itba.pod.client.ManagementClient\n" +
                    "where, \n" +
                    "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                    "- actionName: name of the action to perform:\n" +
                            "       o open: \n" +
                            "       o state: \n" +
                            "       o close: \n"
                    );
            throw new IllegalArgumentException();
        }
        managementAction = ManagementClientAction.valueOf(action.toUpperCase());
    }
}
