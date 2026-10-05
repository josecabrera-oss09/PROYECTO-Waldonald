from __future__ import annotations

import html
import os
import re
from collections import Counter, defaultdict
from datetime import datetime
from pathlib import Path
from typing import Iterable

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm, mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    CondPageBreak,
    Flowable,
    Image,
    KeepTogether,
    LongTable,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "output" / "pdf" / "Manual_Completo_Waldonalds.pdf"
PAGE_SIZE = landscape(A4)

NAVY = colors.HexColor("#011424")
NAVY_2 = colors.HexColor("#07314B")
YELLOW = colors.HexColor("#FFBC0D")
RED = colors.HexColor("#DA291C")
OFF_WHITE = colors.HexColor("#F7F8FA")
LIGHT = colors.HexColor("#E4E9EF")
MID = colors.HexColor("#637083")
GREEN = colors.HexColor("#1F8A5B")
INK = colors.HexColor("#111827")


def clean_text(value: object) -> str:
    text = str(value if value is not None else "")
    return (
        text.replace("\u2011", "-")
        .replace("\u2013", "-")
        .replace("\u2014", "-")
        .replace("\u00a0", " ")
        .replace("\t", "    ")
    )


def esc(value: object) -> str:
    return html.escape(clean_text(value))


def register_fonts() -> tuple[str, str, str]:
    regular = ROOT / "src" / "Font" / "DMSans" / "DMSans-Regular.ttf"
    bold = ROOT / "src" / "Font" / "DMSans" / "DMSans-Bold.ttf"
    medium = ROOT / "src" / "Font" / "DMSans" / "DMSans-Medium.ttf"
    if regular.exists() and bold.exists() and medium.exists():
        pdfmetrics.registerFont(TTFont("DM Sans", str(regular)))
        pdfmetrics.registerFont(TTFont("DM Sans Bold", str(bold)))
        pdfmetrics.registerFont(TTFont("DM Sans Medium", str(medium)))
        return "DM Sans", "DM Sans Bold", "DM Sans Medium"
    return "Helvetica", "Helvetica-Bold", "Helvetica"


FONT, FONT_BOLD, FONT_MEDIUM = register_fonts()


styles = getSampleStyleSheet()
styles.add(
    ParagraphStyle(
        name="CoverTitle",
        parent=styles["Title"],
        fontName=FONT_BOLD,
        fontSize=31,
        leading=35,
        textColor=colors.white,
        alignment=TA_CENTER,
        spaceAfter=9,
    )
)
styles.add(
    ParagraphStyle(
        name="CoverSub",
        parent=styles["Normal"],
        fontName=FONT,
        fontSize=14,
        leading=19,
        textColor=colors.white,
        alignment=TA_CENTER,
    )
)
styles.add(
    ParagraphStyle(
        name="H1Manual",
        parent=styles["Heading1"],
        fontName=FONT_BOLD,
        fontSize=21,
        leading=25,
        textColor=NAVY,
        spaceBefore=8,
        spaceAfter=8,
    )
)
styles.add(
    ParagraphStyle(
        name="H2Manual",
        parent=styles["Heading2"],
        fontName=FONT_BOLD,
        fontSize=14,
        leading=18,
        textColor=NAVY_2,
        spaceBefore=7,
        spaceAfter=5,
    )
)
styles.add(
    ParagraphStyle(
        name="H3Manual",
        parent=styles["Heading3"],
        fontName=FONT_BOLD,
        fontSize=10.5,
        leading=13,
        textColor=RED,
        spaceBefore=5,
        spaceAfter=4,
    )
)
styles.add(
    ParagraphStyle(
        name="BodyManual",
        parent=styles["BodyText"],
        fontName=FONT,
        fontSize=9,
        leading=12.4,
        textColor=INK,
        spaceAfter=5,
    )
)
styles.add(
    ParagraphStyle(
        name="SmallManual",
        parent=styles["BodyText"],
        fontName=FONT,
        fontSize=7.6,
        leading=10,
        textColor=INK,
        spaceAfter=3,
    )
)
styles.add(
    ParagraphStyle(
        name="TinyManual",
        parent=styles["BodyText"],
        fontName=FONT,
        fontSize=6.4,
        leading=8,
        textColor=INK,
    )
)
styles.add(
    ParagraphStyle(
        name="HeaderTiny",
        parent=styles["BodyText"],
        fontName=FONT_BOLD,
        fontSize=6.4,
        leading=8,
        textColor=colors.white,
    )
)
styles.add(
    ParagraphStyle(
        name="CodeManual",
        parent=styles["Code"],
        fontName="Courier",
        fontSize=7.1,
        leading=9.2,
        textColor=NAVY,
        leftIndent=8,
        rightIndent=8,
        borderColor=LIGHT,
        borderWidth=0.7,
        borderPadding=7,
        backColor=OFF_WHITE,
        spaceBefore=4,
        spaceAfter=7,
    )
)
styles.add(
    ParagraphStyle(
        name="Callout",
        parent=styles["BodyText"],
        fontName=FONT_MEDIUM,
        fontSize=8.7,
        leading=12,
        textColor=NAVY,
        borderColor=YELLOW,
        borderWidth=1,
        borderPadding=8,
        backColor=colors.HexColor("#FFF9E6"),
        spaceBefore=5,
        spaceAfter=7,
    )
)


def P(text: object, style: str = "BodyManual") -> Paragraph:
    return Paragraph(esc(text).replace("\n", "<br/>"), styles[style])


def rich(text: str, style: str = "BodyManual") -> Paragraph:
    return Paragraph(clean_text(text), styles[style])


def bullet(text: object) -> Paragraph:
    return Paragraph("• " + esc(text), styles["BodyManual"])


def code_block(text: str) -> Paragraph:
    return Paragraph(esc(text).replace(" ", "&nbsp;").replace("\n", "<br/>"), styles["CodeManual"])


class ManualDoc(SimpleDocTemplate):
    def afterFlowable(self, flowable):
        if isinstance(flowable, Paragraph):
            style = flowable.style.name
            if style in {"H1Manual", "H2Manual"}:
                title = clean_text(flowable.getPlainText())
                key = "section_" + str(abs(hash((title, self.page))))
                self.canv.bookmarkPage(key)
                level = 0 if style == "H1Manual" else 1
                self.canv.addOutlineEntry(title[:120], key, level=level, closed=level > 0)


class CoverBand(Flowable):
    def __init__(self, height=12.2 * cm):
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
        c.setFillColor(colors.HexColor("#0C2B40"))
        for i in range(7):
            c.circle(self.width - 38 - i * 22, self.height - 34, 5 + i * 1.4, stroke=0, fill=1)
        c.setFont(FONT_BOLD, 42)
        c.setFillColor(YELLOW)
        c.drawCentredString(self.width / 2, self.height - 76, "W")
        c.setFont(FONT_BOLD, 29)
        c.setFillColor(colors.white)
        c.drawCentredString(self.width / 2, self.height - 130, "MANUAL COMPLETO DEL SISTEMA POS")
        c.setFont(FONT_BOLD, 23)
        c.drawCentredString(self.width / 2, self.height - 164, "Waldonald's")
        c.setFont(FONT, 12)
        c.setFillColor(colors.HexColor("#DCE8F1"))
        c.drawCentredString(self.width / 2, self.height - 205, "Código Java + interfaces Swing + MySQL + operación del negocio")
        c.drawCentredString(self.width / 2, self.height - 225, "Guía de estudio desde cero y referencia técnica línea por línea")
        c.restoreState()


class FlowDiagram(Flowable):
    def __init__(self, nodes, edges, height=115 * mm, title=None):
        super().__init__()
        self.nodes = nodes
        self.edges = edges
        self.height = height
        self.title = title

    def wrap(self, availWidth, availHeight):
        self.width = availWidth
        return availWidth, self.height

    def draw(self):
        c = self.canv
        c.saveState()
        c.setFillColor(OFF_WHITE)
        c.setStrokeColor(LIGHT)
        c.roundRect(0, 0, self.width, self.height, 12, stroke=1, fill=1)
        if self.title:
            c.setFont(FONT_BOLD, 11)
            c.setFillColor(NAVY)
            c.drawString(14, self.height - 20, clean_text(self.title))
        pad_top = 30 if self.title else 10
        positions = {}
        for node in self.nodes:
            key, label, x, y, w, h, color = node
            px = x * self.width
            py = y * (self.height - pad_top)
            pw = w * self.width
            ph = h * (self.height - pad_top)
            positions[key] = (px, py, pw, ph)
            c.setFillColor(color)
            c.setStrokeColor(NAVY_2)
            c.roundRect(px, py, pw, ph, 7, stroke=1, fill=1)
            c.setFillColor(NAVY if color != NAVY else colors.white)
            c.setFont(FONT_BOLD, 8)
            words = clean_text(label).split("\n")
            base = py + ph / 2 + (len(words) - 1) * 5
            for idx, line in enumerate(words):
                c.drawCentredString(px + pw / 2, base - idx * 10, line[:38])
        for a, b, label in self.edges:
            if a not in positions or b not in positions:
                continue
            ax, ay, aw, ah = positions[a]
            bx, by, bw, bh = positions[b]
            x1, y1 = ax + aw, ay + ah / 2
            x2, y2 = bx, by + bh / 2
            if bx < ax:
                x1, y1 = ax + aw / 2, ay
                x2, y2 = bx + bw / 2, by + bh
            c.setStrokeColor(MID)
            c.setLineWidth(1.2)
            c.line(x1, y1, x2, y2)
            angle = 4
            c.line(x2, y2, x2 - angle, y2 + angle)
            c.line(x2, y2, x2 - angle, y2 - angle)
            if label:
                c.setFont(FONT, 6.5)
                c.setFillColor(MID)
                c.drawCentredString((x1 + x2) / 2, (y1 + y2) / 2 + 3, clean_text(label)[:40])
        c.restoreState()


def _wrap_chars(text: str, font: str, size: float, width: float) -> list[str]:
    text = clean_text(text)
    if not text:
        return [""]
    lines, current = [], ""
    for ch in text:
        trial = current + ch
        if current and pdfmetrics.stringWidth(trial, font, size) > width:
            lines.append(current)
            current = ch
        else:
            current = trial
    if current or not lines:
        lines.append(current)
    return lines


def _wrap_words(text: str, font: str, size: float, width: float) -> list[str]:
    words = clean_text(text).split()
    if not words:
        return [""]
    lines, current = [], ""
    for word in words:
        trial = word if not current else current + " " + word
        if current and pdfmetrics.stringWidth(trial, font, size) > width:
            lines.append(current)
            current = word
        elif pdfmetrics.stringWidth(word, font, size) > width:
            if current:
                lines.append(current)
                current = ""
            lines.extend(_wrap_chars(word, font, size, width))
        else:
            current = trial
    if current:
        lines.append(current)
    return lines or [""]


