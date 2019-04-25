/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author João Nuno
 */
public class Gestao_Encomendas {
    private Map<Integer,Encomenda> enc;
    
    public Gestao_Encomendas() {
        enc = new HashMap();
    }
    public void addEncomenda(Encomenda a) {
        enc.put(a.getId(),a);
    }
    public int getNextId() {
        if (enc.values()== null) return 0;
        return enc.values().size();
    }
         
}
