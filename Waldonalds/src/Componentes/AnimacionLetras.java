package Componentes;

import java.awt.Font;
import java.beans.Beans;
import javax.swing.JLabel;
import javax.swing.Timer;

public class AnimacionLetras extends JLabel {

    private String textoAnimado = "¡Bienvenido a Waldonald's";
    private int velocidad = 80;

    private Timer timer;
    private int indice = 0;

    public AnimacionLetras() {

        // Texto visible mientras diseñas en NetBeans
        setText(textoAnimado);

        setFont(
                new Font(
                        "DM Sans",
                        Font.BOLD,
                        36
                )
        );
    }

    // =====================================================
    // CUANDO EL COMPONENTE APARECE EN PANTALLA
    // =====================================================

    @Override
    public void addNotify() {

        super.addNotify();

        /*
         * Si estamos ejecutando el programa,
         * inicia automáticamente la animación.
         *
         * Si estamos en el diseñador de NetBeans,
         * deja el texto completo visible.
         */
        if (!Beans.isDesignTime()) {

            Timer inicio = new Timer(200, e -> {

                ((Timer) e.getSource()).stop();

                iniciarAnimacion();
            });

            inicio.setRepeats(false);
            inicio.start();

        } else {

            setText(textoAnimado);
        }
    }

    // =====================================================
    // ANIMACIÓN
    // =====================================================

    public void iniciarAnimacion() {

        // Detener animación anterior si existe
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        indice = 0;

        // Borra el texto antes de iniciar
        setText("");

        timer = new Timer(velocidad, e -> {

            if (indice < textoAnimado.length()) {

                indice++;

                setText(
                        textoAnimado.substring(
                                0,
                                indice
                        )
                );

            } else {

                timer.stop();
            }
        });

        timer.start();
    }

    // =====================================================
    // REINICIAR ANIMACIÓN
    // =====================================================

    public void reiniciarAnimacion() {

        iniciarAnimacion();
    }

    // =====================================================
    // TEXTO
    // =====================================================

    public String getTextoAnimado() {

        return textoAnimado;
    }

    public void setTextoAnimado(String textoAnimado) {

        this.textoAnimado = textoAnimado;

        /*
         * En el diseñador muestra el texto completo.
         */
        if (Beans.isDesignTime()) {
            setText(textoAnimado);
        }
    }

    // =====================================================
    // VELOCIDAD
    // =====================================================

    public int getVelocidad() {

        return velocidad;
    }

    public void setVelocidad(int velocidad) {

        if (velocidad < 10) {
            velocidad = 10;
        }

        this.velocidad = velocidad;
    }
}