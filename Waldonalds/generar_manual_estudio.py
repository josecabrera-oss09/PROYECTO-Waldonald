from __future__ import annotations

import html
from pathlib import Path

from pypdf import PdfReader
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import Flowable, PageBreak, Paragraph, Spacer, Table, TableStyle

from generar_manual_completo import (
    FONT,
    FONT_BOLD,
    FONT_MEDIUM,
    GREEN,
    INK,
    LIGHT,
    MID,
    NAVY,
    NAVY_2,
    OFF_WHITE,
    RED,
    YELLOW,
    FlowDiagram,
    ManualDoc,
    P,
    code_block,
    esc,
    parse_schema,
    rich,
    styles,
    table,
    TABLE_DESCRIPTIONS,
)


ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "output" / "pdf" / "Manual_Estudio_Waldonalds.pdf"
PAGE_SIZE = landscape(A4)


styles.add(
    ParagraphStyle(
        name="StudyTitle",
        fontName=FONT_BOLD,
        fontSize=26,
        leading=30,
        textColor=colors.white,
        alignment=1,
        spaceAfter=7,
    )
)
styles.add(
    ParagraphStyle(
        name="StudySubtitle",
        fontName=FONT,
        fontSize=13,
        leading=18,
        textColor=colors.HexColor("#DBE7EF"),
        alignment=1,
    )
)
styles.add(
    ParagraphStyle(
        name="StudyCode",
        fontName="Courier",
        fontSize=5.8,
        leading=6.9,
        textColor=NAVY,
        borderColor=LIGHT,
        borderWidth=0.7,
        borderPadding=6,
        backColor=OFF_WHITE,
        spaceBefore=3,
        spaceAfter=5,
    )
)
styles.add(
    ParagraphStyle(
        name="StudyQ",
        fontName=FONT_BOLD,
        fontSize=8.3,
        leading=11,
        textColor=NAVY,
        spaceAfter=1,
    )
)
styles.add(
    ParagraphStyle(
        name="StudyA",
        fontName=FONT,
        fontSize=7.6,
        leading=10.2,
        textColor=INK,
        leftIndent=8,
        spaceAfter=4,
    )
)


class StudyCover(Flowable):
    def __init__(self, height=140 * mm):
        super().__init__()
        self.height = height

    def wrap(self, availWidth, availHeight):
        self.width = availWidth
        return availWidth, self.height

    def draw(self):
        c = self.canv
        c.saveState()
        c.setFillColor(NAVY)
        c.roundRect(0, 0, self.width, self.height, 18, stroke=0, fill=1)
        c.setFillColor(YELLOW)
        c.rect(0, 0, self.width, 7, stroke=0, fill=1)
        c.setFont(FONT_BOLD, 45)
        c.setFillColor(YELLOW)
        c.drawCentredString(self.width / 2, self.height - 55, "W")
        c.setFillColor(colors.white)
        c.setFont(FONT_BOLD, 27)
        c.drawCentredString(self.width / 2, self.height - 102, "GUÍA DE ESTUDIO DEL PROYECTO")
        c.setFont(FONT_BOLD, 23)
        c.drawCentredString(self.width / 2, self.height - 136, "Waldonald's POS")
        c.setFillColor(colors.HexColor("#DCE8F1"))
        c.setFont(FONT, 12)
        c.drawCentredString(self.width / 2, self.height - 181, "Código, arquitectura, lógica, consultas y base de datos")
        c.drawCentredString(self.width / 2, self.height - 202, "Preparación práctica para exposición y preguntas técnicas")
        c.setFillColor(colors.HexColor("#0C2B40"))
        for i in range(8):
            c.circle(self.width - 55 - i * 22, self.height - 37, 5 + i, stroke=0, fill=1)
        c.restoreState()


def decorate(canvas, doc):
    canvas.saveState()
    width, height = PAGE_SIZE
    if doc.page > 1:
        canvas.setFillColor(NAVY)
        canvas.rect(0, height - 16 * mm, width, 16 * mm, stroke=0, fill=1)
        canvas.setFillColor(YELLOW)
        canvas.rect(0, height - 16.8 * mm, width, 0.8 * mm, stroke=0, fill=1)
        canvas.setFillColor(colors.white)
        canvas.setFont(FONT_BOLD, 9)
        canvas.drawString(15 * mm, height - 10.3 * mm, "Waldonald's - Guía de estudio para la presentación")
        canvas.setFont(FONT, 7.5)
        canvas.drawRightString(width - 15 * mm, height - 10.3 * mm, "Java 21 | Swing | JDBC | MySQL")
    canvas.setStrokeColor(LIGHT)
    canvas.line(15 * mm, 10 * mm, width - 15 * mm, 10 * mm)
    canvas.setFillColor(MID)
    canvas.setFont(FONT, 7)
    canvas.drawString(15 * mm, 6 * mm, "Manual compacto basado en el código actual del proyecto")
    canvas.drawRightString(width - 15 * mm, 6 * mm, f"Página {doc.page}")
    canvas.restoreState()


def heading(story, title, subtitle=None):
    story.append(Paragraph(esc(title), styles["H1Manual"]))
    if subtitle:
        story.append(P(subtitle, "SmallManual"))


def new_page(story):
    story.append(PageBreak())


def bullets(story, items):
    for item in items:
        story.append(Paragraph("• " + esc(item), styles["BodyManual"]))


def excerpt(path: str, ranges: list[tuple[int, int]], max_chars=118) -> Paragraph:
    source = (ROOT / path).read_text(encoding="utf-8").splitlines()
    output = []
    for range_index, (start, end) in enumerate(ranges):
        if range_index:
            output.append("      ...")
        for line_number in range(start, min(end, len(source)) + 1):
            line = source[line_number - 1].replace("\t", "    ")
            if len(line) > max_chars:
                line = line[: max_chars - 3] + "..."
            output.append(f"{line_number:>4} | {line}")
    return Paragraph(
        html.escape("\n".join(output)).replace(" ", "&nbsp;").replace("\n", "<br/>"),
        styles["StudyCode"],
    )


def sql_block(text: str) -> Paragraph:
    return Paragraph(
        html.escape(text).replace(" ", "&nbsp;").replace("\n", "<br/>"),
        styles["StudyCode"],
    )


def explain_rows(items):
    return table(
        [["Línea o concepto", "Qué significa y por qué importa"]] + items,
        widths=[62 * mm, 178 * mm],
        font_size=6.8,
    )


def qa(story, question, answer):
    story.append(Paragraph(esc(question), styles["StudyQ"]))
    story.append(Paragraph(esc(answer), styles["StudyA"]))


