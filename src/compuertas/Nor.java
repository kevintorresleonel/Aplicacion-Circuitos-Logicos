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

public class Nor extends Compuertas {

    public Nor(int x, int y, String nomComp) {
        super(x, y, 2, nomComp);
        agregarPin(x, y, "SALIDA");
        agregarPin(x, y, "ENTRADA");
        agregarPin(x, y, "ENTRADA");
    }


    
    @Override
public void comprobarTabla() {
    int resultado = 0;
 
    // Realizamos la operación OR entre todas las entradas
    for (int i = 1; i < getPines().size(); i++) {
        resultado |= getPines().get(i).getValor(); // OR lógico entre las entradas
    }
 
    // Negamos el resultado de la operación OR para obtener la salida NOR
    int salida = (resultado == 0) ? 1 : 0;
 
    // Establecemos el valor de salida
    setValor(salida);
 
    // Actualizamos el valor en el pin de salida
    asignarValorSalidaAPin();
    
}

    // Dibujar compuerta NOR
    @Override
    public void draw(Graphics2D g) {
        g.setStroke(new BasicStroke(2));

        g.drawArc(getX() - 15, getY(), 70, 50, 270, 180); // Arco frontal
        g.drawArc(getX() - 10, getY(), 40, 50, 270, 180); // Arco trasero
        g.drawOval(getX() + 55, getY() + 20, 10, 10); // Círculo pequeño
        g.drawLine(getX() + 65, getY() + 25, getX() + 80, getY() + 25); // Línea de salida

// Entradas
        if (getNumEntra() == 3) {
            g.drawLine(getX() - 10, getY() + 10, getX() + 28, getY() + 10); // Entrada 1
            g.drawLine(getX() - 10, getY() + 25, getX() + 28, getY() + 25); // Entrada 2
            g.drawLine(getX() - 10, getY() + 40, getX() + 28, getY() + 40); // Entrada 3

            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 80, getY() + 25);
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
                    pine.setXY(getX() + 80, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 15, getY() + (5 + ((index - 1) * 13)));
                }
            }
            drawPin(g);

        } else {
            g.drawLine(getX() - 10, getY() + 15, getX() + 28, getY() + 15); // Entrada 1
            g.drawLine(getX() - 10, getY() + 35, getX() + 28, getY() + 35); // Entrada 2

            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 80, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 15, getY() + (15 + ((index - 1) * 20)));
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
