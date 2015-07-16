/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cromossomo implements Callable<String> {

    private List<Gene> genes;
    private double fitness;
    private double probSelecao;

    public Cromossomo(int nrBits) {
        genes = new ArrayList<>(nrBits);
        criaGenes(nrBits);
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
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
        simulaFitness();
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
        System.out.println("Vou esperar 10 segundos");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.calculaFitness();
        return String.valueOf("Meu fitness: " + fitness);
    }

}
