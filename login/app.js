$( ".input" ).focusin(function() {
  $( this ).find( "span" ).animate({"opacity":"0"}, 200);
});

$( ".input" ).focusout(function() {
  $( this ).find( "span" ).animate({"opacity":"1"}, 300);
});

 $(".login").submit(function(){
  $(this).find(".submit i").removeAttr('class').addClass("fa fa-check").css({"color":"#fff"});
  $(".submit").css({"background":"#2ecc71", "border-color":"#2ecc71"});
  $(".feedback").show().animate({"opacity":"1", "bottom":"-80px"}, 400);
  $("input").css({"border-color":"#2ecc71"});
  return false;
});
$(".submit").click(function() {
	document.location.href='../individuals/index.html'
});

fetch("localhost:8080/authorize", {
	method: "POST",
	cache: "no-cache",
	redirect: "manual",
	body: {
		username: $('.emaill'),
		password: $('.paswordd'),
	}
})
.then(response => {
	if (response.status === 302) {
		window.location.href = response.text()
	} else {
		throw response.text()
	}
})
.catch(err => {
	//invalid Login
	//
})
