# Implementazione

## Elena Rughi

### Selezione dei livelli

Durante il primo sprint ho realizzato la componente grafica per la selezione dei livelli, collaborando con Sun per integrare la schermata di selezione con il menu del gioco. Coerentemente con le altre componenti grafiche dell'applicazione, "LevelSelectionView" è un "ViewComponent", ovvero un *wrapper* realizzato da Castellucci, che viene istanziato con il proprio file in formato FXML. Il trait "LevelSelectionController" corrispondente è implementato nel "MainMenuController".

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

"SettingsStorage" fornisce le funzionalità di caricamento e di salvataggio delle impostazioni. Ogni chiamata al suo metodo "save" è eseguita tramite una "Future" che viene mantenuta in un *set* finchè non completa; in tal modo si relega la costosa operazione di scrittura su disco ad una computazione asincrona. Ogni volta che una "Future" è completata, essa si rimuove da sola dal *set* in cui è stata inserita, che deve perciò essere un "ConcurrentHashSet", cioè un *set* che supporta operazioni concorrenti. Alla chiusura dell'applicazione, prima che questa effettivamente avvenga, si attende il completamento di tutte le operazioni di salvataggio delle impostazioni aspettando che il *set* delle "Future" sia vuoto, il che giustifica l'utilizzo della struttura dati.

### Logica di gioco

Ho contribuito alla realizzazione del metodo "update" in "RulesEngine" e ho implementato la classe "UpdateCell", la quale contiene degli *extension method*.

In PROLOG ho realizzato i predicati "last_index_left", "move_left", "mover_left_next_state", "rotate_counterclockwise" e ho modificato il comportamento dei predicati "generator_right", "generator_left", "generator_top" e "generator_bottom" per impedire la generazione delle cellule "Enemy".

## Lorenzo Gardini

### GameModel

Nel mio primo *sprint* ho sviluppato le entità del modello del dominio del gioco. Ho utilizzato uno *union type* denominato "BaseCell" per definire l'entità atomica del gioco, dopodiché ho implementato "Board", "Level" e "PlayableArea". Infine, ho realizzato "GameModel" seguendo il "*dependency inversion principle*" in modo tale che la classe accettasse "RulesEngine" nel suo costruttore, così da renderla indipendente dall'implementazione di quest'ultima. Inoltre, il "GameModel" è immutabile ed ogni modifica effettuata su di esso ha come risultato la creazione di una nuova istanza.

### Persistenza dei livelli

Dopo "GameModel", mi sono dedicato ad implementare "LevelParser", così da avere sin da subito la possibilità di caricare i livelli da file. La componente che fa "de-serializzazione" utilizza uno *schema*, scritto da Castellucci, che definisce se un file in formato JSON che contiene un livello è corretto oppure no. Successivamente, durante lo sviluppo di "EditorController", ho aggiunto al *parser* la possibilità di serializzare i livelli creati dall'utente tramite l'*editor* di livelli. In "LevelParser" viene fatto uso della monade "Try".

### PROLOG

Mi sono dedicato alla realizzazione dei predicati "rotator_counterclockwise_next_state", "generate_left", "generator_left_next_state", "generate_top", "generator_top_next_state", "generate_down", "generator_down_next_state", "last_index_top", "move_top", "mover_top_next_state", "last_index_bottom", "move_bottom" e "mover_bottom_next_state".

Ho realizzato l'integrazione tra scala e PROLOG realizzando "PrologEngine", una *façade* per la librearia "tuProlog", utilizzando *type aliasing* per i concetti di *Goal* e di *Clause*. Questa classe viene poi usata solamente da "RulesEngine", nascondendone l'implementazione. In quest'ultima classe viene utilizzata "UpdateCell" per tenere conto della progressione dell'aggiornamento in uno "*step*" della simulazione. L'aggiornamento dello stato della griglia viene effettuato in stile funzionale utilizzando un metodo che utilizza la *tail recursion*.

### GameEditor

L'*editor* di livelli è la componente che mi ha impegnato durante gli ultimi *sprint*. Mi sono dedicato al suo sviluppo nella sua interezza modificando ed ampliando alcuni file FXML scritti da Castellucci e rifattorizzando "BoardView" insieme a Sun in modo da poter essere utilizzata anche dall'*editor* rispettando il principio DRY. Anche mentre ho sviluppato "EditorModel" ho voluto garantire che fosse immutabile, così che ogni sua modifica comporti la creazione di una nuova istanza.

### Settings

Ho aggiunto le impostazioni per modificare, in modo _non persistente_, il volume della musica e il volume degli effetti sonori.

