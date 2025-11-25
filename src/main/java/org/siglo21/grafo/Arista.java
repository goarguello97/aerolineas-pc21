package org.siglo21.grafo;

/**
 * Representamos una arista(la conexiòn) entre dos ciudades
 */
public class Arista {

    private Ciudad destino;
    private double tiempo;
    private double precioBase;
    private boolean esDirecto;

    public Arista(Ciudad destino, double tiempo, double precioBase, boolean esDirecto) {
        this.destino = destino;
        this.tiempo = tiempo;
        this.precioBase = precioBase;
        this.esDirecto = esDirecto;
    }

    public Ciudad getDestino() {
        return destino;
    }

    public double getTiempo() {
        return tiempo;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean isEsDirecto() {
        return esDirecto;
    }

    @Override
    public String toString() {
        return "-> " + destino + " (Tiempo: " + tiempo + "h, Precio: " + precioBase + ")";
    }
}
