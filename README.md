# Lab16 -Service-Chronom-tre-Java-Android-

Ce projet est une application Android développée en Java démontrant l'implémentation d'un Foreground Service (Service au premier plan). L'application permet de lancer un chronomètre qui continue de fonctionner de manière indépendante, même si l'interface utilisateur est fermée ou si une autre application est ouverte.

## Fonctionnalités

• Chronomètre persistant : Fonctionne en arrière-plan via un Service.

• Notification Temps Réel : Affiche le temps écoulé dans la barre de notifications (obligatoire pour les Foreground Services).

• Synchronisation UI : L'interface se met à jour automatiquement dès qu'elle est ouverte en se liant (Binding) au service.

• Compatibilité Android 14 : Gestion des types de services (dataSync) et des permissions de notifications.

## Étapes de Développement

### 1. Création de la classe Service

Le fichier ChronometreService.java gère la logique du temps. Il utilise un ScheduledExecutorService pour incrémenter le compteur chaque seconde et met à jour une notification persistante.Le fichier ChronometreService.java gère la logique du temps. Il utilise un ScheduledExecutorService pour incrémenter le compteur chaque seconde et met à jour une notification persistante.

### 2. Configuration du Manifest

Le service est déclaré dans le AndroidManifest.xml avec les propriétés suivantes :

• android:foregroundServiceType="dataSync" (Requis pour Android 14).

• android:exported="false" pour la sécurité.

### 3. Gestion des Permissions

Ajout des permissions nécessaires pour le bon fonctionnement sur les versions récentes d'Android :

• FOREGROUND_SERVICE & FOREGROUND_SERVICE_DATA_SYNC.

• POST_NOTIFICATIONS (Demande dynamique au lancement de l'app).

### 4. Interface Utilisateur (Layout)

L'interface dans activity_main.xml est composée :

• D'un TextView (tvTemps) pour l'affichage géant du temps.

• D'un bouton Démarrer pour lancer le service.

• D'un bouton Arrêter pour stopper le service et réinitialiser le temps.

### 5. Liaison Activity-Service

La MainActivity.java utilise une ServiceConnection pour se lier au service. Cela permet à l'activité de récupérer les données du chronomètre et de les afficher en temps réel via un Handler.

## Démonstration Vidéo

https://github.com/user-attachments/assets/2df8a449-db64-4aea-a020-3567392ce6e9





