A - Plan 2D de la gare
---------------------
Le fichier "2D_plan" pr�sente le plan 2D de la gare utilis� par notre simulation.
Composants :
	* Les salles du Hall, qui sont les seuls endroits o� peuvent se cacher les civils, sont identifi�es par "M1" jusqu'� "M12",
	* Les portes sont repr�sent�es par des rectangles verts-fonc�s,
	* Les issues de secours sont repr�sent�es par des rectangles d'un vert plus clair. Les civils et terroristes peuvent s'�chapper par les issues de secours.
	
B - La simulation
---------------
I - Le d�roulement de la simulation est visible avec la vid�o "gare_attentat_simulation_gama_engine" dans le r�pertoire "simulation_video".
II - Information sur les �l�ments visuels :
	* Civils (97) :
		- "E" : repr�sente ceux qui s'�chappent (rose),
		- "F" : ceux qui suivent un leader,
		- "L" : les leaders,
		- "P" : paralys�s (bleu clair),
		- "H" : cach�s (bleu fon��)
	* Terroristes (6) : en rouge.
	* Porte (18) : vert
	* Issues de secours (7) : rouge clair
	* Zones o� les civils ne peuvent se cacher : bleu clair
	* Croix : civils abattus

C - Donn�es de la simulation
----------------------------
I - Temporalit� :
	* nombre de cycles (unit�s de temps logique) : 602
	* dur�e : 2mn
	Remarque : Il est bon de noter ces points concernant la dur�e de la simulation : 
		- par d�finition, la simulation ne se passe pas en temps r�el. Etant donn�e sa vitesse d'ex�cution, cela serait tout simplement "inregardable" si la 3D suivait le m�me rythme (cela irait en effet beaucoup trop vite).
		- si on devait passer de la dur�e de la simulation � la dur�e r�elle de l'�v�nement, on est sur du 129mn (cf. fichiers indicateurs, indicateur "dur�e de l'�v�nement").
	Je pense donc qu'il faut trouver pour la 3D une vitesse "confortable" pour la visualisation et en m�me temps qui ne donne pas un temps de simulation 3D trop long.
	
II - Les fichiers � consid�rer se trouvent dans le r�pertoire "data_csv". Les fichiers concernent :
	* Les portes : 
		- doorsLogs.csv : 
			* contient les identifiants et le statu de chaque porte � chaque cycle.
			* statu : ouvert ou ferm�.
	
	* Les indicateurs :
		- indicatorsLogs.csv :
			* contient les indicateurs globaux et leur valeur  � chaque cycle.
			* indicateurs :
				\ nombre de morts
				\ taux global d'oocupation
				\ distance moyenne parcour par les terroristes
				\ dur�e de l'�v�nement (en mn)
	* Les civils :
		- Agents.csv :
			* contient les salles, leurs identifiants et la r�partition des civils (ou "agents") dans la gare.
			
		- civiliansLogs.csv :
			* contient l'identifiant des civils, leur statu, leur coordonn�es (x et y) � chaque cycle.
			* statu : vivant, cach�, en fuite
			Pour ceux tu�s, le fichier suivants indiquent � quels endroits ils l'ont �t�.
			
		- corpsesLogs.csv :
			* contient l'identifiant des civils abattus, leur coordonn�es (x et y) et � quel cycle ils l'ont �t�.
			
		- escapedCiviliansLogs.csv :
			* contient l'identifiant des civils qui ont r�ussis � s'�chapper, leur coordonn�es (x et y) et � quel cycle ils ont r�ussis.
		
	* Les terroristes
		- Terroristes.csv :
			* contient les salles, leurs identifiants et la r�partition des terroristes dans la gare.
			
		- terroristLogs.csv :
			* contient l'identifiant des terroristes et leur coordonn�es (x et y) � chaque cycle.
			
		- escapedTerroristLogs.csv :
			* contient l'identifiant des terroristes qui ont r�ussis � s'�chapper, leur coordonn�es (x et y) et � quel cycle ils ont r�ussis.