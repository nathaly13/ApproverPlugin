# **Funzionamento progetto SE**
Questo progetto fa uso delle API di APPROVER, GITHUB e JENKINS usando Unirest per le request.

## **Requisiti:**

* **Installare ngrok**
	Una volta installato bisogna farlo partire da riga di comando come indicato di seguito: ngrok http 9090
	(lavora sulla porta 9090). 
	Questo servizio, effettua un tunnel sicuro dell’indirizzo locale (localhost), anche se la propria rete è dietro un proxy 		oppure un firewall, da notare che ogni volta che si desidera usare l’app bisogna cambiare l’url del punto successivo  “`Webhook URL`” in quanto l'indirizzo cambia ogni volta che si richiama ngrok.

* **Creare un’app github:**:
	* Sul menù a discesa del proprio profilo andare su `Settings`
	* Click su `Developer settings`
	* Click su `GitHub Apps`
	* Dare un nome che si Desidera su `GitHub App name`
	* Su `Homepage URL` scrivere: https://example.com
	* Lasciare tutto invariato fino al punto `Webhook URL` e inserire l’url che fornisce ngrok (forwarding il primo url che 		fornisce ngrok) seguito da /github-webhook, esempio : http://0b7250db.ngrok.io/github-webhook.
	* Nella sezione  `Permissions` su Repository permissions cambiare:
		* Contents -> Read & write
		* Issues ->  Read & write
		* Tutti gli altri devono rimanere in “no access”
	* Nella sezione `Subscribe to events`:
		* Selezionare solo la casella “Push” tutti gli altri rimangono vuoti.
	* Scegliere chi può installare l'app

* **Installare l'app github:**
Installa l'app github appena creata:
	* Andare su `Install App`
	* Click su Install per il proprietario dell'account github in uso
	* Selezionare il/i repository su cui si vuole installare l'app
	* Click su install
		
* **Creare token github:**
Per poter usare le api di github per un repository privato bisogna creare un token personale in questo modo non saranno 	richieste le credenziali:
 	* Andare sul seguente link: https://github.com/settings/tokens 
	* click su `Generate new token`
	* inserire `token` (o qualsiasi altra parola ) nella casella "Note"
	* selezionare le caselle "repo"  e "read:packages" in questo modo possiamo avere le autorizzazioni per creare label, 		creare issue, scaricare files dal repository anche se privato.
	* generate token
	* copiare e incollare il token nel progetto all'interno della classe `WebhookController.java` assegnandolo alla variabile 		`tokenGithub`, esempio String tokenGithub = "856faba3f5af96daf3dcacbde17fcac081ae1038"

	**NOTA:** 
	* l'ultimo passo è da fare subito perchè successivamente questo token non sarà più visibile
	* se si desidera visualizzare i risultati che restituisce github ad ogni webhook (evento) copiare e incollare su un 			qualisasi browser l’url che fornisce ngrok come “Web Interface”, esempio: http://127.0.0.1:4040

* **Installare Jenkins:**
I passi per installare jenkins sono disponibili nella documentazione all'interno delle cartelle `docs/documentazioni` con nome del file: "documentazioneJenkins.docx"

* **Installare MySQL:**
I passi per installare MySQL sono disponibili nella documentazione all'interno delle cartelle `docs/documentazioni` con nome del file: "documentazioneMySQL.docx" .

## **Funzionamento:**
	* Andare sulla classe `DemoApplication.java` e fare eseguire il progetto (run), attendere finchè non si visualizza la 			stampa 	di spring.
	* Andare nella repository su cui si è installata l’applicazione creata.
	* Caricare i files che formano parte di un progetto android su questa repository (ad esempio: può essere caricato da riga 		di comando o da gitHub Desktop)
	* Il tutto dovrà eseguirsi in modo automatico in caso non si presentino errori, altrimenti in caso di errori il nostro 			progetto stampa a video l’eventuale errore.
	* Su jenkins si può vedere l'eventuale completamento del job e se è finito con successo o se è fallito.
	* Ad analisi completata, con una build da parte di jenkins con successo, si potranno vedere su github gli issues 			creati/chiusi/riaperti in automatico con le rispettive label date dalle analisi di approver cosi come la descrizione di 		essi.
	

