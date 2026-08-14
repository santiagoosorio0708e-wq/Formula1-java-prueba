package com.f1.datos;

import com.f1.modelo.*;

import java.util.*;

/**
 * Inicializa los datos del sistema con los 20 pilotos, 10 equipos,
 * 10 vehículos y 7 circuitos del documento de especificación.
 */
public class DataInitializer {

    /**
     * Carga todos los datos iniciales en el DataManager.
     */
    public static void inicializar() {
        DataManager dm = DataManager.getInstance();
        cargarPilotos(dm);
        cargarEquipos(dm);
        cargarVehiculos(dm);
        cargarCircuitos(dm);
    }

    private static void cargarPilotos(DataManager dm) {
        // Red Bull Racing
        dm.agregarPiloto(new Piloto(1, "Max Verstappen", "Red Bull Racing", "Líder", 9, 97));
        dm.agregarPiloto(new Piloto(2, "Sergio Pérez", "Red Bull Racing", "Escudero", 13, 85));
        // Mercedes-AMG Petronas
        dm.agregarPiloto(new Piloto(3, "Lewis Hamilton", "Mercedes-AMG Petronas", "Líder", 17, 96));
        dm.agregarPiloto(new Piloto(4, "George Russell", "Mercedes-AMG Petronas", "Escudero", 5, 88));
        // Ferrari
        dm.agregarPiloto(new Piloto(5, "Charles Leclerc", "Ferrari", "Líder", 6, 92));
        dm.agregarPiloto(new Piloto(6, "Carlos Sainz", "Ferrari", "Escudero", 9, 89));
        // McLaren
        dm.agregarPiloto(new Piloto(7, "Lando Norris", "McLaren", "Líder", 5, 90));
        dm.agregarPiloto(new Piloto(8, "Oscar Piastri", "McLaren", "Escudero", 2, 86));
        // Aston Martin
        dm.agregarPiloto(new Piloto(9, "Fernando Alonso", "Aston Martin", "Líder", 22, 93));
        dm.agregarPiloto(new Piloto(10, "Lance Stroll", "Aston Martin", "Escudero", 7, 78));
        // Alpine
        dm.agregarPiloto(new Piloto(11, "Esteban Ocon", "Alpine", "Líder", 7, 82));
        dm.agregarPiloto(new Piloto(12, "Pierre Gasly", "Alpine", "Escudero", 7, 83));
        // Alfa Romeo
        dm.agregarPiloto(new Piloto(13, "Valtteri Bottas", "Alfa Romeo", "Líder", 11, 84));
        dm.agregarPiloto(new Piloto(14, "Zhou Guanyu", "Alfa Romeo", "Escudero", 2, 75));
        // Haas
        dm.agregarPiloto(new Piloto(15, "Kevin Magnussen", "Haas", "Líder", 10, 79));
        dm.agregarPiloto(new Piloto(16, "Nico Hülkenberg", "Haas", "Escudero", 12, 80));
        // AlphaTauri
        dm.agregarPiloto(new Piloto(17, "Yuki Tsunoda", "AlphaTauri", "Líder", 3, 81));
        dm.agregarPiloto(new Piloto(18, "Daniel Ricciardo", "AlphaTauri", "Escudero", 13, 83));
        // Williams
        dm.agregarPiloto(new Piloto(19, "Alexander Albon", "Williams", "Líder", 5, 82));
        dm.agregarPiloto(new Piloto(20, "Logan Sargeant", "Williams", "Escudero", 1, 72));
    }

