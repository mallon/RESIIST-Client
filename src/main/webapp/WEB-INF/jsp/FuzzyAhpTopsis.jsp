<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fuzzy AHP Topsis</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>

	<h2 style="text-align: center;">Méthode FUZZY AHP TOPSIS Group
		Decision</h2>

	<form action="InfosFuzAhpTopsis" method="post">

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


	<form action="valeurFuzzy2" method="post">
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
	<form action="FuzzyAhptopsisCrit" method="post">
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
	<form method="post" action="fuzAhptopGetTableCrit1">
		<h5>Table de pondération des critères pour le décideur 1</h5>
		<br />

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
										name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
								</c:when>
								<c:when test="${stat2.count>i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />


	</form>
	<br />

	<c:if test="${coheren2=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren2=='True'}"> C'est cohérent, passer au tableau suivant 

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



		<h5>Table de pondération des critères pour le décideur 2</h5>

		<form method="post" action="fuzAhptopGetTableCrit2">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<c:if test="${stat.index==0}">
						<td>critères</td>
					</c:if>
					<td><c:out value="${c}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="c"
					varStatus="stat2">
					<tr>
						<td><c:out value="${c}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat2.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
									</c:when>
									<c:when test="${stat2.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
			<br /> <input type="submit" class="btn btn-outline-primary"
				value="Valider" /> <br />
		</form>

	</c:if>

	<c:if test="${coheren3=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren3=='True'}"> C'est cohérent, passer au tableau suivant 

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



		<h5>Table de pondération des critères pour le décideur 3</h5>

		<form method="post" action="fuzAhptopGetTableCrit3">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<c:if test="${stat.index==0}">
						<td>critères</td>
					</c:if>
					<td><c:out value="${c}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="c"
					varStatus="stat2">
					<tr>
						<td><c:out value="${c}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat2.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
									</c:when>
									<c:when test="${stat2.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
			<br /> <input type="submit" class="btn btn-outline-primary"
				value="Valider" /> <br />
		</form>

	</c:if>

	<c:if test="${coheren4=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren4=='True'}"> C'est cohérent, passer au tableau suivant 

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



		<h5>Table de pondération des critères pour le décideur 4</h5>

		<form method="post" action="fuzAhptopGetTableCrit4">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<c:if test="${stat.index==0}">
						<td>critères</td>
					</c:if>
					<td><c:out value="${c}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="c"
					varStatus="stat2">
					<tr>
						<td><c:out value="${c}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat2.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
									</c:when>
									<c:when test="${stat2.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
			<br /> <input type="submit" class="btn btn-outline-primary"
				value="Valider" /> <br />
		</form>

	</c:if>

	<c:if test="${coheren5=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren5=='True'}"> C'est cohérent, passer au tableau suivant 

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



		<h5>Table de pondération des critères pour le décideur 5</h5>

		<form method="post" action="fuzAhptopGetTableCrit5">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<c:if test="${stat.index==0}">
						<td>critères</td>
					</c:if>
					<td><c:out value="${c}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="c"
					varStatus="stat2">
					<tr>
						<td><c:out value="${c}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat2.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
									</c:when>
									<c:when test="${stat2.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
			<br /> <input type="submit" class="btn btn-outline-primary"
				value="Valider" /> <br />
		</form>

	</c:if>


	<c:if test="${coheren6=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren6=='True'}"> C'est cohérent, passer au tableau suivant 

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



		<h5>Table de pondération des critères pour le décideur 6</h5>

		<form method="post" action="fuzAhptopGetTableCrit6">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="c" varStatus="stat">
					<c:if test="${stat.index==0}">
						<td>critères</td>
					</c:if>
					<td><c:out value="${c}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="c"
					varStatus="stat2">
					<tr>
						<td><c:out value="${c}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat2.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="1,1,1" />
									</c:when>
									<c:when test="${stat2.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat2.index*nbcrit)+(i-1)}" value="0,0,0" />
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
			<br /> <input type="submit" class="btn btn-outline-primary"
				value="Valider" /> <br />
		</form>

	</c:if>

	<c:if test="${coheren6=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren6=='True'}"> C'est cohérent, passer au tableau suivant 

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
	<form action="FusionPoids" method="post">

		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Obt Poids" /> <br />




	</form>


	<form action="TableFuzAHPTopsis" method="post">
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