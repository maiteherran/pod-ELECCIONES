package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class GenericServiceConcurrencyTest {

    private static final int VOTES_COUNT = 1000000;
    private static final int VOTES_COUNT_GORILLA = 300000; /* 30% */
    private static final int VOTES_COUNT_LEOPARD = 210000; /* 21% */
    private static final int VOTES_COUNT_TURTLE = 160000; /* 16% */
    private static final int VOTES_COUNT_OWL = 11000; /* 11% */
    private static final int VOTES_COUNT_TIGER = 100000; /* 10% */
    private static final int VOTES_COUNT_TARSIER = 50000; /* 5% */
    private static final int VOTES_COUNT_MONKEY = 25000; /* 2,5% */
    private static final int VOTES_COUNT_LYNX = 24000; /* 2,4% */
    private static final int VOTES_COUNT_WHITE_TIGER = 8700; /* 0,87% */
    private static final int VOTES_COUNT_WHITE_GORILLA = 6200; /* 0,62% */
    private static final int VOTES_COUNT_SNAKE = 5000; /* 0,5% */
    private static final int VOTES_COUNT_JACKALOPE = 1000; /* 0,1% */
    private static final int VOTES_COUNT_BUFFALO = 100; /* 0,01% */
    private static final int THREAD_COUNT = 1;

    /* debería ser GenericService */
    private GenericServiceImpl genericService;

    private final Map<Party, Integer> partyCounts = new HashMap<>();

    private final ArrayList<ProvinceName> provinceNames = new ArrayList<>();

    @Before
    public final void before () {
        genericService = new GenericServiceImpl();
        partyCounts.put(Party.GORILLA, VOTES_COUNT_GORILLA);
        partyCounts.put(Party.LEOPARD, VOTES_COUNT_LEOPARD);
        partyCounts.put(Party.TURTLE, VOTES_COUNT_TURTLE);
        partyCounts.put(Party.OWL, VOTES_COUNT_OWL);
        partyCounts.put(Party.TIGER, VOTES_COUNT_TIGER);
        partyCounts.put(Party.TARSIER, VOTES_COUNT_TARSIER);
        partyCounts.put(Party.MONKEY, VOTES_COUNT_MONKEY);
        partyCounts.put(Party.LYNX, VOTES_COUNT_LYNX);
        partyCounts.put(Party.WHITE_TIGER, VOTES_COUNT_WHITE_TIGER);
        partyCounts.put(Party.WHITE_GORILLA, VOTES_COUNT_WHITE_GORILLA);
        partyCounts.put(Party.SNAKE, VOTES_COUNT_SNAKE);
        partyCounts.put(Party.JACKALOPE, VOTES_COUNT_JACKALOPE);
        partyCounts.put(Party.BUFFALO, VOTES_COUNT_BUFFALO);
        provinceNames.add(ProvinceName.JUNGLE);
        provinceNames.add(ProvinceName.SAVANNAH);
        provinceNames.add(ProvinceName.TUNDRA);

    }

    private final Runnable election = () -> {

        Random rand = new Random();

        partyCounts.forEach( (party, count) -> {

            ArrayList<Party> vote = new ArrayList<>();
            vote.add(party);
            for (int i = 0; i < count/THREAD_COUNT; i++) {
                try {
                    genericService.addVote(rand.nextInt(1000), provinceNames.get(rand.nextInt(2)), vote);
                } catch (IllegalVoteException e) {
                    System.out.println(e.toString());
                }
            }
        });
    };

    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    @Test
    public final void countingFPTPVotes () throws InterruptedException {

        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(election, "thread" + i);
            threads[i].run();
        }
        for (int j = 0; j < THREAD_COUNT; j++) {
            threads[j].join();
        }

        TreeSet<MutablePair<Party, Double>> results = genericService.queryResults();
        results.forEach(pair -> {
            //Assert.assertEquals(pair.right, (double) partyCounts.get(pair.left) / (double) VOTES_COUNT, 0.001);
        });

        genericService.printResults(results);

    }

}
