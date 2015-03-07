/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

import facade.CromossomoFacade;
import facade.GeneFacade;
import facade.GeracaoFacade;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class AlgoritmoGenetico {

    List<Cromossomo> cromossomos;

    int capacidade;
    int numeroAtributos;
    String base;
    String mem;
    String banco;

    public AlgoritmoGenetico(int capacidadeInicial, String mem, String base, String banco) {
        capacidade = capacidadeInicial;
        this.cromossomos = new ArrayList<>(2 * capacidade);
        this.base = base;
        this.mem = mem;
        this.banco = banco;

    }

    public void criaPopulacaoInicial(int tamanho) {
        for (int i = 0; i < tamanho; i++) {
            cromossomos.add(new Cromossomo(39));
        }
        Random random = new Random();
        int max = 100;
        int min = 0;
        for (Cromossomo cromossomo : cromossomos) {
            cromossomo.getGenes().get(0).setValor(random.nextInt(max - min + 1) + min);
            cromossomo.getGenes().get(1).setValor(random.nextInt(max - min + 1) + min);
        }
        max = 1;
        min = 0;
        for (Cromossomo cromossomo : cromossomos) {
            for (int i = 2; i < cromossomo.getGenes().size(); i++) {
                cromossomo.getGenes().get(i).setValor(random.nextInt(max - min + 1) + min);
            }
        }

        //Cromossomo tradicional
        Cromossomo cromossomoTradicional = new Cromossomo(39);
        cromossomoTradicional.getGenes().get(0).setValor(65);
        cromossomoTradicional.getGenes().get(1).setValor(30);
        cromossomoTradicional.getGenes().get(2).setValor(1);
        for (int i = 3; i < cromossomoTradicional.getGenes().size(); i++) {
            cromossomoTradicional.getGenes().get(i).setValor(0);
        }

        cromossomos.add(cromossomoTradicional);

        cromossomos.stream().forEach((cromossomo) -> {
            cromossomo.getConfigGenes();
            System.out.println(cromossomo.getConfigGenes());
        });

    }

    public Cromossomo getCromossomoRandomico() {
        Cromossomo cr = new Cromossomo(39);
        Random random = new Random();
        int max = 100;
        int min = 0;

        cr.getGenes().get(0).setValor(random.nextInt(max - min + 1) + min);
        cr.getGenes().get(1).setValor(random.nextInt(max - min + 1) + min);

        max = 1;
        min = 0;
        for (int i = 2; i < cr.getGenes().size(); i++) {
            cr.getGenes().get(i).setValor(random.nextInt(max - min + 1) + min);
        }
        cr.getConfigGenes();
        return cr;
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

        for (int i = 0; i < (capacidade); i++) {
            cromossomos.add(getCromossomoRandomico());
        }

        List<Cromossomo> filhos = new ArrayList<>();
        for (int i = 0; i < cromossomos.size(); i = i + 2) {
            if (i + 1 >= cromossomos.size()) {
                continue;
            }
            posInicio = random.nextInt(max - min + 1) + min;
            posFim = random.nextInt((max - posInicio) + 1) + posInicio;
            cromo1 = random.nextInt(maxCromo - 0 + 1) + 0;
            cromo2 = random.nextInt(maxCromo - 0 + 1) + 0;
            Cromossomo[] fs = Cruzamento.cruzaPMX(cromossomos.get(cromo1), cromossomos.get(cromo2), posInicio, posFim);
            filhos.add(fs[0]);
            filhos.add(fs[1]);
        }
        cromossomos.addAll(filhos);

        //for (int i = 0; i < (capacidade / 2); i++) {
        //   cromossomos.add(getCromossomoRandomico());
        // }
    }

    public void muta() {
        Random random = new Random();
        int posicao = random.nextInt(38 - 0 + 1) + 0;
        int max = cromossomos.size() - 1;
        //seleciona qualquer um menos o melhor cromossomo. de 1 ate max
        int cromoEscolhido = random.nextInt(max - 1 + 1) + 1;
       // Cromossomo multado = new Cromossomo(39);
        // for (int i = 0; i < 39; i++) {
        //   multado.getGenes().get(i).setValor(cromossomos.get(cromoEscolhido).getGenes().get(i).getValor());
        // }
        Cromossomo multado = cromossomos.get(cromoEscolhido);
        Mutacao.muta(multado, posicao);
        multado.getConfigGenes();
       // cromossomos.add(multado);

        //cria um clone do melhor cromossomo
        Cromossomo clone1 = new Cromossomo(39);
        for (int i = 0; i < 39; i++) {
            clone1.getGenes().get(i).setValor(cromossomos.get(0).getGenes().get(i).getValor());
        }
        Mutacao.muta(clone1, posicao);
        clone1.getConfigGenes();
        cromossomos.add(clone1);
    }

    public void seleciona(int numGeracao) {

        CromossomoFacade cf = new CromossomoFacade(banco);
        System.out.println(cromossomos.size());
        double fitnessMedio = 0.0;
        double menorFitness = 100;
        for (Cromossomo cromossomo : cromossomos) {
            cromossomo.memoria = mem;
            cromossomo.baseDados = base;
            cromossomo.bancoDados = banco;
            cromossomo.calculaFitness();
            fitnessMedio += cromossomo.getFitness();
            if (cromossomo.getFitness() < menorFitness) {
                menorFitness = cromossomo.getFitness();
            }
        }
        fitnessMedio = fitnessMedio / cromossomos.size();

        cromossomos.clear();
        cromossomos = cf.find(capacidade);

        Geracao geracaoAtual = new Geracao();
        geracaoAtual.setNumero(numGeracao);
        geracaoAtual.setMelhorFitness(cromossomos.get(0).getFitness());
        geracaoAtual.setPiorFitness(menorFitness);
        geracaoAtual.setFitnessMedio(fitnessMedio);
        GeracaoFacade geracaoFacade = new GeracaoFacade(base);
        geracaoFacade.save(geracaoAtual);

        GeneFacade gf = new GeneFacade(banco);
        for (Cromossomo cromossomo : cromossomos) {
            cromossomo.setGenes(gf.find(cromossomo.getID().toString(), "Gene", "cromossomo_ID"));
        }

        Iterator it = cromossomos.iterator();
        while (it.hasNext()) {
            Cromossomo c = (Cromossomo) it.next();
            if (c.getGenes().isEmpty()) {
                it.remove();
            }
        }
        int nrAtributos = cromossomos.get(0).getResultado().getNumeroAtributos();
        String acertos = cromossomos.get(0).getResultado().getPorcentagemAcertos();
        System.out.println("\nMelhor cromossomo " + acertos + " : " + nrAtributos);
        System.out.println(cromossomos.get(0).getStringConfiguracao());
        System.out.println();
    }

}
