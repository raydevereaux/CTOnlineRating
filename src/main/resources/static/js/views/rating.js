$(document).ready(function(){
	init();
});

function init(){
	$('.selectpicker').selectpicker();
	populateTypeAheads();
	setDefaults();
	addListeners();
	clientChanged();
	$('#originSearch').focus();
}

function setDefaults(){
	$.fn.datepicker.defaults.format = "mm/dd/yyyy";
	$('#shipDateInputGroup').datepicker({
	    todayBtn: "linked",
	    autoclose: true,
	    todayHighlight: true
	});
	$('#shipDateInputGroup').datepicker('setDate', new Date());
}

function addListeners(){
	$('#client').change(clientChanged);
	$('#originCode').keypress(keyPressEvent(loadOriginFromCode, $('#destSearch')));
	$('#destCode').keypress(keyPressEvent(loadDestFromCode, $('#commodityDesc')));
}

function loadOriginFromCode(){
	code = $('#originCode').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {locationCode : code}, function(data){
		loadOriginFieldsFromObject(data);
	}).fail(function(xhr, textStatus, errorThrown){
		errorObj = eval("(" + xhr.responseText + ")")
		alert('Unable to load origin from code. Error returned from server: ' + errorObj.message);
		clearOriginFields();
	});
}

function loadDestFromCode(){
	code = $('#destCode').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {locationCode : code}, function(data){
		loadDestFieldsFromObject(data);
	}).fail(function(xhr, textStatus, errorThrown){
		errorObj = eval("(" + xhr.responseText + ")")
		alert('Unable to load destination from code. Error returned from server: ' + errorObj.message);
		clearDestFields();
	});
}

function clearOriginFields(){
	$('#originName').val('');
	$('#originCity').val('');
	$('#originState').val('');
	$('#originZip').val('');
	$('#originCounty').val('');
	$('#originSPLC').val('');
	$('#originCountry').val('');
}

function clearDestFields(){
	$('#destName').val('');
	$('#destCity').val('');
	$('#destState').val('');
	$('#destZip').val('');
	$('#destCounty').val('');
	$('#destSPLC').val('');
	$('#destCountry').val('');
}

function loadDestFieldsFromObject(data){
	$('#destName').val(data.name);
	$('#destCity').val(data.city);
	$('#destState').val(data.state);
	$('#destZip').val(data.zip);
	$('#destCounty').val(data.county);
	$('#destSPLC').val(data.splc);
	$('#destCountry').val(data.country);
}

function loadOriginFieldsFromObject(data){
	$('#originName').val(data.name);
	$('#originCity').val(data.city);
	$('#originState').val(data.state);
	$('#originZip').val(data.zip);
	$('#originCounty').val(data.county);
	$('#originSPLC').val(data.splc);
	$('#originCountry').val(data.country);
}

function keyPressEvent(callback, nextElement){
	return function(e){
		if (!e) e = window.event;
	    var keyCode = e.keyCode || e.which;
	    //Enter
	    if (keyCode == '13'){
	    	callback();
	    	$(nextElement).focus();
	    	return false;
	    }else if (keyCode == '9'){ //Tab
	    	callback();
	    	return true;
	    }	
	}
}

function clientChanged(){
	client = $('#client').val();
	refreshCarrierList(client);
}

function refreshCarrierList(client){
	$.get(carrierListUrl, {client : client}, function(data){
		$('#carrierList').empty();
		$('#carrierList').append($("<option></option>").attr("value", '').text('None'));
		$.each(data, function(key, value){
			$('#carrierList').append($("<option></option>").attr("value", value).text(value));
		});
		$('#carrierList').selectpicker('refresh');
	});
}

function populateTypeAheads(){
	populateOriginDestTypeAheads();
}

function populateOriginDestTypeAheads(){
	substringMatcher = function(strs) {
		return function findMatches(q, cb) {
			var matches, substringRegex;

			// an array that will be populated with substring matches
			matches = [];

			// regex used to determine if a string contains the substring `q`
			substrRegex = new RegExp(q, 'i');

			// iterate through the pool of strings and for any string that
			// contains the substring `q`, add it to the `matches` array
			$.each(strs, function(i, str) {
				if (substrRegex.test(str)) {
					matches.push(str);
				}
			});

			cb(matches);
		};
	};

	$('.typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name:'states',
		source: substringMatcher(['Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California',
		                          'Colorado', 'Connecticut', 'Delaware', 'Florida', 'Georgia', 'Hawaii',
		                          'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana',
		                          'Maine', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota',
		                          'Mississippi', 'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire',
		                          'New Jersey', 'New Mexico', 'New York', 'North Carolina', 'North Dakota',
		                          'Ohio', 'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island',
		                          'South Carolina', 'South Dakota', 'Tennessee', 'Texasjksdkjlfjklsdjklfjlksdfjsdf', 'Utah', 'Vermont',
		                          'Virginia', 'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'])
	});
}


