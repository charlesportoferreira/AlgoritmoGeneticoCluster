/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

import java.io.Serializable;

/**
 *
 * @author charleshenriqueportoferreira
 */

public class Gene implements Serializable{

   
    private Long ID;
    private String nome;
    private int valor;
    boolean isAtivo;
   
    private Cromossomo cromossomo;

    public Cromossomo getCromossomo() {
        return cromossomo;
    }

    public void setCromossomo(Cromossomo cromossomo) {
        this.cromossomo = cromossomo;
    }

    

    
    
    
    public Gene() {
       // cromossomos = new ArrayList<>();
    }

    public Gene(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "" + valor;
    }

}
