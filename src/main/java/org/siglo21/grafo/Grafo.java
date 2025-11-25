package org.siglo21.grafo;

import java.text.Normalizer;
import java.util.*;

/**
 * Representa la red de vuelos
 */
public class Grafo {

    private Map<Ciudad, List<Arista>> adyacencias;

    public Grafo() {
        this.adyacencias = new HashMap<>();
    }

    /**
     * Agrega ciudad al grafo
     */
    public void agregarCiudad(Ciudad ciudad) {
        if (!adyacencias.containsKey(ciudad)) {
            adyacencias.put(ciudad, new ArrayList<>());
        }
    }

    /**
     * Agrega una conexión entre dos ciudades
     */
    public void agregarArista(Ciudad origen, Ciudad destino, double tiempo, double precioBase, boolean esDirecto) {
        agregarCiudad(origen);
        agregarCiudad(destino);

        adyacencias.get(origen).add(new Arista(destino, tiempo, precioBase, esDirecto));
    }

    /**
     * Agrega una arista bidireccional
     * Los vuelos pueden ser de ida y vuelta
     */
    public void agregarAristaBidireccional(Ciudad ciudad1, Ciudad ciudad2, double tiempo, double precioBase) {
        agregarArista(ciudad1, ciudad2, tiempo, precioBase, false);
        agregarArista(ciudad2, ciudad1, tiempo, precioBase, false);
    }

    /**
     * Obtiene las ciudades adyacentes de una ciudad
     */
    public List<Arista> getAdyacentes(Ciudad ciudad) {
        return adyacencias.getOrDefault(ciudad, new ArrayList<>());
    }

    /**
     * Obtiene toda las ciudades del grafo
     */
    public Set<Ciudad> getCiudades() {
        return adyacencias.keySet();
    }

    /**
     * Obtiene una ciudad por nombre
     */
    public Ciudad getCiudadPorNombre(String nombre) {
        String nombreNormalizado = normalizarTexto(nombre);
        for (Ciudad ciudad : adyacencias.keySet()) {
            if (normalizarTexto(ciudad.getNombre()).equals(nombreNormalizado)) {
                return ciudad;
            }
        }
        return null;
    }

    /**
     * Normaliza un texto para compararlo sin tildes ni mayusculas
     */
    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

    /**
     * BFS desde una ciudad
     * Retorna una lista de ciudades alcanzables
     */
    public List<Ciudad> bfs(Ciudad origen) {
        List<Ciudad> resultado = new ArrayList<>();
        Set<Ciudad> visitados = new HashSet<>();
        Queue<Ciudad> cola = new LinkedList<>();

        visitados.add(origen);
        cola.offer(origen);

        while (!cola.isEmpty()) {
            Ciudad actual = cola.poll();
            resultado.add(actual);

            for (Arista arista : adyacencias.get(actual)) {
                Ciudad destino = arista.getDestino();
                if (!visitados.contains(destino)) {
                    visitados.add(destino);
                    cola.offer(destino);
                }
            }

        }

        return resultado;
    }

    /**
     * DFS desde una ciudad
     * Retorna una lista de ciudades alcanzables
     */
    public List<Ciudad> dfs(Ciudad origen) {
        List<Ciudad> resultado = new ArrayList<>();
        Set<Ciudad> visitados = new HashSet<>();
        dfsRecursivo(origen, visitados, resultado);
        return resultado;
    }

    private void dfsRecursivo(Ciudad ciudad, Set<Ciudad> visitados, List<Ciudad> resultado) {
        visitados.add(ciudad);
        resultado.add(ciudad);

        for (Arista arista : adyacencias.get(ciudad)) {
            Ciudad destino = arista.getDestino();
            if (!visitados.contains(destino)) {
                dfsRecursivo(destino, visitados, resultado);
            }
        }
    }