    public static void cargarEquipos(DataManager dm) {
        dm.agregarEquipo(new Equipo("Red Bull Racing", "Austria", "Honda",
                Arrays.asList(1, 2), ""));
        dm.agregarEquipo(new Equipo("Mercedes-AMG Petronas", "Alemania", "Mercedes",
                Arrays.asList(3, 4), ""));
        dm.agregarEquipo(new Equipo("Ferrari", "Italia", "Ferrari",
                Arrays.asList(5, 6), ""));
        dm.agregarEquipo(new Equipo("McLaren", "Reino Unido", "Mercedes",
                Arrays.asList(7, 8), ""));
        dm.agregarEquipo(new Equipo("Aston Martin", "Reino Unido", "Mercedes",
                Arrays.asList(9, 10), ""));
        dm.agregarEquipo(new Equipo("Alpine", "Francia", "Renault",
                Arrays.asList(11, 12), ""));
        dm.agregarEquipo(new Equipo("Alfa Romeo", "Suiza", "Ferrari",
                Arrays.asList(13, 14), ""));
        dm.agregarEquipo(new Equipo("Haas", "Estados Unidos", "Ferrari",
                Arrays.asList(15, 16), ""));
        dm.agregarEquipo(new Equipo("AlphaTauri", "Italia", "Honda",
                Arrays.asList(17, 18), ""));
        dm.agregarEquipo(new Equipo("Williams", "Reino Unido", "Mercedes",
                Arrays.asList(19, 20), ""));
    }

    public static void cargarVehiculos(DataManager dm) {
        // Red Bull Racing RB20
        dm.agregarVehiculo(crearVehiculo("Red Bull Racing", "RB20", "Honda", 360, 2.5,
                Arrays.asList(1, 2),
                320, 1.9, 2.1, 2.4, 1.5, 0.8, 2.5,   // normal
                340, 2.4, 2.6, 3.0, 2.2, 1.2, 3.5,   // agresivo
                300, 1.6, 1.8, 2.1, 1.0, 0.5, 1.8));  // ahorro

        // Mercedes-AMG Petronas W15
        dm.agregarVehiculo(crearVehiculo("Mercedes-AMG Petronas", "W15", "Mercedes", 355, 2.6,
                Arrays.asList(3, 4),
                315, 2.0, 2.2, 2.5, 1.6, 0.9, 2.6,
                335, 2.6, 2.8, 3.2, 2.3, 1.4, 3.8,
                295, 1.7, 1.9, 2.2, 1.1, 0.6, 1.9));

        // Ferrari SF-24
        dm.agregarVehiculo(crearVehiculo("Ferrari", "SF-24", "Ferrari", 358, 2.5,
                Arrays.asList(5, 6),
                318, 1.9, 2.2, 2.5, 1.6, 0.9, 2.6,
                338, 2.5, 2.7, 3.1, 2.3, 1.3, 3.6,
                298, 1.6, 1.8, 2.1, 1.1, 0.6, 1.9));

        // McLaren MCL38
        dm.agregarVehiculo(crearVehiculo("McLaren", "MCL38", "Mercedes", 356, 2.6,
                Arrays.asList(7, 8),
                316, 2.0, 2.2, 2.5, 1.5, 0.9, 2.5,
                336, 2.5, 2.7, 3.1, 2.2, 1.3, 3.6,
                296, 1.7, 1.9, 2.2, 1.0, 0.6, 1.8));

        // Aston Martin AMR24
        dm.agregarVehiculo(crearVehiculo("Aston Martin", "AMR24", "Mercedes", 352, 2.7,
                Arrays.asList(9, 10),
                312, 2.1, 2.3, 2.6, 1.7, 1.0, 2.7,
                332, 2.7, 2.9, 3.3, 2.4, 1.5, 3.9,
                292, 1.8, 2.0, 2.3, 1.2, 0.7, 2.0));

        // Alpine A524
        dm.agregarVehiculo(crearVehiculo("Alpine", "A524", "Renault", 348, 2.8,
                Arrays.asList(11, 12),
                308, 2.2, 2.4, 2.7, 1.8, 1.0, 2.8,
                328, 2.8, 3.0, 3.4, 2.5, 1.5, 4.0,
                288, 1.9, 2.1, 2.4, 1.3, 0.7, 2.1));

        // Alfa Romeo C44
        dm.agregarVehiculo(crearVehiculo("Alfa Romeo", "C44", "Ferrari", 347, 2.8,
                Arrays.asList(13, 14),
                307, 2.2, 2.4, 2.7, 1.8, 1.0, 2.8,
                327, 2.8, 3.0, 3.4, 2.5, 1.5, 4.0,
                287, 1.9, 2.1, 2.4, 1.3, 0.7, 2.1));

        // Haas VF-24
        dm.agregarVehiculo(crearVehiculo("Haas", "VF-24", "Ferrari", 345, 2.9,
                Arrays.asList(15, 16),
                305, 2.3, 2.5, 2.8, 1.9, 1.1, 2.9,
                325, 2.9, 3.1, 3.5, 2.6, 1.6, 4.1,
                285, 2.0, 2.2, 2.5, 1.4, 0.8, 2.2));

        // AlphaTauri AT05
        dm.agregarVehiculo(crearVehiculo("AlphaTauri", "AT05", "Honda", 349, 2.7,
                Arrays.asList(17, 18),
                309, 2.1, 2.3, 2.6, 1.7, 1.0, 2.7,
                329, 2.7, 2.9, 3.3, 2.4, 1.4, 3.8,
                289, 1.8, 2.0, 2.3, 1.2, 0.7, 2.0));

        // Williams FW46
        dm.agregarVehiculo(crearVehiculo("Williams", "FW46", "Mercedes", 344, 2.9,
                Arrays.asList(19, 20),
                304, 2.3, 2.5, 2.8, 1.9, 1.1, 2.9,
                324, 2.9, 3.1, 3.5, 2.6, 1.6, 4.1,
                284, 2.0, 2.2, 2.5, 1.4, 0.8, 2.2));
    }

