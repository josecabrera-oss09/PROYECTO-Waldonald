# Base de datos de Waldonald's

Ejecuta los archivos en este orden:

1. `01_crear_base_de_datos.sql`
2. `02_insertar_datos_iniciales.sql`

El primer archivo crea la base de datos `waldonalds` y todas sus tablas.
El segundo archivo carga usuarios, categorías, productos, ingredientes,
recetas, presentaciones, grupos, opciones y componentes.

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin` | `Admin123!` |
| Cajero | `cajero` | `Cajero123!` |

Los dos usuarios están activos. El administrador tiene turno de mañana y el
cajero tiene turno de tarde. El sistema permite iniciar sesión fuera del turno,
pero muestra la información configurada para cada usuario.

Los scripts están diseñados para ejecutarse una sola vez sobre una instalación
nueva. Si ya existen datos, primero crea otra base de pruebas o respalda la base
actual.
