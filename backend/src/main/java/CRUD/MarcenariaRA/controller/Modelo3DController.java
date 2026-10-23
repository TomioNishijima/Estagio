
package CRUD.MarcenariaRA.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/modelos")
public class Modelo3DController {

    private final Path pastaModelos = Path.of(
            System.getProperty("user.home"),
            "MarcenariaNishijima",
            "modelos"
    );

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("arquivo") MultipartFile arquivo) {

        String nome = arquivo.getOriginalFilename();

        if (arquivo.isEmpty() || nome == null ||
                !nome.toLowerCase(Locale.ROOT).endsWith(".glb")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione um arquivo .glb válido."
            );
        }

        try {
            // Confere a assinatura de um arquivo GLB
            try (InputStream entrada = arquivo.getInputStream()) {
                byte[] assinatura = entrada.readNBytes(4);

                if (assinatura.length != 4 ||
                        assinatura[0] != 'g' ||
                        assinatura[1] != 'l' ||
                        assinatura[2] != 'T' ||
                        assinatura[3] != 'F') {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "O arquivo não é um modelo GLB válido."
                    );
                }
            }

            Files.createDirectories(pastaModelos);

            String novoNome = UUID.randomUUID() + ".glb";

            Files.copy(
                    arquivo.getInputStream(),
                    pastaModelos.resolve(novoNome)
            );

            return ResponseEntity.ok(
                    Map.of(
                            "modelo", "/modelos/uploads/" + novoNome,
                            "message", "Upload realizado com sucesso!"
                    )
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao armazenar o modelo 3D.",
                    e
            );
        }
    }
}
