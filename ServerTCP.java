/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVER_GUI_CHAT;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;

public class ServerTCP implements Runnable {

    private Socket socket;
    private DataOutputStream output;
    private BufferedReader input;
    private String nomeClient;
    public static ArrayList<ServerTCP> clienti = new ArrayList<>();
    private String ricevuta;

    public ServerTCP(Socket socket) {
        try {
            this.socket = socket;
            clienti.add(this);
            output = new DataOutputStream(this.socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.nomeClient = input.readLine();
            invia("SERVER: " + nomeClient + " si è connesso!");
        } catch (IOException ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    public void comunica() {
        while (socket.isConnected()) {
            try {
                ricevuta = input.readLine();
                System.out.println(ricevuta);
                invia(ricevuta);
            } catch (IOException ex) {
                chiudiTutto(socket, input, output);
                break;
            }

        }
    }

    public void rimuoviClient() {

        clienti.remove(this);
        invia("SERVER: " + nomeClient + " è uscito dalla chat!");
        
    }

    private void invia(String messaggio) {
        try {
            for(ServerTCP cliente:clienti){
                if(cliente!=null&&!cliente.nomeClient.equals(nomeClient)){
                    cliente.output.writeBytes(messaggio + "\n");
                }
            }
        } catch (IOException ex) {
            chiudiTutto(socket, input, output);
        }

    }

    public void chiudiTutto(Socket socket, BufferedReader bufferedReader, DataOutputStream output) {
        rimuoviClient();
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
        comunica();
    }
}
