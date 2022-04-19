package learning.sentiment.learners;

import learning.classifiers.Knn;
import learning.core.Histogram;

public class Knn4 extends Knn<Histogram<String>,String>  {
    public Knn4() {
        super(4, Histogram::cosineDistance);
    }
}