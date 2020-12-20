package ai.assignment.decisiontree;

import java.io.FileNotFoundException;
import java.io.IOException;

import ai.assignment.decisiontree.algorithm.InformationGain;


 public class App {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {

        InformationGain informationGain = new InformationGain();
        informationGain.generateDecisionTree();
        informationGain.printTree();
    }

}
