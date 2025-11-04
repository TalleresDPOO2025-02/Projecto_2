package presentacion;

import logica.*; 
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;

public class MenuClienteConsola {
    
    private final Cliente clienteActual;
    private final Marketplace marketplace;
    
    public MenuClienteConsola(Cliente cliente, Marketplace marketplace) {
        this.clienteActual = cliente;
        this.marketplace = marketplace;
    }


    public void mostrarMenuMarketplace() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MARKETPLACE DE REVENTA ---");
            System.out.println("1. Publicar uno de mis tiquetes en venta.");
            System.out.println("2. Ver ofertas activas y hacer ContraOferta.");
            System.out.println("3. Gestionar mis ofertas publicadas (Aceptar CO).");
            System.out.println("4. Volver al menu de Cliente.");
            
            int opcion = InputUtils.leerEntero("Seleccione una opcion: ");
            
            try {
                switch (opcion) {
                    case 1:
                        publicarTiqueteEnMarketplace();
                        break;
                    case 2:
                        verOfertasYContraOfertar();
                        break; 
                    case 3:
                        gestionarMisOfertas();
                        break;
                    case 4:
                        salir = true;
                        break;
                    default:
                        System.err.println("Opcion no valida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.err.println("Ocurrio un error inesperado: " + e.getMessage());
            }
        }
    }


    private void publicarTiqueteEnMarketplace() {
        List<Tiquete> misTiquetes = clienteActual.getTiquetesComprados();
        
        if (misTiquetes.isEmpty()) {
            System.out.println("No tienes tiquetes comprados para poner en venta.");
            return;
        }

        System.out.println("\n--- Tiquetes Comprados (Disponibles para Venta) ---");
        
        for (int i = 0; i < misTiquetes.size(); i++) {
            Tiquete t = misTiquetes.get(i);
            String tipo = t instanceof PaqueteDeluxe ? " (DELUXE - NO VENDIBLE)" : "";
            System.out.printf("[%d] ID: %d | Evento: %s | Precio Original: %.2f%s\n",
                (i + 1), t.getId(), t.getEvento().getNombre(), t.getPrecio(), tipo);
        }
        
        int seleccion = InputUtils.leerEntero("Seleccione el numero de tiquete a vender (0 para cancelar): ");
        if (seleccion == 0) return;

        try {
            if (seleccion < 1 || seleccion > misTiquetes.size()) {
                throw new Exception("Seleccion fuera de rango.");
            }
            Tiquete tiqueteAVender = misTiquetes.get(seleccion - 1);
            
            double precioVenta = -1;
            while (precioVenta <= 0) {
                precioVenta = InputUtils.leerDouble("Ingrese el precio de venta deseado (debe ser mayor a 0): ");
                if (precioVenta <= 0) {
                    System.err.println("El precio debe ser un valor positivo.");
                }
            }

            marketplace.publicarOferta(tiqueteAVender, clienteActual, precioVenta);
            
            System.out.println("\nTiquete publicado con exito.");
            System.out.printf("  - ID Tiquete: %d\n", tiqueteAVender.getId());
            System.out.printf("  - Precio de Venta: %.2f\n", precioVenta);
            
        } catch (ExcepcionMarketplace e) {
            System.err.println("\nERROR DE NEGOCIO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nEntrada Invalida o Tiquete Invalido. Detalles: " + e.getMessage());
        }
    }
    

