package ar.edu.itba.pod.comparators;

import ar.edu.itba.pod.util.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.Comparator;

public class CountComparator implements Comparator<MutablePair<Party, Double>>, Serializable {
    @Override
    public int compare(MutablePair<Party, Double> o1, MutablePair<Party, Double> o2) {

        int compare = Double.compare(o2.right, o1.right);
        if (compare == 0 && o1.left.getName().compareTo(o2.left.getName()) >= 0) {
            return 1;
        }
        return compare;
    }
}
