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

public final class And extends Compuertas {

    public And(int x, int y, String nomCompu) {
        super(x, y, 2, nomCompu);
        agregarPin(x, y, "SALIDA"); // Pin de salida
        agregarPin(x, y, "ENTRADA"); // primer pin entrada
        agregarPin(x, y, "ENTRADA"); // Segundo pin entrada
    }

    @Override
    //se dibuja la compuerta 
    public void draw(Graphics2D g) {

        g.setColor(new Color(0, 0, 255, 100)); // Este sería donde colocamos el color en RGB
        g.fillArc(getX(), getY(), 50, 50, 270, 180); // Arco de la forma AND
        g.fillRect(getX(), getY(), 25, 50); // Cuadro detras del AND


        g.setColor(Color.BLACK); // El color de la compuerta al rededor no modificar
        
        g.setStroke(new BasicStroke(2));
        g.drawArc(getX(), getY(), 50, 50, 270, 180); // Arco
        g.drawLine(getX(), getY(), getX(), getY() + 50); // Linea vertical
        g.drawLine(getX(), getY(), getX() + 25, getY()); // Linea de arriba del AND
        g.drawLine(getX(), getY() + 50, getX() + 25, getY() + 50); // Linea de abajo del AND
        g.drawLine(getX() + 50, getY() + 25, getX() + 70, getY() + 25); // Linea que resalta afuera    
        switch (getNumEntra()) {
            case 3 -> {
                // se dibujan 3 lineas
                g.drawLine(getX() - 20, getY() + 10, getX(), getY() + 10); // Patita 1
                g.drawLine(getX() - 20, getY() + 25, getX(), getY() + 25); // Patita 2
                g.drawLine(getX() - 20, getY() + 40, getX(), getY() + 40); // Patita 3
                             // Ajusta la posición de los pines

                for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 70, getY() + 25);  // añade las nuevas coordenadas del pin
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                        pine.setXY(getX()-20, getY() + (10 + ((index-1)*15)));
                    }
                }
                drawPin(g);
            }
            case 4 -> {
                                // se dibujan 4 lineas

                g.drawLine(getX() - 20, getY() + 5, getX(), getY() + 5);    // Patita 1
                g.drawLine(getX() - 20, getY() + 18, getX(), getY() + 18);  // Patita 2
                g.drawLine(getX() - 20, getY() + 31, getX(), getY() + 31);  // Patita 3
                g.drawLine(getX() - 20, getY() + 44, getX(), getY() + 44);  // Patita 4
                
             // Ajusta la posición de los pines
                for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 70, getY() + 25);
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pines en el array 
                        pine.setXY(getX()-20, getY() + (5 + ((index-1)*13)));
                    }
                }
                drawPin(g); // dibuja los pines xD
                
            }
            default -> {
                g.drawLine(getX() - 20, getY() + 15, getX(), getY() + 15); // Patita 1
                g.drawLine(getX() - 20, getY() + 35, getX(), getY() + 35); // Patita 2
                for (Pines pine : getPines()) {
                    if (pine.getTipoPin().equals("SALIDA")) {
                        pine.setXY(getX() + 70, getY() + 25);
                    } else {
                        int index = getPines().indexOf(pine); // se obtiene el indice de lo pnes en el array 
                        pine.setXY(getX()-20, getY() + (15 + ((index-1)*20)));
                    }
                }
                drawPin(g);
            }
        }

    }

    @Override
    public void comprobarTabla() { // Método que verifica los valores de entrada y genera el valor de salida según la tabla de verdad de la compuerta AND
        int v=1;   // Valor inicial de la AND
        // Multiplica los valores de todas las entradas
        for (int i = 1; i < getPines().size(); i++) {
            v=v*getPines().get(i).getValor();
        }
        
        // Asigna el valor resultante a la salida
        setValor(v);
        asignarValorSalidaAPin(); 
    }

    @Override
    //ajustar el número de entradas de la compuerta
    public void setNumEntra(int numEntra) { // esto es para agregar los pines segun el numero de entradas
        super.setNumEntra(numEntra);
        if (numEntra >= getPines().size()) {
            for (int i = getPines().size(); i <= numEntra; i++) {
                agregarPin(getX(), getY(), "ENTRADA");
            }
        }else if(getPines().size() > numEntra){
            // Si hay más pines de los necesarios, los elimina
            eliminarPin();
        }
    }

}
