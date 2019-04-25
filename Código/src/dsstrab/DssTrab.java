/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author João Nuno
 */
/* 
Designacao:
Pneu: Estrada, montanha, Race.
Jantes: Liga leve, forjadas, modulares, fundidas.
Motor: 1.5 VTEC , 2.2 VTEC, 2.0 Turbocharged, 1.0 Economic.
Pintura: Vermelho, Verde, Preto, Branco, Azul.
Interior: Wireless Charger, Bancos Aquecidos, GPS, Compartimento Bagagem, Painel Carbono
Exterior: Descapotavel, 2 Portas, Aileron traseiro, Porta-Bicicletas, Engate Reboque.
*/
//Componente(String tipo, String designacao , float preco)
public class DssTrab {
       private static Gestao_Componentes map;
     
       private static Gestao_Funcionarios mapFunc;
       private static Gestao_Pacotes pacotes;
       
       
       
       public static void fillMap() {
             Gestao_Componentes mapConf;
             Gestao_Componentes mapSport;
           Componente c1 = new Componente("Pneu","Estrada",300,15);
           Componente c2 = new Componente("Pneu","Montanha",400,15);
           Componente c3 = new Componente("Pneu","Race",500,15);
           Componente c4 = new Componente("Jantes","Liga Leve",200,15);
           Componente c5 = new Componente("Jantes","Forjadas",280,15);
           Componente c6 = new Componente("Jantes","Fundidas",300,15);
           Componente c7 = new Componente("Jantes","Modulares",400,15);
           Componente c8 = new Componente("Pintura","Vermelho",800,15);
           Componente c9 = new Componente("Pintura","Verde",700,15);
           Componente ca = new Componente("Pintura","Branco",500,15);
           Componente cb = new Componente("Pintura","Azul",1000,0);
           Componente cc = new Componente("Pintura","Preto",800,15);
           Componente m1 = new Componente("Motor","1.5 VTEC",5000,15);
           Componente m2 = new Componente("Motor","2.2 VTEC",7000,15);
           Componente m3 = new Componente("Motor","2.0 TurboCharged",4000,15);
           Componente m4 = new Componente("Motor","1.0 Economic",2000,15);
           
           
           
           Componente m5 = new Componente("Interior","Wireless Charger",200,15);
           Componente m6 = new Componente("Interior","GPS",400,15);
           Componente m7 = new Componente("Interior","Bancos Aquecidos",800,2);
           Componente m8 = new Componente("Interior","Compartimento Bagagem",300,15);
           Componente m9 = new Componente("Interior","Sistema Ultra Som",700,0);
           Componente m0 = new Componente("Interior","Painel Carbono",200,15);
           
           
           Componente e1 = new Componente("Exterior","Duas portas",300,15);
           Componente e2 = new Componente("Exterior","Descapotável",2000,15);
           Componente e3 = new Componente("Exterior","Aileron Traseiro",600,1);
           Componente e4 = new Componente("Exterior","Engate reboque",800,0);
           Componente e5 = new Componente("Exterior","Adaptador Bicicletas",700,15);
           Componente e6 = new Componente("Exterior","Faróis Xenón",300,15);
           
           Componente mod = new Componente("Modelo","Civic",5000,15);
           
           Componente mod1 = new Componente("Modelo","Accord",8000,15);
           
           Componente mod2 = new Componente("Modelo","NSX",20000,15);
           
           Componente mod3 = new Componente("Modelo","Jazz",3000,15);
           List<Componente> bancos = new ArrayList();
           bancos.add(m6);
           m7.setObrigatorios(bancos);
           
           List<Componente> gp = new ArrayList();
           gp.add(m7);
           m6.setObrigatorios(gp);
           
           List<Componente> aileron = new ArrayList();
           aileron.add(e5);
           e3.setIncompativeis(aileron);
           
           List<Componente> portas = new ArrayList();
           portas.add(e4);
           e1.setIncompativeis(portas);
           
           List<Componente> port = new ArrayList();
           port.add(e2);
           e1.setObrigatorios(port);
           
           List<Componente> desc = new ArrayList();
           desc.add(e1);
           e2.setObrigatorios(desc);
           
           List<Componente> reboque = new ArrayList();
           reboque.add(e1);
           reboque.add(e2);
           e4.setIncompativeis(reboque);
           
           List<Componente> bicicletas = new ArrayList();
           bicicletas.add(e2);
           bicicletas.add(e3);
           e5.setIncompativeis(bicicletas);
           
           List<Componente> descap = new ArrayList();
           descap.add(e5);
           descap.add(e4);
           e2.setIncompativeis(descap);
           
           
           
           List<Componente> bagagem = new ArrayList();
           bagagem.add(m9);
           m8.setIncompativeis(bagagem);
           
           List<Componente> som = new ArrayList();
           som.add(m8);
           m9.setIncompativeis(som);
           
           List<Componente> painel = new ArrayList();
           painel.add(m6);
           m0.setIncompativeis(painel);
           
           List<Componente> gps = new ArrayList();
           gps.add(m0);
           m6.setIncompativeis(gps);
           
           
           
           
           map = new Gestao_Componentes();
           mapSport = new Gestao_Componentes();
           mapConf = new Gestao_Componentes();
           mapConf.addComponente(e4);
           mapConf.addComponente(e5);
           mapConf.addComponente(m8);
           mapConf.addComponente(m7);
           mapConf.addComponente(m6);
           mapConf.addComponente(e6);
           mapSport.addComponente(e1);
           mapSport.addComponente(e2);
           mapSport.addComponente(e3);
           mapSport.addComponente(m5);
           mapSport.addComponente(m9);
           mapSport.addComponente(m0);
           
           
           map.addComponente(mod);
           map.addComponente(mod1);
           map.addComponente(mod2);
           map.addComponente(mod3);
           map.addComponente(c1);
           map.addComponente(c2);
           map.addComponente(c3);
           map.addComponente(c4);
           map.addComponente(c5);
           map.addComponente(c6);
           map.addComponente(c7);
           map.addComponente(c8);
           map.addComponente(c9);
           map.addComponente(ca);
           map.addComponente(cb);
           map.addComponente(cc);
           map.addComponente(m1);
           map.addComponente(m2);
           map.addComponente(m3);
           map.addComponente(m4);
           map.addComponente(m5);
           map.addComponente(m6);
           map.addComponente(m7);
           map.addComponente(m8);
           map.addComponente(m9);
           map.addComponente(m0);
           map.addComponente(e1);
           map.addComponente(e2);
           map.addComponente(e3);
           map.addComponente(e4);
           map.addComponente(e5);
           map.addComponente(e6);
           mapFunc = new Gestao_Funcionarios();
           
           Funcionario f = new Funcionario("jnuno","pass");
           Funcionario f1 = new Funcionario("jnal","pass");
           
           Funcionario admin = new Funcionario("admin","pass");
           mapFunc.addFunc(f);
           mapFunc.addFunc(f1);
           mapFunc.addFunc(admin);
           
           Pacote sport = new Pacote(mapSport,3000,"Sport");
           sport.setId(0);
          
           Pacote confort = new Pacote(mapConf,2300,"Confort");
           confort.setId(1);
           
           pacotes = new Gestao_Pacotes();
           pacotes.addPacote(sport);
           pacotes.addPacote(confort);
           
       }
    /**
     * @param args the command line arguments
     */
       
        public static void main (String args[]) {
            fillMap();
            
            java.awt.EventQueue.invokeLater(new Runnable() {
          @Override
          public void run() {
               new Menu(map,pacotes,mapFunc).setVisible(true);
               
          }
    });
            
            
            
            
    }
    
    
}