### Audio

Mi sono occupato del suono nel gioco realizzando "AudioPlayer", la quale permette di riprodurre musiche e suoni specificati grazie all'enumerazione "Track". Ogni elemento id "Track" è caratterizzato da un tipo di suono, i cui valori sono definiti dall'enumerazione "AudioType".

## Matteo Castellucci

### Domain specific language

Il mio sviluppo è partito dall'implementazione del *Domain Specific Language* per la creazione di nuovi livelli. Durante il primo *sprint* mi sono occupato di realizzare il design del linguaggio, specificandone il lessico e la grammatica. Dopodiché, sono passato ad implementare tutto ciò che poteva servire per farlo funzionare. Ho implementato un modello del dominio apposito, una serie di classi che rappresentano le parole che fanno parte del linguaggio e un *object* radice che contenesse tutto ciò che poteva servire per utilizzare il DSL. Per fare questo mi sono servito degli strumenti messi a disposizione dalla versione 3 del linguaggio scala per la costruzione di DSL: ho utilizzato le nuove "context functions", nonché parametri e valori impliciti già disponibili a partire da scala 2. Inoltre, mi sono avvalso di alcune caratteristiche tipiche dei linguaggi funzionali come le *higher order functions* e il *currying*.

Infine, mi sono dedicato a realizzare un componente che faccia da "validatore" per le informazioni che l'utente inserisce tramite il DSL, in maniera tale che non sia capace di costruire dei livelli non validi, ma, nel caso accada, venga bloccato con degli errori quanto più possibile eloquenti. Per fare in modo di non avere un validatore "fail-fast", cioè che si blocca al primo errore, ma cerca di catturarne quanti più possibile, mi sono avvalso di un costrutto funzionale apposito che prende il nome di "validated", che è uno specifico tipo di funtore applicativo.

Durante la terza settimana sono tornato sul DSL per poter fare in modo che fosse possibile lanciare sia il gioco che l'*editor* di livelli direttamente dal DSL con il livello che l'utente ha appena creato tramite il DSL stesso. Per fare questo è stato necessario che l'architettura dell'applicazione fosse estremamente modulare, in maniera tale che potesse essere "scomposta" nei suoi componenti di base senza particolari problemi. Questo è stato infatti quello di cui mi sono occupato nel secondo *sprint*.

### Componente "Application"

Per poter avere un'applicazione totalmente modulare, è stato necessario progettarla come tale. Questo ha voluto dire, già nel primo *sprint*, introdurre un componente gerarchicamente superiore agli altri che fornisse loro i servizi di cui necessitavano, in maniera che ciascun componente non contenesse già tutto quello di cui aveva bisogno e potesse adattarsi ai diversi contesti in cui può trovarsi, ad esempio all'interno dell'applicazione oppure da solo perché lanciato direttamente dal DSL. Il componente ha preso il nome di "Application", perché essa rappresenta.

Dal secondo *sprint* invece mi sono occupato di integrare il componente "Game" realizzato da Rughi e Sun con la struttura gerarchica definita in precedenza. Questo ha voluto dire sia riallineare le implementazioni fatte, sia estendere le capacità del componente dedicato al gioco perché si potessero giocare i livelli creati dall'utente. In questo modo, al termine della secondo *sprint* non solo esisteva il DSL funzionante e un'applicazione capace di mostrarsi al giocatore, ma era possibile anche giocare al gioco realizzato.

### PROLOG

Durante il secondo *sprint* mi sono anche occupato di realizzare parte del codice PROLOG che era necessario alla logica di gioco per funzionare. In particolare, mi sono dedicato alla realizzazione dei predicati "last_index_right", "move_right", "mover_right_next_state", "rotate_right" e "rotate_right_next_state". Inoltre, nel momento in cui, nel quinto *sprint*, mi sono dedicato ad introdurre la "deleter cell" nel gioco, ho realizzato anche il predicato "drop_first" nonché tutta una serie di micro-modifiche perché il codice PROLOG esibisse un comportamento corretto anche con il nuovo tipo di cellula introdotta.

### Stato del model di gioco

Nel terzo *sprint* mi sono occupato di rifattorizzare "GameModel" già realizzato da Gardini in maniera tale che fosse fosse più flessibile nella gestione del suo stato: ho introdotto l'entità "GameState" perché racchiudesse tutte le informazioni sullo stato del *model* che era importante mostrare all'esterno, ivi inclusi gli eventi di gioco. Questi si sono infatti resi fondamentali nel momento nel quale sono stati introdotti i suoni che il gioco doveva riprodurre in corrispondenza degli eventi stessi.

