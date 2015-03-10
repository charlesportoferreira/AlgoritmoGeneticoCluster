/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

/**
 *
 * @author debora
 */

public class Geracao  {
   
  
    private int numero;
    private double melhorFitness;
    private double piorFitness;
    private double fitnessMedio;

    public Geracao() {
    }


    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

   
   

    public double getMelhorFitness() {
        return melhorFitness;
    }

    public void setMelhorFitness(double melhorFitness) {
        this.melhorFitness = melhorFitness;
    }

    public double getPiorFitness() {
        return piorFitness;
    }

    public void setPiorFitness(double piorFitness) {
        this.piorFitness = piorFitness;
    }

    public double getFitnessMedio() {
        return fitnessMedio;
    }

    public void setFitnessMedio(double fitnessMedio) {
        this.fitnessMedio = fitnessMedio;
    }
    
}
