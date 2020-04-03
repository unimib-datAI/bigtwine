# BigTWINE
In questa sezione verranno indicate alcune linee guida per facilitare l’utilizzo e la manutenzione di BigTWINE. La prima cosa utile da sapere è che tutto il codice è sotto controllo di versione ed è disponibile pubblicamente su GitHub con licenza AGPL. 

## Struttura del progetto
Nella repository è possibile trovare la configurazione [Docker Compose](docker-compose), la configurazione [Kubernetes](kubernetes), ulteriore [documentazione](docs). Nella sottocartella [services](services) è presente il sorgente di tutti i servizi che compongono il sistema, nella sottocartella [tools](tools) gli strumenti esterni utilizzati da bigtwine per l’elaborazione delle analisi e, infine, nella sottocartella [libraries](libraries) alcune librerie condivise da più servizi. 

## Informazioni generali sui servizi
Per tutti i progetti Java si è usato Gradle per la gestione delle dipendenze e si può far riferimento alle guide di JHipster (utilizzato nella versione 5.7.x) per l’avvio manuale dei singoli servizi. Lo sviluppo è stato portato avanti per mezzo dell’IDE IntelliJ IDEA di JetBrains, ma nelle directory dei progetti non sono state condivise le configurazioni in quanto generabile per mezzo del [plugin idea di Gradle](https://docs.gradle.org/current/userguide/idea_plugin.html) come indicato nella documentazione di Gradle, alternativamente è possibile utilizzare qualsiasi altro IDE. 

## Utilizzo con Docker
Per quanto riguarda la configurazione Docker Compose, si rimanda al [README](docker-compose/README.md) nella relativa directory per maggiori informazioni. Tutte le immagini pre-compilate sono disponibile nel registro pubblico [Docker Hub](https://hub.docker.com/u/bigtwine) sarà comunque necessario compilare alcuni file di configurazione per poter avviare lo stack.

### Requisiti hardware
Per l’avvio con Docker Compose sono necessari all’incirca 10GB di RAM e si consiglia almeno una CPU con 4 core fisici.

## Utilizzo con Kubernetes
Anche per quanto riguarda la configurazione Kubernetes, si rimanda al [README](kubernetes/README.md) presente nella directory kubernetes.

### Requisiti hardware
Per l’avvio con Kubernetes sono necessari circa 16GB di RAM. Nel file [Resource request kubernetes.xlsx](docs/autoscaling/Resource%20request%20kubernetes.xlsx) è presente una tabella riepilogativa dei requisiti.
