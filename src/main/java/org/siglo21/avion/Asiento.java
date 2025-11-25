package org.siglo21.avion;

public class Asiento {

    private String seccion; // A,B,C
    private int numero;
    private boolean ocupado;

    public Asiento(String seccion, int numero) {
        this.seccion = seccion;
        this.numero = numero;
        this.ocupado = false;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getNumero() {
        return numero;
    }


    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    @Override
    public String toString() {
        return seccion + numero;
    }
}
