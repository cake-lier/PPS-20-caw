# Implementazione

## Elena Rughi

## Lorenzo Gardini

### Model
Nella prima settimana mi sono dedicato all'implementazione delle classi che compongono gli elementi di primo livello del dominio del gioco(Cell, Board, Level, etc.) e la struttura del GameModel, dopodiché ho implementato la persistenza su file dei livelli tramite appositi serializzatore e deserializzatore.

Successivamente, mi sono occupato di alcuni predicati in Prolog e di come integrarlo con Scala tramite PrologEngine e RulesEngine.

Infine, mi sono dedicato alla parte di implementazione dell’EditorModel.

### Controller 
Ho realizzato i Controller per la gestione dell’Editor, quindi l’EditorMenuController e l’EditorController


### View
Per il lato View ho realizzato, utilizzando il file fxml scritto da Matteo, la schermata delle impostazioni del gioco e la classe AudioPlayer che permette di inserire nel gioco musica ed effetti sonori.

Ho gestito la parte grafica dell’editor, compreso il menu introduttivo.
Io e Yuqi abbiamo esteso la BoardView (già scritta da lei) in modo da poter essere utilizzata anche dall’Editor.

## Matteo Castellucci

## Yuqi Sun

Nella prima settimana ho lavorato principalmente sulle classi di *View* per fornire, già dopo il primo sprint, un'applicazione concreta con cui l'utente poteva interagire. Usando le librerie di *ScalaFX* e *JavaFX*, ho dunque scritto le classi:
- *MainMenuView*: visualizza il menù principale dell'applicazione;
- *GameView*: visualizza il gioco; contiene una *BoardView*;
- *BoardView*: visualizza un livello, dove l'utente può fare *drag-and-drop* delle cellule di gioco;

*MainMenuView* e *GameView* sono state successivamente integrate con i metodi di *Controller* da Castellucci e Rughi.

In seguito, all'inserimento dell'editor, in collaborazione con Gardini, la classe *BoardView* è stata rifattorizzata in una classe astratta che racchiude i metodi comuni necessari per disegnare una generica *board*. Sono state poi create le due classi concrete *GameBoardView* e *EditorBoardView*.

### ViewComponent
*ViewComponent* è un trait generico che fa da wrapper a un componente di JavaFX e racchiude la logica necessaria per instanziarlo. Il componente di JavaFX è facilmente accessibile dalle classi concrete grazie a una conversione implicita.

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

### Prolog
Ho implementato le clausole *generator_right_next_state* e *generate_right*.
