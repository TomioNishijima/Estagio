package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Usuario;
import CRUD.MarcenariaRA.repository.UsuarioRepository;
import CRUD.MarcenariaRA.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public static class LoginRequest {
        public String email;
        public String senha;
    }

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O nome é obrigatório.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O e-mail é obrigatório.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A senha é obrigatória.");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Este e-mail já está cadastrado.");
        }

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        usuarioSalvo.setSenha(null);

        return ResponseEntity.ok(usuarioSalvo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.email)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest()
                    .body("E-mail ou senha inválidos.");
        }

        boolean senhaCorreta = passwordEncoder.matches(
                request.senha,
                usuario.getSenha()
        );

        if (!senhaCorreta) {
            return ResponseEntity.badRequest()
                    .body("E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarToken(
                usuario.getEmail()
        );

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail()
                )
        );
    }

    public static class LoginResponse {

        public String token;
        public Long id;
        public String nome;
        public String email;

        public LoginResponse(String token,
                             Long id,
                             String nome,
                             String email) {

            this.token = token;
            this.id = id;
            this.nome = nome;
            this.email = email;
        }
    }

}
