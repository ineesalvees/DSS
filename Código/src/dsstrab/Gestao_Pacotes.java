/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Nuno
 */
public class Gestao_Pacotes {
    private Map<Integer,Pacote> pacotes;
    
    public Gestao_Pacotes() {
        pacotes = new HashMap();
        
    }
    public List<String> getAll() {
        List<String> res = new ArrayList();
        
        for (Pacote p : pacotes.values()) 
            res.add(p.getNome());
        
        return res;
    }
    public void addPacote(Pacote p) {
        pacotes.put(p.getId(),p);
    }
    public void removePacote(Pacote p) {
        if (pacotes.containsKey(p.getId())) pacotes.remove(p.getId());
        
    }
    public Pacote getPacoteNome(String nome) {
        for (Pacote p : pacotes.values())
            if (p.getNome().equals(nome)) return p;
        
        return null;
    }
    
}
