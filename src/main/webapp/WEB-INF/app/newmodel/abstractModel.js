// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModelNew", function($q, $timeout, WsApi) {

	var defer = $q.defer();

	var cache;
	
	this.init = function(data) {
		if(data !== undefined) {
			console.log('has data')
			angular.extend(this, data);
			defer.resolve();
		}
		if(cache !== undefined) {
			console.log('using cache')
			angular.extend(this, cache);
			defer.resolve();
		}
		else {
			console.log('has data')
			var abstractModel = this;
			WsApi.fetch(this.mapping.create).then(function(res) {
				cache = {};
				angular.extend(cache, angular.fromJson(res.body).payload.PersistentMap);
				angular.extend(abstractModel, cache);
				defer.resolve();
			});
		}
	}

	this.ready = function() {
		return defer.promise;
	};

	this.save = function() {
		console.log('save');
	};

	this.delete = function() {
		console.log('delete');
	};

	this.listen = function() {
		console.log('listen');
	};
	
	// additional core level model methods and variables
	
	return this;
	
});