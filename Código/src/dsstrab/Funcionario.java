/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

/**
 *
 * @author João Nuno
 */
public class Funcionario {
    private String nome, pass;
    private double orcamento;

    public Funcionario(int orcamento,String nome, String pass) {
        this.nome = nome;
        this.pass = pass;
        this.orcamento = orcamento;
    }

    public Funcionario () {
        this.orcamento = 0;
    }
    public Funcionario(String nome, String pass) {
        this.nome = nome;
        this.pass = pass;
        this.orcamento = 0;
    }

    public double getOrcamento() {
        return this.orcamento;
    }
    public void setOrcamento (double o) {
        this.orcamento = o;
    }

    public String getNome() {
        return nome;
    }
    
    public String getPass() {
        return pass;
    }
    public void setNome(String n) {
        this.nome = n;
    }
    public void setPass(String n) {
        this.pass = n;
    }

}

