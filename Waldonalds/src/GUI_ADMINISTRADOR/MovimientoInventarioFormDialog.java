package GUI_ADMINISTRADOR;

import DAO.InventarioDAO;
import Modelos.ArticuloInventario;
import java.awt.Color;
import java.awt.Window;
import javax.swing.JDialog;

/** Ventana modal; su diseño está en MovimientoInventarioFormPanel.form. */
@SuppressWarnings("serial")
public final class MovimientoInventarioFormDialog extends JDialog {

    private boolean guardado;

    private MovimientoInventarioFormDialog(Window propietario,
            InventarioDAO dao, ArticuloInventario articulo, String tipoFijo) {
        super(propietario, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        MovimientoInventarioFormPanel formulario =
                new MovimientoInventarioFormPanel(dao, articulo, tipoFijo);
        formulario.addPropertyChangeListener(evento -> {
            if (MovimientoInventarioFormPanel.EVENTO_GUARDADO.equals(
                    evento.getPropertyName())) {
                guardado = true;
                dispose();
            } else if (MovimientoInventarioFormPanel.EVENTO_CANCELAR.equals(
                    evento.getPropertyName())) {
                dispose();
            }
        });
        setContentPane(formulario);
        pack();
        setLocationRelativeTo(propietario);
    }

    public static boolean mostrar(Window propietario, InventarioDAO dao,
            ArticuloInventario articulo, String tipoFijo) {
        MovimientoInventarioFormDialog dialogo =
                new MovimientoInventarioFormDialog(
                        propietario, dao, articulo, tipoFijo);
        dialogo.setVisible(true);
        return dialogo.guardado;
    }
}
