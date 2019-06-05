$(function() {

	$.ajaxSetup({ cache: false });
	
	var operation = "A";
	var selected_index = -1;
	var tbParties;
	
	// START SNIPPET: create
	function create() {
		var party_in = {
			id : 0,
			name : $("#txtName").val(),
			foundedYear : $("#txtFoundedYear").val(),
			endYear : $("#txtEndYear").val()
		}
		var partyJson = JSON.stringify(party_in);
		$.ajax({
			url : restURL + 'party',
			type : "POST",
			data : partyJson,
			contentType : "application/json",
			cache : false,
			dataType : "json",
			async : false
        }).done(function(party_out){
        	showSuccess("Added Party " + party_out.name + ".");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		return true;
	}
	// END SNIPPET: create

	// START SNIPPET: update
	function update() {
		var party_in = {
			id : $("#txtId").val(),
			name : $("#txtName").val(),
			foundedYear : $("#txtFoundedYear").val(),
			endYear : $("#txtEndYear").val()
		}
		var partyJson = JSON.stringify(party_in);
		$.ajax({
			url : restURL + 'party',
			type : "PUT",
			data : partyJson,
			contentType : "application/json",
			cache : false,
			dataType : "json",
			async : false
        }).done(function(party_out){
        	showSuccess("Updated Party " + party_out.name + ".");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		operation = "A"; // Return to default value
		$("#txtName").prop('disabled', false);
		return true;
	}
	// END SNIPPET: update

	// START SNIPPET: delete
	function del() {
		var party = tbParties[selected_index];
		$.ajax({
			url : restURL + 'party/' + party.id,
			type : "DELETE",
			contentType : "application/json",
			cache : false,
			dataType : "text",
			async : false
        }).done(function(party_out){
        	showSuccess("Delete Party " + party.name + ".");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		return true;
	}
	// END SNIPPET: delete

	function list(){		
		$("#tblList").html("");
		$("#tblList").html(
			"<thead>"+
			"	<tr>"+
			"	<th></th>"+
			"	<th>Name</th>"+
			"	<th>Founded Year</th>"+
			"	<th>End Year</th>"+
			"	</tr>"+
			"</thead>"+
			"<tbody>"+
			"</tbody>"
			);

		// START SNIPPET: list
		$.getJSON(restURL + 'party', function(json){
			tbParties = json;
		    $.each(tbParties, function(i, party){
		    	var endYear = (!party.endYear || party.endYear === undefined) ? "" : party.endYear;
		    	$("#tblList tbody").append("<tr>"+
		    			" <td><img src='images/edit.png' alt='Edit" + i + "' class='btnEdit'/>" + 
		    				"<img src='images/delete.png' alt='Delete" + i + "' class='btnDelete'/></td>" + 
						" <td class='id' id='" + party.id + "'>" + party.name + "</td>" +
						" <td>" + party.foundedYear + "</td>" + 
						" <td>" + endYear.toString().replace(/null/i,"") + "</td>" +
						"</tr>");
		    });
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
		// END SNIPPET: list
	}
	
	// START SNIPPET: read
	function read() {	
		var id = tbParties[selected_index].id;		
		$.getJSON(restURL + 'party/' + id, function(party){
			var endYear = (!party.endYear || party.endYear === undefined) ? "not yet!" : party.endYear;
			var statusMsg = party.name + " (founded: " + 
			party.foundedYear + 
			", dissolved: " + endYear + ")";
			window.status=statusMsg;
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
	}
	// END SNIPPET: read
	
	$("#frmParty").bind("submit", function() {
		return operation == "A" ? create() : update();
	});

	list();

	$('#tblList').on('click', '.btnEdit', function() {

		operation = "E";
		selected_index = parseInt($(this).attr("alt").replace("Edit", ""),10);

		var party = tbParties[selected_index];

		$("#txtId").val(party.id);
		$("#txtName").val(party.name);
		$("#txtName").prop('disabled', true);
		$("#txtFoundedYear").val(party.foundedYear);
		$("#txtEndYear").val(party.endYear);
		$("#txtName").focus();
	});

	$('#tblList').on('click', '.btnDelete', function() {
		selected_index = parseInt($(this).attr("alt").replace("Delete", ""),10);
		del();
		list();
	});
	
	$('#tblList').on('mouseover', '.id', function(){
		selected_index = parseInt($(this).attr('id'),10) - 1;
		read();
	});

});
