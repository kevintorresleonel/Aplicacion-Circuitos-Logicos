package interfaz;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author Mateo
 * @author Leonardo Ramirez
 * @author Kevin Torres
 * @author Lauren
 * 
 *  Interfaz que define los nombres de las compuertas lógicas disponibles.
 */


public interface NombreCompuerta {
    public enum CompuertaLogica {
        
    /*
     * Enumeración de los tipos de compuertas lógicas.
     * Cada compuerta lógica ya tiene su logica en cada archivo
     */
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    XOR("XOR"),
    NAND("NAND"),
    XNOR("XNOR"),
    NOR("NOR");

            // Variable que guarda el nombre de la compuerta lógica

    private final String nombre;

    // Constructor para asignar el valor de cadena
    CompuertaLogica(String nombre) {
        this.nombre = nombre;
    }

    // Método para obtener el valor de cadena
    public String getNombre() {
        return nombre;
    }
}

}
