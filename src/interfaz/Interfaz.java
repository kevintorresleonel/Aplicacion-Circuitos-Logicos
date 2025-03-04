package interfaz;

/*
Integrantes de grupo: 
Torres Kevin
Ramirez Leonardo
Ramos Mateo
Gonzales Lauren 
 */
import circuito.Circuitos;
import static circuito.Circuitos.expresionAPosfija;
import componentes.Cables;
import componentes.Leds;
import componentes.Switches;
import compuertas.Compuertas;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import serializable.Serializa;

public class Interfaz extends javax.swing.JFrame implements NombreCompuerta {

    // Instancia de la clase Circuitos, que maneja los circuitos lógicos
    Circuitos circuito = new Circuitos(); ///serializarr
    //private ArrayList<Compuertas> compuertas = new ArrayList<>();  //arreglo donde almecenar las compuertas 
    private Compuertas arrastrarCompuerta = null; //para tener la compuerta que se esta deslizando
    private Compuertas clickCompuerta = null; //para tener la compuerta que se esta likeando para eliminar
    private int offsetX, offsetY;
    private Leds arrastrarLed = null; // LED que se está deslizando
    private Switches arrastredSwitch = null; //Switch que se está arrastrando
    private Cables arrastrarCable = null; // Ahora se maneja correctamente el arrastre de cables

    private String expresion; // aqui vamos a guardar la expresion

    CompuertaLogica compuert;
    private boolean modoDibujarCable = false;// modo dibujar cable
    private boolean modoSimulacion = false;  // para facilitar el modo simulacion 
    private boolean modoMoverCableXY = false;
    private boolean modoMoverCableX2Y2 = false;

    private int xInicioCable, yInicioCable, xFinCable, yFinCable; // coordenadas x,y y x1,y1

    private static final int POSICION_INICIAL_X = 100;
    private static final int POSICION_INICIAL_Y = 100;

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem item1 = new JMenuItem("Eliminar compuerta");
    JMenuItem item2 = new JMenuItem("Añadir 3 entradas");
    JMenuItem item3 = new JMenuItem("Añadir 4 entradas");

    // Menús emergentes para LEDs, Switches y Cables
    JPopupMenu ledPopupMenu = new JPopupMenu();
    JMenuItem eliminarLed = new JMenuItem("Eliminar LED");

    JPopupMenu switchPopupMenu = new JPopupMenu();
    JMenuItem eliminarSwitch = new JMenuItem("Eliminar Switch");

    JPopupMenu cablePopupMenu = new JPopupMenu();
    JMenuItem eliminarCable = new JMenuItem("Eliminar Cable");

    private int index = 0;  // Índice para llevar el control de las compuertas o componentes