class AnnotatedLine(Flowable):
    def __init__(self, number: int, code: str, explanation: str, alternate=False):
        super().__init__()
        self.number = number
        self.code = clean_text(code.rstrip("\r\n"))
        self.explanation = clean_text(explanation)
        self.alternate = alternate

    def wrap(self, availWidth, availHeight):
        self.width = availWidth
        self.number_w = 30
        self.code_w = availWidth * 0.50
        self.explain_w = availWidth - self.number_w - self.code_w - 14
        self.code_lines = _wrap_chars(self.code or " ", "Courier", 5.4, self.code_w - 7)
        self.explain_lines = _wrap_words(self.explanation, FONT, 5.6, self.explain_w - 7)
        self.height = max(len(self.code_lines), len(self.explain_lines)) * 6.9 + 4
        return availWidth, self.height

    def draw(self):
        c = self.canv
        c.saveState()
        if self.alternate:
            c.setFillColor(colors.HexColor("#FAFBFC"))
            c.rect(0, 0, self.width, self.height, stroke=0, fill=1)
        c.setStrokeColor(colors.HexColor("#EDF0F3"))
        c.line(0, 0, self.width, 0)
        c.setFillColor(MID)
        c.setFont(FONT_MEDIUM, 5.4)
        c.drawRightString(self.number_w - 5, self.height - 8, str(self.number))
        c.setFillColor(NAVY)
        c.setFont("Courier", 5.4)
        y = self.height - 8
        for line in self.code_lines:
            c.drawString(self.number_w + 3, y, line)
            y -= 6.9
        c.setStrokeColor(LIGHT)
        c.line(self.number_w + self.code_w, 0, self.number_w + self.code_w, self.height)
        c.setFillColor(INK)
        c.setFont(FONT, 5.6)
        y = self.height - 8
        for line in self.explain_lines:
            c.drawString(self.number_w + self.code_w + 6, y, line)
            y -= 6.9
        c.restoreState()


def page_decoration(canvas, doc):
    canvas.saveState()
    width, height = PAGE_SIZE
    if doc.page > 1:
        canvas.setFillColor(NAVY)
        canvas.rect(0, height - 16 * mm, width, 16 * mm, stroke=0, fill=1)
        canvas.setFillColor(YELLOW)
        canvas.rect(0, height - 16.8 * mm, width, 0.8 * mm, stroke=0, fill=1)
        canvas.setFont(FONT_BOLD, 9)
        canvas.setFillColor(colors.white)
        canvas.drawString(15 * mm, height - 10.3 * mm, "Waldonald's - Manual completo del sistema POS")
        canvas.setFont(FONT, 7.5)
        canvas.drawRightString(width - 15 * mm, height - 10.3 * mm, "Java 21 | Swing | JDBC | MySQL")
    canvas.setStrokeColor(LIGHT)
    canvas.line(15 * mm, 10 * mm, width - 15 * mm, 10 * mm)
    canvas.setFont(FONT, 7)
    canvas.setFillColor(MID)
    canvas.drawString(15 * mm, 6 * mm, "Documento generado desde el proyecto local actual")
    canvas.drawRightString(width - 15 * mm, 6 * mm, f"Página {doc.page}")
    canvas.restoreState()


def section(title: str, story: list, level=1):
    story.append(Paragraph(esc(title), styles["H1Manual" if level == 1 else "H2Manual"]))


def table(data, widths=None, header=True, font_size=7.2, repeat=1):
    converted = []
    for r, row in enumerate(data):
        converted.append([
            Paragraph(esc(cell), styles["HeaderTiny" if header and r == 0 else "TinyManual"])
            if not isinstance(cell, Flowable) else cell
            for cell in row
        ])
    t = LongTable(converted, colWidths=widths, repeatRows=repeat if header else 0, hAlign="LEFT")
    commands = [
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("FONTNAME", (0, 0), (-1, -1), FONT),
        ("FONTSIZE", (0, 0), (-1, -1), font_size),
        ("LEADING", (0, 0), (-1, -1), font_size + 2),
        ("GRID", (0, 0), (-1, -1), 0.35, LIGHT),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]
    if header:
        commands += [
            ("BACKGROUND", (0, 0), (-1, 0), NAVY),
            ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
            ("FONTNAME", (0, 0), (-1, 0), FONT_BOLD),
        ]
    for idx in range(1 if header else 0, len(data)):
        if idx % 2 == 0:
            commands.append(("BACKGROUND", (0, idx), (-1, idx), OFF_WHITE))
    t.setStyle(TableStyle(commands))
    return t


TEXT_EXTENSIONS = {".java", ".form", ".sql", ".xml", ".properties", ".mf", ".md", ".ps1"}
RESOURCE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif", ".ttf", ".jar"}
EXCLUDED_DIRS = {".git", "build", "dist", "output", "nbproject/private"}


def excluded(path: Path) -> bool:
    rel_parts = path.relative_to(ROOT).parts
    joined = "/".join(rel_parts)
    return any(part in {".git", "build", "dist", "output"} for part in rel_parts) or joined.startswith("nbproject/private/")


def all_project_files() -> list[Path]:
    files = []
    for p in ROOT.rglob("*"):
        if not p.is_file() or excluded(p):
            continue
        if p.name == Path(__file__).name:
            continue
        files.append(p)
    return sorted(files, key=lambda p: str(p.relative_to(ROOT)).lower())


def is_text_file(path: Path) -> bool:
    return path.suffix.lower() in TEXT_EXTENSIONS or path.name in {"build.xml", "manifest.mf"}


def read_lines(path: Path) -> list[str]:
    try:
        return path.read_text(encoding="utf-8").splitlines()
    except UnicodeDecodeError:
        return path.read_text(encoding="latin-1", errors="replace").splitlines()


def file_purpose(path: Path) -> str:
    rel = str(path.relative_to(ROOT)).replace("\\", "/")
    name = path.stem
    exact = {
        "src/Main/main.java": "Punto de entrada. Inicia la interfaz en el hilo de Swing, muestra la pantalla de carga y luego abre el inicio de sesión.",
        "src/Conexion/Conexion.java": "Centraliza la conexión JDBC con MySQL y devuelve objetos Connection para los DAO y CRUD.",
        "src/Login/Login.java": "Pantalla de autenticación. Busca al usuario, verifica la contraseña, valida estado y turno, guarda la sesión y abre el panel correcto según el rol.",
        "src/GUI_CAJERO/Cajero.java": "Ventana principal del cajero. Coordina menú, pedido, carrito y barra de título.",
        "src/GUI_CAJERO/PedidoPanel.java": "Carrito de compra. Mantiene líneas del pedido, cantidades, total, pago y acceso al último comprobante.",
        "src/GUI_CAJERO/ConfiguradorProductoPanel.java": "Constructor visual de un producto: presentación Individual/WlMenú/Combo, grupos de opciones y cambios internos por ingrediente.",
        "src/GUI_CAJERO/ConfiguradorProductoDialog.java": "Diálogo que aloja el configurador de producto y devuelve la línea terminada al carrito.",
        "src/DAO/PagoDAO.java": "Caso de uso transaccional de cobro. Revalida reglas, registra el pedido completo, descuenta inventario y genera el comprobante.",
        "src/DAO/ConfiguracionMenuDAO.java": "Carga y administra presentaciones, grupos, opciones, componentes y recetas configurables.",
        "src/DAO/ProductoDAO.java": "Consulta los productos visibles para el cajero por categoría, subcategoría y horario.",
        "src/DAO/InventarioDAO.java": "Consulta existencias combinadas de productos directos e ingredientes y registra entradas, salidas y ajustes.",
        "src/DAO/ReporteDAO.java": "Obtiene indicadores y ventas del día para el panel de reportes.",
        "src/GUI_ADMINISTRADOR/InicioAdminForm.java": "Ventana contenedora del área administrativa y su barra de título.",
        "src/GUI_ADMINISTRADOR/MenuAdmin.java": "Menú lateral del administrador. Cambia entre dashboard, usuarios, menú, inventario y reportes.",
        "src/GUI_ADMINISTRADOR/GestionInventarioPanel.java": "Pantalla compuesta de inventario con pestañas Existencias, Ingredientes y Movimientos.",
        "src/GUI_ADMINISTRADOR/InventarioPanel.java": "Vista de existencias, búsqueda, filtros, paginación, entradas/salidas y exportación.",
        "src/GUI_ADMINISTRADOR/IngredientesPanel.java": "Mantenimiento del catálogo de ingredientes, stock mínimo, estado y movimientos.",
        "src/GUI_ADMINISTRADOR/MovimientosInventarioPanel.java": "Historial de entradas, salidas y ajustes de inventario.",
        "src/GUI_ADMINISTRADOR/GestionMenuPanel.java": "Mantenimiento de productos del menú: filtros, resumen, alta, edición, estado y configuración comercial.",
        "src/GUI_ADMINISTRADOR/ProductoFormDialog.java": "Formulario emergente para crear o editar productos, categorías, precios, stock, horario e imagen.",
        "src/GUI_ADMINISTRADOR/ConfiguracionMenuAdminDialog.java": "Editor administrativo de presentaciones, grupos, opciones, componentes y receta del producto.",
        "src/GUI_ADMINISTRADOR/UsuariosPanel.java": "Gestión paginada de usuarios con búsqueda, filtros, resumen, alta, edición y activación.",
        "src/GUI_ADMINISTRADOR/ReportesPanel.java": "Dashboard de reportes con indicadores, filtros, tabla, paginación, exportación e impresión.",
        "sql/base_datos_completa.sql": "Esquema principal. Crea la base waldonalds y sus tablas relacionales.",
        "sql/datos_menu_nueva_base.sql": "Carga idempotente del catálogo, ingredientes, recetas, presentaciones, WlMenús, Cajitas y combos.",
        "nbproject/project.properties": "Configuración del proyecto NetBeans: Java 21, librerías, clase principal y rutas de compilación.",
        "build.xml": "Archivo Ant que delega la compilación y ejecución a la infraestructura generada por NetBeans.",
    }
    if rel in exact:
        return exact[rel]
    if rel.endswith(".form"):
        return f"Metadatos visuales de NetBeans para diseñar {name}. Define componentes, propiedades, eventos y posiciones; se sincroniza con el archivo Java del mismo nombre."
    if rel.startswith("src/Modelos/"):
        return f"Modelo de datos {name}. Transporta información entre la interfaz, la lógica y la base de datos sin dibujar pantallas."
    if rel.startswith("src/CRUD/"):
        return f"Operaciones de creación, consulta, actualización y estado para {name.replace('CRUD', '').lower() or 'el catálogo'} usando JDBC."
    if rel.startswith("src/DAO/"):
        return f"Objeto de acceso a datos {name}. Encapsula consultas SQL y transforma filas de MySQL en objetos Java."
    if rel.startswith("src/Componentes/"):
        return f"Componente visual reutilizable {name}. Mantiene el estilo de la aplicación y evita duplicar código de Swing."
    if rel.startswith("src/GUI_ADMINISTRADOR/"):
        return f"Interfaz del módulo administrador: {name}. Combina componentes visuales con acciones del usuario."
    if rel.startswith("src/GUI_CAJERO/"):
        return f"Interfaz del módulo cajero: {name}. Participa en la selección y venta de productos."
    if rel.startswith("src/Utilidades/"):
        return f"Utilidad compartida {name}. Resuelve una tarea transversal usada por varias pantallas."
    if rel.startswith("src/Labels/"):
        return f"Control de entrada o etiqueta personalizado {name}, diseñado para reutilizar estilos y comportamiento."
    if rel.startswith("test/"):
        return f"Prueba ejecutable {name}. Verifica automáticamente un comportamiento sin modificar la aplicación productiva."
    if rel.startswith("sql/"):
        return f"Script SQL {name}. Prepara, migra o prueba una parte de la base de datos."
    if rel.startswith("scripts/"):
        return f"Herramienta auxiliar {name} usada para generar o validar datos del proyecto."
    if rel.endswith(".md"):
        return "Documentación complementaria escrita durante el desarrollo del proyecto."
    if rel.endswith(".xml"):
        return "Configuración XML utilizada por NetBeans/Ant para describir y construir el proyecto."
    if rel.endswith(".properties"):
        return "Archivo de propiedades clave=valor utilizado por NetBeans o por la compilación."
    return f"Archivo de apoyo del proyecto: {rel}."


