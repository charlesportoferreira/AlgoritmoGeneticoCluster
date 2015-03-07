/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author charleshenriqueportoferreira
 */
@Entity
public class Gene implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    private String nome;
    private int valor;
    boolean isAtivo;
    @ManyToOne
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
