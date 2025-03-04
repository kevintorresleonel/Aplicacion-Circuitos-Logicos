package compuertas;

/*
Integrantes de grupo: 
Torres Kevin
Ramirez Leonardo
Ramos Mateo 
Gonzales Lauren 
 */
import componentes.Pines;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public final class Not extends Compuertas {

    public Not(int x, int y, String nombreComp) {
        super(x, y, 1, nombreComp);
        agregarPin(x, y, "SALIDA");
        agregarPin(x, y, "ENTRADA");
    }
    
        @Override
    public void comprobarTabla() {        // Si el valor de la entrada es 0, la salida será 1 (NOT lógico)

        if(getPines().get(1).getValor()==0){
            
            setValor(1);// Negación: si la entrada es 0, la salida es 1
            
        }else{
            setValor(0); // Si la entrada es 1, la salida es 0
        }
        asignarValorSalidaAPin();
    }


    @Override
    //se dibuja la compuerta
    public void draw(Graphics2D g) {
        // CAMBIAMOS LAS COORENADAS DE LOS PINES DE LA COMPUERTA
        for (Pines pine : getPines()) {
            if (pine.getTipoPin().equals("SALIDA")) {
                pine.setXY(getX() + 80, getY() + 25);
            } else {
                pine.setXY(getX() - 20, getY() + 25);
            }
        }
        g.setStroke(new BasicStroke(2));
        g.drawLine(getX(), getY(), getX() + 50, getY() + 25); // Triángulo patita arriba
        g.drawLine(getX() + 50, getY() + 25, getX(), getY() + 50); // Triángulo patita abajo
        g.drawLine(getX(), getY(), getX(), getY() + 50); // Triángulo patita hipotenusa
        g.drawOval(getX() + 50, getY() + 20, 10, 10); // Círculo
        g.drawLine(getX() - 20, getY() + 25, getX(), getY() + 25); // Patita atrás del triángulo
        g.drawLine(getX() + 60, getY() + 25, getX() + 80, getY() + 25); // Patita afuera del triángulo 
        drawPin(g);
    }


    @Override
    public void agregarPin(int x, int y, String tipoPin) {
        super.agregarPin(x, y, tipoPin);
    }

}
