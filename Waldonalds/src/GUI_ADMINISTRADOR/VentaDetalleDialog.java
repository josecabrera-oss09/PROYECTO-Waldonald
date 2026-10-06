package GUI_ADMINISTRADOR;

import DAO.VentaDetalleDAO;
import java.awt.Color;
import java.awt.Window;
import javax.swing.JDialog;

/** Ventana modal que muestra una venta completa sin permitir modificaciones. */
@SuppressWarnings("serial")
public final class VentaDetalleDialog extends JDialog {

    private VentaDetalleDialog(
            Window propietario, VentaDetalleDAO.Venta venta) {
        super(propietario, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        VentaDetalleFormPanel contenido = new VentaDetalleFormPanel(venta);
        contenido.addPropertyChangeListener(evento -> {
            if (VentaDetalleFormPanel.EVENTO_CERRAR.equals(
                    evento.getPropertyName())) {
                dispose();
            }
        });
        setContentPane(contenido);
        pack();
        setLocationRelativeTo(propietario);
    }

    public static void mostrar(
            Window propietario, VentaDetalleDAO.Venta venta) {
        new VentaDetalleDialog(propietario, venta).setVisible(true);
    }
}
