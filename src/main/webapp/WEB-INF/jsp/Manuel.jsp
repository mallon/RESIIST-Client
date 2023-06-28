<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
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
			<br />
			<h2>Séléction méthode</h2>

			<form action="GroupMetManuel" method="post">
				<br /> <select name="groupMetman" class="form-select"
					aria-label="Default select example">

					<option selected>Selectionner la méthode</option>
					<option value="ahp">AHP</option>
					<option value="AHPTopsis">AHP Topsis</option>
					<option value="anp">ANP</option>
					<option value="electre">Electre</option>
					<option value="fuzzyAHP">Fuzzy-AHP</option>
					<option value="FuzzyAhpTopsis">Fuzzy-AHP-Topsis-Group
						Decision</option>
					<option value="fuzzyANP">Fuzzy-ANP</option>
					<option value="fuzzyTopsis">Fuzzy-Topsis</option>
					<option value="promethee">Prométhée</option>
					<option value="topsis">Topsis</option>


				</select> <br /> <input type="submit" class="btn btn-secondary btn-sm"
					value="Valider" />


			</form>


		</div>
	</div>





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