    /**
     * Detecta componentes conexos del grafo
     */
    public List<List<Ciudad>> detectarComponentes() {
        List<List<Ciudad>> componentes = new ArrayList<>();
        Set<Ciudad> visitados = new HashSet<>();

        for (Ciudad ciudad : getCiudades()) {
            if (!visitados.contains(ciudad)) {
                List<Ciudad> componente = dfs(ciudad);
                componentes.add(componente);
                visitados.addAll(componente);
            }
        }

        return componentes;
    }

    /**
     * Clase auxiliar para Dijkstra
     */
    private static class NodoDijkstra implements Comparable<NodoDijkstra> {
        Ciudad ciudad;
        double tiempo;
        double precio;
        NodoDijkstra anterior;
        Arista aristaAnterior;

        NodoDijkstra(Ciudad ciudad, double tiempo, double precio, NodoDijkstra anterior, Arista aristaAnterior) {
            this.ciudad = ciudad;
            this.tiempo = tiempo;
            this.precio = precio;
            this.anterior = anterior;
            this.aristaAnterior = aristaAnterior;
        }

        @Override
        public int compareTo(NodoDijkstra otro) {
            // Comparar primero por tiempo
            int cmp = Double.compare(this.tiempo, otro.tiempo);
            if (cmp != 0)
                return cmp;
            // Si hay empate, comparar por precio
            return Double.compare(this.precio, otro.precio);
        }
    }

    /**
     * Dijkstra: encuentra la ruta mínima en tiempo desde origen a destino
     * Si hay empate en tiempo, minimiza precio
     * Retorna el itinerario (lista de ciudades) y la información de las aristas
     */
    public Itinerario dijkstra(Ciudad origen, Ciudad destino) {
        Map<Ciudad, NodoDijkstra> distancias = new HashMap<>();
        PriorityQueue<NodoDijkstra> cola = new PriorityQueue<>();
        Set<Ciudad> visitados = new HashSet<>();

        // Inicializar
        for (Ciudad ciudad : getCiudades()) {
            distancias.put(ciudad, new NodoDijkstra(ciudad, Double.MAX_VALUE, Double.MAX_VALUE, null, null));
        }

        NodoDijkstra nodoOrigen = new NodoDijkstra(origen, 0, 0, null, null);
        distancias.put(origen, nodoOrigen);
        cola.offer(nodoOrigen);

        while (!cola.isEmpty()) {
            NodoDijkstra actual = cola.poll();

            if (visitados.contains(actual.ciudad))
                continue;
            visitados.add(actual.ciudad);

            if (actual.ciudad.equals(destino)) {
                // Reconstruir camino
                return reconstruirItinerario(actual);
            }

            for (Arista arista : getAdyacentes(actual.ciudad)) {
                Ciudad vecino = arista.getDestino();

                if (visitados.contains(vecino))
                    continue;

                double nuevoTiempo = actual.tiempo + arista.getTiempo();
                double nuevoPrecio = actual.precio + arista.getPrecioBase();

                NodoDijkstra nodoVecino = distancias.get(vecino);

                // Comparar: primero tiempo, luego precio
                if (nuevoTiempo < nodoVecino.tiempo ||
                        (nuevoTiempo == nodoVecino.tiempo && nuevoPrecio < nodoVecino.precio)) {

                    NodoDijkstra nuevoNodo = new NodoDijkstra(vecino, nuevoTiempo, nuevoPrecio, actual, arista);
                    distancias.put(vecino, nuevoNodo);
                    cola.offer(nuevoNodo);
                }
            }
        }

        return null; // No hay camino
    }

    /**
     * Reconstruye el itinerario desde el nodo destino
     */
    private Itinerario reconstruirItinerario(NodoDijkstra destino) {
        List<Ciudad> ciudades = new ArrayList<>();
        List<Arista> aristas = new ArrayList<>();

        NodoDijkstra actual = destino;
        while (actual != null) {
            ciudades.add(0, actual.ciudad);
            if (actual.aristaAnterior != null) {
                aristas.add(0, actual.aristaAnterior);
            }
            actual = actual.anterior;
        }

        return new Itinerario(ciudades, aristas, destino.tiempo, destino.precio);
    }


}
