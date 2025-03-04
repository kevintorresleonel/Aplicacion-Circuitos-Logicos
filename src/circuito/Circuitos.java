package circuito;

import componentes.Cables;
import componentes.Leds;
import componentes.Pines;
import componentes.Switches;
import compuertas.And;
import compuertas.Compuertas;
import compuertas.Nand;
import compuertas.Nor;
import compuertas.Not;
import compuertas.Or;
import compuertas.Xnor;
import compuertas.Xor;
import interfaz.NombreCompuerta;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Stack;
import javax.swing.JOptionPane;

/*
Integrantes de grupo: 
Torres Kevin
Ramos Mateo 
Gonzales Lauren 
 */
public class Circuitos implements NombreCompuerta, Serializable {
    
     private static final long serialVersionUID = 1L;

    // Atributos: listas para almacenar los diferentes componentes del circuito.
    private ArrayList<Compuertas> compuertas = new ArrayList<>();  // Arreglo de compuertas lógicas
    private ArrayList<Cables> cables = new ArrayList<>();          // Arreglo de cables
    private ArrayList<Leds> leds = new ArrayList<>();              // Arreglo de LEDs
    private ArrayList<Switches> switches = new ArrayList<>();      // Arreglo de switches (interruptores)

    // Variables para contar la cantidad de compuertas de cada tipo y controlar la distancia en y
    private int andContador = 0, orContador = 0, notContador = 0, norContador = 0, nandContador = 0, xorContador = 0, xnorContador = 0;
    private final int espaciadoY = 120; // Espaciado entre compuertas en el eje Y
    private final int espaciadoX = 80; // Espaciado entre columnas en el eje X

    //Función para eliminar todos los componentes presentes en la simulación actual 
    public void eliminarTodo() {
        compuertas.clear();
        switches.clear();
        leds.clear();
        cables.clear();

        andContador = orContador = notContador = norContador = nandContador = xorContador = xnorContador = 0;

    }

    /*
    AND -> .
    OR -> +
    NOT -> '
    NAND -> *
    NOR -> ~
    XOR -> |
    XNOR -> !
     */
    // Método para convertir la expresión booleana infija a posfija
    public static String expresionAPosfija(String expresion) {
        Stack<Character> pila = new Stack<>();
        StringBuilder postfija = new StringBuilder();

        for (int i = 0; i < expresion.length(); i++) {
            char currentChar = expresion.charAt(i);

            // Si el carácter es un operando (letra), lo añadimos al resultado
            if (Character.isLetterOrDigit(currentChar)) {
                postfija.append(currentChar);
            } // Si es un paréntesis de apertura, lo empujamos a la pila
            else if (currentChar == '(') {
                pila.push(currentChar);
            } // Si es un paréntesis de cierre, desapilamos hasta encontrar uno de apertura
            else if (currentChar == ')') {
                while (!pila.isEmpty() && pila.peek() != '(') {
                    postfija.append(pila.pop());
                }
                pila.pop(); // Quitar el '(' de la pila
            } // Si es un operador
            else {
                while (!pila.isEmpty() && obtenerPrecedencia(pila.peek()) >= obtenerPrecedencia(currentChar)) {
                    postfija.append(pila.pop());
                }
                pila.push(currentChar);
            }
        }

        // Vaciar la pila restante
        while (!pila.isEmpty()) {
            postfija.append(pila.pop());
        }

        return postfija.toString();
    }

    // funcion para hacer la transformacion de exprecion a circuito, dibujo
    public void expresionACircuito(String expresion) {
        String expresioPos = expresionAPosfija(expresion);
        // crear fUncion que recorra expresionpos vea las variables, cree los switch y los cables
        crearSwitchCableDeEsteDeExpresion(expresioPos);
        // seguir y crear la funcion que vaya creando las compuertas y conectandolas
        crearCompuertaYconectar(expresioPos);
        //
    }
    //crear la funcion que vaya creando las compuertas y conectandolas

