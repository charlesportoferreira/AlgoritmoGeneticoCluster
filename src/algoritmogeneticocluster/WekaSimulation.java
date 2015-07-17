/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.misc.HyperPipes;
import weka.core.Instances;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class WekaSimulation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SMO classifier = new SMO();
        HyperPipes hy = new HyperPipes();
//        classifier.buildClassifier(trainset);

        BufferedReader datafile = readDataFile("tabela10.arff");

        Instances data;
        Evaluation eval;
        try {
            data = new Instances(datafile);
            data.setClassIndex(data.numAttributes() - 1);
            eval = new Evaluation(data);
            Random rand = new Random(1); // using seed = 1
            int folds = 10;
            eval.crossValidateModel(classifier, data, folds, rand);
            System.out.println(eval.toString());
            System.out.println(eval.numInstances());
            System.out.println(eval.correct());
            System.out.println(eval.incorrect());
            System.out.println(eval.pctCorrect());
            System.out.println(eval.pctIncorrect());

        } catch (Exception ex) {
            Logger.getLogger(WekaSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

}
