# Gestión de usuarios de Waldonald's

Esta guía explica la implementación de la pantalla de usuarios, los archivos
creados, los componentes reutilizados y el flujo de cada operación. El módulo
está escrito para Java 21, Swing, NetBeans y MySQL.

## 1. Resultado funcional

La pantalla permite:

- Mostrar total de usuarios, administradores activos, cajeros activos e
  inactivos.
- Buscar por nombre, apellido, usuario o correo.
- Combinar la búsqueda con filtros de rol y estado.
- Limpiar todos los filtros.
- Mostrar exactamente 10 usuarios por página.
- Navegar únicamente con anterior, números de página y siguiente.
- Agregar usuarios validando datos y duplicados.
- Editar datos sin volver a escribir la contraseña.
- Cambiar la contraseña sólo cuando se solicita.
- Desactivar usuarios conservando su historial de pedidos.
- Exportar a CSV todos los resultados de los filtros actuales.

No existe un selector de “usuarios por página”. El tamaño siempre es 10.

## 2. Requisito del esquema

El código asume que `usuario` ya tiene una columna `correo`. Si la base todavía
no la tiene, debe agregarse antes de ejecutar la pantalla:

```sql
ALTER TABLE usuario
ADD COLUMN correo VARCHAR(120) NULL AFTER usuario;

UPDATE usuario
SET correo = CONCAT(usuario, '@waldonalds.com')
WHERE correo IS NULL;

ALTER TABLE usuario
MODIFY correo VARCHAR(120) NOT NULL;

ALTER TABLE usuario
ADD CONSTRAINT uq_usuario_correo UNIQUE (correo);
```

La implementación no ejecuta automáticamente este `ALTER TABLE`, porque los
cambios de estructura deben administrarse de forma explícita.

## 3. Archivos de la solución

### Archivos nuevos

| Archivo | Responsabilidad |
| --- | --- |
| `src/Modelos/Usuario.java` | Representa un usuario sin exponer su contraseña. |
| `src/Modelos/PaginaUsuarios.java` | Agrupa las filas de una página y el total filtrado. |
| `src/Modelos/ResumenUsuarios.java` | Contiene los cuatro conteos superiores. |
| `src/CRUD/UsuarioCRUD.java` | Ejecuta consultas, altas, ediciones y desactivaciones. |
| `src/Utilidades/SeguridadContrasena.java` | Genera el SHA-256 usado por el login. |
| `src/Utilidades/SesionUsuario.java` | Conserva el usuario autenticado. |
| `src/Utilidades/IconosUsuarios.java` | Dibuja iconos vectoriales sin archivos externos. |
| `src/Componentes/CampoBusquedaAdmin.java` | Buscador blanco, neutro y editable como JavaBean. |
| `src/GUI_ADMINISTRADOR/UsuarioFormPanel.java` | Lógica del formulario de alta/edición. |
| `src/GUI_ADMINISTRADOR/UsuarioFormPanel.form` | Diseño visual editable del formulario. |
| `src/GUI_ADMINISTRADOR/UsuarioFormDialog.java` | Contenedor modal mínimo del panel visual. |

### Archivos modificados

| Archivo | Cambio |
| --- | --- |
| `src/GUI_ADMINISTRADOR/UsuariosPanel.java` | Tabla, filtros, tarjetas, acciones, páginas y exportación. |
| `src/GUI_ADMINISTRADOR/UsuariosPanel.form` | Contiene buscador, filtros, tabla, exportación y paginación en Design. |
| `src/Login/Login.java` | Usa el hash compartido y registra la sesión autenticada. |

Los componentes visuales permanentes están declarados en los archivos `.form`.
Después de `initComponents()` el código solamente aplica fuentes, conecta
eventos, instala renderers y carga datos. Por eso se pueden mover controles
desde la pestaña **Design** sin reconstruir la pantalla en Java.

## 4. Componentes existentes reutilizados

