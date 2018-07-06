/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveursansthread;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 *
 * @author Asus R511L
 */
public class ServeurSansThread extends JFrame {
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton jBserveur;
    private javax.swing.JLabel jLabClient;
    private javax.swing.JLabel jLabPort;
    private javax.swing.JLabel jLabQuestion;
    private javax.swing.JLabel jLabText;
    private javax.swing.JLabel jLabTitre;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparateur1;
    private javax.swing.JSeparator jSeparateur2;
    private javax.swing.JTextArea jTextContenu;
    private javax.swing.JTextField jTextPort;
    private javax.swing.JLabel lblDescription;
    // End of variables declaration 

    private StringBuilder contenu;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new ServeurSansThread().setVisible(true);
        
    }
    
    public ServeurSansThread() {
        fenetre();
    }
    
    private void serveur() {
        
        try {
            jTextContenu.setText("Démarrage du serveur sur le port " + jTextPort.getText() + " ...\n");
            int port = Integer.parseInt(jTextPort.getText());
            ServerSocket serveur = new ServerSocket(port);
            jTextContenu.append("Le serveur a demarré sur le port " + port + ".\n");
            jTextContenu.append("Le serveur est en attente de connexion de clients ..." + "\n\n");
            
            new Thread(() -> {
                try {
                    while (true) {
                        
                        contenu =  new StringBuilder();
                        Socket socket = serveur.accept();
                        
                        jTextContenu.append("Un client s'est connecté : ");
                        jTextContenu.append(socket.toString() + "\n");
                        String msg = "Client connecté : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                        jLabClient.setText(msg);
                        
                        jTextContenu.append("Réception du fichier envoyé en cours ...\n\n");
                        // Pour la lecture du fichier reçu
                        
                        int taille = 6022386; // On fixe la taille maximale a 50Mo
                        int bytes;
                        int bits = 0;
                        
                        byte[] buffer = new byte[taille]; // On reçoit le fichier par paquet de 4096 bytes
                        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                        FileOutputStream fos = new FileOutputStream("temp.txt");
                        
                        bytes = bis.read(buffer, 0, buffer.length);
                        bits = bytes;
                        
                        do {
                            bytes = bis.read(buffer, bits, (buffer.length - bits));
                            
                            if(bytes >= 0) bits += bytes;
                        } while (bytes > -1);
                        
                        jTextContenu.append("Reception du fichier terminée avec une taille de " + bits + " bytes.\n\n");
                        jTextContenu.append("Contenu du fichier reçu :\n");
                        // Affichage du contenu du fichier
                        fos.write(buffer, 0, bits);
                        jTextContenu.append("*****************************************\n");
                        BufferedReader br = new BufferedReader(new FileReader("temp.txt"));
                        String texte;
                        while((texte = br.readLine()) != null) {
                            contenu.append(texte).append("\n");
                        }
                        jTextContenu.append(contenu.toString());
                        jTextContenu.append("*****************************************\n\n\n");
                        
                        // Envoi accusé de reception
                        PrintWriter fluxSortie = new PrintWriter(socket.getOutputStream(), true);
                        fluxSortie.println("Réponse du Serveur : Fichier reçu");
                    }
                } catch (IOException ex) {
                    jTextContenu.append(ex.toString());
                }
            }).start();
            
        }
        catch (IOException | NumberFormatException e) {
            jLabClient.setText(e.toString());
            jTextContenu.setText("Le serveur s'est arreté, veuillez fermer et redémarrer.");
        }
        
    }
    
    private void fenetre() {
        
        jLabTitre = new javax.swing.JLabel();
        jTextPort = new javax.swing.JTextField();
        jLabPort = new javax.swing.JLabel();
        jSeparateur1 = new javax.swing.JSeparator();
        jBserveur = new javax.swing.JButton();
        jLabText = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextContenu = new javax.swing.JTextArea();
        jLabClient = new javax.swing.JLabel();
        jLabQuestion = new javax.swing.JLabel();
        jSeparateur2 = new javax.swing.JSeparator();
        lblDescription = new javax.swing.JLabel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RECEPTION DE FICHIERS : Serveur sans thread");
        setResizable(false);
        
        jLabTitre.setFont(new java.awt.Font("Tempus Sans ITC", 1, 24)); // NOI18N
        jLabTitre.setText("Application serveur pour la réception de fichiers de clients");

        jTextPort.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        jTextPort.setText("5000");

        jLabPort.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        jLabPort.setText("Port utilisé :");
        
        jBserveur.setBackground(new java.awt.Color(0, 0, 255));
        jBserveur.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        jBserveur.setForeground(Color.white);
        jBserveur.setText("Lancer le serveur");
        jBserveur.addActionListener((ActionEvent evt) -> {
            serveur();
        });

        jLabText.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabText.setText("Le contenu du fichier reçu sera affiché dans la zone de texte suivante");

        jTextContenu.setColumns(20);
        jTextContenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextContenu.setRows(5);
        jScrollPane1.setViewportView(jTextContenu);

        jLabClient.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabClient.setText("Client connecté : auncun client connecté");

        jLabQuestion.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        jLabQuestion.setText("Serveur sans thread");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jSeparateur1, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addComponent(jLabTitre))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(102, 102, 102)
                            .addComponent(jLabText)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(173, 173, 173)
                        .addComponent(jLabPort, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextPort, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jBserveur, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabClient)
                    .addComponent(jLabQuestion)
                    .addComponent(jSeparateur2, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabTitre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparateur1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabQuestion)
                    .addComponent(jLabPort, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBserveur, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparateur2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jLabClient)
                .addContainerGap())
        );
        
        pack();
        setLocationRelativeTo(null);
        
    }
}
