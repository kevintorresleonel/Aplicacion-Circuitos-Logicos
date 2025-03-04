package compuertas;

/*
Integrantes de grupo: 
Torres Kevin
Ramos Mateo 
Ramirez Leonardo
Gonzales Lauren 
 */
import componentes.Pines;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Xor extends Compuertas {

    public Xor(int x, int y, String nomComp) {
        super(x, y, 2, nomComp);
        agregarPin(x, y, "SALIDA");
        agregarPin(x, y, "ENTRADA");
        agregarPin(x, y, "ENTRADA");
    }

    @Override
    public void comprobarTabla() { 
        int v1 = getPines().get(1).getValor(); // Obtiene el valor del primer pin de entrada
        int v2 = 0; // Valor del segundo pin de entrada
        
        
        for (int i = 2; i < getPines().size(); i++) { // Recorre todos los pines de entrada para comparar sus valores
            v2 = getPines().get(i).getValor(); // Obtiene el valor de cada pin de entrada
            if(v2!=v1){ // Si los valores son diferentes, la salida es 1
                setValor(1);
                break;
            }
        }
        
                // Si los valores son iguales, la salida es 0

        if(v1==v2){
            setValor(0);
        }
        asignarValorSalidaAPin();
    }

    // Dibujar compuerta Xor
    @Override
    public void draw(Graphics2D g) {
        g.setStroke(new BasicStroke(2));

        g.drawArc(getX() - 25, getY(), 80, 50, 270, 180); // Arco frontal
        g.drawArc(getX() - 20, getY(), 50, 50, 270, 180); // Arco trasero
        g.drawArc(getX() - 30, getY(), 50, 50, 270, 180); // Arco trasero

        g.drawLine(getX() + 10, getY(), getX() + 20, getY()); // Línea de arriba
        g.drawLine(getX() + 10, getY() + 50, getX() + 20, getY() + 50); // Línea de abajo
        g.drawLine(getX() + 55, getY() + 25, getX() + 70, getY() + 25); // Línea de salida

// Entradas
        if (getNumEntra() == 3) {
            g.drawLine(getX() - 10, getY() + 10, getX() + 28, getY() + 10); // Entrada 1
            g.drawLine(getX() - 10, getY() + 25, getX() + 28, getY() + 25); // Entrada 2
            g.drawLine(getX() - 10, getY() + 40, getX() + 28, getY() + 40); // Entrada 3

            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 10, getY() + (10 + ((index - 1) * 15)));
                }
            }
            drawPin(g);

        } else if (getNumEntra() == 4) {
            g.drawLine(getX() - 15, getY() + 5, getX() + 18, getY() + 5);   // Entrada 1
            g.drawLine(getX() - 15, getY() + 18, getX() + 28, getY() + 18); // Entrada 2
            g.drawLine(getX() - 15, getY() + 31, getX() + 28, getY() + 31); // Entrada 3
            g.drawLine(getX() - 15, getY() + 44, getX() + 18, getY() + 44); // Entrada 4

            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 15, getY() + (5 + ((index - 1) * 13)));
                }
            }
            drawPin(g);

        } else {
            g.drawLine(getX() - 10, getY() + 15, getX() + 18, getY() + 15); // Entrada 1
            g.drawLine(getX() - 10, getY() + 35, getX() + 18, getY() + 35); // Entrada 2

            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 10, getY() + (15 + ((index - 1) * 20)));
                }
            }
            drawPin(g);
        }

    }

    @Override
    public void setNumEntra(int numEntra) {// esto es para agregar los pines segun el numero de entradas
        super.setNumEntra(numEntra);
        if (numEntra >= getPines().size()) {
            for (int i = getPines().size(); i <= numEntra; i++) {
                agregarPin(getX(), getY(), "ENTRADA");
            }
        } else if (getPines().size() > numEntra) {
            eliminarPin();
        }
    }

}
