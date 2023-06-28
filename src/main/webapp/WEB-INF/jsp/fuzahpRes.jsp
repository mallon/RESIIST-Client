<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Resultat</title>
</head>
<body>
	<h3 style="text-align: center;">Méthode Fuzzy-AHP</h3>

	<c:out value="${score}" />


	<table>

		<thead class="thead-dark">

			<tr>
				<th>Décision</th>
				<th>Score</th>
			</tr>
		</thead>



		<c:forTokens items="${listdec}" delims="," var="dec" varStatus="stat4">
			<tr>
				<c:forTokens items="${laliste}" delims="," var="sco"
					varStatus="stat">
					<c:if test="${stat4.index==stat.index}">

						<td><c:out value="${dec}" /></td>
						<td><c:out value="${sco}" /></td>

					</c:if>
				</c:forTokens>
			</tr>
		</c:forTokens>

	</table>
	<br />
	<c:forTokens items="${listdec}" delims="," var="dec" varStatus="stat5">

		<c:if test="${stat5.index==solution}"> La meilleure décision est : <c:out
				value="${dec}" />
		</c:if>

	</c:forTokens>



</body>
</html>