def add_cover(story):
    story.append(Spacer(1, 4 * mm))
    story.append(StudyCover())
    story.append(Spacer(1, 8 * mm))
    story.append(table([
        ["Objetivo", "Entender y defender el proyecto, no memorizar cada línea."],
        ["Enfoque", "Arquitectura, lógica, SQL, base de datos y código crítico."],
        ["Cómo estudiarlo", "Leer 1-21, practicar 22-24 y responder 25-27 sin ver las respuestas."],
    ], widths=[42 * mm, 150 * mm], header=False, font_size=8.5, repeat=0))
    new_page(story)


def add_page_2(story):
    heading(story, "1. Qué deben saber del proyecto", "La respuesta de 30 segundos y el mapa de estudio.")
    story.append(P("Waldonald's es un sistema POS de escritorio para restaurante. Permite autenticar usuarios, mostrar productos según horario, configurar productos individuales, WlMenús, Cajitas y combos, personalizar cada componente, cobrar, descontar inventario y consultar reportes administrativos."))
    story.append(P("La idea técnica principal es que un combo no es un producto plano: es una presentación formada por grupos, opciones y productos internos. Cada producto interno conserva sus propias modificaciones." , "Callout"))
    story.append(table([
        ["Tecnología", "Uso en el proyecto", "Archivo o carpeta"],
        ["Java 21", "Lenguaje y records para modelos inmutables.", "src/"],
        ["Swing", "Ventanas, paneles, botones, tablas y eventos.", "GUI_CAJERO / GUI_ADMINISTRADOR"],
        ["NetBeans GUI Builder", "Diseño editable de formularios; genera initComponents.", "archivos .form"],
        ["JDBC", "Conexión y consultas parametrizadas a MySQL.", "Conexion, DAO, CRUD"],
        ["MySQL", "Persistencia de catálogo, configuración, pedidos e inventario.", "sql/"],
        ["Ant", "Compilación y ejecución del proyecto NetBeans.", "build.xml / nbproject"],
    ], widths=[42 * mm, 112 * mm, 86 * mm]))
    bullets(story, [
        "Roles: ADMINISTRADOR y CAJERO.",
        "Clase principal: Main.main.",
        "Base de datos: waldonalds, con 16 tablas.",
        "Patrón general: Vista -> Modelo -> DAO/CRUD -> MySQL.",
    ])
    new_page(story)


def add_page_3(story):
    heading(story, "2. Arquitectura y responsabilidades", "Quién hace qué y por qué no debe mezclarse todo en una ventana.")
    nodes = [
        ("ui", "VISTAS\nSwing", .03, .58, .16, .21, colors.white),
        ("m", "MODELOS\ndatos", .28, .58, .16, .21, YELLOW),
        ("d", "DAO / CRUD\nlógica + SQL", .53, .58, .18, .21, colors.white),
        ("db", "MYSQL\n16 tablas", .80, .58, .15, .21, NAVY),
        ("u", "UTILIDADES\nsesión, horario, seguridad, imágenes", .32, .14, .34, .18, colors.HexColor("#DFF3E9")),
    ]
    edges = [("ui", "m", "crea/lee"), ("m", "d", "transporta"), ("d", "db", "JDBC"), ("u", "ui", "apoya"), ("u", "d", "apoya")]
    story.append(FlowDiagram(nodes, edges, height=88 * mm, title="Capas del sistema"))
    story.append(table([
        ["Carpeta", "Responsabilidad", "Regla para explicarla"],
        ["GUI_CAJERO", "Interacción de venta y carrito.", "No decide por sí sola si el pago es válido."],
        ["GUI_ADMINISTRADOR", "Mantenimiento de usuarios, menú, inventario y reportes.", "Coordina formularios y refresca datos."],
        ["Modelos", "Representar productos, opciones, pedido y resultados.", "No contiene SQL ni dibuja pantallas."],
        ["DAO", "Casos de uso y consultas complejas.", "Devuelve modelos, maneja conexiones y transacciones."],
        ["CRUD", "Altas, lecturas, cambios y estado de catálogos.", "Centraliza validaciones administrativas."],
        ["Componentes", "Estilo visual reutilizable.", "Evita duplicar el mismo botón o tabla."],
        ["Utilidades", "Funciones compartidas.", "Sesión, horarios, hash, CSV, iconos e imágenes."],
    ], widths=[45 * mm, 100 * mm, 95 * mm]))
    new_page(story)


def add_page_4(story):
    heading(story, "3. Arranque de la aplicación", "Main.main, EDT y transición de la pantalla de carga al Login.")
    story.append(excerpt("src/Main/main.java", [(7, 29)]))
    story.append(explain_rows([
        ["7", "main es el punto de entrada que ejecuta la JVM."],
        ["8", "EventQueue.invokeLater coloca la creación de la interfaz en el hilo visual de Swing (EDT)."],
        ["12-14", "Crea, centra y muestra PantallaCarga."],
        ["17", "Timer espera 8000 ms sin bloquear la interfaz. El comentario dice 3 segundos, pero el valor real es 8; en una exposición deben reconocer esa inconsistencia."],
        ["20", "dispose libera la ventana de carga."],
        ["23-25", "Crea, centra y muestra Login."],
        ["28", "setRepeats(false) impide que el Login se abra repetidamente."],
    ]))
    story.append(P("Pregunta típica: ¿por qué no usar Thread.sleep? Porque bloquearía el EDT y congelaría la interfaz. Timer programa la acción manteniendo Swing sensible.", "Callout"))
    new_page(story)


def add_page_5(story):
    heading(story, "4. Inicio de sesión, seguridad y roles")
    story.append(excerpt("src/Login/Login.java", [(134, 153), (177, 183), (190, 207)]))
    story.append(explain_rows([
        ["134-143", "Consulta solo el usuario solicitado y exige estado=TRUE."],
        ["145", "try-with-resources cierra Connection y PreparedStatement incluso si ocurre un error."],
        ["147", "setString sustituye el ? de PreparedStatement; evita concatenar texto del usuario."],
        ["151-153", "Lee la fila y verifica la contraseña contra el hash almacenado."],
        ["177-183", "Extrae identidad mínima y la guarda en SesionUsuario para otras pantallas."],
        ["190-207", "El rol decide si se abre InicioAdminForm o Cajero; luego se cierra Login."],
    ]))
    story.append(P("Debilidad a reconocer: la conexión tiene credenciales en el código. Para producción deberían provenir de variables de entorno o configuración local no versionada." , "Callout"))
    new_page(story)


