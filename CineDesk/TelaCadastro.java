import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

public class TelaCadastro extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private final JTextField txtNome = new JTextField(20);
    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtSenha = new JPasswordField(20);
    private final JPasswordField txtConfirmarSenha = new JPasswordField(20);
    private final JButton btnCadastrar = new JButton("Cadastrar");
    private final JButton btnLogin = new JButton("Login");

    public TelaCadastro() {
        setTitle("CineDesk - Cadastro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(painel, gbc, 1, "Nome:", txtNome);
        adicionarCampo(painel, gbc, 2, "Usuário:", txtUsuario);
        adicionarCampo(painel, gbc, 3, "Senha:", txtSenha);
        adicionarCampo(painel, gbc, 4, "Confirmar senha:", txtConfirmarSenha);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnLogin);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);

        btnCadastrar.addActionListener(e -> cadastrar());
        btnLogin.addActionListener(e -> login());

        // Faixa vinho no topo com o título à esquerda
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cabecalho.setBackground(new Color(114, 18, 38));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        JLabel titulo = new JLabel("CineDesk - Cadastro");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        cabecalho.add(titulo);

        setLayout(new BorderLayout());
        add(cabecalho, BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JTextField campo) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String usuario = txtUsuario.getText().trim();
        char[] senha = txtSenha.getPassword();
        char[] confirmar = txtConfirmarSenha.getPassword();

        if (nome.isEmpty() || usuario.isEmpty() || senha.length == 0 || confirmar.length == 0) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Arrays.equals(senha, confirmar)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (usuario.contains(" ")) {
            JOptionPane.showMessageDialog(this, "O usuário não pode conter espaços.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (senha.length < 4) {
            JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 4 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO.ResultadoCadastro resultado = usuarioDAO.cadastrar(nome, usuario, senha);
        Arrays.fill(senha, '\0');
        Arrays.fill(confirmar, '\0');

        switch (resultado) {
            case USUARIO_JA_EXISTE:
                JOptionPane.showMessageDialog(this, "Usuário já cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            case ERRO:
                JOptionPane.showMessageDialog(this, "Erro ao salvar o cadastro.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            default:
                break;
        }

        JOptionPane.showMessageDialog(this, "Usuário " + nome + " cadastrado com sucesso!");
        limparCampos();
    }

    private void login() {
        new TelaLogin().setVisible(true);
        dispose();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtUsuario.setText("");
        txtSenha.setText("");
        txtConfirmarSenha.setText("");
        txtNome.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro().setVisible(true));
    }
}
