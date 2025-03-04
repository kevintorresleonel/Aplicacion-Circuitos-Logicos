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

public class Nand extends Compuertas {

    public Nand(int x, int y, String nomComp) {
        super(x, y, 2,nomComp);
        agregarPin(x, y, "SALIDA");
        agregarPin(x, y, "ENTRADA");
        agregarPin(x, y, "ENTRADA");
    }
    
    
        @Override
    public void comprobarTabla() { 
        // Inicializa el valor como 1 (para simular la operación AND)
        // Itera sobre los pines de entrada para calcular el resultado AND de todos

        int v=1;  
        for (int i = 1; i < getPines().size(); i++) {
            v=v*getPines().get(i).getValor();
        }
         // Si el resultado es 1 (todos los pines de entrada son 1), la salida es 0
        if(v==1){
            setValor(0);
        }else{
            setValor(1);
        }
        asignarValorSalidaAPin();
    }

    // Dibujar compuerta NAND
    @Override
    public void draw(Graphics2D g) {
        
        g.setColor(new Color(0, 0, 255, 100)); // Este sería donde colocamos el color en RGB
        g.fillArc(getX(), getY(), 50, 50, 270, 180); // Arco de la forma AND
        g.fillRect(getX(), getY(), 25, 50); // Cuadro detras del AND


        g.setColor(Color.BLACK); // El color de la compuerta al rededor no modificar
        
        g.setStroke(new BasicStroke(2));
        g.drawArc(getX(), getY(), 50, 50, 270, 180); // Arco
        g.drawLine(getX(), getY(), getX(), getY() + 50); // Línea vertical
        g.drawLine(getX(), getY(), getX() + 25, getY()); // Línea superior
        g.drawLine(getX(), getY() + 50, getX() + 25, getY() + 50); // Línea inferior
        g.drawOval(getX() + 50, getY() + 20, 10, 10); // Círculo pequeño
        g.drawLine(getX() + 60, getY() + 25, getX() + 80, getY() + 25); // Línea de salida

// Entradas
        if (getNumEntra() == 3) {
            g.drawLine(getX() - 20, getY() + 10, getX(), getY() + 10); // Entrada 1
            g.drawLine(getX() - 20, getY() + 25, getX(), getY() + 25); // Entrada 2
            g.drawLine(getX() - 20, getY() + 40, getX(), getY() + 40); // Entrada 3
            
                         for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 80, getY() + 25);
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                        pine.setXY(getX()-20, getY() + (10 + ((index-1)*15)));
                    }
                }
                drawPin(g);
            
        } else if (getNumEntra() == 4) {
            g.drawLine(getX() - 20, getY() + 5, getX(), getY() + 5);   // Entrada 1
            g.drawLine(getX() - 20, getY() + 18, getX(), getY() + 18); // Entrada 2
            g.drawLine(getX() - 20, getY() + 31, getX(), getY() + 31); // Entrada 3
            g.drawLine(getX() - 20, getY() + 44, getX(), getY() + 44); // Entrada 4
            
                         for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 80, getY() + 25);
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                        pine.setXY(getX()-20, getY() + (5 + ((index-1)*13)));
                    }
                }
                drawPin(g);
            
        } else {
            g.drawLine(getX() - 20, getY() + 15, getX(), getY() + 15); // Entrada 1
            g.drawLine(getX() - 20, getY() + 35, getX(), getY() + 35); // Entrada 2
                         for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 80, getY() + 25);
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                        pine.setXY(getX()-20, getY() + (15 + ((index-1)*20)));
                    }
                }
                drawPin(g);
        }

    }


        @Override
    public void setNumEntra(int numEntra) {// esto es para agregar los pines segun el numero de entradas
        super.setNumEntra(numEntra);// Llama al método de la clase padre para actualizar el número de entradas
        // Si el número de entradas es mayor, se añaden pines
        if (numEntra >= getPines().size()) {
            for (int i = getPines().size(); i <= numEntra; i++) {
                agregarPin(getX(), getY(), "ENTRADA");
            }
        }else if(getPines().size() > numEntra){
            eliminarPin();
        }
    }

}