| Componente | Uso en el módulo |
| --- | --- |
| `PanelFlotante` | Contenedor de filtros, tabla y fondo del modal. |
| `PanelCircular` | Fondo circular del icono principal del formulario. |
| `BotonDerretido` | Botón principal “Agregar usuario”. |
| `BotonRedondeado` | Exportar, limpiar, paginar, guardar, cancelar y acciones por fila. |
| `BotonDesplegable` | Filtros y selectores de rol/estado. |
| `CampoBusquedaAdmin` | Buscador blanco con lupa gris; no utiliza círculo ni fondo amarillo. |
| `LabelEscalable` | Iconos de las tarjetas creadas en el `.form`. |
| `TemaAdmin` | DM Sans, colores, medidas y calidad gráfica. |

`JTable`, `JScrollPane`, `JPasswordField`, `JFileChooser` y `SwingWorker` son
componentes estándar de Swing usados donde el proyecto no tenía un equivalente.

## 5. Modelo de usuario

`Usuario` contiene solamente información que puede mostrarse en la interfaz.
No contiene `password_hash`.

```java
public class Usuario {
    private final int idUsuario;
    private final String nombre;
    private final String apellido;
    private final String nombreUsuario;
    private final String correo;
    private final String rol;
    private final boolean activo;
    private final LocalDateTime fechaCreacion;

    // La tabla usa este método para formar la columna Nombre.
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
```

Esto impide que un hash termine accidentalmente en una celda, formulario o
archivo exportado.

## 6. Capa CRUD

`UsuarioCRUD` recibe filtros y devuelve objetos `Usuario`. Todas las variables
se colocan con `PreparedStatement`.

### Consulta paginada

```java
public PaginaUsuarios listarPagina(
        String busqueda,
        String rol,
        Boolean estado,
        int pagina,
        int tamanoPagina) throws SQLException {

    // La página nunca puede ser menor que 1.
    int paginaSegura = Math.max(1, pagina);
    int desplazamiento = (paginaSegura - 1) * tamanoPagina;

    // construirFiltro agrega sólo las condiciones seleccionadas.
    FiltroSql filtro = construirFiltro(busqueda, rol, estado);

    try (Connection conexion = abrirConexion()) {
        int total = contarFiltrados(conexion, filtro);
        List<Usuario> usuarios = consultarUsuarios(
                conexion, filtro, tamanoPagina, desplazamiento, true);
        return new PaginaUsuarios(usuarios, total);
    }
}
```

La consulta resultante termina con:

```sql
ORDER BY id_usuario ASC
LIMIT ? OFFSET ?
```

### Filtros combinados

La búsqueda utiliza:

```sql
LOWER(CONCAT_WS(' ', nombre, apellido, usuario, correo)) LIKE LOWER(?)
```

Después puede agregar `rol = ?` y `estado = ?`. Las condiciones se unen con
`AND`, por lo que los tres controles funcionan al mismo tiempo.

### Resumen

Las cuatro tarjetas salen de una consulta:

```sql
SELECT COUNT(*) AS total,
       COALESCE(SUM(rol = 'ADMINISTRADOR' AND estado = TRUE), 0)
           AS administradores,
       COALESCE(SUM(rol = 'CAJERO' AND estado = TRUE), 0)
           AS cajeros,
       COALESCE(SUM(estado = FALSE), 0) AS inactivos
FROM usuario;
```

Los conteos son globales; no cambian al filtrar la tabla.

## 7. Paginación fija

El tamaño se declara una sola vez en `UsuariosPanel`:

```java
private static final int USUARIOS_POR_PAGINA = 10;
```

La cantidad de páginas se calcula así:

```java
totalPaginas = Math.max(1, (int) Math.ceil(
        totalRegistros / (double) USUARIOS_POR_PAGINA));
```

Al buscar o cambiar un filtro, `paginaActual` vuelve a 1. Anterior se desactiva
en la primera página y siguiente en la última. Se muestran como máximo tres
botones numéricos alrededor de la página actual.

El texto inferior es informativo, por ejemplo:

```text
Mostrando 11–20 de 28 usuarios
```

No modifica el tamaño de página.

## 8. Búsqueda y carga asíncrona

