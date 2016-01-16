$(document).ready(function(){
  openPage("home.html");
  setEventHandlers();
});

function setEventHandlers(){
  $('#info').click(function(){ openPage("info.html") } );
  $('#home').click(function(){ openPage("home.html") } );
}

function openPage(page){
  $('#content').load(page);
  location.hash = page;
}

function addFooter(msg){
  $('#footer').html($('#footer').html() + "<p>FOOTER: " + msg + "</p>");
}
