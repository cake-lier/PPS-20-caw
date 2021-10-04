| Priorità | Elemento | Dettagli | Stima iniziale dimensioni | Al 1<sup>o</sup> Sprint | Al 2<sup>o</sup> Sprint | Al 3<sup>o</sup> Sprint | Al 4<sup>o</sup> Sprint | Al 5<sup>o</sup> Sprint |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Refactoring e miglioramento della documentazione | [1](#1) | 40 | 40 | 40 | 40
| 2 | Permettere all'area giocabile di avere forme diverse da quella rettangolare | [2](#2) | 10 | 10 | 10 | 10
| 3 | Aggiungere la cellula "trash" | [3](#3) | 10 | 10 | 10 | 10

**Definition of done**: il codice deve essere passato attraverso tutte le fasi del TDD, perciò deve funzionare, deve passare test pensati per quel codice che coprano adeguatamente i suoi casi d'uso (senza introdurre regressioni) e deve essere stato rifattorizzato in maniera tale che segua gli standard del linguaggio scala e sia compatibile con i principi SOLID che guidano l'implementazione. Deve inoltre essere stata prodotta una scaladoc adeguata per quel codice.

<a name="1">1</a>:
#### Debug, refactoring, miglioramento della documentazione
Questo compito richiede di ripulire il codice e rifattorizzarlo, in maniera tale che aderisca allo standard di codice e di qualità prefissato. È necessario inoltre migliorare la sua documentazione, arricchendola in maniera tale che sia la più comprensibile possibile, alla luce delle modifiche che il codice ha subito.

<a name="2">2</a>:
#### Permettere all'area giocabile di avere forme diverse da quella rettangolare
Per poter dare maggior libertà ai giocatori nel costruire i propri livelli, si vuole fare in modo che l'area giocabile, dove le celle possono essere trascinate durante il gioco, non abbia necessariamente la forma rettangolare che ha adesso. L'idea è di farle assumere forme a piacere, così da rendere più complessi i livelli di gioco e svincolare i giocatori dai limiti imposti quando usano l'editor o il DSL.

<a name="3">3</a>: 
#### Aggiungere la cellula "trash"
La cellula "trash" non fa altro che eliminare tutte le cellule che vi si muovono contro. Questo implica specificare delle nuove regole in PROLOG che gestiscano questa cellula (o semplicemente modificare le preesistenti in maniera tale che la considerino) e di aggiungerla in tutte le interfacce grafiche che sono state realizzate fino ad ora (il visualizzatore o l'editor di livelli e il gioco vero e proprio).