JAVA_IMPORTS = {
    "java.sql.Connection": "representa una sesión abierta con la base de datos",
    "java.sql.PreparedStatement": "ejecuta SQL con parámetros y evita concatenar valores del usuario",
    "java.sql.ResultSet": "permite recorrer las filas devueltas por una consulta",
    "java.sql.SQLException": "representa errores ocurridos al comunicarse con MySQL",
    "javax.swing.Timer": "ejecuta una acción periódica sin bloquear la interfaz Swing",
    "javax.swing.SwingWorker": "ejecuta trabajo lento fuera del hilo visual y publica el resultado después",
    "java.math.BigDecimal": "maneja dinero y cantidades decimales sin los errores de aproximación de double",
    "java.time.LocalDate": "representa una fecha sin hora",
    "java.time.LocalTime": "representa una hora sin fecha",
    "java.time.LocalDateTime": "representa fecha y hora juntas",
    "java.util.List": "colección ordenada que admite varios elementos",
    "java.util.Map": "colección de pares clave-valor",
    "java.util.Set": "colección que evita elementos repetidos",
}


def explain_java(line: str, number: int, context: dict) -> str:
    s = line.strip()
    if not s:
        return "Línea en blanco: separa visualmente bloques para que el código sea más legible."
    if "password" in s.lower() and re.search(r'"[^"\n]+"', s) and ("=" in s or "VALUES" in s):
        return "Define o usa información sensible de contraseña. En un sistema real debe venir de configuración segura y nunca publicarse."
    if s.startswith("package "):
        return "Declara el paquete o carpeta lógica a la que pertenece esta clase."
    if s.startswith("import "):
        imported = s.removeprefix("import ").rstrip(";")
        detail = JAVA_IMPORTS.get(imported)
        if detail:
            return f"Importa {imported}: {detail}."
        return f"Importa {imported} para poder usar esa clase sin escribir su nombre completo."
    if s.startswith("//"):
        if "GEN-BEGIN" in s or "GEN-END" in s or "GEN-FIRST" in s or "GEN-LAST" in s:
            return "Marca protegida del GUI Builder de NetBeans. No debe borrarse porque delimita código administrado por el diseñador visual."
        return "Comentario para humanos; Java no ejecuta este texto. " + s.lstrip("/ ")[:180]
    if s.startswith("/*") or s.startswith("*") or s.endswith("*/"):
        return "Parte de un comentario de bloque o documentación Javadoc; describe intención y no se ejecuta."
    if s.startswith("@"):
        return "Anotación de Java: agrega metadatos o indica al compilador cómo tratar el elemento siguiente."
    if re.search(r"\b(record|class|interface|enum)\b", s) and ("public" in s or "private" in s or "final" in s):
        kind = "registro inmutable" if " record " in f" {s} " else "tipo Java"
        return f"Declara un {kind}. Aquí comienza la definición de responsabilidades y datos de esta pieza del sistema."
    if s == "{":
        return "Abre un bloque de instrucciones perteneciente a la declaración anterior."
    if s in {"}", "};"}:
        return "Cierra el bloque abierto anteriormente" + (" y termina la declaración." if s.endswith(";") else ".")
    if s.startswith("}") and any(word in s for word in ("else", "catch", "finally")):
        return "Cierra el bloque anterior y abre la ruta alternativa o de manejo de error."
    if re.match(r"(public|private|protected)\s+.*\([^;]*\)\s*(throws\s+[^\{]+)?\{?$", s) and "=" not in s:
        name_match = re.search(r"([A-Za-z_$][\w$]*)\s*\(", s)
        name = name_match.group(1) if name_match else "este método"
        if name == context.get("class"):
            return f"Constructor de {name}: prepara un objeto nuevo y deja sus componentes listos para usarse."
        return f"Declara el método {name}. Sus parámetros son datos de entrada y el tipo anterior al nombre indica qué devuelve."
    if s.startswith("if ") or s.startswith("if("):
        return "Evalúa una condición; el bloque siguiente solo se ejecuta cuando el resultado es verdadero."
    if s.startswith("else if"):
        return "Prueba otra condición únicamente cuando la condición anterior no se cumplió."
    if s.startswith("else"):
        return "Ruta alternativa que se ejecuta cuando la condición anterior fue falsa."
    if s.startswith("for ") or s.startswith("for("):
        return "Bucle for: repite el bloque para una secuencia, rango o colección de elementos."
    if s.startswith("while ") or s.startswith("while("):
        return "Bucle while: repite el bloque mientras la condición continúe siendo verdadera."
    if s.startswith("switch ") or s.startswith("switch("):
        return "Selecciona una ruta entre varias según el valor evaluado."
    if s.startswith("case ") or s.startswith("default"):
        return "Define una de las rutas posibles dentro del switch."
    if s.startswith("try") or "try (" in s:
        return "Inicia una operación que puede fallar. Si hay recursos entre paréntesis, Java los cerrará automáticamente."
    if s.startswith("catch") or " catch " in s:
        return "Captura un error producido en el try para mostrarlo, convertirlo o recuperarse de forma controlada."
    if s.startswith("finally"):
        return "Bloque que se ejecuta siempre, haya ocurrido un error o no; suele liberar recursos."
    if s.startswith("throw "):
        return "Interrumpe el flujo lanzando un error con una explicación para quien llamó al método."
    if s.startswith("return"):
        return "Finaliza el método y devuelve el valor indicado a quien lo llamó."
    if "new " in s:
        match = re.search(r"new\s+([\w.]+)", s)
        what = match.group(1) if match else "objeto"
        return f"Crea una nueva instancia de {what} y la usa o guarda para continuar el proceso."
    if ".addActionListener" in s or ".addMouseListener" in s or ".addKeyListener" in s:
        return "Registra un listener: el código asociado se ejecutará cuando el usuario produzca ese evento."
    if ".setText(" in s:
        return "Cambia el texto visible de un componente de la interfaz."
    if ".setVisible(" in s:
        return "Muestra u oculta una ventana o componente según el valor booleano enviado."
    if ".setEnabled(" in s:
        return "Habilita o deshabilita la interacción con el componente."
    if ".set" in s and "(" in s:
        method = re.search(r"\.([A-Za-z0-9_]*set[A-Za-z0-9_]*)\(", s)
        return f"Configura una propiedad mediante {method.group(1) if method else 'un método set'} en el objeto indicado."
    if "prepareStatement" in s:
        return "Prepara una sentencia SQL parametrizada para enviarla a MySQL de forma segura."
    if "executeQuery" in s:
        return "Ejecuta una consulta SELECT y recibe sus filas en un ResultSet."
    if "executeUpdate" in s:
        return "Ejecuta un INSERT, UPDATE o DELETE y obtiene cuántas filas fueron afectadas."
    if ".next()" in s:
        return "Avanza el ResultSet a la siguiente fila; devuelve false cuando ya no quedan resultados."
    if ".getString(" in s or ".getInt(" in s or ".getBigDecimal(" in s or ".getBoolean(" in s:
        return "Lee una columna de la fila actual del ResultSet y la convierte al tipo Java solicitado."
    if "setAutoCommit(false)" in s:
        return "Desactiva el guardado automático para iniciar una transacción que se confirmará o revertirá completa."
    if ".commit()" in s:
        return "Confirma la transacción: todos los cambios pendientes quedan guardados juntos."
    if ".rollback()" in s:
        return "Revierte la transacción porque algo falló, evitando guardar un pedido o inventario incompleto."
    if re.match(r"(public|private|protected)?\s*(static\s+)?(final\s+)?[\w<>?,.\[\]]+\s+\w+\s*(=|;)", s):
        if "final" in s:
            return "Declara una constante o referencia que no puede reasignarse después de inicializarse."
        return "Declara una variable o atributo: reserva un nombre y tipo para guardar un dato usado por esta clase."
    if s.startswith("this."):
        return "Accede al atributo del objeto actual para asignarlo o utilizarlo."
    if "=" in s and "==" not in s and "!=" not in s and ">=" not in s and "<=" not in s:
        return "Calcula o toma el valor de la derecha y lo asigna al elemento de la izquierda."
    if s.endswith(";"):
        return "Ejecuta una instrucción Java completa; el punto y coma marca su final."
    return "Forma parte del bloque actual y completa la lógica indicada por las líneas cercanas."


def explain_sql(line: str, number: int, context: dict) -> str:
    s = line.strip()
    u = s.upper()
    if not s:
        return "Línea en blanco usada para separar secciones del script SQL."
    if s.startswith("--"):
        return "Comentario SQL para explicar la intención del bloque; MySQL no lo ejecuta."
    if u.startswith("CREATE DATABASE"):
        return "Crea la base de datos si todavía no existe."
    if u.startswith("USE "):
        return "Selecciona la base de datos donde se ejecutarán las siguientes sentencias."
    if u.startswith("CREATE TABLE"):
        name = re.search(r"CREATE\s+(?:TEMPORARY\s+)?TABLE\s+([\w`]+)", s, re.I)
        context["table"] = name.group(1).strip("`") if name else "tabla"
        return f"Comienza la definición de la tabla {context['table']}."
    if u.startswith("CREATE TEMPORARY TABLE"):
        return "Crea una tabla temporal que existe solo durante la sesión de carga y ayuda a fusionar datos sin duplicarlos."
    if u.startswith("INSERT INTO"):
        return "Inserta nuevas filas en la tabla indicada; las líneas siguientes definen columnas y valores."
    if u.startswith("UPDATE "):
        return "Actualiza filas existentes de la tabla indicada."
    if u.startswith("DELETE FROM"):
        return "Elimina las filas que cumplan la condición; debe usarse con cuidado."
    if u.startswith("SELECT "):
        return "Consulta columnas o valores calculados sin modificar los datos."
    if u.startswith("FROM "):
        return "Indica la tabla principal desde la que se leen los datos."
    if u.startswith("JOIN ") or " JOIN " in u:
        return "Relaciona filas de dos tablas mediante sus identificadores vinculados."
    if u.startswith("WHERE ") or " WHERE " in u:
        return "Filtra las filas para afectar o devolver únicamente las que cumplen la condición."
    if u.startswith("SET @"):
        return "Guarda un valor en una variable de sesión de MySQL para reutilizarlo más adelante."
    if "PRIMARY KEY" in u:
        return "Define la clave primaria: identifica cada fila de manera única."
    if "FOREIGN KEY" in u:
        return "Define una clave foránea que conecta esta tabla con la clave primaria de otra tabla."
    if "AUTO_INCREMENT" in u:
        return "Define una columna y hace que MySQL asigne automáticamente el siguiente identificador."
    if "ENUM(" in u:
        return "Define una columna que solo acepta una de las opciones enumeradas en esta línea."
    if " NOT NULL" in u:
        return "Define una columna obligatoria; no se permite guardar NULL en ella."
    if " DEFAULT " in u:
        return "Define una columna y el valor automático que recibirá si el INSERT no envía otro."
    if u.startswith("VALUES") or s.startswith("("):
        return "Aporta uno o varios registros concretos para la inserción iniciada anteriormente."
    if u.startswith("ON DUPLICATE KEY"):
        return "Si la clave ya existe, actualiza la fila en vez de crear un duplicado."
    if u.startswith("GROUP BY"):
        return "Agrupa filas iguales para calcular totales o resúmenes por categoría."
    if u.startswith("ORDER BY"):
        return "Ordena el resultado según las columnas indicadas."
    if u.startswith("DROP TEMPORARY"):
        return "Elimina la tabla temporal porque su trabajo de carga ya terminó."
    if s in {");", ")", ";"}:
        return "Cierra y finaliza la sentencia SQL iniciada en las líneas anteriores."
    return "Continúa la sentencia SQL actual con columnas, valores, relaciones o condiciones."


