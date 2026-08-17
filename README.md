# 🏎️ Formula 1 - Simulación en Java

# DIAPOSITIVAS : https://drive.google.com/drive/folders/10ngEaurSDW1NG5CtnBJx-SRcnXHXRLqV?usp=sharing

## Descripción
Simulación interactiva de Fórmula 1 desarrollada en **Java Swing** con sistema de concurrencia (hilos), gestión de clima dinámico, combustible, neumáticos y clasificación en tiempo real.

## Características
- ✅ 20 pilotos reales de F1 con 10 equipos
- ✅ 7 circuitos con datos reales
- ✅ Simulación con hilos (un Thread por piloto)
- ✅ Sistema de clima dinámico (seco, nublado, lluvioso, extremo)
- ✅ Gestión de combustible y pit stops
- ✅ CRUD completo para pilotos, equipos, vehículos y circuitos
- ✅ Configuración de vehículo (modo conducción, aerodinámica, neumáticos)
- ✅ Podio visual para top 3 + clasificación completa
- ✅ Interfaz Swing con diseño premium oscuro

## Tecnologías
- **Lenguaje:** Java 21
- **GUI:** Java Swing
- **Concurrencia:** java.util.concurrent (CyclicBarrier, CountDownLatch)
- **Datos:** HashMap (persistencia temporal)
- **Control de versiones:** Git con GitFlow

## Estructura del Proyecto
```
src/com/f1/
├── Main.java
├── modelo/          # Entidades del dominio
├── datos/           # DataManager con HashMap
├── simulacion/      # Motor de simulación con hilos
└── gui/             # Interfaz Swing
    ├── paneles/     # Paneles principales
    ├── componentes/ # Componentes reutilizables
    └── util/        # Colores, fuentes, utilidades
```

## Ejecución
```bash
# Compilar
javac -d bin -sourcepath src src/com/f1/Main.java

# Ejecutar
java -cp bin com.f1.Main
```

## GitFlow
- `main` - Rama protegida (releases)
- `develop` - Integración continua
- `feature/*` - Ramas de funcionalidades

## Autores
Santiago Osorio
Camilo García 
