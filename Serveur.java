import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Serveur {

	private List<String>         connectes;
	private List<GerantDeClient> clients;

	public Serveur() 
	{

		this.connectes = new LinkedList<>();
		this.clients   = new LinkedList<>();

		try 
		{

			ServerSocket ss  = new ServerSocket(6000);
			boolean      bOk = true;
			
			while (bOk) {

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
	 * @param s message à envoyer
	 */
	public void envoyerMessage(String s) 
	{
		for (GerantDeClient gdc : this.clients)
			gdc.envoyerMessage(s);
	}

	/**
	 * Permet d'ajouter un nouvel utilisateur à la liste des utilisateurs connectés
	 * Doit également vérifier que l'utilisateur n'existe pas
	 * @param s Nom de l'utilisateur
	 * @return true s'il peut se connecter, sinon false
	 */
	public boolean ajouterUtilisateur(String s) 
	{
		boolean peutAjouter = true;

		for (String sCo : this.connectes) 
		{
			if (sCo != null && sCo.equals(s)) 
			{
				peutAjouter = false;
				break;
			}
		}

		if (!peutAjouter) return false;

		this.connectes.add(s);

		return true;
	}

	/**
	 * Supprime le pseudo de l'utilisateur qui se déconnecte 
	 * @param s pseudo de l'utilisateur
	 */
	public void deconnecterUtilisateur(String s) { this.connectes.remove(s); }
}