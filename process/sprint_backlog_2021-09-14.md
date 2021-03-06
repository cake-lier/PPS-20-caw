#### Sprint goal:
Completare la realizzazione del gioco, sia integrando quello che è stato realizzato nello sprint precedente, sia implementando le regole del gioco.

<table>
    <thead>
        <td><b>Elemento del product backlog</b></td>
        <td><b>Sprint Task</b></td>
        <td><b>Volontario</b></td>
        <td><b>Stima iniziale del costo</b></td>
        <td><b>Dopo il 1<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 2<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 3<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 4<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 5<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 6<sup>o</sup> giorno</b></td>
        <td><b>Dopo il 7<sup>o</sup> giorno</b></td>
    </thead>
    <tbody>
        <tr>
            <td rowspan="3">Completare il gioco standalone</td>
            <td>Integrare GameView e GameController con l'applicazione standalone già creata</td>
            <td>Matteo Castellucci</td>
            <td>5</td>
            <td>4</td>
            <td>2</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Completare la logica del metodo update del model</td>
            <td>Elena Rughi</td>
            <td>6</td>
            <td>6</td>
            <td>6</td>
            <td>5</td>
            <td>3</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Modificare il RulesEngine alle nuove specifiche derivanti dal codice PROLOG</td>
            <td>Yuqi Sun</td>
            <td>4</td>
            <td>4</td>
            <td>4</td>
            <td>3</td>
            <td>2</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td rowspan="3">Implementazione delle regole del gioco in PROLOG</td>
            <td>Implementare predicati "mover"</td>
            <td>Matteo Castellucci & Lorenzo Gardini & Elena Rughi & Yuqi Sun</td>
            <td>6</td>
            <td>6</td>
            <td>6</td>
            <td>4</td>
            <td>3</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Implementare predicati "rotator"</td>
            <td>Matteo Castellucci & Lorenzo Gardini & Elena Rughi & Yuqi Sun</td>
            <td>6</td>
            <td>6</td>
            <td>6</td>
            <td>4</td>
            <td>4</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Implementare predicati "generator"</td>
            <td>Matteo Castellucci & Lorenzo Gardini & Elena Rughi & Yuqi Sun</td>
            <td>8</td>
            <td>8</td>
            <td>8</td>
            <td>4</td>
            <td>4</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere la possibilità al gioco standalone di giocare ai livelli dei giocatori</td>
            <td>Adattare interfaccia GameView per livelli non default</td>
            <td>Matteo Castellucci</td>
            <td>5</td>
            <td>2</td>
            <td>1</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td rowspan="3">Aggiungere il modulo audio alla view</td>
            <td>Aggiungere musica di sottofondo</td>
            <td>Lorenzo Gardini</td>
            <td>2</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere suoni in corrispondenza degli eventi</td>
            <td>Lorenzo Gardini</td>
            <td>3</td>
            <td>1</td>
            <td>1</td>
            <td>1</td>
            <td>1</td>
            <td>1</td>
            <td>1</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere pagina delle impostazioni con quelle per l'audio</td>
            <td>Lorenzo Gardini</td>
            <td>3</td>
            <td>3</td>
            <td>3</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
    </tbody>
</table>

#### Sprint result:
L'obiettivo di questo sprint è stato raggiunto: il gioco è stato completato e può essere ora giocato, non si trova più in una fase prototipale. Sono necessarie delle rifiniture, che necessariamente saranno eseguite nel prossimo sprint. Il gioco è dotato di suoni e musiche e anche le componenti grafiche restanti sono complete e utilizzabili. Inoltre, le componenti del gioco sono state realizzate in modo tale da essere sufficientemente flessibili e riutilizzate in più contesti. Il gioco può infatti essere lanciato per giocare un livello di default o uno creato dall'utente e sarà possibile dal prossimo sprint lanciarlo direttamente dal DSL.
