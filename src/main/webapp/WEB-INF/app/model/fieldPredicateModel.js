vireo.service("FieldPredicateModel", function($q, WsApi) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-predicates',
			method    : 'all'
		}
	};

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAllPromise = function() {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		return WsApi.fetch(api.request).then(function(response){
			var payload = angular.fromJson(response.body).payload;
			cache.list.length = 0;
			angular.forEach(Object.keys(payload), function(key){
				if (key.indexOf('ArrayList') > -1) {
					angular.extend(cache.list, payload[key]);
				} else {
					cache[key] = payload[key];
				}
			});
			cache.ready = true;
		});
	};

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		this.getAllPromise();
		return cache.list;
	};

	this.predicateWithValueExists = function(value) {
		var retVal = false;
		if (!cache.ready) {
			this.getAllPromise().then(function(){ //If for this is called before InputTypeService.getAll(), our cache would be empty.
				//Now we can be sure the cache is full. Proceed with evaluation.
				angular.forEach(cache.list, function(predicateInCache){
					if (value == predicateInCache.value) {
						console.info('cache not ready match for', value);
						return true;
					}
				});
				return false;
			});
		}else{
			angular.forEach(cache.list, function(predicateInCache){
				if (value == predicateInCache.value) {
					console.info('cache READY match for', value);
					retVal = true;
				}
			});
			
		}
		return retVal;
	};
});

