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
public class Pacote extends Gestao_Componentes{
    private double preco;
    private String nome;
    private Integer id;
    
    public Pacote(Gestao_Componentes c , double p , String n) {
        super(c);
        System.out.println(c);
        this.nome = n;
        this.preco = p;
    }
    public double getPreco() {
        return preco;
        
    }
    public int getId () {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public void setId(int i) {
        this.id = i;
    }
    
}
