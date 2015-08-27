var commodityBloodHound;
var originBloodHound;
var destBloodHound;
var spellCheckMap = {};
var spellCheckFocusElement;
var spellCheckSourceDest = false;

$(document).ready(function(){
	init();
});

function init(){
	setDefaults();
	addListeners();
	clientChanged();
	setUpExampleRates();
	$('#originSearch').focus();
	$('.selectpicker').selectpicker();
}

function setUpExampleRates(){
	setUpRates = function(){
		$('#clientGroup').selectpicker('val', 'BOISEW');
		clientChanged();
		$('#originCode').val('53');
		$('#originCity').val('MEDFORD');
		$('#originState').val('OR');
		$('#destCity').val('DENVER');
		$('#destState').val('CO');
		$('#commodityDesc').val('PLYWOOD');
		$('#commodityCode').val('2432158');
		$('#commodityWeight').val('48000');
		setTimeout(function(){
			$('#carrierList').selectpicker('val', 'TRUCK');
		}, 500);
	}
	$('#exampleRate1').click(setUpRates);
	$('#exampleRate2').click(function(){
		setUpRates();
		$('#destCity').val('AMERICAN CANYON');
		$('#destState').val('CA');
		$('#destCounty').val('NAPA');
	});
}

function setDefaults(){
	$('#ratingForm')[0].reset();
	$.fn.datepicker.defaults.format = "mm/dd/yyyy";
	$('#shipDateInputGroup').datepicker({
	    todayBtn: "linked",
	    autoclose: true,
	    todayHighlight: true
	});
	$('#shipDateInputGroup').datepicker('setDate', new Date());
	$('#quoteTab').parent('li').attr('class', 'disabled');

    $('#tabs li').click(function(event){
        if ($(this).hasClass('disabled')) {
            return false;
        }
    });
    
    $('#refreshOd').click(refreshOriginDest);
    
    $('#ratingForm').ajaxForm({
    	beforeSubmit: validateForm,
    	success: rateSuccess,
    	type: 'POST'
    });
    $('#rateBtn').on('click', function(){
    	$('#ratingForm').submit();
    });
    
    $('[data-toggle="tooltip"]').tooltip();
}

function refreshOriginDest(){
	$('#refreshOd i').addClass('fa-spin');
	commodityBloodHound.clearPrefetchCache();
	commPromise = commodityBloodHound.initialize(true);
	
	$.when(commPromise).then(function(){
		$('#refreshOd i').removeClass()
		.addClass('fa fa-check-circle fa-lg').delay(1500).fadeOut().queue(function(){
			$(this).removeClass().addClass('fa fa-refresh fa-lg').fadeIn().dequeue();
		});
	}, function(){
		$('#refreshOd i').removeClass().addClass('fa fa-times-circle-o fa-lg text-danger');
	});
}

function addListeners(){
	$('#clientGroup').change(clientChanged);
	$('#originCode').keydown(keyPressEvent(loadOriginFromCode, $('#destCode')));
	$('#destCode').keydown(keyPressEvent(loadDestFromCode, $('#commodityDesc')));
	$('#clearOriginBtn').click(clearOriginFields);
	$('#clearDestBtn').click(clearDestFields);
	$('#destCity, #destState, #destZip').keydown(keyPressEvent(checkSpellCheckDest));
	$('#originCity, #originState, #originZip').keydown(keyPressEvent(checkSpellCheckOrigin));
	$('#addressPickList').on('click', 'a.list-group-item', function(){
    	$(this).siblings().removeClass('active');
    	$(this).addClass('active');
    });
	
	//Spell Check Stuff
	$('#useSpellCheckBtn').on('click', function(){
		if (spellCheckSourceDest){
			loadDestFieldsFromObject(spellCheckMap[$('#addressPickList a.active').text()]);	
		}else{
			loadOriginFieldsFromObject(spellCheckMap[$('#addressPickList a.active').text()]);
		}
	});
	$('#addressPickModal').on('keydown', function(e){
		if(e.which == 40) { // down
			$('#addressPickList a.active').removeClass('active').next().addClass('active');
			if (!$('#addressPickList a.active').length){
				$('#addressPickList a:first').addClass('active');
			}
	        return false; // stops the page from scrolling
	    }
	    if(e.which == 38) { // up
	    	$('#addressPickList a.active').removeClass('active').prev().addClass('active');
			if (!$('#addressPickList a.active').length){
				$('#addressPickList a:last').addClass('active');
			}
	        return false; // stops the page from scrolling
	    }
	    if(e.which == 13) { // enter
	    	$('#useSpellCheckBtn').click();
	    }
	});
	$('#addressPickModal').on('show.bs.modal', function(){
		spellCheckFocusElement = $(':focus');
	});
	$('#addressPickModal').on('hidden.bs.modal', function(){
		$(spellCheckFocusElement).focus();
	});
}

function checkSpellCheckDest(){
	spellCheckSourceDest = true;
	city = $('#destCity').val();
	state = $('#destState').val();
	if (city && state){
		readSpellCheck(city, state, $('#destZip').val(), loadDestFieldsFromObject);
	}
}

function checkSpellCheckOrigin(){
	spellCheckSourceDest = false;
	city = $('#originCity').val();
	state = $('#originState').val();
	if (city && state){
		readSpellCheck(city, state, $('#originZip').val(), loadOriginFieldsFromObject);
	}
}

