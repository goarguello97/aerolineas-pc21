package org.siglo21.grafo;

import java.util.List;

/**
 * Representa un itinerario(ruta) entre ciudades
 */
public class Itinerario {
    private List<Ciudad> ciudades;
    private List<Arista> aristas;
    private double tiempoTotal;
    private double precioBaseTotal;

    public Itinerario(List<Ciudad> ciudades, List<Arista> aristas, double tiempoTotal, double precioBaseTotal) {
        this.ciudades = ciudades;
        this.aristas = aristas;
        this.tiempoTotal = tiempoTotal;
        this.precioBaseTotal = precioBaseTotal;
    }

    public List<Ciudad> getCiudades() {
        return ciudades;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public double getTiempoTotal() {
        return tiempoTotal;
    }

    public double getPrecioBaseTotal() {
        return precioBaseTotal;
    }

    /**
     * Verifica si el itinerario es directo (un solo tramo)
     */
    public boolean esDirecto() {
        return aristas.size() == 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciudades.size() - 1; i++) {
            sb.append(ciudades.get(i));
            if (i < aristas.size()) {
                sb.append(" ").append(aristas.get(i));
            }
            sb.append(" ");
        }
        sb.append(ciudades.get(ciudades.size() - 1));
        return sb.toString();
    }
}
