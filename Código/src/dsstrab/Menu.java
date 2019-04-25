/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsstrab;

import java.awt.CheckboxGroup;
import java.awt.MenuItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.scene.control.CheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author João Nuno
 */
public class Menu extends javax.swing.JFrame {
    private String escolheuPacote = null;
    private boolean asFunc = false;
    private boolean asAdmin = false;
    private boolean saved = false;
    private double preco = 0;
    private int id;
    
    
    private Gestao_Encomendas encomendas ;
    private static Gestao_Componentes map; //elementos da loja
    private static Gestao_Componentes conf; //configuração final
    private static Gestao_Pacotes pacotes;
    private static Gestao_Funcionarios mapFunc;
    
    private Funcionario func; //cliente que esta a usar a app
    
    /**
     * Creates new form Menu
     */
    public Menu(Gestao_Componentes mapcomp, Gestao_Pacotes pac, Gestao_Funcionarios cl) {
        this.map = mapcomp;
        encomendas = new Gestao_Encomendas();
        id = encomendas.getNextId();
        conf = new Gestao_Componentes();
        this.pacotes = pac;
        this.mapFunc = cl;
        initComponents();
        
        
        initBoxes();
        panel_menu2.setVisible(false);
        panel_pintura.setVisible(false);
        panel_motor.setVisible(false);
        panel_jantes.setVisible(false);
        panel_pneus.setVisible(false);
        panel_interiores.setVisible(false);
        panel_exteriores.setVisible(false);
        panel_pacotes.setVisible(false);
        panel_final.setVisible(false);
        menu_interior.setVisible(false);
        panel_admin.setVisible(false);
        panel_modelos.setVisible(false);
    }
    
    // Passa de uma lista de componentes, para a lista de todas as designacoes
    public List<String> filterDesignacao (List<Componente> l) { 
        List<String> res = new ArrayList<>();
        for (Componente c : l) 
            res.add(c.getDesignacao());
        return res;
    }
    
    
    public void initBoxes(){
        //metodo que inicia as combo boxes
        
        try {
        
        List a = map.getLista("Pintura");
        
        List design = filterDesignacao(a);
        box_pintura.setModel(new DefaultComboBoxModel(design.toArray()));
        
        a = map.getLista("Pneu");
        
        design = filterDesignacao(a);
        box_pneus.setModel(new DefaultComboBoxModel(design.toArray()));
        
        a = map.getLista("Jantes");
        design = filterDesignacao(a);
        box_jantes.setModel(new DefaultComboBoxModel(design.toArray()));
        
        a = map.getLista("Motor");
        design = filterDesignacao(a);
        box_motor.setModel(new DefaultComboBoxModel(design.toArray()));
        
        a = map.getLista("Modelo");
        
        
        design = filterDesignacao(a);
        
        box_modelos.setModel(new DefaultComboBoxModel(design.toArray()));
        
        design = map.getAll();
        box_admin.setModel(new DefaultComboBoxModel(design.toArray()));
        
        design = pacotes.getAll();
        box_pacotes.setModel(new DefaultComboBoxModel(design.toArray()));
        
        
        } catch (NullPointerException e) {
            System.out.println("ERRO SEM COMPONENTES");
        }
        
    }
    //ordena uma lista de componentes pelo preço crescentemente
    private List<Componente> sortList(List<Componente> a) {
        Collections.sort(a, new Comparator<Componente>() {
        			@Override
        			public int compare(Componente p1, Componente p2) {
        				if ( p1.getPreco() < p2.getPreco()) return -1;
        				else return 1;
        			}
   	 			});
        return a;
    }
    private boolean contem (List<Componente> lista , Componente c) {
        
        for (Componente x : lista ) {
            if (c.getDesignacao().equals(x.getDesignacao())) return true;
        }
        return false;
    }
    private String estaoTodos (List<Componente> ob , List<Componente> escolhas) {
        if (ob == null) return null;
        for (Componente c : ob) {
           System.out.println("OB" +ob);
           
            if (!contem(escolhas,c)) return "O componente " +c.getDesignacao() + " é obrigatório para as suas escolhas.";
        }
        return null;
    }
    private String faltaComponente (List<Componente> escolhas) { // ve se os obrigatorios estao todos na lista
       
        for (Componente c : escolhas) {
            
            if (estaoTodos(c.getObrigatorios() , escolhas)!=null) return estaoTodos(c.getObrigatorios() , escolhas); 
        }
        return null;
    }
    private String isValidWithList (List<Componente> lista, Componente c) { // se retornar null e valido com todos
        String x = c.getTipo();
        if (lista == null || lista.isEmpty()) return null;
        for (Componente ad : lista) {
           
            if (ad.isValidWith(c) == false ) return createMessage(c.getDesignacao(),ad.getDesignacao());
        
        }
        
        return null;
    }
    