    public Interfaz() {
        initComponents();

        popupMenu.add(item1);
        popupMenu.add(item2);
        popupMenu.add(item3);

        // Agregar opciones de popup para LEDs, Switches y Cables
        ledPopupMenu.add(eliminarLed);
        switchPopupMenu.add(eliminarSwitch);
        cablePopupMenu.add(eliminarCable);

        // En el constructor, asegurarte de que los listeners estén bien configurados
        eliminarLed.addActionListener(e -> {
            if (arrastrarLed != null) {
                clickEliminarLed(arrastrarLed);
            }
        });

        eliminarSwitch.addActionListener(e -> {
            if (arrastredSwitch != null) {
                clickEliminarSwitch(arrastredSwitch);
            }
        });

        eliminarCable.addActionListener(e -> {
            if (arrastrarCable != null) {
                clickEliminarCable(arrastrarCable);
            }
        });

        // Añade un mouse listener para detectar acciones del mouse sobre el panel
        Panel.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Se ejecuta cuando el botón del mouse es presionado

                if (!modoSimulacion) { // Solo si no está en modo simulación
                    if (modoDibujarCable) { // Si el modo de dibujar cables está activado
                        // Guardamos las coordenadas de lo que nos dan
                        xInicioCable = evt.getX();
                        yInicioCable = evt.getY();
                    } else {
                        // Si no es el modo de dibujar cable, se llama a manejar el clic normal

                        onMousePressed(evt);
                    }
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Se ejecuta cuando el botón del mouse es soltado

                if (!modoSimulacion) {
                    if (modoDibujarCable) {
                        // Guardamos las coordenadas finales del cable cuando el mouse es soltado

                        xFinCable = evt.getX();
                        yFinCable = evt.getY();
                        // Aquí podrías agregar la lógica para "guardar" el cable, si es necesario
                        circuito.agregarCable(xInicioCable, yInicioCable, xFinCable, yFinCable);
                        repaint();
                    } else {
                        onMouseReleased(evt);
                    }
                }
                repaint();

            }

        });

        // Configuración de MouseMotionListener para el draw, maneja el arrastre del mouse
        Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                // Se ejecuta mientras el mouse es arrastrado

                if (!modoSimulacion) {

                    if (modoDibujarCable) {
                        xFinCable = evt.getX();
                        yFinCable = evt.getY();
                        repaint();
                    } else {
                        onMouseDragged(evt);
                    }

                }

            }
        });

        // Configuración de MouseListener para detectar clics en el panel
        Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {//-----------------------------------------------------------------------------------------------------

                // Mostrar el menú solo si es un clic derecho
                // Detecta el tipo de clic (derecho o izquierdo) 
                if (SwingUtilities.isRightMouseButton(e) && modoSimulacion == false) {
                    onMouseLine(e);
                    // Si se hace clic derecho y no está en modo simulación, muestra el menú contextual para las compuertas

                }

                if (SwingUtilities.isLeftMouseButton(e) && modoSimulacion == true) {
                    // llamamos a la funcionalidad de los swiches ///////////---------------////////////---------------/////////////
                    circuito.iniciarSimulación(e.getX(), e.getY());
                    repaint();
                    // Si se hace clic izquierdo y está en modo simulación, llama a la función de simulación del circuito

                }
            }
        });

        // Acción del menú emergente "Eliminar compuerta" - item1
        item1.addActionListener(e -> {
            if (clickCompuerta != null) {

                circuito.eliminarCompuerta(clickCompuerta);// Elimina la compuerta de la lista
                clickCompuerta = null;  // Reinicia la referencia
                repaint();  // Vuelve a pintar el panel
                JOptionPane.showMessageDialog(null, "Compuerta eliminada, quedan " + circuito.getCompuertas().size() + " compuertas.");

            } else {
                JOptionPane.showMessageDialog(null, "No hay compuerta seleccionada.");
            }
        });

        item2.addActionListener(e -> {
            if (clickCompuerta != null) {

                index = circuito.getCompuertas().indexOf(clickCompuerta); // obtenemos el indice del objeto que esta en la lista 
                circuito.getCompuertas().get(index).setNumEntra(3); // aumentamos el numero de entradas de la compuerta 
                clickCompuerta = null;  // Reinicia la referencia
                repaint();  // Vuelve a pintar el panel
                index = 0;
            } else {
                JOptionPane.showMessageDialog(null, "No hay compuerta seleccionada.");
            }
        });

        item3.addActionListener(e -> {
            if (clickCompuerta != null) {
                index = circuito.getCompuertas().indexOf(clickCompuerta); // obtenemos el indice del objeto que esta en la lista 

                circuito.getCompuertas().get(index).setNumEntra(4); // aumentamos el numero de entradas de la compuerta 
                clickCompuerta = null;  // Reinicia la referencia
                repaint();  // Vuelve a pintar el panel

                index = 0;
            } else {
                JOptionPane.showMessageDialog(null, "No hay compuerta seleccionada.");
            }
        });

    }

