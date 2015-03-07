/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;
// C50test5
//N-Gram   : 1

import facade.CromossomoFacade;
import facade.GeneFacade;
import facade.GeracaoFacade;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGeneticoPLN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int qtde = 1;
        int nrIteracoes = 1;
        int nrAtributos = 30497;
        String base;
        String mem;
        String banco;
       

        if (args.length == 3 && args[0].equals("-c")) {
            printGeneInformation(args[1], args[2]);
            System.exit(0);
        }

        if (args.length == 5) {
            qtde = Integer.parseInt(args[0]);
            nrIteracoes = Integer.parseInt(args[1]);
            mem = args[2];
            base = args[3];
            banco = args[4];
           
        } else {
            help();
            System.exit(0);
            qtde = 4;
            nrIteracoes = 10;
           
            base = "C50test5";
            mem = "1700";
            banco = "teste";
        }

        AlgoritmoGenetico ag = new AlgoritmoGenetico(qtde, mem, base,banco);
        ag.criaPopulacaoInicial(qtde);
       
        for (int i = 0; i < nrIteracoes; i++) {
            System.out.println("Geracao   " + i);
            ag.cruza();
            ag.muta();
            ag.seleciona(i);
        }
        System.exit(0);

    }

    private static void help() {
        //throw new RuntimeException("Insira três parametros populacao nrIteracoes e nrAtributos da base");
        System.out.println("Insira 5 parametros:");
        System.out.println("nrCromossomos");
        System.out.println("nrIteracoes");
        System.out.println("qtdeMemoria");
        System.out.println("NomeBaseDados");
        System.out.println("NomeBancoDados");
    }

    public static void printGeneInformation(String nomeArquivo, String banco) {
        CromossomoFacade cf = new CromossomoFacade(banco);
        List<Cromossomo> crs = cf.find(10);
        GeneFacade gf = new GeneFacade(banco);
        StringBuilder sb = new StringBuilder();
        sb.append("Fitness|Acertos|Atributos|desvio|ngram|stoplist").append("\n");;
        for (Cromossomo c : crs) {
            sb.append(c.getResultado().getFitness()).append("|");
            sb.append(c.getResultado().getPorcentagemAcertos()).append("|");
            sb.append(c.getResultado().getNumeroAtributos()).append(" | ");
            c.setGenes(gf.find(c.getID().toString(), "Gene", "cromossomo_ID"));
            sb.append(c.getConfigGenes().split(",")[0]).append("|");
            sb.append(c.getConfigGenes().split(",")[1]);
            for (int i = 2; i < c.getGenes().size(); i++) {
                if (c.getGenes().get(i).getValor() == 1) {
                    sb.append("|");
                    sb.append(c.getGenes().get(i).getNome().replace(".xml", ""));
                }
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
        try {
            printFile(nomeArquivo, sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(AlgoritmoGeneticoPLN.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void printFile(String fileName, String texto) throws IOException {
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            bw.newLine();
            bw.close();
            fw.close();
        }
    }

}
