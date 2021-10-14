| Priorità | Elemento | Dettagli | Stima iniziale dimensioni | Al 1<sup>o</sup> Sprint | Al 2<sup>o</sup> Sprint | Al 3<sup>o</sup> Sprint | Al 4<sup>o</sup> Sprint | Al 5<sup>o</sup> Sprint |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Aggiungere la cellula "trash" | [1](#1) | 10 | 10 | 10 | 10 | 10
| 2 | Aumentare qualità e quantità degli unit test | [2](#2) | 20 | 20 | 20 | 20 | 20
| 3 | Aggiungere acceptance test | [3](#3) | 20 | 20 | 20 | 20 | 20

**Definition of done**: il codice deve essere passato attraverso tutte le fasi del TDD, perciò deve funzionare, deve passare test pensati per quel codice che coprano adeguatamente i suoi casi d'uso (senza introdurre regressioni) e deve essere stato rifattorizzato in maniera tale che segua gli standard del linguaggio scala e sia compatibile con i principi SOLID che guidano l'implementazione. Deve inoltre essere stata prodotta una scaladoc adeguata per quel codice.

<a name="1">1</a>: 
#### Aggiungere la cellula "trash"
La cellula "trash" non fa altro che eliminare tutte le cellule che vi si muovono contro. Questo implica specificare delle nuove regole in PROLOG che gestiscano questa cellula (o semplicemente modificare le preesistenti in maniera tale che la considerino) e di aggiungerla in tutte le interfacce grafiche che sono state realizzate fino ad ora (il visualizzatore o l'editor di livelli e il gioco vero e proprio).

<a name="2">2</a>:
#### Aumentare qualità e quantità degli unit test
Gli unit test prodotti finora sono il risultato dell'applicazione della tecnica di TDD, ma molte più classi potrebbero essere coperte da questi test, in maniera tale da poter aumentare la copertura dei test sul progetto e avere maggiori certezze sulla sua correttezza.

<a name="3">3</a>:
#### Aggiungere acceptance test
Individuato un framework capace di effettuare "acceptance testing" adeguato per la soluzione implementata, è ora fondamentale introdurre gli acceptance test. In tal modo sarà possibile verificare il soddisfacimento delle specifiche date quanto più dettagliatamente possibile, così da automatizzare anche il processo di controllo dell'applicazione rilasciata.
