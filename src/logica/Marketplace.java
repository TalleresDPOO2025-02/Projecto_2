package logica;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Marketplace {
    
    private final Map<String, Oferta> ofertasActivas; 
    private final Map<Integer, ContraOferta> contraOfertas; 
    private final LogRegistros log;

    public Marketplace() {
        this.ofertasActivas = new HashMap<>();
        this.contraOfertas = new HashMap<>();
        this.log = new LogRegistros();
    }

    // ================== MÉTODOS DE VENTA ==================

    public void publicarOferta(Tiquete tiquete, Cliente vendedor, double precioVenta) throws ExcepcionMarketplace {
        if (tiquete instanceof PaqueteDeluxe) {
            throw new ExcepcionMarketplace("Error de Validación: No se permite la reventa de Paquetes Deluxe.");
        }
        
        String idTiqueteStr = String.valueOf(tiquete.getId());
        
        Oferta nuevaOferta = new Oferta(tiquete, vendedor, precioVenta);
        ofertasActivas.put(idTiqueteStr, nuevaOferta);
        
        log.registrarEvento("PUBLICACION", vendedor.getCorreo(), 
            String.format("Publicó tiquete %s a $%.2f", idTiqueteStr, precioVenta));
    }
    
    // ================== MÉTODOS DE COMPRA (CONTRAOFERTA) ==================

    public int hacerContraOferta(String idTiquete, Cliente comprador, double monto) throws ExcepcionMarketplace {
        Oferta oferta = ofertasActivas.get(idTiquete);
        
        if (oferta == null) {
            throw new ExcepcionMarketplace("El tiquete no está en venta.");
        }
        
        if (monto <= 0) {
             throw new ExcepcionMarketplace("El monto debe ser positivo.");
        }
        
        ContraOferta nuevaCO = new ContraOferta(oferta, comprador, monto);
        contraOfertas.put(nuevaCO.getId(), nuevaCO);
        
        log.registrarEvento("CONTRAOFERTA", comprador.getCorreo(), 
            String.format("Propuesta de $%.2f por tiquete %s", monto, idTiquete));
            
        return nuevaCO.getId();
    }

    public void aceptarContraOferta(int idContraOferta) throws ExcepcionMarketplace {
        ContraOferta co = contraOfertas.get(idContraOferta);

        if (co == null) {
            throw new ExcepcionMarketplace("Contraoferta no encontrada.");
        }

        Oferta oferta = co.getOferta();
        Cliente vendedor = oferta.getVendedor();
        Cliente comprador = co.getComprador();
        double monto = co.getMonto();
        Tiquete tiquete = oferta.getTiquete();

        // 1. VALIDACIÓN CRÍTICA: SALDO
        if (comprador.getSaldo() < monto) {
            log.registrarEvento("FALLO", vendedor.getCorreo(), "Fallo transaccion: Saldo insuficiente del comprador.");
            throw new ExcepcionMarketplace("Saldo insuficiente ($" + comprador.getSaldo() + ") para completar la compra de $" + monto);
        }

        comprador.setSaldo(comprador.getSaldo() - monto);
        vendedor.setSaldo(vendedor.getSaldo() + monto);
        
        vendedor.getTiquetesComprados().remove(tiquete);
        comprador.getTiquetesComprados().add(tiquete);
        tiquete.setDueno(comprador);
        
        ofertasActivas.remove(String.valueOf(tiquete.getId()));
        contraOfertas.values().removeIf(c -> c.getOferta().equals(oferta));
        
        log.registrarEvento("TRANSACCION_EXITOSA", comprador.getCorreo(), 
            String.format("Compra exitosa de tiquete %d por $%.2f", tiquete.getId(), monto));
    }
    
    // ================== MÉTODOS DE CONSULTA ==================
    
    public Collection<Oferta> obtenerTodasLasOfertas() {
        return ofertasActivas.values();
    }
    
    public List<Oferta> obtenerOfertasPorVendedor(Cliente vendedor) {
        return ofertasActivas.values().stream()
            .filter(o -> o.getVendedor().equals(vendedor))
            .collect(Collectors.toList());
    }
    
    public List<ContraOferta> obtenerContraOfertasParaOferta(Oferta oferta) {
        return contraOfertas.values().stream()
            .filter(co -> co.getOferta().equals(oferta))
            .collect(Collectors.toList());
    }

    public ContraOferta buscarContraOferta(int id) {
        return contraOfertas.get(id);
    }
}