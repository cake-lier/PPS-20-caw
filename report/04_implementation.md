# Implementazione

## Elena Rughi

### Selezione dei livelli

Durante il primo sprint ho realizzato la componente grafica per la selezione dei livelli, collaborando con Sun per integrare la schermata di selezione con il menu del gioco. Coerentemente con le altre componenti grafiche dell'applicazione, "LevelSelectionView" è un "ViewComponent", ovvero un *wrapper* realizzato da Sun, che viene istanziato con il proprio file in formato FXML. Il trait "LevelSelectionController" corrispondente è implementato nel "MainMenuController".

Ogni bottone associato ad un livello nella schermata di selezione è rappresentato da una propria "ViewComponent" chiamata "LevelButton" per consentire di mostrare un numero variabile di livelli, oltre che per far iniziare il gioco con il livello selezionato. Dopo aver aggiunto "Settings", nel quale si memorizzano i livelli completati, ho modificato "LevelButton" in modo tale che i livelli completati siano evidenziati.

Il caricamento dei livelli da file e il salvataggio dei livelli creati dall'utente sono realizzati in "LevelStorage".

### GameController

Alcuni metodi di "GameController" sono degni di nota per tutta o parte dell'implementazione che hanno ricevuto:

- "startUpdates" - permette di avviare l'esecuzione automatica degli "*step*" della simulazione. Ho realizzato tale comportamento sfruttando uno "ScheduledExecutorService" che esegue ogni secondo uno "*step*" della simulazione su un proprio *thread*. Questo perché non è possibile pensare di eseguire tramite un semplice *loop* questa operazione senza poi bloccare il flusso di controllo del *thread* della GUI, eventualità da evitare ad ogni costo. Tale esecuzione periodica dello "*step*" viene incapsulata in una "ScheduledFuture" che ne consente la cancellazione.
- "pauseUpdates" - permette di sospendere l'esecuzione periodica degli "*step*" della simulazione. Affinché questo fosse possibile, ho sfruttato la "ScheduledFuture" precedentemente salvata grazie al metodo "startUpdates" cancellandone l'esecuzione associata.
- "nextLevel" - permette di passare al prossimo livello. Prima di cambiare livello, nel caso non fosse già stato fatto, viene fermata l'esecuzione periodica degli "*step*" della simulazione cancellando la *future* che rappresenta l'esecuzione stessa.
- "resetLevel" - permette di effettuare il "*reset*" del livello corrente. Nel caso in cui questo metodo venisse chiamato mentre l'esecuzione periodica degli "*step*" è in corso, esso, come il precedente, prima di effettuare il *reset*, cancella la *future* associata.
- "closeGame" - permette di chiudere il gioco. All'uscita dal gioco lo "ScheduledExecutorService" viene definitivamente terminato, dato che non verrà più utilizzato.

### Settings

Durante il terzo *sprint* ho realizzato il salvataggio su file delle impostazioni contenenti il volume della musica, il volume degli effetti sonori e i livelli completati, così da poter mantenere tali informazioni attraverso le diverse sessioni di utilizzo dell'applicazione. Poiché ho deciso di realizzare il file contenente le impostazioni in formato JSON, per semplificare la conversione da un oggetto di tipo "Settings" ad un valore JSON della libreria "Play JSON", ho aggiunto un "*implicit value*" al *companion object* del *trait* "Settings" che permette di convertirlo nel formato richiesto dalla libreria.

I servizi per effettuare l'aggiornamento delle impostazioni hanno la loro implementazione in "ApplicationController", ovvero il controller di primo livello della nostra applicazione. Il salvataggio delle stesse avviene ogni volta che si esce dalla schermata delle impostazioni nel menu di gioco oppure al completamento di un livello.

"SettingsStorage" fornisce le funzionalità di caricamento e di salvataggio delle impostazioni. Ogni chiamata al suo metodo "save" è eseguita tramite una "Future" che viene mantenuta in un *set* finché non completa; in tal modo si relega la costosa operazione di scrittura su disco ad una computazione asincrona. Ogni volta che una "Future" è completata, essa si rimuove da sola dal *set* in cui è stata inserita, che deve perciò essere un "ConcurrentHashSet", cioè un *set* che supporta operazioni concorrenti. Alla chiusura dell'applicazione, prima che questa effettivamente avvenga, si attende il completamento di tutte le operazioni di salvataggio delle impostazioni aspettando che il *set* delle "Future" sia vuoto, il che giustifica l'utilizzo della struttura dati.

### Logica di gioco

