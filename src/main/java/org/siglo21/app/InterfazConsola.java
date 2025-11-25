package org.siglo21.app;

import org.siglo21.grafo.Arista;
import org.siglo21.grafo.Ciudad;
import org.siglo21.grafo.Itinerario;
import org.siglo21.vuelo.Reserva;
import org.siglo21.vuelo.Vuelo;

import java.util.*;

/**
 * Interfaz de consola para el sistema de aerolínea
 */
public class InterfazConsola {
    private SistemaAerolinea sistema;
    private Scanner scanner;

    public InterfazConsola() {
        this.sistema = new SistemaAerolinea();
        this.scanner = new Scanner(System.in);
    }

    public void ejecutar() {
        System.out.println("=== SISTEMA DE GESTIÓN AEROLÍNEA PC21 ===\n");

        while (true) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    altaVuelo();
                    break;
                case 2:
                    consultarRuta();
                    break;
                case 3:
                    reservarPasaje();
                    break;
                case 4:
                    consultarOcupacion();
                    break;
                case 5:
                    listarVuelos();
                    break;
                case 6:
                    recorridosGrafo();
                    break;
                case 7:
                    System.out.println("¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }

            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void mostrarMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Alta de vuelo");
        System.out.println("2. Consultar ruta (origen-destino)");
        System.out.println("3. Reservar pasaje");
        System.out.println("4. Consultar ocupación por vuelo");
        System.out.println("5. Listar todos los vuelos");
        System.out.println("6. Recorridos del grafo (BFS/DFS/Componentes)");
        System.out.println("7. Salir");
    }

    private void altaVuelo() {
        System.out.println("\n--- ALTA DE VUELO ---");

        Ciudad origen = leerCiudad("Origen: ");
        if (origen == null)
            return;

        Ciudad destino = leerCiudad("Destino: ");
        if (destino == null)
            return;

        // Intentar obtener la ruta mínima (permite múltiples tramos)
        Itinerario itinerario = sistema.getGrafo().dijkstra(origen, destino);
        if (itinerario == null) {
            System.out.println("No existe ruta entre " + origen + " y " + destino);
            return;
        }

        List<Ciudad> ciudadesRuta = itinerario.getCiudades();

        if (sistema.rutaYaRegistrada(ciudadesRuta)) {
            System.out.println("La ruta " + origen + " → " + destino + " ya se encuentra dada de alta.");
            System.out.println("No es posible crear nuevos vuelos para este itinerario hasta completar su operación.");
            return;
        }

        List<Arista> tramos = itinerario.getAristas();
        List<Vuelo> vuelosCreados = new ArrayList<>();

        for (int i = 0; i < tramos.size(); i++) {
            Arista tramo = tramos.get(i);
            Ciudad origenTramo = ciudadesRuta.get(i);
            Ciudad destinoTramo = ciudadesRuta.get(i + 1);

            Vuelo vuelo = sistema.crearVuelo(
                    origenTramo,
                    destinoTramo,
                    tramo.getPrecioBase(),
                    tramo.getTiempo(),
                    tramo.isEsDirecto());
            vuelosCreados.add(vuelo);
        }

        if (vuelosCreados.size() == 1) {
            System.out.println("✓ Vuelo directo creado: " + vuelosCreados.get(0));
        } else {
            System.out.println("✓ No existe vuelo directo, se crearon " + vuelosCreados.size() + " tramos:");
            for (Vuelo v : vuelosCreados) {
                System.out.println("   - " + v);
            }
        }
    }

    private void consultarRuta() {
        System.out.println("\n--- CONSULTAR RUTA ---");

        Ciudad origen = leerCiudad("Origen: ");
        if (origen == null)
            return;

        Ciudad destino = leerCiudad("Destino: ");
        if (destino == null)
            return;

        Itinerario itinerario = sistema.getGrafo().dijkstra(origen, destino);

        if (itinerario == null) {
            System.out.println("No existe ruta entre " + origen + " y " + destino);
            return;
        }

        System.out.println("\n--- RUTA MÍNIMA ---");
        System.out.println("Ruta: " + itinerario);
        System.out.println("Tiempo total: " + String.format("%.2f", itinerario.getTiempoTotal()) + " horas");
        System.out.println("Precio base total: $" + String.format("%.2f", itinerario.getPrecioBaseTotal()));
        System.out.println("Es directo: " + (itinerario.esDirecto() ? "Sí" : "No"));

        // Mostrar detalle de tramos
        System.out.println("\nDetalle de tramos:");
        List<Ciudad> ciudades = itinerario.getCiudades();
        List<Arista> aristas = itinerario.getAristas();
        for (int i = 0; i < aristas.size(); i++) {
            Arista arista = aristas.get(i);
            System.out.println("  " + ciudades.get(i) + " → " + ciudades.get(i + 1) +
                    " | Tiempo: " + arista.getTiempo() + "h | Precio: $" + arista.getPrecioBase());
        }
    }

    private void reservarPasaje() {
        System.out.println("\n--- RESERVAR PASAJE ---");

        Ciudad origen = leerCiudad("Origen: ");
        if (origen == null)
            return;

        Ciudad destino = leerCiudad("Destino: ");
        if (destino == null)
            return;

        List<Reserva> reservas = sistema.realizarReserva(origen, destino);

        if (reservas == null || reservas.isEmpty()) {
            System.out.println("No se pudo realizar la reserva. No hay ruta disponible o no hay asientos libres.");
            return;
        }

        // Calcular precio total
        double precioTotal = 0.0;
        for (Reserva reserva : reservas) {
            precioTotal += reserva.getPrecioFinal();
        }

        System.out.println("\n=== COMPROBANTE DE RESERVA ===");
        System.out.println("Itinerario: " + origen + " → " + destino);
        System.out.println("\nReservas:");
        for (Reserva reserva : reservas) {
            System.out.println("  " + reserva);
        }
        System.out.println("\nPRECIO FINAL TOTAL: $" + String.format("%.2f", precioTotal));

        // Mostrar recargos aplicados
        System.out.println("\nDetalle de recargos:");
        for (Reserva reserva : reservas) {
            Vuelo vuelo = sistema.getVuelo(reserva.getCodigoVuelo());
            if (vuelo != null) {
                System.out.println("  Vuelo " + vuelo.getCodigoVuelo() + ":");
                System.out.println("    Ocupación: " + String.format("%.2f", vuelo.getPorcentajeOcupacion()) + "%");
                if (vuelo.alcanza95Porciento()) {
                    System.out.println("    ✓ Recargo +10% por ocupación ≥95%");
                }
            }
        }
        if (reservas.size() == 1) {
            System.out.println("  ✓ Recargo +20% por vuelo directo");
        }
    }

    private void consultarOcupacion() {
        System.out.println("\n--- CONSULTAR OCUPACIÓN ---");

        String codigoVuelo = leerString("Código de vuelo: ");
        Vuelo vuelo = sistema.getVuelo(codigoVuelo);

        if (vuelo == null) {
            System.out.println("Vuelo no encontrado.");
            return;
        }

        System.out.println("\n--- INFORMACIÓN DEL VUELO ---");
        System.out.println(vuelo);
        System.out.println("Ocupación global: " + String.format("%.2f", vuelo.getPorcentajeOcupacion()) + "%");
        System.out.println("Asientos ocupados: " + vuelo.getAvion().getTotalOcupados() + " / "
                + vuelo.getAvion().getTotalAsientos());

        System.out.println("\nOcupación por sección:");
        Map<String, Integer> ocupacionSecciones = vuelo.getAvion().getOcupacionPorSeccion();
        for (Map.Entry<String, Integer> entry : ocupacionSecciones.entrySet()) {
            System.out.println("  Sección " + entry.getKey() + ": " + entry.getValue() + " / 10");
        }

        System.out.println("\n--- RESERVAS (InOrder del AVL) ---");
        List<Reserva> reservas = vuelo.getReservas().inOrder();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas.");
        } else {
            for (Reserva reserva : reservas) {
                System.out.println("  " + reserva);
            }
        }
    }

