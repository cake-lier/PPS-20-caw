# Processo

Abbiamo adottato un processo di sviluppo SCRUM-inspired: tale framework di sviluppo è adatto a team di piccola dimensione e favorisce lo sviluppo di software che potrebbe presentare difficoltà inattese grazie al suo processo incrementale. Ciò è stato particolarmente utile nella realizzazione della nostra applicazione, poichè era il nostro primo progetto consistente in Scala.<br>

Il ruolo di Product Owner è stato ricoperto da Matteo Castellucci, che ha condotto i daily scrum, aggiornato i backlog degli sprint settimanali e prodotto un report alla fine di ogni sprint.

## Meetings

Il processo SCRUM si focalizza su collaborazione e teamwork: meeting giornalieri e settimanali incoraggiano discussione in itinere e favoriscono una profonda comprensione di ogni parte dell'applicazione da parte di ogni membro.<br>
Abbiamo svolto meeting giornalieri (*daily scrum*) e settimanali (incorporando *sprint review* e *sprint planning*) tramite Discord.

### Daily scrum

Durante i meeting giornalieri ogni membro, a turno, ha esposto:
- il codice che ha prodotto in quel giorno, motivando le scelte implementative
- come intende proseguire
- eventuali dubbi riguardo le scelte implementative o di integrazione con componenti sviluppate dagli altri membri

Tipicamente ogni membro ha impiegato dai 5 ai 15 minuti per esporre il proprio lavoro, a seconda della complessità e eventuali discussioni.

### Sprint review e sprint planning

Abbiamo svolto 5 sprint della durata di una settimana l'uno.<br>
Ogni martedì in seguito al daily scrum abbiamo esteso il meeting giornaliero per svolgere la review dello sprint appena concluso e per pianificare lo sprint successivo.<br>

Lo *sprint review* consiste in:
- analizzare il livello di completamento dello sprint backlog;
- individuare goal più complessi o più semplici del previsto, per ridistribuire il carico di lavoro nello sprint successivo;
- valutazione complessiva del completamento del progetto;

Lo *sprint planning* consiste in:
- decidere i main goal dello sprint (ad esempio struttura di base, rifattorizzazione etc);
- compilare un nuovo *product backlog* per realizzare i goal;

## Suddivisione dei task
Gli item che costituiscono ogni *product backlog* sono stati individuati suddividendo i main goal in sub-goals, e assegnando task autocontenuti e di piccole dimensioni per realizzare tali goal. L'assegnazione dei task ai membri del team era consentita sia all'inizio degli sprint che durante, per uno sviluppo più flessibile.

## Tools

- ScalaTest e TestFx per testare il codice.
- JaCoCo per analizzare la code coverage della nostra suite di test.
- Scalafmt per formattare in modo uniforme il codice.
- Trello come kanban board per tenere traccia del *product backlog* e del completamento dei task per ogni sprint.

### Repository GitHub
Per facilitare lo sviluppo in parallelo dei task assegnati ad ogni membro del team abbiamo organizzato il workflow su Git nel seguente modo: 
- il branch *main* è stato dedicato alle versioni principali dell'applicazione, coincidenti con ogni sprint, e alla documentazione del processo di sviluppo;
- il branch *develop* è stato usato per aggregare man mano le varie feature sviluppate;
- ogni feature ha avuto un proprio branch su cui hanno lavorato al massimo due persone contemporaneamente;

Qui di seguito le GitHub Actions adoperate per automatizzare il workflow:
- *Release* per sistemi Linux, MacOS e Windows in seguito alla creazione di un tag;
- esecuzione dei test ad ogni push su tutti i branch, eccetto main;
- report di JaCoCo (code coverage) ad ogni push sul branch develop;
- generazione del presente report come pdf ad ogni push sul branch main nella cartella report;
