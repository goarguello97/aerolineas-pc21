package org.siglo21.avion;

import java.util.*;

public class Avion {

    private static final int ASIENTOS_SECCION = 10;
    private static final String[] SECCIONES = {"A", "B", "C"};

    private Map<String, List<Asiento>> asientos;
    private int[] ocupacionPorSeccion;

    public Avion() {
        this.asientos = new HashMap<>();
        this.ocupacionPorSeccion = new int[3];

        for (String seccion : SECCIONES) {
            List<Asiento> listaAsientos = new ArrayList<>();
            for (int i = 1; i <= ASIENTOS_SECCION; i++) {
                listaAsientos.add(new Asiento(seccion, i));
            }
            asientos.put(seccion, listaAsientos);
        }
    }

    /**
     * Asigna un asiento aleatorio manteniendo el balance entre las secciones
     */
    public Asiento asignarAsiento() {
        // Buscamos la seccion con menor ocupación
        List<String> seccionesMinimas = new ArrayList<>();
        int minOcupacion = Integer.MAX_VALUE;

        for (int i = 0; i < SECCIONES.length; i++) {
            int ocupacion = ocupacionPorSeccion[i];
            if (ocupacion < minOcupacion) {
                minOcupacion = ocupacion;
                seccionesMinimas.clear();
                seccionesMinimas.add(SECCIONES[i]);
            } else if (ocupacion == minOcupacion) {
                seccionesMinimas.add(SECCIONES[i]);
            }
        }

        // Si hay empate elegimos una al azar
        Random random = new Random();
        String seccionElegida = seccionesMinimas.get(random.nextInt(seccionesMinimas.size()));

        // Obtenemos los asientos libres de la seccion elegida
        List<Asiento> asientosLibres = new ArrayList<>();
        for (Asiento asiento : asientos.get(seccionElegida)) {
            if (!asiento.isOcupado()) {
                asientosLibres.add(asiento);
            }
        }

        if (asientosLibres.isEmpty()) {
            return null; // Esta lleno
        }

        // Elegimos un asiento aleatorio entre los libres
        Asiento asientoElegido = asientosLibres.get(random.nextInt(asientosLibres.size()));
        asientoElegido.setOcupado(true);

        // Acutalizamos el contador de la ocupación
        int indiceSeccion = Arrays.asList(SECCIONES).indexOf(seccionElegida);
        ocupacionPorSeccion[indiceSeccion]++;

        return asientoElegido;
    }

    /**
     * Liberar un asiento
     */
    public void liberarAsiento(String etiqueta) {
        String seccion = etiqueta.substring(0, 1);
        int numero = Integer.parseInt(etiqueta.substring(1));

        List<Asiento> listaAsientos = asientos.get(seccion);
        if (listaAsientos != null && numero >= 1 && numero <= ASIENTOS_SECCION) {
            Asiento asiento = listaAsientos.get(numero - 1);
            if (asiento.isOcupado()) {
                asiento.setOcupado(false);
                int indiceSeccion = Arrays.asList(SECCIONES).indexOf(seccion);
                ocupacionPorSeccion[indiceSeccion]--;
            }
        }
    }

    /**
     * Obtener el procentaje de ocupacion
     */

    public double getPorcentajeDeOcupacion() {
        int totalOcupados = 0;
        for (int ocupacion : ocupacionPorSeccion) {
            totalOcupados += ocupacion;
        }
        return (totalOcupados * 100) / (SECCIONES.length * ASIENTOS_SECCION);
    }

    /**
     * Obtener ocypación por seccion
     */
    public Map<String, Integer> getOcupacionPorSeccion() {
        Map<String, Integer> resultado = new HashMap<>();
        for (int i = 0; i < SECCIONES.length; i++) {
            resultado.put(SECCIONES[i], ocupacionPorSeccion[i]);
        }
        return resultado;
    }

    /**
     * Obtener el total de asientos ocupados
     */
    public int getTotalOcupados() {
        int totalOcupados = 0;
        for (int ocupacion : ocupacionPorSeccion) {
            totalOcupados += ocupacion;
        }
        return totalOcupados;
    }

    /**
     * Obtiene el total de asientos
     */
    public int getTotalAsientos() {
        return SECCIONES.length * ASIENTOS_SECCION;
    }

    /**
     * Verifica si un asiento esta ocupado
     */
    public boolean isAsientoOcupado(String etiqueta) {
        String seccion = etiqueta.substring(0, 1);
        int numero = Integer.parseInt(etiqueta.substring(1));

        List<Asiento> listaAsientos = asientos.get(seccion);
        if (listaAsientos != null && numero >= 1 && numero <= ASIENTOS_SECCION) {
            return listaAsientos.get(numero - 1).isOcupado();
        }
        return false;
    }
}
