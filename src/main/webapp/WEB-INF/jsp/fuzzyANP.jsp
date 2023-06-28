<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ANP</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

</head>
<body>
	<h2 style="text-align: center;">Méthode FUZZY-ANP</h2>

	<form method="post" action="fuzAnpSaveDetails">
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
				id="declistInline" class="text-muted"> séparés d'une virgule
			</small>
		</div>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />
	</form>
	<br />

	<p>
		Il y' a <strong> ${nbcrit}</strong> critères : <strong>${listcrit}</strong>
	</p>
	<br />
	<p>
		Il y' a <strong> ${nbdec}</strong> décisions : <strong>${listdec}</strong>
	</p>



	<h3>Table de pondération</h3>
	<table class="w-auto table table-striped" border="1">
		<thead class="thead-dark">

			<tr>

				<th>Degré de préférence</th>
				<th>Pondération</th>
				<th>Valeur inverses</th>
			</tr>
		</thead>
		<tr>

			<td>Même ordre de grandeur</td>
			<td>1</td>
			<td>1</td>
		</tr>
		<tr>

			<td>Légèrement meilleur</td>
			<td>3</td>
			<td>1/3 = 0.333</td>

		</tr>
		<tr>

			<td>Plus important</td>
			<td>5</td>
			<td>1/5 = 0.2</td>
		</tr>
		<tr>

			<td>Beaucoup plus important</td>
			<td>7</td>
			<td>1/7 = 0.143</td>
		</tr>
		<tr>

			<td>Nettement plus important</td>
			<td>9</td>
			<td>1/9= 0.111</td>
		</tr>
		<tr>

			<td>Valeurs intermédiaires</td>
			<td>2, 4, 6, 8</td>
			<td>0.5, 0.25, 0.167, 0.125</td>
		</tr>




	</table>




	<form method="post" action="fuzAnpGetTableCrit">
		<h5>Table de pondération des critères</h5>
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
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />


	</form>


	<c:if test="${coheren=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren=='True'}"> C'est cohérent, passer au tableau suivant </c:if>


	<p>
		Remplir les
		<c:out value="${nbcrit}" />
		tableaux où on compare entre elles les décisions pour chaque critère
	</p>
	<br />
	<h5>Table de pondération des décisions pour le critère 1</h5>

	<form method="post" action="fuzAnpgetCrit1">
		<table class="w-auto table table-sm" border="1">
			<c:forTokens items="${listdec}" delims="," var="dec"
				varStatus="stat4">
				<c:if test="${stat4.index==0}">
					<td><c:out value="Critere 1" /></td>
				</c:if>
				<td><c:out value="${dec}" /></td>
			</c:forTokens>
			<c:forTokens items="${listdec}" delims="," var="dec"
				varStatus="stat5">
				<tr>
					<td><c:out value="${dec}" /></td>
					<c:forEach var="i" begin="1" end="${nbdec}" step="1">
						<td><c:choose>
								<c:when test="${stat5.count==i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbdec)+(i-1)}" value="1" />
								</c:when>
								<c:when test="${stat5.count>i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbdec)+(i-1)}" value="0" />
								</c:when>
								<c:otherwise>
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbdec)+(i-1)}">
								</c:otherwise>
							</c:choose></td>
					</c:forEach>

				</tr>
			</c:forTokens>
		</table>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />
	</form>


	<c:if test="${coheren2=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=2}">

		<h5>Table de pondération des décisions pour le critère 2</h5>

		<form method="post" action="fuzAnpgetCrit2">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 2" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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
	<c:if test="${coheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=3}">

		<h5>Table de pondération des décisions pour le critère 3</h5>

		<form method="post" action="fuzAnpgetCrit3">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 3" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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
	<c:if test="${coheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=4}">

		<h5>Table de pondération des décisions pour le critère 4</h5>

		<form method="post" action="fuzAnpgetCrit4">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 4" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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
	<c:if test="${coheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=5}">

		<h5>Table de pondération des décisions pour le critère 5</h5>

		<form method="post" action="fuzAnpgetCrit5">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 5" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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
	<c:if test="${coheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=6}">

		<h5>Table de pondération des décisions pour le critère 6</h5>

		<form method="post" action="fuzAnpgetCrit6">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 6" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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

	<c:if test="${coheren7=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=7}">

		<h5>Table de pondération des décisions pour le critère 7</h5>

		<form method="post" action="fuzAnpgetCrit7">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 7" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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


	<c:if test="${coheren8=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=8}">

		<h5>Table de pondération des décisions pour le critère 8</h5>

		<form method="post" action="fuzAnpgetCrit8">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 8" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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

	<c:if test="${coheren9=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=9}">

		<h5>Table de pondération des décisions pour le critère 9</h5>

		<form method="post" action="fuzAnpgetCrit9">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 9" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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

	<c:if test="${coheren10=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${coheren10=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbcrit>=10}">

		<h5>Table de pondération des décisions pour le critère 10</h5>

		<form method="post" action="fuzAnpgetCrit10">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Critere 10" /></td>
					</c:if>
					<td><c:out value="${dec}" /></td>
				</c:forTokens>
				<c:forTokens items="${listdec}" delims="," var="dec"
					varStatus="stat5">
					<tr>
						<td><c:out value="${dec}" /></td>
						<c:forEach var="i" begin="1" end="${nbdec}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbdec)+(i-1)}">
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

	<p>
		Remplir les
		<c:out value="${nbdec}" />
		tableaux où on compare entre eux les critères pour chaque décision
	</p>
	<br />

	<h5>Table de pondération des critères pour la décision 1</h5>

	<form method="post" action="fuzAnpgetDec1">
		<table class="w-auto table table-sm" border="1">
			<c:forTokens items="${listcrit}" delims="," var="crit"
				varStatus="stat4">
				<c:if test="${stat4.index==0}">
					<td><c:out value="Decision 1" /></td>
				</c:if>
				<td><c:out value="${crit}" /></td>
			</c:forTokens>
			<c:forTokens items="${listcrit}" delims="," var="crit"
				varStatus="stat5">
				<tr>
					<td><c:out value="${crit}" /></td>
					<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
						<td><c:choose>
								<c:when test="${stat5.count==i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
								</c:when>
								<c:when test="${stat5.count>i}">
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
								</c:when>
								<c:otherwise>
									<input class="form-control shadow-none" type="text"
										name="${(stat5.index*nbcrit)+(i-1)}">
								</c:otherwise>
							</c:choose></td>
					</c:forEach>

				</tr>
			</c:forTokens>
		</table>
		<br /> <input type="submit" class="btn btn-outline-primary"
			value="Valider" /> <br />
	</form>


	<c:if test="${anpcoheren1=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren1=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=2}">

		<h5>Table de pondération des critères pour la décision 2</h5>


		<form method="post" action="fuzAnpgetDec2">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 2" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren2=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren2=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=3}">

		<h5>Table de pondération des critères pour la décision 3</h5>


		<form method="post" action="fuzAnpgetDec3">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 3" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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


	<c:if test="${anpcoheren3=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren3=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=4}">

		<h5>Table de pondération des critères pour la décision 4</h5>


		<form method="post" action="fuzAnpgetDec4">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 4" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren4=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren4=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=5}">

		<h5>Table de pondération des critères pour la décision 5</h5>


		<form method="post" action="fuzAnpgetDec5">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 5" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren5=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren5=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=6}">

		<h5>Table de pondération des critères pour la décision 6</h5>


		<form method="post" action="fuzAnpgetDec6">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 6" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren6=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren6=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=7}">

		<h5>Table de pondération des critères pour la décision 7</h5>


		<form method="post" action="fuzAnpgetDec7">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 7" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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


	<c:if test="${anpcoheren7=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren7=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=8}">

		<h5>Table de pondération des critères pour la décision 8</h5>


		<form method="post" action="fuzAnpgetDec8">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 8" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren8=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren8=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=9}">

		<h5>Table de pondération des critères pour la décision 9</h5>


		<form method="post" action="fuzAnpgetDec9">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 9" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<c:if test="${anpcoheren9=='False'}"> Ce n'est pas cohérent </c:if>
	<c:if test="${anpcoheren9=='True'}"> C'est cohérent, passer au tableau suivant </c:if>
	<c:if test="${nbdec>=10}">

		<h5>Table de pondération des critères pour la décision 10</h5>


		<form method="post" action="fuzAnpgetDec10">
			<table class="w-auto table table-sm" border="1">
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat4">
					<c:if test="${stat4.index==0}">
						<td><c:out value="Decision 10" /></td>
					</c:if>
					<td><c:out value="${crit}" /></td>
				</c:forTokens>
				<c:forTokens items="${listcrit}" delims="," var="crit"
					varStatus="stat5">
					<tr>
						<td><c:out value="${crit}" /></td>
						<c:forEach var="i" begin="1" end="${nbcrit}" step="1">
							<td><c:choose>
									<c:when test="${stat5.count==i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="1" />
									</c:when>
									<c:when test="${stat5.count>i}">
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}" value="0" />
									</c:when>
									<c:otherwise>
										<input class="form-control shadow-none" type="text"
											name="${(stat5.index*nbcrit)+(i-1)}">
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

	<form method="post" action="fuzAnpgetResult">
		<input type="submit" class="btn btn-outline-success"
			value="Obtenir les résultats" />
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