def explain_form(line: str, number: int, context: dict) -> str:
    s = line.strip()
    if not s:
        return "Línea en blanco para organizar el XML del formulario."
    if s.startswith("<?xml"):
        return "Cabecera XML: informa a NetBeans la versión y codificación del archivo."
    if s.startswith("<!--") or s.startswith("-->"):
        return "Comentario del archivo de diseño; no crea ningún componente visual."
    if "<Form" in s:
        return "Comienza la definición del formulario que administra el GUI Builder."
    if "<Component" in s or "<Container" in s:
        return "Declara un componente o contenedor visual y su clase Java."
    if "<Property" in s:
        return "Configura una propiedad visual, por ejemplo texto, color, tamaño o comportamiento."
    if "<EventHandler" in s:
        return "Asocia un evento visual con el método Java que lo atenderá."
    if "<Constraint" in s or "<AbsoluteConstraints" in s:
        return "Define la posición y el tamaño del componente dentro del diseño."
    if "<Layout" in s:
        return "Indica el administrador de distribución que organiza los componentes hijos."
    if s.startswith("</"):
        return "Cierra la sección XML abierta anteriormente."
    return "Metadato de NetBeans que conserva el estado editable del diseñador visual."


def explain_generic(line: str, number: int, path: Path, context: dict) -> str:
    s = line.strip()
    suffix = path.suffix.lower()
    if suffix == ".md":
        if not s:
            return "Separación entre párrafos del documento."
        if s.startswith("#"):
            return "Título o subtítulo Markdown que organiza la documentación."
        if s.startswith(("- ", "* ")):
            return "Elemento de una lista explicativa."
        if s.startswith("```"):
            return "Abre o cierra un ejemplo de código con formato monoespaciado."
        return "Texto de documentación destinado a explicar una decisión o procedimiento."
    if suffix == ".properties":
        if not s or s.startswith("#"):
            return "Comentario o separación dentro del archivo de propiedades."
        if "=" in s:
            key = s.split("=", 1)[0]
            return f"Asigna un valor a la propiedad {key}; NetBeans/Ant la usa al compilar o ejecutar."
    if suffix == ".ps1":
        if not s:
            return "Separación visual entre instrucciones de PowerShell."
        if s.startswith("#"):
            return "Comentario de PowerShell; no se ejecuta."
        if s.startswith("$") and "=" in s:
            return "Declara o actualiza una variable de PowerShell."
        return "Instrucción de PowerShell utilizada por el proceso auxiliar del proyecto."
    if suffix == ".xml" or path.name == "build.xml":
        if not s:
            return "Separación visual dentro del XML."
        if s.startswith("<!--") or s.startswith("-->"):
            return "Comentario XML para humanos o para NetBeans."
        if s.startswith("</"):
            return "Cierra el elemento XML abierto anteriormente."
        if s.startswith("<"):
            return "Declara un elemento XML de configuración, con sus atributos si corresponde."
    if not s:
        return "Línea en blanco usada para separar secciones."
    return "Contenido de configuración o documentación que completa el archivo."


def explanation_for(path: Path, line: str, number: int, context: dict) -> str:
    if path.suffix.lower() == ".java":
        return explain_java(line, number, context)
    if path.suffix.lower() == ".sql":
        return explain_sql(line, number, context)
    if path.suffix.lower() == ".form":
        return explain_form(line, number, context)
    return explain_generic(line, number, path, context)


def sanitized_line(line: str) -> str:
    if re.search(r"(?i)(CONTRASENA|PASSWORD)\s*=\s*\"[^\"]+\"", line):
        return re.sub(r'("?[^"=]*"?\s*;?)$', '"[VALOR OCULTO]";', line)
    return line


def java_structure(lines: list[str]) -> tuple[list[str], list[tuple[int, str]]]:
    classes, methods = [], []
    for idx, line in enumerate(lines, 1):
        s = line.strip()
        cls = re.search(r"\b(class|record|interface|enum)\s+([A-Za-z_$][\w$]*)", s)
        if cls and cls.group(2) not in classes:
            classes.append(cls.group(2))
        if re.match(r"(public|private|protected)\s+", s) and "(" in s and not s.endswith(";"):
            if any(x in s for x in (" if ", " for ", " while ", " switch ")):
                continue
            signature = re.sub(r"\s+", " ", s)
            methods.append((idx, signature[:170]))
    return classes, methods


TABLE_DESCRIPTIONS = {
    "usuario": "Personas que pueden iniciar sesión. Define credenciales, rol, estado y horario de trabajo.",
    "categoria": "Agrupa productos en las tarjetas principales del menú: Desayuno, Almuerzos, Bebidas, etc.",
    "ingrediente": "Materia prima controlada por inventario, con unidad, existencia y mínimo de reposición.",
    "producto": "Catálogo general. Incluye productos vendibles y productos internos usados como componentes de combos.",
    "producto_ingrediente": "Receta de cada producto y reglas de personalización: quitar, extra, cantidad y recargo.",
    "presentacion_menu": "Formas de vender un producto principal: Individual, WlMenú, Infantil o Combo, cada una con su precio.",
    "grupo_presentacion": "Pregunta o sección dentro de una presentación, con límites mínimo/máximo y permiso de repetir.",
    "opcion_grupo": "Respuesta disponible dentro de un grupo, con recargo y opción predeterminada.",
    "opcion_componente": "Productos concretos que entrega una opción. Una opción puede contener uno o varios productos.",
    "pedido": "Cabecera de la venta: orden, cajero, servicio, estado, pago, total y fecha.",
    "pedido_detalle": "Cada línea del carrito, incluyendo la presentación elegida, cantidad y subtotal.",
    "pedido_opcion": "Captura las elecciones realizadas en cada grupo de la línea para conservar un historial exacto.",
    "pedido_producto": "Expande cada opción a los productos físicos que cocina o entrega el restaurante.",
    "pedido_modificacion": "Cambios aplicados a cada producto interno: SIN ingrediente o EXTRA ingrediente.",
    "movimiento_inventario": "Bitácora de entradas, salidas y ajustes de productos directos o ingredientes.",
    "pago_operacion": "Control de idempotencia del cobro: evita registrar dos veces la misma solicitud de pago.",
}


FIELD_DESCRIPTIONS = {
    "id_usuario": "Identificador único del usuario.",
    "id_categoria": "Identificador de la categoría relacionada.",
    "id_ingrediente": "Identificador del ingrediente relacionado.",
    "id_producto": "Identificador del producto relacionado.",
    "id_producto_principal": "Producto desde el que el cajero abre esta presentación.",
    "id_producto_ingrediente": "Identificador de la regla de receta/personalización.",
    "id_presentacion": "Identificador de la presentación elegida.",
    "id_grupo": "Identificador del grupo de opciones.",
    "id_opcion": "Identificador de la opción elegida.",
    "id_pedido": "Identificador del pedido relacionado.",
    "id_detalle": "Identificador de la línea del pedido.",
    "id_pedido_opcion": "Identificador de la elección guardada.",
    "id_pedido_producto": "Identificador del producto interno del pedido.",
    "id_modificacion": "Identificador de la modificación.",
    "id_movimiento": "Identificador del movimiento de inventario.",
    "nombre": "Nombre visible o copia histórica del nombre.",
    "apellido": "Apellido del usuario.",
    "usuario": "Nombre corto utilizado para iniciar sesión.",
    "correo": "Correo único del usuario.",
    "password_hash": "Resumen criptográfico de la contraseña; no guarda la contraseña en texto normal.",
    "rol": "Permiso principal: ADMINISTRADOR o CAJERO.",
    "estado": "Indica si el registro está activo/disponible.",
    "fecha_creacion": "Fecha y hora de creación automática.",
    "turno": "Turno MANANA o TARDE asignado al usuario.",
    "hora_inicio": "Hora opcional desde la que puede iniciar sesión.",
    "hora_fin": "Hora opcional hasta la que puede iniciar sesión.",
    "unidad_medida": "Unidad usada para contar el ingrediente, por ejemplo UNIDAD, GRAMO o ML.",
    "stock_actual": "Existencia disponible en este momento.",
    "stock_minimo": "Nivel que activa la advertencia de reposición.",
    "descripcion": "Descripción comercial mostrada al cajero.",
    "precio_base": "Precio de referencia del producto individual.",
    "imagen": "Ruta del recurso gráfico del producto.",
    "subcategoria": "Filtro secundario dentro de una categoría.",
    "disponibilidad_menu": "Horario comercial permitido: TODO_DIA, DESAYUNO o ALMUERZO.",
    "tipo_stock": "RECETA descuenta ingredientes; DIRECTO descuenta producto; NINGUNO no controla existencia.",
    "personalizable": "Indica si el cajero puede modificar ingredientes internos.",
    "cantidad_default": "Cantidad consumida por una preparación normal.",
    "permite_quitar": "Permite pedir el producto sin este ingrediente.",
    "permite_extra": "Permite agregar porciones extra de este ingrediente.",
    "cantidad_extra": "Cantidad de inventario que consume cada extra.",
    "precio_extra": "Recargo monetario del extra o de la opción elegida.",
    "max_extras": "Máximo de extras permitidos para este ingrediente.",
    "tipo": "Clasificación controlada del registro según la tabla.",
    "precio": "Precio total de la presentación.",
    "predeterminada": "Opción que aparece seleccionada inicialmente.",
    "minimo": "Cantidad mínima de elecciones requeridas.",
    "maximo": "Cantidad máxima de elecciones permitidas.",
    "permite_repetir": "Autoriza elegir la misma opción más de una vez.",
    "visible": "Decide si el grupo se muestra al cajero.",
    "permite_personalizar": "Autoriza abrir la personalización de los productos internos.",
    "incremento_precio": "Monto que se suma al precio por escoger esta opción.",
    "cantidad": "Número de unidades o cantidad decimal involucrada.",
    "numero_orden": "Número visible que identifica la orden del cliente.",
    "tipo_servicio": "COMER_AQUI o PARA_LLEVAR.",
    "metodo_pago": "EFECTIVO o TARJETA.",
    "monto_recibido": "Dinero entregado por el cliente para calcular el cambio.",
    "total": "Total final cobrado.",
    "fecha_hora": "Momento exacto en que ocurrió el registro.",
    "presentacion": "Copia del nombre de la presentación al momento de vender.",
    "precio_unitario": "Precio de una unidad configurada.",
    "subtotal": "precio_unitario multiplicado por cantidad.",
    "numero": "Número de repetición de una selección dentro del grupo.",
    "grupo": "Copia histórica del nombre del grupo.",
    "opcion": "Copia histórica del nombre de la opción.",
    "ingrediente": "Copia histórica del nombre del ingrediente modificado.",
    "tipo_movimiento": "ENTRADA, SALIDA o AJUSTE.",
    "motivo": "Explicación humana de por qué cambió el inventario.",
    "clave": "UUID o clave única enviada por el cliente para hacer el cobro idempotente.",
    "solicitud": "Copia serializada de la solicitud original.",
    "comprobante": "Texto del comprobante generado tras cobrar.",
    "referencia": "Referencia externa o administrativa del pago.",
}


