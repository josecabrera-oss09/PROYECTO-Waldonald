import GUI_ADMINISTRADOR.MenuAdmin;
import Componentes.BotonMenuLateral;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;

public class MenuAnimacionTest {
    static MenuAdmin menu;
    static Object campo(String n) throws Exception { var f=MenuAdmin.class.getDeclaredField(n); f.setAccessible(true); return f.get(menu); }
    static void llamar(String n) throws Exception { var m=MenuAdmin.class.getDeclaredMethod(n);m.setAccessible(true);m.invoke(menu); }
    static void edt(Runnable r) throws Exception { SwingUtilities.invokeAndWait(r); }
    static void espera() throws Exception { Thread.sleep(450); edt(()->{}); }
    static void verificar(boolean ok,String m) { if(!ok) throw new AssertionError(m); }
    static void capturar(String nombre) throws Exception {
        Container c=menu.getContentPane();
        BufferedImage b=new BufferedImage(c.getWidth(),c.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=b.createGraphics();c.printAll(g);g.dispose();
        File salida=new File("build/reportes-check/"+nombre+".png");
        salida.getParentFile().mkdirs();
        ImageIO.write(b,"png",salida);
    }
    static void ancho(boolean abierto) throws Exception {
        JPanel lateral=(JPanel)campo("panelLateral");
        int esperado=abierto?340:90;
        verificar(Math.abs(lateral.getWidth()-esperado)<=2,"Ancho lateral incorrecto: "+lateral.getWidth()+" esperado "+esperado);
        verificar(Math.abs((double)campo("aperturaMenu")-(abierto?1:0))<0.0001,"Estado final");
        verificar(!((BotonMenuLateral)campo("botonReportes")).getText().isBlank(),"Texto accesible conservado");
    }
    public static void main(String[] args) throws Exception {
        edt(()->{menu=new MenuAdmin();menu.setExtendedState(JFrame.NORMAL);menu.setSize(1920,1080);menu.setVisible(true);}); espera();
        edt(()->{try {
            ((JButton)campo("botonUsuarios")).doClick();
            capturar("usuarios-completo");
            ((JButton)campo("botonGestionMenu")).doClick();
            capturar("gestion-menu-completo");
            ((JButton)campo("botonReportes")).doClick();
            ancho(true); capturar("menu-abierto"); llamar("alternarMenuLateral");
        }catch(Exception ex){throw new RuntimeException(ex);}}); espera();
        edt(()->{try { ancho(false);capturar("menu-cerrado");llamar("alternarMenuLateral");}catch(Exception ex){throw new RuntimeException(ex);}}); espera();
        edt(()->{try {ancho(true);capturar("menu-abierto-final");llamar("alternarMenuLateral");}catch(Exception ex){throw new RuntimeException(ex);}});
        Thread.sleep(90);
        edt(()->{try {llamar("alternarMenuLateral");}catch(Exception ex){throw new RuntimeException(ex);}}); espera();
        edt(()->{try {ancho(true);menu.dispose();}catch(Exception ex){throw new RuntimeException(ex);}});
        System.out.println("OK: abrir/cerrar, invertir durante animación y conservar textos accesibles sin escalado.");
        System.exit(0);
    }
}
