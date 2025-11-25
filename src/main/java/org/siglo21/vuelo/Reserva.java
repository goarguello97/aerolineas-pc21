package org.siglo21.vuelo;

import org.siglo21.grafo.Ciudad;

/**
 * Reserva del pasaje
 */
public class Reserva {

    private String codigoReserva;
    private String codigoVuelo;
    private String asiento;
    private Ciudad origen;
    private Ciudad destino;
    private double precioFinal;

    public Reserva(String codigoReserva, String codigoVuelo, String asiento, Ciudad origen, Ciudad destino, double precioFinal) {
        this.codigoReserva = codigoReserva;
        this.codigoVuelo = codigoVuelo;
        this.asiento = asiento;
        this.origen = origen;
        this.destino = destino;
        this.precioFinal = precioFinal;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public String getAsiento() {
        return asiento;
    }

    public Ciudad getOrigen() {
        return origen;
    }

    public Ciudad getDestino() {
        return destino;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    @Override
    public String toString() {
        return String.format("Reserva %s - Vuelo: %s - Asiento: %s - %s → %s - Precio: $%.2f",
                codigoReserva, codigoVuelo, asiento, origen, destino, precioFinal);
    }
}
