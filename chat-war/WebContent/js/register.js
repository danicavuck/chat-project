$(document).ready(function(){
		$("#btnReg").click(function(){
			let uname = $("#username").val();
			let pass = $("#password").val();
			console.log(uname)
			console.log(pass)
			
			if( uname =='' || pass ==''){
				$('input[type="text"],input[type="password"]').css("border","2px solid red");
				$('input[type="text"],input[type="password"]').css("box-shadow","0 0 3px red");
				alert("Please fill all fields...!!!!!!");
			}
			else{
			$.ajax({
				url: "rest/users/register",
				type: "POST",
				data: JSON.stringify({"username":uname, "password":pass}),
				contentType: "application/json",
				complete: function(data){
					window.location='./index.html'
					console.log('User registered.');
				}
			
			});
		}
		});
	});

