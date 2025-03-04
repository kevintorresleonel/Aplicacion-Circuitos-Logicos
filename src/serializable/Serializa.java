/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serializable;

import circuito.Circuitos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Mateo
 */
public class Serializa {

    public static void serializarCircuito(Circuitos circuito) {
        // Definir la ruta del archivo en la carpeta actual del proyecto
        String rutaArchivo = "./src/serializable/circuito.ser"; // Cambia 'serializar' por la ruta de tu interfaz si es diferente

        // Crear el archivo y escribir el objeto serializado
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(circuito);
            System.out.println("Objeto Circuitos serializado y guardado en: " + rutaArchivo);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al serializar el objeto Circuitos");
        }
    }

    public static Circuitos deserializarCircuito() {
        // Crear un JFileChooser para seleccionar el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Abrir el cuadro de diálogo y almacenar el resultado
        int seleccion = fileChooser.showOpenDialog(null);

        // Verificar si se seleccionó un archivo
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            // Intentar deserializar el objeto desde el archivo seleccionado
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                // Leer el objeto y retornarlo
                Circuitos circuito = (Circuitos) ois.readObject();
                System.out.println("Objeto Circuitos deserializado correctamente.");
                return circuito;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al deserializar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }

        return null; // Retorna null si no se pudo deserializar el objeto
    }

}
