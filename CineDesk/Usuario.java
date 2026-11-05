/**
 * Modelo que representa um usuário do CineDesk.
 * A senha nunca é guardada em texto puro: apenas o salt e o hash.
 */
public class Usuario {

    private final String nome;
    private final String login;
    private final String salt;
    private final String hashSenha;

    public Usuario(String nome, String login, String salt, String hashSenha) {
        this.nome = nome;
        this.login = login;
        this.salt = salt;
        this.hashSenha = hashSenha;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getSalt() {
        return salt;
    }

    public String getHashSenha() {
        return hashSenha;
    }
}
