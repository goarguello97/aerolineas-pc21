package org.siglo21.app;

import org.siglo21.avion.Asiento;
import org.siglo21.grafo.Arista;
import org.siglo21.grafo.Ciudad;
import org.siglo21.grafo.Grafo;
import org.siglo21.grafo.Itinerario;
import org.siglo21.vuelo.Reserva;
import org.siglo21.vuelo.Vuelo;

import java.util.*;

/**
 * Sistema principal de gestión de la aerolínea
 */
public class SistemaAerolinea {
    private Grafo grafo;
    private Map<String, Vuelo> vuelos; // Código de vuelo -> Vuelo
    private Map<String, List<Vuelo>> vuelosPorTramo; // "Origen-Destino" -> Lista de vuelos
    private static int contadorReservas = 1;

    public SistemaAerolinea() {
        this.grafo = new Grafo();
        this.vuelos = new HashMap<>();
        this.vuelosPorTramo = new HashMap<>();
        inicializarDatos();
    }

    /**
     * Inicializa los datos del problema
     */
    private void inicializarDatos() {
        // Crear ciudades
        Ciudad buenosAires = new Ciudad("Buenos Aires");
        Ciudad cordoba = new Ciudad("Córdoba");
        Ciudad mendoza = new Ciudad("Mendoza");
        Ciudad bariloche = new Ciudad("Bariloche");
        Ciudad santaFe = new Ciudad("Santa Fe");
        Ciudad posadas = new Ciudad("Posadas");
        Ciudad santaCruz = new Ciudad("Santa Cruz");

        // Agregar ciudades al grafo
        grafo.agregarCiudad(buenosAires);
        grafo.agregarCiudad(cordoba);
        grafo.agregarCiudad(mendoza);
        grafo.agregarCiudad(bariloche);
        grafo.agregarCiudad(santaFe);
        grafo.agregarCiudad(posadas);
        grafo.agregarCiudad(santaCruz);

        // Vuelos directos desde Buenos Aires
        grafo.agregarArista(buenosAires, cordoba, 1.2, 120000, true);
        grafo.agregarArista(buenosAires, mendoza, 1.7, 150000, true);
        grafo.agregarArista(buenosAires, bariloche, 2.2, 220000, true);
        grafo.agregarArista(buenosAires, santaFe, 1.0, 100000, true);
        grafo.agregarArista(buenosAires, posadas, 1.5, 140000, true);

        // Otras conexiones (bidireccionales)
        grafo.agregarAristaBidireccional(cordoba, mendoza, 1.1, 90000);
        grafo.agregarAristaBidireccional(cordoba, santaFe, 0.8, 70000);
        grafo.agregarAristaBidireccional(mendoza, bariloche, 1.6, 120000);
        grafo.agregarAristaBidireccional(bariloche, santaCruz, 2.0, 160000);
        grafo.agregarAristaBidireccional(mendoza, santaCruz, 2.6, 170000);
        grafo.agregarAristaBidireccional(santaFe, posadas, 1.2, 80000);
    }

    /**
     * Obtiene el grafo
     */
    public Grafo getGrafo() {
        return grafo;
    }

    /**
     * Verifica si ya existe un vuelo registrado para un tramo
     */
    public boolean existeTramoRegistrado(Ciudad origen, Ciudad destino) {
        String claveTramo = origen.getNombre() + "-" + destino.getNombre();
        List<Vuelo> vuelosTramo = vuelosPorTramo.get(claveTramo);
        return vuelosTramo != null && !vuelosTramo.isEmpty();
    }