    private void listarVuelos() {
        System.out.println("\n--- LISTADO DE VUELOS ---");
        Collection<Vuelo> vuelos = sistema.getAllVuelos();

        if (vuelos.isEmpty()) {
            System.out.println("No hay vuelos registrados.");
        } else {
            for (Vuelo vuelo : vuelos) {
                System.out.println(vuelo + " | Ocupación: " +
                        String.format("%.2f", vuelo.getPorcentajeOcupacion()) + "%");
            }
        }
    }

    private void recorridosGrafo() {
        System.out.println("\n--- RECORRIDOS DEL GRAFO ---");
        System.out.println("1. BFS desde una ciudad");
        System.out.println("2. DFS desde una ciudad");
        System.out.println("3. Detectar componentes conexas");

        int opcion = leerEntero("Seleccione: ");

        switch (opcion) {
            case 1:
                Ciudad origenBFS = leerCiudad("Ciudad origen: ");
                if (origenBFS != null) {
                    List<Ciudad> alcanzables = sistema.getGrafo().bfs(origenBFS);
                    System.out.println("Ciudades alcanzables (BFS): " + alcanzables);
                }
                break;
            case 2:
                Ciudad origenDFS = leerCiudad("Ciudad origen: ");
                if (origenDFS != null) {
                    List<Ciudad> alcanzables = sistema.getGrafo().dfs(origenDFS);
                    System.out.println("Ciudades alcanzables (DFS): " + alcanzables);
                }
                break;
            case 3:
                List<List<Ciudad>> componentes = sistema.getGrafo().detectarComponentes();
                System.out.println("Componentes conexas: " + componentes.size());
                for (int i = 0; i < componentes.size(); i++) {
                    System.out.println("  Componente " + (i + 1) + ": " + componentes.get(i));
                }
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private Ciudad leerCiudad(String mensaje) {
        System.out.print(mensaje);
        String nombre = scanner.nextLine().trim();
        Ciudad ciudad = sistema.getGrafo().getCiudadPorNombre(nombre);

        if (ciudad == null) {
            System.out.println("Ciudad no encontrada. Ciudades disponibles:");
            for (Ciudad c : sistema.getGrafo().getCiudades()) {
                System.out.println("  - " + c.getNombre());
            }
            return null;
        }

        return ciudad;
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String leerString(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}