TABLE_EXAMPLES = {
    "usuario": "1 | Ana | López | admin | ana@waldonalds.gt | [hash] | ADMINISTRADOR | 1 | ...",
    "categoria": "2 | Almuerzos | 1",
    "ingrediente": "37 | Pepinillo | UNIDAD | 250.00 | 40.00 | 1",
    "producto": "12 | 2 | Big Mac | ... | 41.00 | ... | Hamburguesas | ALMUERZO | RECETA | 1 | 0 | 0 | 1",
    "producto_ingrediente": "85 | 12 | 37 | 2.00 | 1 | 1 | 1.00 | 1.00 | 3 | 1",
    "presentacion_menu": "21 | 12 | WlMenú | MENU | 61.00 | 0 | 1",
    "grupo_presentacion": "44 | 21 | Elige la bebida | 1 | 1 | 0 | 1 | 1 | 1",
    "opcion_grupo": "190 | 44 | Coca Cola | 0.00 | 1 | 1",
    "opcion_componente": "201 | 190 | 65 | 1",
    "pedido": "500 | 104 | 7 | COMER_AQUI | PAGADO | EFECTIVO | 100.00 | 62.00 | 2026-10-04 12:30",
    "pedido_detalle": "700 | 500 | 21 | Big Mac | WlMenú | 1 | 62.00 | 62.00",
    "pedido_opcion": "900 | 700 | 44 | 190 | 1 | Elige la bebida | Coca Cola | 0.00",
    "pedido_producto": "1000 | 900 | 65 | Coca Cola | 1",
    "pedido_modificacion": "1200 | 1001 | 85 | SIN | Pepinillo | 2.00 | 0.00",
    "movimiento_inventario": "1500 | NULL | 37 | 500 | SALIDA | 2.00 | Preparación de pedido | 2026-10-04 12:30",
    "pago_operacion": "550e8400-e29b-41d4-a716-446655440000 | {solicitud} | 500 | {comprobante} | POS-104",
}


def parse_schema(path: Path):
    lines = read_lines(path)
    tables = []
    current = None
    for line in lines:
        s = line.strip().rstrip(",")
        m = re.match(r"CREATE TABLE\s+(\w+)\s*\(", s, re.I)
        if m:
            current = {"name": m.group(1), "fields": [], "relations": []}
            tables.append(current)
            continue
        if current is None:
            continue
        if s.startswith(");"):
            current = None
            continue
        fk = re.search(r"FOREIGN KEY\s*\((\w+)\)\s+REFERENCES\s+(\w+)\((\w+)\)", s, re.I)
        if fk:
            current["relations"].append((fk.group(1), fk.group(2), fk.group(3)))
            continue
        if not s or s.upper().startswith(("PRIMARY KEY", "UNIQUE", "CONSTRAINT")):
            continue
        fm = re.match(r"(\w+)\s+(.+)", s)
        if fm:
            name, definition = fm.group(1), fm.group(2)
            type_match = re.match(r"([A-Z]+(?:\([^)]*\))?)", definition, re.I)
            dtype = type_match.group(1) if type_match else definition.split()[0]
            rules = definition[len(dtype):].strip()
            current["fields"].append((name, dtype, rules))
    return tables


def add_cover(story, stats):
    story.append(Spacer(1, 5 * mm))
    story.append(CoverBand())
    story.append(Spacer(1, 10 * mm))
    data = [
        ["Versión documentada", datetime.now().strftime("%d/%m/%Y")],
        ["Código fuente", f"{stats['java_files']} archivos Java / {stats['java_lines']:,} líneas"],
        ["Interfaz visual", f"{stats['form_files']} formularios NetBeans / {stats['form_lines']:,} líneas XML"],
        ["Base de datos", f"{stats['sql_files']} scripts SQL / {stats['sql_lines']:,} líneas"],
        ["Recursos", f"{stats['resources']} imágenes, fuentes y librerías catalogadas"],
    ]
    story.append(table(data, widths=[55 * mm, 105 * mm], header=False, font_size=9, repeat=0))
    story.append(Spacer(1, 6 * mm))
    story.append(P("Objetivo: que el equipo pueda explicar qué hace el sistema, cómo circulan los datos, cómo se relacionan las tablas y qué significa cada línea relevante del código durante la presentación.", "Callout"))
    story.append(PageBreak())


def add_scope(story, stats):
    section("1. Cómo usar este manual", story)
    story.append(P("Este documento está diseñado en capas. Primero enseña conceptos y flujos; después explica la arquitectura y la base de datos; finalmente incluye un anexo de referencia con cada archivo de texto y cada línea anotada. No hace falta memorizar el anexo: úsalo para buscar una línea concreta cuando el profesor pregunte."))
    story.append(rich("<b>Cobertura verificable</b>: " + esc(f"{stats['text_files']} archivos de texto documentados, {stats['text_lines']:,} líneas anotadas y {stats['resources']} recursos binarios catalogados.")))
    story.append(bullet("Ruta rápida para presentar: capítulos 2, 3, 5, 7, 8 y 13."))
    story.append(bullet("Ruta para aprender Java: capítulos 4 y 6, luego buscar un archivo en el anexo."))
    story.append(bullet("Ruta para aprender la base de datos: capítulos 8, 9 y 10."))
    story.append(bullet("Ruta para depurar: capítulos 11 y 12, más el índice de archivos."))
    story.append(P("Alcance de 'cada archivo': se explican línea por línea los archivos Java, SQL, .form, XML, properties, Markdown, PowerShell y manifiestos. Las imágenes, fuentes y JAR no tienen líneas de código; se catalogan por carpeta, tipo y utilidad." , "Callout"))
    story.append(Spacer(1, 4 * mm))
    section("Mapa del documento", story, 2)
    rows = [["Parte", "Qué aprenderán"]]
    rows += [
        ["I. Fundamentos", "Conceptos de Java, Swing, JDBC, SQL y la estructura de NetBeans."],
        ["II. Arquitectura", "Cómo se comunican interfaz, modelos, DAO/CRUD y MySQL."],
        ["III. Base de datos", "Cada tabla, campo, relación, ejemplo y flujo de inventario/pedido."],
        ["IV. Casos prácticos", "Login, WlMenú, combo predefinido, Cajita, cobro y administración."],
        ["V. Presentación", "Guion, demostración, preguntas frecuentes y riesgos conocidos."],
        ["VI. Referencia", "Catálogo de archivos y explicación línea por línea."],
    ]
    story.append(table(rows, widths=[50 * mm, 190 * mm]))
    story.append(PageBreak())


def add_system_overview(story):
    section("2. Qué es Waldonald's", story)
    story.append(P("Waldonald's es un sistema POS de escritorio para restaurante. Tiene dos áreas: el cajero arma y cobra pedidos; el administrador mantiene usuarios, productos, recetas, presentaciones, inventario y reportes. La información persiste en MySQL y la interfaz está construida con Java Swing."))
    nodes = [
        ("main", "Main.main", .03, .60, .12, .18, YELLOW),
        ("load", "Pantalla\nde carga", .20, .60, .13, .18, colors.white),
        ("login", "Login", .38, .60, .12, .18, colors.white),
        ("admin", "Administrador", .58, .75, .15, .16, colors.white),
        ("cash", "Cajero", .58, .43, .15, .16, colors.white),
        ("dao", "DAO / CRUD", .79, .60, .14, .18, YELLOW),
        ("db", "MySQL\nwaldonalds", .79, .15, .14, .18, NAVY),
    ]
    edges = [
        ("main", "load", "inicia"), ("load", "login", "8 segundos"),
        ("login", "admin", "rol ADMINISTRADOR"), ("login", "cash", "rol CAJERO"),
        ("admin", "dao", "consulta/edita"), ("cash", "dao", "vende"), ("dao", "db", "JDBC"),
    ]
    story.append(FlowDiagram(nodes, edges, title="Flujo de inicio y separación por roles"))
    story.append(Spacer(1, 4 * mm))
    rows = [["Actor", "Puede hacer", "No debería hacer"]]
    rows += [
        ["Cajero", "Ver productos por horario, configurar presentaciones, modificar ingredientes, cobrar y ver comprobante.", "Editar catálogos, usuarios o reglas comerciales."],
        ["Administrador", "Gestionar usuarios, menú, recetas, inventario, movimientos y reportes.", "Registrar una venta desde el flujo administrativo."],
        ["MySQL", "Persistir reglas, pedidos, movimientos y estados.", "Decidir la interacción visual del usuario."],
    ]
    story.append(table(rows, widths=[35 * mm, 110 * mm, 95 * mm]))
    story.append(PageBreak())


