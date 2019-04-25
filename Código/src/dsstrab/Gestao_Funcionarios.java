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
public class Gestao_Funcionarios {
    private Map<String, Funcionario> map;
    
    public Gestao_Funcionarios() {
        this.map = new HashMap();
    }
    public void addFunc(Funcionario c) {
        map.put(c.getNome(), c);
        
    }
    public boolean containsFunc(String n) {
        return map.containsKey(n);
    }
    public Funcionario getFunc(String n ) {
        return map.get(n);
    }
    public boolean iniciarSessao(String name, String pass) {
        if (map.containsKey(name))
            if ( map.get(name).getPass().equals(pass)) return true;
                   
        return false;
    }
    
}
