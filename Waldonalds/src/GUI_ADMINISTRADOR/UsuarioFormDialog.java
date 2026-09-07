package GUI_ADMINISTRADOR;

import CRUD.UsuarioCRUD;
import Modelos.Usuario;
import java.awt.Color;
import java.awt.Window;
import javax.swing.JDialog;

/**
 * Ventana modal mínima. Todo el contenido visible está en
 * UsuarioFormPanel.form para poder moverlo desde Design en NetBeans.
 */
@SuppressWarnings("serial")
public final class UsuarioFormDialog extends JDialog {

    private boolean guardado;

    private UsuarioFormDialog(
            Window propietario,
            UsuarioCRUD crud,
            Usuario usuario) {
        super(propietario, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        UsuarioFormPanel formulario = new UsuarioFormPanel(crud, usuario);
        formulario.addPropertyChangeListener(evento -> {
            if (UsuarioFormPanel.EVENTO_GUARDADO.equals(
                    evento.getPropertyName())) {
                guardado = true;
                dispose();
            } else if (UsuarioFormPanel.EVENTO_CANCELAR.equals(
                    evento.getPropertyName())) {
                dispose();
            }
        });

        setContentPane(formulario);
        pack();
        setLocationRelativeTo(propietario);
    }

    public static boolean mostrar(
            Window propietario,
            UsuarioCRUD crud,
            Usuario usuario) {
        UsuarioFormDialog dialogo = new UsuarioFormDialog(
                propietario, crud, usuario);
        dialogo.setVisible(true);
        return dialogo.guardado;
    }
}
