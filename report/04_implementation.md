# Implementazione

## Elena Rughi

## Lorenzo Gardini

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
