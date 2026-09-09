package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.domain.model.Circuito;
import com.f1.domain.model.ConfiguracionVehiculo;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackPreviewPanel extends JPanel {

    private String trackName = "";
    private String trackInfo = "";
    private ConfiguracionVehiculo config;
    
    private Timer timer;
    private double progress = 0.0;
    
    private Map<String, Path2D> trackPaths;
    private Path2D currentPath;
    private List<Point2D.Double> pathPoints;
    private double totalPathLength;
    
    private double glowPhase = 0;

    public TrackPreviewPanel() {
        setOpaque(false);
        initPaths();
        
        // 60 FPS Game Loop
        timer = new Timer(16, e -> {
            progress += 0.003; // Velocidad de recorrido (más lenta para mayor realismo)
            if (progress > 1.0) progress = 0.0;
            glowPhase += 0.1;
            repaint();
        });
        timer.start();
    }
    
    public void setPreviewData(Circuito circuito, ConfiguracionVehiculo config) {
        if (circuito != null) {
            this.trackName = circuito.getNombre();
            this.trackInfo = String.format("%s | %.2f km | %d Vueltas", circuito.getPais(), circuito.getLongitudKm(), circuito.getVueltas());
            
            // Buscar path por nombre aproximado
            String match = "DEFAULT";
            for (String key : trackPaths.keySet()) {
                if (trackName.toLowerCase().contains(key.toLowerCase())) {
                    match = key;
                    break;
                }
            }
            setCurrentPath(trackPaths.get(match));
        } else {
            this.trackName = "NO SIGNAL";
            this.trackInfo = "Esperando selección...";
            setCurrentPath(null);
        }
        
        this.config = config;
        this.progress = 0; // Reiniciar animacion
        repaint();
    }
    
    private void setCurrentPath(Path2D path) {
        this.currentPath = path;
        this.pathPoints = new ArrayList<>();
        this.totalPathLength = 0;
        
        if (path == null) return;
        
        // Flatten path to segments for easy interpolation
        PathIterator pi = path.getPathIterator(null, 0.01); // Mayor precisión
        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        
        while (!pi.isDone()) {
            int type = pi.currentSegment(coords);
            if (type == PathIterator.SEG_MOVETO) {
                lastX = coords[0];
                lastY = coords[1];
                pathPoints.add(new Point2D.Double(lastX, lastY));
            } else if (type == PathIterator.SEG_LINETO) {
                double dx = coords[0] - lastX;
                double dy = coords[1] - lastY;
                totalPathLength += Math.sqrt(dx*dx + dy*dy);
                lastX = coords[0];
                lastY = coords[1];
                pathPoints.add(new Point2D.Double(lastX, lastY));
            } else if (type == PathIterator.SEG_CLOSE) {
                if (!pathPoints.isEmpty()) {
                    Point2D.Double first = pathPoints.get(0);
                    double dx = first.x - lastX;
                    double dy = first.y - lastY;
                    totalPathLength += Math.sqrt(dx*dx + dy*dy);
                    pathPoints.add(new Point2D.Double(first.x, first.y));
                }
            }
            pi.next();
        }
    }
    
    private Point2D.Double getInterpolatedPoint(double prog) {
        if (pathPoints == null || pathPoints.size() < 2) return new Point2D.Double(0,0);
        
        double targetLen = prog * totalPathLength;
        double currentLen = 0;
        
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            Point2D.Double p1 = pathPoints.get(i);
            Point2D.Double p2 = pathPoints.get(i+1);
            
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double segLen = Math.sqrt(dx*dx + dy*dy);
            
            if (currentLen + segLen >= targetLen) {
                // Interpolar
                double remaining = targetLen - currentLen;
                double t = remaining / segLen;
                return new Point2D.Double(p1.x + dx * t, p1.y + dy * t);
            }
            currentLen += segLen;
        }
        return pathPoints.get(pathPoints.size()-1);
    }
    
    private void initPaths() {
        trackPaths = new HashMap<>();
        
        // Rutas normalizadas (0.0 a 1.0 en x/y) - Serán escaladas
        
        // Monza (Simplificado)
        Path2D monza = new Path2D.Double();
        monza.moveTo(0.5, 0.9); // Parabolica start
        monza.curveTo(0.7, 0.9, 0.8, 0.8, 0.8, 0.6); // Parabolica
        monza.lineTo(0.5, 0.1); // Recta
        monza.curveTo(0.4, 0.1, 0.3, 0.2, 0.35, 0.25); // Variante Rettifilo
        monza.lineTo(0.7, 0.4); // Curva Grande
        monza.curveTo(0.75, 0.45, 0.75, 0.5, 0.65, 0.5); // Variante della Roggia
        monza.lineTo(0.55, 0.6); // Lesmo
        monza.curveTo(0.5, 0.65, 0.45, 0.7, 0.55, 0.75); // Ascari
        monza.lineTo(0.5, 0.9);
        trackPaths.put("monza", monza);
        
        // Spa
        Path2D spa = new Path2D.Double();
        spa.moveTo(0.5, 0.1); // La Source
        spa.lineTo(0.6, 0.2); // Eau Rouge
        spa.curveTo(0.65, 0.25, 0.65, 0.3, 0.8, 0.35); // Kemmel Straight / Les Combes
        spa.lineTo(0.7, 0.6); // Pouhon
        spa.curveTo(0.65, 0.7, 0.5, 0.8, 0.4, 0.75); // Stavelot
        spa.lineTo(0.2, 0.6); // Blanchimont
        spa.curveTo(0.1, 0.5, 0.1, 0.4, 0.2, 0.3); // Bus Stop
        spa.lineTo(0.5, 0.1);
        trackPaths.put("spa", spa);
        
        // Silverstone
        Path2D silv = new Path2D.Double();
        silv.moveTo(0.5, 0.8); // Copse
        silv.curveTo(0.7, 0.7, 0.8, 0.6, 0.7, 0.5); // Maggotts Becketts
        silv.lineTo(0.9, 0.3); // Hangar Straight
        silv.curveTo(0.9, 0.2, 0.8, 0.1, 0.7, 0.15); // Stowe
        silv.lineTo(0.3, 0.2); // Vale / Club
        silv.curveTo(0.2, 0.3, 0.1, 0.4, 0.2, 0.6); // Abbey / Farm
        silv.lineTo(0.3, 0.7); // Brooklands
        silv.curveTo(0.4, 0.8, 0.4, 0.9, 0.5, 0.8);
        trackPaths.put("silverstone", silv);
        
        // Default genérico en caso de error
        Path2D def = new Path2D.Double();
        def.moveTo(0.2, 0.5);
        def.curveTo(0.2, 0.2, 0.8, 0.2, 0.8, 0.5);
        def.curveTo(0.8, 0.8, 0.2, 0.8, 0.2, 0.5);
        trackPaths.put("DEFAULT", def);
        
        // Interlagos
        Path2D inter = new Path2D.Double();
        inter.moveTo(0.4, 0.8);
        inter.curveTo(0.2, 0.6, 0.3, 0.4, 0.5, 0.3);
        inter.lineTo(0.8, 0.4);
        inter.curveTo(0.9, 0.5, 0.8, 0.7, 0.6, 0.7);
        inter.lineTo(0.4, 0.8);
        trackPaths.put("interlagos", inter);
        
        // Mónaco
        Path2D monaco = new Path2D.Double();
        monaco.moveTo(0.2, 0.2); // Sainte Devote
        monaco.curveTo(0.4, 0.1, 0.6, 0.3, 0.5, 0.4); // Massenet / Casino
        monaco.lineTo(0.6, 0.5); // Mirabeau
        monaco.curveTo(0.7, 0.6, 0.5, 0.7, 0.4, 0.6); // Hairpin / Portier
        monaco.lineTo(0.8, 0.7); // Tunnel
        monaco.curveTo(0.9, 0.8, 0.7, 0.9, 0.6, 0.8); // Nouvelle Chicane
        monaco.lineTo(0.3, 0.8); // Tabac / Swimming Pool
        monaco.curveTo(0.1, 0.7, 0.2, 0.4, 0.2, 0.2); // Rascasse / Anthony Noghes
        trackPaths.put("mónaco", monaco);
        trackPaths.put("monaco", monaco);

        // Yas Marina
        Path2D yas = new Path2D.Double();
        yas.moveTo(0.4, 0.2);
        yas.lineTo(0.8, 0.2);
        yas.lineTo(0.9, 0.4);
        yas.lineTo(0.7, 0.6);
        yas.curveTo(0.6, 0.7, 0.5, 0.7, 0.4, 0.6);
        yas.lineTo(0.2, 0.8);
        yas.lineTo(0.1, 0.6);
        yas.curveTo(0.1, 0.4, 0.3, 0.3, 0.4, 0.2);
        trackPaths.put("yas", yas);

        // Suzuka (Figura 8 aprox)
        Path2D suzuka = new Path2D.Double();
        suzuka.moveTo(0.8, 0.8); // Recta Principal
        suzuka.curveTo(0.9, 0.5, 0.7, 0.4, 0.6, 0.5); // S Curves
        suzuka.lineTo(0.4, 0.7); // Degner
        suzuka.curveTo(0.2, 0.9, 0.1, 0.7, 0.2, 0.5); // Hairpin
        suzuka.lineTo(0.5, 0.3); // 200R / Spoon
        suzuka.curveTo(0.6, 0.1, 0.8, 0.2, 0.7, 0.4); // Spoon
        suzuka.lineTo(0.4, 0.5); // Cross over
        suzuka.lineTo(0.2, 0.2); // 130R
        suzuka.curveTo(0.1, 0.1, 0.3, 0.1, 0.4, 0.2); // Casio Triangle
        suzuka.lineTo(0.8, 0.8);
        trackPaths.put("suzuka", suzuka);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Fondo / Rejilla HUD
        drawHUDBackground(g2, w, h);
        
        // Dibujar Pista Paramétrica
        if (currentPath != null) {
            // Escalar path para centrarlo y ajustarlo al panel
            // Lo hacemos un poco más pequeño para dejar espacio abajo
            double scale = Math.min(w, h) * 0.55;
            double offsetX = (w - scale) / 2.0; 
            double offsetY = (h - scale) / 2.0 - 40; // Desplazar hacia arriba
            
            AffineTransform tx = new AffineTransform();
            tx.translate(offsetX, offsetY);
            tx.scale(scale, scale);
            Shape scaledTrack = tx.createTransformedShape(currentPath);
            
            // Glow base del circuito
            g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(30, 30, 45, 180));
            g2.draw(scaledTrack);
            
            // Línea central brillante
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(F1Colors.BORDER);
            g2.draw(scaledTrack);
            
            // Vehículo animado recorriendo la pista
            Point2D.Double p = getInterpolatedPoint(progress);
            Point2D transformedP = tx.transform(p, null);
            
            // Efecto Estela / Neón Pulsante
            g2.setColor(new Color(F1Colors.F1_RED.getRed(), F1Colors.F1_RED.getGreen(), F1Colors.F1_RED.getBlue(), 120));
            int glowSize = (int) (18 + Math.sin(glowPhase) * 6);
            g2.fillOval((int)transformedP.getX() - glowSize/2, (int)transformedP.getY() - glowSize/2, glowSize, glowSize);
            
            // Núcleo brillante
            g2.setColor(Color.WHITE);
            g2.fillOval((int)transformedP.getX() - 3, (int)transformedP.getY() - 3, 6, 6);
        }
        
        // Renderizar Cuadro de Telemetría
        drawTelemetry(g2, w, h);
    }
    
    private void drawHUDBackground(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(15, 15, 20));
        g2.fillRect(0, 0, w, h);
        
        // Rejilla de fondo
        g2.setColor(new Color(255, 255, 255, 10));
        int gridSize = 40;
        for (int i = 0; i < w; i += gridSize) g2.drawLine(i, 0, i, h);
        for (int j = 0; j < h; j += gridSize) g2.drawLine(0, j, w, j);
        
        // Acentos de esquina (Estética Game HUD)
        g2.setColor(F1Colors.F1_RED);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(0, 15, 15, 0);
        g2.drawLine(w-15, h, w, h-15);
        g2.drawLine(0, h-15, 15, h);
        g2.drawLine(w-15, 0, w, 15);
    }
    
    private void drawTelemetry(Graphics2D g2, int w, int h) {
        // Título del Circuito (Arriba Izquierda)
        g2.setFont(F1Fonts.SUBTITLE);
        g2.setColor(F1Colors.F1_RED);
        g2.drawString("> " + trackName.toUpperCase(), 20, 30);
        
        g2.setFont(F1Fonts.BODY);
        g2.setColor(F1Colors.TEXT_SECONDARY);
        g2.drawString(trackInfo, 20, 50);
        
        // Panel inferior de estadísticas
        if (config != null) {
            int panelW = w - 40;
            int panelH = 90;
            int panelX = 20;
            int panelY = h - panelH - 20;
            
            // Caja traslúcida holográfica
            g2.setColor(new Color(10, 10, 15, 220));
            g2.fillRect(panelX, panelY, panelW, panelH);
            g2.setColor(F1Colors.F1_RED_DARK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(panelX, panelY, panelW, panelH);
            
            // Decoración superior
            g2.setColor(F1Colors.F1_RED);
            g2.fillRect(panelX, panelY, panelW, 4);
            
            g2.setFont(F1Fonts.BODY_BOLD);
            g2.setColor(F1Colors.TEXT_WHITE);
            g2.drawString("ESTIMACIONES DE VUELTA /", panelX + 15, panelY + 25);
            
            g2.setFont(F1Fonts.BODY_SMALL);
            g2.setColor(F1Colors.TEXT_SECONDARY);
            
            // Layout horizontal de 3 columnas
            int colWidth = panelW / 3;
            
            // Factor Rendimiento
            double rend = config.calcularFactorRendimiento();
            g2.drawString("Rendimiento (Velocidad):", panelX + 15, panelY + 50);
            drawProgressBar(g2, panelX + 15, panelY + 60, colWidth - 30, (float)Math.max(0, 2.0 - rend) / 2f, getTelemetryColor(rend));
            
            // Factor Desgaste
            double desg = config.calcularFactorDesgaste();
            g2.drawString("Desgaste Neumáticos:", panelX + 15 + colWidth, panelY + 50);
            drawProgressBar(g2, panelX + 15 + colWidth, panelY + 60, colWidth - 30, (float)Math.max(0, 2.0 - desg) / 2f, getTelemetryColor(desg));
            
            // Factor Consumo
            double cons = config.calcularFactorConsumo();
            g2.drawString("Consumo Combustible:", panelX + 15 + colWidth*2, panelY + 50);
            drawProgressBar(g2, panelX + 15 + colWidth*2, panelY + 60, colWidth - 30, (float)Math.max(0, 2.0 - cons) / 2f, getTelemetryColor(cons));
            
            g2.setFont(F1Fonts.BODY_SMALL);
            g2.setColor(F1Colors.STATUS_OK);
            g2.drawString("[ SISTEMA OPTIMIZADO ]", panelX + panelW - 140, panelY + 25);
            
        } else {
            g2.setFont(F1Fonts.BODY);
            g2.setColor(F1Colors.TEXT_SECONDARY);
            g2.drawString("Esperando enlace de telemetría...", 20, h - 20);
        }
    }
    
    private Color getTelemetryColor(double factor) {
        if (factor <= 0.95) return F1Colors.STATUS_OK;  // Muy bueno
        if (factor > 1.05) return F1Colors.STATUS_DANGER;    // Malo
        return F1Colors.STATUS_WARNING;                      // Medio
    }
    
    private void drawProgressBar(Graphics2D g2, int x, int y, int width, float percentage, Color color) {
        // Fondo barra
        g2.setColor(new Color(30, 30, 35));
        g2.fillRect(x, y, width, 10);
        
        // Relleno
        g2.setColor(color);
        int fill = (int) (width * percentage);
        g2.fillRect(x, y, fill, 10);
        
        // Segmentos (Efecto tecnológico)
        g2.setColor(new Color(15, 15, 20));
        for(int i = 0; i < fill; i += 8) {
            g2.drawLine(x + i, y, x + i + 2, y + 10);
        }
    }
}
