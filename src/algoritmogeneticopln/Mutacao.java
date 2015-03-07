/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

import java.util.Random;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Mutacao {

    public static void muta(Cromossomo c, int posicao) {
        if (posicao >= c.getGenes().size() || posicao < 0) {
            throw new RuntimeException("Posicao para mutacao fora do range de genes");
        }
        Random random = new Random();
        int max = 100;
        int min = 0;
        
        if (posicao == 0 || posicao == 1) {
            c.getGenes().get(posicao).setValor(random.nextInt(max - min + 1) + min);
            return;
        }

        if (c.getGenes().get(posicao).getValor() == 1) {
            c.getGenes().get(posicao).setValor(0);
        } else {
            c.getGenes().get(posicao).setValor(1);
        }
        
        c.getConfigGenes();
    }

}
