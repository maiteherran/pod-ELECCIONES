package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import ar.edu.itba.pod.server.exceptions.NoSuchProvinceException;
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
    private static final int VOTES_COUNT_OWL = 110000; /* 11% */
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

    private final ArrayList<Party> parties = new ArrayList<>();

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
        parties.add(Party.JACKALOPE);
        parties.add(Party.GORILLA);
        parties.add(Party.LYNX);
        parties.add(Party.LEOPARD);
        parties.add(Party.OWL);
        parties.add(Party.BUFFALO);
        parties.add(Party.TARSIER);
        parties.add(Party.TIGER);
        parties.add(Party.TURTLE);
        parties.add(Party.WHITE_GORILLA);
        parties.add(Party.WHITE_TIGER);
        parties.add(Party.SNAKE);
        parties.add(Party.MONKEY);

    }

    private final Runnable election = () -> {

        Random rand = new Random();
        int numberOfVotes = VOTES_COUNT;

        /*
        Para FPTP:
        partyCounts.forEach( (party, count) -> {

            ArrayList<Party> vote = new ArrayList<>();
            vote.add(party);
            int i;
            for (i = 0; i < count/THREAD_COUNT; i++) {
                try {
                    genericService.addVote(rand.nextInt(1000), provinceNames.get(rand.nextInt(3)), vote);
                } catch (IllegalVoteException e) {
                    System.out.println(e.toString());
                }
            }
        });*/
        partyCounts.forEach( (party, count) -> {

            for (int i=0; i < count; i++) {

                int pollingStation = rand.nextInt(1000);
                ProvinceName provinceName = provinceNames.get(rand.nextInt(3));
                ArrayList<Party> vote = new ArrayList<>();
                int randInt = rand.nextInt(3);
                vote.add(party);

                switch (randInt) {
                    case 0:
                        break;
                    case 1:
                        vote.add(parties.get(rand.nextInt(13)));
                        break;
                    case 2:
                        vote.add(parties.get(rand.nextInt(13)));
                        vote.add(parties.get(rand.nextInt(13)));
                        break;
                }

                try {
                    genericService.addVote(pollingStation, provinceName, vote);
                } catch (IllegalVoteException e) {
                    e.printStackTrace();
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
            assertEquals(pair.right, (double) partyCounts.get(pair.left) / (double) VOTES_COUNT, 0.001);
        });
        /*try {
            this.debbuging(results);
        } catch (NoSuchProvinceException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public final void countingAVVotes () throws InterruptedException {

        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(election, "thread" + i);
            threads[i].run();
        }
        for (int j = 0; j < THREAD_COUNT; j++) {
            threads[j].join();
        }
        TreeSet<MutablePair<Party, Double>> results = genericService.getNationalResults();
        results.forEach(pair -> {
            //assertEquals(pair.right, (double) partyCounts.get(pair.left) / (double) VOTES_COUNT, 0.001);
        });
        try {
            this.debbuging(results);
        } catch (NoSuchProvinceException e) {
            e.printStackTrace();
        }
    }

    private void debbuging(TreeSet<MutablePair<Party, Double>> results) throws NoSuchProvinceException {

        Province jungle = genericService.getProvince(ProvinceName.JUNGLE);
        Province tundra = genericService.getProvince(ProvinceName.TUNDRA);
        Province savannah = genericService.getProvince(ProvinceName.SAVANNAH);
        AtomicReference<Integer> acum = new AtomicReference<>(0);
        Map<Party, Integer> resultsMap = new HashMap<>();
        resultsMap.put(Party.GORILLA, 0);
        resultsMap.put(Party.LEOPARD, 0);
        resultsMap.put(Party.TURTLE, 0);
        resultsMap.put(Party.OWL, 0);
        resultsMap.put(Party.TIGER, 0);
        resultsMap.put(Party.TARSIER, 0);
        resultsMap.put(Party.MONKEY, 0);
        resultsMap.put(Party.LYNX, 0);
        resultsMap.put(Party.WHITE_TIGER, 0);
        resultsMap.put(Party.WHITE_GORILLA, 0);
        resultsMap.put(Party.SNAKE, 0);
        resultsMap.put(Party.JACKALOPE, 0);
        resultsMap.put(Party.BUFFALO, 0);

        System.out.println("\nPROVINCE: JUNGLE\n");
        jungle.getPollingStations().forEach(st -> {
            acum.updateAndGet(v -> v + st.getNumberOfVotes());
            st.getVotes().forEach(vote -> {
                int acum1 = resultsMap.get(vote.getFirstChoice());
                resultsMap.put(vote.getFirstChoice(), acum1 + 1);
            });
        });
        resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });
        System.out.println("Number of votes -> " + acum);

        acum.set(0);
        resultsMap.put(Party.GORILLA, 0);
        resultsMap.put(Party.LEOPARD, 0);
        resultsMap.put(Party.TURTLE, 0);
        resultsMap.put(Party.OWL, 0);
        resultsMap.put(Party.TIGER, 0);
        resultsMap.put(Party.TARSIER, 0);
        resultsMap.put(Party.MONKEY, 0);
        resultsMap.put(Party.LYNX, 0);
        resultsMap.put(Party.WHITE_TIGER, 0);
        resultsMap.put(Party.WHITE_GORILLA, 0);
        resultsMap.put(Party.SNAKE, 0);
        resultsMap.put(Party.JACKALOPE, 0);
        resultsMap.put(Party.BUFFALO, 0);

        System.out.println("\nPROVINCE: TUNDRA\n");
        tundra.getPollingStations().forEach(st -> {
            acum.updateAndGet(v -> v + st.getNumberOfVotes());
            st.getVotes().forEach(vote -> {
                int acum1 = resultsMap.get(vote.getFirstChoice());
                resultsMap.put(vote.getFirstChoice(), acum1 + 1);
            });
        });
        resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });
        System.out.println("Number of votes -> " + acum);

        acum.set(0);
        resultsMap.put(Party.GORILLA, 0);
        resultsMap.put(Party.LEOPARD, 0);
        resultsMap.put(Party.TURTLE, 0);
        resultsMap.put(Party.OWL, 0);
        resultsMap.put(Party.TIGER, 0);
        resultsMap.put(Party.TARSIER, 0);
        resultsMap.put(Party.MONKEY, 0);
        resultsMap.put(Party.LYNX, 0);
        resultsMap.put(Party.WHITE_TIGER, 0);
        resultsMap.put(Party.WHITE_GORILLA, 0);
        resultsMap.put(Party.SNAKE, 0);
        resultsMap.put(Party.JACKALOPE, 0);
        resultsMap.put(Party.BUFFALO, 0);

        System.out.println("\nPROVINCE: SAVANNAH\n");
        savannah.getPollingStations().forEach(st -> {
            acum.updateAndGet(v -> v + st.getNumberOfVotes());
            st.getVotes().forEach(vote -> {
                int acum1 = resultsMap.get(vote.getFirstChoice());
                resultsMap.put(vote.getFirstChoice(), acum1 + 1);
            });
        });
        resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });
        System.out.println("Number of votes -> " + acum + "\n");

        genericService.printResults(results);
    }
}