function loadOriginFromCode(){
	clearValidationErrors();
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
	clearValidationErrors();
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
	$('#originCode').val('');
	$('#originName').val('');
	$('#originCity').val('');
	$('#originState').val('');
	$('#originZip').val('');
	$('#originCounty').val('');
	$('#originSPLC').val('');
	$('#originCountry').val('');
}

function clearDestFields(){
	$('#destCode').val('');
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
	    	if (nextElement)
	    		$(nextElement).focus();
	    	return true;
	    }else if (keyCode == '9'){ //Tab
	    	callback();
	    	return true;
	    }	
	}
}

function clientChanged(){
	clearValidationErrors();
	client = $('#clientGroup').val();
	refreshCarrierList(client);
	populateTypeAheads();
}

function refreshCarrierList(client){
	$.get(carrierListUrl, {client : client}, function(data){
		$('#carrierList').empty();
		$('#carrierList').append($("<option></option>").attr("value", '').text('None'));
		$.each(data, function(key, value){
			carrArr = value.split('.');
			if (carrArr[1] == 'CADTRK'){
				carrArr[1] = 'TRUCK';
			}
			$('#carrierList').append($("<option></option>").attr("value", carrArr[1]).text(value));
		});
		$('#carrierList').selectpicker('refresh');
	});
}

function populateTypeAheads(){
	$('#commodityDesc').val('');
	$('#commodityCode').val('');
//	populateTypeAhead($('#originSearch'), url);
//	populateTypeAhead($('#destSearch'), url);
	populateCommodityTypeAhead();
}

function populateCommodityTypeAhead(){
	commodityBloodHound = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('desc'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			url: commodityUrl + '?client=' + $('#clientGroup').val(),
			ttl: 11400000, //Time to live set to 4 hours
			// the json file contains an array of strings, but the Bloodhound
			// suggestion engine expects JavaScript objects so this converts all of
			// those strings
			filter: function(list) {
				return $.map(list, function(commodity) { return { code: commodity.code, desc: commodity.desc}; });
			}
		}
	});
	commodityBloodHound.initialize();
    $('#commodityDesc').typeahead('destroy');
	// passing in `null` for the `options` arguments will result in the default
	// options being used
    $('#commodityDesc').typeahead({
		hint: true,
		highlight: true
	}, {
		name: 'commodities',
		displayKey: 'desc',
		source: commodityBloodHound.ttAdapter()
	}).on('typeahead:selected', function (obj, datum) {
		$('#commodityDesc').val(datum.desc);
        $('#commodityCode').val(datum.code);
        $('#commodityWeight').focus();
    });
}

function readSpellCheck(city, state, zip, callback){
	clearValidationErrors();
	data = {city : city.toUpperCase(), state : state.toUpperCase()};
	if (zip){
		data.zip = zip;
	}
	$.get(spellCheckUrl, data, function(data){
		if (data.length == 0){
			return;
		}else if (data.length == 1){
			callback(data[0]);
		}else{
			populateSpellCheckModal(data);
			$('#addressPickModal').modal('show');
		}
	});
}

function populateSpellCheckModal(dataArr){
	$('#addressPickList').empty();
	spellCheckMap = {};
	for (i=0; i<dataArr.length; i++){
		data = dataArr[i];
		textVal = data.city + ', ' + data.state + ' ' + data.zip + ' ' + 
			data.county + ' ' + data.splc + ' ' + data.country;
		spellCheckMap[textVal] = data;
		element = $('<a></a>').text(textVal).addClass('list-group-item').attr('style', 'cursor:pointer');
		if (i==0){
			element.addClass('active');
		}
		$('#addressPickList').append(element);
	}
}

function rateSuccess(rateResponse, statusText, xhr, form){
	$('#quoteOrigin').text($('#originCity').val() + ', ' + $('#originState').val());
	$('#quoteDest').text($('#destCity').val() + ', ' + $('#destState').val());
	$('#quoteCommodity').text($('#commodityDesc').val());
	$('#quoteWeight').text(numeral($('#commodityWeight').val()).format('0,0'));
	$('#quoteShipDate').text($('#shipDate').val());
	$('#quoteCarrier').text($('#carrierList option:selected').text());
	$('#quoteTabContent').empty().append(rateResponse);
	$('#spinner').toggleClass('hide');
	$('#quoteTab').parent('li').attr('class', 'enabled');
	$('#quoteTab').tab('show');
	$('#quoteTable tbody').on('click', 'tr', function(){
		quoteClicked($(this));
	});
}

function quoteClicked(tableRowElement){
	quoteIndex = $(tableRowElement).find('td:first').text();
	$.each($('#quoteModalTable > tbody > tr, #quoteModalTariffTable > tbody > tr'), function(index, value){
		if (quoteIndex == $(this).find('td:first').text()){
			$(this).show();
		}else{
			$(this).hide();
		}
	});
	$('#quoteModal').modal('show');
}

function validateForm(){
	$('div .form-group').removeClass('has-error');
	validates = true;
	if (!$('#commodityWeight').val() || $('#commodityWeight').val() <= 0){
		$('#commodityWeight').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#commodityDesc').val()){
		$('#commodityDesc').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#destCity').val()){
		$('#destCode').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#originCity').val()){
		$('#originSearch').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	
	if (validates){
		$('#spinner').toggleClass('hide');
	}
	return validates;
}

function clearValidationErrors(){
	$('div .form-group').removeClass('has-error');
}

function constructRateRequest(){
	client = $('#clientGroup').val();
	return JSON.stringify({clientGroup : client});
}

