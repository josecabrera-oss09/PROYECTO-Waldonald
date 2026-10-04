# Módulo de inventario

## Objetivo

El módulo reúne en una sola sección administrativa el catálogo de
ingredientes, las existencias controlables y su historial. En el menú lateral
solo aparece **Inventario**; dentro de ese módulo existen tres vistas:

| Vista | Responsabilidad |
|---|---|
| **Existencias** | Consultar stock y registrar entradas, salidas o ajustes. |
| **Ingredientes** | Crear y editar nombre, unidad, mínimo y estado del ingrediente. |
| **Movimientos** | Consultar la trazabilidad de entradas, salidas, ajustes y ventas. |

La vista Existencias muestra únicamente los artículos cuyo stock se puede
modificar de forma directa:

| Artículo mostrado | Tabla de origen | Motivo |
|---|---|---|
| Ingrediente | `ingrediente` | Es la existencia consumida por los productos con receta. |
| Producto directo | `producto` con `tipo_stock = 'DIRECTO'` | Se vende y descuenta como una unidad terminada. |

Los productos `RECETA` no aparecen como una existencia adicional porque su
stock real está en los ingredientes de `producto_ingrediente`. Los productos
`NINGUNO` tampoco aparecen porque fueron definidos sin control de stock.

No fue necesario agregar ni alterar tablas. Se reutilizan `ingrediente`,
`producto`, `categoria` y `movimiento_inventario` de la base actual.

## Archivos y organización

- `src/GUI_ADMINISTRADOR/InventarioPanel.form`: contenedor visual de las tres
  pestañas. Los botones Existencias, Ingredientes y Movimientos se pueden
  mover o redimensionar desde **Design**.
- `src/GUI_ADMINISTRADOR/InventarioPanel.java`: cambia la tarjeta visible con
  `CardLayout`; no duplica consultas ni reglas de inventario.

- `src/GUI_ADMINISTRADOR/GestionInventarioPanel.form`: diseño visual principal
  editable desde la pestaña **Design** de NetBeans. Contiene títulos, botones,
  filtros, tabla y paginación con sus posiciones y tamaños.
- `src/GUI_ADMINISTRADOR/GestionInventarioPanel.java`: lógica de la vista
  Existencias, filtros, paginación y exportación.
- `src/GUI_ADMINISTRADOR/CatalogoIngredientesPanel.form`: diseño editable del
  catálogo de ingredientes, sin imágenes en sus filas.
- `src/GUI_ADMINISTRADOR/CatalogoIngredientesPanel.java`: carga, filtra,
  pagina, crea, edita y desactiva ingredientes.
- `src/GUI_ADMINISTRADOR/MovimientosInventarioPanel.form`: diseño editable del
  historial integrado.
- `src/GUI_ADMINISTRADOR/MovimientosInventarioPanel.java`: carga los últimos
  movimientos y filtra por texto o tipo.
- `src/GUI_ADMINISTRADOR/IngredienteFormPanel.form`: diseño de la mini ventana
  para agregar o editar un ingrediente.
- `src/GUI_ADMINISTRADOR/IngredienteFormDialog.java`: envoltorio modal liviano
  del formulario anterior.
- `src/GUI_ADMINISTRADOR/MovimientoInventarioFormPanel.form`: diseño de la mini
  ventana para entradas, salidas y ajustes.
- `src/GUI_ADMINISTRADOR/MovimientoInventarioFormDialog.java`: envoltorio modal
  liviano del formulario de movimiento.
- `src/DAO/InventarioDAO.java`: consultas y transacciones de inventario.
- `src/Modelos/ArticuloInventario.java`: representa un ingrediente o producto
  directo con una estructura común.
- `src/Modelos/PaginaInventario.java`: resultado paginado.
- `src/Modelos/MovimientoInventario.java`: fila del historial.
- `src/GUI_ADMINISTRADOR/MenuAdmin.form`: contiene únicamente el botón
  Inventario en el menú lateral. La opción separada Ingredientes fue retirada.
- `src/GUI_ADMINISTRADOR/MenuAdmin.java`: abre el panel mediante el
  `CardLayout` existente.
- `src/Componentes/BotonMenuLateral.java`: dibuja el icono vectorial de la caja.
- `test/InventarioPanelTest.java`: verifica navegación, carga y que la columna
  de nombre sea texto sin imágenes.

## Interfaz

Se reutilizaron los componentes visuales del proyecto:

