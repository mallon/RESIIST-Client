A - Plan 2D de la gare
---------------------
Le fichier "2D_plan" présente le plan 2D de la gare utilisé par notre simulation.
Composants :
	* Les salles du Hall, qui sont les seuls endroits où peuvent se cacher les civils, sont identifiées par "M1" jusqu'à "M12",
	* Les portes sont représentées par des rectangles verts-foncés,
	* Les issues de secours sont représentées par des rectangles d'un vert plus clair. Les civils et terroristes peuvent s'échapper par les issues de secours.
	
B - La simulation
---------------
I - Le déroulement de la simulation est visible avec la vidéo "gare_attentat_simulation_gama_engine" dans le répertoire "simulation_video".
II - Information sur les éléments visuels :
	* Civils (97) :
		- "E" : représente ceux qui s'échappent (rose),
		- "F" : ceux qui suivent un leader,
		- "L" : les leaders,
		- "P" : paralysés (bleu clair),
		- "H" : cachés (bleu fonçé)
	* Terroristes (6) : en rouge.
	* Porte (18) : vert
	* Issues de secours (7) : rouge clair
	* Zones où les civils ne peuvent se cacher : bleu clair
	* Croix : civils abattus

C - Données de la simulation
----------------------------
I - Temporalité :
	* nombre de cycles (unités de temps logique) : 602
	* durée : 2mn
	Remarque : Il est bon de noter ces points concernant la durée de la simulation : 
		- par définition, la simulation ne se passe pas en temps réel. Etant donnée sa vitesse d'exécution, cela serait tout simplement "inregardable" si la 3D suivait le même rythme (cela irait en effet beaucoup trop vite).
		- si on devait passer de la durée de la simulation à la durée réelle de l'événement, on est sur du 129mn (cf. fichiers indicateurs, indicateur "durée de l'événement").
	Je pense donc qu'il faut trouver pour la 3D une vitesse "confortable" pour la visualisation et en même temps qui ne donne pas un temps de simulation 3D trop long.
	
II - Les fichiers à considérer se trouvent dans le répertoire "data_csv". Les fichiers concernent :
	* Les portes : 
		- doorsLogs.csv : 
			* contient les identifiants et le statu de chaque porte à chaque cycle.
			* statu : ouvert ou fermé.
	
	* Les indicateurs :
		- indicatorsLogs.csv :
			* contient les indicateurs globaux et leur valeur  à chaque cycle.
			* indicateurs :
				\ nombre de morts
				\ taux global d'oocupation
				\ distance moyenne parcour par les terroristes
				\ durée de l'événement (en mn)
	* Les civils :
		- Agents.csv :
			* contient les salles, leurs identifiants et la répartition des civils (ou "agents") dans la gare.
			
		- civiliansLogs.csv :
			* contient l'identifiant des civils, leur statu, leur coordonnées (x et y) à chaque cycle.
			* statu : vivant, caché, en fuite
			Pour ceux tués, le fichier suivants indiquent à quels endroits ils l'ont été.
			
		- corpsesLogs.csv :
			* contient l'identifiant des civils abattus, leur coordonnées (x et y) et à quel cycle ils l'ont été.
			
		- escapedCiviliansLogs.csv :
			* contient l'identifiant des civils qui ont réussis à s'échapper, leur coordonnées (x et y) et à quel cycle ils ont réussis.
		
	* Les terroristes
		- Terroristes.csv :
			* contient les salles, leurs identifiants et la répartition des terroristes dans la gare.
			
		- terroristLogs.csv :
			* contient l'identifiant des terroristes et leur coordonnées (x et y) à chaque cycle.
			
		- escapedTerroristLogs.csv :
			* contient l'identifiant des terroristes qui ont réussis à s'échapper, leur coordonnées (x et y) et à quel cycle ils ont réussis.