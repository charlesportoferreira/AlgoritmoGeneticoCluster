/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;


/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGeneticoCluster {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int populacao = 1;
        int nrIteracoes = 1;
        
        String mem;

        if (args.length == 5) {
            populacao = Integer.parseInt(args[0]);
            nrIteracoes = Integer.parseInt(args[1]);
            mem = args[2];
           
        } else {
            help();
            System.exit(0);
            populacao = 4;
            nrIteracoes = 10;
           
            mem = "1700";
        }

        AlgoritmoGenetico ag = new AlgoritmoGenetico(populacao, mem);
        ag.criaPopulacaoInicial(populacao);
       
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



  

}
