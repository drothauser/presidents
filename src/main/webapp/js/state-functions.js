$(function(){ 
	
	$.ajaxSetup({ cache: false });

	var operation = "A"; //"A"=Adding; "E"=Editing 
	var selected_index = -1; 
	var tbStates;

	// START SNIPPET: create
	function create() {
		var state_in = {
			id : 0,
			name : $("#txtName").val(),
			abbreviation : $("#txtAbbr").val()
		}
		var stateJson = JSON.stringify(state_in);
        $.ajax({
            url: restURL + 'state',
            type: "POST",
            data: stateJson,
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(state_out){
        	showSuccess("Added State " + state_out.name + " (" + state_out.abbreviation + ").");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		return true;
	} 
	// END SNIPPET: create

	// START SNIPPET: update
	function update() {
		var state_in = {
				id : $("#txtId").val(),
				name : $("#txtName").val(),
				abbreviation : $("#txtAbbr").val()
			}
		var stateJson = JSON.stringify(state_in);
        $.ajax({
            url: restURL + 'state',
            type: "PUT",
            data: stateJson,
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(state_out){
        	showSuccess("Updated State " + state_out.name + " (" + state_out.abbreviation + ").");
        }).fail(function(xhr, status, error){
        	showFailure(xhr, status, error);
        });
		operation = "A"; // Return to default value
		$("#txtName").prop('disabled', false);
		return true;
	}
	// END SNIPPET: create

	// START SNIPPET: delete
	function del() {
		var state = tbStates[selected_index];
        $.ajax({
            url: restURL + 'state/' + state.id,
            type: "DELETE",
            contentType: "application/json",
            cache: false,
            dataType: "json",
            async: false
        }).done(function(){
        	showSuccess("Deleted State " + state.name + " (" + state.abbreviation + ").");
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
			"	<th>Abbreviation</th>"+
			"	</tr>"+
			"</thead>"+
			"<tbody>"+
			"</tbody>"
			);

		// START SNIPPET: list
		$.getJSON(restURL + 'state', function(json){
			tbStates = json;
		    $.each(tbStates, function(i, state){
		    	$("#tblList tbody").append("<tr>" +
					 	 "	<td><img src='images/edit.png' alt='Edit" + i + "' class='btnEdit'/>" + 
					 	 		"<img src='images/delete.png' alt='Delete" + i + "' class='btnDelete'/></td>" +
					 	 " <td class='id' id='" + state.id + "'>" + state.name + "</td>" +						 
						 "	<td>" + state.abbreviation + "</td>" + 
						 "</tr>");
		    });
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
		// END SNIPPET: list
	}
	
	// START SNIPPET: read
	function read() {	
		var id = tbStates[selected_index].id;		
		$.getJSON(restURL + 'state/' + id, function(state){			
			var statusMsg = state.name + " (" + state.abbreviation + ")";
			window.status=statusMsg;
		}).fail(function(xhr, status, error) {
			showFailure(xhr, status, error);
	    });
	}
	// END SNIPPET: read

	$("#frmState").bind("submit",function(){		
		return operation == "A" ? create() : update();
	});

	list();
	
	$('#tblList').on('click', '.btnEdit', function(){
		
		operation = "E";
		selected_index = parseInt($(this).attr("alt").replace("Edit", ""),10);
		
		var state = tbStates[selected_index];

		$("#txtId").val(state.id);
		$("#txtName").val(state.name);
		$("#txtName").prop('disabled', true);
		$("#txtAbbr").val(state.abbreviation);
		$("#txtName").focus();
	});
	
	$('#tblList').on('click', '.btnDelete', function(){
		selected_index = parseInt($(this).attr("alt").replace("Delete", ""),10);
		del();
		list();
	});
		
	$('#tblList').on('mouseover', '.id', function(){
		selected_index = parseInt($(this).attr('id'),10) - 1;
		read();
	});
	
});
