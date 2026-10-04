# Panel de reportes de Waldonald's

## Abrir y modificar el diseño

Abre `src/GUI_ADMINISTRADOR/ReportesPanel.java` en NetBeans y selecciona **Design**. El diseño se guarda en `ReportesPanel.form`, junto al Java. Ambos archivos se actualizaron. Si NetBeans tenía abierta una versión anterior, recarga los cambios del disco antes de guardar; la captura compartida y el archivo inicialmente guardado tenían textos diferentes.

Los componentes están en el mismo panel. Puedes moverlos, redimensionarlos y cambiar sus propiedades en el diseñador. Se mantuvieron los componentes `PanelFlotante`, `PanelCircular` y `LabelEscalable` que ya usabas. Las tablas utilizan `TablaAdministrativa`, que ya existía en el proyecto.

| Elemento | Nombre en el Inspector | Propiedades útiles |
|---|---|---|
| Tarjeta de ventas | `panelFlotante1` | `colorFondo`, `colorBorde`, `radio`, `sombra` |
| Tarjeta de pedidos | `panelFlotante2` | Las mismas propiedades |
| Tarjeta de ticket promedio | `panelFlotante4` | Las mismas propiedades |
| Tarjeta de stock bajo | `panelFlotante3` | Las mismas propiedades |
| Círculos de iconos | `panelCircular2`, `3`, `5`, `4` | `colorFondo` |
| Imágenes de tarjetas | `labelEscalable2`, `3`, `5`, `4` | `icon` |
| Valores de tarjetas | `labelTitulo5`, `8`, `14`, `11` | `font`, `foreground`; el texto se actualiza con datos |
| Selector de fecha | `fechaReporte` | Posición, tamaño y fuente |
| Controles | `botonHoy`, `botonActualizar`, `botonExportar` | Texto, fondo, fuente y tamaño |
| Tablas | `tablaPedidos`, `tablaProductos`, `tablaAlertas` | `colorCabecera`, `fuenteCabecera`, `altoCabecera`, `colorFilas`, `colorFilaAlterna`, `filasAlternadas`, `paddingHorizontal` |
| Área de cada tabla | `scrollPedidos`, `scrollProductos`, `scrollAlertas` | Posición y tamaño del área visible |
| Encabezados | `tituloPedidos`, `tituloProductos`, `tituloAlertas` | Fuente, color y posición |
| Estado de la consulta | `estadoCarga` | Fuente, color y posición |

La interfaz utiliza directamente las medidas guardadas en los formularios de
NetBeans. El panel de reportes ocupa 1580 × 980 dentro del diseño administrativo
de 1920 × 1080. No existe una transformación automática de posiciones, tamaños
o fuentes.

## Qué muestra y cómo se calcula

- **Ventas del día:** suma de `pedido.total` solo cuando `estado='PAGADO'`.
- **Pedidos del día:** todos los pedidos de la fecha, incluidos pendientes y cancelados. La tarjeta lo indica.
- **Ticket promedio:** promedio del total de los pedidos pagados. Si no existen, muestra Q0.00.
- **Productos con stock bajo:** productos activos cuyo `stock_actual <= stock_minimo`. Es inventario actual, independiente de la fecha seleccionada.
- **Pedidos:** todos los pedidos del día, ordenados inicialmente del más reciente al más antiguo. Incluye cajero, hora, servicio, estado y total. Puedes ordenar pulsando los encabezados y desplazarte para ver todos.
- **Productos más vendidos:** los 10 primeros por unidades de pedidos pagados. Cuenta líneas principales (`id_detalle_padre IS NULL`), de modo que los componentes no vuelvan a contabilizar una venta de combo. Su estado de disponibilidad es el actual.
- **Alertas:** productos e ingredientes activos en o por debajo del mínimo, con cantidad, umbral y unidad. Los agotados aparecen primero.

Las ventas son ingresos cobrados, no ganancias: el esquema no contiene costos para calcular utilidad. Los únicos estados mostrados son los que tu base guarda: pendiente, pagado y cancelado. Las alertas se limitan a existencias porque el esquema proporcionado no registra vencimientos ni mantenimiento programado.

La fecha incluye desde las 00:00:00 hasta antes de las 00:00:00 del día siguiente. Se usan parámetros SQL y el campo DATETIME del pedido. Se conserva la conexión existente del proyecto.

