package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.parameters.VoteClientParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteClient {
    private static Logger logger = LoggerFactory.getLogger(VoteClient.class);

    public static void main(String[] args)  {
        final VoteClientParameters parameters = new VoteClientParameters();
        try {
            parameters.validate();
        } catch (Exception e) {
            logger.error("invalid params");
        }
    }
}