def add_architecture(story):
    section("3. Arquitectura del proyecto", story)
    story.append(P("La aplicación sigue una separación por responsabilidades. No es un framework formal, pero sí aplica una arquitectura por capas: las vistas dibujan y reciben clics, los modelos transportan datos, DAO/CRUD hablan con MySQL y las utilidades resuelven tareas compartidas."))
    rows = [["Capa/carpeta", "Responsabilidad", "Ejemplos"]]
    rows += [
        ["Main / Vistas / Login", "Arranque y autenticación.", "main, PantallaCarga, Login"],
        ["GUI_CAJERO", "Venta, carrito, configuración y cobro.", "Cajero, PedidoPanel, ConfiguradorProductoPanel"],
        ["GUI_ADMINISTRADOR", "Mantenimiento y análisis del negocio.", "MenuAdmin, GestionMenuPanel, InventarioPanel, ReportesPanel"],
        ["Modelos", "Datos tipados que viajan entre capas.", "Producto, LineaPedido, SolicitudPago, Usuario"],
        ["DAO", "Consultas de casos de uso complejos.", "PagoDAO, ConfiguracionMenuDAO, InventarioDAO"],
        ["CRUD", "Altas, consultas, cambios y estado de catálogos.", "ProductoCRUD, UsuarioCRUD, IngredienteCRUD"],
        ["Componentes / Labels", "Controles visuales reutilizables.", "BotonRedondeado, TablaAdministrativa, LabelEscalable"],
        ["Utilidades", "Sesión, horarios, imágenes, seguridad y exportación.", "SesionUsuario, HorarioMenu, SeguridadContrasena"],
        ["sql", "Estructura y datos iniciales.", "base_datos_completa.sql, datos_menu_nueva_base.sql"],
    ]
    story.append(table(rows, widths=[48 * mm, 105 * mm, 87 * mm]))
    story.append(Spacer(1, 5 * mm))
    nodes = [
        ("ui", "Interfaz\nSwing", .04, .57, .17, .22, colors.white),
        ("model", "Modelos\ninmutables/mutables", .30, .57, .19, .22, YELLOW),
        ("logic", "DAO + CRUD\nreglas y SQL", .58, .57, .18, .22, colors.white),
        ("db", "MySQL\n16 tablas", .82, .57, .13, .22, NAVY),
        ("util", "Utilidades\nhorario, sesión, hash", .31, .13, .28, .18, colors.HexColor("#DFF3E9")),
    ]
    edges = [
        ("ui", "model", "crea/lee"), ("model", "logic", "parámetros"),
        ("logic", "db", "JDBC"), ("util", "logic", "apoya"), ("util", "ui", "apoya"),
    ]
    story.append(FlowDiagram(nodes, edges, height=90 * mm, title="Dependencias principales"))
    story.append(PageBreak())


def add_fundamentals(story):
    section("4. Java desde cero: cómo leer el código", story)
    terms = [
        ("package", "Carpeta lógica de una clase. Ejemplo: package DAO;"),
        ("import", "Hace disponible una clase externa. Ejemplo: import java.sql.Connection;"),
        ("class", "Molde que agrupa datos y métodos."),
        ("record", "Modelo compacto e inmutable; Java crea constructor y accesores automáticamente."),
        ("atributo", "Variable que pertenece al objeto y conserva su estado."),
        ("método", "Bloque con nombre que realiza una tarea y puede recibir/devolver datos."),
        ("constructor", "Método especial que se ejecuta al crear un objeto con new."),
        ("public/private", "Controla desde dónde se puede acceder a un elemento."),
        ("static", "Pertenece a la clase y no a una instancia concreta."),
        ("final", "Impide reasignar una referencia o sobrescribir una definición."),
        ("if/else", "Elige entre rutas según una condición."),
        ("for/while", "Repite instrucciones."),
        ("try/catch", "Ejecuta una operación riesgosa y maneja el error."),
        ("listener", "Función que espera un clic, tecla o movimiento del usuario."),
        ("Scanner", "Clase común para leer datos de consola. Este proyecto casi no la usa porque obtiene datos desde componentes Swing."),
    ]
    story.append(table([["Concepto", "Explicación"]] + terms, widths=[45 * mm, 195 * mm]))
    section("Ejemplo mínimo explicado", story, 2)
    story.append(code_block("public BigDecimal total() {\n    return lineas.stream()\n        .map(LineaPedido::subtotal)\n        .reduce(BigDecimal.ZERO, BigDecimal::add);\n}"))
    story.append(P("public permite llamar al método desde otra clase. BigDecimal es el tipo devuelto y se usa para dinero. total es el nombre. stream recorre las líneas; map obtiene cada subtotal; reduce los suma comenzando en cero; return entrega el resultado."))
    section("Swing y el hilo visual", story, 2)
    story.append(P("Swing dibuja la interfaz en el Event Dispatch Thread. Por eso Main usa EventQueue.invokeLater. Las consultas que pueden tardar utilizan SwingWorker para no congelar la ventana. initComponents es generado por NetBeans y crea/posiciona los controles definidos en el archivo .form."))
    story.append(P("Regla de oro: no borrar los comentarios //GEN-BEGIN y //GEN-END. Si desaparecen, NetBeans abre el formulario en modo de solo lectura porque ya no reconoce sus bloques protegidos.", "Callout"))
    story.append(PageBreak())


def add_runtime(story):
    section("5. Cómo preparar y ejecutar el proyecto", story)
    rows = [["Requisito", "Versión/uso"]]
    rows += [
        ["JDK", "Java 21, según nbproject/project.properties."],
        ["IDE", "NetBeans con soporte para proyectos Ant y GUI Builder."],
        ["Base de datos", "MySQL local con acceso para crear y usar waldonalds."],
        ["Driver JDBC", "lib/mysql-connector-j-26.7.0.jar."],
        ["Layout", "lib/absolutelayout/AbsoluteLayout.jar."],
        ["Clase principal", "Main.main."],
    ]
    story.append(table(rows, widths=[55 * mm, 185 * mm]))
    steps = [
        "Abrir el proyecto Waldonalds en NetBeans.",
        "Ejecutar sql/base_datos_completa.sql para crear las tablas.",
        "Ejecutar sql/datos_menu_nueva_base.sql para cargar categorías, productos, recetas y presentaciones.",
        "Revisar usuario y contraseña de MySQL en Conexion/Conexion.java. Para producción deben salir del código y leerse de variables de entorno.",
        "Confirmar que los JAR de lib aparecen en Libraries.",
        "Ejecutar Clean and Build; después Run. La pantalla de carga abre Login y el rol decide la ventana siguiente.",
    ]
    for i, step in enumerate(steps, 1):
        story.append(rich(f"<b>{i}.</b> {esc(step)}"))
    section("Orden correcto de los scripts SQL", story, 2)
    story.append(code_block("1. sql/base_datos_completa.sql\n2. sql/datos_menu_nueva_base.sql\n3. sql/pagos.sql (solo si se parte de un esquema antiguo)\n4. migraciones específicas (solo para bases existentes)"))
    story.append(P("Para una instalación nueva, no se deben mezclar migraciones antiguas sin revisar. El esquema completo ya contiene las columnas actuales. Los scripts de migración existen para instalaciones previas." , "Callout"))
    story.append(PageBreak())


def add_flows(story):
    section("6. Flujos funcionales principales", story)
    flows = [
        ("Inicio de sesión", "Login lee usuario y contraseña -> consulta usuario activo -> verifica hash -> valida turno -> guarda SesionUsuario -> abre administrador o cajero."),
        ("Catálogo del cajero", "CategoriasPanel elige categoría -> ProductoDAO filtra por estado, presentación y horario -> SubCategoriasPanel crea TarjetaProducto."),
        ("Configurar producto", "ConfiguracionMenuDAO carga presentaciones -> el cajero elige Individual/WlMenú/Combo -> completa grupos -> personaliza cada componente -> se crea LineaPedido."),
        ("Carrito", "PedidoPanel agrega líneas, cambia cantidades, quita líneas y calcula el total con BigDecimal."),
        ("Cobro", "SolicitudPago reúne clave, servicio, método y líneas -> PagoDAO revalida todo -> transacción SQL -> descuento de inventario -> comprobante."),
        ("Inventario", "InventarioPanel consulta ArticuloInventario -> InventarioDAO registra ENTRADA/SALIDA/AJUSTE -> actualiza stock y bitácora."),
        ("Administración", "MenuAdmin cambia paneles -> CRUD/DAO ejecutan acciones -> la tabla se recarga y muestra estado actualizado."),
        ("Reportes", "ReportesPanel pide una fecha -> ReporteDAO agrupa pedidos pagados/cancelados -> filtros locales -> exportación CSV o impresión."),
    ]
    story.append(table([["Flujo", "Secuencia"]] + flows, widths=[45 * mm, 195 * mm]))
    section("Por qué se vuelve a validar al cobrar", story, 2)
    story.append(P("La interfaz ayuda al cajero, pero no es la autoridad final: entre configurar y pagar pudo cambiar el precio, el stock o una regla. PagoDAO vuelve a leer las reglas con la misma conexión y bloquea existencias con FOR UPDATE. Así evita vender inventario inexistente o aceptar una selección inválida."))
    story.append(code_block("setAutoCommit(false) -> validar -> bloquear stock -> INSERT pedido -> INSERT detalle/opciones/productos/modificaciones -> UPDATE stock -> INSERT movimientos -> commit()\nSi algo falla: rollback()"))
    story.append(PageBreak())


def add_menu_model(story):
    section("7. Modelo de productos, WlMenú, Cajita y combos", story)
    story.append(P("La clave del diseño es que una venta no es solamente un producto. Un producto abre una o más presentaciones. Cada presentación contiene grupos; cada grupo contiene opciones; cada opción entrega uno o varios productos reales; y cada producto real puede tener modificaciones de receta."))
    rows = [
        ["Nivel", "Ejemplo Big Mac WlMenú", "Regla"],
        ["Producto principal", "Big Mac", "Es la tarjeta que abrió el cajero."],
        ["Presentación", "WlMenú - Q61", "Determina precio y tipo MENU."],
        ["Grupo", "Elige complemento", "mínimo 1, máximo 1."],
        ["Opción", "WlPatatas +Q5", "Alternativa elegible y su recargo."],
        ["Componente", "1 producto WlPatatas", "Producto físico que se prepara/entrega."],
        ["Modificación", "Big Mac sin pepinillo", "Afecta solo ese componente, no toda la línea."],
    ]
    story.append(table(rows, widths=[42 * mm, 73 * mm, 125 * mm]))
    nodes = [
        ("p", "producto", .03, .60, .12, .20, YELLOW),
        ("pm", "presentación", .20, .60, .14, .20, colors.white),
        ("g", "grupo", .39, .60, .12, .20, colors.white),
        ("o", "opción", .56, .60, .12, .20, colors.white),
        ("c", "componente\nproducto", .73, .60, .16, .20, YELLOW),
        ("r", "receta y\nmodificaciones", .73, .18, .16, .20, colors.HexColor("#DFF3E9")),
    ]
    edges = [("p", "pm", "1:N"), ("pm", "g", "1:N"), ("g", "o", "1:N"), ("o", "c", "1:N"), ("r", "c", "aplica a cada uno")]
    story.append(FlowDiagram(nodes, edges, height=82 * mm, title="Jerarquía configurable"))
    section("Ejemplo mental: combo de dos hamburguesas", story, 2)
    story.append(P("El grupo puede requerir dos elecciones o la opción puede contener dos componentes. Cada ProductoPedido tiene su propia lista de ModificacionPedido. Por eso una hamburguesa puede ir sin pepinillos y la otra con todo, aunque ambas pertenezcan a la misma LineaPedido."))
    story.append(PageBreak())


