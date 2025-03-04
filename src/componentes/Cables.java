package componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Cables extends Componente {

    private double porcentajeRelativo;// 
    private int x2; // punto de coordenadas donde se deja el cable
    private int y2; // punto de coordenadas donde se deja el cable
    private Pines origenPinCable = null; // pin de origen del cable
    private Pines destinoPinCable = null; // pin de destino del cable
    private Cables cableOrigen = null; // referencia al cable padre
    public int grosorLinea = 2;
// referencia al cable padre
    private ArrayList<Cables> cablesConectados = new ArrayList<>();
    // separación deseada entre cables hijos
    // lista de cables hijos

    public Cables(int x, int y, int x2, int y2) {
        super(x, y); // inicializa el cable con coordenadas de origen
        this.origenPinCable = null; // inicializa el pin de origen
        this.destinoPinCable = null; // inicializa el pin de destino
        this.x2 = x2; // establece la coordenada x del extremo del cable
        this.y2 = y2; // establece la coordenada y del extremo del cable
    }

    @Override
    public void simular(int valor) {
        setValor(valor);
        // caso de que le salgan otros cables
        if (!cablesConectados.isEmpty()) {
            asignarValosCables();
        }
        //caso de que llegue a un pin 
        if (destinoPinCable != null) {
            destinoPinCable.simular(valor);
        }
    }

    // funcion para darle valor a los cables de salida
    public void asignarValosCables() {
        for (Cables cablesConectado : cablesConectados) {
            cablesConectado.simular(getValor());
        }
    }

    // Método para conectar el cable a un pin
    public void conectarAPin(Pines pin) {
        if (pin.getTipoPin().equals("ENTRADA")) {
            setDestinoPinCable(pin); // si es un pin de entrada, asignarlo como destino
        } else {
            setOrigenPinCable(pin); // si es un pin de salida, asignarlo como origen
        }
    }

    public void setXY(int x, int y) {
        moverCoordenadaCable(x, y, true); // mover el origen del cable
    }

    public void setX2Y2(int x, int y) {
        moverCoordenadaCable(x, y, false); // mover el destino del cable
    }

    // Método para verificar si un punto está cerca de la línea del cable
    public boolean estaEnLaLinea(int posicionX, int posicionY) {
        double distancia = Math.abs((y2 - getY()) * posicionX - (x2 - getX()) * posicionY + x2 * getY() - y2 * getX()) //usando la fórmula de distancia punto-recta en geometría analítica
                / Math.sqrt(Math.pow(y2 - getY(), 2) + Math.pow(x2 - getX(), 2));
        // Fórmula para calcular la distancia entre un punto (posicionX, posicionY) y una línea. definida por los puntos (x1, y1) = (getX(), getY()) y (x2, y2).
        // d = |(y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1| / sqrt((y2 - y1)^2 + (x2 - x1)^2)

        // Verifica si la distancia es menor o igual a 10 y si está dentro de los límites de la línea
        return (distancia <= 15 //Verifican si el clic está dentro del rectángulo delimitador de la línea.
                && Math.min(getX(), x2) <= posicionX && posicionX <= Math.max(getX(), x2)
                && Math.min(getY(), y2) <= posicionY && posicionY <= Math.max(getY(), y2));
    }

    public boolean estaEnLaLineaXY(int posicionX, int posicionY) { // VERIFICA SI EL X Y Y SONLOS EXTREMOS DEL CABLE
        // Definición de los radios para un área de 12x12
        int radioX = 8; // Radio horizontal
        int radioY = 8; // "" Vertical

        // Verificar si el punto está dentro del rectángulo delimitador del óvalo
        if (posicionX >= getX() - radioX && posicionX <= getX() + radioX
                && posicionY >= getY() - radioY && posicionY <= getY() + radioY) {

            // Cálculo de la distancia desde el centro del óvalo
            double dx = (posicionX - getX()) / (double) radioX;
            double dy = (posicionY - getY()) / (double) radioY;

            // Verificar si está dentro del óvalo
            // Verificar si el punto está dentro del elipse usando la ecuación:
            // (dx^2 + dy^2) <= 1
            return (dx * dx + dy * dy) <= 1; // Verifica la ecuación del elipse
        }

        return false;
    }

    public boolean estaEnLaLineaX2Y2(int posicionX, int posicionY) { // VERIFICA SI EL X Y Y SONLOS EXTREMOS DEL CABLE
        // Definición de los radios para un área de 12x12
        int radioX = 8; // Radio horizontal
        int radioY = 8; // "" Vertical

        // Verificar si el punto está dentro del rectángulo delimitador del óvalo
        if (posicionX >= getX2()- radioX && posicionX <= getX2() + radioX
                && posicionY >= getY2() - radioY && posicionY <= getY2() + radioY) {

            // Cálculo de la distancia desde el centro del óvalo
            double dx = (posicionX - getX2()) / (double) radioX;
            double dy = (posicionY - getY2()) / (double) radioY;

            // Verificar si está dentro del óvalo
            // Verificar si el punto está dentro del elipse usando la ecuación:
            // (dx^2 + dy^2) <= 1
            return (dx * dx + dy * dy) <= 1; // Verifica la ecuación del elipse
        }

        return false;
    }
    
      // Método para conectar este cable a otro cable hijo
    public void conectarACable(Cables cable) {  // agregar el porcentaje relativo que representa la distancia a la que debe de ir conectado el cable
        cable.setPorcentajeRelativo(longitudCable(getX(), getY(), cable.getX(), cable.getY()) / longitudCable(getX(), getY(), getX2(), getY2()));
        cablesConectados.add(cable); // Agregar el cable a la lista de cables conectados
        cable.cableOrigen = this; // Configura la referencia al cable de origen
    }

    // Método para mover el cable y actualizar las coordenadas
    public void moverCoordenadaCable(int nuevoX, int nuevoY, boolean esOrigen) {
        if (esOrigen) {
            // Mover el origen
            setX(nuevoX);
            setY(nuevoY);
        } else {
            // Mover el destino
            setX2(nuevoX);
            setY2(nuevoY);
        }
        
        // Actualizar las posiciones de los cables conectados después de mover
        actualizarCablesConectados();
    }
    
    

    // Método para actualizar las posiciones de los cables hijos
    private void actualizarCablesConectados() {
        // Obtener las coordenadas actuales del cable padre
        int x1 = getX(); // coordenada x del origen
        int y1 = getY(); // coordenada y del origen
        int x2 = getX2(); // coordenada x del destino
        int y2 = getY2(); // coordenada y del destino

        // Calcular la longitud del cable padre utilizando la fórmula de la distancia
        double longitudCablePadre = longitudCable(x1, y1, x2, y2);

        // Calcular la cantidad de cables hijos a distribuir a lo largo del cable padre
        int numCablesHijos = cablesConectados.size();

        if (numCablesHijos > 0) {
            for (int i = 0; i < numCablesHijos; i++) {
                Cables cableConectado = cablesConectados.get(i);
                // Calcular la posición a lo largo del cable padre para cada hijo
                double posRelativa = longitudCablePadre * cableConectado.getPorcentajeRelativo();

                // Calcular nuevas coordenadas para cada cable hijo
                // Fórmulas de interpolación lineal:
                double xHijo = x1 + (posRelativa / longitudCablePadre) * (x2 - x1);
                double yHijo = y1 + (posRelativa / longitudCablePadre) * (y2 - y1);

                // Actualizar las coordenadas del cable hijo
                cableConectado.setX((int) xHijo);
                cableConectado.setY((int) yHijo);
            }
        }
    }

    public double longitudCable(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // Método para dibujar el cable
    public void draw(Graphics2D g) {
        g.setStroke(new BasicStroke(grosorLinea)); // establecer el grosor de la línea
        g.setColor(Color.BLACK); // establecer el color de la línea
        g.drawLine(getX(), getY(), getX2(), getY2()); // dibujar la línea del cable
    }

  
    // Para eliminar cables que ya estan unidos a los pins

    public void quitarReferenciaAcablesHijos() {
        for (Cables cablesConectado : cablesConectados) {
            cablesConectado.setCableOrigen(null);
        }
    }

    @Override
    public int getX() {
        return super.getX(); // obtener la coordenada x del origen
    }

    @Override
    public void setX(int x) {
        super.setX(x); // establecer la coordenada x del origen
    }

    @Override
    public int getY() {
        return super.getY(); // obtener la coordenada y del origen
    }

    @Override
    public void setY(int y) {
        super.setY(y); // establecer la coordenada y del origen
    }

    public int getX2() {
        return x2; // obtener la coordenada x del destino
    }

    public void setX2(int x2) {
        this.x2 = x2; // establecer la coordenada x del destino
    }

    public int getY2() {
        return y2; // obtener la coordenada y del destino
    }

    public void setY2(int y2) {
        this.y2 = y2; // establecer la coordenada y del destino
    }

    public Cables getCableOrigen() {
        return cableOrigen; // obtener el cable padre
    }

    public void setCableOrigen(Cables cableOrigen) {
        this.cableOrigen = cableOrigen; // establecer el cable padre
    }

    public ArrayList<Cables> getCablesConectados() {
        return cablesConectados; // obtener la lista de cables conectados
    }

    public Pines getOrigenPinCable() {
        return origenPinCable; // obtener el pin de origen
    }

    public void setOrigenPinCable(Pines origenPinCable) {
        this.origenPinCable = origenPinCable; // establecer el pin de origen
    }

    public Pines getDestinoPinCable() {
        return destinoPinCable; // obtener el pin de destino
    }

    public void setDestinoPinCable(Pines destinoPinCable) {
        this.destinoPinCable = destinoPinCable; // establecer el pin de destino
    }

    /**
     * @return the porcentajeRelativo
     */
    public double getPorcentajeRelativo() {
        return porcentajeRelativo;
    }

    /**
     * @param porcentajeRelativo the porcentajeRelativo to set
     */
    public void setPorcentajeRelativo(double porcentajeRelativo) {
        this.porcentajeRelativo = porcentajeRelativo;
    }

}
