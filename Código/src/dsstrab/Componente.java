/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author João Nuno
 */
//tipo -> pneu, jante, interior, exterior, pintura, motor
/* 
Designacao:
Pneu: Estrada, montanha, Race.
Jantes: Liga leve, forjadas, modulares, fundidas.
Motor: 1.5 VTEC , 2.2 VTEC, 2.0 Turbocharged, 1.0 Economic.
Pintura: Vermelho, Verde, Preto, Branco, Azul.
Interior: Wireless Charger, Bancos Aquecidos, GPS, Compartimento Bagagem, Painel Carbono
Exterior: Descapotavel, 2 Portas, Aileron traseiro, Porta-Bicicletas, Engate Reboque.
*/
public class Componente {
    private String designacao, tipo;
    private double preco;
    private int stock;
    
    private List<Componente> obrigatorios;
    
    private List<Componente> incompativeis;
    public Componente() {
        this.preco = 0;
        this.designacao = this.tipo = "";
        this.stock = 0;
        this.incompativeis = new ArrayList();
    }
    public Componente(String tipo, String g) {
        this.tipo = tipo;
        this.designacao = g;
        this.incompativeis = new ArrayList();
    }
    public Componente(String tipo, String d, double preco) {
        this.tipo = tipo ;
        this.designacao = d;
        this.preco = preco;
        this.incompativeis = new ArrayList();
        this.obrigatorios = new ArrayList();
    }
    public Componente(String tipo, String designacao , double preco, int st) {
        this.tipo = tipo;
        this.designacao = designacao;
        this.preco = preco;
        this.stock = st;
        this.obrigatorios = new ArrayList();
    }
    public void setObrigatorios(List<Componente> list) {
        this.obrigatorios = new ArrayList();
        if (list.isEmpty()) return;
        for (Componente c : list) 
            obrigatorios.add(c);
    }
    public void setIncompativeis(List<Componente> list) {
        this.incompativeis = new ArrayList();
        if (list.isEmpty()) return;
        for (Componente c : list) 
            incompativeis.add(c);
    }
    public List<Componente> getObrigatorios() {
        System.out.println("HHH" + obrigatorios);
        List<Componente> res = new ArrayList();
        if (obrigatorios == null || obrigatorios.isEmpty()) return null;
        for (Componente c: obrigatorios) 
            res.add(c);
        return res;
    }
    public List<Componente> getIncompativeis() {
        List<Componente> res = new ArrayList();
        for (Componente c: incompativeis) 
            res.add(c);
        return res;
    }
    public double getPreco() {
        return preco;
    }

    public String getDesignacao() {
        return designacao;
    }

    public String getTipo() {
        return tipo;
    }
    
    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public boolean hasStock() {
        if (this.tipo.equals("Pneu") || this.tipo.equals("Jantes")) return (stock >= 4);
        else 
            return (stock >= 1);
        
    }
    public void increaseStock() {
        if (this.tipo.equals("Pneu") || this.tipo.equals("Jantes")) stock += 4;
        else 
            stock += 1;
    }
    public void decreaseStock() {
        if (this.tipo.equals("Pneu") || this.tipo.equals("Jantes")) stock -= 4;
        else 
            stock -= 1;
        
        if (stock < 0 ) stock = 0;
    }
    public int getStock () {
        return stock;
    }
    public void setPlusStock(int n) {
        this.stock += n;
    }
    public void setStock(int n) {
        this.stock = n;
    }
    
    public Componente clone() {
        Componente n = new Componente();
        n.setDesignacao(designacao);
        n.setTipo(tipo);
        n.setStock(stock);
        n.setPreco(preco);
        n.incompativeis = this.incompativeis;
        n.obrigatorios = this.obrigatorios;
        
        return n;
    }
    
    public boolean isValidWith(Componente c) {
        
        if (incompativeis == null || incompativeis.isEmpty()) {return true;}
        
        if (c.getTipo().equals(this.tipo)) {
        
            for (Componente x : incompativeis){
                
                if (c.getDesignacao().equals(x.getDesignacao())) return false;}
            
            return true;
        }
     
        return false;
    }
    public boolean equals(Componente c) {
        return (c.getDesignacao().equals(designacao)) && c.getTipo().equals(tipo);
    }
}