Ho contribuito alla realizzazione del metodo "update" in "RulesEngine" e ho implementato la classe "UpdateCell", la quale contiene degli *extension method*.

In PROLOG ho realizzato i predicati "last_index_left", "move_left", "mover_left_next_state", "rotate_counterclockwise" e ho modificato il comportamento dei predicati "generator_right", "generator_left", "generator_top" e "generator_bottom" per impedire la generazione delle cellule "Enemy".

### Testing

Ho scritto i test per le seguenti classi:

- "FileStorage", "SettingsStorage" e "LevelStorage";
- "BaseCell", "PlayableCell" e "UpdateCell";
- "Level", "LevelBuilder" e "Board".

## Lorenzo Gardini

### GameModel

Nel mio primo *sprint* ho sviluppato le entità del modello del dominio del gioco. Ho utilizzato uno *union type* per realizzare quella che è stata poi denominata "BaseCell", cioè la classe che definisce l'entità atomica del gioco, dopodiché ho implementato "Board", "Level" e "PlayableArea". Infine, ho realizzato "GameModel" seguendo il "*dependency inversion principle*" in modo tale che la classe accettasse "RulesEngine" nel suo costruttore, così da renderla indipendente dall'implementazione di quest'ultima. Inoltre, il "GameModel" è immutabile ed ogni modifica effettuata su di esso ha come risultato la creazione di una nuova istanza.

### Persistenza dei livelli

Dopo "GameModel", mi sono dedicato ad implementare "LevelParser", così da avere sin da subito la possibilità di caricare i livelli da file. La componente che fa "de-serializzazione" utilizza uno *schema*, scritto da Castellucci, che definisce se un file in formato JSON che contiene un livello è corretto oppure no. Successivamente, durante lo sviluppo di "EditorController", ho aggiunto al *parser* la possibilità di serializzare i livelli creati dall'utente tramite l'*editor* di livelli. In "LevelParser" viene fatto uso della monade "Try".

### PROLOG

Mi sono dedicato alla realizzazione dei predicati "rotator_counterclockwise_next_state", "generate_left", "generator_left_next_state", "generate_top", "generator_top_next_state", "generate_down", "generator_down_next_state", "last_index_top", "move_top", "mover_top_next_state", "last_index_bottom", "move_bottom" e "mover_bottom_next_state".

Ho realizzato l'integrazione tra scala e PROLOG creando "PrologEngine", una *façade* per la librearia "tuProlog", utilizzando *type aliasing* per i concetti di *Goal* e di *Clause*. Questa classe viene poi usata solamente da "RulesEngine", nascondendone l'implementazione. In quest'ultima classe viene utilizzata "UpdateCell" per tenere conto della progressione dell'aggiornamento durante uno "*step*" della simulazione. L'aggiornamento dello stato della griglia viene effettuato in stile funzionale attraverso un metodo che utilizza la *tail recursion*.

### GameEditor

L'*editor* di livelli è la componente che mi ha impegnato durante gli ultimi *sprint*. Mi sono dedicato al suo sviluppo nella sua interezza e, tra gli altri compiti, ho modificato ed ampliato alcuni file FXML scritti da Castellucci e ho rifattorizzato "BoardView" insieme a Sun in modo tale che quest'ultima classe potesse essere utilizzata anche dall'*editor*, rispettando il principio DRY. Anche mentre ho sviluppato "EditorModel" ho fatto in modo la classe fosse immutabile, così che ogni sua modifica comporti la creazione di una nuova istanza.

### Settings

Ho aggiunto le impostazioni per modificare, in modo _non persistente_, il volume della musica e il volume degli effetti sonori.

### Audio

Mi sono occupato del suono nel gioco realizzando "AudioPlayer", la quale permette di riprodurre musiche e suoni, specificati grazie all'enumerazione "Track". Ogni elemento di "Track" è caratterizzato da una tipologia di suono, i cui valori sono definiti dall'enumerazione "AudioType".

### Testing

Ho realizzato i seguenti test:

- Per la classe "RulesEngine": "PrologEngineTest", "ResultTest" e "RulesEngineTest";
- Per la classe "GameModel": "GameModelTest" e "GameStateTest";
- Per la classe "EditorView": EditorViewTest;
- Per la classe "EditorModel": LevelEditorModelTest;
- Per la classe "LevelParser": "LevelParserTest".

## Matteo Castellucci

### Domain specific language