    private void crearCompuertaYconectar(String expresion) {

        int cont = 0;
        char operadorPivote;
        boolean bandera = true;
        int tamañoExpresion = expresion.length();
        ArrayList<Character> variables = new ArrayList<>();
        //a.s + b.c = as.bc.+
        //a.s + b.c + c.a= as.bc.+ca.+
        // as.bc.+c+ // 
        //a+b+c = ab+c+
        //ab.c.d.
        //ad'+ -- a+d'
        //(a+s)~(c*h)'+(c.g)
        ///(a+f)*(v+b)'*(p+l)
        //(a+d)'.(f+g).(a+g)'
        for (int i = 0; i < tamañoExpresion; i++) {  //recorremos la expresion
            if (Character.isLetter(expresion.charAt(i))) { // si el que se encuantra es una variable
                variables.add(expresion.charAt(i)); //agregamos variable
            } else {
                operadorPivote = expresion.charAt(i);
                cont++;

                // esta funcion mira si el siguiente operador es diferente o igual
                bandera = preguntarSiEsElMismoOperador(expresion, i + 1, tamañoExpresion, operadorPivote);

                //logica para crear la compuerta
                if (cont == 3 || i + 1 == tamañoExpresion || bandera == false) { // mientras que hayan mas de 4 variables, o el sea el total del 
                    int cantComp = compuertas.size();                              //tamaño de la expresion o se encuenre otro operador conecta 
                    //llamamos a una funcion que nos dice que compuerta es y crea
                    Compuertas compuerta = crearCompu(operadorPivote);
                    if ((cont + 1) > 2) { // se cambia el numero de entradas de las compuerta creadas si es el caso
                        compuerta.setNumEntra(cont + 1);
                    }
                    agregarCompuerta(compuerta);
                    int restante;
                    if (operadorPivote == '\'') { // si es un not vemos si niega una varable o el resultado de una compuerta
                        if (variables.isEmpty()) {
                            restante = 1; // si niega una compuerta
                        } else {
                            restante = 0; // si niega una variable
                        }
                    } else {
                        restante = (cont + 1) - variables.size();
                    }
                    System.out.println("restantes " + restante);

                    // en caso de que el restante sea mayor a 0 eso es que otra compuerta se conecta a esta 
                    if (restante > 0) {
                        for (int m = 1; m <= restante; m++) { // conectar otras compuertas a la nueva cuando sea el caso
                            Cables cablecito;
                            cablecito = new Cables(getCompuertas().get(cantComp - m).getPines().getFirst().getX(), getCompuertas().get(cantComp - m).getPines().getFirst().getY(), 0, 0); //creaos el cable y se conecta a si pin de origen  
                            getCompuertas().get(cantComp - m).getPines().getFirst().setCableEntradaSalida(cablecito);
                            cablecito.setOrigenPinCable(getCompuertas().get(cantComp - m).getPines().getFirst());
                         
                            if(getCompuertas().get(cantComp-m).nombreComp.equals("NOT")){
                                int cantComp2 = cantComp - m  ;

                                while(getCompuertas().get(cantComp2).nombreComp.equals("NOT") && cantComp2 >0 ){
                                     System.out.println("aquiiiiiiiiiiiiiiiiiiiiiiii");
                                    cantComp2--;
                                }
                            if(cantComp2>0 && restante == m){
                                
                              intercambiarElementos(compuertas, cantComp - m, cantComp2);  
                            }
                                
                            }

                            // caso de que sea una not
                            if (operadorPivote == '\'') {
                                cablecito.setDestinoPinCable(compuerta.getPines().get((compuerta.getPines().size()) - m));
                                compuerta.getPines().get((compuerta.getPines().size()) - m).setCableEntradaSalida(cablecito);
                                // cambiar de poiciones las compuertas para que estas se conecten a las corectas caso (a+d).(a+c)
                                if (compuertas.size() > 2) {
                                    intercambiarElementos(compuertas, compuertas.size() - 2, compuertas.size() - 3); //(a+d)'.(f+g).(a+g)'
                                } // lo anterior intercmbia las compuertas del array list de las compuertas 

                                break;
                            } else {
                                cablecito.setDestinoPinCable(compuerta.getPines().get((compuerta.getPines().size()) - m));
                                compuerta.getPines().get((compuerta.getPines().size()) - m).setCableEntradaSalida(cablecito);
                            }
                            cablecito.setX2Y2(compuerta.getPines().get((compuerta.getPines().size()) - m).getX(), compuerta.getPines().get((compuerta.getPines().size()) - m).getY());
                            cables.add(cablecito);

                        }
                    }

                    if (operadorPivote != '\'') {
                        //int n = 0; n < variables.size(); n++

                        if (variables.size() > cont + 1) { // para validar expresiones de la forma bnm.+
                            int m = 1;
                            int n = variables.size() - (cont + 1);
                            for (; n < variables.size(); n++) {

                                char variable = variables.get(n);
                                Cables cable = buscarCableDeSwitch(variable);
                                Cables cablecito;
                                // try {

                                cablecito = new Cables(cable.getX(), (cable.getCablesConectados().size() + 1) * 50, 0, 0);
                                compuerta.getPines().get(m).setCableEntradaSalida(cablecito);
                                cable.conectarACable(cablecito);
                                cablecito.setDestinoPinCable(compuerta.getPines().get(m));
                                getCables().add(cablecito);
                                m++;
                            }
                        } else {
                            for (int n = 0; n < variables.size(); n++) { // este for recorre la lista de variables y crea los cables y conecta a a nueva compuerta

                                char variable = variables.get(n);
                                Cables cable = buscarCableDeSwitch(variable);
                                Cables cablecito;
                                // try {

                                cablecito = new Cables(cable.getX(), (cable.getCablesConectados().size() + 1) * 50, 0, 0);
                                compuerta.getPines().get(n + 1).setCableEntradaSalida(cablecito);
                                cable.conectarACable(cablecito);
                                cablecito.setDestinoPinCable(compuerta.getPines().get(n + 1));
                                getCables().add(cablecito);

                            }
                        }

                        //reiniciar contador
                        if (variables.size() > cont + 1) {
                            cont = variables.size() - (cont + 1);
                            for (int j = variables.size() - 1; j >= cont; j--) {
                                variables.removeLast();
                            }
                            System.out.println("aquiiiii " + variables.getLast());
                            cont = 0;
                        } else {
                            cont = 0;
                            //despues de crear la compuerta debemos de vacial el array list de variables
                            variables.clear();
                            //agregar compuerta
                        }

                    } else if (operadorPivote == '\'' && variables.size() > 0) {
                        Cables cable = buscarCableDeSwitch(variables.getLast()); //buscar cable para conectar
                        Cables cablecito;
                        cablecito = new Cables(cable.getX(), (cable.getCablesConectados().size() + 1) * 50, 0, 0); //nuevo cable
                        cable.conectarACable(cablecito); //conectamos el cable
                        compuerta.getPines().get(1).setCableEntradaSalida(cablecito); // conectamos el cable a la enrada de la compuerta 
                        cablecito.setDestinoPinCable(compuerta.getPines().get(1)); //se conecta el cable a la compuerta 
                        getCables().add(cablecito);
                        cont = 0;
                        variables.removeLast();

                    } else {
                        //reiniciar contador
                        cont = 0;
                        //despues de crear la compuerta debemos de vacial el array list de variables
                        variables.clear();
                        //agregar compuerta
                    }

                }

            }
        }
    }