def add_database(story, tables):
    section("8. Base de datos completa", story)
    story.append(P("La base utiliza claves primarias para identificar filas y claves foráneas para conectarlas. Los campos de estado permiten desactivar sin borrar el historial. Los nombres copiados en las tablas de pedido son snapshots históricos: si mañana cambia el nombre del producto, el comprobante viejo conserva lo vendido."))
    nodes = [
        ("catalog", "CATÁLOGO\ncategoria - producto\ningrediente - receta", .03, .61, .22, .23, colors.white),
        ("menu", "MENÚ CONFIGURABLE\npresentación - grupo\nopción - componente", .31, .61, .24, .23, YELLOW),
        ("sale", "VENTA\npedido - detalle - opción\nproducto - modificación", .63, .61, .28, .23, colors.white),
        ("inv", "INVENTARIO\nmovimiento_inventario", .14, .17, .23, .18, colors.HexColor("#DFF3E9")),
        ("auth", "SEGURIDAD Y PAGO\nusuario - pago_operacion", .58, .17, .28, .18, colors.HexColor("#FBE3E5")),
    ]
    edges = [("catalog", "menu", "configura"), ("menu", "sale", "se elige"), ("inv", "catalog", "actualiza stock"), ("auth", "sale", "autoriza/idempotencia"), ("sale", "inv", "genera salidas")]
    story.append(FlowDiagram(nodes, edges, height=92 * mm, title="Mapa relacional por dominios"))
    story.append(Spacer(1, 4 * mm))
    story.append(table([["Tabla", "Función", "Campos"]] + [[t["name"], TABLE_DESCRIPTIONS.get(t["name"], ""), str(len(t["fields"]))] for t in tables], widths=[50 * mm, 170 * mm, 20 * mm]))
    story.append(PageBreak())
    for idx, t in enumerate(tables, 1):
        section(f"8.{idx} Tabla {t['name']}", story, 2)
        story.append(P(TABLE_DESCRIPTIONS.get(t["name"], "Tabla del sistema.")))
        rows = [["Campo", "Tipo", "Reglas", "Para qué sirve"]]
        for name, dtype, rules in t["fields"]:
            rows.append([name, dtype, rules or "-", FIELD_DESCRIPTIONS.get(name, f"Dato {name.replace('_', ' ')} de esta tabla.")])
        story.append(table(rows, widths=[43 * mm, 43 * mm, 68 * mm, 86 * mm], font_size=6.5))
        if t["relations"]:
            story.append(P("Relaciones: " + "; ".join(f"{field} -> {other}.{target}" for field, other, target in t["relations"]), "SmallManual"))
        story.append(P("Registro de ejemplo (orden de columnas del script): " + TABLE_EXAMPLES.get(t["name"], "Consulte el script de datos."), "Callout"))
        if idx % 2 == 0:
            story.append(PageBreak())
    if len(tables) % 2 != 0:
        story.append(PageBreak())


def add_database_rules(story):
    section("9. Reglas de datos que deben poder explicar", story)
    rules = [
        ("tipo_stock = RECETA", "El producto no descuenta su propio stock; consume ingrediente según producto_ingrediente."),
        ("tipo_stock = DIRECTO", "El producto se entrega como unidad terminada y descuenta producto.stock_actual."),
        ("tipo_stock = NINGUNO", "Producto lógico o agrupador sin control directo de existencias."),
        ("disponibilidad_menu", "Horario comercial del producto: TODO_DIA, DESAYUNO o ALMUERZO. HorarioMenu decide si se puede vender."),
        ("minimo / maximo", "Cantidad de elecciones obligatorias y límite dentro de un grupo."),
        ("permite_repetir", "Permite elegir varias veces la misma opción si el grupo acepta más de una selección."),
        ("permite_personalizar", "Decide si los componentes internos abren cambios de ingredientes."),
        ("predeterminada", "Selección inicial para acelerar el trabajo del cajero; debe seguir cumpliendo reglas."),
        ("estado", "Borrado lógico. Oculta un registro sin destruir relaciones o ventas históricas."),
        ("snapshot de nombres", "pedido_detalle, pedido_opcion, pedido_producto y pedido_modificacion copian textos para preservar historia."),
        ("clave de pago", "Hace idempotente el cobro: repetir la misma solicitud devuelve el mismo resultado y no duplica venta."),
    ]
    story.append(table([["Regla/campo", "Interpretación"]] + rules, widths=[58 * mm, 182 * mm]))
    section("Ejemplo de cálculo de precio", story, 2)
    story.append(code_block("Presentación WlMenú                 Q61.00\nCambio a WlPatatas                  + Q5.00\nExtra queso en la hamburguesa       + Q3.00\nPrecio unitario configurado          Q69.00\nCantidad 2                           x 2\nSubtotal                            Q138.00"))
    story.append(P("El precio se calcula con BigDecimal. No se debe usar double para dinero porque algunos decimales binarios generan diferencias de centavos."))
    section("Ejemplo de consumo de inventario", story, 2)
    story.append(P("Si Big Mac es RECETA, PagoDAO suma la cantidad_default de cada ingrediente, resta los ingredientes quitados y agrega cantidad_extra por cada extra. Si Coca Cola es DIRECTO, descuenta unidades del producto. Todo ocurre dentro de la misma transacción del pedido."))
    story.append(PageBreak())


def add_practical_cases(story):
    section("10. Casos prácticos para demostrar", story)
    cases = [
        ("Caso A - Individual", "Abrir Big Mac, elegir Individual, quitar pepinillo, agregar extra queso y añadir al carrito. Explicar que la modificación pertenece al ProductoPedido interno."),
        ("Caso B - WlMenú", "Abrir la misma tarjeta, cambiar a WlMenú, elegir complemento y bebida. Mostrar cómo cada opción proviene de un grupo con mínimo=1 y máximo=1."),
        ("Caso C - Combo predefinido", "Abrir Caja Grande, configurar sus elecciones, modificar cada hamburguesa por separado y observar un único subtotal de combo."),
        ("Caso D - Cajita", "Elegir entrada, complemento, bebida y juguete. Demostrar opciones mutuamente excluyentes y recargos."),
        ("Caso E - Horario", "Intentar ver un desayuno fuera de horario. ProductoDAO/HorarioMenu deben ocultarlo o ConfiguracionMenuDAO rechazarlo."),
        ("Caso F - Inventario", "Registrar una entrada, revisar stock, cobrar un producto y comprobar la salida automática ligada al pedido."),
        ("Caso G - Reporte", "Elegir la fecha del pedido, filtrar por cajero/método/servicio y exportar CSV."),
    ]
    story.append(table([["Demostración", "Qué deben explicar"]] + cases, widths=[48 * mm, 192 * mm]))
    section("Objeto conceptual de una línea del carrito", story, 2)
    story.append(code_block("LineaPedido {\n  presentacion: 'WlMenú', cantidad: 1, precioUnitario: 69.00,\n  opciones: [\n    { grupo: 'Principal', productos: [Big Mac { SIN Pepinillo, EXTRA Queso }] },\n    { grupo: 'Complemento', productos: [WlPatatas] },\n    { grupo: 'Bebida', productos: [Coca Cola] }\n  ]\n}"))
    story.append(P("En el código real estos datos son records Java: LineaPedido, OpcionPedido, ProductoPedido y ModificacionPedido. Cada método con... crea una copia con una lista nueva, evitando modificar accidentalmente la selección anterior."))
    story.append(PageBreak())


def add_security_and_debug(story):
    section("11. Seguridad, consistencia y puntos de mejora", story)
    rows = [["Tema", "Qué hace hoy", "Recomendación profesional"]]
    rows += [
        ["Contraseñas", "SeguridadContrasena genera/verifica hashes y Login migra claves antiguas.", "No aceptar contraseñas planas nuevas y usar un algoritmo adaptativo como BCrypt/Argon2."],
        ["Credenciales MySQL", "Están configuradas en Conexion.java.", "Mover URL, usuario y contraseña a variables de entorno o un archivo local no versionado."],
        ["SQL injection", "Las consultas usan PreparedStatement en las operaciones principales.", "Mantener parámetros; nunca concatenar texto ingresado por el usuario."],
        ["Cobro doble", "pago_operacion usa una clave única para idempotencia.", "Generar una clave por intento lógico y reutilizarla al reintentar por error de red."],
        ["Inventario", "PagoDAO bloquea y descuenta dentro de la transacción.", "Mantener índices y pruebas concurrentes cuando haya múltiples cajas."],
        ["Borrado", "estado permite desactivar registros.", "Evitar DELETE de datos con historia, salvo tablas auxiliares seguras."],
        ["GUI Builder", "Los .form dependen de bloques protegidos en Java.", "Editar el diseño desde NetBeans y conservar las marcas GEN-BEGIN/GEN-END."],
    ]
    story.append(table(rows, widths=[40 * mm, 95 * mm, 105 * mm]))
    section("Guía de diagnóstico", story, 2)
    diagnostics = [
        ("No conecta a MySQL", "Revisar servicio, puerto 3306, base waldonalds, credenciales y JAR JDBC."),
        ("Producto no aparece", "Revisar estado, categoría, presentación activa, disponibilidad_menu y consulta de subcategoría."),
        ("No aparece WlMenú", "Debe existir presentacion_menu tipo MENU activa con grupos/opciones/componentes válidos."),
        ("No deja agregar al carrito", "Algún grupo no cumple mínimo/máximo o un componente está inactivo/fuera de horario."),
        ("Cobro falla por stock", "Comparar stock_actual con consumo de recetas y productos DIRECTO."),
        ("NetBeans dice form corrupted", "Restaurar comentarios protegidos y mantener sincronizados .java y .form."),
        ("La interfaz se congela", "Mover consultas largas a SwingWorker y actualizar Swing en done()."),
    ]
    story.append(table([["Síntoma", "Qué revisar"]] + diagnostics, widths=[65 * mm, 175 * mm]))
    story.append(PageBreak())


def add_tests(story):
    section("12. Pruebas y verificación", story)
    rows = [["Prueba", "Objetivo"]]
    for path in sorted((ROOT / "test").glob("*.java")):
        rows.append([path.name, file_purpose(path)])
    rows.append(["scripts/ValidarDatosMenu.java", "Ejecuta scripts en una base temporal y valida que no haya duplicados, recetas incompletas ni relaciones inválidas."])
    rows.append(["VisualCheckMenuAdmin.java", "Abre una comprobación visual del menú administrativo."])
    story.append(table(rows, widths=[63 * mm, 177 * mm]))
    story.append(P("Estas pruebas son clases con main, no JUnit. Se compilan con el proyecto y se ejecutan individualmente. Algunas usan MySQL real; antes de correrlas hay que leer si crean o eliminan datos temporales."))
    section("Qué significa una prueba útil", story, 2)
    story.append(bullet("Preparar un estado conocido."))
    story.append(bullet("Ejecutar una acción concreta."))
    story.append(bullet("Comparar el resultado real con el esperado."))
    story.append(bullet("Limpiar datos temporales y fallar con un mensaje claro si algo no coincide."))
    story.append(PageBreak())


