<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fonction d'évolution</title>
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
		<h3 class="display-4">Les fonctions d'évolution</h3>
		<p class="lead"></p>
	</div>



	<div class="jumbotron">
		<div class="container">

			<h3>Sélectionner la fonction</h3>

			<form action="SelectFEvolu" method="post">
				<br /> <select name="numFonc" class="form-select">
					<option selected>Sélectionner la fonction d'évolution</option>
					<option value="1">Loi Normale</option>
					<option value="2">Loi de Weibull</option>
					<option value="3">Loi de Poisson</option>
					<option value="4">Intervalle entre 0 et 1</option>
					<option value="5">Intervalle</option>
					<option value="6">Identité</option>
					<option value="7">Fonction Linéaire</option>
					<option value="8">Fonction Exponentielle</option>
					<option value="9">Fonction Logarithmique</option>

				</select> <input type="submit" class="btn btn-secondary btn-sm"
					value="Valider" />

			</form>
		</div>
	</div>


	<div class="container">
		<h2>Informations</h2>
		<c:if test="${(nb<=5)&&(nb>0)}">
			<form method="post" action="NbVariable">
				<div class="form-row">
					<div class="form-group col-md-6">
						<br /> <label for="nombrevar">Nombre de variables
							souhaité</label> <input type="number" class="form-control "
							id="nombrevar" name="nombrevar">
					</div>

				</div>
				<div class="d-flex justify-content-center">
					<input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" />
				</div>
			</form>
		</c:if>
		<c:if test="${(nb>5)}">
			<form method="post" enctype="multipart/form-data"
				action="VectVariable">
				<br />
				<h6>Importer le vecteur de variable</h6>

				<br />
				<p>
					<input type="file" name="file" id="file" />
				</p>
				<div class="d-flex justify-content-center">
					<input type="submit" class="btn btn-secondary btn-sm"
						value="Valider" />
				</div>
			</form>
		</c:if>
		<c:if test="${nb>0}">
			<h2>Paramètres</h2>
			<br />
			<c:if test="${nb==1}">
				<h5>Loi Normale</h5>
				<br />
				<form method="post" action="LoiNormale">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="esperance">Espérance</label> <input type="number"
								class="form-control " id="esperance" name="esperance">
						</div>
						<div class="form-group col-md-6">
							<label for="variance">Variance</label> <input type="number"
								class="form-control " id="variance" name="variance">
						</div>
						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>



			</c:if>
			<c:if test="${nb==2}">
				<h5>Loi de Weibull</h5>

				<form method="post" action="LoiWeibull">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="echelle">Echelle</label> <input type="number"
								class="form-control " id="echelle" name="echelle">
						</div>
						<div class="form-group col-md-6">
							<label for="forme">Forme</label> <input type="number"
								class="form-control " id="forme" name="forme">
						</div>
						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>

			</c:if>
			<c:if test="${nb==3}">
				<h6>Loi de Poisson</h6>
				<form method="post" action="LoiPoisson">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="lambda">Lambda</label> <input type="number"
								class="form-control " id="lambda" name="lambda">
						</div>

						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>
			</c:if>


			<c:if test="${nb==5}">
				<h6>Intervalle</h6>
				<form method="post" action="Intervalle">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="inf">Borne Inf</label> <input type="number"
								class="form-control " id="inf" name="inf">
						</div>
						<div class="form-group col-md-6">
							<label for="sup">Borne Sup</label> <input type="number"
								class="form-control " id="sup" name="sup">
						</div>

						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>
			</c:if>
			<c:if test="${nb==7}">
				<h6>Fonction Linéaire</h6>
				<form method="post" action="Lineaire">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="a">a</label> <input type="number"
								class="form-control " id="a" name="a">
						</div>
						<div class="form-group col-md-6">
							<label for="b">b</label> <input type="number"
								class="form-control " id="b" name="b">
						</div>
						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>
			</c:if>

			<c:if test="${nb==8}">
				<h6>Fonction Exponentielle</h6>
				<form method="post" action="exponentiel">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="a">a</label> <input type="number"
								class="form-control " id="a" name="a">
						</div>
						<div class="form-group col-md-6">
							<label for="c">c</label> <input type="number"
								class="form-control " id="c" name="c">
						</div>
						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>
			</c:if>
			<c:if test="${nb==9}">
				<h6>Fonction Logarithmique</h6>
				<form method="post" action="logarithm">
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="a">a</label> <input type="number"
								class="form-control " id="a" name="a">
						</div>
						<div class="form-group col-md-6">
							<label for="c">c</label> <input type="number"
								class="form-control " id="c" name="c">
						</div>
						<br />

					</div>
					<div class="d-flex justify-content-center">
						<input type="submit" class="btn btn-secondary btn-sm"
							value="Valider" />
					</div>
				</form>
			</c:if>

			<c:if test="${nb>0}">
				<br />
				<form method="post" action="calculEvolu">

					<div class="d-flex justify-content-center">

						<input type="submit" class="btn btn-secondary btn-sm"
							value="Calculer" />
					</div>
				</form>
			</c:if>


		</c:if>

	</div>

	<c:if test="${oui==1}">
		<div class="container">
			<h3>Résultats</h3>

			<br />




			<table class="w-auto table table-sm">

				<tr>
					<c:forEach var="j" begin="0" end="${tai-1}" step="1">
						<td><c:out value="${resultat[j]}"></c:out></td>
					</c:forEach>

				</tr>


			</table>






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












