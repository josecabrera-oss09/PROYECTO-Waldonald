package GUI_ADMINISTRADOR;

import Componentes.BotonRedondeado;
import DAO.ConfiguracionMenuDAO;
import Modelos.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Editor administrativo del producto: receta y presentaciones del menú. */
public final class ConfiguracionMenuAdminDialog extends JDialog {
    private static final Color AZUL = new Color(0,20,43);
    private static final Color AMARILLO = new Color(255,188,0);
    private static final Color BORDE = new Color(225,229,235);
    private final Producto producto;
    private final ConfiguracionMenuDAO dao = new ConfiguracionMenuDAO();
    private ConfiguracionProducto configuracion;
    private final DefaultListModel<PresentacionMenu> modeloPresentaciones = new DefaultListModel<>();
    private final DefaultListModel<GrupoMenu> modeloGrupos = new DefaultListModel<>();
    private final DefaultListModel<OpcionMenu> modeloOpciones = new DefaultListModel<>();
    private final DefaultListModel<ComponenteMenu> modeloComponentes = new DefaultListModel<>();
    private final DefaultListModel<IngredienteProducto> modeloIngredientes = new DefaultListModel<>();
    private final JList<PresentacionMenu> presentaciones = new JList<>(modeloPresentaciones);
    private final JList<GrupoMenu> grupos = new JList<>(modeloGrupos);
    private final JList<OpcionMenu> opciones = new JList<>(modeloOpciones);
    private final JList<ComponenteMenu> componentes = new JList<>(modeloComponentes);
    private final JList<IngredienteProducto> ingredientes = new JList<>(modeloIngredientes);

