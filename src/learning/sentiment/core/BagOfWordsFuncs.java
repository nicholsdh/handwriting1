package learning.sentiment.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BagOfWordsFuncs {
    public static <K, N extends Number> double cosineDistance(Map<K,N> a, Map<K, N> b) {
        return 1.0 - cosineSimilarity(a, b);
    }

    public static <K, N extends Number> double cosineSimilarity(Map<K,N> a, Map<K, N> b) {
        return dotProduct(a, b) / (magnitude(a) * magnitude(b));
    }

    public static <K, N extends Number> double magnitude(Map<K,N> a) {
        return Math.sqrt(dotProduct(a, a));
    }

    public static <K, N extends Number> double dotProduct(Map<K, N> a, Map<K, N> b) {
        double result = 0.0;
        for (K key: keysFromBoth(a, b)) {
            result += a.get(key).doubleValue() * b.get(key).doubleValue();
        }
        return result;
    }

    public static <K, N extends Number> HashMap<K,Double> weightedAverage(Map<K,N> a, Map<K, N> b, double aWeight) {
        // TODO: Find the weighted average of Maps a and b. Multiply the value for each key in a with aWeight,
        //  then multiply the corresponding value from b by (1 - aWeight), and add them.
        //  Since N extends Number, you can use the Number.doubleValue() method to get a concrete value.
        HashMap<K, Double> average = new HashMap<>();
        // ignore this first loop
//        for (int i = 0; i < a.keySet().size(); i++) { // Shouldn't be a.size()... something with K or N? loop over a keys, look up value in b, if not present, val = 0. Loop through b, same thing. // a times aweight, b times 1 - aweight
//
//        }
        for (K key : a.keySet()) {
            if (b.containsKey(key)) {
                // Do I add both a and b values (with appropriate mult. w/ aweight?)
                // take count from a, mult by aweight, count from b and 1 - aweight, ADD THEM.
                double newa;
                double newb;
                newa = a.get(key).doubleValue() * aWeight;
                newb = b.get(key).doubleValue() * (1 - aWeight);
                average.put(key, (newa + newb));
            }
            else {
                // mult a count by aweight (bc zero from b)
                double newa = a.get(key).doubleValue() * aWeight;
                average.put(key, newa);
            }
        }
        for (K key : b.keySet()) {
            if (!a.containsKey(key)) {
                //take care of leftovers from b
                double newb = b.get(key).doubleValue() * (1 - aWeight);
                average.put(key, newb);
            }
        }
        return null;
    }

    public static <K, N> HashSet<K> allKeysFrom(Map<K,N> a, Map<K, N> b) {
        HashSet<K> all = new HashSet<>(a.keySet());
        all.addAll(b.keySet());
        return all;
    }

    public static <K, N> HashSet<K> keysFromBoth(Map<K, N> a, Map<K, N> b) {
        HashSet<K> both = new HashSet<>(a.keySet());
        both.retainAll(b.keySet());
        return both;
    }
}
