/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI_CHAT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author INTEL
 */
public class Client implements Runnable {

    private String indirizzo;
    private int porta;
    private String nome;
    private DataOutputStream output;
    private BufferedReader input;
    private Socket socket;
    private boolean connesso;
    private String daInviare, ricevuta;
    private JButton invio, confermaNome, connessione;
    private JTextField inserimento, nomeField, TXTIp, TXTPorta;
    private DefaultListModel modello;
    private JList lista;
    private JLabel exitLabel;

    public Client(String indirizzo, int porta, JButton invia, JTextField inserimento, DefaultListModel modello, JList lista, JTextField nomeField, JButton confermaNome, JButton connessione,
            JTextField ip, JTextField portaField, JLabel exit) {
        this.invio = invia;
        connesso = false;
        this.confermaNome = confermaNome;
        this.connessione = connessione;
        this.exitLabel = exit;
        this.modello = modello;
        this.lista = lista;
        this.TXTIp = ip;
        this.TXTPorta = portaField;
        this.inserimento = inserimento;
        this.indirizzo = indirizzo;
        this.porta = porta;
        this.nomeField = nomeField;
        this.nome = "";
    }

    private void connetti() {
        this.confermaNome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nome = nomeField.getText();
                System.out.println(nome);
                confermaNome.setEnabled(false);
                connessione.setEnabled(true);
            }
        });
        this.connessione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nome.equals("") && nome != null) {
                    try {
                        try {
                            socket = new Socket(indirizzo, porta);
                            connesso = true;
                        } catch (IOException ex1) {
                            modello.addElement("Non è possibile collegarsi in questo momento :(");
                            lista.setModel(modello);
                        }

                        if (connesso) {
                            TXTIp.setEditable(false);
                            TXTPorta.setEditable(false);
                            nomeField.setEditable(false);
                            modello.addElement("Connesso col server");
                            lista.setModel(modello);
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            output = new DataOutputStream(socket.getOutputStream());
                            ascolta();
                            output.writeBytes(nome + "\n");
                            connessione.setEnabled(false);
                            exitLabel.setVisible(true);
                        }

                    } catch (IOException ex) {
                        System.out.println("Errore: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void comunica() {
        this.invio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    daInviare = inserimento.getText();
                    output.writeBytes(nome + ": " + daInviare + "\n");
                    inserimento.setText("");
                } catch (IOException ex) {
                    System.out.println("Errore: " + ex.getMessage());
                }
            }
        });

    }

    private void ascolta() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creo un altro thread per ascoltare cosi non ho problemi di blocco della comunicazione
                while (socket.isConnected()) {
                    try {
                        ricevuta = input.readLine();
                        modello.addElement(ricevuta);
                        lista.setModel(modello);
                        System.out.println(ricevuta);
                    } catch (IOException ex) {
                        chiudiTutto(socket, input, output);
                    }
                }
            }
        }).start();
    }

    private void chiudiTutto(Socket socket, BufferedReader bufferedReader, DataOutputStream output) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        connetti();
        comunica();
    }
}
