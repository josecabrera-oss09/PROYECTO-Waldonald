# Manual de TablaAdministrativa

## 1. Qué es

`TablaAdministrativa` es un componente reutilizable basado en
`JTable`. Centraliza el diseño de las tablas administrativas y permite
que cada pantalla conserve su propio `TableModel`, consultas y acciones.

Archivo del componente:

```text
src/Componentes/TablaAdministrativa.java
```

La tabla de usuarios ya utiliza este componente en `UsuariosPanel.form`.

## 2. Agregarlo a un formulario desde NetBeans

1. Ejecutar **Clean and Build** para compilar el componente.
2. Abrir **Tools > Palette > Swing/AWT Components**.
3. Usar **Add from Project** y seleccionar `TablaAdministrativa`.
4. Colocar primero un `JScrollPane` en el formulario.
5. Arrastrar `TablaAdministrativa` dentro del `JScrollPane`.
6. Cambiar el nombre de variable, por ejemplo `tablaProductos`.

También se puede cambiar una tabla existente en el archivo `.form` para
que su clase sea `Componentes.TablaAdministrativa`, como se hizo con
`tablaUsuarios`.

## 3. Propiedades visuales

Seleccionar la tabla en Design y abrir **Properties**.

| Propiedad | Función |
|---|---|
| `font` | Fuente utilizada por las filas. |
| `rowHeight` | Altura de cada fila. |
| `fuenteCabecera` | Fuente de los títulos de columna. |
| `altoCabecera` | Altura de la cabecera. |
| `colorCabecera` | Fondo de la cabecera. |
| `colorTextoCabecera` | Texto de los títulos. |
| `colorFilas` | Fondo normal de las filas. |
| `colorFilaAlterna` | Fondo de filas pares cuando se activa la alternancia. |
| `filasAlternadas` | Activa o desactiva filas de dos colores. |
| `colorTexto` | Color del texto de las filas. |
| `colorLineas` | Color de las divisiones horizontales. |
| `colorSeleccion` | Fondo de la fila seleccionada. |
| `colorTextoSeleccion` | Texto de la fila seleccionada. |
| `paddingHorizontal` | Separación entre el texto y los bordes. |
| `columnasCentradas` | Índices separados por coma, por ejemplo `5,7`. |
| `columnasDerecha` | Columnas alineadas a la derecha, por ejemplo `2,4`. |

Los índices comienzan en cero. En una tabla con ocho columnas:

```text
0  1       2        3    4       5       6               7
ID Nombre  Usuario  Rol  Correo  Estado  Fecha creación  Acciones
```

Por eso la tabla de usuarios tiene:

```text
columnasCentradas = 5,7
```

Todas las demás columnas quedan alineadas a la izquierda.

## 4. Modelo básico

La tabla reutiliza el modelo que necesite cada pantalla:

```java
private final class ModeloProductos extends AbstractTableModel {

    private final String[] columnas = {
        "Producto", "Categoría", "Precio", "Estado"
    };

    private List<Producto> productos = new ArrayList<>();

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Producto producto = productos.get(fila);

        return switch (columna) {
            case 0 -> producto.getNombre();
            case 1 -> producto.getCategoria();
            case 2 -> producto.getPrecio();
            case 3 -> producto.getEstado();
            default -> "";
        };
    }
}
```

Se conecta normalmente:

```java
ModeloProductos modelo = new ModeloProductos();
tablaProductos.setModel(modelo);
tablaProductos.aplicarEstilo();
```

Se llama `aplicarEstilo()` después de cambiar el modelo para reaplicar la
cabecera y las propiedades del componente.

## 5. Mostrar imagen, nombre y descripción

### Base de datos

Conviene guardar una ruta relativa:

```sql
ALTER TABLE producto
ADD COLUMN ruta_imagen VARCHAR(255);
```

Ejemplos:

```text
productos/big-wald.png
productos/papas.png
productos/coca-cola.png
```

No se recomienda guardar una ruta absoluta como
`C:\Users\Humberto\Desktop\...` porque dejaría de funcionar en otra
computadora.

Una estructura apropiada es:

```text
Waldonalds/
├── datos/
│   └── imagenes/
│       └── productos/
│           ├── big-wald.png
│           └── papas.png
└── Waldonalds.jar
```

### Valor devuelto por el modelo

La primera columna debe devolver una `CeldaImagenTexto`:

