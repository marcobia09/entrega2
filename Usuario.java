public class Usuario {
    private int id;
    private String nombre;
    private String tipo; // Estudiante o Profesor

    public Usuario(int id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", id, nombre, tipo);
    }
}