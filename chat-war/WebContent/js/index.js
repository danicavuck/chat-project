$(document).ready(function() {
	$("#btnLogin").click(function() {
		let username=$('#username').val();
		let password=$('#password').val();

		$.ajax({
			  url:'rest/users/login',
			  type:"POST",
			  data:JSON.stringify({"username": username, "password" : password}),
			  contentType:"application/json; charset=utf-8",
			  success: function(data){
				  	sessionStorage.setItem('username', data);
				  	window.location='./home.html';
			  },
			  error: function(){
				alert('Neispravni podaci');
			  }
			});
	});
});
	