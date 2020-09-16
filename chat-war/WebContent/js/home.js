$(document).ready(function(){
	
	  var socketUser;
	  let username = sessionStorage.getItem('username');
	   
	    var hostUser = "ws://localhost:8080/chat-war/ws/login/" + username;
	    try{
		    socketUser = new WebSocket(hostUser);
		    console.log('connect: Socket Status: '+socketUser.readyState);
		
		    socketUser.onopen = function(){
		   	 console.log('onopen: Socket Status: '+socketUser.readyState+' (open)');
		    }
		
		    socketUser.onmessage = function(msg){
		    	  $('#loggedInTable').append('<tr id='+msg.data + '><th scope = "row" >' + "*" + "</th>" + "<td>" + msg.data + "</td></tr>");		   	
		    }
		
		    socketUser.onclose = function(){
		    	socketUser = null;
		    }
		    
		
		} catch(exception){
		   console.log('Error'+exception);
		}

    var socket;
	let usrName = sessionStorage.getItem('username');

    var host = "ws://localhost:8080/chat-war/ws/" + usrName;
    try{
	    socket = new WebSocket(host);
	    console.log('connect: Socket Status: '+socket.readyState);
	
	    socket.onopen = function(){
	   	 console.log('onopen: Socket Status: '+socket.readyState+' (open)');
	    }
	
	    socket.onmessage = function(msg){
	     console.log(msg.data);
	     $("#msgPanel").append('<p>' + msg.data + '</p>');
	    }
	
	    socket.onclose = function(){
	    	socket = null;
	    }
	    
	
	} catch(exception){
	   console.log('Error'+exception);
	}
	
	 var socketLogout;
	  let username2 = sessionStorage.getItem('username');
	   
	    var hostLogout = "ws://localhost:8080/chat-war/ws/logout/" + username2;
	    try{
	    	socketLogout = new WebSocket(hostLogout);
		    console.log('connect: Socket Status: '+socketLogout.readyState);
		
		    socketLogout.onopen = function(){
		   	 console.log('onopen: Socket Status: '+socketLogout.readyState+' (open)');
		    }
		
		    socketLogout.onmessage = function(msg){
		    	$('#'+msg.data).remove();
		    }
		
		    socketLogout.onclose = function(){
		    	socketUser = null;
		    }
		    
		
		} catch(exception){
		   console.log('Error'+exception);
		}

	
	let loggedUser = sessionStorage.getItem('username');
	
	
	
	
	$.ajax({
        url: 'rest/users/loggedIn',
        type: 'GET',
        success: function(data) {
        	var num = 0;
            for(var i = 0; i < data.length; i++){
               var temp = data[i];
               console.log(temp);
               if(data[i] !== loggedUser){ 
            	   	num = num + 1;
                    $('#loggedInTable').append('<tr id='+temp + '><th scope = "row" >' + num + "</th>" + "<td>" + temp + "</td></tr>");
               }
            }	
        },
        error: function(){
            console.log('Cannot get messages');
        }
    });
	
	
	
	//dobavljanje svih poruka ulogovanog korisnika
	
	$.ajax({
        url: 'rest/messages/' + loggedUser,
        type: 'GET',
        success: function(data) {
            for(var i = 0; i < data.length; i++){
               var sender = data[i].sender;
               var reciever = data[i].reciever;
               var content = data[i].content;
               $("#msgPanel").append('<p>' + sender + " : " + content + '<\p>');
             
            }
        },
        error: function(){
            console.log('Cannot get messages');
        }
    });
	
	
	//dobavljanje svih registrovanih korisnika
    
	$.ajax({
        url: 'rest/users/registered',
        type: 'GET',
        success: function(data) {
        	var num = 0;
            for(var i = 0; i < data.length; i++){
               var temp = data[i];
               if(data[i] !== loggedUser){ 
            	   	num = num + 1;
                    $('#usersTable').append('<tr><th scope = "row">'+ num + "</th>" + "<td>" + temp + "</td></tr>");
               }
            }	
        },
        error: function(){
            console.log('Cannot get all logged in users');
        }
    });

    $('#btnLogout').click(function(){
        let username = sessionStorage.getItem('username');
        console.log(username)
        
        $.ajax({
            url:'rest/users/loggedIn/'+username,
            type:"DELETE",
            success: function(){
                   window.location='./index.html';
                   sessionStorage.removeItem(username);
            },
            error: function(){
              alert('Error');
            }
          });
    });

   
    $('#btnSend').click(function(){
        let reciever = $('#reciever').val();
        let content = $('#content').val();
        let sender = loggedUser;
        
        if(reciever === ''){
            $.ajax({
                url: 'rest/messages/all',
                type: 'POST',
                data: JSON.stringify({sender,reciever,content}),
                contentType:"application/json; charset=utf-8", 
                success: function(){
                    console.log('Message sent successfully');
                    $("#msgPanel").append('<p>' + sender + ": " + content + '<\p>');
                    $('#content').val('');
                },
                error: function(err){
                    console.log(err);
                }
            });
        } else {
            $.ajax({
                url: 'rest/messages/'+reciever,
                type: 'POST',
                data: JSON.stringify({sender,reciever,content}),
                contentType:"application/json; charset=utf-8", 
                success: function(){
                    console.log('Message sent successfully');
                    $("#msgPanel").append('<p>' + sender + ':' + content + '</p>');
                    $('#content').val('');
                },
                error: function(err){
                    console.log(err);
                }
            });
        }

    });


});