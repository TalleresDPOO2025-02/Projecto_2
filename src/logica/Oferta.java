package logica;

public class Oferta {
    private static int CONTADOR_ID = 1;
    private final String idOferta;
    private final Tiquete tiquete;
    private final Cliente vendedor;
    private final double precio;
    
    public Oferta(Tiquete tiquete, Cliente vendedor, double precio) {
        this.idOferta = "OFERTA-" + CONTADOR_ID++;
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.precio = precio;
    }

    public Tiquete getTiquete() { return tiquete; }
    public Cliente getVendedor() { return vendedor; }
    public double getPrecio() { return precio; }
    public String getIdTiqueteStr() { return String.valueOf(tiquete.getId()); } 
}