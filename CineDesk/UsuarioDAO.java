import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * DAO (Data Access Object) de usuários.
 *
 * Centraliza todo o acesso aos dados de usuários, usado tanto pela
 * TelaCadastro quanto pela TelaLogin. Os dados ficam salvos no arquivo
 * "usuarios.dat", então continuam existindo depois que o programa fecha.
 *
 * Formato de cada linha: login;nome;salt;hash (cada campo em Base64).
 */
public class UsuarioDAO {

    private static final Path ARQUIVO = Paths.get("usuarios.dat");
    private static final String SEPARADOR = ";";

    /** Resultado possível de um cadastro. */
    public enum ResultadoCadastro { SUCESSO, USUARIO_JA_EXISTE, ERRO }

    /**
     * Cadastra um novo usuário.
     */
    public synchronized ResultadoCadastro cadastrar(String nome, String login, char[] senha) {
        try {
            if (buscarPorLogin(login) != null) {
                return ResultadoCadastro.USUARIO_JA_EXISTE;
            }
            String salt = gerarSalt();
            Usuario usuario = new Usuario(nome, login, salt, gerarHash(senha, salt));
            Files.write(ARQUIVO,
                    Collections.singletonList(paraLinha(usuario)),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return ResultadoCadastro.SUCESSO;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultadoCadastro.ERRO;
        }
    }

    /**
     * Verifica usuário e senha. Retorna o usuário autenticado ou null
     * se o login ou a senha estiverem incorretos.
     */
    public Usuario autenticar(String login, char[] senha) throws IOException {
        Usuario usuario = buscarPorLogin(login);
        if (usuario == null) {
            return null;
        }
        String hashDigitado = gerarHash(senha, usuario.getSalt());
        boolean senhaCorreta = MessageDigest.isEqual(
                hashDigitado.getBytes(StandardCharsets.UTF_8),
                usuario.getHashSenha().getBytes(StandardCharsets.UTF_8));
        return senhaCorreta ? usuario : null;
    }

    /** Busca um usuário pelo login. Retorna null se não existir. */
    public Usuario buscarPorLogin(String login) throws IOException {
        for (Usuario u : listar()) {
            if (u.getLogin().equalsIgnoreCase(login)) {
                return u;
            }
        }
        return null;
    }

    /** Lista todos os usuários cadastrados. */
    public List<Usuario> listar() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();
        if (!Files.exists(ARQUIVO)) {
            return usuarios;
        }
        for (String linha : Files.readAllLines(ARQUIVO, StandardCharsets.UTF_8)) {
            Usuario u = deLinha(linha);
            if (u != null) {
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    // ---------- Conversão de/para o arquivo ----------

    private String paraLinha(Usuario u) {
        return String.join(SEPARADOR,
                codificar(u.getLogin()),
                codificar(u.getNome()),
                u.getSalt(),
                u.getHashSenha());
    }

    private Usuario deLinha(String linha) {
        String[] partes = linha.split(SEPARADOR);
        if (partes.length != 4) {
            return null; // linha inválida ou vazia
        }
        try {
            return new Usuario(decodificar(partes[1]), decodificar(partes[0]), partes[2], partes[3]);
        } catch (IllegalArgumentException e) {
            return null; // Base64 corrompido
        }
    }

    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto) {
        return new String(Base64.getDecoder().decode(texto), StandardCharsets.UTF_8);
    }

    // ---------- Segurança da senha ----------

    private String gerarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String gerarHash(char[] senha, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hash = md.digest(new String(senha).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
