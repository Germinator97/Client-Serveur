/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author Asus R511L
 */
public class Client extends javax.swing.JFrame {
    
    // Déclaration des variables
    
    private javax.swing.JToggleButton jBtransfert;
    private javax.swing.JLabel jLabAdr;
    private javax.swing.JLabel jLabPort;
    private javax.swing.JLabel jLabFich;
    private javax.swing.JLabel jLabNom;
    private javax.swing.JLabel jLabTxt;
    private javax.swing.JLabel jLabReponse;
    private javax.swing.JLabel jLabTitre;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JSeparator separateur;
    private javax.swing.JTextArea jTxtContenu;
    private javax.swing.JTextField jTxtPort;
    private javax.swing.JTextField jTxtAdr;
    private javax.swing.JLabel lblDescription;
    
    private StringBuilder contenuFichier;
    BufferedReader fluxEntree;
    PrintStream fluxSortie;
    // Fin de la declaration
    
    public Client() {
        fenetre();
    }
    
    private void envoie(Socket socket, File fichier) throws FileNotFoundException, IOException {
        
        BufferedReader br = new BufferedReader(new FileReader(fichier));
        String texte;
        byte[] buffer = new byte[(int)fichier.length()];
        jTxtContenu.append("Transfert du fichier " + fichier.getName() + " (" + buffer.length + " bytes) en cours ...\n");
        FileInputStream fis = new FileInputStream(fichier);
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        bis.read(buffer, 0, buffer.length);
        
        OutputStream sortie = socket.getOutputStream();
        
        sortie.write(buffer, 0, buffer.length);
        sortie.flush();
        
        jTxtContenu.append("Transfert terminé.\n\n");
        jTxtContenu.append("Contenu de mon fichier :\n");
        jTxtContenu.append("**************************************\n");
        while ((texte = br.readLine()) != null) {
            contenuFichier.append(texte).append("\n");
        }
        jTxtContenu.append(contenuFichier.toString());
        jTxtContenu.append("**************************************\n\n\n");
        
        socket.shutdownOutput();
        BufferedReader entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String reponse = entree.readLine();
        jLabReponse.setText(reponse);
        
        socket.close();
        
    }
    
    @SuppressWarnings("empty-statement")
    private void fenetre() {
        
        jLabTitre = new javax.swing.JLabel();
        jTxtAdr = new javax.swing.JTextField();
        jTxtPort = new javax.swing.JTextField();
        jLabAdr = new javax.swing.JLabel();
        jLabPort = new javax.swing.JLabel();
        separateur = new javax.swing.JSeparator();
        jBtransfert = new javax.swing.JToggleButton();
        jLabFich = new javax.swing.JLabel();
        jLabNom = new javax.swing.JLabel();
        jLabTxt = new javax.swing.JLabel();
        jScroll = new javax.swing.JScrollPane();
        jTxtContenu = new javax.swing.JTextArea();
        jLabReponse = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();

        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TRANSFERT DE FICHIERS : Client - Serveur");
        setResizable(false);
        
        jLabTitre.setFont(new java.awt.Font("Tempus Sans ITC", 1, 24));
        jLabTitre.setText("Application cliente pour l'envoi de fichiers à un serveur");

        jTxtAdr.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jTxtAdr.setText("127.0.0.1");

        jTxtPort.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jTxtPort.setText("3000");

        jLabAdr.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jLabAdr.setText("Adresse du serveur :");

        jLabPort.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jLabPort.setText("Port utilisé :");
        
        lblDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        jBtransfert.setBackground(new java.awt.Color(0, 0, 255));
        jBtransfert.setForeground(Color.white);
        jBtransfert.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jBtransfert.setText("Fichier à transférer");
        jBtransfert.addActionListener((ActionEvent evt) -> {
            //Selection du fichier à transferer
            JFileChooser selectFichier = new JFileChooser(new File("."));
            int result = selectFichier.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION) {
                
                File fichier = selectFichier.getSelectedFile();
                if(fichier.isFile()) {
                    new Thread(() -> {
                        try {
                            jLabReponse.setText("Attente d'une connexion");
                            
                            jLabNom.setText(fichier.getName());
                            
                            contenuFichier =  new StringBuilder();
                            
                            String serveur = jTxtAdr.getText();
                            int numeroPort = Integer.parseInt(jTxtPort.getText());
                            
                            InetAddress adr = InetAddress.getByName(serveur);
                            jTxtContenu.append("Tentative de connexion au serveur ...\n\n");
                            
                            Socket socket = new Socket(adr, numeroPort);
                            jLabReponse.setText("Connexion reussie");
                            jTxtContenu.append("Connexion réussie (" + socket + ") \n\n");
                            
                            // Pour envoyer sur le reseau
                            envoie(socket, fichier);
                            
                        }
                        catch (FileNotFoundException ex) {
                        }
                        catch (IOException ex) {;
                        }
                    }).start(); 
                }
            }
        });
        
        jLabFich.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jLabFich.setText("Nom du fichier à envoyer au serveur :");

        jLabNom.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14));
        jLabNom.setText("Nom du fichier");

        jLabTxt.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14));
        jLabTxt.setText("Le contenu du fichier sera affiché dans la zone de texte suivante");

        jTxtContenu.setFont(new java.awt.Font("Verdana", 0, 12));
        jTxtContenu.setColumns(20);
        jTxtContenu.setRows(5);
        jScroll.setViewportView(jTxtContenu);

        jLabReponse.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14));
        jLabReponse.setText("Envoyer un fichier à un serveur");
        
        lblDescription.getAccessibleContext().setAccessibleName("lblDescription");
        lblDescription.getAccessibleContext().setAccessibleDescription("");
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(separateur)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabTitre, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabTxt)
                                .addGap(115, 115, 115))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabAdr)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTxtAdr, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabFich, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabPort, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTxtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabNom)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabReponse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBtransfert, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabTitre)
                .addGap(5, 5, 5)
                .addComponent(separateur, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTxtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabPort, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabAdr)
                        .addComponent(jTxtAdr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabFich)
                    .addComponent(jLabNom))
                .addGap(18, 18, 18)
                .addComponent(jLabTxt)
                .addGap(18, 18, 18)
                .addComponent(jScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtransfert, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabReponse))
                .addGap(6, 6, 6))
        );
        
        pack();
        setLocationRelativeTo(null);
    }    
    public static void main(String[] arg) {
        new Client().setVisible(true);
    }
}
