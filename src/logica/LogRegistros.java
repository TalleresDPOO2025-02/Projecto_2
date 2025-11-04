package logica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogRegistros {
    private final List<String> registros = new ArrayList<>();
    
    public void registrarEvento(String tipo, String usuario, String detalle) {
        String registro = String.format("[%s] - %s - Usuario: %s - Detalle: %s", 
            LocalDateTime.now(), tipo, usuario, detalle);
        registros.add(registro);
    }
    
    public String obtenerLogCompleto() {
        return registros.toString();
    }
}	