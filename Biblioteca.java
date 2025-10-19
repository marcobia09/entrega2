import java.util.*;

// Clase que organiza libros, usuarios, préstamos y cola de espera.
public class Biblioteca {
    private TreeMap<Integer, Libro> libros;
    private TreeMap<Integer, Usuario> usuarios;
    private Map<Integer, Queue<Integer>> colasEspera; // libroId -> queue de userId
    private Map<Integer, Integer> prestados; // libroId -> userId
    private Stack<String> historialAcciones; // para deshacer (registro simple)

    public Biblioteca() {
        libros = new TreeMap<>();
        usuarios = new TreeMap<>();
        colasEspera = new HashMap<>();
        prestados = new HashMap<>();
        historialAcciones = new Stack<>();
    }

    // Libros
    public void agregarLibro(Libro l) {
        libros.put(l.getId(), l);
        historialAcciones.push("AGREGAR_LIBRO:" + l.getId());
    }

    public List<Libro> listarLibros() {
        return new ArrayList<>(libros.values());
    }

    public List<Libro> buscarPorTitulo(String q) {
        List<Libro> res = new ArrayList<>();
        for (Libro l : libros.values()) {
            if (l.getTitulo().toLowerCase().contains(q.toLowerCase())) res.add(l);
        }
        return res;
    }

    public String disponibilidad(int libroId) {
        Libro l = libros.get(libroId);
        if (l == null) return "Libro no encontrado";
        return l.isDisponible() ? "Disponible" : "No disponible";
    }

    // Usuarios
    public void registrarUsuario(Usuario u) {
        usuarios.put(u.getId(), u);
        historialAcciones.push("REGISTRAR_USUARIO:" + u.getId());
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    // Prestamos
    public String prestar(int libroId, int userId) {
        Libro l = libros.get(libroId);
        if (l == null) return "Libro no existe.";
        if (!usuarios.containsKey(userId)) return "Usuario no registrado.";

        if (l.isDisponible()) {
            l.setDisponible(false);
            prestados.put(libroId, userId);
            historialAcciones.push("PRESTAR:" + libroId + ":" + userId);
            return "Prestado correctamente a usuario " + userId;
        } else {
            // agregar a cola de espera
            Queue<Integer> q = colasEspera.computeIfAbsent(libroId, k -> new LinkedList<>());
            q.add(userId);
            historialAcciones.push("APUNTA_COLA:" + libroId + ":" + userId);
            return "Libro no disponible. Usuario agregado a la cola. Posición: " + q.size();
        }
    }

    public String devolver(int libroId) {
        Libro l = libros.get(libroId);
        if (l == null) return "Libro no existe.";
        if (!prestados.containsKey(libroId)) return "El libro no está prestado.";

        prestados.remove(libroId);
        // si hay cola, asignarlo al primero
        Queue<Integer> q = colasEspera.getOrDefault(libroId, new LinkedList<>());
        if (!q.isEmpty()) {
            int proxUser = q.poll();
            prestados.put(libroId, proxUser);
            historialAcciones.push("DEVOLVER_ASIGNAR:" + libroId + ":" + proxUser);
            return "Libro devuelto y asignado al usuario en cola: " + proxUser;
        } else {
            l.setDisponible(true);
            historialAcciones.push("DEVOLVER_LIBRE:" + libroId);
            return "Libro devuelto y queda disponible.";
        }
    }

    // Deshacer (reversión simple basada en historial de acciones)
    public String deshacer() {
        if (historialAcciones.isEmpty()) return "Nada que deshacer.";
        String acc = historialAcciones.pop();
        String[] parts = acc.split(":");
        switch (parts[0]) {
            case "AGREGAR_LIBRO": {
                int id = Integer.parseInt(parts[1]);
                libros.remove(id);
                return "Deshecho: agregar libro " + id;
            }
            case "REGISTRAR_USUARIO": {
                int id = Integer.parseInt(parts[1]);
                usuarios.remove(id);
                return "Deshecho: registrar usuario " + id;
            }
            case "PRESTAR": {
                int libroId = Integer.parseInt(parts[1]);
                // quitar préstamo
                prestados.remove(libroId);
                Libro l = libros.get(libroId);
                if (l != null) l.setDisponible(true);
                return "Deshecho: préstamo libro " + libroId;
            }
            case "APUNTA_COLA": {
                int libroId = Integer.parseInt(parts[1]);
                int userId = Integer.parseInt(parts[2]);
                Queue<Integer> q = colasEspera.get(libroId);
                if (q != null) {
                    // remover primera ocurrencia de userId (si existe)
                    List<Integer> temp = new ArrayList<>(q);
                    temp.remove(Integer.valueOf(userId));
                    q.clear();
                    q.addAll(temp);
                }
                return "Deshecho: remover de cola usuario " + userId;
            }
            case "DEVOLVER_ASIGNAR": {
                int libroId = Integer.parseInt(parts[1]);
                int userId = Integer.parseInt(parts[2]);
                // revertir asignación: volver a marcar libro como no disponible y prestado a userId
                Libro l = libros.get(libroId);
                if (l != null) l.setDisponible(false);
                prestados.put(libroId, userId);
                return "Deshecho: revertir asignación del libro " + libroId + " al usuario " + userId;
            }
            case "DEVOLVER_LIBRE": {
                int libroId = Integer.parseInt(parts[1]);
                Libro l = libros.get(libroId);
                if (l != null) l.setDisponible(false);
                // no volvemos a establecer prestado (no tenemos id), es una reversión aproximada
                return "Deshecho: revertir devolución (marcar no disponible) del libro " + libroId;
            }
        }
        return "Acción de historial no reconocida: " + acc;
    }
}