    private void verOfertasYContraOfertar() {
        Collection<Oferta> ofertas = marketplace.obtenerTodasLasOfertas();
        
        List<Oferta> ofertasDisponibles = ofertas.stream()
            .filter(o -> !o.getVendedor().equals(clienteActual))
            .collect(Collectors.toList());
        
        if (ofertasDisponibles.isEmpty()) {
            System.out.println("No hay ofertas de reventa activas para usted.");
            return;
        }

        System.out.println("\n--- OFERTAS DE REVENTA ACTIVAS ---");
        
        for (int i = 0; i < ofertasDisponibles.size(); i++) {
            Oferta o = ofertasDisponibles.get(i);
            Tiquete t = o.getTiquete();
            System.out.printf("[%d] ID Tiquete: %d | Evento: %s | Precio Solicitado: $%.2f | Vendedor: %s\n",
                (i + 1), t.getId(), t.getEvento().getNombre(), o.getPrecio(), o.getVendedor().getCorreo());
        }
        
        int seleccion = InputUtils.leerEntero("Seleccione el numero de la oferta para contraofertar (0 para cancelar): ");
        if (seleccion == 0) return;

        try {
            if (seleccion < 1 || seleccion > ofertasDisponibles.size()) {
                throw new Exception("Seleccion de oferta fuera de rango.");
            }
            Oferta ofertaSeleccionada = ofertasDisponibles.get(seleccion - 1);
            
            double montoContraOferta = -1;
            while (montoContraOferta <= 0) {
                montoContraOferta = InputUtils.leerDouble("Ingrese el monto de su contraoferta (debe ser mayor a 0): ");
                if (montoContraOferta <= 0) {
                    System.err.println("El monto debe ser un valor positivo.");
                }
            }

            String idTiquete = ofertaSeleccionada.getIdTiqueteStr();
            int idContraOferta = marketplace.hacerContraOferta(idTiquete, clienteActual, montoContraOferta);
            
            System.out.println("\nControferta registrada con exito.");
            System.out.printf("  - ID ContraOferta: %d\n", idContraOferta);
            System.out.printf("  - Monto Propuesto: $%.2f\n", montoContraOferta);
            
        } catch (ExcepcionMarketplace e) {
            System.err.println("\nERROR DE NEGOCIO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nEntrada Invalida. Detalles: " + e.getMessage());
        }
    }
    

    private void gestionarMisOfertas() {
        List<Oferta> misOfertas = marketplace.obtenerOfertasPorVendedor(clienteActual);
        
        if (misOfertas.isEmpty()) {
            System.out.println("No tienes tiquetes publicados en venta.");
            return;
        }

        System.out.println("\n--- TUS OFERTAS PUBLICADAS ---");
        
        for (int i = 0; i < misOfertas.size(); i++) {
            Oferta o = misOfertas.get(i);
            System.out.printf("[%d] ID Tiquete: %d | Evento: %s | Precio Base: $%.2f\n",
                (i + 1), o.getTiquete().getId(), o.getTiquete().getEvento().getNombre(), o.getPrecio());
        }
        
        int seleccionOferta = InputUtils.leerEntero("Seleccione el numero de la oferta a gestionar (0 para cancelar): ");
        if (seleccionOferta == 0) return;

        try {
            if (seleccionOferta < 1 || seleccionOferta > misOfertas.size()) {
                throw new Exception("Seleccion de oferta fuera de rango.");
            }
            Oferta ofertaSeleccionada = misOfertas.get(seleccionOferta - 1);
            
            List<ContraOferta> contraOfertasRecibidas = marketplace.obtenerContraOfertasParaOferta(ofertaSeleccionada);
            
            if (contraOfertasRecibidas.isEmpty()) {
                System.out.println("\nNo hay contraofertas pendientes para este tiquete.");
                return;
            }
            
            System.out.printf("\n--- CONTRAOFERTAS RECIBIDAS (Tiquete ID %d) ---\n", ofertaSeleccionada.getTiquete().getId());
            for (int i = 0; i < contraOfertasRecibidas.size(); i++) {
                ContraOferta co = contraOfertasRecibidas.get(i);
                System.out.printf("[%d] ID CO: %d | Comprador: %s | Monto: $%.2f\n",
                    (i + 1), co.getId(), co.getComprador().getCorreo(), co.getMonto());
            }

            int seleccionCO = InputUtils.leerEntero("Seleccione el numero de la CO para ACEPTAR (0 para cancelar): ");
            if (seleccionCO == 0) return;
            
            if (seleccionCO < 1 || seleccionCO > contraOfertasRecibidas.size()) {
                throw new Exception("Seleccion de contraoferta fuera de rango.");
            }
            ContraOferta coAceptada = contraOfertasRecibidas.get(seleccionCO - 1);

            marketplace.aceptarContraOferta(coAceptada.getId());

            System.out.println("\n TRANSACCION COMPLETADA CON ÉXITO.");
            System.out.printf("   - Venta: Tiquete ID %d\n", ofertaSeleccionada.getTiquete().getId());
            System.out.printf("   - Monto Total Recibido: $%.2f\n", coAceptada.getMonto());
            System.out.printf("   - Nuevo Dueño: %s\n", coAceptada.getComprador().getCorreo());
            
        } catch (ExcepcionMarketplace e) {
            System.err.println("\n ERROR DE NEGOCIO AL ACEPTAR: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n Entrada Inválida o Error al seleccionar: " + e.getMessage());
        }
    }
}