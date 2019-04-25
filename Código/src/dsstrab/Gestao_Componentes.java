/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Gestao_Componentes{

    private Map<String,List<Componente>>  comps ; // String -> tipo (pneus, jantes etc)

    public Gestao_Componentes () {
        this.comps = new HashMap<>();
    }
    
   public Gestao_Componentes(Gestao_Componentes c) {
       
       this.comps = c.getMap();
   }
    public Map<String,List<Componente>> getMap() {
        return this.comps;
    }
    
    public double precoTotal() {
        double res = 0;
        for (List<Componente> l : comps.values())
            if (l != null) res += l.get(0).getPreco();
        
        return res;
    }
    public void addComponente (Componente c) {

        List<Componente> aux = this.comps.get(c.getTipo());

        if ( aux == null) aux = new ArrayList<>();
        
        aux.add(c);

        comps.put(c.getTipo(),aux);
        
    }
    public List<Componente> getLista(String tipo) {
        return comps.get(tipo);
    }
    public List<String> getListaTotal() {
        List<String> res = new ArrayList();
        
        for (List<Componente> l : comps.values()) {
            for(Componente c : l)
                if (!c.getTipo().equals("Modelo")) res.add(c.getDesignacao());
        }
        return res;
    }
    public List<String> getAll() {
        List<String> res = new ArrayList();
        
        for (List<Componente> l : comps.values()) {
            for(Componente c : l)
                res.add(c.getDesignacao());
        }
        return res;
    }
    public Componente getComponente(String tipo, String design) {
        List<Componente> aux = this.comps.get(tipo);
        if ( aux != null) {
            for( Componente c : aux) 
                if (c.getDesignacao().equals(design)) return c;
            return null;
        }
        else return null;
        
    }
    
    
    public void remove(String tipo) {
        
        if (comps.containsKey(tipo)) this.comps.remove(tipo);
    }
    public Componente getFirstComponente(String tipo) {
        if (this.comps.containsKey(tipo)) return this.comps.get(tipo).get(0);
        else return null;
    }
    public String getFirst(String tipo) {
        if (this.comps.containsKey(tipo)) return this.comps.get(tipo).get(0).getDesignacao();
        else return null;
    }
    public void addValueStock(String d, int n) {
        for (List<Componente> l : comps.values())
            for (Componente c : l)
                if (c.getDesignacao().equals(d)) {c.setPlusStock(n);return;}
    }
    public Set<String> getKeys() {
        return comps.keySet();
    }
    public void createTipo (String tipo,List<Componente> a) {
        this.comps.put(tipo,a);
        
    }
    public String printMap() {
        String res = "";
        for (String t: this.comps.keySet()) 
            for (Componente c : comps.get(t) )
                res = res + c.getDesignacao()+"\n";
                
        return res;
    }
    public double getPreco(String tipo, String d) {
        return getComponente(tipo,d).getPreco();
    }
    
    public void removeStock() {
        for (List<Componente> lista : comps.values()) {
            if (lista == null) continue;
            for (Componente c: lista) 
                c.decreaseStock();
        }
    }
    public void save () {
        try {
        FileOutputStream fos = new FileOutputStream("conf.TXT");
	ObjectOutputStream oos = new ObjectOutputStream(fos);
	oos.writeObject(comps);
	oos.close();
        fos.close();
        
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException i) {
            System.out.println("File Not Found1");
        }
    }
}
