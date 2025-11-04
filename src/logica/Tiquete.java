package logica;

public abstract class Tiquete {
    private static int contador = 0;
    protected int id;
    protected double precio;
    protected boolean disponible;
    protected boolean transferible;
    protected Cliente dueno;
    protected Localidad localidad;

    public Tiquete(Localidad localidad, double precio) {
        this.id = ++contador;
        this.localidad = localidad;
        this.precio = precio;
        this.disponible = true;
        this.transferible = true;
        this.dueno = null;
    }
    public int getId() {
        return id;
    }

    public boolean marcarComoVendido(Cliente c) {
        if (disponible) {
            disponible = false;
            dueno = c;
            return true;
        }
        return false;
    }

    public boolean isVencido() {
        return e.getFechaHora().isBefore(java.time.LocalDateTime.now());
    }

    public static int getContador() {
        return contador;
    }
    
 static void setContador(int nuevoContador) {
        contador = nuevoContador;
    }
}