def add_page_6(story):
    heading(story, "5. Java y Swing que deben poder explicar")
    story.append(table([
        ["Concepto", "Significado dentro del proyecto", "Ejemplo"],
        ["class", "Molde mutable con atributos y métodos.", "Producto, Usuario"],
        ["record", "Modelo compacto e inmutable; Java crea accesores.", "LineaPedido, OpcionPedido"],
        ["constructor", "Valida y prepara un objeto al usar new.", "public LineaPedido {...}"],
        ["private/public", "Control de acceso dentro o fuera de la clase.", "private void confirmar"],
        ["final", "La referencia no puede reasignarse.", "private final Map..."],
        ["listener", "Reacciona a clic, tecla o cambio.", "addActionListener"],
        ["CardLayout", "Muestra un panel de varios sin abrir otra ventana.", "PedidoPanel, MenuAdmin"],
        ["SwingWorker", "Ejecuta consultas fuera del EDT para no congelar.", "paneles administrativos"],
        ["try/catch", "Maneja errores esperados.", "SQLException"],
        ["try-with-resources", "Cierra conexión, sentencia y ResultSet.", "try (Connection c=...)"],
        ["BigDecimal", "Dinero exacto; evita errores de double.", "precio, total, stock decimal"],
        ["List", "Colección ordenada.", "opciones, productos"],
        ["Map", "Relaciona una clave con un objeto.", "idLinea -> LineaPedido"],
        ["stream", "Procesa colecciones declarativamente.", "map + reduce del total"],
    ], widths=[40 * mm, 128 * mm, 72 * mm]))
    story.append(P("initComponents es código generado a partir del .form. Las marcas //GEN-BEGIN y //GEN-END permiten que NetBeans reconozca el bloque. Si se borran, el diseñador puede quedar en solo lectura." , "Callout"))
    new_page(story)


def add_page_7(story):
    heading(story, "6. Modelo relacional completo", "Las 16 tablas se agrupan en catálogo, menú configurable, ventas e inventario.")
    nodes = [
        ("cat", "CATÁLOGO\ncategoria\nproducto\ningrediente\nproducto_ingrediente", .03, .54, .23, .30, colors.white),
        ("menu", "MENÚ CONFIGURABLE\npresentacion_menu\ngrupo_presentacion\nopcion_grupo\nopcion_componente", .31, .54, .25, .30, YELLOW),
        ("sale", "PEDIDO\npedido\npedido_detalle\npedido_opcion\npedido_producto\npedido_modificacion", .62, .51, .29, .34, colors.white),
        ("aux", "APOYO\nusuario\nmovimiento_inventario\npago_operacion", .34, .12, .31, .20, colors.HexColor("#DFF3E9")),
    ]
    edges = [("cat", "menu", "define"), ("menu", "sale", "se vende como"), ("aux", "sale", "autoriza/registra"), ("sale", "cat", "descuenta stock")]
    story.append(FlowDiagram(nodes, edges, height=102 * mm, title="Mapa de la base de datos"))
    story.append(table([
        ["Tipo de clave", "Qué significa", "Ejemplo"],
        ["PRIMARY KEY", "Identificador único de cada fila.", "producto.id_producto"],
        ["FOREIGN KEY", "Apunta a una fila de otra tabla.", "producto.id_categoria -> categoria"],
        ["AUTO_INCREMENT", "MySQL genera el siguiente ID.", "pedido.id_pedido"],
        ["UNIQUE", "Impide repetir un valor.", "usuario.usuario, pago_operacion.id_pedido"],
        ["ENUM", "Limita un campo a opciones conocidas.", "tipo_stock, rol, tipo_servicio"],
        ["estado", "Borrado lógico; desactiva sin destruir historia.", "TRUE/FALSE"],
    ], widths=[45 * mm, 120 * mm, 75 * mm]))
    new_page(story)


def schema_page(story, title, names, tables_by_name):
    heading(story, title)
    rows = [["Tabla", "Para qué sirve", "Campos que deben reconocer", "Relaciones clave"]]
    for name in names:
        t = tables_by_name[name]
        fields = ", ".join(field[0] for field in t["fields"])
        relations = "; ".join(f"{a} -> {b}.{c}" for a, b, c in t["relations"]) or "Sin clave foránea"
        rows.append([name, TABLE_DESCRIPTIONS[name], fields, relations])
    story.append(table(rows, widths=[40 * mm, 80 * mm, 76 * mm, 44 * mm], font_size=6.1))
    story.append(Spacer(1, 4 * mm))


def add_menu_queries_page(story):
    heading(
        story,
        "8.1 Menú configurable: consultas y relaciones",
        "Cómo pasa el programa de una tarjeta del cajero a sus grupos, opciones, productos reales e ingredientes.",
    )
    story.append(table([
        ["Tabla", "Relación", "Campos decisivos", "Ejemplo sencillo"],
        ["presentacion_menu", "producto 1 -> N presentaciones", "tipo, precio, predeterminada, estado", "Big Mac: Individual o WlMenú"],
        ["grupo_presentacion", "presentación 1 -> N grupos", "minimo, maximo, permite_repetir, visible, permite_personalizar", "Elige una bebida"],
        ["opcion_grupo", "grupo 1 -> N opciones", "incremento_precio, predeterminada, estado", "McPatatas + Q7.00"],
        ["opcion_componente", "opción N <-> N producto", "id_opcion, id_producto, cantidad", "McPatatas -> producto McPatatas x1"],
    ], widths=[42 * mm, 51 * mm, 92 * mm, 55 * mm], font_size=6.2))
    story.append(P("El signo ? es un parámetro de PreparedStatement. Java lo sustituye con setInt o setString; no se concatena la entrada del usuario."))
    story.append(sql_block(
        "-- 1. Presentaciones del producto seleccionado\n"
        "SELECT id_presentacion,nombre,tipo,precio,predeterminada\n"
        "FROM presentacion_menu WHERE id_producto_principal=? AND estado=TRUE\n"
        "ORDER BY predeterminada DESC,id_presentacion;\n\n"
        "-- 2. Preguntas de cada presentación\n"
        "SELECT id_grupo,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar\n"
        "FROM grupo_presentacion WHERE id_presentacion=? AND estado=TRUE ORDER BY id_grupo;\n\n"
        "-- 3. Respuestas disponibles\n"
        "SELECT id_opcion,nombre,incremento_precio,predeterminada\n"
        "FROM opcion_grupo WHERE id_grupo=? AND estado=TRUE\n"
        "ORDER BY predeterminada DESC,id_opcion;\n\n"
        "-- 4. Productos reales que entrega la respuesta\n"
        "SELECT oc.id_producto,oc.cantidad,p.nombre,p.tipo_stock,p.personalizable\n"
        "FROM opcion_componente oc JOIN producto p ON p.id_producto=oc.id_producto\n"
        "WHERE oc.id_opcion=? ORDER BY oc.id_opcion_componente;"
    ))
    story.append(P(
        "Lectura mental: 'Quiero WlMenú' = presentación; 'elige bebida' = grupo; 'Coca-Cola' = opción; "
        "'Coca-Cola mediana x1' = componente. Si el componente es RECETA, el grupo permite personalizar y el producto es personalizable, "
        "se consulta producto_ingrediente para crear los controles SIN y EXTRA.",
        "Callout",
    ))
    new_page(story)


