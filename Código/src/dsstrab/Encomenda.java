/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author João Nuno
 */

public class Encomenda extends Gestao_Componentes{
    private int id;
    private double preco;
   
    
    public Encomenda(Gestao_Componentes comp,int id,double preco) {
        super(comp);
        this.preco = preco;
        this.id = id;
    }
    public Encomenda() {
        id = 0;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public double getPreco() {
        return preco;
    }
    
}