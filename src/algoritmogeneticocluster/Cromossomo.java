/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import static algoritmogeneticocluster.WekaSimulation.readDataFile;
import convertclustertotav.ConvertClusterToTAV;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.misc.HyperPipes;
import weka.core.Instances;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cromossomo implements Callable<String> {

    private List<Gene> genes;
    private double fitness;
    private double probSelecao;
    private static Long id = 0L;
    private final Long inId;
    private double pctAcerto;
    private double microAverage;
    private double macroAverage;
    public static int PCT_ACERTOS = 1;
    public static int MICRO_AVERAGE = 2;
    public static int MACRO_AVERAGE = 3;
    private int metodoFitness;

    public Cromossomo(int nrBits, int metodoFitness) {
        inId = id;
        id++;
        genes = new ArrayList<>(nrBits);
        criaGenes(nrBits);
        this.metodoFitness = metodoFitness;
//        System.out.println("Meu ID: " + this.inId);
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public int countGenesSelecionados() {
        int count = 0;
        for (Gene gene : genes) {
            if (gene.getValor() == 0) {
                count++;
            }
        }
        return count;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        if (fitness == 0) {
            calculaFitness();
        }
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    private void criaGenes(int nrGenes) {
        Random random = new Random();
        int valor;
        for (int i = 0; i < nrGenes; i++) {
            valor = random.nextInt(1 - 0 + 1) + 0;
            genes.add(new Gene(valor));
        }
    }

    private void calculaFitness() {

        int[] decodicacao = decodificaCromossomo();
        criaArff(decodicacao);
        classifica();
        limparDados();
    }

    public double getProbSelecao() {
        return probSelecao;
    }

    public void setProbSelecao(double probSelecao) {
        this.probSelecao = probSelecao;
    }

    public void simulaFitness() {
        Random random = new Random();
        this.fitness = random.nextInt(100 - 0 + 1) + 0;
    }

    @Override
    public String call() throws Exception {
        getFitness();
        return String.valueOf("Meu fitness: " + fitness);
    }

    private int[] decodificaCromossomo() {
        int pos = 0;
        ArrayList<Integer> listDecodificacao = new ArrayList<>();
        for (int i = genes.size() - 1; i >= 0; i--) {
            if (genes.get(i).getValor() == 1) {
                listDecodificacao.add(i);
            }
        }
        int i = 0;
        int[] array = new int[listDecodificacao.size()];
        for (int decodicacao : listDecodificacao) {
            array[i] = decodicacao;
            i++;
        }

        return array;
    }

    private void criaArff(int[] decodificacao) {
        ConvertClusterToTAV cc = new ConvertClusterToTAV();
        try {
            cc.convert("cluster.txt", inId + ".arff", decodificacao);
        } catch (IOException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void classifica() {
        //SMO classifier = new SMO();
        //HyperPipes classifier = new HyperPipes();
        IBk classifier = new IBk(5);
        BufferedReader datafile = readDataFile(inId + ".arff");

        Instances data;
        Evaluation eval;
        try {
            data = new Instances(datafile);
            data.setClassIndex(data.numAttributes() - 1);
            eval = new Evaluation(data);
            Random rand = new Random(1); // usando semente = 1
            int folds = 10;
            eval.crossValidateModel(classifier, data, folds, rand);
            //this.fitness = eval.pctCorrect();
            //fitness = new BigDecimal(fitness).setScale(2, RoundingMode.HALF_UP).doubleValue();//arredondamento para duas casas
            pctAcerto = eval.pctCorrect();
            pctAcerto = new BigDecimal(pctAcerto).setScale(2, RoundingMode.HALF_UP).doubleValue();
            microAverage = getMicroAverage(eval, data);
            microAverage = new BigDecimal(microAverage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            macroAverage = getMacroAverage(eval, data);
            macroAverage = new BigDecimal(macroAverage).setScale(2, RoundingMode.HALF_UP).doubleValue();

        } catch (Exception ex) {
            System.out.println("Erro ao tentar fazer a classificacao");
            Logger.getLogger(WekaSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (metodoFitness) {
            case 1:
                fitness = pctAcerto;
                break;
            case 2:
                fitness = microAverage;
                break;
            case 3:
                fitness = macroAverage;
                break;
            default:
                break;
        }

    }

    private void limparDados() {
        try {
            Runtime.getRuntime().exec("rm " + inId + ".arff");
        } catch (IOException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private double getMacroAverage(Evaluation eval, Instances data) {
        double macroMeasure;
        double macroPrecision = 0;
        double macrorecall = 0;

        for (int i = 0; i < data.numClasses(); i++) {
            macroPrecision += eval.precision(i);
            macrorecall += eval.recall(i);
        }
        macroPrecision = macroPrecision / data.numClasses();
        macrorecall = macrorecall / data.numClasses();
        macroMeasure = (macroPrecision * macrorecall * 2) / (macroPrecision + macrorecall);
        //System.out.println("macroMeasure: " + macroMeasure);

        return macroMeasure;
    }

    private double getMicroAverage(Evaluation eval, Instances data) {
        double TP = 0;
        double TP_plus_FP = 0;
        double TP_plus_FN = 0;
        double microPrecision;
        double microRecall;
        double microMeasure;

        for (int i = 0; i < data.numClasses(); i++) {
            TP += eval.truePositiveRate(i);
            TP_plus_FP += eval.truePositiveRate(i) + eval.falsePositiveRate(i);
            TP_plus_FN += eval.truePositiveRate(i) + eval.falseNegativeRate(i);
        }
        microPrecision = TP / TP_plus_FP;
        microRecall = TP / TP_plus_FN;
        microMeasure = (microPrecision * microRecall * 2) / (microPrecision + microRecall);

        //System.out.println("microMeasure: " + microMeasure);
        return microMeasure;
    }

    public double getPctAcerto() {
        return pctAcerto;
    }

    public double getMicroAverage() {
        return microAverage;
    }

    public double getMacroAverage() {
        return macroAverage;
    }

    public int getMetodoFitness() {
        return metodoFitness;
    }

}
