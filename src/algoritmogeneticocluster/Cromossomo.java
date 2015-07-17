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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
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

    public Cromossomo(int nrBits) {
        inId = id;
        id++;
        genes = new ArrayList<>(nrBits);
        criaGenes(nrBits);
        System.out.println("Meu ID: " + this.inId);
    }

    public List<Gene> getGenes() {
        return genes;
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
        calculaFitness();
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
        SMO classifier = new SMO();

        BufferedReader datafile = readDataFile(inId + ".arff");

        Instances data;
        Evaluation eval;
        try {
            data = new Instances(datafile);
            data.setClassIndex(data.numAttributes() - 1);
            eval = new Evaluation(data);
            Random rand = new Random(1); // using seed = 1
            int folds = 10;
            eval.crossValidateModel(classifier, data, folds, rand);
            this.fitness = eval.pctCorrect();

        } catch (Exception ex) {
            Logger.getLogger(WekaSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void limparDados() {
        try {
            Runtime.getRuntime().exec("rm " + inId + ".arff");
        } catch (IOException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