    private Encomenda everyCaseExtra (Encomenda m, double pre, double orcamento) {
        System.out.println("PRECO " + pre +" \n");
        List<Componente> exterior = map.getLista("Exterior");
        List<Componente> interior = map.getLista("Interior");
        
        exterior = sortList(exterior);
        interior = sortList(interior);
        List<Componente> adicionados = new ArrayList();
        
        double falta = (orcamento -pre) / 2;
        System.out.println("falta " + falta +" \n");
        int i  = 0;
        for (Componente c : interior) {
            if (isValidWithList(adicionados,c) != null || !c.hasStock()) continue;
            
            
            if (i +c.getPreco() <= falta) {
                i += c.getPreco();
                m.addComponente(c);
                adicionados.add(c);
                pre += c.getPreco();
            }
            else {break;}
        }
        adicionados = new ArrayList();
        i = 0;
        for (Componente c : exterior) {
            if (isValidWithList(adicionados,c) != null || !c.hasStock()) continue;
            
            if (i +c.getPreco()<= falta) {
                i += c.getPreco();
                m.addComponente(c);
                adicionados.add(c);
                pre += c.getPreco();
            }
            else {break;}
        }
        Encomenda res = new Encomenda(m,encomendas.getNextId(), pre);
        //this.preco = pre;
        return res;
    }
    
    private List<Encomenda> everyCaseBasic(Gestao_Componentes m,double pre,double orcamento) {
        List<Encomenda> res = new ArrayList();
        Gestao_Componentes aux;
        List<Componente> jante = map.getLista("Jantes");
        List<Componente> pneu = map.getLista("Pneu");
        double st = pre;
        for(Componente c : jante) {
            for (Componente x : pneu) {
                aux = new Gestao_Componentes();
                aux.addComponente(c);
                aux.addComponente(x);
                pre += c.getPreco();
                pre += x.getPreco();
                
                
                if ( pre <= orcamento) {
                    Encomenda e = new Encomenda(aux,0,pre);
                    res.add(e);
                }
                
                pre = st;
                
                
            }
        }
        if (res.isEmpty()) return null;
        
        return res;
    }
    
    
    private Encomenda confOtima(double preco, double orcamento ) {
        List<Encomenda> aux = everyCaseBasic(conf, preco, orcamento);
        
        
        Encomenda best = null;
        if ( aux == null) {JOptionPane.showMessageDialog(null,"Não foi possivel fazer uma configuracao com esse orçamento");return null;}
        double x = 0;
        for ( Encomenda e : aux) {
            
            e.addComponente(conf.getFirstComponente("Pintura").clone());
            e.addComponente(conf.getFirstComponente("Motor").clone());
            e.addComponente(conf.getFirstComponente("Modelo").clone());
            Encomenda enc = new Encomenda();
            enc = everyCaseExtra(e,e.getPreco(),orcamento);
            System.out.println("PRECO OLE " + enc.getPreco() +" \n"+ "orca" +orcamento + " \n");
            if (enc.getPreco() > x) {x = enc.getPreco(); best = enc; }
            
        }
        System.out.println("PRECOXX " + x + " preco best"+best.getPreco()+" \n");
        this.preco = best.getPreco();
        return best;
    }
    
    // Da o preco de um certo componente
    private double filterPreco(Componente c) {
        List<Componente> l = map.getLista(c.getTipo());
        for ( Componente aux : l) {
            if ( aux.getDesignacao().equals(c.getDesignacao()))
                return aux.getPreco();
        }
        return 0;
    }
    
