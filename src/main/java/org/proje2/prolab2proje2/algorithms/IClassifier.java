package org.proje2.prolab2proje2.algorithms; 

import java.util.List;
import org.proje2.prolab2proje2.data.UserRecord;

public interface IClassifier {
    void trainModel(List<UserRecord> trainingDataset);

    String predictCategory(UserRecord targetRecord);

}