/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debora
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int geracoes = 500;
        int metodoFitness = 1;
        if (args.length > 0) {
            try {
                geracoes = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Numero de geracoes digitado incorretamente");
                System.exit(0);
            }
            try {
                metodoFitness = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Metodo de fitness errado1 Digite:\n"
                        + " 1 para porcentagem de acertos\n"
                        + " 2 para micro average\n"
                        + " 3 para macro average");
                System.exit(0);
            }

        }
        int numGenes = 0;

        try {
            numGenes = getNumGenes();
            System.out.println(numGenes);

        } catch (IOException ex) {
            Logger.getLogger(AlgoritmoGeneticoCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Cromossomo> evoluidos = new ArrayList<>();
        try {
            Runtime.getRuntime().exec("rm *.arff");
        } catch (IOException ex) {
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
        }// catch (InterruptedException ex) {

        if (args.length != 2) {
            //help();
        }
        AlgoritmoGeneticoCluster ag = new AlgoritmoGeneticoCluster(metodoFitness);

        int tamanhoPopulacao = 12;//Integer.parseInt(args[0]);
        int numeroGenes = numGenes;//Integer.parseInt(args[1]);
        ag.criaPopulacaoInicial(tamanhoPopulacao * 5, numeroGenes);
        Instant inicio = Instant.now();

        for (int i = 0; i < geracoes; i++) {
//            separateEvolution(i, evoluidos, ag, tamanhoPopulacao, numeroGenes);
            System.out.println("---------------  Geracao: " + i + " ---------------");
            ag.cruza();
            ag.muta();
            ag.seleciona(tamanhoPopulacao);
//            printTheBest(evoluidos);
        }
        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim);
        long duracaoEmMinutos = duracao.toMinutes();

        String resultado
                = "Atributos: " + ag.cromossomos.get(0).getGenes().size() + "\n"
                + "Geracoes: " + geracoes + "\n"
                + "Fitness: " + ag.cromossomos.get(0).getFitness() + "\n"
                + "pctAcertos: " + ag.cromossomos.get(0).getPctAcerto() + "\n"
                + "microAverage: " + ag.cromossomos.get(0).getMicroAverage() + "\n"
                + "macroAverage: " + ag.cromossomos.get(0).getMacroAverage() + "\n"
                + "Duracao em minutos: " + duracaoEmMinutos + "\n"
                + "Populacao: " + tamanhoPopulacao + "\n"
                + "Numero de Grupos: " + ag.cromossomos.get(0).countGenesSelecionados();

        try {
            salvaLinhaDados(inicio.toString(), resultado);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int getNumGenes() throws FileNotFoundException, IOException {
        String linha;
        int numGenes = 0;
        try (FileReader fr = new FileReader("cluster.txt"); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (linha.contains("Attribute    Full Data")) {
                    String[] dados = linha.split(" ");
                    numGenes = Integer.parseInt(dados[dados.length - 1]) + 1;
                    return numGenes;
                }
            }
            br.close();
            fr.close();
        }
        return numGenes;
    }

    private static void separateEvolution(int i, List<Cromossomo> evoluidos, AlgoritmoGeneticoCluster ag, int tamanhoPopulacao, int numeroGenes) {
        int max = 900;
        int min = 100;
        if (i != 0 && i % min == 0 && i <= max) {
            evoluidos.add(ag.cromossomos.get(0));
            evoluidos.add(ag.cromossomos.get(1));
            ag.cromossomos = new ArrayList<>();
            if (i != max) {
                ag.criaPopulacaoInicial(tamanhoPopulacao, numeroGenes);
            }
        }
        if (i == max) {
            evoluidos.stream().forEach((evoluido) -> {
                ag.cromossomos.add(evoluido);
            });
        }
    }

    private static void printTheBest(List<Cromossomo> evoluidos) {
        System.out.print("The Best: ");
        evoluidos.stream().forEach((evoluido) -> {
            System.out.print(evoluido.getFitness() + " ");
        });
        System.out.println("");
    }

    private static void help() {
        System.out.println("Digite dois parametros:");
        System.out.println("Tamanho da populacao");
        System.out.println("Numero de genes");
    }

    private static void salvaLinhaDados(String fileName, String dado) throws IOException {
        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(dado);
            bw.newLine();
            bw.close();
            fw.close();
        }
    }

}
