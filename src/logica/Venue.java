package logica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venue {
    private String nombre;
    private String ubicacion;
    private int capacidadMaxima;
    private boolean aprobado;
    private List<Evento> eventosProgramados = new ArrayList<>();

    public Venue(String nombre, String ubicacion, int capacidadMaxima) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidadMaxima = capacidadMaxima;
        this.aprobado = false;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean estaAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
    
    public void agregarEvento(Evento evento) {
        if (evento != null && !eventosProgramados.contains(evento)) {
            eventosProgramados.add(evento);
        }
    }

    public boolean estaDisponible(LocalDateTime fecha) {
        for (Evento e : eventosProgramados) {
            if (e.getFechaHora().toLocalDate().equals(fecha.toLocalDate())) {
                return false;
            }
        }
        return true;
    }
}



