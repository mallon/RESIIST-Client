<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fuzzy Topsis</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>

	<h2 style="text-align: center;">Méthode Fuzzy TOPSIS</h2>

	<form action="FuzTopsisGetTable" method="post">

		<label for="cr">Nombre de niveaux de décision: </label>
		<div class="col-sm-10">
			<input type="number" name="nblevel" id="level" />
		</div>
		<br /> <label for="cr">Nombre de décideurs: </label>
		<div class="col-sm-10">
			<input type="number" name="nbdecideur" id="nbdecr" />
		</div>
		<br /> <label for="cr">Nombre de critères: </label>
		<div class="col-sm-10">
			<input type="number" name="nbcrit" id="cr" />
		</div>
		<br /> <label for="de"> Nombre d'alternatives: </label>
		<div class="col-sm-10">
			<input type="number" name="nbdec" id="de" />
		</div>
		<br /> <label for="critlistInline">Liste des critères: </label>
		<div class="col-sm-10">
			<input type="text" name="critlist" id="critlistInline" /> <small
				id="critlistInline" class="text-muted"> séparés d'une
				virgule </small>
		</div>
		<br /> <label for="declistInline">Liste des alternatives: </label>
		<div class="col-sm-10">
			<input type="text" name="declist" id="declistInline" /> <small
				id="declistInline" class="text-muted"> séparés d'une virgule
			</small>
		</div>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />

	</form>
	<br />
	<form action="valeurFuzzy" method="post">
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
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />

	</form>

	<br />
	<h5>Informations sur les critères. Indiquer si il faut maximiser
		ou minimiser chaque critère.</h5>
	<form action="FuzzytopsisInfo" method="post">
		<table class="w-auto table table-sm" border="1">
			<tr>
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
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
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />

	</form>
	<br />
	<h5>Informations sur les critères. Indiquer le poids de chaque
		critères.</h5>
	<form action="FuzzytopsisPoids" method="post">
		<table class="w-auto table table-sm" border="1">
			<tr>
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<td><c:out value="${c}" /></td>
				</c:forTokens>
			</tr>
			<tr>
				<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
					<td><input class="form-control shadow-none" type="number"
						step="0.00001" name="${i}"></td>
				</c:forEach>
			</tr>


		</table>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />

	</form>



	<form action="Table" method="post">
		<table class="w-auto table table-sm" border="1">

			<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
				<c:if test="${stat.index==0}">
					<td>Table Multicritère</td>
				</c:if>
				<td><c:out value="${c}" /></td>
			</c:forTokens>

			<c:forTokens items="${listdec}" delims="," var="d" varStatus="stat2">
				<c:forEach var="j" begin="1" end="${nbDecideur}" step="1">
					<tr>
						<td><c:out value="${d}" /> Decideur <c:out value="${ j }" />
						</td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><input class="form-control shadow-none" type="number"
								name="${(stat2.index*nbDecideur*nbcrit)+((j-1)*nbcrit)+(i-1)}" />
							</td>
						</c:forEach>

					</tr>
				</c:forEach>
			</c:forTokens>





		</table>

		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />




	</form>















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