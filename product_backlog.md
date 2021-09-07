| Priorità | Elemento | Dettagli | Stima iniziale dimensioni | Al 1<sup>o</sup> Sprint | Al 2<sup>o</sup> Sprint | Al 3<sup>o</sup> Sprint | Al 4<sup>o</sup> Sprint | Al 5<sup>o</sup> Sprint |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Creazione di un gioco standalone con i livelli di default | [^1] | 50
| 2 | Implementazione delle regole del gioco in PROLOG | [^2] | 20
| 3 | Creazione di un DSL per creare i livelli | [^3] | 20
| 4 | Creazione di una GUI per visualizzare i livelli creati | [^4] | 10
| 5 | Aggiungere la possibilità al gioco standalone di giocare livelli dei giocatori | [^5] | 15
| 6 | Trasformare la GUI per visualizzare i livelli in un level editor | [^6] | 20
| 7 | Aggiungere la cellula "trash" | [^7] | 10

**Definition of done**: il codice deve essere passato attraverso tutte le fasi del TDD, perciò deve funzionare, deve passare test pensati per quel codice che coprano adeguatamente i suoi casi d'uso (senza introdurre regressioni) e deve essere stato rifattorizzato in maniera tale che segua gli standard del linguaggio scala e sia compatibile con i principi SOLID che guidano l'implementazione. Deve inoltre essere stata prodotta una scaladoc adeguata per quel codice.

[^1]: #### Creazione di un gioco standalone con i livelli di default 
	Questo elemento richiede la realizzazione del gioco, così come di un menu per la scelta dei livelli che possono poi essere giocati. Il gioco deve avere una board che mostra la disposizione delle cellule e le cellule devono poter essere spostate all'interno dell'area di gioco (drag & drop? click?). La board può essere resettata, fatta avanzare di uno step o fatta avanzare continuamente. Deve essere possibile avanzare di livello in livello quando lo si completa (ogni cellula nemica è stata eliminata).

[^2]: #### Implementazione delle regole del gioco in PROLOG
	Ad ognuna delle cellule "attive" (mover, generator, rotator), in ciascuna delle sue varianti direzionali (right - left - top - bottom), deve corrispondere un predicato PROLOG che, data la cellula e i suoi "dintorni", sia capace di restituire lo stato di tutte le cellule passate aggiornato.

[^3]: #### Creazione di un DSL per creare i livelli
	Il DSL dovrà quanto più possibile rispettare le regole della lingua inglese. Le informazioni che necessariamente il giocatore deve inserire sono: le dimensioni della board, la posizione e le dimensioni dell'area di gioco, la posizione di ciascuna cellula sulla board. La serializzazione del livello dovrà avvenire in un formato JSON aperto.

[^4]: #### Creazione di una GUI per visualizzare i livelli creati
	Questa GUI avrà come unico scopo quello di visualizzare sotto forma di griglia il contenuto del file JSON risultante dalla creazione di un livello.

[^5]: #### Aggiungere la possibilità al gioco standalone di giocare livelli dei giocatori
	In questo caso, non solo i livelli di default, ma anche i livelli dei giocatori dovranno poter essere giocati tramite il gioco che è stato realizzato. Deve perciò poter prendere in input un livello qualsiasi e permettere di giocarlo, come se fosse uno dei livelli già creati. 

[^6]: #### Trasformare la GUI per visualizzare i livelli in un level editor
	In questo caso, occorre trasformare il visualizzatore di livelli in un level editor completo, capace di modificare il livello così come era contenuto nel file e salvarlo in un nuovo file che costituirà il livello creato. Questo implica un'interfaccia più complessa, con cui l'utente può anche interagire, dove sono mostrate tutte le cellule che possono essere piazzate, così come l'area di gioco.

[^7]: #### Aggiungere la cellula “trash”
	La cellula "trash" non fa altro che eliminare tutte le cellule che vi si muovono contro. Questo implica specificare delle nuove regole in PROLOG che gestiscano questa cellula (o semplicemente modificare le preesistenti in maniera tale che la considerino) e di aggiungerla in tutte le interfacce grafiche che sono state realizzate fino ad ora (il visualizzatore o l'editor di livelli e il gioco vero e proprio).