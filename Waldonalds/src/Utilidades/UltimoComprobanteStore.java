package Utilidades;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Persiste el último comprobante confirmado para restaurarlo tras reiniciar la caja. */
public final class UltimoComprobanteStore {
    private final Path archivo;

    public UltimoComprobanteStore(int usuario) {
        this(Path.of("comprobantes_ultimo", "caja-" + usuario + ".txt"));
    }

    public UltimoComprobanteStore(Path archivo) {
        this.archivo = archivo;
    }

    public void guardar(String comprobante) throws IOException {
        if (comprobante == null || comprobante.isBlank()) {
            throw new IOException("El comprobante está vacío.");
        }

        Path destino = archivo.toAbsolutePath();
        Path directorio = destino.getParent();
        Files.createDirectories(directorio);
        Path temporal = Files.createTempFile(directorio, "comprobante-", ".tmp");
        try {
            Files.writeString(temporal, comprobante, StandardCharsets.UTF_8);
            Files.move(temporal, destino,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public String cargar() throws IOException {
        Path destino = archivo.toAbsolutePath();
        if (!Files.exists(destino)) return null;
        String comprobante = Files.readString(destino, StandardCharsets.UTF_8);
        return comprobante.isBlank() ? null : comprobante;
    }
}
