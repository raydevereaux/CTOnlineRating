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
	$('.selectpicker').selectpicker();
	addListeners();
	setDefaults();
	clientChanged();
	setUpExampleRates();
	$('#originSearch').focus();
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
		$('#destCity').val('DENVER');
		$('#destState').val('CO');
		$('#destCounty').val('');
		setTimeout(function(){
			$('#carrierList').selectpicker('val', 'ALL');
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
    	error: function(jqXHR, textStatus, errorThrown){
    		alert(textStatus + ' - ' + errorThrown);
    	},
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
    
    readClientGroupCookie();
}

function readClientGroupCookie(){
	var cookieValue = $.cookie("clientGroup");
	//Read cookie value, set client group, and remake cookie
	if (cookieValue){
		$('#clientGroup').selectpicker('val', cookieValue);
		setClientGroupCookie(cookieValue);
	}
}

function setClientGroupCookie(clientGroup){
	$.cookie("clientGroup", clientGroup, {expires : 30});
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
	$('#originCode').change(function(){
		loadOriginFromCode($('#destCode'));
	});
	$('#destCode').change(function(){
		loadDestFromCode($('#commodityDesc'));
	});
	$('#stopOffCode').change(loadStopOffFromCode);
	$('#clearOriginBtn').click(clearOriginFields);
	$('#clearDestBtn').click(clearDestFields);
	$('#originCity, #originState, #originZip').change(checkSpellCheckOrigin);
	$('#destCity, #destState, #destZip').change(checkSpellCheckDest);
	$('#stopOffCity, #stopOffState, #stopOffZip').change(checkSpellCheckStopOff);
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
	$('body').on('show.bs.modal', '#rateRequestXmlModal,#rateResponseXmlModal', function(){
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

function checkSpellCheckDest(e){
	spellCheckSource = 'dest';
	city = $('#destCity').val();
	state = $('#destState').val();
	zip = $('#destZip').val();
	if (($(e.target).is($('#destCity')) || $(e.target).is($('#destState'))) && ($('#destSPLC').val() || $('#destCounty').val())){
		//Turn off event listener before clearing fields
		$('#destCity, #destState, #destZip').off('change', checkSpellCheckDest);
		prevVal = $(this).val();
		clearDestFields();
		$('#destCity, #destState, #destZip').on('change', checkSpellCheckDest);
		$(this).val(prevVal);
		return;
	}
	zipBlanked = $(e.target).is($('#destZip')) && zip == '';
	$('#' + spellCheckSource + 'WarningIcon').addClass('hide');
	if (city && state && !zipBlanked){
		readSpellCheck(city, state, zip, loadDestFieldsFromObject);
	}
}

function checkSpellCheckOrigin(e){
	spellCheckSource = 'origin';
	city = $('#originCity').val();
	state = $('#originState').val();
	zip = $('#originZip').val();
	if (($(e.target).is($('#originCity')) || $(e.target).is($('#originState'))) && ($('#originSPLC').val() || $('#originCounty').val())){
		//Turn off event listener before clearing fields
		$('#originCity, #originState, #originZip').off('change', checkSpellCheckOrigin);
		prevVal = $(this).val();
		clearOriginFields();
		$('#originCity, #originState, #originZip').on('change', checkSpellCheckOrigin);
		$(this).val(prevVal);
		return;
	}
	zipBlanked = $(e.target).is($('#originZip')) && zip == '';
	$('#' + spellCheckSource + 'WarningIcon').addClass('hide');
	if (city && state && !zipBlanked){
		readSpellCheck(city, state, zip, loadOriginFieldsFromObject);
	}
}

function checkSpellCheckStopOff(e){
	spellCheckSource = 'stopOff';
	city = $('#stopOffCity').val();
	state = $('#stopOffState').val();
	zip = $('#stopOffZip').val();
	if (($(e.target).is($('#stopOffCity')) || $(e.target).is($('#stopOffState'))) && ($('#stopOffSPLC').val() || $('#stopOffCounty').val())){
		//Turn off event listener before clearing fields
		$('#stopOffCity, #stopOffState, #stopOffZip').off('change', checkSpellCheckStopOff);
		prevVal = $(this).val();
		clearStopOffFields();
		$('#stopOffCity, #stopOffState, #stopOffZip').on('change', checkSpellCheckStopOff);
		$(this).val(prevVal);
		return;
	}
	zipBlanked = $(e.target).is($('#stopOffZip')) && zip == ''; 
	$('#' + spellCheckSource + 'WarningIcon').addClass('hide');
	if (city && state && !zipBlanked){
		readSpellCheck(city, state, zip, loadStopOffFieldsFromObject);
	}
}

function loadOriginFromCode(nextFocusElement){
	clearValidationErrors();
	code = $('#originCode').val();
	client = $('#clientGroup').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {client: client, locationCode : code}, function(data){
		loadOriginFieldsFromObject(data);
		if (nextFocusElement)
			$(nextFocusElement).focus();	
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

function loadDestFromCode(nextFocusElement){
	clearValidationErrors();
	code = $('#destCode').val();
	client = $('#clientGroup').val();
	if (!code){
		return;
	}
	$.get(locationByCodeUrl, {client: client, locationCode : code}, function(data){
		loadDestFieldsFromObject(data);
		if (nextFocusElement)
			$(nextFocusElement).focus();
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

function originChanged(){
	if ($('#originSPLC').val() || $('#originCounty').val()){
		//Turn off event listener before clearing fields
		$('#originCity, #originState').off('change', originChanged);
		prevVal = $(this).val();
		clearOriginFields();
		$('#originCity, #originState').on('change', originChanged);
		$(this).val(prevVal);
	}
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
	setClientGroupCookie(client);
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
//			if (carrArr[1] == 'CADTRK'){
//				carrArr[1] = 'TRUCK';
//			}
			$('#carrierList').append($("<option></option>").attr("value", carrArr[1]).text(value));
		});
		$('#carrierList').selectpicker('refresh');
		//Default carrier if applicable
		if ('BOISEB' === client){
			$('#carrierList').selectpicker('val', 'RAIL');
		}
	});
	//Default carrier if applicable
	if ('BOISEB' === client){
		$('#carrierList').selectpicker('val', 'RAIL');
	}
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
			url: allMillLocationsUrl + '?client=' + $('#clientGroup').val(),
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
		$('#originSearch').typeahead('val', '');
		loadOriginFieldsFromObject(datum);
		$('#originCode').val(datum.code);
		$('#originName').val(datum.name);
        $('#destCode').focus();
    });
}

function populateCommodityTypeAhead(){
	commodityBloodHound = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('descCode'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
//			url: commodityUrl + '?client=' + $('#clientGroup').val(),
			url: commodityUrl,
			ttl: 11400000, //Time to live set to 4 hours
			// the json file contains an array of strings, but the Bloodhound
			// suggestion engine expects JavaScript objects so this converts all of
			// those strings
			filter: function(list) {
				return $.map(list, function(commodity) { return { code: commodity.code, desc: commodity.desc, 
					descCode: commodityDesc(commodity.desc, commodity.code), commClass: commodity.commClass}; });
			}
		},
		identify: function(comm){return comm.code;}
	});
	
	function commodityDesc(desc, code){
		var matches = desc.match(/\b(\w)/g);
		var acronym = matches.join('');
		if (acronym.length > 1)
			return desc + ' ' + acronym + ' ' + code;	
		else
			return desc + ' ' + code;
	}
	
	function commoditiesWithDefaults(q, sync){
		if (q === '' && $('#commodityDesc').val() === ''){
			if ('BOISEW' === $('#clientGroup').val()){
				//LVL, Lumber, Particleboard, Plywood
				sync(commodityBloodHound.get('2439120', '2421184', '2499610','2432158'));	
			}else if ('BOISEB' === $('#clientGroup').val()){
				//LVL, Lumber, Plywood, Thermoply, PB
				sync(commodityBloodHound.get('2439120', '2421184', '2499110', '2499610', '2432158','2661935'));
			}
		}else{
			commodityBloodHound.search(q, sync);
		}
	}
	
	commodityBloodHound.clearPrefetchCache();
	commodityBloodHound.initialize(true);
    $('#commodityDesc').typeahead('destroy');
	// passing in `null` for the `options` arguments will result in the default
	// options being used
    $('#commodityDesc').typeahead({
		minLength: 0,
		hint: true,
		highlight: true
	}, {
		limit: 20,
		name: 'commodities',
		display: 'descCode',
//		source: commodityBloodHound.ttAdapter()
		source: commoditiesWithDefaults
	}).on('typeahead:selected', function (obj, datum) {
		$('#commodityDesc').typeahead('val', datum.desc);
        $('#commodityCode').val(datum.code);
        $('#commodityClass').val(datum.commClass);
        $('#commodityWeight').focus().select();
    });
}

function readSpellCheck(city, state, zip, callback){
	clearValidationErrors();
	$('#' + spellCheckSource + 'WarningIcon').addClass('hide');
	data = {city : $.trim(city).toUpperCase(), state : $.trim(state).toUpperCase()};
	if (zip){
		data.zip = $.trim(zip);
	}
	$.get(spellCheckUrl, data, function(data){
		if (data.length == 0){
			noSpellCheckFound();
		}else if (data.length == 1){
			callback(data[0]);
		}else{
			populateSpellCheckModal(data);
			$('#addressPickModal').modal('show');
		}
	});
}

function noSpellCheckFound(){
	$('#' + spellCheckSource + 'County, #' + spellCheckSource + 'SPLC, #' + spellCheckSource + 'Country').val('');	
	$('#' + spellCheckSource + 'WarningIcon').removeClass('hide');
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
	setUpCurrency();
}

function setUpCurrency(){
	$('#currencyGroup button').click(function(){
		if ($(this).hasClass('disabled') || $(this).hasClass('active')){
			return;
		}
		$('#currencyGroup button').removeClass('active').addClass('disabled');
		$(this).addClass('active');
		$('#currencyGroup i').css('visibility', 'visible');
		changeCurrency($('#quoteTable tbody tr td:nth(7)').text(), $(this).text());
	});
}

function changeCurrency(fromCurrency, toCurrency){
	$.get(readCurrencyUrl, {fromCurrency: fromCurrency, toCurrency: toCurrency}, function(data){

		$('#quoteTable tbody tr').each(function(){
			var originalValue = $(this).find('td:nth(4)').text();
			var newValue = numeral(originalValue).multiply(numeral(data)).format('$0,0.00');
			$(this).find('td:nth(4)').text(newValue);
			$(this).find('td:nth(7)').text(toCurrency.toUpperCase());
		});
		
		$('#quoteModalTable tbody tr').each(function(){
			var originalValue = $(this).find('td:nth(6)').text();
			var newValue = numeral(originalValue).multiply(numeral(data)).format('$0,0.00');
			$(this).find('td:nth(6)').text(newValue);
		});
		
		title = '<p>This currency conversion is only for informational purposes.  Some rounding error may occur within a few cents.  The conversion rate is updated once a month in MOM.</p>'
			+'<p>The conversion from ' + fromCurrency + ' to ' + toCurrency + ' is ' + data + ' for the given ship date.';
		$('#currencyGroup i:last').attr('data-original-title', title).tooltip({html:true, container: 'body'});
	})
		.fail(function(jqXHR, textStatus, errorThrown){
			 var err = eval("(" + jqXHR.responseText + ")");
			alert(err.message);
		})
		.always(function(){
			$('#currencyGroup button').removeClass('disabled');
			$('#currencyGroup i:first').css('visibility', 'hidden');
		});
}

function downloadExcelFile(){
	window.location.href = rateExcelUrl + '?' + lastRateFormObj;
}

function quoteClicked(tableRowElement){
	leg = '---' == $(tableRowElement).find('td:nth(1)').text() ? '1' : $(tableRowElement).find('td:nth(1)').text();
	quoteIndex = $(tableRowElement).find('td:first').text() + ',' + leg;
	console.log(quoteIndex);
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