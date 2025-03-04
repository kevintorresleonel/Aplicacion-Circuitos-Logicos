package componentes;

import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mateo
 * @author Leonardo Ramirez
 * @author  Kevin Torres
 * @author Lauren
 */
public abstract class  Componente implements  Serializable{
    private int x;
    private int y; // Estas dos son las coordenadas en el draw 2D
    private int valor;
    enum tipoComponente {  // Enumera los diferentes tipos de compuertas
        LED,
        CABLES,
        COMPUERTA,
        PIN,
        SWITCH,
    }
    public tipoComponente tipoComp; // almacena el tipo de componente
    
    public  void simular(int valor){} // Sirven para simular esta con un valor de entrada y la otra sin valor de
    public  void simular(){}
//
    public Componente(int x, int y) { // El metodo constructor
        this.x = x;
        this.y = y;
        this.valor=-1; // Valor inicial del componente
    }

    /**
     * @return the x
     * Devuelve la coordenada x del componente
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     * Coloca una nueva coordenada x 
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     * Lo mismo que el get X, pero con Y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     * Lo mismo que el set X, pero con Y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the valor
     * Devuelve el valor actual
     */
    public int getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     * Establece un nuevo valor
     */
    public void setValor(int valor) {
        this.valor = valor;
    }
    
}
