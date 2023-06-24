import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class DES extends JFrame {
    private JTextField claveTextField;
    private JTextArea mensajeTextArea;
    private JTextArea mensajeCifradoTextArea;
    private JTextArea mensajeDescifradoTextArea;

    public DES() {
        // Configuración de la ventana
        setTitle("Cifrado de Mensajes");
        setSize(750, 937);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(237, 240, 252)); // Fondo en tono pastel

        // Creación de los componentes de la interfaz
        JLabel claveLabel = new JLabel("Clave:");
        claveTextField = new JTextField(10);
        claveTextField.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente legible
        claveTextField.setDocument(new JTextFieldLimit(8)); // Limitar la entrada a 8 caracteres
        JLabel mensajeLabel = new JLabel("Mensaje:");
        mensajeTextArea = new JTextArea(14, 40);
        mensajeTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente legible
        JButton cargarButton = new JButton("Cargar Archivo");
        cargarButton.setFont(new Font("Arial", Font.BOLD, 18)); // Fuente llamativa
        JButton cifrarButton = new JButton("Cifrar");
        cifrarButton.setFont(new Font("Arial", Font.BOLD, 18)); // Fuente llamativa
        JButton descifrarButton = new JButton("Descifrar");
        descifrarButton.setFont(new Font("Arial", Font.BOLD, 18)); // Fuente llamativa
        JLabel mensajeCifradoLabel = new JLabel("Mensaje Cifrado:");
        mensajeCifradoTextArea = new JTextArea(14, 40);
        mensajeCifradoTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente legible
        mensajeCifradoTextArea.setEditable(false);
        JLabel mensajeDescifradoLabel = new JLabel("Mensaje Descifrado:");
        mensajeDescifradoTextArea = new JTextArea(14, 40);
        mensajeDescifradoTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente legible
        mensajeDescifradoTextArea.setEditable(false);

        // Configuración del diseño de la interfaz con proporciones áureas
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(claveLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(claveTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(mensajeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(new JScrollPane(mensajeTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(cargarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(cifrarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(descifrarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(mensajeCifradoLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        add(new JScrollPane(mensajeCifradoTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(mensajeDescifradoLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        add(new JScrollPane(mensajeDescifradoTextArea), gbc);

        // Configuración de los eventos de los botones
        cargarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileReader reader = new FileReader(file);
                        BufferedReader br = new BufferedReader(reader);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }
                        br.close();
                        mensajeTextArea.setText(sb.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        mostrarMensajeEmergente("Error al cargar el archivo.");
                    }
                }
            }
        });

        cifrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clave = claveTextField.getText();
                if (clave.length() != 8) {
                    mostrarMensajeEmergente("La clave debe tener exactamente 8 caracteres.");
                    return;
                }
                String mensaje = mensajeTextArea.getText();
                String mensajeCifrado = cifrarMensaje(clave, mensaje);
                if (mensajeCifrado != null) {
                    mensajeCifradoTextArea.setText(mensajeCifrado);
                } else {
                    mostrarMensajeEmergente("No se pudo cifrar el mensaje.");
                }
            }
        });

        descifrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clave = claveTextField.getText();
                if (clave.length() != 8) {
                    mostrarMensajeEmergente("La clave debe tener exactamente 8 caracteres.");
                    return;
                }
                String mensajeCifrado = mensajeCifradoTextArea.getText();
                String mensajeDescifrado = descifrarMensaje(clave, mensajeCifrado);
                if (mensajeDescifrado != null) {
                    mensajeDescifradoTextArea.setText(mensajeDescifrado);
                } else {
                    mostrarMensajeEmergente("No se pudo descifrar el mensaje.");
                }
            }
        });

        pack();
    }

    private String cifrarMensaje(String clave, String mensaje) {
        try {
            SecretKeySpec key = new SecretKeySpec(clave.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
            return Base64.getEncoder().encodeToString(mensajeCifrado);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String descifrarMensaje(String clave, String mensajeCifrado) {
        try {
            SecretKeySpec key = new SecretKeySpec(clave.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytesDescifrados = cipher.doFinal(Base64.getDecoder().decode(mensajeCifrado));
            return new String(bytesDescifrados);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mostrarMensajeEmergente(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DES().setVisible(true);
            }
        });
    }

    // Clase para limitar la longitud de un JTextField
    class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            } else {
                mostrarMensajeEmergente("La clave debe tener exactamente 8 caracteres.");
            }
        }
    }
}
















