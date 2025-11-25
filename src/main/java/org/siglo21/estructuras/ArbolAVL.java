package org.siglo21.estructuras;


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


}