### Modello del dominio

Una volta completato lo sviluppo dell'*editor* di livelli da parte di Gardini, ci si è resi conto che non era più utile avere un modello separato per ciascuna delle componenti dell'applicazione, dacché tutti i modelli erano essenzialmente molto simili e la "*separation of concerns*" che l'esistenza di ciascuno di essi garantiva non giustificava a sufficienza la ridondanza di codice presente. Nel quarto *sprint* mi sono perciò occupato di unificare tutte e tre le implementazioni dei modelli del dominio con quello già realizzato per primo da Gardini nel componente "Game". Se per il componente "Editor" questo passaggio è stato fatto in maniera abbastanza semplice, nel DSL è stato invece necessario un ulteriore *refactoring* perché conteneva un'implementazione leggermente semplificata rispetto a quella del gioco vero e proprio. Mentre facevo questo, ho anche modificato la gerarchia dell'entità "Cell" per averla così come descritta nel capitolo precedente.

### Deleter cell

Ultimo aspetto implementativo a cui mi sono dedicato, come accennato già in precedenza, è l'aggiunta della cellula "deleter" al gioco. Dopo avere modificato il codice PROLOG, mi sono dedicato ad aggiungere "DeleterCell" a tutte le sotto-gerarchie di "Cell" che ne necessitavano, nonché agli elementi che facevano de-serializzazione del livello. A questo punto è bastato aggiungere il supporto per questa "Cell" nelle *view* dei componenti "Game" ed "Editor" e il supporto a livello di applicazione è stato garantito. L'ultimo passo è stato quello di aggiungere nel DSL il supporto per la "DeleterCell" sia a livello di linguaggio che a livello di controllo delle informazioni inserite e di serializzazione.

## Yuqi Sun

Nella prima settimana ho lavorato principalmente sulle classi di *View* per fornire, già dopo il primo sprint, un'applicazione concreta con cui l'utente poteva interagire. Usando le librerie di *ScalaFX* e *JavaFX*, ho realizzato le classi:

- *MainMenuView*: visualizza il menù principale dell'applicazione;
- *GameView*: visualizza il gioco; contiene una *BoardView*;
- *BoardView*: visualizza un livello, dove l'utente può fare *drag-and-drop* delle cellule di gioco;

Tutte e tre le classi estendono da *AbstractViewComponent* e hanno un proprio file fxml, contenente la loro struttura e stile. *MainMenuView* e *GameView* sono state successivamente integrate con i metodi di *Controller* da Castellucci e Rughi.

In seguito, all'inserimento dell'editor, in collaborazione con Gardini, la classe *BoardView* è stata rifattorizzata in una classe astratta che racchiude i metodi comuni necessari per disegnare un generico livello. Sono state poi create le due classi concrete *GameBoardView* e *EditorBoardView*.

Inoltre ho implementato le classi di test riguardanti la *View* del menu, della selezione dei livelli e del gioco.

### ViewComponent

*ViewComponent* è un trait generico che fa da wrapper a un componente di JavaFX. Con la class astratta *AbstractViewComponent*, che estende questo trait, si riesce a creare componenti di *view* modulari senza specificare ogni volta come inizializzarle, in quanto tutta la logica necessaria per instanziarle è già incapsulata all'interno di *AbstractViewComponent*.

Il componente di JavaFX è facilmente accessibile dalle classi concrete grazie a una conversione implicita.

### Disegno del livello

La classe astratta *BoardView* usa le classi *CellView* e *TileView* per disegnare il pavimento e le cellule del livello. In *CellView* ho usato un *union type*, in quanto deve essere in grado di gestire sia *BaseCell* sia *PlayableCell*: in questo modo si ha un unico costruttore che, a seconda del tipo effettivo della cellula, istanzia una *BaseCellView* o una *PlayableCellView*. Queste due classi sono l'implementazione concreta di una classe astratta *CellView*, che rifattorizza il codice comune per disegnare una cellula.

*CellImage* è un *enumeration* che contiene tutte le possibili immagini usate per disegnare un livello:

- è più pulito usare un *enum* invece del percorso per fare riferimento alle varie immagini;
- in quanto ogni tipo di cellula, anche se in posizioni diverse, condivide la stessa immagine, ci permette di riusare lo stesso oggetto *Image*, applicando in questo modo il pattern *Flyweight*;

### Interazione View-Utente

Ho implementato:

- il *drag-and-drop* delle cellule per il gioco e l'editor;
- i metodi necessari per aggiungere e rimuovere le cellule e per selezionare e deselezionare la *playable area* con il mouse per l'editor;

