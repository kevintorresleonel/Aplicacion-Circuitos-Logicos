package compuertas;


/*
Integrantes de grupo: 
Torres Kevin
Ramos Mateo
Gonzales Lauren 
Ramirez Leonardo
 */
import componentes.Componente;
import java.awt.Graphics2D;
import componentes.Pines;
import java.util.ArrayList;

//Aquí la clase padre 
public abstract class Compuertas extends Componente {

    private int numEntra;  // Número de entradas de la compuerta
    protected int width, height; // Tamaño
    public String nombreComp; // Nombre

    private ArrayList<Pines> pines;  //arreglo donde almecenar las entradas/salidas

    //Metodo constructor
    public Compuertas(int x, int y, int numEntrada, String nombreComp) {
        super(x, y);
        this.width = 50;
        this.height = 30;
        this.numEntra = numEntrada; // Número de entradas inicial
        pines = new ArrayList<>(); // la lista de pines
        this.nombreComp = nombreComp; // Nombre de la compuerta

    }

    // Metodo que comprueba la tabla
    public abstract void comprobarTabla();

    //Metodos abstractos
    public abstract void draw(Graphics2D g);

// funcion para asignar valor al pin de salida 
    public void asignarValorSalidaAPin() {
        getPines().get(0).simular(getValor());
    }

    @Override
    public void simular() {
        comprobarTabla();
    }

    //metodo para agregar pines
    public void agregarPin(int x, int y, String tipoPin) {
        Pines pin = new Pines(x, y, tipoPin); // Se crea un nuevo pin
        pin.setCompuertaPadre(this); // Se asocia el pin con la compuerta actual
        getPines().add(pin); // Se agrega el pin a la lista de pines
    }

      // elimina el último pin de la lista
    public void eliminarPin() {
        if (!getPines().isEmpty()) { // Verifica si hay pines para eliminar
            getPines().remove(getPines().size() - 1);
        }
    }

    //drawPines
    // Método para dibujar los pines de la compuerta

    public void drawPin(Graphics2D g) {
        for (Pines pine : pines) {
            pine.draw(g);
            if (pine.getCableEntradaSalida() != null) { // Si el pin tiene un cable asociado
                pine.getCableEntradaSalida().draw(g); // Dibuja el cable
            }

        }
    }

    public boolean estaEnLaLinea(int posicionX, int posicionY) {
        return posicionX >= getX() && posicionX <= getX() + width && posicionY >= getY() && posicionY <= getY() + height+20;
    }

    public void cambiarPosicion(int x, int y) {
        setX(x);
        setY(y);
    }

    //Getters y setters 
    @Override
    public int getX() {
        return super.getX();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    @Override
    public int getY() {
        return super.getY();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the numEntra
     */
    public int getNumEntra() {
        return numEntra;
    }

    /**
     * @param numEntra the numEntra to set
     */
    public void setNumEntra(int numEntra) {
        this.numEntra = numEntra;

    }

    /**
     * @return the pines
     */
    public ArrayList<Pines> getPines() {
        return pines;
    }

}
