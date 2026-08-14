package com.f1.modelo;

/**
 * Representa la configuración del vehículo elegida por el usuario antes de la carrera.
 * Incluye modo de conducción, carga aerodinámica, presión de neumáticos y estrategia de combustible.
 */
public class ConfiguracionVehiculo {
    
    /** Modos de conducción disponibles */
    public enum ModoCond {
        NORMAL("conduccion_normal", "Normal", 1.0, 1.0, 1.0),
        AGRESIVO("conduccion_agresiva", "Agresivo", 0.95, 1.3, 1.4),
        AHORRO("ahorro_combustible", "Ahorro", 1.08, 0.75, 0.7);

        private final String clave;
        private final String nombre;
        private final double factorTiempo;
        private final double factorConsumo;
        private final double factorDesgaste;

        ModoCond(String clave, String nombre, double factorTiempo, double factorConsumo, double factorDesgaste) {
            this.clave = clave;
            this.nombre = nombre;
            this.factorTiempo = factorTiempo;
            this.factorConsumo = factorConsumo;
            this.factorDesgaste = factorDesgaste;
        }

        public String getClave() { return clave; }
        public String getNombre() { return nombre; }
        public double getFactorTiempo() { return factorTiempo; }
        public double getFactorConsumo() { return factorConsumo; }
        public double getFactorDesgaste() { return factorDesgaste; }
    }

    /** Niveles de carga aerodinámica */
    public enum CargaAero {
        BAJA("Baja", 0.97, 1.05),
        MEDIA("Media", 1.0, 1.0),
        ALTA("Alta", 1.04, 0.92);

        private final String nombre;
        private final double factorTiempo;   // < 1 = más rápido en rectas, > 1 = más lento
        private final double factorDesgaste; // > 1 = más desgaste

        CargaAero(String nombre, double factorTiempo, double factorDesgaste) {
            this.nombre = nombre;
            this.factorTiempo = factorTiempo;
            this.factorDesgaste = factorDesgaste;
        }

        public String getNombre() { return nombre; }
        public double getFactorTiempo() { return factorTiempo; }
        public double getFactorDesgaste() { return factorDesgaste; }
    }

    /** Niveles de presión de neumáticos */
    public enum PresionNeum {
        BAJA("Baja", 1.02, 1.15),
        ESTANDAR("Estándar", 1.0, 1.0),
        ALTA("Alta", 0.98, 0.88);

        private final String nombre;
        private final double factorTiempo;
        private final double factorDesgaste;

        PresionNeum(String nombre, double factorTiempo, double factorDesgaste) {
            this.nombre = nombre;
            this.factorTiempo = factorTiempo;
            this.factorDesgaste = factorDesgaste;
        }

        public String getNombre() { return nombre; }
        public double getFactorTiempo() { return factorTiempo; }
        public double getFactorDesgaste() { return factorDesgaste; }
    }

    /** Estrategias de combustible */
    public enum EstrategiaComb {
        AGRESIVA("Agresiva", 0.96, 1.35),
        BALANCEADA("Balanceada", 1.0, 1.0),
        AHORRO("Ahorro", 1.06, 0.70);

        private final String nombre;
        private final double factorTiempo;
        private final double factorConsumo;

        EstrategiaComb(String nombre, double factorTiempo, double factorConsumo) {
            this.nombre = nombre;
            this.factorTiempo = factorTiempo;
            this.factorConsumo = factorConsumo;
        }

        public String getNombre() { return nombre; }
        public double getFactorTiempo() { return factorTiempo; }
        public double getFactorConsumo() { return factorConsumo; }
    }

    private ModoCond modoConduccion;
    private CargaAero cargaAerodinamica;
    private PresionNeum presionNeumaticos;
    private EstrategiaComb estrategiaCombustible;

    public ConfiguracionVehiculo() {
        this.modoConduccion = ModoCond.NORMAL;
        this.cargaAerodinamica = CargaAero.MEDIA;
        this.presionNeumaticos = PresionNeum.ESTANDAR;
        this.estrategiaCombustible = EstrategiaComb.BALANCEADA;
    }

    public ConfiguracionVehiculo(ModoCond modo, CargaAero aero, PresionNeum presion, EstrategiaComb estrategia) {
        this.modoConduccion = modo;
        this.cargaAerodinamica = aero;
        this.presionNeumaticos = presion;
        this.estrategiaCombustible = estrategia;
    }

    // Getters
    public ModoCond getModoConduccion() { return modoConduccion; }
    public CargaAero getCargaAerodinamica() { return cargaAerodinamica; }
    public PresionNeum getPresionNeumaticos() { return presionNeumaticos; }
    public EstrategiaComb getEstrategiaCombustible() { return estrategiaCombustible; }

    // Setters
    public void setModoConduccion(ModoCond modoConduccion) { this.modoConduccion = modoConduccion; }
    public void setCargaAerodinamica(CargaAero cargaAerodinamica) { this.cargaAerodinamica = cargaAerodinamica; }
    public void setPresionNeumaticos(PresionNeum presionNeumaticos) { this.presionNeumaticos = presionNeumaticos; }
    public void setEstrategiaCombustible(EstrategiaComb estrategiaCombustible) { this.estrategiaCombustible = estrategiaCombustible; }

    /**
     * Calcula el factor de rendimiento combinado para el tiempo de vuelta.
     * Factor < 1.0 = más rápido, > 1.0 = más lento
     */
    public double calcularFactorRendimiento() {
        return modoConduccion.getFactorTiempo()
             * cargaAerodinamica.getFactorTiempo()
             * presionNeumaticos.getFactorTiempo()
             * estrategiaCombustible.getFactorTiempo();
    }

    /**
     * Calcula el factor de desgaste combinado de neumáticos.
     */
    public double calcularFactorDesgaste() {
        return modoConduccion.getFactorDesgaste()
             * cargaAerodinamica.getFactorDesgaste()
             * presionNeumaticos.getFactorDesgaste();
    }

    /**
     * Calcula el factor de consumo combinado de combustible.
     */
    public double calcularFactorConsumo() {
        return modoConduccion.getFactorConsumo()
             * estrategiaCombustible.getFactorConsumo();
    }

    @Override
    public String toString() {
        return String.format("Modo: %s | Aero: %s | Neumáticos: %s | Combustible: %s",
                modoConduccion.getNombre(), cargaAerodinamica.getNombre(),
                presionNeumaticos.getNombre(), estrategiaCombustible.getNombre());
    }
}
