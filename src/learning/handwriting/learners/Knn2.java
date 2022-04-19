package learning.handwriting.learners;

import learning.classifiers.Knn;
import learning.handwriting.core.Drawing;

public class Knn2 extends Knn<Drawing,String> {
    public Knn2() {
        super(2, (d1, d2) -> (double)d1.distance(d2));
    }
}
