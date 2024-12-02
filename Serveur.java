import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Serveur {

	private List<String>         connectes;
	private List<GerantDeClient> clients;
	private List<String>         messages;

	public Serveur() 
	{

		this.connectes = new LinkedList<String>        ();
		this.clients   = new LinkedList<GerantDeClient>();
		this.messages  = new LinkedList<String>        ();

		try 
		{

			ServerSocket ss  = new ServerSocket(6000);
			boolean      bOk = true;
			
			while (bOk)
			{

				Socket s = ss.accept();

				GerantDeClient gcg = new GerantDeClient(s, this);

				this.clients.add(gcg);

				Thread tgdc = new Thread(gcg);

				tgdc.start();

			}

			ss.close();

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


	/**
	 * Permet d'envoyer un message à tous les gérants de client pour envoyer par la suite au client lié
	 * @param message message à envoyer
	 */
	public void envoyerMessage(String message)
	{
		for (GerantDeClient gdc : this.clients)
			gdc.envoyerMessage(message);

		this.messages.add(message);
	}


	/**
	 * Permet d'envoyer un message à tous les gérants de client pour envoyer par la suite au client concerné
	 * @param message le message à transmettre
	 * @param pseudo le pseudo de l'utilisateur à qui envoyer le message
	 */
	public void envoyerMessagePrive(String message, String pseudo)
	{
		for (GerantDeClient gdc : this.clients)
			gdc.envoyerMessagePrive(message, pseudo);
	}

	/**
	 * Envoie au gérant de client les messages contenant la recherche
	 * @param pseudo le pseudo de l'utilisateur à qui envoyer la liste des messages ou la recherche est présente
	 * @param recherche la recherche que l'utilisateur à passer
	 */
	public void envoyerRecherche(String pseudo, String recherche)
	{
		List<String> recherches = new ArrayList<String>();

		for(String message : messages)
			if(message.contains(recherche))
				recherches.add(message);

		if ( recherches.isEmpty() )
			recherches.add("Auncun message ne contient : " + recherche);

		for (GerantDeClient gdc : this.clients)
			gdc.envoyerRecherche(pseudo, recherches);
	}


	/**
	 * Vérifie si le pseudo passer en paramètre existe ou non
	 * @param pseudo pseudo à vérifier
	 * @return vrai si le pseudo existe
	 */
	public boolean verifPseudo(String pseudo)
	{
		boolean pseudoExiste = false;

		for (String pseudoConnectes : this.connectes)
		{
			if (pseudoConnectes.equals(pseudo))
				pseudoExiste = true;
		}

		return pseudoExiste;
	}

	/**
	 * Permet d'ajouter un nouvel utilisateur à la liste des utilisateurs connectés
	 * Doit également vérifier que l'utilisateur n'existe pas
	 * @param pseudo Nom de l'utilisateur
	 * @return true s'il peut se connecter, sinon false
	 */
	public boolean ajouterUtilisateur(String pseudo)
	{
		boolean peutAjouter = true;

		for (String sCo : this.connectes) 
		{
			if (sCo != null && sCo.equals(pseudo))
			{
				peutAjouter = false;
				break;
			}
		}

		if (!peutAjouter) return false;

		this.connectes.add(pseudo);

		return true;
	}

	/**
	 * Supprime le pseudo de l'utilisateur qui se déconnecte 
	 * @param pseudo pseudo de l'utilisateur
	 */
	public void deconnecterUtilisateur(String pseudo) { this.connectes.remove(pseudo); }
}