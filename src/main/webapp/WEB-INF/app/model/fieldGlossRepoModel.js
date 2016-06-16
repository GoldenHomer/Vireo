vireo.service("FieldGlossModel", function($q, WsApi, VireoAbstractModel) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-gloss',
			method    : ''
		}
	};

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		VireoAbstractModel.getAllPromise(api, cache);
		return cache.list;
	};

	this.addGloss = function(gloss){
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, 'create', gloss));
	};

	this.glossWithValueExists = function(value) {
		var retVal = false;
		if (!cache.ready) { //If for this function is called before InputTypeService.getAll(), our cache would be empty.
			VireoAbstractModel.getAllPromise(api, cache).then(function(){ //Now we can be sure the cache is full. Proceed with evaluation.
				angular.forEach(cache.list, function(glossInCache){
					if (value == glossInCache.value) {
						retVal = true;
					}
				});
			});
		}else{ //Cache is available. Evaluate right away.
			angular.forEach(cache.list, function(glossInCache){
				if (value == glossInCache.value) {
					retVal = true;
				}
			});
		}
		return retVal;
	};

});