def add_order_queries_page(story):
    heading(
        story,
        "9.1 Pedido guardado: consultas y relaciones",
        "Cómo se convierte la configuración elegida en una venta permanente y consultable.",
    )
    story.append(table([
        ["Tabla", "Padre", "Qué conserva", "Uso principal"],
        ["pedido", "usuario", "servicio, pago, total, fecha y estado", "Cabecera de la venta"],
        ["pedido_detalle", "pedido + presentacion_menu", "nombre, presentación, cantidad, precio y subtotal", "Líneas y reportes de ventas"],
        ["pedido_opcion", "detalle + grupo + opción", "número, nombres e incremento", "Elecciones del cliente"],
        ["pedido_producto", "pedido_opcion + producto", "producto real y cantidad", "Cocina e inventario"],
        ["pedido_modificacion", "pedido_producto + producto_ingrediente", "SIN/EXTRA, ingrediente, cantidad y precio", "Preparación individual"],
    ], widths=[40 * mm, 55 * mm, 91 * mm, 54 * mm], font_size=6.2))
    story.append(P("PagoDAO obtiene la clave AUTO_INCREMENT después de cada INSERT y la usa como clave foránea del siguiente nivel:"))
    story.append(sql_block(
        "INSERT INTO pedido(numero_orden,id_usuario,tipo_servicio,estado,metodo_pago,monto_recibido,total)\n"
        "VALUES(0,?,?,'PAGADO',?,?,?);\n"
        "INSERT INTO pedido_detalle(id_pedido,id_presentacion,nombre,presentacion,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?,?,?);\n"
        "INSERT INTO pedido_opcion(id_detalle,id_grupo,id_opcion,numero,grupo,opcion,precio_extra) VALUES(?,?,?,?,?,?,?);\n"
        "INSERT INTO pedido_producto(id_pedido_opcion,id_producto,nombre,cantidad) VALUES(?,?,?,?);\n"
        "INSERT INTO pedido_modificacion(id_pedido_producto,id_producto_ingrediente,tipo,ingrediente,cantidad,precio_extra) VALUES(?,?,?,?,?,?);"
    ))
    story.append(sql_block(
        "-- Reconstruir un pedido completo\n"
        "SELECT pe.numero_orden,pd.nombre,pd.presentacion,po.grupo,po.opcion,\n"
        "       pp.nombre AS producto_interno,pm.tipo,pm.ingrediente,pm.precio_extra\n"
        "FROM pedido pe JOIN pedido_detalle pd ON pd.id_pedido=pe.id_pedido\n"
        "LEFT JOIN pedido_opcion po ON po.id_detalle=pd.id_detalle\n"
        "LEFT JOIN pedido_producto pp ON pp.id_pedido_opcion=po.id_pedido_opcion\n"
        "LEFT JOIN pedido_modificacion pm ON pm.id_pedido_producto=pp.id_pedido_producto\n"
        "WHERE pe.id_pedido=?;"
    ))
    story.append(Spacer(1, 3 * mm))
    story.append(P(
        "Los nombres y precios repetidos son copias históricas: renombrar Big Mac mañana no cambia el ticket de ayer. "
        "Cada unidad interna obtiene su propio pedido_producto; por eso dos hamburguesas iguales pueden tener modificaciones distintas. "
        "Los reportes suman pedido_detalle, no los componentes, para no contar un WlMenú como tres ventas.",
        "Callout",
    ))
    new_page(story)


def add_pages_8_11(story, tables):
    by_name = {t["name"]: t for t in tables}
    schema_page(story, "7. Tablas de catálogo y recetas", ["categoria", "producto", "ingrediente", "producto_ingrediente"], by_name)
    story.append(P("Ejemplo: Big Mac tiene tipo_stock=RECETA. Sus ingredientes y cantidades viven en producto_ingrediente. permite_quitar y permite_extra controlan la personalización; precio_extra y max_extras controlan el recargo y límite." , "Callout"))
    new_page(story)
    schema_page(story, "8. Tablas del menú configurable", ["presentacion_menu", "grupo_presentacion", "opcion_grupo", "opcion_componente"], by_name)
    story.append(P("Jerarquía: un producto tiene presentaciones; una presentación tiene grupos; un grupo tiene opciones; una opción entrega uno o más productos. Los campos minimo, maximo y permite_repetir son reglas, no simples textos." , "Callout"))
    new_page(story)
    add_menu_queries_page(story)
    schema_page(story, "9. Tablas que conservan el pedido", ["pedido", "pedido_detalle", "pedido_opcion", "pedido_producto", "pedido_modificacion"], by_name)
    story.append(P("pedido_detalle, pedido_opcion, pedido_producto y pedido_modificacion guardan nombres históricos. Es un snapshot: si el producto cambia después, la venta antigua sigue diciendo exactamente qué se cobró y preparó." , "Callout"))
    new_page(story)
    add_order_queries_page(story)
    schema_page(story, "10. Usuarios, inventario e idempotencia", ["usuario", "movimiento_inventario", "pago_operacion"], by_name)
    story.append(P("movimiento_inventario puede apuntar a producto o ingrediente. pago_operacion usa clave como identificador del intento lógico de cobro para que un reintento no duplique el pedido." , "Callout"))
    new_page(story)


