package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.util.QueryClientParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);

    public static void main(String[] args)  {
        final QueryClientParameters parameters = new QueryClientParameters();
        try {
            parameters.validate();
        } catch (Exception e) {
            logger.error("invalid params");
        }
    }
}
