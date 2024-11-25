import iut.algo.Clavier;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {

        new Client();

    }

    public Client() {

        boolean deconnecte;
        String  messageEntrant;
        String  pseudo;

        System.out.println("Connexion au serveur en cours ...");

        try {

            Socket toServer = new Socket("localhost", 6000);

            System.out.println("Connecté au serveur !");

            PrintWriter    out = new PrintWriter(toServer.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(toServer.getInputStream()));

            // Vérifie si le pseudo est disponible ou non
            do {

                System.out.print("Entrez votre pseudo : ");
                pseudo = Clavier.lireString();
                out.println(pseudo);

            } while (!in.readLine().equals("PSEUDO OK"));

			// Gère l'envoie des messages
			ThreadEcriture thE = new ThreadEcriture(toServer);
			Thread threadEcriture = new Thread(thE);
			threadEcriture.start();

            deconnecte = false;
            while ( !deconnecte )
            {
				// Gère la lecture des messages des autres utilisateurs
                messageEntrant = in.readLine();
				
                if (!messageEntrant.contains(pseudo))
                    System.out.println(messageEntrant);

            }

            in.close();
            out.close();
            toServer.close();

        }
        catch (UnknownHostException e)  { e.printStackTrace(); }
        catch (Exception e)             { e.printStackTrace(); }

    }

}