Il mio sviluppo è partito dall'implementazione del *Domain Specific Language* per la creazione di nuovi livelli. Durante il primo *sprint* mi sono occupato di realizzare il design del linguaggio, specificandone il lessico e la sintassi. Dopodiché, sono passato ad implementare tutto ciò che poteva servire per farlo funzionare. Ho implementato un modello del dominio apposito, una serie di classi che rappresentano le parole che fanno parte del linguaggio e un *object* radice che contiene tutto ciò che può servire per utilizzare il DSL. Per fare questo mi sono servito sia degli strumenti messi a disposizione dalla versione 3 del linguaggio scala per la costruzione di DSL, cioè ho utilizzato le nuove "context functions", nonché parametri e valori impliciti, già disponibili a partire da scala 2. Inoltre, mi sono avvalso di alcune caratteristiche tipiche dei linguaggi funzionali come le *higher order functions* e il *currying*.

Infine, mi sono dedicato a realizzare un componente che faccia da "validatore" per le informazioni che l'utente inserisce tramite il DSL, in maniera tale che non sia capace di costruire livelli non validi e, nel caso accada, venga bloccato con degli errori quanto più possibile eloquenti. Per fare in modo di non avere un validatore "fail-fast", cioè che si blocca al primo errore, ma uno che cerca di catturarne quanti più possibile, mi sono avvalso di un costrutto funzionale apposito che prende il nome di "validated", che è uno specifico tipo di funtore applicativo.

Durante la terza settimana sono tornato sul DSL per poter fare in modo che fosse possibile lanciare sia il gioco che l'*editor* di livelli direttamente dal DSL con il livello che l'utente ha appena creato tramite il DSL stesso. Per fare questo è stato necessario che l'architettura dell'applicazione fosse estremamente modulare, in maniera tale che potesse essere "scomposta" nei suoi componenti di base senza particolari problemi. Questo è stato quello di cui mi sono occupato nel secondo *sprint*.

Ho realizzato inoltre tutti i test per verificare il corretto funzionamento del DSL.

### Componente "Application"

Per poter avere un'applicazione totalmente modulare, è stato necessario progettarla come tale. Questo ha voluto dire, già nel primo *sprint*, introdurre un componente gerarchicamente superiore agli altri che fornisse loro i servizi di cui necessitavano, in maniera tale che ciascun componente potesse adattarsi ai diversi contesti in cui può trovarsi, ad esempio all'interno dell'applicazione oppure da solo, perché lanciato direttamente dal DSL. Il componente ha preso il nome di "Application", perché rappresenta l'applicazione stessa.

Dal secondo *sprint* invece mi sono occupato di integrare il componente "Game" realizzato da Rughi e Sun con l'architettura gerarchica definita in precedenza. Questo ha voluto dire sia riallineare le implementazioni fatte, sia estendere le capacità del componente dedicato al gioco perché si potessero giocare i livelli creati dall'utente. In questo modo, al termine del secondo *sprint*, non solo esistevano il DSL funzionante e un'applicazione capace di mostrarsi al giocatore, ma era possibile anche giocare al gioco realizzato.

### PROLOG

Durante il secondo *sprint* mi sono anche occupato di realizzare parte del codice PROLOG che era necessario alla logica di gioco per funzionare. In particolare, mi sono dedicato alla realizzazione dei predicati "last_index_right", "move_right", "mover_right_next_state", "rotate_right" e "rotate_right_next_state". Inoltre, nel momento in cui, nel quinto *sprint*, mi sono dedicato ad introdurre la cellula "deleter" nel gioco, ho realizzato anche il predicato "drop_first", nonché tutta una serie di micro-modifiche perché il codice PROLOG esibisse un comportamento corretto anche con il nuovo tipo di cellula introdotto.

### Stato del model di gioco

Nel terzo *sprint* mi sono occupato di fare *refactoring* della classe "GameModel" già realizzata da Gardini, in maniera tale che fosse fosse più flessibile nella gestione del suo stato. Ho introdotto l'entità "GameState" perché racchiudesse tutte le informazioni sullo stato del *model* che era importante mostrare all'esterno, ivi inclusi gli eventi di gioco. Questi si sono infatti resi fondamentali nel momento nel quale sono stati introdotti i suoni che il gioco doveva riprodurre in corrispondenza degli eventi stessi.

### Modello del dominio

