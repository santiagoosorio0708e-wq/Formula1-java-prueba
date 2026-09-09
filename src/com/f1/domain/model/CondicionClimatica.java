package com.f1.domain.model;

/**
 * Enum que representa las condiciones climÃ¡ticas durante una carrera.
 * Cada condiciÃ³n afecta la velocidad, el consumo de combustible
 * y el desgaste de neumÃ¡ticos de manera diferente.
 */
public enum CondicionClimatica {
    SECO("Seco", "â˜€ï¸", 1.00, 1.00, 1.00),
    NUBLADO("Nublado", "â˜ï¸", 1.05, 1.03, 1.02),
    LLUVIOSO("Lluvioso", "ðŸŒ§ï¸", 1.15, 1.12, 0.85),
    EXTREMO("Extremo", "â›ˆï¸", 1.30, 1.25, 2.50);

    private final String nombre;
    private final String icono;
    private final double factorVelocidad;      // > 1.0 = mÃ¡s lento (multiplica tiempo)
    private final double factorConsumo;        // > 1.0 = mÃ¡s consumo
    private final double factorDesgaste;       // > 1.0 = mÃ¡s desgaste (excepto lluvioso que reduce)

    CondicionClimatica(String nombre, String icono, double factorVelocidad,
                       double factorConsumo, double factorDesgaste) {
        this.nombre = nombre;
        this.icono = icono;
        this.factorVelocidad = factorVelocidad;
        this.factorConsumo = factorConsumo;
        this.factorDesgaste = factorDesgaste;
    }

    public String getNombre() { return nombre; }
    public String getIcono() { return icono; }
    public double getFactorVelocidad() { return factorVelocidad; }
    public double getFactorConsumo() { return factorConsumo; }
    public double getFactorDesgaste() { return factorDesgaste; }

    /**
     * Obtiene la clave de clima para buscar en los mapas de rendimiento.
     * @return clave compatible con ModoConduccion ("seco", "lluvioso", "extremo")
     */
    public String getClave() {
        return switch (this) {
            case SECO, NUBLADO -> "seco";
            case LLUVIOSO -> "lluvioso";
            case EXTREMO -> "extremo";
        };
    }

    @Override
    public String toString() {
        return String.format("%s %s (Factor velocidad: x%.2f)", icono, nombre, factorVelocidad);
    }
}
