<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Topsis</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>

	<h2 style="text-align: center;">Méthode AHP-TOPSIS</h2>

	<form action="ahptopsisSaveDetails" method="post">

		<label for="cr">Nombre de critères: </label>
		<div class="col-sm-10">
			<input type="number" name="nbcrit" id="cr" />
		</div>
		<br /> <label for="de"> Nombre de décisions: </label>
		<div class="col-sm-10">
			<input type="number" name="nbdec" id="de" />
		</div>
		<br /> <label for="critlistInline">Liste des critères: </label>
		<div class="col-sm-10">
			<input type="text" name="critlist" id="critlistInline" /> <small
				id="critlistInline" class="text-muted"> séparés d'une
				virgule </small>
		</div>
		<br /> <label for="declistInline">Liste des décisions: </label>
		<div class="col-sm-10">
			<input type="text" name="declist" id="declistInline" /> <small
				id="declistInline" class="text-muted"> séparés d'une virgule
			</small>
		</div>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />

	</form>


	<h5>Informations sur les critères. Indiquer si il faut maximiser
		ou minimiser chaque critère.</h5>
	<form action="ahptopsisInfo" method="post">
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

	<h5>Informations sur les critères. Remplir la table de pondération
		des critères pour obtenir leur poids.</h5>

	<form action="ahptopsisPoids" method="post">

		<h5>Table de pondération des critères</h5>

		<table class="w-auto table table-sm" border="1">
			<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
				<c:if test="${stat.index==0}">
					<td>critères</td>
				</c:if>
				<td><c:out value="${c}" /></td>
			</c:forTokens>
			<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat2">
				<tr>
					<td><c:out value="${c}" /></td>
					<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
						<td><c:choose>
								<c:when test="${stat2.count==i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat2.index*nbcrit)+(i-1)}" value="1" />
								</c:when>
								<c:when test="${stat2.count>i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat2.index*nbcrit)+(i-1)}" value="0" />
								</c:when>
								<c:otherwise>
									<input class="form-control shadow-none" type="text"
										name="${(stat2.index*nbcrit)+(i-1)}">
								</c:otherwise>
							</c:choose></td>
					</c:forEach>

				</tr>
			</c:forTokens>
		</table>
		<br /> <input type="submit" value="Valider" /> <br />


	</form>


	<c:if test="${coheren=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren=='True'}"> C'est cohérent, passer au tableau suivant 
<br /> Poids des critères : <c:out value="${poids}"></c:out>
	</c:if>




	<form method="post" action="ahptopsisGetTable">




		<h5>Table multicritère</h5>
		<br />

		<table class="w-auto table table-sm" border="1">
			<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
				<c:if test="${stat.index==0}">
					<td>Table Multicritère</td>
				</c:if>
				<td><c:out value="${c}" /></td>
			</c:forTokens>

			<c:forTokens items="${listdec}" delims="," var="d" varStatus="stat2">
				<tr>
					<td><c:out value="${d}" /></td>
					<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
						<td><input class="form-control shadow-none" type="text"
							name="${(stat2.index*nbcrit)+(i-1)}"></td>
					</c:forEach>

				</tr>
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