def add_page_12(story):
    heading(story, "11. Cómo se representa Individual, WlMenú, Cajita y combo")
    story.append(table([
        ["Nivel", "Ejemplo", "Responsabilidad"],
        ["producto", "Big Mac", "Tarjeta que abre el cajero y producto que puede tener receta."],
        ["presentacion_menu", "Individual / WlMenú", "Precio base del formato de venta."],
        ["grupo_presentacion", "Elige complemento", "Exige mínimo y máximo de selecciones."],
        ["opcion_grupo", "WlPatatas +Q5", "Alternativa y recargo."],
        ["opcion_componente", "1 WlPatatas", "Producto real que entrega la opción."],
        ["producto_ingrediente", "Pepinillo", "Regla para SIN/EXTRA dentro de una hamburguesa."],
    ], widths=[48 * mm, 68 * mm, 124 * mm]))
    story.append(code_block("LineaPedido\n  -> OpcionPedido: Producto principal\n       -> ProductoPedido: Big Mac\n            -> ModificacionPedido: SIN Pepinillo\n  -> OpcionPedido: Complemento\n       -> ProductoPedido: WlPatatas\n  -> OpcionPedido: Bebida\n       -> ProductoPedido: Coca Cola"))
    story.append(P("Combo con dos hamburguesas: se crean dos ProductoPedido distintos, cada uno con idInterno y lista propia de modificaciones. Por eso una puede ir sin pepinillo y la otra con todo."))
    story.append(P("No se duplica el producto para crear WlMenú. Se añade otra presentacion_menu al mismo producto principal. Así la tarjeta puede mostrar pestañas Individual y WlMenú." , "Callout"))
    new_page(story)


def add_page_13(story):
    heading(story, "12. Cómo se carga una configuración de menú")
    story.append(excerpt("src/DAO/ConfiguracionMenuDAO.java", [(56, 71), (84, 96), (106, 118)]))
    story.append(explain_rows([
        ["56-71", "Consulta presentaciones, prioriza la predeterminada y construye cada una con sus grupos."],
        ["66-67", "Si un grupo obligatorio no tiene opciones válidas, la presentación se oculta al cajero."],
        ["84-96", "Lee mínimo, máximo, repetición, visibilidad y personalización; después carga opciones."],
        ["106-118", "Cada opción conserva recargo, selección predeterminada y productos componentes."],
    ]))
    story.append(P("El DAO convierte filas SQL en un árbol de modelos. La interfaz recibe ConfiguracionProducto ya estructurado y no ejecuta estas consultas." , "Callout"))
    new_page(story)


def add_page_14(story):
    heading(story, "13. Consulta que decide qué productos ve el cajero")
    story.append(excerpt("src/DAO/ProductoDAO.java", [(42, 63)]))
    story.append(explain_rows([
        ["COALESCE", "Usa el precio de una presentación activa; si no existe, cae a precio_base."],
        ["subconsulta", "Busca una presentación del mismo producto principal."],
        ["WHERE id_categoria=?", "Filtra por la categoría elegida; el valor se asigna con PreparedStatement."],
        ["estado=TRUE", "Oculta productos desactivados sin borrarlos."],
        ["EXISTS", "Exige al menos una presentación activa; evita tarjetas que no se pueden configurar."],
        ["ORDER BY ... LIMIT 1", "Prefiere la presentación predeterminada y devuelve solo una."],
    ]))
    story.append(P("Además del SQL, HorarioMenu valida disponibilidad_menu. TODO_DIA siempre; DESAYUNO y ALMUERZO dependen de la hora. Por eso un WlMuffin no debe aparecer de noche." , "Callout"))
    new_page(story)


def add_page_15(story):
    heading(story, "14. Modelo del carrito y cálculo del precio")
    story.append(excerpt("src/Modelos/LineaPedido.java", [(7, 38)]))
    story.append(explain_rows([
        ["record", "Representa una línea inmutable; dos configuraciones no se mezclan."],
        ["idLinea UUID", "Identifica la configuración exacta dentro del carrito, aunque sea el mismo producto."],
        ["List.copyOf", "Crea una copia inmutable de opciones para evitar cambios externos."],
        ["precio()", "Comienza con precioBase, suma recargos de opciones y recargos de modificaciones."],
        ["setScale(2)", "Mantiene dos decimales monetarios."],
        ["subtotal()", "precio configurado multiplicado por cantidad."],
        ["conCantidad", "Como el record es inmutable, devuelve otra LineaPedido con la nueva cantidad."],
    ]))
    new_page(story)


def add_page_16(story):
    heading(story, "15. Del configurador al carrito")
    story.append(excerpt("src/GUI_CAJERO/ConfiguradorProductoPanel.java", [(273, 298)]))
    story.append(excerpt("src/GUI_CAJERO/PedidoPanel.java", [(485, 503)]))
    story.append(explain_rows([
        ["actualizarPrecio", "Multiplica el precio actual de la presentación por la cantidad y actualiza el botón."],
        ["crearOpciones", "Convierte los controles seleccionados en OpcionPedido/ProductoPedido/ModificacionPedido."],
        ["alAgregar.accept", "Callback: entrega la LineaPedido terminada al panel del carrito."],
        ["Map idLinea -> línea", "Conserva configuraciones separadas; put agrega o reemplaza por ID."],
        ["cambiar", "Crea una copia con nueva cantidad o elimina si llega a cero."],
        ["suma", "stream + map + reduce suma todos los subtotales con BigDecimal."],
        ["actualizar", "Reconstruye filas, total, contador y estado de botones."],
    ]))
    new_page(story)


def add_page_17(story):
    heading(story, "16. Cobro: la transacción más importante")
    nodes = [
        ("req", "SolicitudPago", .02, .59, .14, .20, YELLOW),
        ("idem", "bloquear clave\nidempotente", .21, .59, .16, .20, colors.white),
        ("val", "revalidar reglas\ny usuario", .42, .59, .16, .20, colors.white),
        ("stock", "bloquear stock\nFOR UPDATE", .63, .59, .15, .20, colors.white),
        ("save", "guardar pedido\ny descontar", .83, .59, .14, .20, YELLOW),
        ("ok", "commit\nTODO queda guardado", .31, .15, .19, .18, colors.HexColor("#DFF3E9")),
        ("bad", "rollback\nNADA queda a medias", .58, .15, .19, .18, colors.HexColor("#FBE3E5")),
    ]
    edges = [("req", "idem", "1"), ("idem", "val", "2"), ("val", "stock", "3"), ("stock", "save", "4"), ("save", "ok", "éxito"), ("save", "bad", "error")]
    story.append(FlowDiagram(nodes, edges, height=90 * mm, title="Unidad atómica de cobro"))
    story.append(table([
        ["Concepto", "Por qué es necesario"],
        ["setAutoCommit(false)", "Nada se confirma automáticamente; el DAO controla la unidad completa."],
        ["FOR UPDATE", "Bloquea la fila durante la transacción para que otra caja no use el mismo stock."],
        ["commit", "Confirma pedido, detalle, elecciones, modificaciones y salidas juntos."],
        ["rollback", "Si una parte falla, revierte todo y evita pedidos sin inventario o inventario sin pedido."],
        ["idempotencia", "Reintentar la misma clave devuelve el comprobante previo y no cobra dos veces."],
    ], widths=[55 * mm, 185 * mm]))
    new_page(story)


