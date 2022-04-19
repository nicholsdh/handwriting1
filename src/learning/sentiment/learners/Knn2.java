package learning.sentiment.learners;

import learning.classifiers.Knn;
import learning.core.Histogram;

public class Knn2 extends Knn<Histogram<String>,String>  {
    public Knn2() {
        super(2, Histogram::cosineDistance);
    }
}