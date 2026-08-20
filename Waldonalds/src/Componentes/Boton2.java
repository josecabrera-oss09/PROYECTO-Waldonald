package Componentes;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.Cursor;

public class Boton2 extends JButton {

    private Font fuentePersonalizada;
    
    private Timer timerAnimacion;
    private int anchoOriginal = -1;
    private int altoOriginal = -1;
    private int xOriginal = -1;
    private int yOriginal = -1;
    
    private final int incrementoMaximo = 26; 
    private final int paso = 4; 

    public Boton2() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        // Se modifica el margen derecho (de 25 a 55) y el izquierdo (de 25 a 15) para desplazar el texto hacia la izquierda
        setBorder(new EmptyBorder(10, 15, 10, 55));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        this.fuentePersonalizada = new Font("Horizon", Font.BOLD, 14);
        super.setFont(this.fuentePersonalizada);
        
        agregarEfectoZoom();
    }

    private void agregarEfectoZoom() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (anchoOriginal == -1) {
                    anchoOriginal = getWidth();
                    altoOriginal = getHeight();
                    xOriginal = getX();
                    yOriginal = getY();
                }

                int anchoObjetivo = anchoOriginal + incrementoMaximo;
                int altoObjetivo = altoOriginal + (incrementoMaximo * altoOriginal / anchoOriginal);

                if (timerAnimacion != null && timerAnimacion.isRunning()) {
                    timerAnimacion.stop();
                }

                timerAnimacion = new Timer(10, ev -> {
                    int w = getWidth();
                    int h = getHeight();
                    if (w < anchoObjetivo) {
                        int nuevoW = Math.min(w + paso, anchoObjetivo);
                        int nuevoH = Math.min(h + paso, altoObjetivo);
                        int nuevoX = getX() - (nuevoW - w) / 2;
                        int nuevoY = getY() - (nuevoH - h) / 2;
                        setBounds(nuevoX, nuevoY, nuevoW, nuevoH);
                    } else {
                        timerAnimacion.stop();
                    }
                });
                timerAnimacion.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (anchoOriginal == -1) return;

                if (timerAnimacion != null && timerAnimacion.isRunning()) {
                    timerAnimacion.stop();
                }

                timerAnimacion = new Timer(10, ev -> {
                    int w = getWidth();
                    int h = getHeight();
                    if (w > anchoOriginal) {
                        int nuevoW = Math.max(w - paso, anchoOriginal);
                        int nuevoH = Math.max(h - paso, altoOriginal);
                        int nuevoX = getX() + (w - nuevoW) / 2;
                        int nuevoY = getY() + (h - nuevoH) / 2;
                        setBounds(nuevoX, nuevoY, nuevoW, nuevoH);
                    } else {
                        setBounds(xOriginal, yOriginal, anchoOriginal, altoOriginal);
                        timerAnimacion.stop();
                    }
                });
                timerAnimacion.start();
            }
        });
    }

    @Override
    public void setFont(Font font) {
        this.fuentePersonalizada = font;
        super.setFont(font);
        repaint(); 
    }

    @Override
    public Font getFont() {
        return this.fuentePersonalizada != null ? this.fuentePersonalizada : super.getFont();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setFont(getFont());

        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Point2D inicio = new Point2D.Float(0, 0);
        Point2D fin = new Point2D.Float(getWidth(), 0);
        
        // Distribución del degradado lineal horizontal
        float[] distribucion = {0.0f, 1.0f};

        // 🎨 COLORES EXACTOS EXTRAÍDOS DE TU IMAGEN
        Color colorInicio = new Color(103, 29, 225); // #671DE1 - Morado/Índigo izquierdo
        Color colorFin    = new Color(193, 58, 251); // #C13AFB - Violeta neón derecho

        LinearGradientPaint lgp = new LinearGradientPaint(inicio, fin, distribucion, new Color[]{colorInicio, colorFin});
        g2.setPaint(lgp);

        // Usamos getHeight() en los arcos para asegurar la forma exacta de cápsula redonda de la imagen
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        g2.dispose();
        super.paintComponent(g);
    }
}