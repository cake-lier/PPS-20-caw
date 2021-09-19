| Priorità | Elemento | Dettagli | Stima iniziale dimensioni | Al 1<sup>o</sup> Sprint | Al 2<sup>o</sup> Sprint | Al 3<sup>o</sup> Sprint | Al 4<sup>o</sup> Sprint | Al 5<sup>o</sup> Sprint |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Completare il gioco standalone | [1](#1) | 15 | 15 | 
| 2 | Implementazione delle regole del gioco in PROLOG | [2](#2) | 20 | 20
| 3 | Aggiungere la possibilità al gioco standalone di giocare ai livelli dei giocatori | [3](#3) | 15 | 5
| 4 | Creazione di una GUI per visualizzare i livelli creati con il DSL | [4](#4) | 10 | 10
| 5 | Trasformare la GUI per visualizzare i livelli in un level editor | [5](#5) | 20 | 20
| 6 | Aggiungere la cellula "trash" | [6](#6) | 10 | 10
| 7 | Aggiungere modulo per l'audio alla view | [7](#7) | 8 | 8

**Definition of done**: il codice deve essere passato attraverso tutte le fasi del TDD, perciò deve funzionare, deve passare test pensati per quel codice che coprano adeguatamente i suoi casi d'uso (senza introdurre regressioni) e deve essere stato rifattorizzato in maniera tale che segua gli standard del linguaggio scala e sia compatibile con i principi SOLID che guidano l'implementazione. Deve inoltre essere stata prodotta una scaladoc adeguata per quel codice.

<a name="1">1</a>:
#### Completare il gioco standalone
Questo compito richiede di integrare il lavoro già fatto per quanto riguarda le componenti di view e controller del gioco con quelle dell'applicazione principale, nonché di completare le parti mancanti del model ed arrivare ad una versione funzionante del gioco.

<a name="2">2</a>: 
#### Implementazione delle regole del gioco in PROLOG
Ad ognuna delle cellule "attive" (mover, generator, rotator), in ciascuna delle sue varianti direzionali (right - left - top - bottom), deve corrispondere un predicato PROLOG che, data la cellula e i suoi "dintorni", sia capace di restituire lo stato di tutte le cellule passate aggiornato.
	
<a name="3">3</a>: 
#### Aggiungere la possibilità al gioco standalone di giocare livelli dei giocatori
In questo caso, non solo i livelli di default, ma anche i livelli dei giocatori dovranno poter essere giocati tramite il gioco che è stato realizzato. Deve perciò poter prendere in input un livello qualsiasi e permettere di giocarlo, come se fosse uno dei livelli già creati. 

<a name="4">4</a>:
#### Creazione di una GUI per visualizzare i livelli creati con il DSL
Questa GUI avrà come unico scopo quello di visualizzare sotto forma di griglia il contenuto del file JSON risultante dalla creazione di un livello con il DSL.

<a name="5">5</a>: 
#### Trasformare la GUI per visualizzare i livelli in un level editor
In questo caso, occorre trasformare il visualizzatore di livelli in un level editor completo, capace di modificare il livello così come era contenuto nel file e salvarlo in un nuovo file che costituirà il livello creato. Questo implica un'interfaccia più complessa, con cui l'utente può anche interagire, dove sono mostrate tutte le cellule che possono essere piazzate, così come l'area di gioco.

<a name="6">6</a>: 
#### Aggiungere la cellula "trash"
La cellula "trash" non fa altro che eliminare tutte le cellule che vi si muovono contro. Questo implica specificare delle nuove regole in PROLOG che gestiscano questa cellula (o semplicemente modificare le preesistenti in maniera tale che la considerino) e di aggiungerla in tutte le interfacce grafiche che sono state realizzate fino ad ora (il visualizzatore o l'editor di livelli e il gioco vero e proprio).

<a name="7">7</a>:
#### Aggiungere modulo audio alla view
Deve essere possibile riprodurre sia musica che suoni durante l'avvio di qualsiasi applicazione. La musica dovrà essere riprodotta in loop e dovranno essere presenti più musiche, una per ogni componente dell'applicazione (menu, gioco ed eventualmente level editor). Queste non dovranno sovrapporsi, ma ognuna deve terminare nel momento nel quale comincia la successiva. Per quanto riguarda i suoni, questi invece dovranno essere di breve durata ed emessi nel momento nel quale si verifica un evento di sufficiente rilevanza all'interno della view (è stato premuto un bottone, il gioco è avanzato di uno step, un nemico è stato ucciso, ecc). Questo significa che possono sovrapporsi tra di loro e anche sovrapporsi alla musica, che non dovrà fermarsi nel momento nel quale un suono viene riprodotto. Inoltre, deve essere disponibile nel gioco una pagina di "impostazioni" dove, tra le altre, sono presenti quelle per regolare il volume dei suoni e della musica.
