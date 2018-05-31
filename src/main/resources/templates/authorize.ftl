<#--

    The MIT License (MIT)

    Copyright (c) 2016-present IxorTalk CVBA

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

-->
<html>
<head>
<link rel="stylesheet" href="css/bootstrap.min.css"/>
</head>
<body>
	<div class="container">
		<h2>Please Confirm</h2>

		<p>
			Do you authorize "${authorizationRequest.clientId}" at "${authorizationRequest.redirectUri}" to access your protected resources
			with scope ${authorizationRequest.scope?join(", ")}.
		</p>
		<form id="confirmationForm" name="confirmationForm"
			action="../oauth/authorize" method="post">
            <input type="hidden" name="scope.openid" value="true">
			<input name="user_oauth_approval" value="true" type="hidden" />
			<input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<button class="btn btn-primary" type="submit">Approve</button>
		</form>
		<form id="denyForm" name="confirmationForm"
			action="../oauth/authorize" method="post">
			<input name="user_oauth_approval" value="false" type="hidden" />
			<input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<button class="btn btn-primary" type="submit">Deny</button>
		</form>
	</div>
</body>
</html>