def add_page_18(story):
    heading(story, "17. PagoDAO: líneas que deben saber defender")
    story.append(excerpt("src/DAO/PagoDAO.java", [(19, 35), (51, 70)]))
    story.append(explain_rows([
        ["20-22", "Abre conexión e inicia la transacción."],
        ["26-27", "INSERT ... ON DUPLICATE KEY crea o reconoce la misma operación."],
        ["28", "Bloquea pago_operacion por clave."],
        ["32-40", "Si la clave pertenece a otro pedido, falla; si ya hay comprobante, lo devuelve."],
        ["44-49", "Vuelve a verificar que la sesión siga activa y autorizada."],
        ["51-53", "Calcula consumo de productos/ingredientes y bloquea inventario."],
        ["55-64", "Guarda cabecera, número, árbol del pedido, inventario y comprobante."],
        ["66", "commit confirma todos los cambios."],
        ["68-71", "rollback revierte si ocurre SQLException o una regla inválida."],
    ]))
    new_page(story)


def add_page_19(story):
    heading(story, "18. Inventario, recetas y movimientos")
    story.append(excerpt("src/DAO/PagoDAO.java", [(226, 247), (265, 273)]))
    story.append(explain_rows([
        ["productos", "Para tipo_stock=DIRECTO se comprueba y descuenta producto.stock_actual."],
        ["ingredientes", "Para tipo_stock=RECETA se comprueba y descuenta ingrediente.stock_actual."],
        ["compareTo < 0", "Detecta que lo disponible es menor que lo requerido."],
        ["guardarLinea", "Expande línea -> opciones -> productos -> modificaciones mediante claves generadas."],
        ["movimiento_inventario", "Cada descuento genera una SALIDA ligada al id_pedido para auditoría."],
    ]))
    story.append(P("Una modificación SIN no consume la cantidad_default de ese ingrediente. Un EXTRA agrega cantidad_extra por el número elegido. El backend vuelve a validar permite_quitar, permite_extra y max_extras antes de cobrar." , "Callout"))
    new_page(story)


def add_page_20(story):
    heading(story, "19. Navegación y módulos del administrador")
    story.append(excerpt("src/GUI_ADMINISTRADOR/MenuAdmin.java", [(486, 516)]))
    story.append(explain_rows([
        ["CardLayout", "Mantiene paneles dentro de una ventana y cambia el visible por nombre."],
        ["configurarSecciones", "Crea Dashboard, Usuarios, Gestión de menú, Inventario y Reportes."],
        ["mostrarSeccion", "Desmarca el botón anterior, marca el actual y muestra el panel."],
        ["revalidate/repaint", "Recalcula el diseño y vuelve a dibujar después del cambio."],
        ["barraTitulo.setSeccion", "Actualiza el texto superior con la ubicación actual."],
    ]))
    story.append(table([
        ["Módulo", "Qué administra", "Clases centrales"],
        ["Usuarios", "Roles, estado, turno, horario y contraseña.", "UsuariosPanel, UsuarioFormPanel, UsuarioCRUD"],
        ["Menú", "Productos, precios, horario, stock, imagen y configuración.", "GestionMenuPanel, ProductoCRUD, ConfiguracionMenuDAO"],
        ["Inventario", "Existencias, ingredientes y movimientos.", "InventarioPanel, IngredientesPanel, InventarioDAO"],
        ["Reportes", "Ventas, pedidos, ticket, métodos y cancelaciones.", "ReportesPanel, ReporteDAO"],
    ], widths=[42 * mm, 105 * mm, 93 * mm]))
    new_page(story)


def add_page_21(story):
    heading(story, "20. DAO, CRUD y consultas JDBC")
    story.append(code_block("String sql = \"SELECT nombre FROM producto WHERE id_producto=?\";\ntry (Connection c = conectar();\n     PreparedStatement ps = c.prepareStatement(sql)) {\n    ps.setInt(1, idProducto);\n    try (ResultSet rs = ps.executeQuery()) {\n        if (rs.next()) nombre = rs.getString(\"nombre\");\n    }\n}"))
    story.append(table([
        ["Pieza", "Función"],
        ["Connection", "Sesión abierta con MySQL."],
        ["PreparedStatement", "SQL precompilado con ?; separa instrucción y datos."],
        ["setInt/setString/setBigDecimal", "Asigna los parámetros en orden empezando en 1."],
        ["executeQuery", "Ejecuta SELECT y devuelve ResultSet."],
        ["executeUpdate", "Ejecuta INSERT/UPDATE/DELETE y devuelve filas afectadas."],
        ["ResultSet.next", "Avanza a una fila; false significa fin o resultado vacío."],
        ["getGeneratedKeys", "Obtiene el AUTO_INCREMENT creado por un INSERT."],
    ], widths=[58 * mm, 182 * mm]))
    story.append(P("CRUD describe operaciones básicas de un catálogo. DAO representa acceso a datos orientado a un caso de uso más amplio, como cobrar, cargar el árbol de menú o calcular reportes. En este proyecto ambos conceptos conviven." , "Callout"))
    new_page(story)


def add_page_22(story):
    heading(story, "21. SQL que deben saber leer")
    story.append(table([
        ["Elemento SQL", "Pregunta que responde", "Ejemplo en el proyecto"],
        ["SELECT", "¿Qué columnas quiero consultar?", "producto, pedidos, reportes"],
        ["FROM", "¿De qué tabla parte la consulta?", "FROM producto p"],
        ["JOIN ... ON", "¿Cómo relaciono tablas?", "receta con ingrediente"],
        ["WHERE", "¿Qué filas cumplen condiciones?", "estado=TRUE, categoría=?"],
        ["EXISTS", "¿Existe al menos una fila relacionada?", "producto con presentación activa"],
        ["COALESCE", "¿Qué valor alternativo uso si el primero es NULL?", "precio presentación o precio_base"],
        ["GROUP BY", "¿Cómo agrupo para contar/sumar?", "ventas por método/cajero"],
        ["ORDER BY", "¿En qué orden muestro?", "predeterminada primero"],
        ["LIMIT", "¿Cuántas filas necesito?", "una presentación preferida"],
        ["INSERT", "¿Cómo creo una fila?", "pedido y sus niveles"],
        ["UPDATE", "¿Cómo cambio una fila?", "stock_actual=stock_actual-?"],
        ["FOR UPDATE", "¿Cómo bloqueo la fila durante la transacción?", "stock y pago_operacion"],
    ], widths=[45 * mm, 102 * mm, 93 * mm]))
    story.append(code_block("SELECT p.nombre, c.nombre AS categoria\nFROM producto p\nJOIN categoria c ON c.id_categoria = p.id_categoria\nWHERE p.estado = TRUE\nORDER BY c.nombre, p.nombre;"))
    story.append(P("Los alias p y c son nombres cortos para tablas. AS categoria cambia el nombre de la columna en el resultado. JOIN no copia datos: combina filas relacionadas para esa consulta."))
    new_page(story)


