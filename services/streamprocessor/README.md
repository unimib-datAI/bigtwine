# BigTWINE Streamprocessor

Lo streamprocessor comprende 3 diversi job Flink:
- ExportResultsJob: Esporta i risultati in un documento GridFS
- TwitterStreamJob: Elabora l'input dell'utente (query, bbox, dataset, ...) attraverso i servizi della pipeline
- TwitterStreamDump: Genera un file contente alcuni tweet recuperati dall'api streaming di Twitter in un file testuale (Job usato solo per test)

Tutti i job possono essere eseguire anche al di fuori del contesto BigTWINE. 
Sono necessarie delle credenziali OAuth Twitter, quelle presenti nelle running configuration di IntelliJ IDEA sono solo d'esempio