    // Método para intercambiar elementos en un ArrayList
    public static <T> void intercambiarElementos(ArrayList<T> lista, int indice1, int indice2) {
        // Guardar el primer elemento en una variable temporal
        T temp = lista.get(indice1);
        // Asignar el segundo elemento al primer índice
        lista.set(indice1, lista.get(indice2));
        // Asignar el elemento temporal al segundo índice
        lista.set(indice2, temp);
    }

    private boolean preguntarSiEsElMismoOperador(String expresion, int i, int tamañoExpresion, char operadorPivoteActual) {
        int cont = 0; //para manejar el caso a.d+g.f   ad.gf.+ 
        for (int j = i; j < tamañoExpresion; j++) {  //recorremos la expresion
            cont++;
            if (cont == 3) { // si el contador es 3 etonces tenemos as.fg.s+  a.s+f.g
                return false;
            }
            if (!Character.isLetter(expresion.charAt(j))) { // si el que se encuantra no es una variable
                if (operadorPivoteActual != expresion.charAt(j)) { // preguntamos si el operador que se encontro es igual o no 
                    return false;
                } else {
                    if(operadorPivoteActual=='\''){
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    // funcion para obtener el cable que salga de un pin de un switch

    private Cables buscarCableDeSwitch(char c) {
        for (Switches swit : switches) {
            if (swit.getVar() == c) {
                return swit.getPinSwitch().getCableEntradaSalida(); // obtenemos el cable del switch
            }
        }
        return null;
    }

    // Método para crear una compuerta específica según su nombre y posicionarla en su columna
    private Compuertas crearCompu(char c) {
        switch (c) {
            case '\'': // NOT
                return new Not(320, 50 + (andContador++ * espaciadoY), "NOT");
            case '.': // AND
                return new And(320 + espaciadoX, 90 + (orContador++ * espaciadoY), "AND");
            case '+': // OR
                return new Or(320 + (2 * espaciadoX), 130 + (notContador++ * espaciadoY), "OR");
            case '~': // NOR
                return new Nor(320 + (3 * espaciadoX), 170 + (norContador++ * espaciadoY), "NOR");
            case '*': // NAND
                return new Nand(320 + (4 * espaciadoX), 210 + (nandContador++ * espaciadoY), "NAND");
            case '|': // XOR
                return new Xor(320 + (5 * espaciadoX), 250 + (xorContador++ * espaciadoY), "XOR");
            case '!': // XNOR
                return new Xnor(320 + (6 * espaciadoX), 270 + (xnorContador++ * espaciadoY), "XNOR");
            default:
                return null; // Si el nombre de la compuerta no coincide, retorna null
        }
    }

    // Método para definir la precedencia de los operadores
    public static int obtenerPrecedencia(char operador) {
        switch (operador) {
            case '\'':
                return 3;  // NOT
            case '.':
                return 2;  // AND
            case '+':
                return 1;  // OR
            case '*':
                return 2;  // NAND
            case '~':
                return 1;  // NOR
            case '|':
                return 1;  // XOR
            case '!':
                return 1;  // XNOR  (a.s)+(h.k)
            default:
                return 0;    // Para paréntesis y otros caracteres
        }
    }

    // fucion para crear los switch dados las variables y los cables de los switch
    private void crearSwitchCableDeEsteDeExpresion(String expresionpos) {
        int x = 1;
        LinkedHashSet<Character> variables = new LinkedHashSet<>(); // se guardan las variables
        for (int i = 0; i < expresionpos.length(); i++) {
            if (Character.isLetter(expresionpos.charAt(i))) {
                variables.add(expresionpos.charAt(i)); //recorremos cada variable y se agrega
            }
        }

        for (Character variable : variables) {
            crearSwitch(x * 30, 20, variable); // crea Los switch y sus cables
            agregarCable(x * 30, 20, x * 30, 500);
            x++;
        }

    }

    // Métodos para iniciar la simulación del circuito
    public void iniciarSimulación(int x, int y) {
        Switches switche = encontrarSwitch(x, y);
        if (switche != null) {
            switche.setValor();
        }
    }

    // inicia todos los switches
    public void iniciarSimulacion() {
        for (Switches swit : getSwitches()) {
            swit.setValor();
        }
    }

    public void finalizarSimulación() {
        // Implementar lógica para finalizar la simulación
    }

    // Método para validar errores en el circuito
    public void validarErrores() {
        // Implementar lógica para validar errores
    }

    // Método para crear una compuerta específica según su nombre
    private Compuertas crearCompu(CompuertaLogica compuert) {
        switch (compuert.getNombre()) {
            case "AND":
                return new And(0, 0, compuert.getNombre());
            case "OR":
                return new Or(0, 0, compuert.getNombre());
            case "NOT":
                return new Not(0, 0, compuert.getNombre());
            case "NOR":
                return new Nor(0, 0, compuert.getNombre());
            case "NAND":
                return new Nand(0, 0, compuert.getNombre());
            case "XOR":
                return new Xor(0, 0, compuert.getNombre());
            case "XNOR":
                return new Xnor(0, 0, compuert.getNombre());
            default:
                return null; // Si el nombre de la compuerta no coincide, retorna null
        }
    }

    // Método para crear una nueva compuerta lógica y agregarla al circuito.
    public void agregarCompuerta(CompuertaLogica compuert, int x, int y) {
        Compuertas compu = crearCompu(compuert); // Crear la compuerta correspondiente
        compu.cambiarPosicion(x, y); // Cambiar la posición de la compuerta en el gráfico
        getCompuertas().add(compu); // Agregar la compuerta a la lista de compuertas
    }

    // Método para crear una nueva compuerta lógica y agregarla al circuito.
    public void agregarCompuerta(Compuertas compu) {
        getCompuertas().add(compu); // Agregar la compuerta a la lista de compuertas
    }

    // Método para eliminar una compuerta del circuito
    public void eliminarCompuerta(Compuertas comp) {
        getCompuertas().remove(comp); // Elimina la compuerta de la lista
    }
    // Getters para acceder a las listas de componentes

    public ArrayList<Compuertas> getCompuertas() {
        return compuertas;
    }

    // Método para agregar un cable entre dos puntos del circuito.
    // Se determina si el cable conecta dos pines o si se conecta a otros cables.
    public void agregarCable(int x, int y, int x1, int y1) {
        Pines origen = encontrarPinCercano(x, y); // Buscar el pin de origen
        Pines destino = encontrarPinCercano(x1, y1); // Buscar el pin de destino
        Cables cableOrigen = encontrarCablesCercanos(x, y); // Buscar cable cercano al origen
        Cables cableDestino = encontrarCablesCercanos(x1, y1); // Buscar cable cercano al destino

        Leds led = encontrarLed(x, y);
        if (led == null) {
            led = encontrarLed(x1, y1);
        }
        Switches switche = encontrarSwitch(x1, y1);
        if (switche == null) {
            switche = encontrarSwitch(x, y);
        }

        // Crear el nuevo cable que conecta los puntos dados
        Cables cable = new Cables(x, y, x1, y1);

        // si se encuanra un led en l posicion 
        if (led != null) {
            destino=led.getPin();
            led.getPin().setCableEntradaSalida(cable);
            cable.setDestinoPinCable(led.getPin());
        }

        // si se encuatra un switch se conecta el cable al pin del switch 
        if (switche != null) {
            switche.getPinSwitch().setCableEntradaSalida(cable);
            cable.setOrigenPinCable(switche.getPinSwitch());
        }

        // Si se encuentra el pin de origen, conectamos el cable a ese pin
        if (origen != null) {
            origen.setCableEntradaSalida(cable); // Conecta el cable al pin de origen
            cable.conectarAPin(origen); // Asocia el pin con el cable
        }

        // Si se encuentra el pin de destino, conectamos el cable a ese pin
        if (destino != null) {
            destino.setCableEntradaSalida(cable); // Conecta el cable al pin de destino
            cable.conectarAPin(destino); // Asocia el pin con el cable
        }

        // Si se encuentra un cable cercano al origen, se conectan ambos cables
        if (cableOrigen != null) {
            cableOrigen.conectarACable(cable); // Conectar el nuevo cable al cable de origen
            cable.setCableOrigen(cableOrigen); // Asocia el nuevo cable con el cable de origen
        }

        // Si se encuentra un cable cercano al destino, se conectan ambos cables
        if (cableDestino != null) {
            cableDestino.conectarACable(cable); // Conectar el nuevo cable al cable de destino
            cable.setCableOrigen(cableDestino); // Asocia el nuevo cable con el cable de destino
        }

        // Finalmente, agregamos el cable a la lista de cables
        cables.add(cable);

        // Si no se encontraron los pines ni los cables de origen/destino, se reporta un error
        if (origen == null && destino == null && cableOrigen == null ) {
            System.out.println("No se pudo conectar el cable, los pines no fueron encontrados.");
        }
    }

    // mtodo para desreferenciar cables origen
    public void desreferenciarCableDeSuOrigen(Cables cable) {
        if (cable.getCableOrigen() != null) {
            cable.getCableOrigen().getCablesConectados().remove(cable);
        }
        // ARREGLAR EL REFERENCIADO AL DESREFERENCIAR EL CABE DE SU ORIGEN 
        if (cable.getOrigenPinCable() != null) {
            cable.getOrigenPinCable().setCableEntradaSalida(null);
        }
        cable.setOrigenPinCable(null);
        
        cable.setCableOrigen(null);
        
    }
    // mtodo para desreferenciar cables destino

    public void desreferenciarCableDeSuDestino(Cables cable) {
        
        //ARREGLAR EL DESREFERENCIADO DE LOS CABLES DE SU DESTINO
        if (cable.getDestinoPinCable() != null) {
            cable.getDestinoPinCable().setCableEntradaSalida(null);
        }
        cable.setDestinoPinCable(null);
        // PODRIA HABER CABLES CONECTADOS EN X2 Y Y2 ARREGLAR ESO 
        
    }

    // metodos para reasignar destinos origenes a u cable cuando se mueve 
    //--------------------------------------------------
    // Método auxiliar para encontrar un cable cercano a las coordenadas dadas
    private Cables encontrarCablesCercanos(int x, int y) {
        for (Cables cabl : cables) {
            if (cabl.estaEnLaLinea(x, y)) {
                return cabl; // Retorna el cable si se encuentra cercano a las coordenadas
            }
        }
        return null; // Si no se encuentra, retorna null
    }

    // eliminar completamente un cable
    public void eliminarCable(Cables cable) {
        if (cable.getOrigenPinCable() != null) {
            cable.getOrigenPinCable().setCableEntradaSalida(null);
        }
        if (cable.getDestinoPinCable() != null) {
            cable.getDestinoPinCable().setCableEntradaSalida(null);
        }
        if (cable.getCableOrigen() != null) {
            cable.getCableOrigen().getCablesConectados().remove(cable);
        }
        if (!cable.getCablesConectados().isEmpty()) {
            cable.quitarReferenciaAcablesHijos();
        }

        getCables().remove(cable); // Elimina el cable de la lista
        System.out.println(getCables().size() + "\n");
    }

    public ArrayList<Cables> getCables() {
        return cables;
    }

    // metodo para cambiar las posiciones de los extremos de un cable 
    public void cambiarConeccionCable(Cables arrastrarCable, int x, int y, boolean origen, boolean destino) {
        if (origen == true) {
            Cables cableDestino = encontrarCablesCercanos(x, y);
            if(arrastrarCable.equals(cableDestino)){
                cableDestino=null;
            }
            if (cableDestino != null) {
                cableDestino.conectarACable(arrastrarCable);
                arrastrarCable.setCableOrigen(cableDestino);
            }
            Pines pin = encontrarPin(x, y);
            if (pin != null) {
                pin.setCableEntradaSalida(arrastrarCable);
                // pin.setCableEntradaSalida(arrastrarCable);
                if (pin.getTipoPin().equals("SALIDA")) {
                    arrastrarCable.setOrigenPinCable(pin);

                } /*else {
                    arrastrarCable.setDestinoPinCable(pin);
                }*/

            }

        } else if (destino == true) {
            Pines pin = encontrarPin(x, y);
            if (pin != null) {
                pin.setCableEntradaSalida(arrastrarCable);
                if (pin.getTipoPin().equals("ENTRADA")) {
                    arrastrarCable.setDestinoPinCable(pin);
                } /*else {
                    arrastrarCable.setOrigenPinCable(pin);
                }*/
            }
        }
    }

    // Método auxiliar para encontrar un pin cercano a las coordenadas dadas
    private Pines encontrarPinCercano(int x, int y) {
        for (Compuertas compuerta : compuertas) {
            for (Pines pin : compuerta.getPines()) {
                if (pin.estaEnLaLinea(x, y)) {
                    return pin; // Retorna el pin si se encuentra cercano a las coordenadas
                }
            }
        }

        return null; // Si no se encuentra, retorna null
    }

    // Método auxiliar para encontrar un pin cercano a las coordenadas dadas
    private Pines encontrarPin(int x, int y) {
        for (Compuertas compuerta : compuertas) {
            for (Pines pin : compuerta.getPines()) {
                if (pin.estaEnLaLinea(x, y)) {
                    System.out.println("pin encontradooooooooooooooooooooooooooooo");
                    return pin; // Retorna el pin si se encuentra cercano a las coordenadas

                }
            }
        }

        for (Switches swit : switches) {
            if (swit.getPinSwitch().estaEnLaLinea(x, y)) {
                return swit.getPinSwitch();
            }
        }

        for (Leds led : leds) {
            if (led.getPin().estaEnLaLinea(x, y)) {
                return led.getPin();
            }
        }

        return null; // Si no se encuentra, retorna null
    }
    // Lógica para mover la posición de un LED en el circuito.

//para encontrar el led seleccionado 
    private Leds encontrarLed(int x, int y) {
        for (Leds ledd : leds) {
            if (ledd.estaEnLaLinea(x, y)) {
                return ledd;
            }
        }
        return null;
    }

    public void moverLed(Leds led, int newX, int newY) {
        led.setX(newX); // Actualiza la posición X del LED
        led.setY(newY); // Actualiza la posición Y del LED
    }
    // Método para agregar un LED al circuito.

    public void agregarLed(int x, int y) {
        Leds led = new Leds(x, y);
        leds.add(led); // Agregar LED al arreglo
    }

    // Métodos para eliminar componentes específicos del circuito
    public void eliminarLed(Leds led) {
        getLeds().remove(led); // Elimina el LED de la lista
    }

    public ArrayList<Leds> getLeds() {
        return leds;
    }

    // para encontrar el switch seleccionado 
    private Switches encontrarSwitch(int x, int y) {
        for (Switches switc : switches) {
            if (switc.estaEnLaLinea(x, y)) {
                return switc;
            }
        }
        return null;
    }

    // metodo para crear un switch
    public void crearSwitch(int x, int y) {
        Switches nuevoSwitches = new Switches(x, y);
        agregarWSwitches(nuevoSwitches); // Agregar el switch al circuito
    }

    // metodo para crear un switch
    public void crearSwitch(int x, int y, Character c) {
        Switches nuevoSwitches = new Switches(x, y);
        nuevoSwitches.setVar(c);
        agregarWSwitches(nuevoSwitches); // Agregar el switch al circuito
    }

    // Lógica para mover la posición de un switch en el circuito.
    public void moverSwitch(Switches swich, int newX, int newY) {
        swich.setX(newX); // Actualiza la posición X del switch
        swich.setY(newY); // Actualiza la posición Y del switch
    }

    // Método para agregar un switch (interruptor) al circuito.
    public void agregarWSwitches(Switches swich) {
        switches.add(swich); // Agregar switch al arreglo
    }

    public ArrayList<Switches> getSwitches() {
        return switches;
    }

    public void eliminarSwitch(Switches switchObj) {
        getSwitches().remove(switchObj); // Elimina el switch de la lista
    }

}
