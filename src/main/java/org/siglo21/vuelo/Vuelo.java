package org.siglo21.vuelo;

import org.siglo21.avion.Avion;
import org.siglo21.estructuras.ArbolAVL;
import org.siglo21.grafo.Ciudad;

/**
 * Representa un vuelo con su avión y reservas
 */
public class Vuelo {
    private String codigoVuelo;
    private Ciudad origen;
    private Ciudad destino;
    private Avion avion;
    private ArbolAVL reservas;
    private double precioBase;
    private double tiempo;
    private boolean esDirecto;
    private static int contadorVuelos = 1;

    public Vuelo(Ciudad origen, Ciudad destino, double precioBase, double tiempo, boolean esDirecto) {
        this.codigoVuelo = "VUELO-" + String.format("%04d", contadorVuelos++);
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.tiempo = tiempo;
        this.esDirecto = esDirecto;
        this.avion = new Avion();
        this.reservas = new ArbolAVL();
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public Ciudad getOrigen() {
        return origen;
    }

    public Ciudad getDestino() {
        return destino;
    }

    public Avion getAvion() {
        return avion;
    }

    public ArbolAVL getReservas() {
        return reservas;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public double getTiempo() {
        return tiempo;
    }

    public boolean esDirecto() {
        return esDirecto;
    }

    /**
     * Obtiene el porcentaje de ocupación del vuelo
     */
    public double getPorcentajeOcupacion() {
        return avion.getPorcentajeDeOcupacion();
    }

    /**
     * Verifica si el vuelo alcanza ≥95% de ocupación
     */
    public boolean alcanza95Porciento() {
        return getPorcentajeOcupacion() >= 95.0;
    }

    @Override
    public String toString() {
        return String.format("%s: %s → %s (Tiempo: %.1fh, Precio: $%.0f)",
                codigoVuelo, origen, destino, tiempo, precioBase);
    }
}


