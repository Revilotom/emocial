#$ ->
#  $.get "/persons", (persons) ->
#    $.each persons, (index, person) ->
#      $("#persons").append $("<li>").text person.name

#$ ->
#  $("#logoutButton").click ->
#    console.log "ASDADAS"
#    $.ajax({
#      type: "POST",
#      url: "/logout",
#      data: "{}",
#      success: () -> console.log "ASD",
#      dataType: "application/json"
#    });
