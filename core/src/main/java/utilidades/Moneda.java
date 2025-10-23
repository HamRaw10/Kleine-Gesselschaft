package utilidades;

public class Moneda {
        private int cantidad;

        public Moneda() { this(0); }
        public Moneda(int inicial) { this.cantidad = Math.max(0, inicial); }

        public int getCantidad() { return cantidad; }

        /** Suma (ignora negativos) */
        public void sumar(int delta) {
            if (delta <= 0) return;
            long nuevo = (long) cantidad + delta;               // evita overflow
            cantidad = (int) Math.min(nuevo, Integer.MAX_VALUE);
        }

        /** Resta si hay fondos; true si pudo */
        public boolean restar(int costo) {
            if (costo <= 0) return true;
            if (costo <= cantidad) { cantidad -= costo; return true; }
            return false;
        }

        public void setCantidad(int valor) { cantidad = Math.max(0, valor); }
    }


