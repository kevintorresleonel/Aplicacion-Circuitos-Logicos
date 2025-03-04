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

public class Or extends Compuertas {

    public Or(int x, int y, String nomComp) {
        super(x, y, 2, nomComp);
        agregarPin(x, y, "SALIDA");
        agregarPin(x, y, "ENTRADA");
        agregarPin(x, y, "ENTRADA");
    }
    
    
@Override
    public void comprobarTabla() {// por definir
          int resultado = 0;
         for (int i = 1; i < getPines().size(); i++) {
            resultado |= getPines().get(i).getValor(); // Operación OR: Si algún valor es 1, el resultado será 1
        }
        
        // Asigna el resultado al valor de la compuerta
        setValor(resultado);
        
        // Asigna el valor de salida a la salida del pin correspondiente
        asignarValorSalidaAPin();
    }
    
    

    @Override
    //se dibuja la compuerta con 2 entradas
    public void draw(Graphics2D g) {

        g.setStroke(new BasicStroke(2));
        g.drawArc(getX() - 15, getY(), 70, 50, 270, 180); // Arco frente
        g.drawArc(getX() - 10, getY(), 40, 50, 270, 180); // Arco atrás
        g.drawLine(getX() + 10, getY() + 50, getX() + 20, getY() + 50); // Línea de abajo
        g.drawLine(getX() + 10, getY(), getX() + 20, getY()); // Línea de arriba
        g.drawLine(getX() + 55, getY() + 25, getX() + 70, getY() + 25); // Línea afuera

        if (getNumEntra() == 3) {
            g.drawLine(getX(), getY() + 10, getX() + 28, getY() + 10); // Patita 1
            g.drawLine(getX(), getY() + 25, getX() + 28, getY() + 25); // Patita 2
            g.drawLine(getX(), getY() + 40, getX() + 28, getY() + 40); // Patita 3
            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX(), getY() + (10 + ((index - 1) * 15)));
                }
            }
            drawPin(g);
        } else if (getNumEntra() == 4) {
            g.drawLine(getX() - 10, getY() + 5, getX() + 18, getY() + 5);    // Patita 1
            g.drawLine(getX() - 10, getY() + 18, getX() + 28, getY() + 18);  // Patita 2
            g.drawLine(getX() - 10, getY() + 31, getX() + 28, getY() + 31);  // Patita 3
            g.drawLine(getX() - 10, getY() + 44, getX() + 18, getY() + 44); // Patita 4
            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX() - 10, getY() + (5 + ((index - 1) * 13)));
                }
            }
            drawPin(g);
        } else {
            g.drawLine(getX(), getY() + 15, getX() + 28, getY() + 15); // Patita 1
            g.drawLine(getX(), getY() + 35, getX() + 28, getY() + 35); // Patita 2
            for (Pines pine : getPines()) {
                if (pine.getTipoPin().equals("SALIDA")) {
                    pine.setXY(getX() + 70, getY() + 25);
                } else {
                    int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                    pine.setXY(getX(), getY() + (15 + ((index - 1) * 20)));
                }
            }
            drawPin(g);
        }

    }


    
    @Override
    public void setNumEntra(int numEntra) { // esto es para agregar los pines segun el numero de entradas
        super.setNumEntra(numEntra);
        if (numEntra >= getPines().size()) {
            for (int i = getPines().size(); i <= numEntra; i++) {
                agregarPin(getX(), getY(), "ENTRADA");
            }
                    // Si el número de entradas es menor, se eliminan los pines extra

        } else if (getPines().size() > numEntra) {
            eliminarPin();
        }
    }

}
