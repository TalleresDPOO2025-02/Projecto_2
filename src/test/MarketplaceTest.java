package test;

import logica.*; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MarketplaceTest {

    private Marketplace marketplace; 
    private Cliente vendedor;
    private Cliente comprador;
    private Tiquete tiqueteVentaSimple;
    private Tiquete tiqueteVentaDeluxe;
    private final double PRECIO_OFERTA = 300.0;
    private final double CONTRAOFERTA_MONTO = 250.0;

    public void setupEscenario() throws Exception {
        marketplace = new Marketplace(); 
        vendedor = new Cliente("vendedor@mail.com", "vpass");
        comprador = new Cliente("comprador@mail.com", "cpass");
        
        vendedor.setSaldo(50.0);
        comprador.setSaldo(1000.0); 

        Venue venue = new Venue("Stadium", "Ubicacion X", 1000);
        Evento evento = new Evento("Concierto", java.time.LocalDateTime.now().plusDays(5), "MUSICA", null, venue);
        Localidad localidad = new Localidad("General", false, 100.0, 50, evento);
        

        tiqueteVentaSimple = new TiqueteSimple(localidad, 100.0); 
        tiqueteVentaSimple.setDueno(vendedor);
        vendedor.getTiquetesComprados().add(tiqueteVentaSimple);

        tiqueteVentaDeluxe = new PaqueteDeluxe(localidad, 300.0, "Acceso VIP"); 
        tiqueteVentaDeluxe.setDueno(vendedor);
        vendedor.getTiquetesComprados().add(tiqueteVentaDeluxe);
    }
    

    public void testPublicarOferta_PaqueteDeluxeFalla() {

        assertThrows(ExcepcionMarketplace.class, () -> {
            marketplace.publicarOferta(tiqueteVentaDeluxe, vendedor, 500.0);
        }, "Debería lanzarse ExcepcionMarketplace al intentar vender un Paquete Deluxe.");

        assertFalse(marketplace.existeOferta(String.valueOf(tiqueteVentaDeluxe.getId())), 
            "La oferta Deluxe NO debe aparecer en el Marketplace.");
    }

    public void testAceptarContraoferta_TransaccionCompleta() throws Exception {
        String tiqueteID = String.valueOf(tiqueteVentaSimple.getId());
        double saldoVendedorInicial = vendedor.getSaldo(); 
        double saldoCompradorInicial = comprador.getSaldo(); 
        

        marketplace.publicarOferta(tiqueteVentaSimple, vendedor, PRECIO_OFERTA);
        int idContraOferta = marketplace.hacerContraOferta(tiqueteID, comprador, CONTRAOFERTA_MONTO);
        

        marketplace.aceptarContraOferta(idContraOferta); 

        assertEquals(comprador, tiqueteVentaSimple.getDueno(), "El dueño del tiquete debe ser el Comprador.");
        assertFalse(marketplace.existeOferta(tiqueteID), "La oferta debe eliminarse del Marketplace.");


        double saldoVendedorEsperado = saldoVendedorInicial + CONTRAOFERTA_MONTO; 
        double saldoCompradorEsperado = saldoCompradorInicial - CONTRAOFERTA_MONTO; 
        
        assertEquals(saldoVendedorEsperado, vendedor.getSaldo(), 0.001, "El saldo del Vendedor debe aumentar.");
        assertEquals(saldoCompradorEsperado, comprador.getSaldo(), 0.001, "El saldo del Comprador debe disminuir.");


        assertTrue(marketplace.obtenerLogCompleto().contains("TRANSACCION_EXITOSA"), 
            "El Log debe registrar la transacción final.");
    }
    

    public void testAceptarContraoferta_FallaPorSaldo() throws Exception {

        comprador.setSaldo(100.0); 
        String tiqueteID = String.valueOf(tiqueteVentaSimple.getId());
        
        marketplace.publicarOferta(tiqueteVentaSimple, vendedor, PRECIO_OFERTA);
        int idContraOferta = marketplace.hacerContraOferta(tiqueteID, comprador, CONTRAOFERTA_MONTO);

        assertThrows(ExcepcionMarketplace.class, () -> {
            marketplace.aceptarContraOferta(idContraOferta);
        }, "Debe fallar por falta de saldo.");

        assertEquals(100.0, comprador.getSaldo(), 0.001, "El saldo del comprador no debe cambiar.");
        assertEquals(vendedor, tiqueteVentaSimple.getDueno(), "La propiedad del tiquete NO debe cambiar.");
        assertTrue(marketplace.existeOferta(tiqueteID), "La oferta debe seguir activa.");
        assertTrue(marketplace.obtenerLogCompleto().contains("Fallo transacción"), "El Log debe registrar el fallo.");
    }
}