package juegoplataforma;
import entity.Entidad;
import entity.*;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import objeto.Objeto;

/**
 * 
 * Clase que representa los niveles del juego. Manejada a los NPCs y a los objetos.
 * @author Leonardo Aguilera, Leonardo Lizcano, Leonardo Vergara, Henry Caicedo, Fernando Acuña
 * 
 */
public class Nivel {
    public LinkedList<Entidad> entidades;
    public LinkedList<Objeto> objetos;
    public ArrayList<Npc> npcs;
    public EtapaNivel etapa;
    public int jugadorPosX, jugadorPosY;
    //Nuevos
    public Cinematica cinematica;
    
    public objeto.Puerta puerta;
    public objeto.Llave llave;
    public int numDiamantes;
    /**
     * Crea un nuevo nivel con sus listas de NPCs, objetos y entidades en general.
     */
    public Nivel(int i){
        
    }
    public Nivel(){
        npcs=new ArrayList();
        entidades=new LinkedList();
        objetos=new LinkedList();
        etapa=EtapaNivel.cinematica;
        cinematica=new Cinematica(new File("src\\res\\Niveles\\Nivel_"+JuegoPlataforma.nivel+"\\Cinematica"));
        initConfig();
        //Nuevos
        numDiamantes=0;
    }
    /**
     * Lleva a cabo la renderización de las entidades en general, NPCs en específico y de los objetos de este nivel.
     * <p>
     * Invoca a los métodos {@link entity.Entidad#render(java.awt.Graphics) } para las entidades, a 
     * {@link entity.Npc#render(java.awt.Graphics, java.awt.Graphics) } para los NPCs, y a 
     * {@link objeto.Objeto#render(java.awt.Graphics) } para los objetos.
     * </p>
     * @param g gráficos empleados para dibujar los elementos del nivel
     * @param g2 gráficos empleados para dibujar las conversaciones de los NPCs, si las tienen
     * @see JuegoPlataforma#getCurrentLevel() 
     * 
     */
    public synchronized void render(Graphics g,Graphics g2) {
        if(etapa!=EtapaNivel.cinematica){
            //System.out.println("Entro");
            for (Objeto ti : objetos) {
                if(ti.getId()==Id.diamante){
                    objeto.Diamante d = (objeto.Diamante)ti;
                    if(!d.isRecogido()) ti.render(g);
                }else
                    ti.render(g);
            }
            for (Npc npc : npcs) 
                npc.render(g,g2);
        }else{
            if(!cinematica.render(g2))
                playLevel();
        }
    }
    /**
     * Produce el movimiento de las entidades en general, NPCs en específico y de los objetos de este nivel.
     * <p>
     * Llama a los métodos {@link entity.Entidad#tick() } para las entidades, a {@link entity.Npc#tick() } para los NPCs, 
     * y a {@link objeto.Objeto#tick() } para los objetos.
     * </p>
     * 
     */
    public synchronized void tick() {
        if(etapa!=EtapaNivel.cinematica){
            for (Objeto ti : objetos)
                ti.tick();

            for (Npc npc : npcs)
                npc.tick();
        }else{
            cinematica.tick();
        }
    }
    /**
     * Añade un nuevo elemento a la lista de NPCs.
     * @param npc NPC a añadir.
     */
    public void addNpc(Npc npc){
        npcs.add(npc);
    }
    /**
     * Añade un nuevo elemento a la lista de entidades.
     * @param en entidad a añadir.
     */
    public void addEntidad(Entidad en) {
        entidades.add(en);
    }
    /**
     * Elimina a un elemento de la lista de entidades.
     * @param en entidad a ser removida.
     */
    public void removeEntidad(Entidad en) {
        entidades.remove(en);
    }
    /**
     * Añade un nuevo elemento a la lista de objetos.
     * @param ti objeto a añadir.
     */
    public void addObjeto(Objeto ti) {
        objetos.add(ti);
    }
    /**
     * Elimina a un elemento de la lista de objetos.
     * @param ti objeto a ser removido.
     */
    public void removeObjeto(Objeto ti) {
        objetos.remove(ti);
    }
    /**
     * Asigna a la clase {@link JuegoPlataforma} un fondo y un soundtrack específicos, dependiendo de 
     * las indicaciones dadas en el archivo "c.config" de la carpeta de cada nivel.
     * <p>
     * El archivo "c.config" posee dos indicaciones:
     * </p>
     * <p>
     * La primera es el índice o el nombre del archivo de imagen que representa el fondo.
     * </p>
     * <p>
     * La segunda es el índice o el nombre del archivo de sonido que representa la música del nivel.
     * </p>
     * @see JuegoPlataforma#selectMusic(int) 
     * @see JuegoPlataforma#setFondo(int) 
     */
    private void initConfig(){
        String dir="src\\res\\Niveles\\Nivel_"+JuegoPlataforma.nivel;
        File config=new File(dir,"c.config");
        if (config.exists() && config.isFile()) {
            try {
                BufferedReader bf=new BufferedReader(new FileReader(config));
                String line,line2[]=new String[2];
                while((line=bf.readLine())!=null){
                    line2=line.split("\t");
                    if (line2.length==2) {
                        switch(line2[0]){
                            case "Fondo":
                                try{
                                    JuegoPlataforma.setFondo(Integer.parseInt(line2[1]));
                                }catch(Exception t){
                                    if ((t instanceof IllegalArgumentException)) {
                                        JuegoPlataforma.setFondo(line2[1]);
                                    }else
                                    JuegoPlataforma.setFondo(3);
                                }
                                break;
                            case "Sonido":
                                try{
                                    JuegoPlataforma.selectMusic(Integer.parseInt(line2[1])-1);
                                }catch(Exception t){
                                    if ((t instanceof IllegalArgumentException)) {
                                        System.out.println("SOnido2---  "+line2[1]);
                                        JuegoPlataforma.selectMusic(line2[1]);
                                    }else
                                        JuegoPlataforma.selectMusic(4);
                                }
                        }
                    }
                }
                
            }catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Problemas con la configuración");
                Logger.getLogger(Nivel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void nextScene() {
        cinematica.continuar();
    }
    public void playLevel() {
        etapa=EtapaNivel.tranquiloDeLaVida;
        cinematica.recharge();
    }
    public void actualizarNivel(Npc npc){
        switch(JuegoPlataforma.nivel){
            case 1:
                if(npc.id==Id.robot){
                    if(npc.cours.equals(npc.getLastConversación()) && !npc.cours.isSuperada()){
                        int i=JuegoPlataforma.handler.jugador.x<npc.x? 1:-1;// :'D
                        llave.setY(npc.y);
                        llave.setX(npc.x+64*i);
                        llave.setDisponible(true);
                        puerta.setSolid(false);
                        for(Npc n: npcs){
                            if(n.id==Id.man){
                                for(interaction.Conversación c: n.conversaciones){
                                    if(!c.equals(n.getLastConversación()))
                                        c.setDisponible(false);
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                if(!npc.cours.equals(npc.getLastConversación()))
                    return;
                
                if(npc.id==Id.aventurero || npc.id==Id.man){
                    Npc segundo=null;
                    
                    if(npc.id==Id.aventurero)
                        for(Npc n: npcs){
                            if(n.id==Id.man){
                                segundo=n;
                                break;
                            }
                        }
                    else if(npc.id==Id.man)
                        for(Npc n: npcs){
                            if(n.id==Id.aventurero){
                                segundo=n;
                                break;
                            }
                        }
                    if(segundo!=null && segundo.getLastConversación().isSuperada()){
                        for(Npc n: npcs)
                            if(n.id==Id.robot){
                                for(interaction.Conversación c: n.conversaciones){
                                    if(!c.equals(n.getLastConversación()))
                                        c.setDisponible(false);
                                }
                            }
                    }
                }else if(npc.id==Id.robot){
                    JuegoPlataforma.handler.nextLevel();
                }
                break;
            case 3:
                
                break;
            case 4:
                
                break;
            case 5:
                
                break;
            case 6:
        }
    }
}