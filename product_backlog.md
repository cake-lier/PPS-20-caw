| Priorità | Elemento | Dettagli | Stima iniziale dimensioni | Al 1<sup>o</sup> Sprint | Al 2<sup>o</sup> Sprint | Al 3<sup>o</sup> Sprint | Al 4<sup>o</sup> Sprint | Al 5<sup>o</sup> Sprint |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Debug, refactoring, miglioramento della documentazione | [1](#1) | 15 | 15 | 15 |
| 2 | Aggiunta di un level editor nel gioco standalone | [2](#2) | 20 | 20 | 20
| 3 | Creazione di un'applicazione con il solo gioco e una con il solo editor per il DSL | [3](#3) | 5 | 5 | 5 |
| 4 | Aggiungere file di settings per supporto ai livelli progressivi | [4](#4) | 10 | 10 | 10
| 5 | Permettere all'area giocabile di avere forme diverse da quella rettangolare | [5](#5) | 10 | 10 | 10
| 6 | Aggiungere la cellula "trash" | [6](#6) | 10 | 10 | 10

**Definition of done**: il codice deve essere passato attraverso tutte le fasi del TDD, perciò deve funzionare, deve passare test pensati per quel codice che coprano adeguatamente i suoi casi d'uso (senza introdurre regressioni) e deve essere stato rifattorizzato in maniera tale che segua gli standard del linguaggio scala e sia compatibile con i principi SOLID che guidano l'implementazione. Deve inoltre essere stata prodotta una scaladoc adeguata per quel codice.

<a name="1">1</a>:
#### Debug, refactoring, miglioramento della documentazione
Questo compito richiede di effettuare il debugging dell'applicazione nel suo complesso, ora che è stata completata. Occorre verificare il suo comportamento atteso e se e come si discosta dal previsto. Occorre inoltre ripulire il codice e rifattorizzarlo, nonché migliorare la sua documentazione, alla luce delle modifiche che ha subito e che subirà dopo il completamento di questo task. Inoltre, occorre individuare i problemi di ottimizzazione delle risorse e risolverli.

<a name="2">2</a>: 
#### Aggiunta di un level editor nel gioco standalone
Occorre realizzare un level editor, capace di creare un livello inziale vuoto sulla base delle indicazioni del giocatore così che quest'ultimo possa poi modificare il livello a suo piacimento e  poi salvarlo in un nuovo file che costituirà il livello creato. Necessariamente, oltre al livello che sta venendo costruito, devono essere mostrati tutti i comandi necessari per farlo, ovvero tutte le cellule che possono essere piazzate e i comandi per piazzare l'area di gioco.

<a name="3">3</a>:
#### Creazione di un'applicazione con il solo gioco e una con il solo editor per il DSL
Occorre realizzare due applicazioni che possano essere lanciate da riga di comando che permettano di utilizzare la sola interfaccia di gioco e la sola interfaccia dell'editor. Queste due applicazioni dovranno ricevere in input il percorso del file del livello che visualizzano e che verrà prodotto dopo l'esecuzione del DSL. In questo modo, nel DSL sarà possibile lanciare tramite degli appositi comandi queste due applicazioni fornendo ulteriori funzionalità che il giocatore può sfruttare.

<a name="4">4</a>:
#### Aggiungere file di settings per supporto ai livelli progressivi
Al momento, l'applicazione non ha memoria delle impostazioni che l'utente inserisce al suo interno. Si vuole perciò aggiungere il supporto per un file di impostazioni in cui possano essere salvate sia le informazioni sulle impostazioni che l'utente ha scelto, sia le informazioni sull'avanzamento del gioco. In questo modo, sarà possibile rendere i livelli sbloccabili progressivamente, mano a mano che il giocatore avanza nel gioco.

<a name="5">5</a>:
#### Permettere all'area giocabile di avere forme diverse da quella rettangolare
Per poter dare maggior libertà ai giocatori nel costruire i propri livelli, si vuole fare in modo che l'area giocabile, dove le celle possono essere trascinate durante il gioco, non abbia necessariamente la forma rettangolare che ha adesso. L'idea è di farle assumere forme a piacere, così da rendere più complessi i livelli di gioco e svincolare i giocatori dai limiti imposti quando usano l'editor o il DSL.

<a name="6">6</a>: 
#### Aggiungere la cellula "trash"
La cellula "trash" non fa altro che eliminare tutte le cellule che vi si muovono contro. Questo implica specificare delle nuove regole in PROLOG che gestiscano questa cellula (o semplicemente modificare le preesistenti in maniera tale che la considerino) e di aggiungerla in tutte le interfacce grafiche che sono state realizzate fino ad ora (il visualizzatore o l'editor di livelli e il gioco vero e proprio).
