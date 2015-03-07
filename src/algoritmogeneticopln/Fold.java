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
public class Fold {

    private int id;
    private int nrIntancias;
    private int erros;
    private int acertos;
    private double porcentagemErros;
    private double porcentagemAcertos;

    public int getAcertos() {
        return acertos;
    }

    public void setAcertos(int acertos) {
        this.acertos = acertos;
    }

    public double getPorcentagemErros() {
        return porcentagemErros;
    }

    public void setPorcentagemErros(double porcentagemErros) {
        this.porcentagemErros = porcentagemErros;
    }

    public double getPorcentagemAcertos() {
        return porcentagemAcertos;
    }

    public void setPorcentagemAcertos(double porcentagemAcertos) {
        this.porcentagemAcertos = porcentagemAcertos;
    }

    
    
    public void addInstancia() {
        nrIntancias++;
        atualizaAcertos();
    }

    public void addErro() {
        erros++;
        atualizaAcertos();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getNrIntancias() {
        return nrIntancias;
    }

    public int getErros() {
        return erros;
    }

    private void atualizaAcertos() {
        if(nrIntancias == 0){
            int a = 2;
        }
        acertos = nrIntancias - erros;
        porcentagemAcertos = (acertos * 100) / nrIntancias;
        porcentagemErros = 100 - porcentagemAcertos;
    }

}
