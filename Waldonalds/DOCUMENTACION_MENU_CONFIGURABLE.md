# Menú configurable de Waldonald's

## Puesta en marcha

Para una instalación nueva, ejecuta `sql/base_datos_completa.sql`. El programa
usa directamente las tablas `presentacion_menu`, `grupo_presentacion`,
`opcion_grupo`, `opcion_componente` y `producto_ingrediente`; no vuelve a usar
los campos antiguos `es_combo` ni `tamano_bebida`.

Si ya conservas productos creados antes del cambio, ejecuta una sola vez
`sql/migracion_productos_existentes.sql`. Ese archivo solo les crea su
presentación individual inicial; no borra ventas, productos ni inventario.

Al crear un producto desde **Gestión del menú**, el sistema crea automáticamente
su presentación **Individual** y un grupo interno que contiene ese producto.
Después selecciona el producto en la tabla y pulsa **Opciones y receta**.

## Tipos de inventario

- `DIRECTO`: descuenta unidades de `producto.stock_actual` (bebida embotellada,
  juguete, postre ya preparado).
- `RECETA`: descuenta los ingredientes de `producto_ingrediente`. Es el tipo
  normal para hamburguesas y otros productos preparados.
- `NINGUNO`: no descuenta inventario por sí mismo. Se usa para un producto que
  funciona únicamente como nombre/contenedor de una Cajita o Caja Grande.

Solo los productos `RECETA` muestran opciones **Sin** y **Extra**. En la pestaña
**Ingredientes y personalización** se define la cantidad normal, si se puede
quitar, la cantidad de cada extra, su precio y el máximo permitido.

## Big Mac individual o McMenú

1. Crea el producto Big Mac como `RECETA` y configura sus ingredientes.
2. Conserva la presentación **Individual** creada automáticamente.
3. Agrega una presentación **McMenú**, tipo `MENU`, con su precio base.
4. Dentro de McMenú crea un grupo oculto que incluya el Big Mac, o agrega el
   producto principal como componente fijo.
5. Crea los grupos visibles **Elige el tamaño**, **Elige el complemento** y
   **Elige la bebida**, todos con mínimo 1 y máximo 1.
6. Cada opción de complemento o bebida apunta al producto real correspondiente.
   El precio mayor de una opción se guarda en `incremento_precio`.

El cajero verá Individual y McMenú como pestañas. En Individual podrá quitar o
agregar ingredientes; en McMenú además tendrá que completar los grupos.

## Cajita infantil

1. Crea un producto contenedor con tipo de inventario `NINGUNO`.
2. Crea una presentación de tipo `INFANTIL`.
3. Agrega grupos con mínimo 1 y máximo 1: comida principal, complemento, bebida
   y juguete.
4. Para un adicional opcional que deba responderse explícitamente, usa mínimo 1
   y máximo 1 y agrega una opción **No gracias** sin productos internos.

## Combo predefinido con cuatro hamburguesas

1. Crea el producto contenedor de tipo `NINGUNO` y una presentación `COMBO`.
2. Crea **Elige tus hamburguesas** con mínimo 4, máximo 4 y
   `permite_repetir = true`.
3. Cada opción contiene una hamburguesa real. El cajero verá cuatro selectores.
4. Para papas o bebida fijas, crea grupos ocultos con una opción predeterminada
   y agrega esos productos como componentes con la cantidad incluida.

Aunque se elija la misma hamburguesa varias veces, el programa crea una unidad
interna independiente para cada una. Por eso sus ingredientes pueden cambiarse
por separado y el ticket de cocina imprime cada preparación individualmente.

## Reglas importantes

- `minimo` y `maximo` se vuelven a validar al cobrar; la interfaz no es la única
  protección.
- Los precios y el inventario se vuelven a consultar dentro de la transacción de
  venta para evitar cobros viejos o sobreventa entre dos cajas.
- `disponibilidad_menu` conserva `TODO_DIA`, `DESAYUNO` y `ALMUERZO`. El horario
  existente del proyecto no fue modificado.
- El comprobante conserva el detalle de la configuración y sus opciones y
  modificaciones resaltadas.
