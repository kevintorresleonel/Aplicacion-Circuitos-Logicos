package componentes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import componentes.Leds;

/*
La principial función del archivo Leds.java es representar en el draw un Led que sería el encendido(1) o apagado(0) representado como 1 || 0

*/
public class Leds extends Componente {
    protected int width, height; // Tamaño
    private Pines pin; // Pin asociado al LED

    public Leds(int x, int y) { 
        super(x, y);
        this.width = 20;  // Ancho del LED
        this.height = 20; // Alto del LED
        // Agregar el pin de entrada
        agregarPin(x, y);

       
    }

    @Override
    public void simular(int valor) {
        setValor(valor); // Asigna el valor de entrada del Led
    }
    
    /*
    Si el valor es uno se pondrá rojo, si no, pasará a ser gris, además este cuenta con un pin en el centro para pegar el cable
    */


    public void draw(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    // Dibujar el LED
    g2d.setColor(getValor() == 1 ? Color.RED : Color.GRAY);
    g2d.fillOval(getX(), getY(), width, height);

    // Dibujar el pin como un pequeño círculo negro dentro del LED
    g2d.setColor(Color.BLACK);
    g2d.fillOval(getX() + width / 2 - 5, getY() + height / 2 - 5, 10, 10);

    // Actualizar la posición del pin para que siempre esté centrado dentro del LED
    pin.setXY(getX() + width / 2 - 5, getY() + height / 2 - 5);
}

/*
    verifica si la posición de X,Y esta dentro del area ocupada por el LED
    */
    public boolean estaEnLaLinea(int posicionX, int posicionY) {
        return posicionX >= getX() && posicionX <= getX() + width+5 && posicionY >= getY() && posicionY <= getY() + height+5;
    }
    
    // Metodo para aceptar el cable, se conecta el pin al cable y viceversa
    public void conectarPin(Cables cable) {
    if (cable != null) {
        pin.setCableEntradaSalida(cable); // Conectar el cable al pin del LED
        cable.conectarAPin(pin); // Conectar el cable al pin correctamente
    }
}
    // Para crear el pin en el centro del LED 
      private void agregarPin(int x, int y) {
                pin = new Pines(x + width / 2 - 5, y + height / 2 - 5, "ENTRADA"); // Posición inicial centrada en el LED
                pin.setLedEntrada(this);
    }


    // Getters y Setters
    public Pines getPin() {
        return pin;
    }

    public void setPin(Pines pin) {
        this.pin = pin;
    }

  


}