El documento del campo de búsqueda reinicia un `Timer` de 300 milisegundos:

```java
temporizadorBusqueda = new Timer(300, evento -> {
    paginaActual = 1;
    cargarPagina();
});
temporizadorBusqueda.setRepeats(false);
```

La consulta corre dentro de `SwingWorker`. De esa forma MySQL no bloquea el
hilo gráfico y la ventana continúa respondiendo.

`secuenciaCarga` identifica cada petición. Si una consulta anterior termina
después de que el usuario cambió nuevamente el filtro, su respuesta se ignora.

## 9. Tabla y acciones por fila

`ModeloTablaUsuarios` extiende `AbstractTableModel`. Las columnas de estado y
acciones devuelven el objeto `Usuario` completo, permitiendo trabajar con el ID
correcto sin intentar leerlo desde el texto pintado.

El estado usa `RenderEstado` para dibujar una insignia verde o gris.

La columna de acciones requiere dos partes:

- `RenderAcciones`: dibuja los botones.
- `EditorAcciones`: recibe los clics reales.

Un `TableCellRenderer` por sí solo no es interactivo. El editor llama:

```java
private void editar(ActionEvent evento) {
    Usuario seleccionado = usuario;
    fireEditingStopped();       // Finaliza la edición de la celda.
    abrirFormulario(seleccionado);
}

private void eliminar(ActionEvent evento) {
    Usuario seleccionado = usuario;
    fireEditingStopped();
    desactivarUsuario(seleccionado);
}
```

## 10. Formulario reutilizable

`UsuarioFormDialog.mostrar(...)` recibe `null` para agregar o un `Usuario` para
editar:

```java
// Alta
UsuarioFormDialog.mostrar(ventana, crud, null);

// Edición
UsuarioFormDialog.mostrar(ventana, crud, usuarioSeleccionado);
```

`UsuarioFormDialog` no construye controles. Crea un
`UsuarioFormPanel(crud, usuario)` y lo muestra como contenido modal. El diseño
completo se edita abriendo `UsuarioFormPanel.form` en la pestaña **Design**.
El mismo panel cambia título, subtítulo, valores y botón principal según reciba
`null` o un usuario existente.

### Cómo modificar el diseño en NetBeans

Para la pantalla principal:

1. Abrir `UsuariosPanel.java`.
2. Seleccionar la pestaña **Design**.
3. Mover o redimensionar `panelFiltros`, `campoBusqueda`, `filtroRol`,
   `filtroEstado`, `botonLimpiarFiltros`, `panelTabla`, `scrollUsuarios`,
   `panelPaginacion` y `botonExportar`.
4. No colocar consultas SQL dentro de `initComponents()`.

Para agregar y editar:

1. Abrir `UsuarioFormPanel.java`.
2. Seleccionar **Design**.
3. Editar campos, etiquetas, botones y el panel de contraseñas.
4. Ambos modos comparten el mismo diseño; `cargarModo()` decide qué elementos
   se muestran durante la ejecución.

El buscador es `CampoBusquedaAdmin`, un JavaBean con propiedades `placeholder`,
`colorBorde`, `colorIcono`, `colorPlaceholder` y `radio`. Su lupa se pinta en
gris oscuro directamente dentro del campo, sin la pieza amarilla del login.

### Validaciones

- Nombre, apellido, usuario, correo y rol obligatorios.
- Límites iguales a los `VARCHAR` del esquema.
- Usuario de al menos cuatro caracteres, sin espacios.
- Formato básico de correo.
- Usuario y correo no repetidos.
- Contraseña de al menos ocho caracteres.
- Confirmación igual a la contraseña.
- Siempre debe quedar un administrador activo.
- El usuario conectado no puede desactivarse a sí mismo.

### Contraseña al editar

La opción “Cambiar contraseña” oculta los campos inicialmente. Cuando no está
marcada, el CRUD usa un `UPDATE` que no incluye `password_hash`.

```java
String hash = requiereContrasena
        ? SeguridadContrasena.sha256(contrasena)
        : null;

crud.actualizar(datos, hash);
```

