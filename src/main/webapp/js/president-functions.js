$(function(){ 

	$.ajaxSetup({ cache: false });
	
	var operation = "A"; 
	var selected_index = -1;
	var tbPresidents;
	
	// START SNIPPET: create
	function create() {
		var president_in = {
			id : 0,
			firstname : $("#txtFirstname").val(),
			lastname : $("#txtLastname").val(),
			stateId : $("#ctlState option:selected").val(),
			partyId : $("#ctlParty option:selected").val(),
			inauguratedYear : $("#txtInauguratedYear").val(),
			years : $("#txtYears").val()
		}
		
		var presidentJson = JSON.stringify(president_in);
        $.ajax({
            url: restURL + 'president',
            type: "POST",
            data: presidentJson,
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(president_out){
        	showSuccess("Added President " + president_out.firstname + " " + president_out.lastname + ".");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });	
		return true;		
	} 
	// END SNIPPET: create

	// START SNIPPET: update
	function update() {
		var partyId = $("#ctlParty option:selected").val();		
		var president_in = {
				id : $("#txtId").val(),
				firstname : $("#txtFirstname").val(),
				lastname : $("#txtLastname").val(),
				stateId : $("#ctlState option:selected").val(),
				partyId : (partyId == '') ? 'null' : partyId,
				inauguratedYear : $("#txtInauguratedYear").val(),
				years : $("#txtYears").val()
		}
		var presidentJson = JSON.stringify(president_in);
        $.ajax({
            url: restURL + 'president',
            type: "PUT",
            data: presidentJson,
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(president){
        	showSuccess("Updated President " + president_out.firstname + " " + president_out.lastname + ".");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		operation = "A"; // Return to default value
		return true;
	}
	// START SNIPPET: update
	
	// START SNIPPET: delete
	function del() {
		var president = tbPresidents[selected_index];
        $.ajax({
            url: restURL + 'president/' + president.id,
            type: "DELETE",
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(){
        	showSuccess("Deleted President " + president.lastname);
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
        return true;
	}
	// END SNIPPET: delete
	
	function list() {		
		$("#tblList").html("");
		$("#tblList").html(
			"<thead>"+
			"	<tr>"+
			"	<th></th>"+
			"   <th>No.</th>"+
			"   <th>Name</th>"+
			"	<th>State</th>"+
			"	<th>Party</th>"+
			"	<th>Inaugurated Year</th>"+
			"	<th>Years in Office</th>"+
			"	</tr>"+
			"</thead>"+
			"<tbody>"+
			"</tbody>"
			);

		// START SNIPPET: list
		$.getJSON(restURL + 'president', function(json) {
			tbPresidents = json;
		    $.each(tbPresidents, function(i, president){
		    	var party = president.party || "";
		    	$("#tblList tbody").append("<tr>"+
		    			"	<td><img src='images/edit.png' alt='Edit"+i+"' class='btnEdit'/><img src='images/delete.png' alt='Delete"+i+"' class='btnDelete'/></td>" + 
					 	"	<td class='id'>"+president.id+"</td>" +
					 	"	<td><a href='https://en.wikipedia.org/wiki/" + president.firstname + "_" +president.lastname + "' target=_blank>" + president.firstname + " " +president.lastname + "</a></td>" +
					 	"	<td><a href='https://en.wikipedia.org/wiki/" + president.state.replace(" ","_") + "' target=_blank>" + president.state + "</a></td>" +
						"	<td><a href='https://en.wikipedia.org/wiki/" + party.replace(" ","_") + "' target=_blank>" + party + "</a></td>" + 
						"	<td>" + president.inauguratedYear +"</td>" +
						"	<td>" + president.years +"</td>" + 
						"</tr>");
		    });
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
		// END SNIPPET: list
	}
	
	// START SNIPPET: read
	function read() {	
		var id = tbPresidents[selected_index].id;
		$.getJSON(restURL + 'president/' + id, function(president){
			var statusMsg = president.firstname + " " + 
			president.lastname + 
			" " + president.inauguratedYear + " - " + (president.inauguratedYear + president.years - 1);
			window.status=statusMsg;
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
	}
	// END SNIPPET: read
	
	function populateStates() {		
		$.getJSON(restURL + 'state', function(data){
		    var html = '<option value="-1"> </option>';
		    var len = data.length;
		    for (var i = 0; i< len; i++) {
		        html += '<option value="' + data[i].id + '">' + data[i].name + '</option>';
		    }
		    $('select#ctlState').append(html);
			$('#ctlState option:selected').val(-1);
		});		
	}
	
	function populateParties() {		
		$.getJSON(restURL + 'party', function(data){
		    var html = '<option value="null"> </option>';
		    var len = data.length;
		    for (var i = 0; i< len; i++) {
		        html += '<option value="' + data[i].id + '">' + data[i].name + '</option>';
		    }
		    $('select#ctlParty').append(html);
			$('#ctlParty option:selected').val("");
		});		
	}

	$("#frmPresident").bind("submit",function(){		
		return operation == "A" ? create() : update();
	});
	
	list();
	populateStates();
	populateParties();
	
	$('#tblList').on('click', '.btnEdit', function(){
		
		operation = "E";
		selected_index = parseInt($(this).attr("alt").replace("Edit", ""),10);
		
		var president = tbPresidents[selected_index];

		$("#txtId").val(president.id);
		$("#txtFirstname").val(president.firstname);
		$("#txtLastname").val(president.lastname);				
		$('#ctlState option').filter(function () { return $(this).html() == president.state; }).attr('selected','selected'); 		
		$('#ctlParty option').filter(function () { return $(this).html() == president.party; }).attr('selected','selected'); 
		$("#txtInauguratedYear").val(president.inauguratedYear);
		$("#txtYears").val(president.years);
		$("#txtFirstname").focus();
	});
	
	$('#tblList').on('click', '.btnDelete', function(){
		selected_index = parseInt($(this).attr("alt").replace("Delete", ""),10);
		del();
		list();
	});

	$('#tblList').on('mouseover', '.id', function(){
		selected_index = parseInt($(this).text(),10)  - 1;
		read();
	});
	
});
