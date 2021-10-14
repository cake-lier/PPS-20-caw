#### Sprint goal:
Aggiungere la cellula trash al gioco, aumentare la coverage data dagli unit test e aggiungere gli acceptance test.

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
            <td rowspan="4">Aggiungere la cellula "trash" al gioco</td>
            <td>Modificare il codice PROLOG perché si comporti correttamente anche con la cellula trash</td>
            <td>Matteo Castellucci</td>
            <td>2</td>
            <td>1</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere la cellula trash a tutte le entità di model</td>
            <td>Matteo Castellucci</td>
            <td>3</td>
            <td>2</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere possibilità di visualizzare la trash cell</td>
            <td>Matteo Castellucci</td>
            <td>3</td>
            <td>3</td>
            <td>3</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere metodi per la creazione delle cellule trash nel DSL</td>
            <td>Matteo Castellucci</td>
            <td>2</td>
            <td>2</td>
            <td>2</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td rowspan="6">Aumentare quantità e qualità unit test</td>
            <td>Unificare i test di serializzazione e deserializzazione, poi migliorarli</td>
            <td>Lorenzo Gardini</td>
            <td>3</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Aggiungere unit test per prolog engine e le classi a lui correlate</td>
            <td>Lorenzo Gardini</td>
            <td>4</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Migliorare i test di GameModel, aggiungere i test per GameState</td>
            <td>Lorenzo Gardini</td>
            <td>3</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Aggiungere unit test per tutte le classi del package "common.storage"</td>
            <td>Elena Rughi</td>
            <td>4</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Espandere gli unit test già scritti per Cell per coprire tutti i metodi mancanti di BaseCell, ripetere per PlayableCell, UpdateCell</td>
            <td>Elena Rughi</td>
            <td>3</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Testare Board, Level, LevelBuilder di editor</td>
            <td>Elena Rughi</td>
            <td>3</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td rowspan="4">Aggiungere gli acceptance test</td>
            <td>Aggiungere acceptance test per la pagina principale del menu</td>
            <td>Yuqi Sun</td>
            <td>1</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Aggiungere acceptance test per level selection, gioco</td>
            <td>Yuqi Sun</td>
            <td>7</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Aggiungere acceptance test per settings</td>
            <td>Matteo Castellucci</td>
            <td>6</td>
            <td>6</td>
            <td>6</td>
            <td>3</td>
            <td>1</td>
            <td>0</td>
            <td>0</td>
            <td>0</td>
        </tr>
        <tr>
            <td>Aggiungere acceptance test per pagina input editor, editor</td>
            <td>Lorenzo Gardini</td>
            <td>6</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </tbody>
</table>

#### Sprint result:
Ora la copertura del codice è adeguata e copre tutte le classi che possono essere testate. Sono presenti acceptance test capaci di verificare l'accettabilità dei requisiti così come definiti dal committente per ogni requisito testabile dal framework. Ora è possibile giocare e creare livelli con la cellula "trash".
