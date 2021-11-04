# Requisiti

## Requisiti di business

Come progetto volevamo replicare un gioco che fosse realizzabile in 60-80 ore come da richiesto dai requisiti d'esame. Di conseguenza, per la scelta del gioco abbiamo suggerito i seguenti propositi:

 - Il gioco deve avere il giusto livello di complessità per dimostrare le conoscenze acquisite durante il corso e completarlo nel monte ore stabilito.
 - Se possibile, la logica del gioco deve essere descritta con regole esprimibili tramite logica di primo ordine.
 - La componente grafica deve essere semplice ma efficace; non deve distogliere da altri aspetti fondamentali quali l'architettura e l'implementazione.
- Se possible, deve avere un elemento di gioco sufficientemente semplice da essere descritto con un *Domain-Specific Language* (DSL), come la struttura del livello o la mappa di gioco.

Tra le tante proposte che ci sono venute in mente, la scelta è ricaduta sul puzzle game "Cell Machine" di Sam Hogan e sulla sua mod "Cell Machine Mystic Mod", in quanto soddisfano tutti i nostri requisiti: è un gioco con il giusto grado di complessità sia implementativa sia grafica; le sue regole di gioco si possono esprimere con il linguaggio Prolog e i suoi livelli possono essere descritti con un DSL.
 
L'idea del gioco è molto semplice: basato sul concetto di "cellular automaton", ogni livello è un "automaton" composto da una griglia bidimensionale che contiene varie cellule, ognuna dotata di stato e comportamento specifico; il livello si evolve e cambia di stato applicando le regole di gioco alle cellule contenute al suo interno. L'obiettivo del giocatore è spostare e riorganizzare le cellule in una disposizione che le renda capaci di eliminare quelle nemiche. Infatti, anche se le regole non sono complicate, le cellule, interagendo tra di loro, riescono a creare comportamenti più complessi, permettendo di risolvere livelli via via sempre più difficili.
 
Inoltre anche noi abbiamo voluto dare al giocatore non solo la possibilità di risolvere puzzle ma anche quella di creare nuovi livelli che possono essere condivisi e giocati da altri giocatori, una caratteristica che mancava nel gioco originale ma che è presente nella mod. Con questa aggiunta, speravamo di offrire un gioco intrattenente, coinvolgente e dall'alta rigiocabilità, in quanto si può essere giocatori ma anche creatori di livelli.

## Requisiti utente

Un utente ha le seguenti richieste che vuole realizzate nel prodotto finale.

### Giocare un livello
Il giocatore vuole che il livello sia il più identico possibile a quello di "Cell Machine", cioè:

 - avere tutte le cellule presenti nel gioco originale, inclusa la cellula *Deleter* aggiunta nella mod;
 - avere il comportamento delle cellule durante l'evoluzione del livello simile al gioco originale;

Durante il gioco del livello il giocatore vuole:

 - fare drag-and-drop delle cellule in posizioni libere entro un'area predefinita del livello;
 - visualizzare l'evoluzione del livello in due modalità distinte:
    - foreword automatico del livello;
    - single step del livello;
 - resettare il livello, portando le cellule nelle posizioni precedenti all'evoluzione;
 - avere la possibilità di ritornare al menu iniziale;
 - se esiste, avere la possibilità di passare immediatamente al livello successivo;

### Creare di un livello
Il giocatore vuole:

 - creare un livello tramite DSL, dove può:
    - definire l'area giocabile specificandone la dimensione e la posizione;
    - aggiungere le cellule specificandone le caratteristiche e la posizione;
    - salvare il livello creato;
    - visualizzare in console, giocare o modificare tramite editor il livello creato;
 - creare un livello tramite editor, dove può:
    - avviare l'editor in modalità "stand alone" attraverso DSL con un livello precedentemente creato;
    - gestire l'area giocabile, cioè:
       - definire un'area giocabile rettangolare mediante drag-and-drop sul livello;
       - rimuovere l'area giocabile con il tasto destro del mouse;
    - gestire le cellule, cioè:
       - fare drag-and-drop delle cellule, presenti attualmente nel livello, verso qualunque altra posizione libera;
       - rimuovere una cellula con il tasto destro del mouse;
    - resettare il livello, eliminando tutte le cellule e l'area giocabile;
    - gestire il dispenser di cellule che include:
      - fare drag&drop delle cellule dal dispenser alle posizioni libere del livello;
      - trasformare e rotare le cellule del dispenser per ottenere ogni possibile cellula;
    - salvare il livello corrente nella directory specificata dall'utente;
    - ritornare al menu o chiudere l'editor;

