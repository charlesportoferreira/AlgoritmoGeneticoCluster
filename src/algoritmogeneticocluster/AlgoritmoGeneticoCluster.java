/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGeneticoCluster {

    List<Cromossomo> cromossomos;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlgoritmoGeneticoCluster ag = new AlgoritmoGeneticoCluster();
        ag.criaPopulacaoInicial(5, 10);
        for (int i = 0; i < 1; i++) {
            ag.cruza();
            ag.muta();
            ag.seleciona();
        }
    }

    public AlgoritmoGeneticoCluster() {
        cromossomos = new ArrayList<>();
    }

    public void criaPopulacaoInicial(int tamanhoPopulacao, int nrGenes) {
        for (int i = 0; i < tamanhoPopulacao; i++) {
            cromossomos.add(new Cromossomo(nrGenes));
        }

        for (Cromossomo c : cromossomos) {
            System.out.println(c.getGenes().toString());
        }
    }

    public void cruza() {
        Random random = new Random();
        int max = cromossomos.get(0).getGenes().size() - 1;
        int min = 0;
        int posInicio;
        int posFim;
        int cromo1;
        int cromo2;
        int maxCromo = cromossomos.size() - 1;

        List<Cromossomo> filhos = new ArrayList<>();
        for (int i = 0; i < cromossomos.size(); i = i + 2) {
            if (i + 1 >= cromossomos.size()) {
                continue;
            }
            posInicio = random.nextInt(max - min + 1) + min;
            posFim = random.nextInt((max - posInicio) + 1) + posInicio;
            cromo1 = random.nextInt(maxCromo - 0 + 1) + 0;
            cromo2 = random.nextInt(maxCromo - 0 + 1) + 0;
            System.out.println(posInicio + " : " + posFim + " : " + cromo1 + " : " + cromo2);
            Cromossomo[] fs = Cruzamento.cruzaPMX(cromossomos.get(cromo1), cromossomos.get(cromo2), posInicio, posFim);
            filhos.add(fs[0]);
            filhos.add(fs[1]);
        }
        cromossomos.addAll(filhos);
        cromossomos.stream().forEach((cromo) -> {
            System.out.println(cromo.getGenes().toString());
        });
    }

    public void muta() {
        Random random = new Random();
        int maxCromo = cromossomos.size() - 1;
        int minCromo = 0;
        int maxMuta = cromossomos.get(0).getGenes().size() - 1;
        int minMuta = 0;
        int posCromo = random.nextInt(maxCromo - minCromo + 1) + minCromo;
        int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;
        System.out.println("posCromo: " + posCromo + " posMuta: " + posMutacao);
        Mutacao.muta(cromossomos.get(posCromo), posMutacao);
    }

    public void seleciona() {

        List<Future> futures = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(cromossomos.size());
        for (Cromossomo cromossomo : cromossomos) {
            Future f = pool.submit(cromossomo);
            futures.add(f);
        }
        for (Future future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException("erro na paralelizacao do fitness");
            }
        }
        pool.shutdown();

        int nrCromo = cromossomos.size();

        double somatorio = 0;
        somatorio = cromossomos.stream().map((c) -> c.getFitness()).reduce(somatorio, (accumulator, _item) -> accumulator + _item);
        for (Cromossomo c : cromossomos) {
            c.setProbSelecao((c.getFitness() / somatorio) * 100);
        }

        double[] roleta = new double[nrCromo + 1];
        cromossomos.stream().forEach((c) -> {
            System.out.println(c.getProbSelecao());
        });
        roleta[0] = 0;
        for (int i = 1; i < roleta.length; i++) {
            roleta[i] = roleta[i - 1] + cromossomos.get(i - 1).getProbSelecao();
        }
        roleta[nrCromo] = 100;
        System.out.println(Arrays.toString(roleta));

        Random rand = new Random();
        int selecao = rand.nextInt(100 - 0 + 1) + 0;
        System.out.println("selecao: " + selecao);
        for (int i = 1; i < roleta.length; i++) {
            if (selecao >= roleta[i - 1] && selecao < roleta[i]) {
                System.out.println("Cromossomo selecionado: "
                        + cromossomos.get(i - 1).getProbSelecao()
                        + " F: " + cromossomos.get(i - 1).getFitness());
                break;
            }
        }

    }

}
