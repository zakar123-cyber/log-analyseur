# 🧾 Analyse de Logs Multithread – Java / Spring Boot

##  Objectif du projet

Ce projet permet de **lire plusieurs fichiers journaux (logs Spring Boot)**,  
de **transformer chaque ligne en un objet `LogEntry`**,  
puis d’extraire différentes **statistiques** sur les logs :

- Nombre total d’entrées  
- Répartition par niveau (`INFO`, `WARN`, `ERROR`, etc.)  
- Logger le plus actif  
- Nombre d’erreurs (`ERROR`) détectées  

Le traitement est effectué dans un premier temps à l’aide d’une gestion simple avec des **threads
