/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGeneticoCluster {

    List<Cromossomo> cromossomos;
    int numMutacoes = 1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
        
        try {
            Runtime.getRuntime().exec("rm *.arff");
        } catch (IOException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }// catch (InterruptedException ex) {

        if (args.length != 2) {
            //help();
        }
        AlgoritmoGeneticoCluster ag = new AlgoritmoGeneticoCluster();
        int tamanhoPopulacao = 10;//Integer.parseInt(args[0]);
        int numeroGenes = 79;//Integer.parseInt(args[1]);
        ag.criaPopulacaoInicial(tamanhoPopulacao, numeroGenes);
        for (int i = 0; i < 10000; i++) {
            System.out.println("---------------  Geracao: " + i + " ---------------");
            ag.cruza();
            ag.muta();
            ag.seleciona(tamanhoPopulacao);
        }
    }

    private static void help() {
        System.out.println("Digite dois parametros:");
        System.out.println("Tamanho da populacao");
        System.out.println("Numero de genes");
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
//            System.out.println(posInicio + " : " + posFim + " : " + cromo1 + " : " + cromo2);
            Cromossomo[] fs = Cruzamento.cruzaPMX(cromossomos.get(cromo1), cromossomos.get(cromo2), posInicio, posFim);
            filhos.add(fs[0]);
            filhos.add(fs[1]);
        }
        cromossomos.addAll(filhos);
//        cromossomos.stream().forEach((cromo) -> {
//            System.out.println(cromo.getGenes().toString());
//        });
    }

    public void muta() {

        for (int i = 0; i < numMutacoes; i++) {
            Random random = new Random();
            int maxCromo = cromossomos.size() - 1;
            int minCromo = 1;
            int maxMuta = cromossomos.get(0).getGenes().size() - 1;
            int minMuta = 0;
            int posCromo = random.nextInt(maxCromo - minCromo + 1) + minCromo;
            int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;
//        System.out.println("posCromo: " + posCromo + " posMuta: " + posMutacao);
            Mutacao.muta(cromossomos.get(posCromo), posMutacao);
        }
    }

    public void seleciona(int tamanhoPopulacao) {
        System.out.println("calculando fitness");
        List<Future> futures = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(cromossomos.size());
        for (Cromossomo cromossomo : cromossomos) {
            Future f = pool.submit(cromossomo);
            futures.add(f);
        }
        for (Future future : futures) {
            try {
//                System.out.println(future.get());
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
                throw new RuntimeException("erro na paralelizacao do fitness");
            }
        }
        pool.shutdown();

        List<Cromossomo> selecionados = new ArrayList<>();
        Collections.sort(cromossomos, (Cromossomo c1, Cromossomo c2) -> new Double(c1.getFitness()).compareTo(c2.getFitness()));
        selecionados.add(cromossomos.get(cromossomos.size() - 1));

        int nrCromo = cromossomos.size();

        double somatorio = 0;
        somatorio = cromossomos.stream().map((c) -> c.getFitness()).reduce(somatorio, (accumulator, _item) -> accumulator + _item);
        for (Cromossomo c : cromossomos) {
            c.setProbSelecao((c.getFitness() / somatorio) * 100);
        }

        double[] roleta = new double[nrCromo + 1];
//        cromossomos.stream().forEach((c) -> {
//            System.out.println(c.getProbSelecao());
//        });
        roleta[0] = 0;
        for (int i = 1; i < roleta.length; i++) {
            roleta[i] = roleta[i - 1] + cromossomos.get(i - 1).getProbSelecao();
        }
        roleta[nrCromo] = 100;
//        System.out.println("Roleta: " + Arrays.toString(roleta));

        Random rand = new Random();
        int selecao;
        System.out.println("Selecionando...");

        for (int j = 1; j < tamanhoPopulacao; j++) {
            selecao = rand.nextInt(100 - 0 + 1) + 0;
            for (int i = 1; i < roleta.length; i++) {
                if (selecao >= roleta[i - 1] && selecao < roleta[i]) {
                    selecionados.add(cromossomos.get(i - 1));
//                    System.out.println("Cromossomo selecionado: "
//                            + cromossomos.get(i - 1).getProbSelecao()
//                            + " F: " + cromossomos.get(i - 1).getFitness());
                    break;
                }
            }
        }
//        System.exit(0);
        System.out.print("Selecionados:\t");
        HashMap<Double, Integer> map = new HashMap<>();
        int count;
        for (Cromossomo selecionado : selecionados) {
            System.out.print(selecionado.getFitness() + "\t");
            if (map.containsKey(selecionado.getFitness())) {
                count = map.get(selecionado.getFitness());
                count++;
                map.put(selecionado.getFitness(), count);
            } else {
                map.put(selecionado.getFitness(), 1);
            }
        }
//        selecionados.stream().forEach((selecionado) -> {
//            
//        });
        System.out.println("");
        cromossomos = selecionados;

        if (!populacaoConvergiu(map)) {
            numMutacoes = cromossomos.size() - 1;
            System.out.println("-------------- Convergiu !!!!!!!---------------------");
        } else {
            numMutacoes = 1;
        }

//        System.out.println("M: " + cromossomos.get(0).getGenes().toString());
    }

    private boolean populacaoConvergiu() {
        double fitness = cromossomos.get(0).getFitness();
        int count = 0;
        for (int i = 0; i < cromossomos.size(); i++) {
            if (fitness == cromossomos.get(i).getFitness()) {
                count++;
            }
        }
        count = 100 * count / cromossomos.size();
        System.out.println("% convergencia: " + count + " mutacoes: " + numMutacoes);
        return count >= 80.0;
    }

    private boolean populacaoConvergiu(HashMap<Double, Integer> map) {
        int count;
        int max = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            count = (int) pair.getValue();
            count = 100 * count / cromossomos.size();
            if (count > max) {
                max = count;
            }
            if (count >= 80.0) {
                System.out.println("% convergencia: " + count + " mutacoes: " + numMutacoes);
                return true;
            }
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("% convergencia: " + max + " mutacoes: " + numMutacoes);
        return false;
    }
}