    /**
     * Verifica si toda una ruta ya cuenta con vuelos dados de alta
     */
    public boolean rutaYaRegistrada(List<Ciudad> ciudadesRuta) {
        if (ciudadesRuta == null || ciudadesRuta.size() < 2) {
            return false;
        }
        for (int i = 0; i < ciudadesRuta.size() - 1; i++) {
            Ciudad origen = ciudadesRuta.get(i);
            Ciudad destino = ciudadesRuta.get(i + 1);
            if (!existeTramoRegistrado(origen, destino)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Crea un nuevo vuelo para un tramo
     */
    public Vuelo crearVuelo(Ciudad origen, Ciudad destino, double precioBase, double tiempo, boolean esDirecto) {
        Vuelo vuelo = new Vuelo(origen, destino, precioBase, tiempo, esDirecto);
        vuelos.put(vuelo.getCodigoVuelo(), vuelo);

        // Agregar a vuelos por tramo
        String claveTramo = origen.getNombre() + "-" + destino.getNombre();
        vuelosPorTramo.computeIfAbsent(claveTramo, k -> new ArrayList<>()).add(vuelo);

        return vuelo;
    }

    /**
     * Obtiene o crea un vuelo para un tramo
     */
    public Vuelo obtenerOCrearVuelo(Ciudad origen, Ciudad destino, double precioBase, double tiempo,
                                    boolean esDirecto) {
        String claveTramo = origen.getNombre() + "-" + destino.getNombre();
        List<Vuelo> vuelosTramo = vuelosPorTramo.get(claveTramo);

        if (vuelosTramo != null && !vuelosTramo.isEmpty()) {
            // Retornar el primer vuelo disponible (podría mejorarse con lógica de
            // selección)
            return vuelosTramo.get(0);
        }

        // Crear nuevo vuelo
        return crearVuelo(origen, destino, precioBase, tiempo, esDirecto);
    }

    /**
     * Calcula el precio final de un itinerario
     */
    public double calcularPrecioFinal(Itinerario itinerario, List<Vuelo> vuelosItinerario) {
        double precioTotal = 0.0;

        for (int i = 0; i < vuelosItinerario.size(); i++) {
            Vuelo vuelo = vuelosItinerario.get(i);
            double precioTramo = vuelo.getPrecioBase();

            // +10% si el vuelo alcanza ≥95% de ocupación
            if (vuelo.alcanza95Porciento()) {
                precioTramo *= 1.10;
            }

            precioTotal += precioTramo;
        }

        // +20% si el itinerario es directo (un solo tramo)
        if (itinerario.esDirecto()) {
            precioTotal *= 1.20;
        }

        return precioTotal;
    }

    /**
     * Realiza una reserva de pasaje
     */
    public List<Reserva> realizarReserva(Ciudad origen, Ciudad destino) {
        // Calcular ruta mínima
        Itinerario itinerario = grafo.dijkstra(origen, destino);

        if (itinerario == null) {
            return null; // No hay ruta disponible
        }

        // Obtener o crear vuelos para cada tramo
        List<Vuelo> vuelosItinerario = new ArrayList<>();
        List<Arista> aristas = itinerario.getAristas();
        List<Ciudad> ciudades = itinerario.getCiudades();

        for (int i = 0; i < aristas.size(); i++) {
            Arista arista = aristas.get(i);
            Ciudad origenTramo = ciudades.get(i);
            Ciudad destinoTramo = ciudades.get(i + 1);

            Vuelo vuelo = obtenerOCrearVuelo(origenTramo, destinoTramo,
                    arista.getPrecioBase(), arista.getTiempo(), arista.isEsDirecto());
            vuelosItinerario.add(vuelo);
        }

        // Asignar asientos y crear reservas
        List<Reserva> reservas = new ArrayList<>();

        // Primero asignar todos los asientos y calcular precios
        for (Vuelo vuelo : vuelosItinerario) {
            // Verificar ocupación ANTES de asignar para calcular recargo
            double porcentajeOcupacionAntes = vuelo.getPorcentajeOcupacion();
            boolean alcanza95Antes = porcentajeOcupacionAntes >= 95.0;

            Asiento asiento = vuelo.getAvion().asignarAsiento();

            if (asiento == null) {
                // No hay asientos disponibles, cancelar todas las reservas
                for (Reserva reserva : reservas) {
                    cancelarReserva(reserva.getCodigoReserva());
                }
                return null;
            }

            // Calcular precio del tramo (con recargo de ocupación si aplica)
            double precioTramo = vuelo.getPrecioBase();
            if (alcanza95Antes) {
                precioTramo *= 1.10; // +10% por ocupación ≥95% (antes de asignar)
            }
            String codigoReserva = "RES-" + String.format("%06d", contadorReservas++);
            Reserva reserva = new Reserva(codigoReserva, vuelo.getCodigoVuelo(),
                    asiento.toString(), vuelo.getOrigen(), vuelo.getDestino(), precioTramo);

            // Insertar en el árbol AVL del vuelo
            vuelo.getReservas().insertar(codigoReserva, reserva);
            reservas.add(reserva);
        }

        // Aplicar +20% si el itinerario es directo (solo un tramo)
        if (itinerario.esDirecto() && !reservas.isEmpty()) {
            double precioAnterior = reservas.get(0).getPrecioFinal();
            reservas.get(0).setPrecioFinal(precioAnterior * 1.20);
        }

        return reservas;
    }

    /**
     * Cancela una reserva
     */
    public boolean cancelarReserva(String codigoReserva) {
        for (Vuelo vuelo : vuelos.values()) {
            Reserva reserva = vuelo.getReservas().buscar(codigoReserva);
            if (reserva != null) {
                // Liberar asiento
                vuelo.getAvion().liberarAsiento(reserva.getAsiento());
                // Eliminar del árbol AVL
                vuelo.getReservas().eliminar(codigoReserva);
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene un vuelo por código
     */
    public Vuelo getVuelo(String codigoVuelo) {
        if (codigoVuelo == null) {
            return null;
        }
        return vuelos.get(codigoVuelo.trim().toUpperCase());
    }

    /**
     * Obtiene todos los vuelos
     */
    public Collection<Vuelo> getAllVuelos() {
        return vuelos.values();
    }
}

