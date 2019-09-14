package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.util.ManagementClientParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementClient {
    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);

    public static void main(String[] args)  {
        final ManagementClientParameters parameters = new ManagementClientParameters();
        try {
            parameters.validate();
        } catch (Exception e) {
            logger.error("invalid params");
        }
    }
}
