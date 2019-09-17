package ar.edu.itba.pod.server;

import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
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
    private static final int VOTES_COUNT_GORILLA_1 = 300000; /* 30% */
    private static final int VOTES_COUNT_LEOPARD_1 = 170000; /* 21% */
    private static final int VOTES_COUNT_TURTLE_1 = 160000; /* 16% */
    private static final int VOTES_COUNT_OWL_1 = 130000; /* 11% */
    private static final int VOTES_COUNT_TIGER_1 = 100000; /* 10% */
    private static final int VOTES_COUNT_TARSIER_1 = 70000; /* 5% */
    private static final int VOTES_COUNT_MONKEY_1 = 25000; /* 2,5% */
    private static final int VOTES_COUNT_LYNX_1 = 24000; /* 2,4% */
    private static final int VOTES_COUNT_WHITE_TIGER_1 = 8700; /* 0,87% */
    private static final int VOTES_COUNT_WHITE_GORILLA_1 = 6200; /* 0,62% */
    private static final int VOTES_COUNT_SNAKE_1 = 5000; /* 0,5% */
    private static final int VOTES_COUNT_JACKALOPE_1 = 1000; /* 0,1% */
    private static final int VOTES_COUNT_BUFFALO_1 = 100; /* 0,01% */
    private static final int VOTES_COUNT_LEOPARD_2 = 630000; /* 63% */
    private static final int VOTES_COUNT_OWL_2 = 300000; /* 30% */
    private static final int THREAD_COUNT = 10;
    private static final double LAMBDA = 0.001;

    /* debería ser GenericService */
    private GenericServiceImpl genericService;

    private final Map<Party, Integer> partyCounts = new HashMap<>();

    private final ArrayList<ProvinceName> provinceNames = new ArrayList<>();

    private final ArrayList<Party> parties = new ArrayList<>();

    private final Runnable election = () -> {
        System.out.println("Entro thread");

        Random rand = new Random();
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
                vote.add(party);

                switch (party) {
                    case OWL:
                        vote.add(Party.TURTLE);
                        vote.add(Party.LEOPARD);
                        break;
                    case LYNX:
                        vote.add(Party.MONKEY);
                        vote.add(Party.TARSIER);
                        break;
                    case LEOPARD:
                        vote.add(Party.GORILLA);
                        vote.add(Party.TURTLE);
                        break;
                    case BUFFALO:
                        vote.add(Party.JACKALOPE);
                        vote.add(Party.SNAKE);
                        break;
                    case SNAKE:
                        vote.add(Party.WHITE_GORILLA);
                        vote.add(Party.WHITE_TIGER);
                        break;
                    case MONKEY:
                        vote.add(Party.TARSIER);
                        vote.add(Party.TIGER);
                        break;
                    case GORILLA:
                        vote.add(Party.LEOPARD);
                        vote.add(Party.TURTLE);
                        break;
                    case TARSIER:
                        vote.add(Party.TIGER);
                        vote.add(Party.OWL);
                        break;
                    case JACKALOPE:
                        vote.add(Party.SNAKE);
                        vote.add(Party.WHITE_GORILLA);
                        break;
                    case WHITE_TIGER:
                        vote.add(Party.LYNX);
                        vote.add(Party.MONKEY);
                        break;
                    case WHITE_GORILLA:
                        vote.add(Party.WHITE_TIGER);
                        vote.add(Party.LYNX);
                        break;
                    case TIGER:
                        vote.add(Party.OWL);
                        vote.add(Party.TURTLE);
                        break;
                    case TURTLE:
                        vote.add(Party.LEOPARD);
                        vote.add(Party.GORILLA);
                        break;
                }

                try {
                    genericService.addVote(pollingStation, provinceName, vote);
                } catch (IllegalVoteException e) {
                    e.printStackTrace();
                }
            }

        });

        System.out.println("Salgo thread");
    };

    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    @Before
    public final void before () {
        genericService = new GenericServiceImpl();
        partyCounts.put(Party.GORILLA, VOTES_COUNT_GORILLA_1);
        partyCounts.put(Party.LEOPARD, VOTES_COUNT_LEOPARD_1);
        partyCounts.put(Party.TURTLE, VOTES_COUNT_TURTLE_1);
        partyCounts.put(Party.OWL, VOTES_COUNT_OWL_1);
        partyCounts.put(Party.TIGER, VOTES_COUNT_TIGER_1);
        partyCounts.put(Party.TARSIER, VOTES_COUNT_TARSIER_1);
        partyCounts.put(Party.MONKEY, VOTES_COUNT_MONKEY_1);
        partyCounts.put(Party.LYNX, VOTES_COUNT_LYNX_1);
        partyCounts.put(Party.WHITE_TIGER, VOTES_COUNT_WHITE_TIGER_1);
        partyCounts.put(Party.WHITE_GORILLA, VOTES_COUNT_WHITE_GORILLA_1);
        partyCounts.put(Party.SNAKE, VOTES_COUNT_SNAKE_1);
        partyCounts.put(Party.JACKALOPE, VOTES_COUNT_JACKALOPE_1);
        partyCounts.put(Party.BUFFALO, VOTES_COUNT_BUFFALO_1);
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

    @Test
    public final void countingFPTPVotes () throws InterruptedException {

        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(election, "thread" + i);
            threads[i].start();
        }
        for (int j = 0; j < THREAD_COUNT; j++) {
            threads[j].join();
        }

        TreeSet<MutablePair<Party, Double>> results = genericService.queryResults();
        results.forEach(pair -> {
            assertEquals(pair.right, (double) partyCounts.get(pair.left) / (double) VOTES_COUNT, LAMBDA);
        });
    }

    @Test
    public final void countingAVVotes () throws InterruptedException {

        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(election, "thread" + i);
            threads[i].start();
        }
        for (int j = 0; j < THREAD_COUNT; j++) {
            threads[j].join();
        }
        TreeSet<MutablePair<Party, Double>> results = genericService.getNationalResults();
        results.forEach(pair -> {
            if (pair.getLeft().equals(Party.OWL)) {
                assertEquals(pair.right, (double) VOTES_COUNT_OWL_2 / (double) VOTES_COUNT, LAMBDA);
            } else {
                assertEquals(pair.right, (double) VOTES_COUNT_LEOPARD_2 / (double) VOTES_COUNT, LAMBDA);
            }
        });
    }

    @Test
    public final void countingSTVVotes () throws InterruptedException {

        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(election, "thread" + i);
            threads[i].start();
        }
        for (int j = 0; j < THREAD_COUNT; j++) {
            threads[j].join();
        }

        List<Province> provinces = new ArrayList<>();
        provinceNames.forEach(p -> {
            try {
                provinces.add(genericService.getProvince(p));
            } catch (NoSuchProvinceException e) {
                e.printStackTrace();
            }
        });
        provinces.forEach( p -> {
            TreeSet<MutablePair<Party, Double>> results = p.getResultsSTV();
            try {
                this.debbuging(results);
            } catch (NoSuchProvinceException e) {
                e.printStackTrace();
            }
        });
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
        /*resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });*/
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
        /*resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });*/
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
        /*resultsMap.forEach( (party, votes) -> {
            System.out.println("PARTY: " + party + " COUNT: " + votes);
        });*/
        System.out.println("Number of votes -> " + acum + "\n");

        genericService.printResults(results);
    }
}
