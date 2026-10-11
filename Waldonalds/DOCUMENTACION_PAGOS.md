# Pagos en caja

El cajero permite agregar productos al pedido, ajustar cantidades, quitar líneas y cancelar el carrito. Al continuar se elige comer aquí o para llevar, y efectivo o tarjeta cobrada en una terminal externa. Los importes se muestran en quetzales (Q), incluidos el menú, la administración y los comprobantes. Cancelar cierra el panel y devuelve el espacio al menú; si existe un cobro pendiente, se conserva para reintentar al abrir «Ordenar».

En efectivo se validan hasta dos decimales, monto suficiente y cambio. En tarjeta se exige confirmar la aprobación externa, escribir el ID de terminal (TID) del voucher, el vencimiento en exactamente cuatro dígitos `MMYY` y un CVV de exactamente tres dígitos; el vencimiento se valida contra el mes actual. El TID identifica el aparato que procesó la transacción, no la tarjeta. Estos datos de tarjeta solo viven durante la validación: no se guardan CVV, vencimiento, números de tarjeta ni PIN.

El cobro también permite ingresar un NIT opcional. Los NITs se guardan una sola vez en `nit_cliente` y, al volver a escribir su inicio, aparecen como sugerencias para autocompletar. La tabla usa el NIT como clave primaria para impedir duplicados.

El selector de NIT incluye la opción `CF` (consumidor final) y el NIT seleccionado aparece en el comprobante. Antes de confirmar una tarjeta se muestra una alerta sobre guardar los datos; por seguridad, el sistema no almacena número de tarjeta, vencimiento ni CVV, aunque el usuario acepte continuar.

La sección `Pedidos` del cajero muestra los últimos pedidos, su estado y un contador de preparación de 5 minutos por pedido. La cancelación devuelve las existencias consumidas al inventario dentro de una transacción y marca el pedido como cancelado. Para una base existente puede ejecutarse `sql/migracion_pedidos_tiempo.sql`; el panel también intenta crear las columnas automáticamente.

El cobro verifica usuario activo, productos activos, horario, precio vigente y stock. Registra pedido pagado, detalles y salidas de inventario en una única transacción InnoDB. El número de orden usa el identificador autoincremental del pedido, sin reiniciarlo diariamente. Se descuentan existencias de productos terminados; la receta actual no define cantidades de ingredientes para descontarlos.

## Instalación

1. Usar Java 21 o posterior y la conexión MySQL configurada en `src/Conexion/Conexion.java`.
2. Ejecutar `sql/pagos.sql` en la base `waldonalds`. Es una migración aditiva e idempotente para guardar claves de operación, comprobantes y NITs; no modifica pedidos anteriores. Para una base ya existente también puede ejecutarse `sql/migracion_nit_facturacion.sql`.
3. Compilar y ejecutar el proyecto desde NetBeans. Las dependencias son AbsoluteLayout y el conector MySQL que ya incluye el proyecto.
4. Iniciar sesión con un usuario activo. Los productos deben tener stock mayor que cero y estar disponibles en el horario actual.

## Fallos y recuperación

Antes de enviar el cobro, se guarda su solicitud en `pagos_pendientes/caja-ID.properties`, relativa al directorio de ejecución. Mantener ese directorio estable y escribible, y usar una instancia por cajero. No borrar el respaldo mientras el resultado esté pendiente.

Ante un error SQL, el carrito queda bloqueado y ofrece reintentar la misma operación. Si MySQL ya confirmó la venta pero se perdió la respuesta, devuelve el comprobante original sin duplicar el pedido ni descontar más stock. Al reiniciar desde el mismo directorio, el cajero recupera la solicitud pendiente. No repetir el cargo en la terminal externa al reintentar su registro. Si la terminal aprobó pero el pedido no se puede completar, se debe resolver o revertir ese cargo en la propia terminal.

Tras un pago exitoso se muestra el comprobante; el botón «Último comprobante» permite reabrirlo durante la sesión. La copia persistente está en `pago_operacion.comprobante`. Es un comprobante de venta interno; no implementa facturación electrónica.

## Pruebas

Desde la carpeta `Waldonalds`, en PowerShell con JDK disponible:

```powershell
$fuentes = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 21 -cp 'dist/lib/AbsoluteLayout.jar;lib/mysql-connector-j-26.7.0.jar' -d build/payment-check $fuentes test/PagosTest.java
java '-Djava.awt.headless=true' -cp 'build/payment-check;dist/lib/AbsoluteLayout.jar;lib/mysql-connector-j-26.7.0.jar' PagosTest
```

Las pruebas requieren permisos para crear una base temporal. Copian únicamente el esquema necesario a una base nueva con nombre aleatorio `waldonalds_test_pagos_*`, insertan datos ficticios y eliminan exclusivamente esa base en `finally`. No insertan ventas ni cambian existencias en la base de trabajo.

Cubren decimales, pago insuficiente, pedido vacío, referencia de tarjeta, total y cambio, recuperación del respaldo, stock, precio modificado, usuario y producto inactivos, horario, rollback ante fallo de inventario, reintentos y pérdida de respuesta del commit, y concurrencia por la última existencia.