    /**
     * Helper para crear un vehículo con todos los modos de conducción.
     */
    private static Vehiculo crearVehiculo(String equipo, String modelo, String motor,
                                           double velMax, double acel, List<Integer> pilotos,
                                           // Normal
                                           double nVel, double nCSeco, double nCLluv, double nCExt,
                                           double nDSeco, double nDLluv, double nDExt,
                                           // Agresivo
                                           double aVel, double aCSeco, double aCLluv, double aCExt,
                                           double aDSeco, double aDLluv, double aDExt,
                                           // Ahorro
                                           double eVel, double eCSeco, double eCLluv, double eCExt,
                                           double eDSeco, double eDLluv, double eDExt) {
        Map<String, ModoConduccion> rendimiento = new HashMap<>();

        // Modo Normal
        Map<String, Double> consumoNormal = new HashMap<>();
        consumoNormal.put("seco", nCSeco);
        consumoNormal.put("lluvioso", nCLluv);
        consumoNormal.put("extremo", nCExt);
        Map<String, Double> desgasteNormal = new HashMap<>();
        desgasteNormal.put("seco", nDSeco);
        desgasteNormal.put("lluvioso", nDLluv);
        desgasteNormal.put("extremo", nDExt);
        rendimiento.put("conduccion_normal", new ModoConduccion(nVel, consumoNormal, desgasteNormal));

        // Modo Agresivo
        Map<String, Double> consumoAgresivo = new HashMap<>();
        consumoAgresivo.put("seco", aCSeco);
        consumoAgresivo.put("lluvioso", aCLluv);
        consumoAgresivo.put("extremo", aCExt);
        Map<String, Double> desgasteAgresivo = new HashMap<>();
        desgasteAgresivo.put("seco", aDSeco);
        desgasteAgresivo.put("lluvioso", aDLluv);
        desgasteAgresivo.put("extremo", aDExt);
        rendimiento.put("conduccion_agresiva", new ModoConduccion(aVel, consumoAgresivo, desgasteAgresivo));

        // Modo Ahorro
        Map<String, Double> consumoAhorro = new HashMap<>();
        consumoAhorro.put("seco", eCSeco);
        consumoAhorro.put("lluvioso", eCLluv);
        consumoAhorro.put("extremo", eCExt);
        Map<String, Double> desgasteAhorro = new HashMap<>();
        desgasteAhorro.put("seco", eDSeco);
        desgasteAhorro.put("lluvioso", eDLluv);
        desgasteAhorro.put("extremo", eDExt);
        rendimiento.put("ahorro_combustible", new ModoConduccion(eVel, consumoAhorro, desgasteAhorro));

        return new Vehiculo(equipo, modelo, motor, velMax, acel, pilotos, rendimiento, "");
    }

