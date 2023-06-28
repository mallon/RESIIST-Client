<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Importation</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>




	<nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
		<a class="navbar-brand" href="#"></a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarsExampleDefault"
			aria-controls="navbarsExampleDefault" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse" id="navbarsExampleDefault">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active"><a class="nav-link" href="/home">Accueil
						<span class="sr-only">(current)</span>
				</a></li>

				<li class="nav-item dropdown"><a
					class="nav-link dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-haspopup="true" aria-expanded="false">Fonctions</a>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="/foncAgre">Fonctions
							d'agrégation</a> <a class="dropdown-item" href="/foncNorm">Fonctions
							de normalisation </a> <a class="dropdown-item" href="/foncEvolu">Fonctions
							d'évolution</a>
					</div></li>
				<li class="nav-item dropdown"><a
					class="nav-link dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-haspopup="true" aria-expanded="false">Méthodes</a>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="/ahp">AHP</a> <a
							class="dropdown-item" href="/ahptopsis">AHP Topsis</a> <a
							class="dropdown-item" href="/anpGetForm">ANP</a> <a
							class="dropdown-item" href="/electre">Electre</a> <a
							class="dropdown-item" href="/fuzzyAhp">Fuzzy-AHP</a> <a
							class="dropdown-item" href="/FuzzyAhpTopsis">Fuzzy-AHP-Topsis-Group
							Decision</a> <a class="dropdown-item" href="/fuzzyAnp">Fuzzy-ANP</a>
						<a class="dropdown-item" href="/fuzzyTopsis">Fuzzy-Topsis</a> <a
							class="dropdown-item" href="/promethee">Prométhée</a> <a
							class="dropdown-item" href="/topsis">Topsis</a>
					</div></li>
			</ul>
			<form class="form-inline my-2 my-lg-0">
				<input class="form-control mr-sm-2" type="text" placeholder="Search"
					aria-label="Search">
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
			</form>
		</div>
	</nav>


	<div class="jumbotron">
		<div class="container">
			<!-- Example row of columns -->
			<div class="row">
				<div class="col-md-4">
					<br />
					<h3>Groupe 1</h3>
					<p>
						Pour les méthodes : <br> * AHP<br>* ANP<br>* Fuzzy
						AHP<br>* Fuzzy ANP
					</p>

				</div>
				<div class="col-md-4">
					<br />
					<h3>Groupe 2</h3>
					<p>
						Pour les méthodes : <br>* Fuzzy Topsis <br>* Fuzzy AHP
						Topsis-Group Decision <br> Ce sont des méthodes de décision
						en groupe.
					</p>

				</div>
				<div class="col-md-4">
					<br />
					<h3>Groupe 3</h3>
					<p>
						Pour les méthodes : <br>* AHP Topsis <br>* Electre<br>*
						Topsis<br>* Promethee
					</p>

				</div>
			</div>
			<hr>



			<form action="GroupMet" method="post">
				<br /> <select name="groupMet" class="form-select"
					aria-label="Default select example">
					<option selected>Selectionner le groupe</option>
					<option value="1">Groupe 1</option>
					<option value="2">Groupe 2</option>
					<option value="3">Groupe 3</option>
				</select> <input type="submit" class="btn btn-secondary btn-sm"
					value="Valider" />

			</form>
		</div>
	</div>

	<c:if test="${(gp==1)}">
		<div class="jumbotron">
			<div class="container">
				<h2>Informations groupe 1</h2>
				<form method="post" action="fuzAhpSaveDetailsImport">
					Nombre de critères:
					<div class="col-sm-10">
						<input type="number" name="nbcrit" />
					</div>
					<br /> Nombre de décisions:
					<div class="col-sm-10">
						<input type="number" name="nbdec" />
					</div>
					<br /> Liste des critères:
					<div class="col-sm-10">
						<input type="text" name="critlist" id="critlistInline" /> <small
							id="critlistInline" class="text-muted"> séparés d'une
							virgule </small>
					</div>
					<br /> Liste des décisions:
					<div class="col-sm-10">
						<input type="text" name="declist" id="declistInline" /> <small
							id="declistInline" class="text-muted"> séparés d'une
							virgule </small>
					</div>
					<br /> <input type="submit" class="btn btn-outline-primary"
						value="Valider" /> <br />
				</form>
				<br />
				<h2>Choix des méthodes</h2>
				<p>Pour en sélectionner plusieurs, appuyer sur Ctrl</p>

				<br />
				<form action="Methode1" method="post">
					<select name="met1" size="4" multiple>

						<option value="1">Fuzzy-AHP</option>
						<option value="2">AHP</option>
						<option value="3">Fuzzy-ANP</option>
						<option value="4">ANP</option>

					</select> <br /> <input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" /> <br />
				</form>
			</div>
		</div>

		<c:if test="${nbmet>0}">
			<div class="jumbotron">
				<div class="container">
					<h2>Paramètres</h2>

					<c:forEach var="i" begin="0" end="${nbmet-1}" step="1">
						<c:if test="${listMet[i]==1}">
							<h5>* FUZZY-AHP</h5>

							<c:if test="${nbcrit>0}">
								<form method="post" enctype="multipart/form-data"
									action="fuzAhpGetTableCritImport">
									<h6>Importer la table de pondération des critères</h6>
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

								<br />

								<c:if test="${coheren=='False'}"> Ce n'est pas cohérent </c:if>
								<c:if test="${coheren=='True'}"> C'est cohérent, passer au tableau suivant </c:if>


								<p>
									Vecteur normal :
									<c:out value="${vecteur}"></c:out>
								</p>

								<p>
									<strong>Importer les <c:out value="${nbcrit}" />
										tableaux où on compare entre elles les décisions pour chaque
										critère
									</strong>
								</p>
								<br />
								<h6>Importer la table de pondération des décisions pour le
									critère 1</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit1Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren2=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=2}">

								<h6>Importer la table de pondération des décisions pour le
									critère 2</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit2Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren3=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=3}">

								<h6>Importer la table de pondération des décisions pour le
									critère 3</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit3Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren4=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=4}">

								<h6>Importer la table de pondération des décisions pour le
									critère 4</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit4Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren5=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=5}">

								<h6>Importer la table de pondération des décisions pour le
									critère 5</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit5Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=6}">

								<h6>Importer la table de pondération des décisions pour le
									critère 6</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit6Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren7=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=7}">

								<h6>Importer la table de pondération des décisions pour le
									critère 7</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit7Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren8=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=8}">

								<h6>Importer la table de pondération des décisions pour le
									critère 8</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit8Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren9=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=9}">

								<h6>Importer la table de pondération des décisions pour le
									critère 9</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit9Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren10=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren10=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=10}">

								<h6>Importer la table de pondération des décisions pour le
									critère 10</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzahpgetCrit10Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<form action="fuzahpgetResultImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode FuzzyAHP validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>




						</c:if>
						<c:if test="${listMet[i]==2}">
							<h5>* AHP</h5>
							<c:if test="${nbcrit>0}">
								<form method="post" enctype="multipart/form-data"
									action="AhpGetTableCritImport">
									<h6>Importer la table de pondération des critères</h6>
									<br />

									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

								<br />

								<c:if test="${coherenahp=='False'}"> Ce n'est pas cohérent </c:if>
								<c:if test="${coherenahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>


								<p>
									Vecteur normal :
									<c:out value="${vecteur}"></c:out>
								</p>

								<p>
									<strong>Importer les <c:out value="${nbcrit}" />
										tableaux où on compare entre elles les décisions pour chaque
										critère
									</strong>
								</p>
								<br />
								<h6>Importer la table de pondération des décisions pour le
									critère 1</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit1Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren2ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren2ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=2}">

								<h6>Importer la table de pondération des décisions pour le
									critère 2</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit2Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren3ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren3ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=3}">

								<h6>Importer la table de pondération des décisions pour le
									critère 3</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit3Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren4ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren4ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=4}">

								<h6>Importer la table de pondération des décisions pour le
									critère 4</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit4Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren5ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren5ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=5}">

								<h6>Importer la table de pondération des décisions pour le
									critère 5</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit5Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren6ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren6ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=6}">

								<h6>Importer la table de pondération des décisions pour le
									critère 6</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit6Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren7ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren7ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=7}">

								<h6>Importer la table de pondération des décisions pour le
									critère 7</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit7Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren8ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren8ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=8}">

								<h6>Importer la table de pondération des décisions pour le
									critère 8</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit8Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren9ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren9ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=9}">

								<h6>Importer la table de pondération des décisions pour le
									critère 9</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit9Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren10ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren10ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=10}">

								<h6>Importer la table de pondération des décisions pour le
									critère 10</h6>

								<form method="post" enctype="multipart/form-data"
									action="ahpgetCrit10Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>
							<c:if test="${coheren11ahp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren11ahp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>

							<form action="getResultAHPImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode AHP validés appuyer
									sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>

						</c:if>
						<c:if test="${listMet[i]==3}">
							<h5>* FUZZY-ANP</h5>



							<c:if test="${nbcrit>0}">
								<form method="post" enctype="multipart/form-data"
									action="fuzAnpGetTableCritImport">
									<h6>Importer la table de pondération des critères</h6>
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
								<br />

								<c:if test="${coherenanp=='False'}"> Ce n'est pas cohérent </c:if>
								<c:if test="${coherenanp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>


								<p>
									<strong>Importer les <c:out value="${nbcrit}" />
										tableaux où on compare entre elles les décisions pour chaque
										critère
									</strong>
								</p>
								<br />
								<h6>Importer la table de pondération des décisions pour le
									critère 1</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit1Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren2anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren2anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=2}">

								<h6>Importer la table de pondération des décisions pour le
									critère 2</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit2Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren3anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren3anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=3}">

								<h6>Importer la table de pondération des décisions pour le
									critère 3</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit3Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren4anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren4anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=4}">

								<h6>Importer la table de pondération des décisions pour le
									critère 4</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit4Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />

							</c:if>

							<c:if test="${coheren5anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren5anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=5}">

								<h6>Importer la table de pondération des décisions pour le
									critère 5</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit5Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren6anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren6anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=6}">

								<h6>Importer la table de pondération des décisions pour le
									critère 6</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit6Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />

							</c:if>

							<c:if test="${coheren7anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren7anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=7}">

								<h6>Importer la table de pondération des décisions pour le
									critère 7</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit7Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${coheren8anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren8anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=8}">

								<h6>Importer la table de pondération des décisions pour le
									critère 8</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit8Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren9anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren9anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=9}">

								<h6>Importer la table de pondération des décisions pour le
									critère 9</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit9Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${coheren10anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren10anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=10}">

								<h6>Importer la table de pondération des décisions pour le
									critère 10</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetCrit10Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${coheren11anp=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren11anp=='True'}"> C'est cohérent, passer au tableau suivant </c:if>

							<c:if test="${nbdec>0}">
								<p>
									<strong> Importer les <c:out value="${nbdec}" />
										tableaux où on compare entre eux les critères pour chaque
										décision
									</strong>
								</p>
								<br />

								<h6>Importer la table de pondération des critères pour la
									décision 1</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec1Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>
							<c:if test="${anpcoheren1=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren1=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=2}">

								<h6>Importer la table de pondération des critères pour la
									décision 2</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec2Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren2=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=3}">

								<h6>Importer la table de pondération des critères pour la
									décision 3</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec3Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${anpcoheren3=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=4}">

								<h6>Importer la table de pondération des critères pour la
									décision 4</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec4Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren4=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=5}">

								<h6>Importer la table de pondération des critères pour la
									décision 5</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec5Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren5=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=6}">

								<h6>Importer la table de pondération des critères pour la
									décision 6</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec6Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=7}">

								<h6>Importer la table de pondération des critères pour la
									décision 7</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec7Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>


							<c:if test="${anpcoheren7=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=8}">

								<h6>Importer la table de pondération des critères pour la
									décision 8</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec8Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren8=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=9}">

								<h6>Importer la table de pondération des critères pour la
									décision 9</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec9Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<c:if test="${anpcoheren9=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${anpcoheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=10}">

								<h6>Importer la table de pondération des critères pour la
									décision 10</h6>


								<form method="post" enctype="multipart/form-data"
									action="fuzAnpgetDec10Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />
							</c:if>

							<form action="fuzAnpgetResultImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Fuzzy ANP validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>




						</c:if>
						<c:if test="${listMet[i]==4}">
							<h5>* ANP</h5>


							<h6>Table de pondération des critères</h6>
							<br />
							<form method="post" enctype="multipart/form-data"
								action="anpGetTableCritImport">
								<br />
								<p>
									<input type="file" name="file" id="file" />
								</p>
								<input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />
							</form>
							<br />


							<c:if test="${vcoheren=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren=='True'}"> C'est cohérent, passer au tableau suivant </c:if>


							<p>
								<strong> Importer les <c:out value="${nbcrit}" />
									tableaux où on compare entre elles les décisions pour chaque
									critère
								</strong>
							</p>
							<br />
							<h6>Importer la table de pondération des décisions pour le
								critère 1</h6>

							<form method="post" enctype="multipart/form-data"
								action="anpgetCrit1Import">
								<br />
								<p>
									<input type="file" name="file" id="file" />
								</p>
								<input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />
							</form>
							<br />


							<c:if test="${vcoheren2=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=2}">

								<h6>Importer la table de pondération des décisions pour le
									critère 2</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit2Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
								<br />

							</c:if>

							<c:if test="${vcoheren3=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=3}">

								<h6>Importer la table de pondération des décisions pour le
									critère 3</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit3Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${vcoheren4=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=4}">

								<h6>Importer la table de pondération des décisions pour le
									critère 4</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit4Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vcoheren5=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=5}">

								<h6>Importer la table de pondération des décisions pour le
									critère 5</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit5Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=6}">

								<h6>Importer la table de pondération des décisions pour le
									critère 6</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit6Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vcoheren7=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=7}">

								<h6>Importer la table de pondération des décisions pour le
									critère 7</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit7Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${vcoheren8=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=8}">

								<h6>Importer la table de pondération des décisions pour le
									critère 8</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit8Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vcoheren9=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=9}">

								<h6>Importer la table de pondération des décisions pour le
									critère 9</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit9Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vcoheren10=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren10=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbcrit>=10}">

								<h6>Importer la table de pondération des décisions pour le
									critère 10</h6>

								<form method="post" enctype="multipart/form-data"
									action="anpgetCrit10Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>
							<c:if test="${vcoheren11=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vcoheren11=='True'}"> C'est cohérent, passer au tableau suivant </c:if>

							<p>
								<strong> Importer les <c:out value="${nbdec}" />
									tableaux où on compare entre eux les critères pour chaque
									décision
								</strong>
							</p>
							<br />

							<h6>Importer la table de pondération des critères pour la
								décision 1</h6>

							<form method="post" enctype="multipart/form-data"
								action="anpgetDec1Import">
								<br />

								<p>
									<input type="file" name="file" id="file" />
								</p>
								<input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />
							</form>


							<c:if test="${vanpcoheren1=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren1=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=2}">

								<h6>Importer la table de pondération des critères pour la
									décision 2</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec2Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren2=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=3}">

								<h6>Importer la table de pondération des critères pour la
									décision 3</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec3Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${vanpcoheren3=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=4}">

								<h6>Importer la table de pondération des critères pour la
									décision 4</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec4Import">
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren4=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=5}">

								<h6>Importer la table de pondération des critères pour la
									décision 5</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec5Import">
									<br />

									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren5=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=6}">

								<h6>Importer la table de pondération des critères pour la
									décision 6</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec6Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=7}">

								<h6>Importer la table de pondération des critères pour la
									décision 7</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec7Import">
									<br />

									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${vanpcoheren7=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=8}">

								<h6>Importer la table de pondération des critères pour la
									décision 8</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec8Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren8=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=9}">

								<h6>Importer la table de pondération des critères pour la
									décision 9</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec9Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${vanpcoheren9=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${vanpcoheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
							<c:if test="${nbdec>=10}">

								<h6>Importer la table de pondération des critères pour la
									décision 10</h6>


								<form method="post" enctype="multipart/form-data"
									action="anpgetDec10Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>
							<form action="anpgetResultImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode ANP validés appuyer
									sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>
						</c:if>

					</c:forEach>


					<form action="ResultatFinalGp1" method="post">
						<br />
						<div class="d-flex justify-content-center">
							<input type="submit" class="btn btn-secondary btn-sm"
								value="Résultats Finaux" />
						</div>
						<br />

					</form>
				</div>
			</div>
		</c:if>

	</c:if>


	<c:if test="${(gp==2)}">
		<div class="jumbotron">
			<div class="container">

				<h2>Informations pour l'importation</h2>
				<form action="Group2InfosImport" method="post">
					<label for="cr">Nombre de niveaux de décision: </label>
					<div class="col-sm-10">
						<input type="number" name="nblevel" id="level" />
					</div>
					<br /> <label for="cr">Nombre de décideurs: </label>
					<div class="col-sm-10">
						<input type="number" name="nbdecideur" id="nbdecr" />
					</div>
					<br /> <input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" />



				</form>

				<h2>Importation groupe 2</h2>
				<p>Fichier à importer :</p>
				<form action="fichMeth2Import" enctype="multipart/form-data"
					method="post">
					<p>
						<input type="file" name="file" id="file" />
					</p>
					<input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" /> <br />
				</form>

				<h2>Choix des méthodes</h2>
				<p>Pour en sélectionner plusieurs, appuyer sur Ctrl</p>

				<br />
				<form action="Methode2" method="post">
					<select name="met2" size="2" multiple>

						<option value="1">Fuzzy Topsis</option>
						<option value="2">Fuzzy AHP Topsis</option>

					</select> <br /> <input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" />

				</form>
			</div>
		</div>

		<c:if test="${nbmet>0}">
			<div class="jumbotron">
				<div class="container">
					<h2>Paramètres</h2>
					<br />
					<h6>Remplir la valeur des niveaux de Fuzzy</h6>
					<form action="valeurFuzzyImport" method="post">
						<table class="w-auto table table-sm" border="1">

							<tr>
								<th>Niveau</th>
								<th>Fuzzy</th>
							</tr>
							<c:forEach var="i" begin="1" end="${nblevel}" step="1">
								<tr>
									<td>L<c:out value="${i}" /></td>
									<td><input class="form-control shadow-none" type="text"
										name="${i}"></td>

								</tr>
							</c:forEach>
						</table>
						<br /> <input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" /> <br />

					</form>


					<br />

					<c:forEach var="i" begin="0" end="${nbmet-1}" step="1">

						<c:if test="${listMet[i]==1}">

							<h5>* FUZZY-TOPSIS</h5>

							<h6>Informations sur les critères. Indiquer si il faut
								maximiser ou minimiser chaque critère.</h6>
							<br />
							<form action="FuzzytopsisInfoImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><select name="${i}" size="1">
													<option>Min
													<option>Max
											</select></td>
										</c:forEach>
									</tr>


								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />


							</form>
							<br />
							<h6>Informations sur les critères. Indiquer le poids de
								chaque critères.</h6>
							<br />
							<form action="FuzzytopsisPoidsImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${i}"></td>
										</c:forEach>
									</tr>


								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />

							</form>
							<form action="FuzzyTopResImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Fuzzy Topsis
									validés appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>
							<br />

						</c:if>
						<c:if test="${listMet[i]==2}">

							<h5>* FUZZY AHP TOPSIS</h5>
							<br />

							<h5>Informations sur les critères. Indiquer si il faut
								maximiser ou minimiser chaque critère.</h5>
							<form action="FuzzyAhptopsisCritImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><select name="${i}" size="1">
													<option>Min
													<option>Max
											</select></td>
										</c:forEach>
									</tr>


								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />

							</form>
							<br />
							<form method="post" enctype="multipart/form-data"
								action="fuzAhptopGetTableCrit1Import">
								<h6>Importer la table de pondération des critères pour le
									décideur 1</h6>
								<br />
								<p>
									<input type="file" name="file" id="file" />
								</p>
								<input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br /> <br />
							</form>
							<br />

							<c:if test="${fahptcoheren2=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren2=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC}"></c:out>
								</p>
							</c:if>
							<c:if test="${nbDecideur>=2}">



								<h6>Importer la table de pondération des critères pour le
									décideur 2</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAhptopGetTableCrit2Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />

								</form>

							</c:if>

							<c:if test="${fahptcoheren3=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren3=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids2}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC2}"></c:out>
								</p>
							</c:if>
							<c:if test="${nbDecideur>=3}">



								<h6>Importer la table de pondération des critères pour le
									décideur 3</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAhptopGetTableCrit3Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${fahptcoheren4=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren4=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids3}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC3}"></c:out>
								</p>
							</c:if>
							<c:if test="${nbDecideur>=4}">



								<h6>Importer la table de pondération des critères pour le
									décideur 4</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAhptopGetTableCrit4Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>

							<c:if test="${fahptcoheren5=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren5=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids4}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC4}"></c:out>
								</p>

							</c:if>
							<c:if test="${nbDecideur>=5}">



								<h6>Importer la table de pondération des critères pour le
									décideur 5</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAhptopGetTableCrit5Import">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>

							</c:if>


							<c:if test="${fahptcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren6=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids5}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC5}"></c:out>
								</p>
							</c:if>

							<c:if test="${nbDecideur>=6}">



								<h6>Importer la table de pondération des critères pour le
									décideur 6</h6>

								<form method="post" enctype="multipart/form-data"
									action="fuzAhptopGetTableCrit6">
									<br />
									<p>
										<input type="file" name="file" id="file" />
									</p>
									<input type="submit" class="btn btn-secondary btn-sm"
										value="Valider" /> <br />
								</form>
							</c:if>

							<c:if test="${fahptcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${fahptcoheren6=='True'}"> C'est cohérent, passer au tableau suivant 

 <p>
									Le poids
									<c:out value="${poids5}"></c:out>
								</p>
								<p>
									RC
									<c:out value="${RC5}"></c:out>
								</p>
							</c:if>


							<p>Obtenir le poids
							<p>
							<form action="FusionPoidsImport" method="post">

								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Obt Poids" /> <br />


							</form>

							<br />
							<p>
								Voici le poids obtenu :
								<c:out value="${poidsfinal}"></c:out>
							</p>
							<br />


							<form action="FuzAHPTopsisResImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Fuzzy Ahp Topsis
									validés appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />
							</form>

							<br />

						</c:if>

					</c:forEach>
				</div>
			</div>
		</c:if>


		<form action="ResultatFinalGp2" method="post">
			<br />
			<div class="d-flex justify-content-center">
				<input type="submit" class="btn btn-secondary btn-sm"
					value="Résultats Finaux" />
			</div>
			<br />

		</form>

	</c:if>



	<c:if test="${(gp==3)}">
		<div class="jumbotron">
			<div class="container">
				<h2>Importation groupe 3</h2>
				<c:if test="${(alreadyImported==true)}">
					<p>
						Fichier pré-importé à utiliser :
						<c:out value="${fileNamespace}"></c:out>
					<form action="fichMethAlreadyImport" 
						method="post">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" /> <br />
					</form>
					</p>

					<p>Sélectionner un autre fichier :</p>
					<form action="fichMethImport" enctype="multipart/form-data"
						method="post">
						<p>
							<input type="file" name="file" id="file" />
						</p>
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" /> <br />
					</form>
				</c:if>

				<c:if test="${(alreadyImported==false)}">
					<p>Fichier à importer :</p>
					<form action="fichMethImport" enctype="multipart/form-data"
						method="post">
						<p>
							<input type="file" name="file" id="file" />
						</p>
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" /> <br />
					</form>
				</c:if>

				<h2>Choix des méthodes</h2>
				<p>Pour en sélectionner plusieurs, appuyer sur Ctrl</p>

				<br />
				<form action="Methode3" method="post">
					<select name="met3" size="4" multiple>

						<option value="1">AHP-Topsis</option>
						<option value="2">Electre</option>
						<option value="3">Topsis</option>
						<option value="4">Prométhée</option>

					</select> <br /> <input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" /> <br />
				</form>
			</div>
		</div>





		<c:if test="${nbmet>0}">
			<div class="jumbotron">
				<div class="container">
					<h2>Paramètres</h2>

					<c:forEach var="i" begin="0" end="${nbmet-1}" step="1">

						<c:if test="${listMet[i]==1}">
							<h5>* AHP-TOPSIS</h5>

							<br />

							<h6>Informations sur les critères. Indiquer si il faut
								maximiser ou minimiser chaque critère.</h6>

							<form action="ahptopsisInfoImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><select name="${i}" size="1">
													<option>Min
													<option>Max
											</select></td>
										</c:forEach>
									</tr>


								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />
							</form>
							<br />

							<h6>Informations sur les critères.</h6>
							<br />

							<form action="ahptopsisPoidsImport" enctype="multipart/form-data"
								method="post">

								<h6>Importer la table de pondération des critères</h6>
								<p>
									<input type="file" name="file" id="file" />
								</p>
								<input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />
							</form>
							<br />

							<c:if test="${coheren=='False'}"> Ce n'est pas cohérent </c:if>
							<c:if test="${coheren=='True'}"> C'est cohérent, passer au tableau suivant 
<br /> Poids des critères : <c:out value="${poids}"></c:out>
							</c:if>
							<form action="AHPTOPResImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode AHP Topsis validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>
							<br />


						</c:if>
						<c:if test="${listMet[i]==2}">
							<h5>* ELECTRE</h5>
							<h6>Indiquer le poids de chaque critères.</h6>
							<br />
							<form action="electrePoidsImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${i}"></td>
										</c:forEach>
									</tr>
								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />
							</form>
							<br />

							<form action="ResultatElectreImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Electre validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>
							<br />
						</c:if>
						<c:if test="${listMet[i]==3}">
							<h5>* TOPSIS</h5>
							<h6>Indiquer si il faut maximiser ou minimiser chaque
								critère.</h6>
							<br />
							<form action="topsisInfoImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><select name="${i}" size="1">
													<option>Min
													<option>Max
											</select></td>
										</c:forEach>
									</tr>
								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />


							</form>
							<br />

							<h6>Indiquer le poids de chaque critères.</h6>
							<br />
							<form action="topsisPoidsImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${i}"></td>
										</c:forEach>
									</tr>

								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />
							</form>


							<br />
							<form action="ResultatTopsisImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Topsis validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" />


							</form>
							<br />
						</c:if>
						<c:if test="${listMet[i]==4}">
							<h5>* PROMETHEE</h5>
							<h6>Informations sur les critères. Indiquer si il faut
								maximiser ou minimiser chaque critère.</h6>
							<br />
							<form action="PrometheeCritImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forTokens items="${minOrMaxs}" delims="," var="m"
											varStatus="stat">
											<td><select name="${stat.index+1}" size="1">
												<c:if test="${m==0}">
													<option selected="selected">Min
													<option>Max
												</c:if>
												<c:if test="${m==1}">
													<option>Min
													<option selected="selected">Max
												</c:if>
											</select></td>
										</c:forTokens>
									</tr>

								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />

							</form>
							<br />

							<br />
							<h6>Informations sur les critères. Indiquer le poids de
								chaque critères.</h6>
							<br />
							<form action="PrometheePoidsImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forTokens items="${weights}" delims="," var="w"
											varStatus="stat">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${stat.index+1}" value="${w}">
											</td>
										</c:forTokens>
									</tr>

								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />


							</form>
							<br />

							<h6>Informations sur les critères. Indiquer la fonction de
								préférence choisie</h6>
							<br />
							<form action="PrometheePrefImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<c:forTokens items="${prefFunctions}" delims="," var="p"
											varStatus="stat">
											<td><select name="${stat.index+1}" size="1">
												<c:if test="${p==0}">
													<option selected="selected">Usual
												</c:if>
												<c:if test="${p!=0}">
													<option>Usual
												</c:if>
												
												<c:if test="${p==3}">
													<option selected="selected">U-Shape
												</c:if>
												<c:if test="${p!=3}">
													<option>U-Shape
												</c:if>
													
												<c:if test="${p==2}">
													<option selected="selected">V-Shape
												</c:if>
												<c:if test="${p!=2}">
													<option>V-Shape
												</c:if>
												
													<option>V-Shape-Ind
													
												<c:if test="${p==1}">
													<option selected="selected">Level
												</c:if>
												<c:if test="${p!=1}">
													<option>Level
												</c:if>
												
													<option>Gaussian
											</select></td>
										</c:forTokens>
									</tr>

								</table>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" />


							</form>
							<br />

Verification choix fonction de préférence : <c:out value="${NumPref}" />

							<br />
							<h6>Renseigner si besoin : P préférence seuil, Q
								indifférence seuil, S propre au type Gussian</h6>
							<br />
							<form action="ValeurPrefImport" method="post">
								<table class="w-auto table table-sm" border="1">
									<tr>
										<td>Critères</td>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><c:out value="${c}" /></td>
										</c:forTokens>
									</tr>
									<tr>
										<td>Q indifférence seuil</td>
										<c:forTokens items="${indiffThresholds}" delims="," var="ith"
											varStatus="stat">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${stat.index}" 
												value="${ith}">
											</td>
										</c:forTokens>
									</tr>
									<tr>
										<td>P préférence seuil</td>
										<c:forTokens items="${prefThresholds}" delims="," var="pth"
											varStatus="stat">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${stat.index+nbcrit}"
												value="${pth}">
											</td>
										</c:forTokens>
									</tr>
									<tr>
										<td>S</td>
										<c:forTokens items="${listcrit}" delims="," var="c"
											varStatus="stat">
											<td><input class="form-control shadow-none"
												type="number" step="0.00001" name="${stat.index+nbcrit*2}"
												value="0"></td>
										</c:forTokens>
									</tr>

								</table>

								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Valider" /> <br />
							</form>
							<br />

							<br />
Verification liste seuil d'indifférence : <c:out value="${listInd}" />
							<br />
Verification liste seuil de préférence : <c:out value="${listPref}" />
							<br />
Verification liste S : <c:out value="${listS}" />


							<form action="PromResultatImport" method="post">
								<br />
								<p>Une fois les paramètres de la méthode Promethée validés
									appuyer sur Calcul</p>
								<br /> <input type="submit" class="btn btn-secondary btn-sm"
									value="Calcul" /> <br />

							</form>


						</c:if>

					</c:forEach>

				</div>
			</div>
		</c:if>


		<form action="ResultatFinalGp3" method="post">
			<br />
			<div class="d-flex justify-content-center">
				<input type="submit" class="btn btn-secondary btn-sm"
					value="Résultats Finaux" />
			</div>
			<br />

		</form>

	</c:if>









	<c:out value="${nbmet}"></c:out>
	<c:out value="${listMet[0]}"></c:out>
	<c:out value="${nbmet}"></c:out>
	<c:out value="${nbmet}"></c:out>

	<!-- 
<h5>* FUZZY-TOPSIS</h5>
	
 -->



	<!-- Optional JavaScript -->
	<!-- jQuery first, then Popper.js, then Bootstrap JS -->
	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
		integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
		integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
		integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
		crossorigin="anonymous"></script>
	<!-- Latest compiled and minified CSS -->



</body>
</html>