Una volta completato lo sviluppo dell'*editor* di livelli da parte di Gardini, ci si è resi conto che non era più utile avere un modello separato per ciascuna delle componenti dell'applicazione, dacché tutti i modelli erano essenzialmente molto simili e la "*separation of concerns*" che l'esistenza di ciascuno di essi garantiva non giustificava a sufficienza la ridondanza di codice presente. Nel quarto *sprint* mi sono perciò occupato di unificare tutte e tre le implementazioni del modello del dominio con quello realizzato per primo da Gardini nel componente "Game". Se per il componente "Editor" questo passaggio è stato fatto in maniera abbastanza semplice, nel DSL è stato invece necessario un ulteriore *refactoring* perché conteneva un'implementazione del modello leggermente semplificata rispetto a quella del gioco vero e proprio. Mentre facevo questo, ho anche modificato la gerarchia dell'entità "Cell" per averla così come descritta nel capitolo precedente.

### Deleter cell

Ultimo aspetto implementativo a cui mi sono dedicato, come accennato già in precedenza, è l'aggiunta della cellula "deleter" al gioco. Dopo avere modificato il codice PROLOG, mi sono dedicato ad aggiungere "DeleterCell" a tutte le sotto-gerarchie di "Cell" che ne necessitavano, nonché agli elementi che facevano de-serializzazione del livello. A questo punto è bastato aggiungere il supporto per questa "Cell" nelle *view* dei componenti "Game" ed "Editor" e il supporto a livello di applicazione è stato così garantito. L'ultimo passo è stato quello di aggiungere nel DSL il supporto per "DeleterCell" sia a livello di linguaggio che a livello di serializzazione.

Mi sono infine dedicato allo sviluppo dei test per la *view* delle impostazioni.

## Yuqi Sun

Nella prima settimana ho lavorato principalmente sulle classi di *view* per fornire, già dopo il primo *sprint*, un'applicazione concreta con cui l'utente potesse interagire. Usando le librerie "ScalaFX" e "JavaFX", ho realizzato le classi:

- "MainMenuView", che visualizza il menu principale dell'applicazione;
- "GameView", che visualizza il gioco e contiene un'istanza di "BoardView";
- "BoardView", che visualizza una griglia dove l'utente può fare "*drag and drop*" delle cellule di gioco.

Tutte e tre le classi estendono da "AbstractViewComponent" e hanno un proprio file FXML, contenente la loro struttura e stile. "MainMenuView" e "GameView" sono state successivamente integrate con i componenti di "*controller*" da Castellucci e Rughi.

In seguito alla creazione dell'*editor*, in collaborazione con Gardini, la classe "BoardView" è stata rifattorizzata in una classe astratta che racchiude i metodi comuni necessari per disegnare una generica griglia di un livello. Sono state poi create le due classi concrete "GameBoardView" ed "EditorBoardView".

Infine, ho implementato le classi di test riguardanti la "view" del menu, della selezione dei livelli e del gioco.

### ViewComponent

"ViewComponent" è un *trait* generico che fa da *wrapper* ad un componente di JavaFX. Con la classe astratta "AbstractViewComponent" che estende questo *trait*, è possibile creare componenti di "*view*" modulari senza specificare ogni volta come inizializzarli, in quanto tutta la logica necessaria per istanziarli è già incapsulata all'interno di "AbstractViewComponent".

Il componente di JavaFX interno è facilmente accessibile dall'esterno grazie ad una conversione implicita.

### Disegno del livello

La classe astratta "BoardView" usa le classi "CellView" e "TileView" per disegnare il pavimento e le cellule del livello. In "CellView" ho usato uno *union type*, in quanto la classe deve essere in grado di gestire sia "BaseCell" sia "PlayableCell": in questo modo si ha un unico costruttore che, a seconda del tipo effettivo della cellula, istanzia una "BaseCellView" o una "PlayableCellView". Queste due classi sono l'implementazione concreta di una classe astratta "AbstractCellView", che rifattorizza il codice comune per disegnare una cellula.

"CellImage" è una *enumeration* che contiene tutte le possibili immagini usate per disegnare una cellula. Questa è stata realizzata perchè:

- è più pulito usare una *enum* invece del percorso del file per fare riferimento alle varie immagini;
- visto che ogni tipo di cellula, anche se in posizioni diverse, condivide la stessa immagine, una *enum* ci permette di riusare lo stesso oggetto "Image", applicando in questo modo il pattern "Flyweight".

### Interazione View-Utente

Ho implementato:

- il "*drag and drop*" delle cellule per il gioco e l'*editor*;
- i metodi necessari per aggiungere e rimuovere le cellule e per selezionare e deselezionare la *playable area* con il mouse per l'*editor*.

Il "*drag and drop*" è stato implementato grazie alle classi "DraggableImageView" e "DroppableImageView", che aggiungono gli *handler* necessari ad "ImageView" di JavaFX.

### Aggiornamento della View

