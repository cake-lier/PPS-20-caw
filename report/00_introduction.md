# Introduzione

"Cells at Work" è un *puzzle game* basato sul concetto di "*cellular automaton*", dove perciò ogni livello è un automa rappresentato da una griglia composta da cellule. Dato un certo suo stato iniziale, il livello si evolve in modo deterministico applicando regole di gioco ben precise sulle cellule che lo compongono. Sfruttando i comportamenti che caratterizzano ogni cellula nel livello, l'obiettivo del giocatore è riorganizzarle in una disposizione capace di eliminare tutte quelle nemiche.

Il giocatore avrà la possibilità di visualizzare l'evoluzione del livello e di riportarlo alle condizioni iniziali per provare una nuova disposizione di cellule. Il giocatore, inoltre, avrà la possibilità di creare nuovi livelli o di modificare livelli già esistenti sia attraverso un *editor* di livelli, sia attraverso un *Domain-Specific Language* creato allo scopo, così da poterli condividere con altri giocatori. Questi potranno quindi giocare non solo ai livelli che il gioco fornisce, ma anche a quelli creati da altri.

"Cells at Work" è implementato sia in Scala che in PROLOG per poter avere un gioco che sfrutti al meglio i vantaggi di entrambi i linguaggi.
