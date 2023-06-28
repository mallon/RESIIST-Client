<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fonctions d'agrégation</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>

	<nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
		<a class="navbar-brand" href="#">Navbar</a>
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
				<li class="nav-item"><a class="nav-link" href="#">Link</a></li>
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
							class="dropdown-item" href="/ahptopsis">AHP-Topsis</a> <a
							class="dropdown-item" href="/anpGetForm">ANP</a> <a
							class="dropdown-item" href="/electre">Electre</a> <a
							class="dropdown-item" href="/fuzzyAhp">Fuzzy-AHP</a> <a
							class="dropdown-item" href="/FuzzyAhpTopsis">Fuzzy-AHP-Topsis-Group</a>
						<a class="dropdown-item" href="/fuzzyAnp">Fuzzy-ANP</a> <a
							class="dropdown-item" href="/fuzzyTopsis">Fuzzy-Topsis</a> <a
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

	<br />
	<br />

	<div
		class="pricing-header px-3 py-3 pt-md-5 pb-md-4 mx-auto text-center">
		<h3 class="display-4">Les fonctions d'agrégation</h3>
		<div class="row">
			<div class="col">
				<p class="lead">
					Les fonctions d'agrégations suivantes prennent en entrée un
					vecteur. <br>
				</p>

				<div class="row">
					<div class="col ">

						<div class="d-flex justify-content-between">
							<p class="lead">* Somme, Produit</p>
						</div>
						<div class="d-flex justify-content-between">
							<p class="lead">* Moyenne, Médiane</p>
						</div>
						<div class="d-flex justify-content-between">
							<p class="lead">* Minimum, Maximum</p>
						</div>


					</div>

					<div class="col ">


						<div class="d-flex justify-content-between">
							<p class="lead">* Moyenne (dé)croissante</p>
						</div>
						<div class="d-flex justify-content-between">
							<p class="lead">* Minimum (dé)croissant</p>
						</div>
						<div class="d-flex justify-content-between">
							<p class="lead">* Maximum (dé)croissant</p>
						</div>



					</div>
				</div>
			</div>

			<div class="col justify-content-start">
				<p class="lead">
					Les fonctions d'agrégations suivantes prennent en entrée un
					tableau. <br>
				</p>

				<div class="d-flex justify-content-center">
					<p class="lead">* Somme pondérée</p>
				</div>
				<div class="d-flex justify-content-center">
					<p class="lead">* Produit pondéré 1</p>
				</div>
				<div class="d-flex justify-content-center">
					<p class="lead">* Produit pondéré 2</p>
				</div>


			</div>
		</div>
	</div>


	<div class="jumbotron">
		<div class="container">

			<h3>Sélectionner la fonction</h3>

			<form action="SelectFAgreg" method="post">
				<br /> <select name="numFonc" class="form-select">
					<option selected>Sélectionner la fonction d'agrégation</option>
					<option value="1">Somme</option>
					<option value="2">Moyenne</option>
					<option value="3">Médiane</option>
					<option value="4">Minimum</option>
					<option value="5">Maximum</option>
					<option value="6">Produit</option>
					<option value="7">Moyenne croissante</option>
					<option value="8">Moyenne décroissante</option>
					<option value="9">Minimum croissante</option>
					<option value="10">Minimum décroissante</option>
					<option value="11">Maximum croissante</option>
					<option value="12">Maximum décroissante</option>
					<option value="13">Somme pondérée</option>
					<option value="14">Produit Pondéré 1</option>
					<option value="15">Produit Pondéré 2</option>


				</select> <input type="submit" class="btn btn-secondary btn-sm"
					value="Valider" />

			</form>

			<c:if test="${nb>0}">

				<br />
				<br />
				<c:if test="${nb<13}">
					<h3>Importer le vecteur :</h3>

					<form method="post" enctype="multipart/form-data"
						action="ImporVect4Agreg">

						<br />
						<p>
							<input type="file" name="file" id="file" />
						</p>

						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />

					</form>
					<br />
				</c:if>



				<c:if test="${nb>12}">
					<h3>Importer la table :</h3>

					<form method="post" enctype="multipart/form-data"
						action="ImporTab4Agreg">

						<br />
						<p>
							<input type="file" name="file" id="file" />
						</p>

						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />

					</form>
					<br />
				</c:if>


			</c:if>



			<c:if test="${nb>0}">
				<form method="post" action="calculAgreg">

					<div class="d-flex justify-content-center">

						<input type="submit" class="btn btn-secondary btn-sm"
							value="Calculer" />
					</div>
				</form>
			</c:if>

		</div>
	</div>


	<br />
	<c:if test="${oui==1}">
		<div class="container">
			<h3>Résultats</h3>

			<br />


			<c:if test="${(nb<13)&&(oui==1)}">

				<div class="d-flex justify-content-center">
					<h4>
						<c:out value="${resultat}"></c:out>
					</h4>
				</div>

			</c:if>

			<c:if test="${((nb==13)||(nb==14))&&(oui==1)}">
				<table class="w-auto table table-sm">

					<tr>
						<c:forEach var="j" begin="0" end="${tai-1}" step="1">
							<td><c:out value="${vectRes[j]}"></c:out></td>
						</c:forEach>

					</tr>


				</table>


			</c:if>



			<c:if test="${(nb==15)&&(oui==1)}">

				<div class="d-flex justify-content-center">

					<table class="w-auto table table-sm">
						<c:forEach var="i" begin="0" end="${nbl-1}" step="1">
							<tr>
								<c:forEach var="j" begin="0" end="${nbCol-1}" step="1">
									<td><c:out value="${tableauRes[i][j]}"></c:out></td>
								</c:forEach>

							</tr>
						</c:forEach>

					</table>

				</div>

			</c:if>
		</div>
	</c:if>


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



</body>
</html>