## Uso y organización del código

1. Entra a **Reportes**: la sección consulta automáticamente. Al regresar desde otra sección vuelve a consultar.
2. Para otra fecha, escríbela como `dd/MM/aaaa` y pulsa **Actualizar**. **Hoy** vuelve a la fecha actual.
3. **Exportar CSV** guarda los indicadores y las tres tablas del reporte cargado. Incluye la fecha y aclara que las existencias son actuales. Los importes se exportan con decimales, texto UTF-8 y protección para nombres que podrían interpretarse como fórmulas.

`ReportesPanel.java` conserva la presentación generada y contiene eventos/carga fuera de `initComponents()`. `DAO/ReporteDAO.java` contiene las consultas SQL. `Utilidades/ReporteCsv.java` contiene la exportación. No es necesario editar esas clases para mover componentes o cambiar los colores del formulario.

La carga utiliza `SwingWorker`: MySQL se consulta fuera del hilo que dibuja Swing. Los resultados se aplican juntos. Mientras se consulta, se bloquean las acciones duplicadas. Ante un error se muestran guiones y un mensaje para reintentar; no se presentan ceros como si la consulta hubiera tenido éxito. Las consultas usan una transacción de lectura para mantener coherentes los indicadores y las tablas.

## Animación del menú

En `MenuAdmin.java` se mantiene un temporizador Swing de aproximadamente 60 actualizaciones por segundo. La transición usa una curva suave de aceleración y frenado, con duración máxima de 300 ms. Si pulsas otra vez durante la transición, se invierte desde la posición actual y la duración se adapta al recorrido pendiente.

En `BotonMenuLateral.java`, `aperturaMenu` controla la opacidad del texto y la
posición horizontal del icono. El texto permanece disponible para accesibilidad
y ayudas emergentes. El nombre y rol de la marca también se atenúan. Esta
animación modifica únicamente el ancho del menú lateral; no escala la interfaz.
El control admite Espacio y Enter cuando tiene el foco y el temporizador se
detiene al cerrar la ventana.

## Iconos

No se generaron ni dibujaron iconos nuevos. Las cuatro tarjetas reutilizan estos archivos de `src/Imagenes`:

- `quetzal_icono.png`: ventas.
- `bolsa_icono.png`: pedidos.
- `ticket_icono.png`: ticket promedio.
- `caja_icono.png`: existencias.

El panel actual no necesita más imágenes para funcionar. Para añadir iconos de encabezado similares a tu referencia, puedes preparar:

- `reportes_pedidos.png`: portapapeles/lista, amarillo.
- `reportes_productos.png`: hamburguesa, amarillo.
- `reportes_alertas.png`: triángulo de advertencia, rojo; también se puede evaluar el `advertencia_icono.png` que ya tienes.

PNG transparente de 64 × 64 o 96 × 96 px, con el mismo grosor de trazo y margen interno. Se mostrarían aproximadamente a 24–28 px. Los iconos que ya usaba el menú lateral se conservaron; se cambió su posición durante la animación, no su dibujo.

## Verificación realizada

- Compilación completa con Java 21 y construcción del JAR mediante Ant: correctas.
- Consultas a tu MySQL en modo de lectura: correctas. En la comprobación del 27/09/2026 no había pedidos de ese día.
- Pruebas SQL con tablas TEMPORARY exclusivas de la conexión de prueba: pagos, pendientes, cancelaciones, medianoche, combos, mínimos inclusivos, registros inactivos y día vacío. Se destruyen al cerrar la conexión; no alteran las tablas reales.
- Validación de fecha imposible, constructor sin conexión obligatoria, tablas de solo lectura y escape de CSV.
- Prueba de apertura/cierre e inversión durante la animación con las medidas
  fijas del formulario de 1920 × 1080.
- Lectura del modelo de fecha y los tres modelos de tabla mediante los editores reales de NetBeans 24.

Los programas de comprobación están en `test/ReportesTest.java`, `test/ReporteDAOTest.java`, `test/MenuAnimacionTest.java` y `test/FormReportesTest.java`. Las capturas de verificación están en `build/reportes-check/`. La prueba de formulario necesita las bibliotecas del NetBeans instalado; las otras usan las dependencias normales del proyecto.