El formulario nunca recibe ni muestra el hash anterior.

## 11. Contraseñas y login

El login original ya usaba SHA-256. El cálculo se movió a
`SeguridadContrasena.sha256(...)`, utilizado tanto por `Login` como por el alta
y la edición:

```java
public static String sha256(String contrasena) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(
            contrasena.getBytes(StandardCharsets.UTF_8));
    // Después convierte cada byte a hexadecimal.
}
```

Esto conserva compatibilidad con las contraseñas existentes. Para producción
conviene migrar posteriormente a BCrypt o Argon2, porque SHA-256 sin sal no es
un algoritmo específico para almacenar contraseñas.

## 12. Desactivación en lugar de DELETE

El icono rojo ejecuta:

```sql
UPDATE usuario
SET estado = FALSE
WHERE id_usuario = ?;
```

No se usa `DELETE FROM usuario`. La tabla `pedido` tiene una llave foránea con
`ON DELETE RESTRICT`; borrar usuarios rompería la trazabilidad de ventas.

Antes de desactivar se confirma la acción y se comprueba que:

- No sea el usuario autenticado.
- No sea el último administrador activo.
- El usuario no esté ya inactivo.

Los usuarios se reactivan abriendo Editar y seleccionando `Activo`.

## 13. Exportación CSV

El botón Exportar conserva los filtros actuales, pero consulta todos los
resultados sin `LIMIT`. El usuario elige la ubicación mediante `JFileChooser`.

El archivo utiliza UTF-8 con BOM para que Excel muestre correctamente tildes y
la `ñ`. Cada valor se encierra entre comillas y las comillas internas se
duplican.

## 14. Sesión autenticada

Después de validar el login se ejecuta:

```java
SesionUsuario.iniciar(idUsuario, nombre, apellido, rol);
```

`UsuariosPanel` y `UsuarioFormDialog` consultan ese ID para impedir que el
administrador conectado desactive su propia cuenta.

## 15. Iconos

`IconosUsuarios` implementa `javax.swing.Icon` y dibuja con `Graphics2D`:

- búsqueda;
- editar;
- eliminar/desactivar;
- filtros;
- exportar;
- agregar;
- flechas;
- usuarios, administradores, cajeros e inactivos;
- mostrar contraseña.

Así los iconos mantienen colores nítidos y no requieren instalar una librería
ni agregar múltiples PNG.

## 16. Cómo probar

1. Confirmar que MySQL esté iniciado.
2. Confirmar que `usuario.correo` exista.
3. Abrir el proyecto `Waldonalds` en NetBeans.
4. Ejecutar Clean and Build.
5. Iniciar sesión como administrador.
6. Abrir Gestión de Usuarios.
7. Probar búsqueda, los dos filtros y Limpiar filtros.
8. Crear al menos 11 usuarios para comprobar el cambio de página.
9. Editar un usuario sin cambiar contraseña y comprobar que todavía inicia
   sesión.
10. Cambiar su contraseña y probar nuevamente el login.
11. Desactivar un usuario y comprobar que no puede iniciar sesión.
12. Editarlo, reactivarlo y comprobar el acceso.
13. Exportar con filtros y revisar el CSV.

También puede compilarse desde una terminal que tenga Ant:

```powershell
cd Waldonalds
ant clean jar
```

En el entorno donde se desarrolló, Ant no estaba instalado; se verificó con
`javac 21.0.4` usando `AbsoluteLayout.jar` y el conector MySQL del directorio
`lib`.

## 17. Verificaciones realizadas

- Todos los archivos Java compilan correctamente con Java 21.
- `UsuariosPanel.form` y `UsuarioFormPanel.form` son XML válidos.
- La construcción visual principal quedó dentro de los dos `.form`.
- El conector logra abrir la base `waldonalds`.
- La consulta de resumen se ejecuta correctamente.
- La base encontrada durante la verificación todavía no tenía físicamente la
  columna `correo`; por eso la consulta de filas sólo funcionará después de
  aplicar el requisito de la sección 2, aunque el código se implementó según
  la instrucción de asumir que el campo existe.