def add_presentation_guide(story):
    section("13. Guion sugerido para la presentación", story)
    guide = [
        ("1 minuto", "Problema", "Un restaurante necesita controlar opciones, personalizaciones, horarios e inventario; un combo no puede ser un producto estático."),
        ("2 minutos", "Arquitectura", "Mostrar las capas y explicar que Swing no contiene directamente toda la lógica SQL."),
        ("3 minutos", "Cajero", "Demostrar Individual/WlMenú, grupo obligatorio, personalización interna y carrito."),
        ("2 minutos", "Cobro", "Explicar transacción, revalidación, idempotencia y descuento de inventario."),
        ("3 minutos", "Administrador", "Usuarios, menú, recetas/presentaciones, inventario y reportes."),
        ("2 minutos", "Base de datos", "Mostrar el recorrido producto -> presentación -> grupo -> opción -> componente y pedido -> detalle -> opción -> producto -> modificación."),
        ("1 minuto", "Cierre", "Ventajas, pruebas y mejoras futuras."),
    ]
    story.append(table([["Tiempo", "Sección", "Mensaje"]] + guide, widths=[28 * mm, 45 * mm, 167 * mm]))
    section("Preguntas que podrían hacerles", story, 2)
    qa = [
        ("¿Por qué no guardaron el combo como texto?", "Porque cocina, inventario y personalización necesitan conocer los productos internos y cada elección."),
        ("¿Por qué existen snapshots de nombres?", "Para que una venta histórica no cambie cuando se renombra un producto o una opción."),
        ("¿Por qué BigDecimal?", "Porque el dinero requiere precisión decimal exacta."),
        ("¿Qué evita vender sin stock?", "PagoDAO calcula consumo, bloquea filas con FOR UPDATE y confirma dentro de una transacción."),
        ("¿Para qué sirve pago_operacion?", "Evita que un reintento registre dos pedidos idénticos."),
        ("¿Cuál es la diferencia entre DAO y modelo?", "El modelo contiene datos; el DAO sabe leerlos y escribirlos en la base."),
        ("¿Por qué hay .java y .form?", "Java contiene comportamiento; .form conserva el diseño editable de NetBeans."),
    ]
    story.append(table([["Pregunta", "Respuesta breve"]] + qa, widths=[80 * mm, 160 * mm]))
    story.append(PageBreak())


def add_glossary(story):
    section("14. Glosario", story)
    terms = [
        ("API", "Contrato mediante el que dos piezas de software se comunican."),
        ("CRUD", "Create, Read, Update, Delete: operaciones básicas de mantenimiento."),
        ("DAO", "Clase que centraliza acceso a datos de un caso de uso."),
        ("DTO/modelo", "Objeto que transporta datos tipados."),
        ("JDBC", "API estándar de Java para conectarse y ejecutar SQL."),
        ("Swing", "Biblioteca de interfaz gráfica de escritorio de Java."),
        ("EDT", "Hilo de Swing encargado de dibujar y procesar eventos visuales."),
        ("GUI Builder", "Diseñador visual de NetBeans que mantiene archivos .form y parte de initComponents."),
        ("Clave primaria", "Columna que identifica una fila de forma única."),
        ("Clave foránea", "Columna que apunta a una fila de otra tabla."),
        ("Transacción", "Grupo de cambios que se confirma completo o se revierte completo."),
        ("Rollback", "Deshacer los cambios pendientes de una transacción fallida."),
        ("Idempotencia", "Repetir la misma petición no produce un segundo efecto."),
        ("Hash", "Resultado no reversible usado para verificar contraseñas sin guardarlas en claro."),
        ("Snapshot", "Copia histórica de un valor tal como era en el momento de la venta."),
        ("Listener", "Objeto que reacciona a un evento del usuario."),
        ("Renderizador", "Código que decide cómo se dibuja una celda o control."),
        ("Paginación", "Dividir muchos registros en páginas pequeñas."),
        ("Stock", "Existencia disponible de un producto o ingrediente."),
    ]
    story.append(table([["Término", "Definición"]] + terms, widths=[48 * mm, 192 * mm]))
    story.append(PageBreak())


def add_file_catalog(story, files):
    section("15. Catálogo de archivos", story)
    story.append(P("Esta sección permite localizar rápidamente una clase. El anexo posterior contiene la explicación línea por línea."))
    groups = defaultdict(list)
    for path in files:
        rel = str(path.relative_to(ROOT)).replace("\\", "/")
        top = rel.split("/", 2)
        if rel.startswith("src/") and len(top) > 1:
            group = "src/" + top[1]
        else:
            group = top[0]
        groups[group].append(path)
    for group in sorted(groups):
        section(group, story, 2)
        rows = [["Archivo", "Líneas/tamaño", "Utilidad"]]
        for path in groups[group]:
            rel = str(path.relative_to(ROOT)).replace("\\", "/")
            if is_text_file(path):
                count = len(read_lines(path))
                size = f"{count} líneas"
            else:
                size = f"{path.stat().st_size / 1024:.1f} KB"
            rows.append([rel, size, file_purpose(path) if is_text_file(path) else resource_purpose(path)])
        story.append(table(rows, widths=[86 * mm, 28 * mm, 126 * mm], font_size=6.2))
        story.append(Spacer(1, 3 * mm))
    story.append(PageBreak())


def resource_purpose(path: Path) -> str:
    rel = str(path.relative_to(ROOT)).replace("\\", "/")
    ext = path.suffix.lower()
    if ext == ".png":
        if "/productos/" in rel.lower() or "/hamburguesas/" in rel.lower() or "/desayunos/" in rel.lower() or "/bebidas/" in rel.lower() or "/postres/" in rel.lower() or "/antojos/" in rel.lower() or "/wlcafe/" in rel.lower() or "/cajita feliz/" in rel.lower() or "/para compartir/" in rel.lower():
            return "Imagen de producto usada por las tarjetas del menú."
        return "Recurso visual de interfaz: logotipo, icono, fondo o indicador."
    if ext == ".ttf":
        return "Archivo de fuente DM Sans para mantener la tipografía visual del sistema."
    if ext == ".jar":
        if "mysql" in path.name.lower():
            return "Driver JDBC que permite a Java comunicarse con MySQL."
        if "absolute" in path.name.lower():
            return "Librería de NetBeans para AbsoluteLayout."
        return "Librería compilada requerida por el proceso de construcción."
    return "Recurso binario utilizado por el proyecto."


def add_source_appendix(story, text_files):
    section("16. Anexo: explicación archivo por archivo y línea por línea", story)
    story.append(P("Formato de lectura: la primera columna es el número original, la segunda conserva el código y la tercera explica su función en lenguaje sencillo. Las credenciales literales se ocultan en el PDF por seguridad, pero su línea se sigue explicando."))
    story.append(P("Las explicaciones de líneas triviales como llaves, cierres XML o separaciones son deliberadamente breves. El bloque de estructura antes de cada archivo explica el significado conjunto de sus métodos.", "Callout"))
    total = len(text_files)
    for file_index, path in enumerate(text_files, 1):
        rel = str(path.relative_to(ROOT)).replace("\\", "/")
        lines = read_lines(path)
        story.append(Paragraph(esc(f"16.{file_index} {rel}"), styles["H2Manual"]))
        story.append(P(file_purpose(path), "SmallManual"))
        story.append(P(f"Tipo: {path.suffix or path.name} | Líneas: {len(lines)} | Archivo {file_index} de {total}", "TinyManual"))
        context = {}
        if path.suffix.lower() == ".java":
            classes, methods = java_structure(lines)
            if classes:
                context["class"] = classes[0]
                story.append(P("Clases/tipos detectados: " + ", ".join(classes), "TinyManual"))
            if methods:
                method_rows = [["Línea", "Constructor o método detectado"]]
                for ln, signature in methods:
                    method_rows.append([str(ln), signature])
                story.append(table(method_rows, widths=[25 * mm, 215 * mm], font_size=5.8))
                story.append(Spacer(1, 2 * mm))
        story.append(CondPageBreak(22 * mm))
        header = Table(
            [[P("Línea", "TinyManual"), P("Código original", "TinyManual"), P("Explicación", "TinyManual")]],
            colWidths=[30, 0.50 * (PAGE_SIZE[0] - 30 - 42 * mm), 0.50 * (PAGE_SIZE[0] - 30 - 42 * mm)],
        )
        header.setStyle(TableStyle([
            ("BACKGROUND", (0, 0), (-1, -1), NAVY),
            ("TEXTCOLOR", (0, 0), (-1, -1), colors.white),
            ("FONTNAME", (0, 0), (-1, -1), FONT_BOLD),
            ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
            ("LEFTPADDING", (0, 0), (-1, -1), 4),
            ("RIGHTPADDING", (0, 0), (-1, -1), 4),
            ("TOPPADDING", (0, 0), (-1, -1), 3),
            ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
        ]))
        story.append(header)
        context = context or {}
        for number, line in enumerate(lines, 1):
            explanation = explanation_for(path, line, number, context)
            story.append(AnnotatedLine(number, sanitized_line(line), explanation, alternate=number % 2 == 0))
        story.append(Spacer(1, 5 * mm))
        story.append(PageBreak())


def project_stats(files):
    text_files = [p for p in files if is_text_file(p)]
    java = [p for p in files if p.suffix.lower() == ".java" and "test" not in p.parts]
    forms = [p for p in files if p.suffix.lower() == ".form"]
    sql = [p for p in files if p.suffix.lower() == ".sql"]
    resources = [p for p in files if p.suffix.lower() in RESOURCE_EXTENSIONS]
    return {
        "text_files": len(text_files),
        "text_lines": sum(len(read_lines(p)) for p in text_files),
        "java_files": len(java),
        "java_lines": sum(len(read_lines(p)) for p in java),
        "form_files": len(forms),
        "form_lines": sum(len(read_lines(p)) for p in forms),
        "sql_files": len(sql),
        "sql_lines": sum(len(read_lines(p)) for p in sql),
        "resources": len(resources),
    }, text_files


def build_pdf():
    files = all_project_files()
    stats, text_files = project_stats(files)
    tables = parse_schema(ROOT / "sql" / "base_datos_completa.sql")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    doc = ManualDoc(
        str(OUTPUT),
        pagesize=PAGE_SIZE,
        rightMargin=15 * mm,
        leftMargin=15 * mm,
        topMargin=20 * mm,
        bottomMargin=14 * mm,
        title="Manual Completo del Sistema POS Waldonald's",
        author="Equipo Waldonald's",
        subject="Código Java, base de datos MySQL, arquitectura y referencia línea por línea",
        creator="Generador documental del proyecto Waldonalds",
    )
    story = []
    add_cover(story, stats)
    add_scope(story, stats)
    add_system_overview(story)
    add_architecture(story)
    add_fundamentals(story)
    add_runtime(story)
    add_flows(story)
    add_menu_model(story)
    add_database(story, tables)
    add_database_rules(story)
    add_practical_cases(story)
    add_security_and_debug(story)
    add_tests(story)
    add_presentation_guide(story)
    add_glossary(story)
    add_file_catalog(story, files)
    add_source_appendix(story, text_files)
    doc.build(story, onFirstPage=page_decoration, onLaterPages=page_decoration)
    print(f"PDF={OUTPUT}")
    print(f"TEXT_FILES={stats['text_files']}")
    print(f"TEXT_LINES={stats['text_lines']}")
    print(f"TABLES={len(tables)}")


if __name__ == "__main__":
    build_pdf()