    public ConfiguracionMenuAdminDialog(Window propietario, Producto producto) {
        super(propietario, "Opciones y receta", ModalityType.APPLICATION_MODAL);
        this.producto = producto;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 700));
        setContentPane(crearContenido());
        conectarEventos();
        try {
            dao.asegurarPresentacionIndividual(producto.getIdProducto());
        } catch (SQLException ex) {
            error(ex);
        }
        recargar();
        setLocationRelativeTo(propietario);
    }

    private JComponent crearContenido() {
        JPanel raiz = new JPanel(new BorderLayout(0,12));
        raiz.setBackground(new Color(247,248,250));
        raiz.setBorder(new EmptyBorder(18,18,18,18));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(AZUL);
        cabecera.setBorder(new EmptyBorder(17,20,17,20));
        JLabel titulo = new JLabel("Configurar " + producto.getNombre());
        titulo.setFont(new Font("SansSerif",Font.BOLD,24)); titulo.setForeground(Color.WHITE);
        JLabel ayuda = new JLabel("Crea Individual, Menú, Infantil o Combo; luego define sus elecciones y productos internos.");
        ayuda.setForeground(new Color(219,226,233));
        JPanel textos = new JPanel(); textos.setOpaque(false); textos.setLayout(new BoxLayout(textos,BoxLayout.Y_AXIS));
        textos.add(titulo); textos.add(ayuda); cabecera.add(textos);
        raiz.add(cabecera,BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("SansSerif",Font.BOLD,14));
        pestanas.addTab("Presentaciones y opciones", crearPresentaciones());
        pestanas.addTab("Ingredientes y personalización", crearIngredientes());
        raiz.add(pestanas,BorderLayout.CENTER);
        BotonRedondeado cerrar = boton("Cerrar",AMARILLO,AZUL);
        cerrar.addActionListener(e -> dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pie.setOpaque(false); pie.add(cerrar);
        raiz.add(pie,BorderLayout.SOUTH);
        return raiz;
    }

    private JComponent crearPresentaciones() {
        JPanel panel = new JPanel(new GridLayout(1,4,10,0));
        panel.setBackground(Color.WHITE); panel.setBorder(new EmptyBorder(12,12,12,12));
        panel.add(columna("1. Presentaciones",presentaciones,
                botonAccion("Nueva",e->editarPresentacion(null)),
                botonAccion("Editar",e->editarPresentacion(presentaciones.getSelectedValue())),
                botonAccion("Desactivar",e->desactivarPresentacion())));
        panel.add(columna("2. Grupos de elección",grupos,
                botonAccion("Nuevo",e->editarGrupo(null)),
                botonAccion("Editar",e->editarGrupo(grupos.getSelectedValue())),
                botonAccion("Desactivar",e->desactivarGrupo())));
        panel.add(columna("3. Opciones",opciones,
                botonAccion("Nueva",e->editarOpcion(null)),
                botonAccion("Editar",e->editarOpcion(opciones.getSelectedValue())),
                botonAccion("Desactivar",e->desactivarOpcion())));
        panel.add(columna("4. Productos internos",componentes,
                botonAccion("Agregar",e->agregarComponente()),
                botonAccion("Quitar",e->quitarComponente())));
        componentes.setCellRenderer((list,value,index,selected,focus) -> etiquetaLista(
                value.nombre()+"  x"+value.cantidad(),selected));
        opciones.setCellRenderer((list,value,index,selected,focus) -> etiquetaLista(value.toString(),selected));
        return panel;
    }

    private JComponent crearIngredientes() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setBackground(Color.WHITE); panel.setBorder(new EmptyBorder(18,18,18,18));
        JLabel texto = new JLabel("Estos ingredientes controlan inventario y generan las opciones Sin / Extra del cajero.");
        texto.setFont(new Font("SansSerif",Font.PLAIN,14)); texto.setForeground(new Color(92,103,124));
        panel.add(texto,BorderLayout.NORTH);
        ingredientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientes.setFixedCellHeight(52);
        ingredientes.setCellRenderer((list,value,index,selected,focus) -> etiquetaLista(
                value.nombre()+" · base "+value.cantidadDefault()+
                (value.permiteQuitar()?" · se puede quitar":"")+
                (value.permiteExtra()?" · extra Q"+value.precioExtra():""),selected));
        JScrollPane scrollIngredientes = new JScrollPane(ingredientes);
        Componentes.DesplazamientoSuave.ocultarBarras(scrollIngredientes);
        panel.add(scrollIngredientes,BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); acciones.setOpaque(false);
        acciones.add(botonAccion("Agregar ingrediente",e->editarIngrediente(null)));
        acciones.add(botonAccion("Editar",e->editarIngrediente(ingredientes.getSelectedValue())));
        acciones.add(botonAccion("Desactivar",e->desactivarIngrediente()));
        panel.add(acciones,BorderLayout.SOUTH);
        return panel;
    }

    private JPanel columna(String titulo, JList<?> lista, JButton... botones) {
        JPanel panel = new JPanel(new BorderLayout(0,8)); panel.setOpaque(false);
        JLabel etiqueta = new JLabel(titulo); etiqueta.setFont(new Font("SansSerif",Font.BOLD,14)); etiqueta.setForeground(AZUL);
        panel.add(etiqueta,BorderLayout.NORTH);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(44); lista.setFont(new Font("SansSerif",Font.PLAIN,13));
        JScrollPane scroll = new JScrollPane(lista);
        Componentes.DesplazamientoSuave.ocultarBarras(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        panel.add(scroll,BorderLayout.CENTER);
        JPanel acciones = new JPanel(new GridLayout(0,1,0,5)); acciones.setOpaque(false);
        for(JButton boton:botones) acciones.add(boton);
        panel.add(acciones,BorderLayout.SOUTH);
        return panel;
    }

    private JLabel etiquetaLista(String texto, boolean seleccionado) {
        JLabel etiqueta = new JLabel(texto); etiqueta.setOpaque(true);
        etiqueta.setBorder(new EmptyBorder(0,9,0,9));
        etiqueta.setBackground(seleccionado?new Color(255,243,203):Color.WHITE);
        etiqueta.setForeground(AZUL); etiqueta.setFont(new Font("SansSerif",Font.PLAIN,13));
        return etiqueta;
    }

    private BotonRedondeado boton(String texto, Color fondo, Color frente) {
        BotonRedondeado b=new BotonRedondeado(); b.setText(texto); b.setDegradado(false);
        b.setColorInicio(fondo); b.setColorFinal(fondo); b.setColorBorde(fondo.equals(Color.WHITE)?BORDE:fondo);
        b.setGrosorBorde(1f); b.setRadio(14); b.setForeground(frente);
        b.setFont(new Font("SansSerif",Font.BOLD,13)); b.setPreferredSize(new Dimension(180,42)); return b;
    }
    private JButton botonAccion(String texto, java.awt.event.ActionListener accion) {
        BotonRedondeado b=boton(texto,Color.WHITE,AZUL); b.addActionListener(accion); return b;
    }

    private void conectarEventos() {
        presentaciones.addListSelectionListener(e->{ if(!e.getValueIsAdjusting()) cargarGrupos(); });
        grupos.addListSelectionListener(e->{ if(!e.getValueIsAdjusting()) cargarOpciones(); });
        opciones.addListSelectionListener(e->{ if(!e.getValueIsAdjusting()) cargarComponentes(); });
    }

    private void recargar() {
        try {
            configuracion=dao.cargarProductoAdmin(producto.getIdProducto());
            modeloPresentaciones.clear(); for(PresentacionMenu p:configuracion.presentaciones()) modeloPresentaciones.addElement(p);
            if(!modeloPresentaciones.isEmpty()) presentaciones.setSelectedIndex(0);
            modeloIngredientes.clear(); for(IngredienteProducto i:dao.cargarIngredientes(producto.getIdProducto())) modeloIngredientes.addElement(i);
        } catch(Exception ex) { error(ex); }
    }
    private void cargarGrupos() {
        modeloGrupos.clear(); modeloOpciones.clear(); modeloComponentes.clear();
        PresentacionMenu p=presentaciones.getSelectedValue(); if(p==null)return;
        for(GrupoMenu g:p.grupos()) modeloGrupos.addElement(g);
        if(!modeloGrupos.isEmpty()) grupos.setSelectedIndex(0);
    }
    private void cargarOpciones() {
        modeloOpciones.clear(); modeloComponentes.clear(); GrupoMenu g=grupos.getSelectedValue(); if(g==null)return;
        for(OpcionMenu o:g.opciones()) modeloOpciones.addElement(o);
        if(!modeloOpciones.isEmpty()) opciones.setSelectedIndex(0);
    }
    private void cargarComponentes() {
        modeloComponentes.clear(); OpcionMenu o=opciones.getSelectedValue(); if(o==null)return;
        for(ComponenteMenu c:o.componentes()) modeloComponentes.addElement(c);
    }

    private void editarPresentacion(PresentacionMenu original) {
        JTextField nombre=new JTextField(original==null?"":original.nombre());
        JComboBox<String> tipo=new JComboBox<>(new String[]{"INDIVIDUAL","MENU","INFANTIL","COMBO"});
        JTextField precio=new JTextField(original==null?producto.getPrecioBase().toPlainString():original.precio().toPlainString());
        JCheckBox predeterminada=new JCheckBox("Presentación predeterminada",original!=null&&original.predeterminada());
        if(original!=null)tipo.setSelectedItem(original.tipo());
        if(!confirmarFormulario("Presentación",new String[]{"Nombre","Tipo","Precio (Q)",""},nombre,tipo,precio,predeterminada))return;
        try { validarTexto(nombre.getText(),"nombre"); dao.guardarPresentacion(original==null?null:original.idPresentacion(),producto.getIdProducto(),nombre.getText().trim(),(String)tipo.getSelectedItem(),decimal(precio.getText()),predeterminada.isSelected()); recargar(); }
        catch(Exception ex){error(ex);}
    }
    private void editarGrupo(GrupoMenu original) {
        PresentacionMenu p=presentaciones.getSelectedValue(); if(p==null){aviso("Selecciona una presentación.");return;}
        JTextField nombre=new JTextField(original==null?"":original.nombre());
        JSpinner minimo=new JSpinner(new SpinnerNumberModel(original==null?1:original.minimo(),0,20,1));
        JSpinner maximo=new JSpinner(new SpinnerNumberModel(original==null?1:original.maximo(),1,20,1));
        JCheckBox repetir=new JCheckBox("Permitir repetir opciones",original!=null&&original.permiteRepetir());
        JCheckBox visible=new JCheckBox("Mostrar al cajero",original==null||original.visible());
        JCheckBox personalizar=new JCheckBox("Permitir personalizar productos internos",original==null||original.permitePersonalizar());
        if(!confirmarFormulario("Grupo de elección",new String[]{"Nombre","Mínimo","Máximo","","",""},nombre,minimo,maximo,repetir,visible,personalizar))return;
        try { int min=(Integer)minimo.getValue(),max=(Integer)maximo.getValue(); if(min>max)throw new IllegalArgumentException("El mínimo no puede superar al máximo."); validarTexto(nombre.getText(),"nombre"); dao.guardarGrupo(original==null?null:original.idGrupo(),p.idPresentacion(),nombre.getText().trim(),min,max,repetir.isSelected(),visible.isSelected(),personalizar.isSelected()); recargar(); }
        catch(Exception ex){error(ex);}
    }
    private void editarOpcion(OpcionMenu original) {
        GrupoMenu g=grupos.getSelectedValue(); if(g==null){aviso("Selecciona un grupo.");return;}
        JTextField nombre=new JTextField(original==null?"":original.nombre());
        JTextField precio=new JTextField(original==null?"0.00":original.incrementoPrecio().toPlainString());
        JCheckBox predeterminada=new JCheckBox("Seleccionada inicialmente",original!=null&&original.predeterminada());
        if(!confirmarFormulario("Opción",new String[]{"Nombre","Incremento (Q)",""},nombre,precio,predeterminada))return;
        try { validarTexto(nombre.getText(),"nombre"); dao.guardarOpcion(original==null?null:original.idOpcion(),g.idGrupo(),nombre.getText().trim(),decimal(precio.getText()),predeterminada.isSelected()); recargar(); }
        catch(Exception ex){error(ex);}
    }
    private void agregarComponente() {
        OpcionMenu o=opciones.getSelectedValue(); if(o==null){aviso("Selecciona una opción.");return;}
        try {
            JComboBox<Item> productos=new JComboBox<>(); for(var e:dao.listarProductos().entrySet())productos.addItem(new Item(e.getKey(),e.getValue()));
            JSpinner cantidad=new JSpinner(new SpinnerNumberModel(1,1,20,1));
            if(!confirmarFormulario("Producto interno",new String[]{"Producto","Cantidad"},productos,cantidad))return;
            Item elegido=(Item)productos.getSelectedItem(); if(elegido==null)return;
            dao.agregarComponente(o.idOpcion(),elegido.id(),(Integer)cantidad.getValue()); recargar();
        }catch(Exception ex){error(ex);}
    }
    private void quitarComponente() {
        OpcionMenu o=opciones.getSelectedValue(); ComponenteMenu c=componentes.getSelectedValue();
        if(o==null||c==null){aviso("Selecciona un producto interno.");return;}
        try{dao.quitarComponente(o.idOpcion(),c.idProducto());recargar();}catch(Exception ex){error(ex);}
    }

    private void editarIngrediente(IngredienteProducto original) {
        try {
            JComboBox<Item> lista=new JComboBox<>(); for(var e:dao.listarIngredientes().entrySet())lista.addItem(new Item(e.getKey(),e.getValue()));
            if(original!=null)for(int i=0;i<lista.getItemCount();i++)if(lista.getItemAt(i).id()==original.idIngrediente())lista.setSelectedIndex(i);
            JTextField base=new JTextField(original==null?"1":original.cantidadDefault().toPlainString());
            JCheckBox quitar=new JCheckBox("Se puede quitar",original!=null&&original.permiteQuitar());
            JCheckBox extra=new JCheckBox("Se puede agregar extra",original!=null&&original.permiteExtra());
            JTextField cantidadExtra=new JTextField(original==null?"1":original.cantidadExtra().toPlainString());
            JTextField precioExtra=new JTextField(original==null?"0.00":original.precioExtra().toPlainString());
            JSpinner maximo=new JSpinner(new SpinnerNumberModel(original==null?1:original.maxExtras(),1,20,1));
            if(!confirmarFormulario("Ingrediente del producto",new String[]{"Ingrediente","Cantidad normal","","","Cantidad por extra","Precio por extra (Q)","Máximo de extras"},lista,base,quitar,extra,cantidadExtra,precioExtra,maximo))return;
            Item elegido=(Item)lista.getSelectedItem();
            dao.guardarIngredienteProducto(original==null?null:original.idProductoIngrediente(),producto.getIdProducto(),elegido.id(),decimal(base.getText()),quitar.isSelected(),extra.isSelected(),decimal(cantidadExtra.getText()),decimal(precioExtra.getText()),(Integer)maximo.getValue()); recargar();
        }catch(Exception ex){error(ex);}
    }

    private void desactivarPresentacion(){PresentacionMenu p=presentaciones.getSelectedValue();if(p!=null)desactivar("presentacion_menu","id_presentacion",p.idPresentacion());}
    private void desactivarGrupo(){GrupoMenu g=grupos.getSelectedValue();if(g!=null)desactivar("grupo_presentacion","id_grupo",g.idGrupo());}
    private void desactivarOpcion(){OpcionMenu o=opciones.getSelectedValue();if(o!=null)desactivar("opcion_grupo","id_opcion",o.idOpcion());}
    private void desactivar(String tabla,String campo,int id){try{dao.desactivar(tabla,campo,id);recargar();}catch(Exception ex){error(ex);}}
    private void desactivarIngrediente(){IngredienteProducto i=ingredientes.getSelectedValue();if(i==null)return;try{dao.desactivarIngredienteProducto(i.idProductoIngrediente());recargar();}catch(Exception ex){error(ex);}}

    private boolean confirmarFormulario(String titulo,String[] etiquetas,JComponent...campos){
        JPanel panel=new JPanel(new GridLayout(0,1,3,5));
        for(int i=0;i<campos.length;i++){if(i<etiquetas.length&&!etiquetas[i].isBlank())panel.add(new JLabel(etiquetas[i]));panel.add(campos[i]);}
        return JOptionPane.showConfirmDialog(this,panel,titulo,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
    }
    private BigDecimal decimal(String texto){try{BigDecimal d=new BigDecimal(texto.trim().replace(',','.'));if(d.signum()<0||d.stripTrailingZeros().scale()>2)throw new NumberFormatException();return d.setScale(2);}catch(Exception ex){throw new IllegalArgumentException("Ingresa cantidades válidas con hasta dos decimales.");}}
    private void validarTexto(String texto,String campo){if(texto==null||texto.isBlank())throw new IllegalArgumentException("Ingresa el "+campo+".");}
    private void aviso(String texto){JOptionPane.showMessageDialog(this,texto,"Configuración",JOptionPane.INFORMATION_MESSAGE);}
    private void error(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"No se pudo guardar",JOptionPane.ERROR_MESSAGE);}
    private record Item(int id,String nombre){@Override public String toString(){return nombre;}}
}
