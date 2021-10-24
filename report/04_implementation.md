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

### View
Nella prima settimana ho lavorato principalmente sulle classi di *View* per fornire, già dopo il primo sprint, un'applicazione concreta con cui l'utente poteva interagire. Ho dunque scritto le classi *MainMenuView* e *GameView*, che sono state successivamente integrate con i metodi di *Controller* da Castellucci e Rughi, e la classe *BoardView*, completa già della funzionalità di *drag-and-drop* delle cellule di gioco.

In seguito, all'inserimento dell'editor, in collaborazione con Gardini, la classe *BoardView* è stata rifattorizzata in una classe astratta che racchiude i metodi comuni necessari per disegnare una generica *board*; sono state poi create le due classi concrete *GameBoardView* e *EditorBoardView*.

Essendo la persona nel team che ha avuto più esperienza con ScalaFX, ho scritto i metodi necessari per aggiungere, spostare e rimuovere le cellule e per selezionare e deselezionare la *playable area* nella *board* dell'editor.

Altre classi di *View* implementate da me sono:
- *CellImage*
- *CellView*
- *DraggableImageView*
- *DroppableImageView*
- *TileView*
- *ViewComponent*

### Controller e Model
Ho contribuito alla classe *RulesEngine* aggiungendo le funzioni necessarie per calcolare la *board* parziale da dare in input a *Prolog* e aggiornare la *board* globale con il risultato parziale ritornato.

Avendo lavorato strettamente con l'interazione utente-gui, ho implementato nei *Controller* della *GameView* ed *EditorView* i metodi necessari per il corretto aggiornamento del *Model* a seguito di cambienti della *View*.
