package presentacion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);


    public static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); 
                return valor;
            } catch (InputMismatchException e) {
                System.err.println("Error: Por favor, ingrese un numero entero valido.");
                scanner.nextLine(); 
                System.out.print(mensaje);
            }
        }
    }

    public static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        while (true) {
            try {
                double valor = scanner.nextDouble();
                scanner.nextLine(); 
                return valor;
            } catch (InputMismatchException e) {
                System.err.println("Error: Por favor, ingrese un numero decimal valido.");
                scanner.nextLine(); 
                System.out.print(mensaje);
            }
        }
    }
}