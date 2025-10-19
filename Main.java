import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Biblioteca b = new Biblioteca();

        // Datos de ejemplo
        b.agregarLibro(new Libro(1, "Cien Años de Soledad", "Gabriel García Márquez"));
        b.agregarLibro(new Libro(2, "Programación en Java", "Autor Ejemplo"));
        b.agregarLibro(new Libro(3, "Estructuras de Datos", "Autor Ejemplo 2"));

        b.registrarUsuario(new Usuario(100, "Ana Pérez", "Estudiante"));
        b.registrarUsuario(new Usuario(101, "Carlos Ruiz", "Profesor"));

        System.out.println("--- LISTA DE LIBROS ---");
        for (Libro l : b.listarLibros()) System.out.println(l);

        System.out.println("\n--- LISTA DE USUARIOS ---");
        for (Usuario u : b.listarUsuarios()) System.out.println(u);

        System.out.println("\n--- BUSCAR POR TITULO 'java' ---");
        List<Libro> encontrados = b.buscarPorTitulo("java");
        for (Libro x : encontrados) System.out.println(x);

        System.out.println("\n--- PRESTAR LIBRO ID 2 A USUARIO 100 ---");
        System.out.println(b.prestar(2, 100));
        System.out.println("Disponibilidad libro 2: " + b.disponibilidad(2));

        System.out.println("\n--- INTENTAR PRESTAR LIBRO 2 A USUARIO 101 (se pondrá en cola) ---");
        System.out.println(b.prestar(2, 101));

        System.out.println("\n--- DEVOLVER LIBRO 2 (se asigna al primero en cola) ---");
        System.out.println(b.devolver(2));
        System.out.println("Disponibilidad libro 2: " + b.disponibilidad(2));

        System.out.println("\n--- USAR DESHACER ---");
        System.out.println(b.deshacer()); // deshace la última acción
        System.out.println("Disponibilidad libro 2: " + b.disponibilidad(2));

        // Interfaz mínima por consola (opcional)
        Scanner sc = new Scanner(System.in);
        System.out.println("\nInterfaz rápida: escribe 'salir' para terminar.");
        while (true) {
            System.out.print("comando> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("salir")) break;
            if (line.startsWith("prestar")) {
                // prestar <libroId> <userId>
                String[] t = line.split("\\s+");
                if (t.length>=3) System.out.println(b.prestar(Integer.parseInt(t[1]), Integer.parseInt(t[2])));
                else System.out.println("Uso: prestar <libroId> <userId>");
            } else if (line.startsWith("devolver")) {
                String[] t = line.split("\\s+");
                if (t.length>=2) System.out.println(b.devolver(Integer.parseInt(t[1])));
                else System.out.println("Uso: devolver <libroId>");
            } else if (line.equalsIgnoreCase("deshacer")) {
                System.out.println(b.deshacer());
            } else if (line.equalsIgnoreCase("listar libros")) {
                for (Libro l : b.listarLibros()) System.out.println(l);
            } else if (line.equalsIgnoreCase("listar usuarios")) {
                for (Usuario u : b.listarUsuarios()) System.out.println(u);
            } else {
                System.out.println("Comandos: prestar, devolver, deshacer, listar libros, listar usuarios, salir");
            }
        }
        sc.close();
    }
}