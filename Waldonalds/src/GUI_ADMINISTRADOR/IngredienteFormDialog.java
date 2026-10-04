package GUI_ADMINISTRADOR;

import CRUD.IngredienteCRUD;
import Modelos.Ingrediente;
import java.awt.Color;
import java.awt.Window;
import javax.swing.JDialog;

/** Ventana modal; el contenido editable vive en IngredienteFormPanel.form. */
@SuppressWarnings("serial")
public final class IngredienteFormDialog extends JDialog {

    private boolean guardado;

    private IngredienteFormDialog(Window propietario, IngredienteCRUD crud,
            Ingrediente ingrediente) {
        super(propietario, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        IngredienteFormPanel formulario =
                new IngredienteFormPanel(crud, ingrediente);
        formulario.addPropertyChangeListener(evento -> {
            if (IngredienteFormPanel.EVENTO_GUARDADO.equals(
                    evento.getPropertyName())) {
                guardado = true;
                dispose();
            } else if (IngredienteFormPanel.EVENTO_CANCELAR.equals(
                    evento.getPropertyName())) {
                dispose();
            }
        });
        setContentPane(formulario);
        pack();
        setLocationRelativeTo(propietario);
    }

    public static boolean mostrar(Window propietario, IngredienteCRUD crud,
            Ingrediente ingrediente) {
        IngredienteFormDialog dialogo = new IngredienteFormDialog(
                propietario, crud, ingrediente);
        dialogo.setVisible(true);
        return dialogo.guardado;
    }
}
