/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.util.ArrayList;
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

/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGeneticoCluster {

    List<Cromossomo> cromossomos;
    int numMutacoes = 1;
    int tipoMutacao = 0;
    int metodoSelecao = 0;
    int fixedFitness = 0;

    public AlgoritmoGeneticoCluster(int metodoSelecao) {
        cromossomos = new ArrayList<>();
        this.metodoSelecao = metodoSelecao;
    }

    public void criaPopulacaoInicial(int tamanhoPopulacao, int nrGenes) {
        for (int i = 0; i < tamanhoPopulacao; i++) {
            cromossomos.add(new Cromossomo(nrGenes, metodoSelecao));
        }
//        for (Gene gene : cromossomos.get(0).getGenes()) {
//            gene.setValor(0);
//        }
//        for (Cromossomo c : cromossomos) {
//            System.out.println(c.getGenes().toString());
//        }
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

//        Cromossomo c = new Cromossomo(cromossomos.get(0).getGenes().size());
//        for (Gene gene : c.getGenes()) {
//            gene.setValor(0);
//        }
//        cromossomos.add(c);
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
//        Random random = new Random();
//        if (tipoMutacao == 0) {
//            for (int i = 0; i < numMutacoes; i++) {
//                int maxCromo = cromossomos.size() - 1;
//                int minCromo = 1;
//                int maxMuta = cromossomos.get(0).getGenes().size() - 1;
//                int minMuta = 0;
//                int posCromo = random.nextInt(maxCromo - minCromo + 1) + minCromo;
//                int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;
////        System.out.println("posCromo: " + posCromo + " posMuta: " + posMutacao);
//                Mutacao.muta(cromossomos.get(posCromo), posMutacao);
//            }
//        } else {
//            for (int i = 1; i < cromossomos.size(); i++) {
//                int maxMuta = cromossomos.get(0).getGenes().size() - 1;
//                int minMuta = 0;
//                int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;
//                Mutacao.muta(cromossomos.get(i), posMutacao);
//            }
//            cruza();
//        }
        System.out.println("Antes da mutacao " + cromossomos.size());
        Random random = new Random();
        if (tipoMutacao == 0) {
            for (int i = 0; i < numMutacoes; i++) {
                int maxCromo = cromossomos.size() - 1;
                int minCromo = 1;
                int maxMuta = cromossomos.get(0).getGenes().size() - 1;
                int minMuta = 0;
                int posCromo = random.nextInt(maxCromo - minCromo + 1) + minCromo;
                int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;
//        System.out.println("posCromo: " + posCromo + " posMuta: " + posMutacao);

                cromossomos.add(Mutacao.muta(cromossomos.get(posCromo), posMutacao));
            }
        } else {
            int max = cromossomos.size();
            for (int i = 1; i < max; i++) {
                int maxMuta = cromossomos.get(0).getGenes().size() - 1;
                int minMuta = 0;
                int posMutacao = random.nextInt(maxMuta - minMuta + 1) + minMuta;

                cromossomos.add(Mutacao.muta(cromossomos.get(i), posMutacao));
            }
            // cruza();
        }
        System.out.println("Apos a mutacao " + cromossomos.size());
    }

    public void seleciona(int tamanhoPopulacao) {
        System.out.println("calculando fitness");
        List<Future> futures = new ArrayList<>();
//        ExecutorService pool = Executors.newFixedThreadPool(4);
        ExecutorService pool = Executors.newWorkStealingPool();
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

//        if (1 == 1) {
//            return;
//        }
        List<Cromossomo> selecionados = new ArrayList<>();
        Collections.sort(cromossomos, (Cromossomo c1, Cromossomo c2) -> new Double(c1.getFitness()).compareTo(c2.getFitness()));
        for (int i = 0; i < 3; i++) {
            selecionados.add(cromossomos.get(cromossomos.size() - i - 1));
        }
//        System.out.println("t:" + selecionados.get(0).getGenes().size());

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

        for (int j = 3; j < tamanhoPopulacao; j++) {
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
        System.out.print("Selecionados:\n");
        HashMap<Double, Integer> map = new HashMap<>();
        int count;
        for (Cromossomo selecionado : selecionados) {
            System.out.print("F:" + selecionado.getFitness() + " - "
                    + "N:" + selecionado.countGenesSelecionados() + " - "
                    + "MI:" + selecionado.getMicroAverage() + " - "
                    + "MA:" + selecionado.getMacroAverage() + " - "
                    + "AC:" + selecionado.getPctAcerto() + "\n"
            );
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

        if (populacaoConvergiu(map)) {
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
        double value;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            count = (int) pair.getValue();
            count = 100 * count / cromossomos.size();
            if (count > max) {
                max = count;
            }
            if (count >= 80.0) {
                value = (double) pair.getKey();
                if (value == cromossomos.get(0).getFitness()) {
                    tipoMutacao = 1;
                } else {
                    tipoMutacao = 0;
                }
                System.out.println("% convergencia: " + count + " mutacoes: " + numMutacoes + " tipo de mutacao: " + tipoMutacao);
                return true;
            }
            tipoMutacao = 0;
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("% convergencia: " + max + " mutacoes: " + numMutacoes + " tipo de mutacao: " + tipoMutacao);
        return false;
    }
}
