package com.f1.infrastructure.adapter.in.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashFrame extends JFrame {

    private Timer animationTimer;
    private long startTime;
    private List<ConfettiParticle> particles = new ArrayList<>();
    private Random random = new Random();
    private boolean confetiLanzado = false;
    
    private boolean fadingOut = false;
    private float globalAlpha = 1.0f;

    private Image f1LogoImage;
    private boolean intentadoCargarLogo = false;

    // Caché para optimizar FPS
    private BufferedImage backgroundCache;

    public SplashFrame() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startTime = System.currentTimeMillis();

        cargarLogoConFiltroColor();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Optimización de renderizado para subir FPS
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                long currentTime = System.currentTimeMillis() - startTime;

                if (fadingOut) {
                    globalAlpha -= 0.05f;
                    if (globalAlpha <= 0f) {
                        globalAlpha = 0f;
                        avanzarInmediato();
                    }
                }
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));

                // 1. Dibujar el fondo cacheado (Sube los FPS dramáticamente)
                if (backgroundCache == null || backgroundCache.getWidth() != w || backgroundCache.getHeight() != h) {
                    crearFondoCacheado(w, h);
                }
                g2d.drawImage(backgroundCache, 0, 0, null);

                // 2. God Rays Optimizados (Menos rayos)
                drawGodRays(g2d, w, h, currentTime);

                // 3. Banderas Realistas Optimizadas (Menos polígonos, matemática simplificada)
                drawOptimizedWavingFlags(g2d, w, h, currentTime);

                // 4. Logo F1 Extraído
                draw3DNeonLogo(g2d, w, h, currentTime);



                // 6. Texto animado
                drawBlinkingText(g2d, w, h, currentTime);

                // 7. Confeti
                if (currentTime > 3000) {
                    if (!confetiLanzado) {
                        lanzarConfeti(w);
                        confetiLanzado = true;
                    }
                    updateAndDrawConfetti(g2d);
                }
            }
        };

        setContentPane(panel);

        // Subir a ~60fps (16ms) con carga optimizada
        animationTimer = new Timer(16, e -> panel.repaint());
        animationTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { iniciarFadeOut(); }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { iniciarFadeOut(); }
        });
        
        setFocusable(true);
        requestFocusInWindow();
    }

    private void crearFondoCacheado(int w, int h) {
        backgroundCache = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBG = backgroundCache.createGraphics();
        gBG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        gBG.setColor(new Color(10, 12, 15));
        gBG.fillRect(0, 0, w, h);
        
        // Fibra de carbono estática
        gBG.setColor(new Color(30, 30, 35, 80));
        gBG.setStroke(new BasicStroke(2));
        for (int i = -h; i < w; i += 12) {
            gBG.drawLine(i, 0, i + h, h);
            gBG.drawLine(i, h, i + h, 0);
        }
        
        // Marcas de agua estáticas
        if (f1LogoImage != null && f1LogoImage.getWidth(this) > 0) {
            gBG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
            int imgW = 200;
            int imgH = (imgW * f1LogoImage.getHeight(this)) / f1LogoImage.getWidth(this);
            for (int x = -100; x < w + 100; x += 300) {
                for (int y = -100; y < h + 100; y += 200) {
                    gBG.drawImage(f1LogoImage, x, y, imgW, imgH, this);
                }
            }
        }
        
        gBG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        float radius = Math.max(w, h);
        float[] dist = {0.0f, 0.6f, 1.0f};
        Color[] colors = {new Color(60, 65, 80, 150), new Color(15, 18, 22, 180), new Color(5, 5, 8, 255)};
        RadialGradientPaint rgp = new RadialGradientPaint(new Point(w / 2, h / 2), radius, dist, colors);
        gBG.setPaint(rgp);
        gBG.fillRect(0, 0, w, h);
        
        gBG.dispose();
    }

    private void cargarLogoConFiltroColor() {
        if (!intentadoCargarLogo) {
            File logoFile = new File("img/logo_f1.png");
            if (logoFile.exists()) {
                try {
                    java.awt.image.BufferedImage rawImage = javax.imageio.ImageIO.read(logoFile);
                    // Filtro infalible: Extraer SÓLO los píxeles ROJOS del logo, borrando fondos de cuadros o blancos.
                    java.awt.image.ImageFilter filter = new java.awt.image.RGBImageFilter() {
                        @Override
                        public final int filterRGB(int x, int y, int rgb) {
                            int r = (rgb >> 16) & 0xFF;
                            int g = (rgb >> 8) & 0xFF;
                            int b = rgb & 0xFF;
                            // Si el píxel es fuertemente rojo (logo F1)
                            if (r > 150 && g < 80 && b < 80) {
                                return rgb; // Mantener rojo
                            }
                            return 0x00FFFFFF & rgb; // Hacer TODO lo demás transparente
                        }
                    };
                    java.awt.image.ImageProducer ip = new java.awt.image.FilteredImageSource(rawImage.getSource(), filter);
                    f1LogoImage = Toolkit.getDefaultToolkit().createImage(ip);
                } catch (Exception e) {
                    f1LogoImage = new ImageIcon("img/logo_f1.png").getImage();
                }
            }
            intentadoCargarLogo = true;
        }
    }

    private void drawGodRays(Graphics2D g2d, int w, int h, long time) {
        int centerX = w / 2;
        int centerY = h / 2;
        int numRays = 10; // Reducido para FPS
        double rotationOffset = time * 0.00015;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f * globalAlpha));
        g2d.setColor(Color.WHITE);

        for (int i = 0; i < numRays; i++) {
            double angle1 = (i * Math.PI * 2 / numRays) + rotationOffset;
            double angle2 = angle1 + 0.12; 
            int[] xPoints = {centerX, centerX + (int)(Math.cos(angle1) * w), centerX + (int)(Math.cos(angle2) * w)};
            int[] yPoints = {centerY, centerY + (int)(Math.sin(angle1) * w), centerY + (int)(Math.sin(angle2) * w)};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));
    }

    private void drawOptimizedWavingFlags(Graphics2D g2d, int w, int h, long time) {
        int rectSize = 15;
        int rows = 12;
        int cols = 20;
        AffineTransform old = g2d.getTransform();

        // Cached math values para optimizar
        double t1 = time * 0.005;
        double t2 = time * 0.002;

        // Izquierda
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double baseMath = c * 0.15 + r * 0.1;
                double wave = Math.sin(t1 + baseMath) * Math.cos(t2 + c * 0.05);
                int yOffset = (int)(wave * 20);
                int x = c * rectSize - 50;
                int y = r * rectSize - 50 + yOffset;
                
                boolean isWhite = ((r/2) + (c/2)) % 2 == 0;
                Color baseColor = isWhite ? new Color(230, 230, 235) : new Color(15, 15, 18);
                
                int shadow = (int)(Math.cos(t1 + baseMath) * 50);
                g2d.setColor(new Color(
                    Math.max(0, Math.min(255, baseColor.getRed() + shadow)),
                    Math.max(0, Math.min(255, baseColor.getGreen() + shadow)),
                    Math.max(0, Math.min(255, baseColor.getBlue() + shadow))
                ));
                
                int nextY = y + rectSize;
                int[] px = {x, x + rectSize, x + rectSize, x};
                int[] py = {y, y, nextY, nextY};
                g2d.fillPolygon(px, py, 4);
            }
        }
        
        // Derecha
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double baseMath = -c * 0.15 - r * 0.1;
                double wave = Math.sin(t1 + baseMath) * Math.cos(t2 - c * 0.05);
                int yOffset = (int)(wave * 20);
                int x = w - (cols - c) * rectSize + 50;
                int y = h - (rows - r) * rectSize + 50 + yOffset;
                
                boolean isWhite = ((r/2) + (c/2)) % 2 == 0;
                Color baseColor = isWhite ? new Color(230, 230, 235) : new Color(15, 15, 18);
                
                int shadow = (int)(Math.cos(t1 + baseMath) * -50); 
                g2d.setColor(new Color(
                    Math.max(0, Math.min(255, baseColor.getRed() + shadow)),
                    Math.max(0, Math.min(255, baseColor.getGreen() + shadow)),
                    Math.max(0, Math.min(255, baseColor.getBlue() + shadow))
                ));
                
                int nextY = y + rectSize;
                int[] px = {x, x + rectSize, x + rectSize, x};
                int[] py = {y, y, nextY, nextY};
                g2d.fillPolygon(px, py, 4);
            }
        }
        g2d.setTransform(old);
    }

    private void draw3DNeonLogo(Graphics2D g2d, int w, int h, long time) {
        int x = w / 2;
        int y = h / 2 - 20;

        if (f1LogoImage != null && f1LogoImage.getWidth(this) > 0) {
            // Escalar dinámicamente para que ocupe un 30% del ancho de la pantalla (evita que sea gigante)
            int targetWidth = (int) (w * 0.30);
            int imgW = targetWidth;
            int imgH = (targetWidth * f1LogoImage.getHeight(this)) / f1LogoImage.getWidth(this);
            
            int drawX = x - imgW / 2;
            int drawY = y - imgH / 2;

            // Extrusión reducida para FPS (de 15 a 8)
            int depth = 8;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f * globalAlpha));
            for (int i = depth; i >= 0; i--) {
                g2d.drawImage(f1LogoImage, drawX + i, drawY + i, imgW, imgH, this);
            }

            // Glow reducido para FPS
            double flicker = Math.sin(time * 0.02) * 0.1 + 0.9; 
            for (int i = 12; i > 0; i -= 4) {
                float alpha = (float) (0.05f * flicker * globalAlpha);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(f1LogoImage, drawX - i, drawY - i, imgW + i*2, imgH + i*2, this);
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));
            g2d.drawImage(f1LogoImage, drawX, drawY, imgW, imgH, this);
            
        } else {
            // Fallback (texto) si no hay imagen
            String text = "F1";
            g2d.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 240));
            FontMetrics fm = g2d.getFontMetrics();
            int textW = fm.stringWidth(text);
            int drawX = x - textW / 2;
            int drawY = y + fm.getAscent() / 3;

            g2d.setColor(new Color(20, 0, 0)); 
            for (int i = 8; i >= 0; i--) {
                g2d.drawString(text, drawX + i, drawY + i);
            }

            double flicker = Math.sin(time * 0.02) * 0.1 + 0.9; 
            for (int i = 15; i > 0; i -= 5) {
                float alpha = (float) (0.05f * flicker * globalAlpha);
                g2d.setColor(new Color(255, 0, 0, (int)(alpha * 255)));
                g2d.setStroke(new BasicStroke(i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawString(text, drawX, drawY);
            }
            
            g2d.setColor(new Color(225, 6, 0));
            g2d.drawString(text, drawX, drawY);
        }
    }



    private void drawBlinkingText(Graphics2D g2d, int w, int h, long time) {
        double alphaSine = (Math.sin(time * 0.005) + 1.0) / 2.0; 
        int baseAlpha = (int) (alphaSine * 200) + 55; 
        int finalAlpha = (int)(baseAlpha * globalAlpha);
        
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        String subtitle = "PRESIONA UNA TECLA O CLIC PARA CONTINUAR";
        int subX = (w - fm.stringWidth(subtitle)) / 2;
        int subY = h - 150;
        
        g2d.setColor(new Color(0, 0, 0, finalAlpha));
        g2d.drawString(subtitle, subX + 3, subY + 3);
        
        g2d.setColor(new Color(255, 255, 255, finalAlpha));
        g2d.drawString(subtitle, subX, subY);
        
        int iconY = subY + 25;
        int iconX = w / 2;
        int offset1 = (int)(Math.sin(time * 0.01) * 6);
        int offset2 = (int)(Math.sin(time * 0.01 + Math.PI/2) * 6);
        int offset3 = (int)(Math.sin(time * 0.01 + Math.PI) * 6);
        
        g2d.setColor(new Color(225, 6, 0, finalAlpha));
        g2d.fillRoundRect(iconX - 12, iconY - offset1/2, 5, 12 + Math.abs(offset1), 3, 3);
        g2d.fillRoundRect(iconX - 2, iconY - offset2/2, 5, 12 + Math.abs(offset2), 3, 3);
        g2d.fillRoundRect(iconX + 8, iconY - offset3/2, 5, 12 + Math.abs(offset3), 3, 3);
    }

    private void lanzarConfeti(int w) {
        Color[] colors = {new Color(225, 6, 0), Color.WHITE, new Color(150, 150, 150), new Color(255, 180, 0)};
        for (int i = 0; i < 200; i++) { // Reducido para FPS
            particles.add(new ConfettiParticle(random.nextInt(w), -100 - random.nextInt(500), colors[random.nextInt(colors.length)]));
        }
    }

    private void updateAndDrawConfetti(Graphics2D g2d) {
        for (ConfettiParticle p : particles) {
            p.update();
            p.draw(g2d);
        }
    }

    private void iniciarFadeOut() {
        if (!fadingOut) {
            fadingOut = true;
        }
    }
    
    private void avanzarInmediato() {
        if (animationTimer != null) animationTimer.stop();
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private class ConfettiParticle {
        double x, y, speedY, speedX, size, rotationZ, rotationX, rotationY;
        double rotSpeedZ, rotSpeedX, rotSpeedY;
        Color color;

        public ConfettiParticle(int startX, int startY, Color color) {
            this.x = startX; this.y = startY; this.color = color;
            this.size = 6 + random.nextInt(10);
            this.speedY = 4 + random.nextDouble() * 5;
            this.speedX = -3 + random.nextDouble() * 6;
            this.rotationZ = random.nextDouble() * 360;
            this.rotationX = random.nextDouble() * 360;
            this.rotationY = random.nextDouble() * 360;
            this.rotSpeedZ = -8 + random.nextDouble() * 16;
            this.rotSpeedX = -12 + random.nextDouble() * 24;
            this.rotSpeedY = -12 + random.nextDouble() * 24;
        }

        public void update() {
            y += speedY;
            x += Math.sin(y * 0.015) * 3 + speedX; 
            rotationZ += rotSpeedZ; rotationX += rotSpeedX; rotationY += rotSpeedY;
        }

        public void draw(Graphics2D g2d) {
            AffineTransform old = g2d.getTransform();
            double scaleX = Math.abs(Math.cos(Math.toRadians(rotationY)));
            double scaleY = Math.abs(Math.cos(Math.toRadians(rotationX)));
            g2d.translate(x, y);
            g2d.rotate(Math.toRadians(rotationZ));
            g2d.scale(Math.max(0.1, scaleX), Math.max(0.1, scaleY)); 
            
            g2d.setColor(color); // Optimizada la sombra individual para FPS
            g2d.fillRect((int)(-size/2), (int)(-size/2), (int)size, (int)(size*0.6));
            g2d.setTransform(old);
        }
    }
}
