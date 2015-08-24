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
	$('#quoteTab').attr('class', 'disabled');

    $('#tabs li').click(function(event){
        if ($(this).hasClass('disabled')) {
            return false;
        }
    });
    
    $('#ratingForm').ajaxForm({
    	beforeSubmit: validateForm,
    	type: 'POST'
    });
    
//    $('#rateBtn').click(validateAndRate);
}

function addListeners(){
	$('#clientGroup').change(clientChanged);
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
	client = $('#clientGroup').val();
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
//	populateTypeAhead($('#originSearch'), url);
//	populateTypeAhead($('#destSearch'), url);
	populateTypeAhead($('#commodityDesc'), 'commodities', 'desc', commodityUrl + '?client=' + $('#clientGroup').val());
}

function populateTypeAhead(typeAheadElement, dataName, displayKey, url){
	var bloodHound = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace(displayKey),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			url: url,
			ttl: 11400000, //Time to live set to 4 hours
			// the json file contains an array of strings, but the Bloodhound
			// suggestion engine expects JavaScript objects so this converts all of
			// those strings
			filter: function(list) {
				return $.map(list, function(commodity) { return { code: commodity.code, desc: commodity.desc}; });
			}
		}
	});
	bloodHound.clearPrefetchCache();
    bloodHound.initialize(true);
	// passing in `null` for the `options` arguments will result in the default
	// options being used
	$(typeAheadElement).typeahead({
		hint: true,
		highlight: true
	}, {
		name: dataName,
		displayKey: displayKey,
		source: bloodHound.ttAdapter()
	}).on('typeahead:selected', function (obj, datum) {
        $('#commodityCode').val(datum.code);
        $('#commodityWeight').focus();
    });
}

function validateForm(){
	$('div .form-group').removeClass('has-error');
	validates = true;
	if (!$('#originCity').val()){
		$('#originSearch').closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#destCity').val()){
		$('#destSearch').closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#commodityDesc').val()){
		$('#commodityDesc').closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#commodityWeight').val() || $('#commodityWeight').val() <= 0){
		$('#commodityWeight').closest('.form-group').addClass('has-error');
		validates = false;
	}
	return validates;
}

function constructRateRequest(){
	client = $('#clientGroup').val();
	return JSON.stringify({clientGroup : client});
}