Il *drag-and-drop* è implementato con le classi *DraggableImageView* e *DroppableImageView*, che aggiungono gli handler necessari all'ImageView di JavaFX.

### Aggiornamento della View

Per l'aggiornamento della view, il cui design è già stato descritto nel capitolo di "Design", ho implementato:

- *ModelUpdater* e relativa implementazione in *GameView*;
- *EditorModelUpdater* e relativa implementazione in *EditorView*;

Gli effettivi metodi di *Controller* che modificano il *Model* e che vengono chiamati da *ModelUpdater* e *EditorModelUpdater* sono stati implementati dai miei compagni.

### RulesEngine

Ho contribuito alla classe *RulesEngine* aggiungendo un'ottimizzazione. Prolog, infatti, non è efficiente a calcolare il prossimo stato del livello quando questo contiene un alto numero di cellule, causando lag evidenti al gioco; per diminuire questo problema, le regole di Prolog ricevono una *board* contenente solo le cellule circostanti alla cellula su cui si vuole applicare la regola; dopo che Prolog ritorna un risultato, questo viene usato per aggiornare lo stato del livello.

### PROLOG

Durante il secondo *sprint*, ho contribuito alla realizzazione delle regole di gioco in PROLOG. In particolare, ho implementato le clausole *generator_right_next_state* e *generate_right*.

## Testing

Per le componenti di *Model*, *Storage* e *DSL* si è cercato di applicare un processo TDD, anche se a volte è venuto a meno, soprattutto in seguito a lunghe fasi di refactoring e redesign. Per la componente di *View* è stato applicato un approccio più tradizionale, verificandone il corretto funzionamento dopo la sua implementazione. Non sono stati effettuati test per le componenti di *Controller*.

Per i test sono stati usati gli seguenti strumenti:

- *ScalaTest*:
  - in particolare è stato utilizzato *FunSpec* per rendere i test più facilmente interpretabili;
- *TestFx* e *JUnit* per la componente di *View*:
  - a causa di una difficile integrazione tra *TestFX* e *ScalaTest*, la scelta è ricaduta su *JUnit*;

Come supporto al testing, sono state usate le *Github Actions*, una feature di Github che permette di automatizzare certi task durante lo sviluppo del software. Con queste *actions*, dopo ogni push tutti i test sono eseguiti automaticamente su una macchina virtuale, permettendoci di verificare che ogni modifica al codice non avesse compromesso i test e quindi il comportamento atteso dall'applicazione.

Per la coverage dei test è stato usato JaCoCo, in quanto supporta Scala 3.

### Model

Della componente *Model* sono implementati i seguenti test:

- corretto instanziamento e stato di tutte le entità di gioco:
  - *BaseCell* e *PlayableCell*;
  - *Dimensions* e *Position*;
  - *Level*, *PlayableArea* e *Board*;
- corretto funzionamento e aggiornamento del modello e stato dell'editor;
- corretto instanziamento, funzionamento e aggiornamento del modello e stato del gioco;
- corretto aggiornamento del livello e delle regole di Prolog;

### Storage

Della componente *Storage* è stato verificato che lettura, caricamento e salvataggio di file di risorse, di livello o di impostazioni avvenissero correttamente.

### View

Sono state testate tutte le principali *View* del gioco:

- Menù principale:
  - comportamento e stato atteso dei bottoni;
- Impostazioni:
  - comportamento e stato atteso degli slider per il volume;
  - persistenza del volume negli slider dopo la chiusura dell'applicazione;
- Selezione dei livelli:
  - comportamento e stato dei bottoni;
  - persistenza dei livelli completati dopo la chiusura dell'applicazione;
- Gioco:
  - comportamento e stato dei bottoni prima e dopo le fasi di setup, gioco e reset del livello;
  - presenza e assenza di drag-and-drop delle cellule;
  - posizionamento delle cellule durante la simulazione;
- Editor:
  - comportamento e stato dei bottoni;
  - presenza e assenza di cellule e area di gioco dopo azioni del giocatore;
  - drag-and-drop e rimozione con tasto destra di cellule e playable area;
  - rotazione del dispenser di cellule;

### DSL

È stato testato che fossero corretti:

- lo stato delle cellule alla loro creazione;
- lo stato del livello prima e dopo l'aggiunta delle dimensioni, della playable area e delle cellule e dopo la funzione di copia;
- la visualizzazione a riga di comando del livello;
- la visualizzazione degli errori;
- il salvataggio del livello;
