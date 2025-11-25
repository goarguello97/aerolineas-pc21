package org.siglo21.estructuras;


import org.siglo21.vuelo.Reserva;

/**
 * Árbol AVL para las reservas
 */
public class ArbolAVL {

    private NodoAVL raiz;

    /**
     * Retorna la altura de un nodo
     */
    private int altura(NodoAVL nodo) {
        return nodo == null ? 0 : nodo.getAltura();
    }

    /**
     * Retorna el factor de balance de un nodo
     */
    private int factorDeBalance(NodoAVL nodo) {
        return nodo == null ? 0 : altura(nodo.getIzquierdo()) - altura(nodo.getDerecho());
    }

    /**
     * Actualiza la altura de un nodo
     */
    private void actualizarAltura(NodoAVL nodo) {
        if (nodo != null) {
            nodo.setAltura(1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
        }
    }

    /**
     * Rotación simple a la derecha
     */
    private NodoAVL rotarDerecha(NodoAVL nodoY) {
        NodoAVL nodoX = nodoY.getIzquierdo();
        NodoAVL nodoZ = nodoX.getDerecho();

        nodoX.setDerecho(nodoY);
        nodoY.setIzquierdo(nodoZ);

        actualizarAltura(nodoY);
        actualizarAltura(nodoX);

        return nodoX;
    }

    /**
     * Rotación simple a la izquierda
     */
    private NodoAVL rotarIzquierda(NodoAVL nodoX) {
        NodoAVL nodoY = nodoX.getDerecho();
        NodoAVL nodoZ = nodoY.getIzquierdo();

        nodoY.setIzquierdo(nodoX);
        nodoX.setDerecho(nodoZ);

        actualizarAltura(nodoX);
        actualizarAltura(nodoY);

        return nodoY;
    }

    /**
     * Insertar reserva
     * Utiliza el metodo recursivo
     */
    public void insertar(String clave, Reserva reserva) {
        raiz = insertarRecursivo(raiz, clave, reserva);
    }

    private NodoAVL insertarRecursivo(NodoAVL nodo, String clave, Reserva reserva) {

        if (nodo == null) {
            return new NodoAVL(clave, reserva);
        }

        int comparacion = clave.compareTo(nodo.getClave());

        if (comparacion < 0) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), clave, reserva));
        } else if (comparacion > 0) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), clave, reserva));
        } else {
            nodo.setReserva(reserva);
            return nodo;
        }

        actualizarAltura(nodo);

        int balance = factorDeBalance(nodo);

        // Rotación izquierda-izquierda
        if (balance > 1 && clave.compareTo(nodo.getIzquierdo().getClave()) < 0) {
            return rotarDerecha(nodo);
        }

        // Rotación derecha-derecha
        if (balance < -1 && clave.compareTo(nodo.getDerecho().getClave()) > 0) {
            return rotarIzquierda(nodo);
        }

        // Rotación izquierda-derecha
        if (balance > 1 && clave.compareTo(nodo.getIzquierdo().getClave()) > 0) {
            nodo.setIzquierdo(rotarIzquierda(nodo.getIzquierdo()));
            return rotarDerecha(nodo);
        }

        // Rotación derecha-izquierda
        if (balance < -1 && clave.compareTo(nodo.getDerecho().getClave()) < 0) {
            nodo.setDerecho(rotarDerecha(nodo.getDerecho()));
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    /**
     * Busca reserva por clave/código de vuelo
     */
    public Reserva buscar(String clave) {
        return buscarRecursivo(raiz, clave);
    }

    private Reserva buscarRecursivo(NodoAVL nodo, String clave) {
        if (nodo == null) {
            return null;
        }

        int comparacion = clave.compareTo(nodo.getClave());

        if (comparacion < 0) {
            return buscarRecursivo(nodo.getIzquierdo(), clave);
        } else if (comparacion > 0) {
            return buscarRecursivo(nodo.getDerecho(), clave);
        } else {
            return nodo.getReserva();
        }
    }
}