```java
case 0 -> new TablaAdministrativa.CeldaImagenTexto(
        producto.getNombre(),
        producto.getDescripcion(),
        producto.getRutaImagen()
);
```

También debe declarar la clase de esa columna:

```java
@Override
public Class<?> getColumnClass(int columna) {
    if (columna == 0) {
        return TablaAdministrativa.CeldaImagenTexto.class;
    }
    return Object.class;
}
```

Después de colocar el modelo, se configura la columna:

```java
Path carpetaImagenes = Paths.get("datos", "imagenes");

tablaProductos.configurarColumnaImagenTexto(
        0,                 // Columna Producto
        carpetaImagenes,   // Carpeta base permitida
        48,                // Ancho de imagen
        48                 // Alto de imagen
);
```

El componente:

- Une la carpeta base con la ruta obtenida de MySQL.
- Comprueba que el archivo exista.
- Impide salir de la carpeta permitida mediante `..`.
- Escala la imagen.
- Conserva las imágenes en caché para no leerlas en cada repintado.
- Ajusta la altura de fila cuando sea necesario.

Importaciones:

```java
import Componentes.TablaAdministrativa;
import java.nio.file.Path;
import java.nio.file.Paths;
```

## 6. Mostrar estrellas

Las estrellas deben representar una valoración real, por ejemplo el promedio
de calificaciones de clientes entre 0 y 5.

En la base de datos se guarda el número, no los símbolos:

```sql
popularidad DECIMAL(2,1)
```

El modelo devuelve:

```java
case 6 -> new TablaAdministrativa.CeldaEstrellas(
        producto.getPopularidad(),
        5
);
```

Y declara la clase:

```java
if (columna == 6) {
    return TablaAdministrativa.CeldaEstrellas.class;
}
```

Después de asignar el modelo:

```java
tablaProductos.configurarColumnaEstrellas(
        6,
        new Color(255, 184, 0)
);
```

La celda muestra estrellas llenas y vacías. El valor numérico continúa
disponible en un tooltip.

## 7. Mostrar porcentajes

Para progreso, disponibilidad, ventas alcanzadas o popularidad estadística,
un porcentaje suele comunicar mejor que estrellas.

El modelo devuelve:

```java
case 6 -> new TablaAdministrativa.CeldaPorcentaje(
        producto.getPorcentajePopularidad()
);
```

Se configura así:

```java
tablaProductos.configurarColumnaPorcentaje(
        6,
        new Color(255, 184, 0)
);
```

El valor debe estar entre 0 y 100. El componente lo limita automáticamente
si accidentalmente llega un número fuera de ese rango.

## 8. ¿Estrellas o porcentaje?

Usar estrellas cuando:

- Los clientes califican el producto.
- Existe un promedio de valoraciones.
- El significado es calidad percibida.

Usar porcentaje cuando:

- Se mide popularidad por ventas.
- Se compara contra una meta.
- Se muestra disponibilidad o avance.
- El dato proviene de una fórmula estadística.

No conviene mostrar estrellas calculadas únicamente por cantidad de ventas,
porque el usuario normalmente interpreta las estrellas como calificaciones.
Para ventas puede mostrarse `82%`, una barra o el número de unidades.

## 9. Columnas con botones

Los botones Editar, Eliminar y el interruptor necesitan un
`TableCellRenderer` para verse y un `TableCellEditor` para recibir
clics. Esos componentes permanecen en cada módulo porque las acciones cambian
entre usuarios, productos, ingredientes y pedidos.

La tabla reutilizable se encarga del diseño general; cada pantalla conserva
sus reglas de negocio.

## 10. Orden recomendado al configurar

```java
// 1. Colocar los datos.
tablaProductos.setModel(modeloProductos);

// 2. Reaplicar el diseño del componente.
tablaProductos.aplicarEstilo();

// 3. Configurar columnas especiales.
tablaProductos.configurarColumnaImagenTexto(
        0, Paths.get("datos", "imagenes"), 48, 48);
tablaProductos.configurarColumnaEstrellas(
        6, new Color(255, 184, 0));

// 4. Ajustar anchos.
tablaProductos.getColumnModel()
        .getColumn(0).setPreferredWidth(300);
```

Si se vuelve a cambiar el modelo, deben configurarse nuevamente las columnas
especiales porque Swing crea un nuevo `TableColumnModel`.