    private String createMessage(String s1, String s2) {
        return ("O elemento "+s1+" e o elemento "+s2+" não são compatíveis.\n");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu_interior = new javax.swing.JCheckBoxMenuItem();
        panel_menu2 = new javax.swing.JPanel();
        button_personalizado = new javax.swing.JButton();
        button_pacotes = new javax.swing.JButton();
        button_voltarMenu1 = new javax.swing.JButton();
        panel_menu1 = new javax.swing.JPanel();
        button_iniciar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        button_registar = new javax.swing.JButton();
        button_terminar = new javax.swing.JButton();
        panel_pintura = new javax.swing.JPanel();
        box_pintura = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        button_proxMotor = new javax.swing.JButton();
        button_voltarMenu2 = new javax.swing.JButton();
        panel_motor = new javax.swing.JPanel();
        button_voltarPintura = new javax.swing.JButton();
        box_motor = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        button_proxJantes = new javax.swing.JButton();
        panel_jantes = new javax.swing.JPanel();
        button_voltarMotor = new javax.swing.JButton();
        box_jantes = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        button_proxPneus = new javax.swing.JButton();
        button_otima = new javax.swing.JButton();
        panel_pneus = new javax.swing.JPanel();
        button_proxInterior = new javax.swing.JButton();
        button_voltarJantes = new javax.swing.JButton();
        box_pneus = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        panel_interiores = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        check_bancos = new javax.swing.JCheckBox();
        check_painel = new javax.swing.JCheckBox();
        check_gps = new javax.swing.JCheckBox();
        check_bagagem = new javax.swing.JCheckBox();
        check_charger = new javax.swing.JCheckBox();
        check_som = new javax.swing.JCheckBox();
        button_proxExterior = new javax.swing.JButton();
        button_voltarPneus = new javax.swing.JButton();
        panel_exteriores = new javax.swing.JPanel();
        check_bicicletas = new javax.swing.JCheckBox();
        check_reboque = new javax.swing.JCheckBox();
        check_farois = new javax.swing.JCheckBox();
        button_proxCompra = new javax.swing.JButton();
        button_voltarInteriores = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        check_descapotavel = new javax.swing.JCheckBox();
        check_portas = new javax.swing.JCheckBox();
        check_aileron = new javax.swing.JCheckBox();
        panel_pacotes = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        button_newPacotes = new javax.swing.JButton();
        box_pacotes = new javax.swing.JComboBox<>();
        panel_final = new javax.swing.JPanel();
        label_preco = new javax.swing.JLabel();
        button_guardar = new javax.swing.JButton();
        button_voltaMenu1 = new javax.swing.JButton();
        panel_admin = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        box_admin = new javax.swing.JComboBox<>();
        text_quant = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        button_stock = new javax.swing.JButton();
        text_modelo = new javax.swing.JTextField();
        button_addModelo = new javax.swing.JButton();
        button_adminToMenu = new javax.swing.JButton();
        panel_modelos = new javax.swing.JPanel();
        box_modelos = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        button_proxModelo = new javax.swing.JButton();
        button_voltarModelo = new javax.swing.JButton();

        menu_interior.setSelected(true);
        menu_interior.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        button_personalizado.setText("Personalizado");
        button_personalizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_personalizadoActionPerformed(evt);
            }
        });

        button_pacotes.setText("Pacotes");
        button_pacotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pacotesActionPerformed(evt);
            }
        });

        button_voltarMenu1.setText("Voltar");
        button_voltarMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarMenu1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_menu2Layout = new javax.swing.GroupLayout(panel_menu2);
        panel_menu2.setLayout(panel_menu2Layout);
        panel_menu2Layout.setHorizontalGroup(
            panel_menu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_menu2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_voltarMenu1)
                .addGap(29, 29, 29))
            .addGroup(panel_menu2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(button_personalizado)
                .addGap(78, 78, 78)
                .addComponent(button_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );
        panel_menu2Layout.setVerticalGroup(
            panel_menu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_menu2Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(panel_menu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_personalizado, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(button_voltarMenu1)
                .addContainerGap())
        );

        button_iniciar.setText("Iniciar sessão");
        button_iniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_iniciarActionPerformed(evt);
            }
        });

        jLabel1.setText("BEM-VINDO");

        button_registar.setText("Registar");
        button_registar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_registarActionPerformed(evt);
            }
        });

        button_terminar.setText("Terminar Sessão");
        button_terminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_menu1Layout = new javax.swing.GroupLayout(panel_menu1);
        panel_menu1.setLayout(panel_menu1Layout);
        panel_menu1Layout.setHorizontalGroup(
            panel_menu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_menu1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(button_iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(button_terminar)
                .addGap(33, 33, 33))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_menu1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_registar)
                .addGap(54, 54, 54))
            .addGroup(panel_menu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panel_menu1Layout.createSequentialGroup()
                    .addGap(162, 162, 162)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(137, Short.MAX_VALUE)))
        );
        panel_menu1Layout.setVerticalGroup(
            panel_menu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_menu1Layout.createSequentialGroup()
                .addContainerGap(131, Short.MAX_VALUE)
                .addGroup(panel_menu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_terminar))
                .addGap(34, 34, 34)
                .addComponent(button_registar)
                .addGap(22, 22, 22))
            .addGroup(panel_menu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panel_menu1Layout.createSequentialGroup()
                    .addGap(59, 59, 59)
                    .addComponent(jLabel1)
                    .addContainerGap(194, Short.MAX_VALUE)))
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel2.setText("Escolha a pintura");

        button_proxMotor.setText("Próximo");
        button_proxMotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxMotorActionPerformed(evt);
            }
        });

        button_voltarMenu2.setText("Voltar");
        button_voltarMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarMenu2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_pinturaLayout = new javax.swing.GroupLayout(panel_pintura);
        panel_pintura.setLayout(panel_pinturaLayout);
        panel_pinturaLayout.setHorizontalGroup(
            panel_pinturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pinturaLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(box_pintura, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_pinturaLayout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(87, 87, 87))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_pinturaLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(button_voltarMenu2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_proxMotor)
                .addContainerGap())
        );
        panel_pinturaLayout.setVerticalGroup(
            panel_pinturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pinturaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(39, 39, 39)
                .addComponent(box_pintura, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addGroup(panel_pinturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxMotor)
                    .addComponent(button_voltarMenu2))
                .addGap(27, 27, 27))
        );

        button_voltarPintura.setText("Voltar");
        button_voltarPintura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarPinturaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel3.setText("Escolha o motor");

        button_proxJantes.setText("Próximo");
        button_proxJantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxJantesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_motorLayout = new javax.swing.GroupLayout(panel_motor);
        panel_motor.setLayout(panel_motorLayout);
        panel_motorLayout.setHorizontalGroup(
            panel_motorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_motorLayout.createSequentialGroup()
                .addContainerGap(123, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(87, 87, 87))
            .addGroup(panel_motorLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(button_voltarPintura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_proxJantes)
                .addContainerGap())
            .addGroup(panel_motorLayout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(box_motor, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_motorLayout.setVerticalGroup(
            panel_motorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_motorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(43, 43, 43)
                .addComponent(box_motor, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addGroup(panel_motorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxJantes)
                    .addComponent(button_voltarPintura))
                .addGap(27, 27, 27))
        );

        button_voltarMotor.setText("Voltar");
        button_voltarMotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarMotorActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel4.setText("Escolha as jantes");

        button_proxPneus.setText("Próximo");
        button_proxPneus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxPneusActionPerformed(evt);
            }
        });

        button_otima.setText("Configurar Automaticamente");
        button_otima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_otimaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_jantesLayout = new javax.swing.GroupLayout(panel_jantes);
        panel_jantes.setLayout(panel_jantesLayout);
        panel_jantesLayout.setHorizontalGroup(
            panel_jantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_jantesLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(box_jantes, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_otima)
                .addGap(33, 33, 33))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_jantesLayout.createSequentialGroup()
                .addContainerGap(164, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(87, 87, 87))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_jantesLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(button_voltarMotor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_proxPneus)
                .addContainerGap())
        );
        panel_jantesLayout.setVerticalGroup(
            panel_jantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_jantesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(39, 39, 39)
                .addGroup(panel_jantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(box_jantes, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_otima, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                .addGroup(panel_jantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxPneus)
                    .addComponent(button_voltarMotor))
                .addGap(27, 27, 27))
        );

        button_proxInterior.setText("Próximo");
        button_proxInterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxInteriorActionPerformed(evt);
            }
        });

        button_voltarJantes.setText("Voltar");
        button_voltarJantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarJantesActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel5.setText("Escolha os pneus");

        javax.swing.GroupLayout panel_pneusLayout = new javax.swing.GroupLayout(panel_pneus);
        panel_pneus.setLayout(panel_pneusLayout);
        panel_pneusLayout.setHorizontalGroup(
            panel_pneusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pneusLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(box_pneus, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_pneusLayout.createSequentialGroup()
                .addContainerGap(134, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(87, 87, 87))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_pneusLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(button_voltarJantes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_proxInterior)
                .addContainerGap())
        );
        panel_pneusLayout.setVerticalGroup(
            panel_pneusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pneusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(39, 39, 39)
                .addComponent(box_pneus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                .addGroup(panel_pneusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxInterior)
                    .addComponent(button_voltarJantes))
                .addGap(27, 27, 27))
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel6.setText("Escolha os interiores");

        check_bancos.setText("Bancos Aquecidos");

        check_painel.setText("Painel Carbono");

        check_gps.setText("GPS");

        check_bagagem.setText("Compartimento Bagagem extra");

        check_charger.setText("Wireless Charger");

        check_som.setText("Sistema Ultra Som");

        button_proxExterior.setText("Próximo");
        button_proxExterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxExteriorActionPerformed(evt);
            }
        });

        button_voltarPneus.setText("Voltar");
        button_voltarPneus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarPneusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_interioresLayout = new javax.swing.GroupLayout(panel_interiores);
        panel_interiores.setLayout(panel_interioresLayout);
        panel_interioresLayout.setHorizontalGroup(
            panel_interioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_interioresLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panel_interioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check_som)
                    .addComponent(check_charger)
                    .addComponent(check_bagagem)
                    .addComponent(check_gps)
                    .addComponent(check_painel)
                    .addComponent(check_bancos)
                    .addComponent(button_voltarPneus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_interioresLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(panel_interioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_proxExterior)
                    .addComponent(jLabel6))
                .addGap(28, 28, 28))
        );
        panel_interioresLayout.setVerticalGroup(
            panel_interioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_interioresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(check_bancos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_painel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_gps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_bagagem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_charger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_som)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(panel_interioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxExterior)
                    .addComponent(button_voltarPneus))
                .addGap(26, 26, 26))
        );

        check_bicicletas.setText("Porta Bicicletas");

        check_reboque.setText("Engate Reboque");

        check_farois.setText("Faróis Xenon");

        button_proxCompra.setText("Próximo");
        button_proxCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxCompraActionPerformed(evt);
            }
        });

        button_voltarInteriores.setText("Voltar");
        button_voltarInteriores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarInterioresActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel9.setText("Escolha os exteriores");

        check_descapotavel.setText("Descapotável");

        check_portas.setText("Duas portas");

        check_aileron.setText("Aileron Traseiro");

        javax.swing.GroupLayout panel_exterioresLayout = new javax.swing.GroupLayout(panel_exteriores);
        panel_exteriores.setLayout(panel_exterioresLayout);
        panel_exterioresLayout.setHorizontalGroup(
            panel_exterioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_exterioresLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panel_exterioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check_farois)
                    .addComponent(check_reboque)
                    .addComponent(check_bicicletas)
                    .addComponent(check_aileron)
                    .addComponent(check_portas)
                    .addComponent(check_descapotavel)
                    .addComponent(button_voltarInteriores))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_exterioresLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(panel_exterioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_proxCompra)
                    .addComponent(jLabel9))
                .addGap(28, 28, 28))
        );
        panel_exterioresLayout.setVerticalGroup(
            panel_exterioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_exterioresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(check_descapotavel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_portas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_aileron)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_bicicletas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_reboque)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check_farois)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(panel_exterioresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxCompra)
                    .addComponent(button_voltarInteriores))
                .addGap(26, 26, 26))
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel10.setText("Escolha o pacote");

        button_newPacotes.setText("Seguinte");
        button_newPacotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newPacotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_pacotesLayout = new javax.swing.GroupLayout(panel_pacotes);
        panel_pacotes.setLayout(panel_pacotesLayout);
        panel_pacotesLayout.setHorizontalGroup(
            panel_pacotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pacotesLayout.createSequentialGroup()
                .addGroup(panel_pacotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_pacotesLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel10))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel_pacotesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(box_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addContainerGap(82, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_pacotesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_newPacotes)
                .addGap(52, 52, 52))
        );
        panel_pacotesLayout.setVerticalGroup(
            panel_pacotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_pacotesLayout.createSequentialGroup()
                .addGroup(panel_pacotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_pacotesLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel10))
                    .addGroup(panel_pacotesLayout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(box_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(85, 85, 85)
                .addComponent(button_newPacotes)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        label_preco.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        button_guardar.setText("Guardar Configuração");
        button_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_guardarActionPerformed(evt);
            }
        });

        button_voltaMenu1.setText("Voltar menu principal");
        button_voltaMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltaMenu1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_finalLayout = new javax.swing.GroupLayout(panel_final);
        panel_final.setLayout(panel_finalLayout);
        panel_finalLayout.setHorizontalGroup(
            panel_finalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_finalLayout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(label_preco, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_guardar)
                .addGap(75, 75, 75))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_finalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_voltaMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        panel_finalLayout.setVerticalGroup(
            panel_finalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_finalLayout.createSequentialGroup()
                .addContainerGap(167, Short.MAX_VALUE)
                .addGroup(panel_finalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_finalLayout.createSequentialGroup()
                        .addComponent(label_preco, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_finalLayout.createSequentialGroup()
                        .addComponent(button_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)))
                .addComponent(button_voltaMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jLabel7.setText("MENU ADMINISTRADOR");

        jLabel8.setText("Quantidade de Stock");

        button_stock.setText("Adicionar Stock");
        button_stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_stockActionPerformed(evt);
            }
        });

        button_addModelo.setText("Adicionar Modelo");
        button_addModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addModeloActionPerformed(evt);
            }
        });

        button_adminToMenu.setText("Voltar");
        button_adminToMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_adminToMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_adminLayout = new javax.swing.GroupLayout(panel_admin);
        panel_admin.setLayout(panel_adminLayout);
        panel_adminLayout.setHorizontalGroup(
            panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_adminLayout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(jLabel7)
                .addContainerGap(172, Short.MAX_VALUE))
            .addGroup(panel_adminLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(box_admin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text_modelo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_stock)
                    .addComponent(jLabel8)
                    .addComponent(text_quant, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_addModelo))
                .addGap(116, 116, 116))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_adminLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_adminToMenu)
                .addContainerGap())
        );
        panel_adminLayout.setVerticalGroup(
            panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_adminLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(text_quant, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(box_admin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(button_stock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(panel_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(text_modelo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_addModelo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(16, 16, 16)
                .addComponent(button_adminToMenu)
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel11.setText("Escolha o modelo do carro:");

        button_proxModelo.setText("Próximo");
        button_proxModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proxModeloActionPerformed(evt);
            }
        });

        button_voltarModelo.setText("Voltar");
        button_voltarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_voltarModeloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_modelosLayout = new javax.swing.GroupLayout(panel_modelos);
        panel_modelos.setLayout(panel_modelosLayout);
        panel_modelosLayout.setHorizontalGroup(
            panel_modelosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_modelosLayout.createSequentialGroup()
                .addGroup(panel_modelosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panel_modelosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel_modelosLayout.createSequentialGroup()
                            .addGap(80, 80, 80)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel_modelosLayout.createSequentialGroup()
                            .addGap(206, 206, 206)
                            .addComponent(box_modelos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_modelosLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(button_voltarModelo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_proxModelo)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        panel_modelosLayout.setVerticalGroup(
            panel_modelosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_modelosLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(box_modelos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                .addGroup(panel_modelosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proxModelo)
                    .addComponent(button_voltarModelo))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_menu2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_menu1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pintura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_motor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_jantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pneus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_interiores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_exteriores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_final, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_admin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_modelos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_menu2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_menu1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pintura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_motor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_jantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pneus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_interiores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_exteriores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_pacotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_final, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_admin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel_modelos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void sessaoStatus() {
        if (func.getNome().equals("admin")) {
                panel_admin.setVisible(true);
                this.asAdmin = true;
            }
            else {
                panel_modelos.setVisible(true);
                this.asFunc = true;
            }
            panel_menu1.setVisible(false);
            JOptionPane.showMessageDialog(null,"Sessão iniciada com sucesso");
    }
    
    
    private void button_iniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_iniciarActionPerformed
        //ir do menu1 para o menu2
        if ( asFunc || asAdmin) sessaoStatus();
        else {
        String username = JOptionPane.showInputDialog(this,"Escreva o seu UserName: ");
        
        String pass = JOptionPane.showInputDialog(this,"Escreva a sua Password: ");
        
        if (mapFunc.iniciarSessao(username,pass)) {
            func = new Funcionario();
            func.setNome(username);
            func.setPass(pass);
            sessaoStatus();
        }
        else 
            JOptionPane.showMessageDialog(null,"Username não existe ou pass errada.");
        }
        
    }//GEN-LAST:event_button_iniciarActionPerformed

    private void button_voltarMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarMenu1ActionPerformed
        //ir do menu2 para os modelos
        resetStock("Modelo");
        conf = new Gestao_Componentes();
       
        panel_modelos.setVisible(true);
        panel_menu2.setVisible(false);
        
        System.out.println(map.printMap());
    }//GEN-LAST:event_button_voltarMenu1ActionPerformed

    private void button_personalizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_personalizadoActionPerformed
        //ir do menu2 para a pintura
        String input = JOptionPane.showInputDialog(this,"Defina o seu orçamento: ");
        
        func.setOrcamento(Integer.parseInt(input));
        panel_pintura.setVisible(true);
        panel_menu2.setVisible(false);
    }//GEN-LAST:event_button_personalizadoActionPerformed
    private void resetStock(String tipo) {
        String aux;
        if (tipo.equals("Interior") || tipo.equals("Exterior")) {
            for (Componente c : conf.getLista(tipo)) {
                aux = c.getDesignacao();
                if ( aux != null) this.map.getComponente(tipo,aux).increaseStock();
            }
        }
        else {
        aux = conf.getFirst(tipo);
        if ( aux != null) this.map.getComponente(tipo,aux).increaseStock();
        }
    }
    private void button_voltarMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarMenu2ActionPerformed
        // Ir da pintura para o menu2
        panel_menu2.setVisible(true);
        panel_pintura.setVisible(false);
        conf.remove("Pintura");
        
    }//GEN-LAST:event_button_voltarMenu2ActionPerformed

    private void button_proxMotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxMotorActionPerformed
        // ir da pintura para o motor
        
        String d = box_pintura.getSelectedItem().toString();
        if (this.map.getComponente("Pintura", d).hasStock()) {
            conf.addComponente(map.getComponente("Pintura",d).clone());
            panel_motor.setVisible(true);
            panel_pintura.setVisible(false);
        }
        else JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!");
        
    }//GEN-LAST:event_button_proxMotorActionPerformed

    private void button_voltarPinturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarPinturaActionPerformed
        // Ir do motor para a pintura
        panel_pintura.setVisible(true);
        panel_motor.setVisible(false);
           
        conf.remove("Motor");
        conf.remove("Pintura");
        
    }//GEN-LAST:event_button_voltarPinturaActionPerformed

    private void button_proxJantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxJantesActionPerformed
        // Ir do motor para jantes
        if (escolheuPacote != null) button_otima.setVisible(false) ;
        String d = box_motor.getSelectedItem().toString();
        if (this.map.getComponente("Motor", d).hasStock()) {
            conf.addComponente(map.getComponente("Motor",d).clone());
            panel_jantes.setVisible(true);
            panel_motor.setVisible(false);
        }
        else JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!");
    }//GEN-LAST:event_button_proxJantesActionPerformed

    private void button_voltarMotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarMotorActionPerformed
        // Ir das jantes para o motor
        panel_motor.setVisible(true);
        panel_jantes.setVisible(false);
        conf.remove("Jantes");
        conf.remove("Motor");
    }//GEN-LAST:event_button_voltarMotorActionPerformed

    private void button_proxPneusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxPneusActionPerformed
        // Ir das jantes para os pneus
        String d = box_jantes.getSelectedItem().toString();
        if (this.map.getComponente("Jantes", d).hasStock()) {
            conf.addComponente(map.getComponente("Jantes",d).clone());
            panel_pneus.setVisible(true);
            panel_jantes.setVisible(false);
        }
        else JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!");
    }//GEN-LAST:event_button_proxPneusActionPerformed

    private void button_proxInteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxInteriorActionPerformed
        // ir dos pneus para os interiores
        String d = box_pneus.getSelectedItem().toString();
        if (this.map.getComponente("Pneu", d).hasStock()) {
            conf.addComponente(map.getComponente("Pneu",d).clone());
        }
        else JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!");
        
        panel_pneus.setVisible(false);
        if (this.escolheuPacote == null) panel_interiores.setVisible(true);
        else {
            Pacote p = this.pacotes.getPacoteNome(escolheuPacote);
            this.conf.createTipo("Interior",p.getLista("Interior"));
            this.conf.createTipo("Exterior",p.getLista("Exterior"));
            this.preco = conf.precoTotal();
            this.conf.removeStock();
            
            label_preco.setText(String.valueOf(this.preco));
            panel_final.setVisible(true);
            JOptionPane.showMessageDialog(null,conf.printMap());
        }
    }//GEN-LAST:event_button_proxInteriorActionPerformed
    
    private void button_voltarJantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarJantesActionPerformed
        // ir dos pneus para as jantes
        panel_jantes.setVisible(true);
        panel_pneus.setVisible(false);
        conf.remove("Pneu");
        conf.remove("Jantes");
    }//GEN-LAST:event_button_voltarJantesActionPerformed

    private void button_voltarPneusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarPneusActionPerformed
        // ir dos interiores para os pneus    
        panel_pneus.setVisible(true);
        panel_interiores.setVisible(false);
        conf.remove("Interior");
        conf.remove("Pneu");
    }//GEN-LAST:event_button_voltarPneusActionPerformed
    private boolean validaEscolhas(List<Componente> escolhas,String t) {

        for (Componente x : escolhas) {
                String d = x.getDesignacao();
                if (isValidWithList(escolhas,x) != null) {
                    JOptionPane.showMessageDialog(null,isValidWithList(escolhas,x));
                    conf.remove(t);return false;
                }
                if (this.map.getComponente(t, d).hasStock() == false) {
                    
                    JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!\n");
                    conf.remove(t);
                    return false;
                }
            }
        if (escolhas.isEmpty() == false && faltaComponente(escolhas)!=null) {JOptionPane.showMessageDialog(null,faltaComponente(escolhas));return false;}
        return true;
    }
    private void button_proxExteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxExteriorActionPerformed
        // ir dos interiores para os exteriores
        String t = "Interior";
        List<Componente> escolhas = new ArrayList();
        boolean flag = true; //tem tudo stock
        
            
            if (check_bagagem.isSelected()) escolhas.add(map.getComponente(t,"Compartimento Bagagem").clone());
            if (check_charger.isSelected()) escolhas.add(map.getComponente(t,"Wireless Charger").clone());
            if (check_bancos.isSelected()) escolhas.add(map.getComponente(t,"Bancos Aquecidos").clone());
            if (check_gps.isSelected()) escolhas.add(map.getComponente(t,"GPS").clone());
            if (check_painel.isSelected()) escolhas.add(map.getComponente(t,"Painel Carbono").clone());
            
            flag = validaEscolhas(escolhas,"Interior");
            
            if (flag == true) {
                addListToOrder(escolhas);
                panel_exteriores.setVisible(true);
                panel_interiores.setVisible(false);
            }
            
       
    }//GEN-LAST:event_button_proxExteriorActionPerformed
    private void addListToOrder(List<Componente> escolhas) {
        if (escolhas.isEmpty()) return;
        for (Componente x : escolhas) { //adiciona as escolhas a encomenda
            conf.addComponente(x);      
        }
    }
    private void button_proxCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxCompraActionPerformed
        // ir dos exteriores para a compra
        String t = "Exterior";
        List<Componente> escolhas = new ArrayList();
        boolean flag = true;
        
            Componente c;
            if (check_aileron.isSelected()) escolhas.add(map.getComponente(t,"Aileron Traseiro").clone());
            if (check_bicicletas.isSelected()) escolhas.add(map.getComponente(t,"Adaptador Bicicletas").clone());
            if (check_descapotavel.isSelected()) escolhas.add(map.getComponente(t,"Descapotável").clone());
            if (check_farois.isSelected()) escolhas.add(map.getComponente(t,"Faróis Xenón").clone());
            if (check_portas.isSelected()) escolhas.add(map.getComponente(t,"Duas portas").clone());
            if (check_reboque.isSelected()) escolhas.add(map.getComponente(t,"Engate reboque").clone());
            
            flag = validaEscolhas(escolhas,"Exterior");
            
            if (flag == true) {
                
                addListToOrder(escolhas);
                System.out.println(conf.printMap());
                this.preco = conf.precoTotal();
                this.conf.removeStock();
                label_preco.setText(String.valueOf(this.preco));
                panel_final.setVisible(true);
                panel_exteriores.setVisible(false);
            }
    }//GEN-LAST:event_button_proxCompraActionPerformed

    private void button_voltarInterioresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarInterioresActionPerformed
        // ir dos exteriores para os interiores
        
        panel_interiores.setVisible(true);
        panel_exteriores.setVisible(false);
        conf.remove("Interior");
        conf.remove("Exterior");
    }//GEN-LAST:event_button_voltarInterioresActionPerformed

    private void button_pacotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pacotesActionPerformed
        // ir do menu2 para os pacotes
        String input = JOptionPane.showInputDialog(this,"Defina o seu orçamento: ");
        double orcamento = Double.parseDouble(input);
        func.setOrcamento(orcamento);
        panel_pacotes.setVisible(true);
        panel_menu2.setVisible(false);
    }//GEN-LAST:event_button_pacotesActionPerformed
    private void saveConf() {
        map.save();
        
    }
    private void button_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_guardarActionPerformed
        if (!saved) {
            Encomenda e = new Encomenda(conf,id,preco);
            encomendas.addEncomenda(e);
            saved = true;
        }
        
        saveConf();
    }//GEN-LAST:event_button_guardarActionPerformed

    private void button_voltaMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltaMenu1ActionPerformed
        //do final para menu 1
        this.conf = new Gestao_Componentes();
        this.preco = 0;
        this.saved = false;
        panel_final.setVisible(false);
        panel_menu1.setVisible(true);
        escolheuPacote = null;
        button_otima.setVisible(true);
    }//GEN-LAST:event_button_voltaMenu1ActionPerformed
   
    private void button_registarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_registarActionPerformed
        String username = JOptionPane.showInputDialog(this,"Escreva o seu UserName: ");
        
        String pass = JOptionPane.showInputDialog(this,"Escreva a sua Password: ");
        
        if (mapFunc.containsFunc(username)) JOptionPane.showMessageDialog(null,"Utilizador ja existe");
        else {
            Funcionario f = new Funcionario (username, pass) ;
            mapFunc.addFunc(f);
        }
    }//GEN-LAST:event_button_registarActionPerformed
    private void adicionarStock(String design,int quant) {
        this.map.addValueStock(design,quant);
    }
    private void button_stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_stockActionPerformed
        String s= "";
        if (text_quant.getText() != null) s = text_quant.getText() ;
        int quant = Integer.parseInt(s);
        
        String design = box_admin.getSelectedItem().toString();
        adicionarStock(design,quant);
    }//GEN-LAST:event_button_stockActionPerformed

    private void button_terminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terminarActionPerformed
        if (asAdmin || asFunc) {
            asAdmin = false;
            asFunc = false;
        }
        else JOptionPane.showMessageDialog(null,"Não há sessão iniciada");
    }//GEN-LAST:event_button_terminarActionPerformed

    private void button_voltarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_voltarModeloActionPerformed
        // vai dos modelos para o menu 1
        conf = new Gestao_Componentes();
        this.preco = 0;
        panel_menu1.setVisible(true);
        panel_modelos.setVisible(false);
    }//GEN-LAST:event_button_voltarModeloActionPerformed

    private void button_proxModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proxModeloActionPerformed
        //vai dos modelos para o menu2
        
        String d = box_modelos.getSelectedItem().toString();
        if (this.map.getComponente("Modelo", d).hasStock()) {
            conf.addComponente(map.getComponente("Modelo",d).clone());
            panel_menu2.setVisible(true);
            panel_modelos.setVisible(false);
        }
        else JOptionPane.showMessageDialog(null,"Produto "+d +" sem stock! Escolha outro!");
               
        
    }//GEN-LAST:event_button_proxModeloActionPerformed

    private void button_addModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addModeloActionPerformed
        if (text_modelo.getText() != null ) {
            Componente c = new Componente("Modelo",text_modelo.getText(),10);
            map.addComponente(c);
            initBoxes();
        }
        else JOptionPane.showMessageDialog(null,"Insira um modelo valido");
    }//GEN-LAST:event_button_addModeloActionPerformed

    private void button_adminToMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_adminToMenuActionPerformed
        System.out.println(map.getAll());
        panel_admin.setVisible(false);
        panel_menu1.setVisible(true);
        asAdmin = false;
        asFunc = false;
    }//GEN-LAST:event_button_adminToMenuActionPerformed

    private void button_otimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_otimaActionPerformed
       
        Encomenda e = confOtima(conf.precoTotal(), func.getOrcamento());
        if ( e != null){
        panel_jantes.setVisible(false);
        panel_final.setVisible(true);
        
        label_preco.setText(String.valueOf(this.preco));
        JOptionPane.showMessageDialog(null,e.printMap());
        }
        
    }//GEN-LAST:event_button_otimaActionPerformed

    private void button_newPacotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newPacotesActionPerformed
        this.escolheuPacote = box_pacotes.getSelectedItem().toString();
        panel_pintura.setVisible(true);
        panel_pacotes.setVisible(false);
    }//GEN-LAST:event_button_newPacotesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu(map,pacotes,mapFunc).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> box_admin;
    private javax.swing.JComboBox<String> box_jantes;
    private javax.swing.JComboBox<String> box_modelos;
    private javax.swing.JComboBox<String> box_motor;
    private javax.swing.JComboBox<String> box_pacotes;
    private javax.swing.JComboBox<String> box_pintura;
    private javax.swing.JComboBox<String> box_pneus;
    private javax.swing.JButton button_addModelo;
    private javax.swing.JButton button_adminToMenu;
    private javax.swing.JButton button_guardar;
    private javax.swing.JButton button_iniciar;
    private javax.swing.JButton button_newPacotes;
    private javax.swing.JButton button_otima;
    private javax.swing.JButton button_pacotes;
    private javax.swing.JButton button_personalizado;
    private javax.swing.JButton button_proxCompra;
    private javax.swing.JButton button_proxExterior;
    private javax.swing.JButton button_proxInterior;
    private javax.swing.JButton button_proxJantes;
    private javax.swing.JButton button_proxModelo;
    private javax.swing.JButton button_proxMotor;
    private javax.swing.JButton button_proxPneus;
    private javax.swing.JButton button_registar;
    private javax.swing.JButton button_stock;
    private javax.swing.JButton button_terminar;
    private javax.swing.JButton button_voltaMenu1;
    private javax.swing.JButton button_voltarInteriores;
    private javax.swing.JButton button_voltarJantes;
    private javax.swing.JButton button_voltarMenu1;
    private javax.swing.JButton button_voltarMenu2;
    private javax.swing.JButton button_voltarModelo;
    private javax.swing.JButton button_voltarMotor;
    private javax.swing.JButton button_voltarPintura;
    private javax.swing.JButton button_voltarPneus;
    private javax.swing.JCheckBox check_aileron;
    private javax.swing.JCheckBox check_bagagem;
    private javax.swing.JCheckBox check_bancos;
    private javax.swing.JCheckBox check_bicicletas;
    private javax.swing.JCheckBox check_charger;
    private javax.swing.JCheckBox check_descapotavel;
    private javax.swing.JCheckBox check_farois;
    private javax.swing.JCheckBox check_gps;
    private javax.swing.JCheckBox check_painel;
    private javax.swing.JCheckBox check_portas;
    private javax.swing.JCheckBox check_reboque;
    private javax.swing.JCheckBox check_som;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel label_preco;
    private javax.swing.JCheckBoxMenuItem menu_interior;
    private javax.swing.JPanel panel_admin;
    private javax.swing.JPanel panel_exteriores;
    private javax.swing.JPanel panel_final;
    private javax.swing.JPanel panel_interiores;
    private javax.swing.JPanel panel_jantes;
    private javax.swing.JPanel panel_menu1;
    private javax.swing.JPanel panel_menu2;
    private javax.swing.JPanel panel_modelos;
    private javax.swing.JPanel panel_motor;
    private javax.swing.JPanel panel_pacotes;
    private javax.swing.JPanel panel_pintura;
    private javax.swing.JPanel panel_pneus;
    private javax.swing.JTextField text_modelo;
    private javax.swing.JTextField text_quant;
    // End of variables declaration//GEN-END:variables
}
