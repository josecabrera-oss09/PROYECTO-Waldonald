# Pagos en caja

El cajero permite agregar productos al pedido, ajustar cantidades, quitar líneas y cancelar el carrito. Al continuar se elige comer aquí o para llevar, y efectivo o tarjeta cobrada en una terminal externa. Los importes se muestran en quetzales (Q), incluidos el menú, la administración y los comprobantes. Cancelar cierra el panel y devuelve el espacio al menú; si existe un cobro pendiente, se conserva para reintentar al abrir «Ordenar».

En efectivo se validan hasta dos decimales, monto suficiente y cambio. En tarjeta se exige confirmar la aprobación externa y escribir la referencia de la terminal. Esta aplicación registra el pago externo; no envía cargos bancarios ni almacena números de tarjeta, CVV o PIN.

El cobro verifica usuario activo, productos activos, horario, precio vigente y stock. Registra pedido pagado, detalles y salidas de inventario en una única transacción InnoDB. El número de orden usa el identificador autoincremental del pedido, sin reiniciarlo diariamente. Se descuentan existencias de productos terminados; la receta actual no define cantidades de ingredientes para descontarlos.

## Instalación

1. Usar Java 21 o posterior y la conexión MySQL configurada en `src/Conexion/Conexion.java`.
2. Ejecutar `sql/pagos.sql` en la base `waldonalds`. Es una migración aditiva e idempotente para guardar claves de operación y comprobantes; no modifica pedidos anteriores.
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