### Modificare le impostazioni
Il giocatore vuole avere musica di sottofondo ed effetti sonori. Dunque, nelle impostazioni vuole poter:

 - impostare il volume della musica;
 - impostare il volume degli effetti sonori;
 - ritornare al menu iniziale;

### Accedere al menu

Il giocatore vuole un menu che gli permette di:

 - accedere alla schermata dei livelli di default da cui scegliere il livello da giocare;
 - accedere al menu dell'editor che permette di:
    - aprire l'editor con un livello vuoto specificandone le dimensioni;
    - aprire l'editor scegliendo un livello precedentemente creato;
- scegliere e giocare a un livello precedentemente creato dal giocatore;
- accedere alla schermata delle impostazioni;
- chiudere il gioco.

![Requisiti utente catturati tramite diagramma dei casi d'uso UML](imgs/use_cases.png){ width=100% }

Inoltre, il giocatore non vuole che l'applicazione sia lenta o che abbia errori che bloccano il suo utilizzo.

## Requisiti funzionali

A partire dai requisiti utente, si elencano i seguenti requisiti funzionali:

1. Al suo avvio, l'applicazione deve mostrare un menu con le opzioni per permettere al giocatore di:
   - giocare un livello di default selezionandolo da una lista;
   - giocare un livello creato dall'utente caricandolo da file;
   - uscire dall'applicazione;
2. Il gioco deve avere le cellule di tipo *Mover*, *Generator*, *Block*, *Rotator*, *Enemy*, *Wall*.
3. Assieme all'applicazione, deve essere fornito un DSL con il quale il giocatore è in grado di creare un nuovo livello e salvarlo su file.
4. Quando si seleziona o si gioca un livello, l'applicazione deve permettere al giocatore di tornare indietro al menu.

Si elencano i seguenti requisiti opzionali:

1. L'applicazione deve permettere all'utente di creare un nuovo livello o modificare un livello già esistente attraverso un *level editor*.
   - Il menu deve avere un'opzione per aprire il *level editor*.
   - L'editor deve avere l'opzione di tornare indietro al menu.
2. Il gioco deve implementare la cellula di tipo *Deleter*.
3. L'applicazione deve avere musica ed effetti sonori.
   - Il menu deve avere un'opzione per accedere alle impostazioni dove si può modificare il volume della musica e degli effetti sonori.
   - Le impostazioni devono avere l'opzione di tornare indietro al menu.
4. Il gioco deve avere animazioni.

Di seguito, si presenta una descrizione dettagliata dei requisiti dell'applicazione.

### Gioco
1. Il giocatore deve avere la possibilità di giocare a un livello di default.
2. Un livello è una griglia bidimensionale di una certa dimensione al cui interno sono posizionate le cellule di gioco.
   - Ogni cellula deve avere una posizione univoca nella griglia e un determinato comportamento a seconda del suo tipo.
      - I tipi di cellule sono: *Mover*, *Generator*, *Block*, *Rotator*, *Enemy*, *Wall*, *Deleter*.
   - Di default un livello deve essere circondato da un perimetro di cellule *Wall*.
   - Il livello deve avere una sola *area di gioco*, ovvero un'area rettangolare del livello le cui cellule possono essere spostate.
      - Il giocatore può spostare le cellule dell'area di gioco in una posizione diversa ma sempre interna all'area di gioco.
      - Il giocatore non può spostare le cellule posizionate al di fuori dell'area di gioco.
      - L'area di gioco deve essere evidenziata per distinguerla dalla restante area del livello.
3. L'obiettivo del livello è eliminare le cellule *Enemy*.
   - Un livello si dice *completato* quando tutte le cellule *Enemy* sono state eliminate.
4. Il gioco deve prevedere due fasi distinte: *fase di setup* e *fase di gioco*.
   - Nella fase di setup, il giocatore riorganizza le cellule posizionate all'interno dell'area di gioco.
   - Decisa una certa disposizione delle cellule, il giocatore ha la possibilità di far partire la simulazione del livello, dando inizio alla fase di gioco.
   - Una *simulazione* è l'evolversi dello stato del livello secondo regole ben definite.
      - Cambiare di stato vuol dire applicare una sola volta le regole di gioco alle cellule, modificando lo stato delle cellule e di conseguenza lo stato del livello.
      - La simulazione termina quando tutte le cellule *Enemy* sono state eliminate o quando non è più applicabile nessuna regola.
   -  Durante la fase di gioco, le cellule non possono essere spostate dal giocatore.
3. Il giocatore deve avere la possibilità di:
   - visualizzare la simulazione in maniera continuativa o step-by-step;
   - mettere in pausa e riprendere la simulazione;
4. Il giocatore deve avere la possibilità di resettare il livello allo stato iniziale precedente alla fase di gioco.
   - Facendo reset, il gioco torna alla fase di setup.
5. Quando un livello è completato, deve essere mostrato al giocatore l'opzione per giocare il livello successivo.

### Cellule
1. Una cellula *Mover* o *Generator* possiede un orientamento.
   - L'orientamento può essere destra, sinistra, sopra o sotto.
   - Il fronte della cellula è la posizione adiacente indicata dall'orientamento.
   - Il dietro della cellula è la posizione adiacente opposta all'orientamente.
2. Una cellula *Mover* si muove di una posizione nella direzione data dal suo orientamento.
   - Muovendosi, può spostare le cellule di fronte ad essa se queste hanno uno spostamento compatibile con quella del *Mover*.
   - Non può spostare un *Generator* o un *Mover* se hanno un orientamento opposto.
   - Non può spostare un *Rotator* o un *Wall*.
   - Non può spostare un *Block* se questo non può essere spostato nello stesso orientamento del *Mover*.
   - Non si muove quando incontra cellule che non può spostare.
3. Una cellula *Generator* genera un'altra cellula nella direzione data dal suo orientamento.
   - Può generare se dietro al *Generator* è presente un'altra cellula.
   - La generazione è un processo composto da due passi:
      - la cellula generata viene posizionata di fronte al *Generator*;
      - eventuali cellule che erano presenti di fronte al *Generator* vengono spostate di una posizione;
   - Non può generare cellule *Enemy*.
   - Non può generare se la generazione incontra un *Mover* o un *Generator* con un orientamento opposto.
   - Non può generare se la generazione incontra un *Wall*.
   - Non può generare se la generazione incontra un *Block* che non può essere spostato nello stesso orientamento del *Generator*.
4. Una cellula *Block* può essere spinta di una posizione da altre cellule.
   - Lo spostamento può essere:
      - orrizontale;
      - verticale;
      - sia orrizontale sia verticale;
   - Non può essere spinta in una direzione diversa dal suo spostamento.
5. Una cellula *Rotator* ruota le cellule adiacenti ad essa, ovvero le cellule immediatamente sopra, sotto, a destra e a sinistra di essa.
   - Può ruotare le cellule in senso orario o antiorario.
   - La rotazione non ha effetto sul *Wall* o *Enemy*.
   - La rotazione non ha effetto sul *Block* che può spostarsi sia orrizontalmente sia verticalmente.
6. Una cellula *Enemy* è la cellula che deve essere eliminata dal giocatore.
   - Viene eliminato quando una cellula si muove o viene spostata verso di essa.
   - Oltre all'*Enemy*, viene eliminata anche la cellula che è stata spostata contro l'*Enemy*.
7. Una cellula *Wall* non si muove.
   - Non può essere spostata né da un *Generator* né da un *Mover*.
   - L'unico momento del gioco in cui si può muovere un *Wall* è durante la fase di setup quando questa è posizionata nell'area di gioco.
8. Una cellula *Deleter* è in grado di rimuovere le altre cellule.
   - Rimuove qualsiasi tipo di cellula quando:
      - la cellula viene spinta contro il *Deleter*;
      - il *Deleter* viene spinto contro la cellula;
   - Se generato da un *Generator*:
      - se è presente una cellula di fronte al *Generator* viene eliminata dal *Deleter* che ne occupa la sua posizione;
      - se è presente un *Deleter* di fronte al *Generator* viene eliminato dal nuovo *Deleter* che ne occupa la sua posizione;


![Diagramma del modello del dominio dell'applicazione catturato tramite diagramma delle classi UML](imgs/domain_classes.png){ width=100% }

### Regole di gioco
1. Le regole di gioco devono essere regole espresse tramite logica di primo ordine.
2. Le regole del gioco definiscono il comportamento delle cellule *Mover*, *Generator* e *Rotator*, in quanto sono le uniche cellule che cambiano attivamento lo stato del livello, in quanto si muovono oppure spostano, ruotano o generano altre cellule.
3. Le regole del gioco non definiscono il comportamento delle cellule *Block*, *Enemy*, *Wall* o *Deleter* in quanto non possono attivamente cambiare lo stato del livello finché una cellula non si sposta o viene spostata nella loro direzione.
4. Per cambiare lo stato del livello, si applicano le regole del gioco nel seguente ordine:
   - la regola del *Generator*: applicato ai *Generator*, effettua la generazione di una nuova cellula se le condizioni sono soddisfatte;
   - la regola del *Rotator*: applicato ai *Rotator*, ruota le cellule circostanti se queste sono ruotabili;
   - la regola del *Mover*: applicato ai *Mover*, sposta il *Mover* e le cellule di fronte se le condizioni sono soddisfatte;
5. Dato un gruppo di cellule dello stesso tipo, la precedenza di applicazione della corrispondente regola è data alla cellula che si trova più in alto e più a sinistra delle altre.

### DSL
1. Il DSL deve essere il più vicino possibile al linguaggio umano.
2. Il DSL deve permettere di:
   - creare un livello specificando la sua dimensione;
   - aggiungere l'area di gioco, specificando la sua dimensione e la posizione in cui deve essere collocata;
   - aggiungere una cellula a una certa posizione specificando
      - l'orientamento se è un *Mover* o un *Generator*;
      - la rotazione se è un *Rotator*;
      - lo spostamento se è un *Block*;
   - aggiungere un gruppo di uno stesso tipo di cellule collocandole in una determinata area, specificando
      - tutte le caratteristiche del tipo di cellula;
      - la dimensione dell'area e la posizione in cui deve essere collocata;
3. Il DSL deve permettere di:
   - salvare il livello su file JSON;
   - visualizzare il livello in forma testuale su console;
   - giocare il livello aprendo il gioco in modalità "stand alone";
   - modificare il livello aprendo l'editor in modalità "stand alone";
4. Il DSL deve dare errore se:
   - il livello non ha dimensione;
   - l'area di gioco è assente oppure occupa un'area maggiore del livello;
   - si usano dimensioni negative;
   - si usano posizioni dalle coordinate negative o al di fuori del livello;
   - due o più cellule hanno la stessa posizione;

### Editor
1. Dato un livello, l'editor permette il giocatore di modificarlo.
2. Deve avere un menu dove il giocatore sceglie se:
   - modificare un livello già esistente caricandolo da file;
   - creare un nuovo livello specificandone le dimensioni;
3. Di default, il livello dell'editor è circondato da un perimetro di cellule *Wall* che non può essere rimosso.
4. Deve essere presente un dispenser di tutti i tipi di cellule che il giocatore può aggiungere al livello.
   - Deve essere presente la possibilità di trasformare e ruotare le cellule del dispenser per ottenere ogni possibile cellula.
5. Il giocatore deve avere la possibilità di:
   - selezionare e deselezionare l'area di gioco del livello;
   - aggiungere, spostare e rimuovere qualunque tipo di cellula;
   - resettare il livello, rimuovendo l'area di gioco e le cellule;
   - salvare su file JSON il livello creato o modificato;

### Selezione dei livelli
1. L'applicazione deve fare persistenza dei livelli completati dal giocatore.
2. Quando l'applicazione mostra la lista dei livelli di default, deve evidenziare i livelli completati.
3. I livelli di default non devono essere bloccati quando tutti i livelli precedenti non sono stati completati.
   - L'applicazione deve permettere di giocare i livelli di default nell'ordine in cui preferisce il giocatore.

### Musica ed effetti sonori
1. L'applicazione deve fare persistenza del volume della musica e degli effetti sonori scelti dal giocatore.
2. Dovranno essere emmessi eventi sonori quando:
   - la simulazione del livello avanza di uno step;
   - una cellula *Enemy* viene eliminata;
   - il livello viene completato;

## Requisiti non funzionali

- L'applicazione deve essere cross-platform, eseguibile su Windows, MacOS e Linux.
- L'applicazione non deve interrompersi qualora si verifichi un errore; deve invece mostrare il messaggio di errore all'utente.
- L'interfaccia grafica deve essere reattiva.
- L'applicazione deve essere fluida, non presentare stuttering o freezing su una macchina con requisiti minimi di 6GB di RAM, processore i5-2400.

## Requisiti implementativi

- Implementazione in Scala, con compatibilità garantita sulla versione 3.0.2
- Applicazione del paradigma di programmazione funzionale.
- Implementazione della logica di gioco in Prolog, tramite la libreria TuProlog alla versione 3.3.0
- Applicazione del processo TDD ove possibile, utilizzando le librerie ScalaTest e TestFX per realizzare la suite di test.