def add_page_23(story):
    heading(story, "22. Reportes, paginación y trabajo en segundo plano")
    story.append(table([
        ["Concepto", "Implementación", "Razón"],
        ["Resumen diario", "ReporteDAO.cargar(LocalDate)", "Calcula ventas, pedidos, ticket y métodos del rango del día."],
        ["Límites de fecha", "inicio inclusivo y día siguiente exclusivo", "Incluye todas las horas sin depender de 23:59:59."],
        ["Filtros", "cajero, método, servicio y búsqueda", "Permite explorar el mismo resultado sin mezclar la lógica visual."],
        ["Paginación", "paginaActual + tamaño de página", "No dibuja cientos de filas simultáneamente."],
        ["SwingWorker", "consulta en doInBackground, actualización en done", "Evita congelar el EDT."],
        ["CSV", "ReporteCsv / exportadores", "Formato simple que abre Excel sin librerías pesadas."],
        ["Renderizadores", "TableCellRenderer", "Cambian color/formato sin modificar el dato real."],
    ], widths=[43 * mm, 102 * mm, 95 * mm]))
    story.append(code_block("LocalDateTime inicio = fecha.atStartOfDay();\nLocalDateTime fin = fecha.plusDays(1).atStartOfDay();\n// SQL: fecha_hora >= inicio AND fecha_hora < fin"))
    story.append(P("Pregunta frecuente: ¿por qué no usar DATE(fecha_hora)=? Siempre? El rango puede aprovechar mejor un índice sobre fecha_hora y define con claridad los límites." , "Callout"))
    new_page(story)


def add_page_24(story):
    heading(story, "23. Validaciones, errores y riesgos que deben reconocer")
    story.append(table([
        ["Riesgo", "Protección actual", "Mejora futura"],
        ["SQL injection", "PreparedStatement con parámetros.", "No concatenar texto del usuario."],
        ["Contraseña expuesta", "Hash PBKDF2-SHA256 y migración de cuentas antiguas.", "Nunca volver a texto plano; considerar BCrypt/Argon2."],
        ["Credenciales DB", "Conexión centralizada.", "Mover secretos fuera del código."],
        ["Cobro duplicado", "pago_operacion y clave idempotente.", "Persistir/monitorizar reintentos de caja."],
        ["Venta sin stock", "FOR UPDATE y revalidación en PagoDAO.", "Pruebas concurrentes con varias cajas."],
        ["Datos a medias", "Transacción commit/rollback.", "Mantener toda nueva escritura del cobro en la transacción."],
        ["Ventana congelada", "SwingWorker en cargas administrativas.", "No ejecutar consultas lentas en listeners del EDT."],
        ["Form corrupto", "Bloques protegidos de NetBeans.", "Editar initComponents desde GUI Builder."],
        ["Borrado de historia", "estado como borrado lógico.", "No eliminar filas usadas por pedidos."],
    ], widths=[50 * mm, 94 * mm, 96 * mm]))
    story.append(P("Diferencia importante: validar en la interfaz mejora la experiencia, pero validar en el backend/DAO protege la integridad. El cajero puede mostrar una regla antigua; PagoDAO debe volver a leer precio, grupo, opción, receta y stock antes de confirmar." , "Callout"))
    new_page(story)


def add_page_25(story):
    heading(story, "24. Pruebas y diagnóstico")
    story.append(table([
        ["Prueba", "Qué cubre"],
        ["PagosTest", "Validaciones de pago, reintento e inconsistencias."],
        ["ReporteDAOTest / ReportesTest", "Fechas, indicadores, CSV y resultados vacíos."],
        ["InventarioPanelTest", "Navegación, datos y tabla sin imágenes."],
        ["MenuAnimacionTest", "Apertura/cierre y textos del menú."],
        ["FormReportesTest", "Integridad del formulario de reportes."],
        ["ValidarDatosMenu", "Ejecuta scripts dos veces, busca duplicados y configuraciones inválidas."],
    ], widths=[65 * mm, 175 * mm]))
    story.append(table([
        ["Síntoma", "Ruta de revisión"],
        ["No conecta", "Servicio MySQL -> puerto/base -> credenciales -> driver JDBC."],
        ["Producto no aparece", "estado -> categoría -> presentación activa -> horario -> subcategoría."],
        ["No aparece WlMenú", "presentacion MENU -> grupos -> opciones -> componentes activos."],
        ["No deja agregar", "mínimo/máximo -> opción elegida -> componente -> personalización."],
        ["Falla al cobrar", "sesión -> precio/reglas -> stock -> transacción -> mensaje raíz."],
        ["Stock incorrecto", "tipo_stock -> receta -> SIN/EXTRA -> movimiento ligado al pedido."],
        ["Formulario solo lectura", "marcas GEN-BEGIN/GEN-END y sincronía .java/.form."],
    ], widths=[58 * mm, 182 * mm]))
    new_page(story)


def add_page_26(story):
    heading(story, "25. Guion de demostración en 10 minutos")
    story.append(table([
        ["Tiempo", "Qué mostrar", "Qué decir"],
        ["0:00-0:45", "Login", "Autenticación parametrizada, hash, estado y rol."],
        ["0:45-1:30", "Arquitectura", "Vista -> modelos -> DAO/CRUD -> MySQL."],
        ["1:30-3:30", "Big Mac", "Individual/WlMenú, grupos, opción con recargo y SIN/EXTRA."],
        ["3:30-4:30", "Combo", "Dos productos internos pueden tener cambios distintos."],
        ["4:30-6:00", "Carrito y pago", "BigDecimal, SolicitudPago, transacción e idempotencia."],
        ["6:00-7:00", "Inventario", "RECETA vs DIRECTO y movimientos automáticos."],
        ["7:00-8:30", "Administrador", "Usuarios, menú configurable, inventario y reportes."],
        ["8:30-9:30", "Base de datos", "Jerarquía de menú y jerarquía histórica del pedido."],
        ["9:30-10:00", "Cierre", "Consistencia, pruebas y mejoras futuras."],
    ], widths=[30 * mm, 65 * mm, 145 * mm]))
    story.append(P("Consejo: una persona maneja la aplicación y otra explica el flujo técnico. No lean código completo en pantalla; señalen método, condición, consulta y efecto. Si ocurre un error durante la demo, expliquen la ruta de diagnóstico en vez de ocultarlo." , "Callout"))
    story.append(P("Frase de cierre sugerida: 'El diseño separa la configuración comercial de los productos físicos, por eso el mismo modelo soporta individuales, menús, Cajitas y combos, conserva personalizaciones por componente y mantiene el inventario consistente al cobrar.'"))
    new_page(story)


