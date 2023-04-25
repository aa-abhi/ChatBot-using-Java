package org.example;

import org.json.JSONException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;

public class ChatInterface extends JFrame implements ActionListener {
    private final JTextField messageField;
    private final JTextArea chatArea;
    private final Chatbot chatbot;

    public ChatInterface(Chatbot chatbot) {
        super("Chatbot");

        this.chatbot = chatbot;
        // Load the chatbot icon
        ImageIcon icon = new ImageIcon("src/main/java/logo.jpg");
        setIconImage(icon.getImage());
        messageField = new JTextField(20);
        messageField.addActionListener(this);

        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);

        add(panel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String message = messageField.getText().trim();
        messageField.setText("");

        String response = chatbot.getResponse(message);
        chatArea.append("You: " + message + "\n");
        if(response.startsWith("http")){
            String clickableLink = makeClickableLink(response);
            chatArea.append("Chatbot: " + clickableLink + "\n");
        }else{
            chatArea.append("Chatbot: " + response + "\n");
        }

        // Check if chatbot didn't understand and do a Google search
        if (response.equals("I'm sorry, I didn't understand what you said.")) {
            try {
                Desktop.getDesktop().browse(new URI("https://www.google.com/search?q=" + message));
            } catch (IOException | URISyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Failed to open browser.");
            }
        }
    }

    // Method to create a clickable URL in the chat area
    private String makeClickableLink(String url) {
        return "<html><u><font color='blue'>" + url + "</font></u></html>";
    }

    public static void main(String[] args) throws IOException, JSONException {
        Chatbot chatbot = new Chatbot("src/main/java/intents.json");
        ChatInterface chatInterface = new ChatInterface(chatbot);
    }
}
