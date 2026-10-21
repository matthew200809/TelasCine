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

public class TelaLogin extends JFrame {

    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtSenha = new JPasswordField(20);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnCadastro = new JButton("Cadastro");

    public TelaLogin() {
        setTitle("CineDesk - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(painel, gbc, 0, "Usuário:", txtUsuario);
        adicionarCampo(painel, gbc, 1, "Senha:", txtSenha);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCadastro);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);

        btnLogin.addActionListener(e -> login());
        btnCadastro.addActionListener(e -> abrirCadastro());

        // Enter no campo de senha também faz login
        getRootPane().setDefaultButton(btnLogin);

        // Faixa vinho no topo com o título à esquerda
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cabecalho.setBackground(new Color(114, 18, 38));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        JLabel titulo = new JLabel("CineDesk - Login");
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

    private void login() {
        String usuario = txtUsuario.getText().trim();
        char[] senha = txtSenha.getPassword();

        if (usuario.isEmpty() || senha.length == 0) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String senhaCadastrada = TelaCadastro.usuarios.get(usuario);
        boolean valido = senhaCadastrada != null && Arrays.equals(senha, senhaCadastrada.toCharArray());
        Arrays.fill(senha, '\0');

        if (valido) {
            JOptionPane.showMessageDialog(this, "Login realizado com sucesso! Bem-vindo, " + usuario + ".");
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
        }
    }

    private void abrirCadastro() {
        new TelaCadastro().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
