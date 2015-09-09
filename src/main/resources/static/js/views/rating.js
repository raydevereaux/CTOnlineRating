var commodityBloodHound;
var originBloodHound;
var spellCheckMap = {};
var spellCheckFocusElement;
var spellCheckSource;
var stopOffs = [];
var lastRateFormObj;

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
	$('#exampleRate3').click(function(){
		$('#clientGroup').selectpicker('val', 'BOISEP');
		clientChanged();
		$('#originCode').val('IF');
		loadOriginFromCode();
		$('#destCode').val('CH');
		loadDestFromCode();
		$('#commodityDesc').val('PRINTING PAPER');
		$('#commodityCode').val('2621345');
		$('#commodityWeight').val('150000');
		setTimeout(function(){
			$('#carrierList').selectpicker('val', 'RAIL');
		}, 500);
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
    	complete: function(jqXHR, textStatus){
//    		alert(jqXHR.responseText  + ' ' + textStatus + ' X_CSRF_TOKEN: ' + jqXHR.getResponseHeader('X-CSRF-TOKEN'));
    		$('#spinner').addClass('hide');
    	},
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
	originBloodHound.clearPrefetchCache();
	originPromise = originBloodHound.initialize(true);
	
	$.when(commPromise, originPromise).then(function(){
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
	$('#stopOffCode').keydown(keyPressEvent(loadStopOffFromCode));
	$('#clearOriginBtn').click(clearOriginFields);
	$('#clearDestBtn').click(clearDestFields);
	$('#destCity, #destState, #destZip').keydown(keyPressEvent(checkSpellCheckDest));
	$('#originCity, #originState, #originZip').keydown(keyPressEvent(checkSpellCheckOrigin));
	$('#stopOffCity, #stopOffState, #stopOffZip').keydown(keyPressEvent(checkSpellCheckStopOff));
	$('#addressPickList').on('click', 'a.list-group-item', function(){
    	$(this).siblings().removeClass('active');
    	$(this).addClass('active');
    });
	$('#stopOffModalAdd').click(addStopOff);
	
	$('body').on('click', '#excelIcon', downloadExcelFile);
	
	//Spell Check Stuff
	$('#useSpellCheckBtn').on('click', function(){
		spellCheckObj = spellCheckMap[$('#addressPickList a.active').text()];
		if ('dest' == spellCheckSource){
			loadDestFieldsFromObject(spellCheckObj);	
		}else if ('origin' == spellCheckSource){
			loadOriginFieldsFromObject(spellCheckObj);
		}else if ('stopOff' == spellCheckSource){
			loadStopOffFieldsFromObject(spellCheckObj);
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
	$('body').on('show.bs.modal', '#rateRequestXmlModal', function(){
		prettyPrint();
	});
}

function addStopOff(){
	code = $('#stopOffCode').val();
	city = $('#stopOffCity').val();
	state = $('#stopOffState').val();
	zip = $('#stopOffZip').val();
	county = $('#stopOffCounty').val();
	country = $('#stopOffCountry').val();
	obj = {code: code, city:city, state:state, zip:zip, county:county, country:country};
	stopOffs.push(obj);
	holderDiv = createStopOffText(obj);
	createStopOffHiddenFields(obj, holderDiv);
}

function createStopOffText(obj){
	text = '';
	if (obj.code){
		text = text + obj.code + '-';
	}
	text = text + obj.city + ', ' + obj.state + ' ' + obj.zip + '<br/>';
	text = text + obj.county + ' ' + obj.country;
	index = _.indexOf(stopOffs, obj);
	numberCircle = '<span class="fa-stack fa-lg pull-left">' +
		'<i class="fa fa-circle fa-stack-2x"></i>' +
		'<i class="fa fa-inverse fa-stack-1x">' + (index + 1) + '</i></span>';
	holderDiv = $("<div></div>");
	$('#stopOffBody').append(holderDiv.addClass('col-sm-3')
			.append($(numberCircle))
			.append($('<p></p>').html(text).attr('style', 'overflow:hidden;margin:0;'))
			.append($('<i></i>').addClass('fa fa-times fa-lg text-danger')
					.attr('style', 'cursor:pointer; position:absolute; top:10%; right:0%;')
					.click(function(){
						removeStopOff(_.indexOf(stopOffs, obj));
					})));
	return holderDiv;
}

function createStopOffHiddenFields(obj, holderDiv){
	index = _.indexOf(stopOffs, obj);
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.code').attr('value', obj.code));
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.city').attr('value', obj.city));
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.state').attr('value', obj.state));
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.zip').attr('value', obj.zip));
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.county').attr('value', obj.county));
	holderDiv.append($("<input></input>").attr('type', 'hidden').attr('name', 'Stops[' + index + '].Stop.nation').attr('value', obj.country));
}

function removeStopOff(index){
	if (index > -1){
		stopOffs.splice(index, 1);
	}
	$('#stopOffBody').empty();
	$.each(stopOffs, function(key, value){
		holderDiv = createStopOffText(value);
		createStopOffHiddenFields(value, holderDiv);	
	});
}

function checkSpellCheckDest(){
	spellCheckSource = 'dest';
	city = $('#destCity').val();
	state = $('#destState').val();
	zip = $('#destZip').val();
	if (city && state && !zip){
		readSpellCheck(city, state, zip, loadDestFieldsFromObject);
	}
}

function checkSpellCheckOrigin(){
	spellCheckSource = 'origin';
	city = $('#originCity').val();
	state = $('#originState').val();
	zip = $('#originZip').val();
	if (city && state && !zip){
		readSpellCheck(city, state, zip, loadOriginFieldsFromObject);
	}
}

function checkSpellCheckStopOff(){
	spellCheckSource = 'stopOff';
	city = $('#stopOffCity').val();
	state = $('#stopOffState').val();
	zip = $('#stopOffZip').val();
	if (city && state && !zip){
		readSpellCheck(city, state, zip, loadStopOffFieldsFromObject);
	}
}

function loadOriginFromCode(){
	clearValidationErrors();
	code = $('#originCode').val();
	client = $('#clientGroup').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {client: client, locationCode : code}, function(data){
		loadOriginFieldsFromObject(data);
	}).fail(function(xhr, textStatus, errorThrown){
		errorObj = eval("(" + xhr.responseText + ")")
		alert('Unable to load origin from code. Error returned from server: ' + errorObj.message);
		clearOriginFields();
	});
}

function loadStopOffFromCode(){
	clearValidationErrors();
	code = $('#stopOffCode').val();
	client = $('#clientGroup').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {client: client, locationCode : code}, function(data){
		loadStopOffFieldsFromObject(data);
	}).fail(function(xhr, textStatus, errorThrown){
		errorObj = eval("(" + xhr.responseText + ")")
		alert('Unable to load origin from code. Error returned from server: ' + errorObj.message);
		clearStopOffFields();
	});
}

function loadDestFromCode(){
	clearValidationErrors();
	code = $('#destCode').val();
	client = $('#clientGroup').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {client: client, locationCode : code}, function(data){
		loadDestFieldsFromObject(data);
	}).fail(function(xhr, textStatus, errorThrown){
		errorObj = eval("(" + xhr.responseText + ")")
		alert('Unable to load destination from code. Error returned from server: ' + errorObj.message);
		clearDestFields();
	});
}

function clearStopOffFields(){
	$('#stopOffCode').val('');
	$('#stopOffName').val('');
	$('#stopOffCity').val('');
	$('#stopOffState').val('');
	$('#stopOffZip').val('');
	$('#stopOffCounty').val('');
	$('#stopOffSPLC').val('');
	$('#stopOffCountry').val('');
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
	$('#destCode').val(data.code);
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

function loadStopOffFieldsFromObject(data){
	$('#stopOffName').val(data.name);
	$('#stopOffCity').val(data.city);
	$('#stopOffState').val(data.state);
	$('#stopOffZip').val(data.zip);
	$('#stopOffCounty').val(data.county);
	$('#stopOffSPLC').val(data.splc);
	$('#stopOffCountry').val(data.country);
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
	$('#commodityClass').parent('.form-group').toggleClass('hide', 'BOISEW' == client);
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
	populateOriginTypeAhead();
	populateCommodityTypeAhead();
}

function populateOriginTypeAhead(){
	originBloodHound = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('desc'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			url: allMillLocationsUrl,
			ttl: 11400000, //Time to live set to 4 hours
			// the json file contains an array of strings, but the Bloodhound
			// suggestion engine expects JavaScript objects so this converts all of
			// those strings
			filter: function(list) {
				return $.map(list, function(mill) { 
					desc = '';
					if (mill.code){
						desc = desc + mill.code + ' - ';
					}
					if (mill.name){
						desc = desc + mill.name + ' - ';
					}
					if (mill.city){
						desc = desc + mill.city + ' - ';
					}
					if (mill.state){
						desc = desc + mill.state;
					}
					return { code: mill.code, city: mill.city, state: mill.state,
					zip: mill.zip, splc: mill.splc, county: mill.county, country: mill.country, name: mill.name,
					desc: desc}; });
			}
		}
	});
	originBloodHound.initialize();
    $('#originSearch').typeahead('destroy');
	// passing in `null` for the `options` arguments will result in the default
	// options being used
    $('#originSearch').typeahead({
		hint: true,
		highlight: true
	}, {
		name: 'millLocations',
		displayKey: 'desc',
		source: originBloodHound.ttAdapter()
	}).on('typeahead:selected', function (obj, datum) {
		loadOriginFieldsFromObject(datum);
		$('#originCode').val(datum.code);
		$('#originName').val(datum.name);
        $('#destCode').focus();
    });
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
				return $.map(list, function(commodity) { return { code: commodity.code, desc: commodity.desc, descCode: commodity.desc + ' ' + commodity.code, commClass: commodity.commClass}; });
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
		minLength: 2,
		limit: 20,
		name: 'commodities',
		displayKey: 'descCode',
		source: commodityBloodHound.ttAdapter()
	}).on('typeahead:selected', function (obj, datum) {
		$('#commodityDesc').val(datum.desc);
        $('#commodityCode').val(datum.code);
        $('#commodityClass').val(datum.commClass);
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
	lastRateFormObj = $('#ratingForm').formSerialize();
	$('body').remove('iframe');
}

function downloadExcelFile(){
	window.location.href = rateExcelUrl + '?' + lastRateFormObj;
//	$.post(rateExcelUrl, lastRateFormObj, function() {
//	}).complete(function(jqXHR, textStatus){
//		alert(jqXHR.url);
//		$("body").append("<iframe src='" + jqXHR.url+ "' style='display: none;' ></iframe>");
//	}); 
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
	if (!$('#carrierList').selectpicker('val')){
		$('#carrierList').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#shipDate').val()){
		$('#shipDate').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#commodityWeight').val() || $('#commodityWeight').val() <= 0){
		$('#commodityWeight').focus().closest('.form-group').addClass('has-error');
		validates = false;
	}
	if (!$('#commodityCode').val()){
		$('#commodityCode').focus().closest('.form-group').addClass('has-error');
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

	$('#spinner').toggleClass('hide', !validates);
	return validates;
}

function clearValidationErrors(){
	$('div .form-group').removeClass('has-error');
}