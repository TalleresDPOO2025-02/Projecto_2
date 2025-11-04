package logica;

public class ContraOferta {
    private static int CONTADOR_ID = 1;
    private final int id;
    private final Oferta oferta;
    private final Cliente comprador;
    private final double monto;

    public ContraOferta(Oferta oferta, Cliente comprador, double monto) {
        this.id = CONTADOR_ID++;
        this.oferta = oferta;
        this.comprador = comprador;
        this.monto = monto;
    }

    public int getId() { return id; }
    public Oferta getOferta() { return oferta; }
    public Cliente getComprador() { return comprador; }
    public double getMonto() { return monto; }
}