def add_page_27(story):
    heading(story, "26. Preguntas técnicas probables - parte 1")
    qa(story, "¿Por qué un combo no es un producto simple?", "Porque cocina, precio e inventario necesitan conocer cada componente y cada modificación. El producto principal abre una presentación; sus opciones entregan productos internos.")
    qa(story, "¿Cómo una hamburguesa va sin pepinillo y la otra no?", "Cada ProductoPedido tiene idInterno y su propia lista de ModificacionPedido. No se guarda el cambio en la línea completa.")
    qa(story, "¿Qué diferencia hay entre producto y presentacion_menu?", "Producto describe el artículo reutilizable y su receta. presentacion_menu describe una forma de venderlo, su tipo y precio.")
    qa(story, "¿Para qué sirven minimo y maximo?", "Obligan y limitan cuántas opciones pueden elegirse dentro de un grupo.")
    qa(story, "¿Por qué se guardan nombres repetidos en pedido_*?", "Son snapshots históricos. Un cambio posterior del catálogo no altera el contenido de una venta ya realizada.")
    qa(story, "¿Por qué usan BigDecimal?", "El dinero necesita decimales exactos. double representa valores binarios aproximados y puede introducir centavos incorrectos.")
    qa(story, "¿Qué evita una inyección SQL?", "PreparedStatement separa la sentencia de los valores asignados con setString/setInt; no se concatena la entrada del usuario.")
    qa(story, "¿Qué es una transacción?", "Una unidad de cambios que se confirma completa con commit o se deshace completa con rollback.")
    qa(story, "¿Qué hace FOR UPDATE?", "Bloquea las filas leídas hasta terminar la transacción, evitando que dos cajas consuman simultáneamente el mismo stock.")
    qa(story, "¿Qué es idempotencia?", "Repetir el mismo cobro lógico con la misma clave no crea una segunda venta; devuelve el resultado previo.")
    new_page(story)


def add_page_28(story):
    heading(story, "27. Preguntas técnicas probables - parte 2 y repaso final")
    qa(story, "¿Qué diferencia hay entre DAO y CRUD?", "CRUD reúne operaciones básicas de un catálogo; DAO encapsula acceso a datos de un caso de uso más complejo, como cobrar o cargar el árbol del menú.")
    qa(story, "¿Por qué hay archivos .form y .java?", ".form conserva el diseño editable de NetBeans. .java contiene comportamiento y el initComponents generado.")
    qa(story, "¿Por qué usar SwingWorker?", "Para ejecutar consultas lentas fuera del EDT y evitar que la interfaz deje de responder.")
    qa(story, "¿Cómo funciona la disponibilidad por horario?", "producto.disponibilidad_menu guarda TODO_DIA, DESAYUNO o ALMUERZO; HorarioMenu y los DAO filtran/rechazan fuera de horario.")
    qa(story, "¿Qué diferencia hay entre RECETA y DIRECTO?", "RECETA descuenta ingredientes de producto_ingrediente. DIRECTO descuenta unidades de producto.stock_actual. NINGUNO no controla existencia directa.")
    qa(story, "¿Por qué no confiar solo en la interfaz?", "La configuración puede cambiar antes del cobro. PagoDAO vuelve a validar reglas, precios, usuario e inventario contra la base.")
    qa(story, "¿Qué mejorarían?", "Externalizar credenciales, añadir índices/pruebas concurrentes, usar migraciones versionadas, aumentar pruebas automatizadas y separar aún más servicios de la interfaz.")
    story.append(table([
        ["Repaso en una línea", "Respuesta"],
        ["Arquitectura", "Vista -> Modelo -> DAO/CRUD -> MySQL."],
        ["Menú", "Producto -> Presentación -> Grupo -> Opción -> Componente."],
        ["Pedido", "Pedido -> Detalle -> Opción -> Producto -> Modificación."],
        ["Cobro", "Validar -> bloquear -> guardar -> descontar -> commit; error -> rollback."],
        ["Inventario", "DIRECTO descuenta producto; RECETA descuenta ingredientes."],
    ], widths=[55 * mm, 185 * mm]))


def build_pdf():
    tables = parse_schema(ROOT / "sql" / "base_datos_completa.sql")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    doc = ManualDoc(
        str(OUTPUT),
        pagesize=PAGE_SIZE,
        rightMargin=15 * mm,
        leftMargin=15 * mm,
        topMargin=20 * mm,
        bottomMargin=14 * mm,
        title="Guía de Estudio del Proyecto Waldonald's POS",
        author="Equipo Waldonald's",
        subject="Preparación para exposición: código, arquitectura, lógica, SQL y base de datos",
        creator="Proyecto Waldonalds",
    )
    story = []
    add_cover(story)
    add_page_2(story)
    add_page_3(story)
    add_page_4(story)
    add_page_5(story)
    add_page_6(story)
    add_page_7(story)
    add_pages_8_11(story, tables)
    add_page_12(story)
    add_page_13(story)
    add_page_14(story)
    add_page_15(story)
    add_page_16(story)
    add_page_17(story)
    add_page_18(story)
    add_page_19(story)
    add_page_20(story)
    add_page_21(story)
    add_page_22(story)
    add_page_23(story)
    add_page_24(story)
    add_page_25(story)
    add_page_26(story)
    add_page_27(story)
    add_page_28(story)
    doc.build(story, onFirstPage=decorate, onLaterPages=decorate)
    pages = len(PdfReader(str(OUTPUT)).pages)
    if pages > 30:
        raise RuntimeError(f"El manual excede el máximo: {pages} páginas")
    print(f"PDF={OUTPUT}")
    print(f"PAGES={pages}")
    print(f"TABLES={len(tables)}")


if __name__ == "__main__":
    build_pdf()