    public static void cargarCircuitos(DataManager dm) {
        // 1. Mónaco
        dm.agregarCircuito(new Circuito(
                "Circuito de Mónaco", "Mónaco", 3.34, 78,
                "Uno de los circuitos más prestigiosos y difíciles del calendario, conocido por sus calles angostas y la falta de zonas de adelantamiento.",
                new RecordVuelta("1:10.166", "Lewis Hamilton", 2019),
                Arrays.asList(
                        new GanadorHistorico(2021, 1),
                        new GanadorHistorico(2022, 2),
                        new GanadorHistorico(2023, 1)
                ), ""));

        // 2. Silverstone
        dm.agregarCircuito(new Circuito(
                "Silverstone", "Reino Unido", 5.89, 52,
                "Uno de los circuitos más rápidos del calendario, con curvas de alta velocidad como Maggotts y Becketts.",
                new RecordVuelta("1:27.097", "Max Verstappen", 2020),
                Arrays.asList(
                        new GanadorHistorico(2021, 3),
                        new GanadorHistorico(2022, 5),
                        new GanadorHistorico(2023, 1)
                ), ""));

        // 3. Spa-Francorchamps
        dm.agregarCircuito(new Circuito(
                "Circuito de Spa-Francorchamps", "Bélgica", 7.00, 44,
                "Famoso por la curva Eau Rouge y la larga recta de Kemmel, un circuito donde la potencia del motor es clave.",
                new RecordVuelta("1:46.286", "Valtteri Bottas", 2018),
                Arrays.asList(
                        new GanadorHistorico(2021, 1),
                        new GanadorHistorico(2022, 1),
                        new GanadorHistorico(2023, 1)
                ), ""));

        // 4. Monza
        dm.agregarCircuito(new Circuito(
                "Circuito de Monza", "Italia", 5.79, 53,
                "Conocido como 'El Templo de la Velocidad', Monza es el circuito más rápido del calendario con largas rectas y chicanes icónicas.",
                new RecordVuelta("1:21.046", "Rubens Barrichello", 2004),
                Arrays.asList(
                        new GanadorHistorico(2021, 2),
                        new GanadorHistorico(2022, 1),
                        new GanadorHistorico(2023, 1)
                ), ""));

        // 5. Interlagos
        dm.agregarCircuito(new Circuito(
                "Interlagos", "Brasil", 4.31, 71,
                "Interlagos es un circuito legendario con cambios de elevación y un trazado técnico que ha sido sede de algunas de las carreras más emocionantes de la historia.",
                new RecordVuelta("1:10.540", "Valtteri Bottas", 2018),
                Arrays.asList(
                        new GanadorHistorico(2021, 3),
                        new GanadorHistorico(2022, 1),
                        new GanadorHistorico(2023, 1)
                ), ""));

        // 6. Yas Marina
        dm.agregarCircuito(new Circuito(
                "Circuito de Yas Marina", "Emiratos Árabes Unidos", 5.28, 58,
                "Ubicado en Abu Dhabi, es famoso por ser el circuito donde se definen muchos campeonatos, con un diseño moderno y una espectacular carrera nocturna.",
                new RecordVuelta("1:39.283", "Lewis Hamilton", 2019),
                Arrays.asList(
                        new GanadorHistorico(2021, 1),
                        new GanadorHistorico(2022, 1),
                        new GanadorHistorico(2023, 3)
                ), ""));

        // 7. Suzuka
        dm.agregarCircuito(new Circuito(
                "Circuito de Suzuka", "Japón", 5.81, 53,
                "Un circuito desafiante con un diseño en forma de ocho, famoso por sus curvas de alta velocidad como 130R y la 'S' de Senna.",
                new RecordVuelta("1:30.983", "Lewis Hamilton", 2019),
                Arrays.asList(
                        new GanadorHistorico(2021, 1),
                        new GanadorHistorico(2022, 1),
                        new GanadorHistorico(2023, 1)
                ), ""));
    }
}
