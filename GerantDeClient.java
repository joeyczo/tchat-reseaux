import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GerantDeClient implements Runnable
{

	private final String COULEUR_VERT     = "\u001B[32m";
	private final String COULEUR_ROUGE    = "\u001B[31m";
	private final String COULEUR_GRIS     = "\u001B[90m";
	private final String ITALIQUE         = "\u001B[3m";
	private final String REINITIALISATION = "\u001B[0m";

	private Socket  socket;
	private Serveur serv;
	private String  pseudo;

	private PrintWriter     out;
	private BufferedReader  in;


	public GerantDeClient(Socket s, Serveur serv) 
	{

		this.socket = s;
		this.serv   = serv;

		try 
		{
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	/**
	 * Envoie un message au client lié à ce gérent de client
	 * @param s Le message à envoyer
	 */
	public void envoyerMessage(String s) { this.out.println(s); }


	/**
	 * Envoie le message que si c'est le client concerné
	 * @param message le message à envoyer
	 * @param pseudo le pseudo de l'utilisateur concerné
	 */
	public void envoyerMessagePrive(String message, String pseudo)
	{
		if (this.pseudo.equals(pseudo))
			this.out.println(message);
	}

	public void run() 
	{

		try 
		{

			boolean peutAjouterPseudo = false;
			boolean espaceDansPseudo  = false;

			while ( !peutAjouterPseudo || espaceDansPseudo ) 
			{
				this.pseudo       = this.in.readLine();

				espaceDansPseudo  = this.pseudo.contains(" ");
				peutAjouterPseudo = this.serv.ajouterUtilisateur(this.pseudo);

				if ( espaceDansPseudo  )       this.out.println("Le pseudo ne doit pas contenir d'espace");
				else if ( !peutAjouterPseudo ) this.out.println("Le pseudo est déjà utilisé");
			}

			this.out.println("Pseudo accepté"); // Permets de savoir si le pseudo est disponible pour le client

			// Vérifie si l'utilisateur se déconnecte sans entrer de pseudo
			if ( !(this.pseudo == null) )
				this.serv.envoyerMessage(this.COULEUR_VERT + this.pseudo + " s'est connecté" + this.REINITIALISATION);

			boolean bok = true;
			while(bok) 
			{
				String msg = this.in.readLine();

				bok = (msg != null); // Vérifie si le client est toujours là
				if( !bok )
				{
					if (!(this.pseudo == null))
						this.serv.envoyerMessage(this.COULEUR_ROUGE + this.pseudo + " s'est déconnecté" + this.REINITIALISATION);
				}
				else
				{
					if (msg.startsWith("/msg"))
					{
						String[] texte     = msg.split(" ");
						String   pseudoMsg = texte[1];

						// 5 pour la taille "/msg " + la taille du pseudo
						String   message   = msg.substring(5 + pseudoMsg.length());

						if ( message.length() >= 1 && this.serv.verifPseudo(pseudoMsg) )
							this.serv.envoyerMessagePrive(this.ITALIQUE + this.COULEUR_GRIS + "(De " + this.pseudo + " à vous)" + message + this.REINITIALISATION, pseudoMsg);
						else
							this.out.println("Erreur de syntaxe pour le message privé \"/msg \"pseudo\" \"message\"\" ");
					}
					else
						this.serv.envoyerMessage("[" + this.pseudo + "] " + msg );
				}
			}

			this.serv.deconnecterUtilisateur(this.pseudo);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}
