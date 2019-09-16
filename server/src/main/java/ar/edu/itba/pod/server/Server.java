package ar.edu.itba.pod.server;

import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("tpe Server Starting ...");

        GenericServiceImpl elections = new GenericServiceImpl();

        Random rand = new Random();
        List<Party> parties = new ArrayList<>();
        parties.add(Party.BUFFALO);
        parties.add(Party.JACKALOPE);
        parties.add(Party.SNAKE);
        parties.add(Party.TURTLE);
        parties.add(Party.GORILLA);
        parties.add(Party.LEOPARD);
        parties.add(Party.LYNX);
        parties.add(Party.MONKEY);
        parties.add(Party.OWL);
        parties.add(Party.TIGER);
        parties.add(Party.WHITE_GORILLA);
        parties.add(Party.TARSIER);
        parties.add(Party.WHITE_TIGER);

        for (int i = 0; i< 100000; i++) {
            List<Party> vote1 = new ArrayList<>();
            vote1.add(parties.get(rand.nextInt(13)));
            vote1.add(parties.get(rand.nextInt(13)));
            vote1.add(parties.get(rand.nextInt(13)));
            try {
                if (!elections.addVote(1, ProvinceName.JUNGLE, vote1)) {
                    System.out.println("Error 1 en el voto");
                }
            } catch (IllegalVoteException e) {
                e.printStackTrace();
                System.out.println("Error 2 en el voto");
            }
        }

        AtomicReference<Double> sum = new AtomicReference<>(0.0);

        //System.out.println("\nFPTP SISTEM:\n");
        //elections.printResults(elections.queryResults());
        //elections.queryResults().forEach(res -> sum.updateAndGet(v -> v + res.right));
        //System.out.println("TOTAL = " + sum);
        //sum.updateAndGet(v -> v = 0.0);
        System.out.println("\nAV SISTEM:\n");
        elections.printResults(elections.getNationalResults());
        elections.getNationalResults().forEach(res -> sum.updateAndGet(v -> v + res.right));
        System.out.println("TOTAL = " + sum);
        System.out.println("\nSTV SISTEM:\n");
        try {
            elections.printResults(elections.getProvinceResults(ProvinceName.JUNGLE));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        elections.getNationalResults().forEach(res -> sum.updateAndGet(v -> v + res.right));
    }
}
