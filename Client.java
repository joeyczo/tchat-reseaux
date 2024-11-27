import iut.algo.Clavier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client 
{

	private boolean deconnecte;
	private String  messageEntrant;
	private String  pseudo;

	private PrintWriter    out;
	private BufferedReader in;

	private Socket socket;


	public Client() 
	{
		this.deconnecte = false;

		System.out.println("Connexion au serveur en cours ...");

		try 
		{

			this.socket = new Socket("localhost", 6000);

			System.out.println("Connecté au serveur !");

			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			String retourGDC = "";

			// Vérifie si le pseudo est disponible ou non
			do 
			{
				System.out.print("Entrez votre pseudo : ");
				this.pseudo = Clavier.lireString();
				out.println(this.pseudo);

				retourGDC = this.in.readLine();
				System.out.println(retourGDC); // Affiche le message d'erreur de l'entrer du pseudo ou si il est accepté

			} while (!retourGDC.equals("Pseudo accepté"));

			// Gère l'envoie des messages
			ThreadEcriture thE    = new ThreadEcriture(this.socket);
			Thread threadEcriture = new Thread(thE);
			threadEcriture.start();

			while ( !this.deconnecte )
			{
				// Gère la lecture des messages des autres utilisateurs
				this.messageEntrant = in.readLine();
				
				if (!this.messageEntrant.contains("["+this.pseudo+"]"))
					System.out.println(this.messageEntrant);
			}

			this.in    .close();
			this.out   .close();
			this.socket.close();

		}
		catch (UnknownHostException e)  
		{ 
			e.printStackTrace(); 
		}
		catch (Exception e)
		{ 
			e.printStackTrace(); 
		}

	}

	public static void main(String[] args) { new Client(); }
}