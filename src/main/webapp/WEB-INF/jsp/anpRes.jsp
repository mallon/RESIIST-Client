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
	Le resultat
	<c:out value="${scores}" />


	<table>




		<c:forTokens items="${listdec}" delims="," var="dec" varStatus="stat4">
			<tr>
				<c:forTokens items="${listscore}" delims="," var="sco"
					varStatus="stat">
					<c:if test="${stat4.index==stat.index}">

						<td><c:out value="${dec}" /></td>
						<td><c:out value="${sco}" /></td>

					</c:if>

				</c:forTokens>
			</tr>
		</c:forTokens>

	</table>

	<c:forTokens items="${listdec}" delims="," var="dec" varStatus="stat5">

		<c:if test="${stat5.index==solution}"> La meilleure décision est : <c:out
				value="${dec}" />
		</c:if>

	</c:forTokens>



</body>
</html>