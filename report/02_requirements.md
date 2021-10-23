# Requisiti

## Requisiti di business

Come progetto volevamo replicare un gioco che fosse realizzabile in 60-80 ore come da richiesto dai requisiti d'esame. Di conseguenza, per la scelta del gioco abbiamo suggerito i seguenti propositi:
- Il gioco deve avere il giusto livello di complessità per dimostrare le conoscenze acquisite durante il corso e completarlo nel monte ore stabilito.
- Se possibile, la logica del gioco deve essere descritta con regole esprimibili tramite logica di primo ordine.
- La componente grafica deve essere semplice ma efficace; non deve distogliere da altri aspetti fondamentali quali l'architettura e l'implementazione.
- Se possible, deve avere un elemento di gioco sufficientemente semplice da essere descritto con un Domain-Specific Language (DSL), come la struttura del livello o la mappa di gioco.

Tra le tante proposte che ci sono venute in mente, la scelta è ricaduta sul puzzle game "Cell Machine" di Sam Hogan e sulla sua mod "Cell Machine Mystic Mod", in quanto soddisfano tutti i nostri requisiti.
 
L'idea del gioco è molto semplice: basato sul concetto di "cellular automaton", ogni livello è composto da una griglia bidimensionale di cellule, dotate di uno stato, capace di evolvere nel tempo sulla base di regole predefinite; usando il mouse, il giocatore deve spostare e riorganizzare le cellule a sua disposizione per eliminare quelle nemiche. Anche se le regole sono molto facili, le cellule, interagendo tra di loro, riescono a creare comportamenti più complessi, permettendo di risolvere livelli via via sempre più difficili. 
 
Inoltre anche noi abbiamo voluto dare al giocatore non solo la possibilità di risolvere puzzle ma anche quella di creare nuovi livelli che possono essere condivisi e giocabili da altri giocatori, una caratteristica che mancava nel gioco originale ma che è presenta nella mod. Con questa aggiunta, speravamo di offrire un gioco intrattenente, coinvolgente e dall'alta rigiocabilità, in quanto si può essere giocatori ma anche creatori di livelli.

## Requisiti utente

![Requisiti utente catturati tramite diagramma dei casi d'uso UML](imgs/use_cases.jpg)

## Requisiti funzionali

A partire dai requisiti utente, si elencano i seguenti requisiti funzionali.

### Applicazione
1. Al suo avvio, l'applicazione deve mostrare un menù con le opzioni per permettere al giocatore di:
   - giocare un livello di default selezionandolo da una lista;
   - giocare un livello creato dall'utente caricandolo da file;
   - uscire dall'applicazione;
2. Assieme all'applicazione, deve essere fornito un DSL con il quale il giocatore è in grado di creare un nuovo livello e salvarlo su file.

### Gioco
1. Il gioco deve essere composto da livelli.
2. Un livello è una griglia bidimensionale di una certa dimensione al cui interno sono posizionate le cellule di gioco.
    - Ogni cellula deve avere una posizione univoca nella griglia e un determinato comportamento.
       - I tipi di cellule sono: *Mover*, *Generator*, *Block*, *Rotator*, *Enemy*, *Wall*.
   - Il giocatore può manipolare solo le cellule posizionate in una determinata area, facendo drag-and-drop delle cellule.
   - Il livello si evolve di stato in stato secondo regole ben definite.
3. Il giocatore deve avere la possibilità di visualizzare l'evoluzione del livello in maniera continuativa o step-by-step.
   - Mentre è in corso la simulazione nessuna cellula è manipolabile.
4. Il giocatore deve avere la possibilità di resettare il livello al suo stato iniziale.

### Requisiti opzionali
1. L'applicazione deve permettere all'utente di creare un nuovo livello o modificare un livello già esistente attraverso un *level editor*.
   - Il menù deve avere un'opzione per aprire il *level editor*.
2. Il gioco ha anche la cellula di tipo *Trash*.
3. L'applicazione deve avere musica ed effetti sonori.
   - Il menù deve avere un'opzione per accedere alle impostazioni dove si può modificare il volume della musica e degli effetti sonori.
4. Il gioco deve avere animazioni.

![Diagramma del modello del dominio dell'applicazione catturato tramite diagramma delle classi UML](imgs/domain_classes.jpg)

## Requisiti non funzionali

## Requisiti implementativi
