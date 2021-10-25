# Processo

## SCRUM

Abbiamo adottato un processo di sviluppo SCRUM-inspired: tale framework di sviluppo è adatto a team di piccola dimensione e favorisce lo sviluppo di software che potrebbe presentare difficoltà imprevedibili grazie al suo processo incrementale. Ciò è stato particolarmente utile nella realizzazione della nostra applicazione, poichè era il nostro primo progetto consistente in Scala.<br>
Abbiamo svolto *daily scrum*, ovvero brevi meeting giornalieri molto conducenti ad un alto grado di collaborazione, e 5 *sprint* ciasuno della durata di una settimana, con risultati tangibili alla fine di ognuno.<br>

All'inizio di ogni sprint abbiamo tenuto un team meeting per definire lo *sprint goal* e il *product backlog* per realizzarlo. Gli item che costituiscono ogni *product backlog* sono stati individuati suddividendo i main goal in sub-goals, e assegnando task autocontenuti e di piccole dimensioni per realizzare tali goal. L'assegnazione dei task ai membri del team era consentita sia all'inizio degli sprint che durante, per uno sviluppo più flessibile.

Matteo Castellucci ha svolto il ruolo di Product Owner e Scrum Master, conducendo i daily scrum, aggiornando i backlog degli sprint settimanali e producendo un report alla fine di ogni sprint.

## Continuous Integration
Per facilitare lo sviluppo in parallelo dei task assegnati ad ogni membro del team abbiamo organizzato il workflow su Git nel seguente modo: 
- il branch *main* è stato dedicato alle versioni principali dell'applicazione, coincidenti con ogni sprint, e alla documentazione del processo di sviluppo
- il branch *develop* è stato usato per aggregare man mano le varie feature sviluppate
- ogni feature ha avuto un proprio branch su cui hanno lavorato al massimo due persone contemporaneamente


Qui di seguito le GitHub Actions adoperate per automatizzare il workflow:
- *Release* per sistemi Linux, MacOS e Windows in seguito alla creazione di un tag
- esecuzione dei test ad ogni push su tutti i branch, eccetto main
- report di JaCoCo (code coverage dei test) ad ogni push sul branch develop
- generazione del presente report come pdf ad ogni push sul branch main nella cartella report/