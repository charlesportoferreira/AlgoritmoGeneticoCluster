/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticocluster;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Mutacao {

//    public static void muta(Cromossomo c, int posicao) {
//        if (posicao >= c.getGenes().size() || posicao < 0) {
//            throw new RuntimeException("Posicao para mutacao fora do range de genes");
//        }
//
//        if (c.getGenes().get(posicao).getValor() == 1) {
//            c.getGenes().get(posicao).setValor(0);
//        } else {
//            c.getGenes().get(posicao).setValor(1);
//        }
////        System.out.println(c.getGenes().toString());
//    }
    public static Cromossomo muta(Cromossomo c, int posicao) {
        if (posicao >= c.getGenes().size() || posicao < 0) {
            throw new RuntimeException("Posicao para mutacao fora do range de genes");
        }
        Cromossomo clone = new Cromossomo(c.getGenes().size(), 1);
        for (int i = 0; i < c.getGenes().size(); i++) {
            clone.getGenes().get(i).setValor(c.getGenes().get(i).getValor());
        }

        if (clone.getGenes().get(posicao).getValor() == 1) {
            clone.getGenes().get(posicao).setValor(0);
        } else {
            clone.getGenes().get(posicao).setValor(1);
        }

        return clone;
        //c.getConfigGenes();
    }

}