Per l'aggiornamento della *view* dei componenti "Game" ed "Editor", il cui design è già stato descritto nel capitolo "Design", ho implementato:

- "ModelUpdater" e la relativa implementazione in "GameView";
- "EditorModelUpdater" e la relativa implementazione in "EditorView".

Gli effettivi metodi nei *controller* che modificano i *model* e che vengono chiamati da "ModelUpdater" ed "EditorModelUpdater" sono stati implementati dai miei colleghi.

### RulesEngine

Ho contribuito alla classe "RulesEngine" ottimizzandola. Il codice PROLOG, infatti, non è efficiente nel calcolare il prossimo stato del livello quando questo contiene un alto numero di cellule, causando *lag* evidenti al gioco. Per limitare questo problema, le regole scritte in PROLOG ricevono ora una griglia contenente solo le cellule appartenenti all'intorno di quella su cui si vuole applicare la regola. Dopo che PROLOG restituisce un risultato, questo viene usato per aggiornare lo stato del livello.

### PROLOG

Durante il secondo *sprint*, ho contribuito alla realizzazione delle regole di gioco in PROLOG. In particolare, ho implementato i predicati "generator_right_next_state" e "generate_right".

## Testing

Per le classi di *model*, di *storage* e per il DSL si è cercato di applicare un processo di "Test-Driven Development", anche se a volte è venuto meno, soprattutto in seguito a lunghe fasi di *refactoring* e *redesign*. Per le classi di *view* è stato applicato un approccio più tradizionale, verificandone il corretto funzionamento dopo la sua implementazione. Non sono stati effettuati test per le classi di *controller*.

Per i test sono stati usati gli seguenti strumenti:

- "ScalaTest": in particolare è stato utilizzato lo stile "FunSpec" per rendere i test più facilmente comprensibili;
- "TestFx" e "JUnit" per le classi di *view*: a causa di una difficile integrazione tra "TestFX" e "ScalaTest", la scelta è ricaduta su "JUnit" come piattaforma per l'esecuzione di questi test.

Come supporto al *testing*, è stata usata la tecnologia di *continuous integration* "Github Actions", una *feature* di "Github" che permette di automatizzare certi compiti durante lo sviluppo del software. Tramite un'apposita *pipeline*, dopo ogni *push* tutti i test sono stati eseguiti automaticamente su di una macchina virtuale, permettendoci di verificare che ogni modifica al codice non avesse introdotto regressioni e quindi non avesse minato il comportamento atteso dall'applicazione.

Per la coverage dei test è stata usata la libreria "JaCoCo", in quanto supporta la versione 3 del linguaggio scala.

### Model

Per le classi di *model* sono stati implementati i seguenti test:

- corretto istanziamento e corretta gestione dello stato di tutte le entità del modello del dominio;
- corretto aggiornamento dello stato e funzionamento dell'*editor*;
- corretto aggiornamento dello stato e funzionamento del gioco;
- corretto aggiornamento della griglia del livello.

### Storage

Delle classi inerenti agli *storage* è stato verificato che lettura, caricamento e salvataggio di file di risorse, di livello e di impostazioni avvenissero correttamente.

### View

Sono state testate tutte le principali *view* del gioco:

- Menu principale:
  - comportamento e stato atteso dei bottoni.
- Impostazioni:
  - comportamento e stato atteso degli *slider* per il volume;
  - persistenza dei valori degli *slider* dopo la chiusura dell'applicazione.
- Selezione dei livelli:
  - comportamento e stato dei bottoni;
  - persistenza dell'informazione sui livelli completati dopo la chiusura dell'applicazione.
- Gioco:
  - comportamento e stato dei bottoni prima e dopo le fasi di "*setup*", di "*play*" e dopo il "*reset*" del livello;
  - presenza e assenza di "*drag and drop*" delle cellule;
  - posizionamento delle cellule durante la simulazione.
- Editor:
  - comportamento e stato dei bottoni;
  - presenza e assenza di cellule e area di gioco dopo le azioni del giocatore;
  - "*drag and drop*" e rimozione con tasto destro di cellule e area di gioco;
  - rotazione delle cellule contenute nel *dispenser*.

### DSL

È stato testato che fossero corretti:

- lo stato della struttura dati per raccogliere le informazioni sulla costruzione del livello prima e dopo l'aggiunta delle dimensioni, dell'area di gioco e delle cellule e dopo l'applicazione del "*copy constructor*";
- la visualizzazione a riga di comando del livello utilizzando tutti i metodi messi a disposizione dal DSL stesso;
- la visualizzazione degli errori;
- il salvataggio del livello.