// metodos 
    /*
    public void hacerZoom() {

    }

    public void moverLienzo() {

    }
     */
    // Cambios en los métodos de eliminación
    // Método para eliminar un LED cuando se hace clic en el botón 
    private void clickEliminarLed(Leds led) {
        // Elimina el LED del circuito
        circuito.eliminarLed(led);
        arrastrarLed = null; // Reiniciar la referencia
        repaint();
        JOptionPane.showMessageDialog(null, "LED eliminado.");
    }

    private void clickEliminarSwitch(Switches switches) {
        // Elimina el switch del circuito

        circuito.eliminarSwitch(switches);
        arrastredSwitch = null; // Reiniciar la referencia
        repaint();
        JOptionPane.showMessageDialog(null, "Switch eliminado.");
    }

    private void clickEliminarCable(Cables cable) {

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres eliminar este cable?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            // Elimina el cable del circuito
            circuito.eliminarCable(cable); // Asegúrate de que este método esté correctamente implementado
            // Actualiza la interfaz
            arrastrarCable = null;
            repaint(); // Vuelve a dibujar el panel para reflejar los cambios
            JOptionPane.showMessageDialog(null, "Cable Eliminado.");
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D gr = (Graphics2D) Panel.getGraphics();

        // Mejora del renderizado activando el antialiasing
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        Toolkit.getDefaultToolkit().sync();

        for (Compuertas compu : circuito.getCompuertas()) {
            compu.draw(gr);
        }

        // Dibujar cable si estamos en modo de dibujo
        if (modoDibujarCable) {
            gr.setColor(Color.BLACK);
            gr.setStroke(new BasicStroke(2));
            gr.drawLine(xInicioCable, yInicioCable, xFinCable, yFinCable);
        }

        for (Cables cable : circuito.getCables()) {
            cable.draw(gr);
        }

        // Dibujar LEDs
        for (Leds led : circuito.getLeds()) {
            led.draw(gr);
        }
        //Dibujar switches
        for (Switches switches : circuito.getSwitches()) {
            switches.draw(gr);
        }

    }

    private void onMousePressed(MouseEvent evt) {
        // Verifica si se hace clic en una compuerta y almacena la referencia para moverla

        for (Compuertas compu : circuito.getCompuertas()) {
            if (compu.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastrarCompuerta = compu;
                offsetX = evt.getX() - compu.getX(); // Calcula el desplazamiento con respecto a la compuerta
                offsetY = evt.getY() - compu.getY();
                break;
            }
        }
        // Verifica si se hace clic en un LED y almacena la referencia para moverlo

        for (Leds led : circuito.getLeds()) {
            if (led.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastrarLed = led;
                offsetX = evt.getX() - led.getX();
                offsetY = evt.getY() - led.getY();
                break;
            }
        }
        // Verifica si se está haciendo clic en un switch
        for (Switches switches : circuito.getSwitches()) {     // Verifica si se hace clic en un switch y almacena la referencia para moverlo

            if (switches.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastredSwitch = switches;
                offsetX = evt.getX() - switches.getX();
                offsetY = evt.getY() - switches.getY();
                break;
            }
        }
        // Verificar Cables-------------
        if (arrastredSwitch == null && arrastrarLed==null) {
            for (Cables cable : circuito.getCables()) {

                if (cable.estaEnLaLineaXY(evt.getX(), evt.getY())) {
                    arrastrarCable = cable; // Almacenar el cable seleccionado
                    modoMoverCableXY = true;
                    arrastrarCable.grosorLinea = 4; // se cambia el grosor de la linea
                    circuito.desreferenciarCableDeSuOrigen(arrastrarCable);
                    break;
                } else if (cable.estaEnLaLineaX2Y2(evt.getX(), evt.getY())) {
                    arrastrarCable = cable; // Almacenar el cable seleccionado
                    modoMoverCableX2Y2 = true;
                    arrastrarCable.grosorLinea = 4; // se cambia el grosor de la linea
                    circuito.desreferenciarCableDeSuDestino(arrastrarCable);
                    break;
                }

            }
        }

    }

    private void onMouseLine(MouseEvent evt) {

        for (Compuertas compu : circuito.getCompuertas()) {
            if (compu.estaEnLaLinea(evt.getX(), evt.getY())) {
                clickCompuerta = compu;
                popupMenu.show(Panel, 800, -90); // mostrar el panel
                repaint();
                break;
            }

        }
        // Verificar LEDs
        for (Leds led : circuito.getLeds()) {
            if (led.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastrarLed = led; // Almacenar el LED seleccionado
                ledPopupMenu.show(Panel, evt.getX() + 10, evt.getY() + 10); // Mostrar menú para LED
                return; // Salir del método
            }
        }

        // Verificar Switches
        for (Switches switches : circuito.getSwitches()) {
            if (switches.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastredSwitch = switches; // Almacenar el switch seleccionado
                switchPopupMenu.show(Panel, evt.getX(), evt.getY()); // Mostrar menú para switch
                return; // Salir del método
            }
        }

        // Verificar Cables
        for (Cables cable : circuito.getCables()) {
            if (cable.estaEnLaLinea(evt.getX(), evt.getY())) {
                arrastrarCable = cable; // Almacenar el cable seleccionado
                cablePopupMenu.show(Panel, evt.getX(), evt.getY()); // Mostrar menú para cable
                return; // Salir del método
            }
        }
    }     // Verifica si el mouse se encuentra sobre una compuerta, LED o switch para mostrar el menú contextual

    private void onMouseReleased(MouseEvent evt) {
        arrastrarCompuerta = null;  // Dejar de deslizar la compuerta
        arrastrarLed = null; // Dejar de deslizar el LED
        arrastredSwitch = null; // dejar de arrastrar el switch

        if (arrastrarCable != null) {

            circuito.cambiarConeccionCable(arrastrarCable, evt.getX(), evt.getY(), modoMoverCableXY, modoMoverCableX2Y2);
            arrastrarCable.grosorLinea = 2;
            arrastrarCable = null;
            modoMoverCableX2Y2 = false;
            modoMoverCableXY = false;
        }

    }

    private void onMouseDragged(MouseEvent evt) {
        if (arrastrarCompuerta != null) {
            // Actualizar la posición de la compuerta que se esta moviendo 
            arrastrarCompuerta.cambiarPosicion(evt.getX() - offsetX, evt.getY() - offsetY);
            repaint();
        }
        if (arrastrarLed != null) {
            arrastrarLed.setX(evt.getX() - offsetX);
            arrastrarLed.setY(evt.getY() - offsetY);
            repaint();
        }
        if (arrastredSwitch != null) {
            arrastredSwitch.setX(evt.getX() - offsetX);
            arrastredSwitch.setY(evt.getY() - offsetY);
            repaint();
        }
        if (arrastrarCable != null) { // si estamos movieno un cables

            if (modoMoverCableXY == true) {
                arrastrarCable.setXY(evt.getX(), evt.getY());
                System.out.println("funcionaaaaaaaaaaaa");
            } else if (modoMoverCableX2Y2 == true) {
                arrastrarCable.setX2Y2(evt.getX(), evt.getY());
            }
            repaint();
        }
        repaint();
    }

    public void nuevaCompuerta(CompuertaLogica compuerta) { // agregar conpuerta al circuito
        int x = POSICION_INICIAL_X;
        int y = POSICION_INICIAL_Y;
        circuito.agregarCompuerta(compuerta, x, y);
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BotonAnd = new javax.swing.JButton();
        BotonOr = new javax.swing.JButton();
        BotonNot = new javax.swing.JButton();
        Panel = new javax.swing.JPanel();
        BotonNor = new javax.swing.JButton();
        buttonSwitch = new javax.swing.JButton();
        led = new javax.swing.JButton();
        cable = new javax.swing.JButton();
        nand = new javax.swing.JButton();
        xnor = new javax.swing.JButton();
        xor = new javax.swing.JButton();
        textoExpresion = new javax.swing.JTextField();
        evaluarExpresion = new javax.swing.JButton();
        botonTablaVerdad = new javax.swing.JButton();
        simular = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        importar = new javax.swing.JMenuItem();
        exportar = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BotonAnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/AND.png"))); // NOI18N
        BotonAnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAndActionPerformed(evt);
            }
        });

        BotonOr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/OR.png"))); // NOI18N
        BotonOr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonOrActionPerformed(evt);
            }
        });

        BotonNot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/NOT.png"))); // NOI18N
        BotonNot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonNotActionPerformed(evt);
            }
        });

        Panel.setBackground(new java.awt.Color(247, 242, 248));
        Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Evaluador de circuitos lógicos.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 8))); // NOI18N
        Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(150, 150, 150), 2),
            "Evaluador de circuitos lógicos.",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 15),
            new java.awt.Color(50, 50, 50)
        ));
        // Center es centrar, Top es para que este arriba, arial la tipografía, lo otro es para el negrilla y lo otro el tamaño

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 552, Short.MAX_VALUE)
        );

        BotonNor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/NOR.png"))); // NOI18N
        BotonNor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonNorActionPerformed(evt);
            }
        });

        buttonSwitch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/SWITCH.png"))); // NOI18N
        buttonSwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSwitchActionPerformed(evt);
            }
        });

        led.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/LED.png"))); // NOI18N
        led.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ledActionPerformed(evt);
            }
        });

        cable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/CABLE.png"))); // NOI18N
        cable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cableActionPerformed(evt);
            }
        });

        nand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/NAND.png"))); // NOI18N
        nand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nandActionPerformed(evt);
            }
        });

        xnor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/XNOR.png"))); // NOI18N
        xnor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xnorActionPerformed(evt);
            }
        });

        xor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documentos/img/XOR.png"))); // NOI18N
        xor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xorActionPerformed(evt);
            }
        });

        textoExpresion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textoExpresionActionPerformed(evt);
            }
        });

        evaluarExpresion.setText("Evaluar");
        evaluarExpresion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evaluarExpresionActionPerformed(evt);
            }
        });

        botonTablaVerdad.setText("Tabla de verdad");
        botonTablaVerdad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTablaVerdadActionPerformed(evt);
            }
        });

        simular.setText("Simular");
        simular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simularActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        importar.setText("Import");
        importar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importarActionPerformed(evt);
            }
        });
        jMenu1.add(importar);

        exportar.setText("Export");
        exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportarActionPerformed(evt);
            }
        });
        jMenu1.add(exportar);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BotonAnd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonOr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonNot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xnor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonNor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSwitch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(evaluarExpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textoExpresion)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(simular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(botonTablaVerdad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(evaluarExpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textoExpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonTablaVerdad, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BotonAnd, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonOr, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonNot, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(nand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(xnor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(xor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BotonNor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonSwitch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(led, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(simular, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void BotonAndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAndActionPerformed

        if (!modoSimulacion) {
            compuert = CompuertaLogica.AND;
            nuevaCompuerta(compuert);
        }
    }//GEN-LAST:event_BotonAndActionPerformed

    private void BotonOrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonOrActionPerformed

        if (!modoSimulacion) {
            compuert = CompuertaLogica.OR;
            nuevaCompuerta(compuert);
        }
    }//GEN-LAST:event_BotonOrActionPerformed

    private void BotonNotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonNotActionPerformed

        if (!modoSimulacion) {
            compuert = CompuertaLogica.NOT;
            nuevaCompuerta(compuert);
        }
    }//GEN-LAST:event_BotonNotActionPerformed

    private void BotonNorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonNorActionPerformed

        if (!modoSimulacion) {
            compuert = CompuertaLogica.NOR;
            nuevaCompuerta(compuert);
        }

    }//GEN-LAST:event_BotonNorActionPerformed

    private void buttonSwitchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSwitchActionPerformed
        circuito.crearSwitch(POSICION_INICIAL_X, POSICION_INICIAL_Y);
        repaint();
    }//GEN-LAST:event_buttonSwitchActionPerformed

    private void cableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cableActionPerformed
        modoDibujarCable = !modoDibujarCable;
    }//GEN-LAST:event_cableActionPerformed

    private void ledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ledActionPerformed
        // Crear un nuevo LED en una posición fija o aleatoria
        circuito.agregarLed(POSICION_INICIAL_X, POSICION_INICIAL_Y); // Agregar el LED al circuito
        repaint();
    }//GEN-LAST:event_ledActionPerformed

    private void simularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simularActionPerformed
        modoSimulacion = !modoSimulacion;
        if (modoSimulacion == true) {
            circuito.iniciarSimulacion();
            repaint();
        }
    }//GEN-LAST:event_simularActionPerformed

    private void nandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nandActionPerformed
        // TODO add your handling code here:

        if (!modoSimulacion) {
            compuert = CompuertaLogica.NAND;
            nuevaCompuerta(compuert);
        }

    }//GEN-LAST:event_nandActionPerformed

    private void xnorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xnorActionPerformed
        // TODO add your handling code here:

        if (!modoSimulacion) {
            compuert = CompuertaLogica.XNOR;
            nuevaCompuerta(compuert);
        }
    }//GEN-LAST:event_xnorActionPerformed

    private void xorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xorActionPerformed
        // TODO add your handling code here:

        if (!modoSimulacion) {
            compuert = CompuertaLogica.XOR;
            nuevaCompuerta(compuert);
        }

    }//GEN-LAST:event_xorActionPerformed

    private void textoExpresionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textoExpresionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textoExpresionActionPerformed

    private void evaluarExpresionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evaluarExpresionActionPerformed
        circuito.eliminarTodo();
        String expresion = textoExpresion.getText();
        String postfija = Circuitos.expresionAPosfija(expresion);
        System.out.println("Expresión Posfija: " + postfija);
        circuito.expresionACircuito(expresion);
        repaint();


    }//GEN-LAST:event_evaluarExpresionActionPerformed

    private void botonTablaVerdadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTablaVerdadActionPerformed
        String expresion = textoExpresion.getText();
        mostrarTablaDeVerdad(expresion);
    }//GEN-LAST:event_botonTablaVerdadActionPerformed

    private void importarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importarActionPerformed
        // TODO add your handling code here:
        
        Circuitos circuit = Serializa.deserializarCircuito();
        if(circuit!=null){
            circuito = circuit;
            repaint();
        }else{
            System.out.println("Sircuito no encontrado");
        }
      //  Circuitos miCircuito = DeserializarCircuito.deserializarCircuito();
    }//GEN-LAST:event_importarActionPerformed

    private void exportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportarActionPerformed
        // TODO add your handling code here:
        Serializa.serializarCircuito(circuito);
    }//GEN-LAST:event_exportarActionPerformed

    //Función para mostrar "manualmente" la tabla de verdad correspondiente a la expresión booleana 
    private void mostrarTablaDeVerdad(String expresion) {
        // Primero, obtengo una lista de las variables presentes en la expresión booleana.
        List<String> variables = obtenerVariables(expresion);

        // Calculo cuántas variables hay en la expresión.
        int numVariables = variables.size();

        // Calculo cuántas filas debe tener la tabla (2^numVariables combinaciones posibles).
        int numFilas = (int) Math.pow(2, numVariables);

        // Defino los nombres de las columnas, que incluyen las variables más una columna "Resultado".
        String[] columnNames = new String[numVariables + 1];
        for (int i = 0; i < numVariables; i++) {
            // Asigno cada variable a una columna.
            columnNames[i] = variables.get(i);
        }
        // La última columna será para el resultado de la expresión booleana.
        columnNames[numVariables] = "Resultado";

        // Creo una matriz para almacenar los datos de la tabla.
        String[][] data = new String[numFilas][numVariables + 1];

        // Genero todas las combinaciones de valores de verdad (0 y 1).
        for (int i = 0; i < numFilas; i++) {
            // Convierto el número 'i' a su representación binaria de numVariables dígitos (una combinación de verdad).
            String combinacion = Integer.toBinaryString(i | (1 << numVariables)).substring(1);

            // Lleno cada columna de la fila con los valores de la combinación binaria.
            for (int j = 0; j < numVariables; j++) {
                data[i][j] = String.valueOf(combinacion.charAt(j));
            }

            // Evaluo la expresión booleana usando la combinación de valores de verdad actual.
            String resultado = evaluarExpresionConValores(expresion, variables, combinacion);

            // Coloco el resultado de la evaluación en la última columna.
            data[i][numVariables] = resultado;
        }

        // Creo una tabla con los datos generados y los nombres de las columnas.
        JTable table = new JTable(data, columnNames);

        // Le añado una barra de desplazamiento a la tabla.
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Creo un cuadro de diálogo para mostrar la tabla de verdad.
        JDialog dialog = new JDialog(this, "Tabla de Verdad", true);
        dialog.getContentPane().add(scrollPane);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);  // Centra el diálogo respecto a la ventana principal.
        dialog.setVisible(true);  // Hago visible el diálogo.
    }

    //Función para obtener y hacer operaciones sobre cada variable de la expresión 
    private List<String> obtenerVariables(String expresion) {
        // Creo una lista para almacenar las variables.
        List<String> variables = new ArrayList<>();

        // Recorro cada carácter de la expresión.
        for (char c : expresion.toCharArray()) {
            // Si el carácter es una letra y no lo he añadido antes, lo agrego a la lista.
            if (Character.isLetter(c) && !variables.contains(String.valueOf(c))) {
                variables.add(String.valueOf(c));
            }
        }

        // Devuelvo la lista de variables encontradas.
        return variables;
    }

    //Función con la logica para evaluar y realizar operaciones sobre la expresión booleana 
    private String evaluarExpresionConValores(String expresion, List<String> variables, String combinacion) {
        // Reemplazo cada variable en la expresión con los valores de la combinación actual (0 o 1).
        for (int i = 0; i < variables.size(); i++) {
            expresion = expresion.replace(variables.get(i), String.valueOf(combinacion.charAt(i)));
        }

        // Convierto la expresión infija a notación postfija.
        String expresionPostfija = expresionAPosfija(expresion);

        // Creo una pila para evaluar la expresión postfija.
        Stack<Integer> pila = new Stack<>();

        // Recorro cada carácter de la expresión postfija.
        for (int i = 0; i < expresionPostfija.length(); i++) {
            char currentChar = expresionPostfija.charAt(i);

            // Si el carácter es un dígito (0 o 1), lo convierto a entero y lo apilo.
            if (Character.isDigit(currentChar)) {
                pila.push(currentChar - '0');  // '0' o '1' se convierte en 0 o 1.
            } // Si es un operador, aplico la operación con los operandos en la pila.
            else {
                // Verifico que haya operandos suficientes para operar.
                if (pila.isEmpty()) {
                    throw new IllegalArgumentException("Operandos faltantes para el operador: " + currentChar);
                }

                // Si es el operador NOT, aplico la operación de negación.
                if (currentChar == '\'') {  // Operador NOT
                    int valor = pila.pop();
                    pila.push(valor == 0 ? 1 : 0);  // Aplico NOT (complemento).
                } else {
                    // Verifico que haya al menos dos operandos para operadores binarios.
                    if (pila.size() < 2) {
                        throw new IllegalArgumentException("Operandos faltantes para el operador: " + currentChar);
                    }
                    int b = pila.pop();  // Segundo operando.
                    int a = pila.pop();  // Primer operando.

                    // Aplico la operación lógica dependiendo del operador.
                    switch (currentChar) {
                        case '.':  // AND
                            pila.push(a & b);
                            break;
                        case '+':  // OR
                            pila.push(a | b);
                            break;
                        case '*':  // NAND
                            pila.push(a == 1 && b == 1 ? 0 : 1);
                            break;
                        case '~':  // NOR
                            pila.push(a == 0 && b == 0 ? 1 : 0);
                            break;
                        case '|':  // XOR
                            pila.push(a ^ b);
                            break;
                        case '!':  // XNOR
                            pila.push(a == b ? 1 : 0);
                            break;
                        default:
                            throw new IllegalArgumentException("Operador no reconocido: " + currentChar);
                    }
                }
            }
        }

        // El resultado final es el único valor que queda en la pila.
        if (pila.size() != 1) {
            throw new IllegalArgumentException("Expresión booleana mal formada.");
        }

        // Devuelvo el resultado final.
        return String.valueOf(pila.pop());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonAnd;
    private javax.swing.JButton BotonNor;
    private javax.swing.JButton BotonNot;
    private javax.swing.JButton BotonOr;
    private javax.swing.JPanel Panel;
    private javax.swing.JButton botonTablaVerdad;
    private javax.swing.JButton buttonSwitch;
    private javax.swing.JButton cable;
    private javax.swing.JButton evaluarExpresion;
    private javax.swing.JMenuItem exportar;
    private javax.swing.JMenuItem importar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JButton led;
    private javax.swing.JButton nand;
    private javax.swing.JButton simular;
    private javax.swing.JTextField textoExpresion;
    private javax.swing.JButton xnor;
    private javax.swing.JButton xor;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

}