- `BotonDerretido` para la acción principal Registrar entrada.
- `BotonRedondeado` para salida, historial, exportación, paginación y acciones.
- `CampoBusquedaAdmin` para la búsqueda por nombre, categoría o unidad.
- `BotonDesplegable` para los filtros de tipo y estado.
- `PanelFlotante` para filtros, tablas y tarjetas de los formularios.
- `TablaAdministrativa` para conservar cabecera, colores, filas y selección.
- `TemaAdmin` e `IconosUsuarios` para tipografía e iconos vectoriales.

La tabla no carga rutas ni imágenes. La columna **Producto / ingrediente**
recibe directamente un `String` con el nombre.

La distribución de cada pantalla y mini ventana está guardada en su archivo
`.form`. El código Java se concentra en eventos, validación y acceso a datos.
Por eso, para cambiar posiciones, tamaños o textos se abre el `.form` en
NetBeans y se usa la pestaña **Design**.

## Patrón de las mini ventanas

Se siguió el mismo patrón de `UsuarioForm`:

1. Un `...FormPanel.form` contiene todo lo visible: tarjeta, títulos, campos,
   error y botones.
2. Su clase `...FormPanel.java` valida y guarda los datos.
3. Un `...FormDialog.java` crea una ventana modal sin bordes, coloca el panel,
   escucha los eventos Guardado/Cancelar y se cierra.

Así el diseño no queda construido con decenas de componentes dentro del
controlador y puede editarse visualmente. Los diálogos anteriores construidos
directamente en código ya no forman parte del flujo nuevo de Inventario.

Las columnas son: ID, nombre, tipo, categoría, unidad, stock actual, stock
mínimo, último movimiento, estado y acción.

## Estados calculados

El estado visible no necesita otra columna en la base:

- **Inactivo:** `estado = false`.
- **Agotado:** artículo activo con `stock_actual <= 0`.
- **Stock bajo:** artículo activo, con mínimo mayor que cero y stock actual
  menor o igual al mínimo.
- **Disponible:** cualquier otro artículo activo con existencias.

## Entradas, salidas y ajustes

1. El administrador selecciona una fila.
2. Puede pulsar Registrar entrada, Registrar salida, Ajustar stock o `± Stock`.
3. El diálogo solicita cantidad y un motivo opcional.
4. Para **Entrada**, la cantidad se suma.
5. Para **Salida**, la cantidad se resta y nunca puede dejar stock negativo.
6. Para **Ajuste**, la cantidad escrita es el stock final que debe quedar.

`InventarioDAO.registrarMovimiento` bloquea la fila con `FOR UPDATE`, calcula
el nuevo stock, actualiza el artículo e inserta su fila en
`movimiento_inventario`. Ambas operaciones se confirman juntas; si una falla,
se hace `rollback` y no queda un stock sin historial.

Los productos directos solo aceptan unidades enteras. Los ingredientes aceptan
hasta dos decimales, de acuerdo con `DECIMAL(12,2)`.

## Historial y ventas

La pestaña Movimientos muestra hasta 300 movimientos recientes. También
aparecen las salidas creadas automáticamente durante el cobro por `PagoDAO`,
porque utilizan la misma tabla `movimiento_inventario`.

En la vista, las entradas llevan signo positivo y las salidas se presentan con
signo negativo. En un ajuste se conserva la diferencia registrada entre el
stock anterior y el nuevo.

## Filtros, paginación y exportación

- La búsqueda espera 350 ms antes de consultar para evitar una consulta por
  cada tecla.
- Los filtros permiten separar ingredientes, productos directos y estados.
- La consulta trae 10 filas por página.
- Exportar genera un CSV con todos los registros que cumplen los filtros
  actuales, no solamente la página visible.

Las consultas se ejecutan con `SwingWorker`, así la interfaz no se congela
mientras MySQL responde.

## Uso

1. Abre **Administrador > Inventario**.
2. En **Existencias**, localiza y selecciona el artículo.
3. Registra entrada, salida o ajuste con los botones superiores o `± Stock`.
4. En **Ingredientes**, crea o edita los datos descriptivos y el stock mínimo.
5. En **Movimientos**, revisa la trazabilidad y filtra el historial.
6. Usa Exportar desde Existencias para guardar el resultado actual en CSV.

## Verificación realizada

- Compilación completa con JDK 21.
- Consulta real de solo lectura: 89 artículos cargados en la base probada, 54
  ingredientes y 35 productos directos.
- Filtros de tipo y estado ejecutados correctamente.
- Prueba visual del menú, panel y navegación interna.
- Validación XML de los siete formularios `.form` relacionados.
- Validación de que el nombre de la tabla es texto y no una celda con imagen.
