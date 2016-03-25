vireo.service("EmbargoRepo", function(AbstractModel, WsApi, AlertService) {
	
	var self;

	var EmbargoRepo = function(futureData) {
		self = this;
		
		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	EmbargoRepo.data = null;
	
	EmbargoRepo.listener = null;

	EmbargoRepo.promise = null;

	EmbargoRepo.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};

	EmbargoRepo.get = function() {

		if(EmbargoRepo.promise) return EmbargoRepo.data;
		
		var newAllEmbargoRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/embargo', 
			method: 'all',
		});

		EmbargoRepo.promise = newAllEmbargoRepoPromise;
		if (EmbargoRepo.data) {
			newAllEmbargoRepoPromise.then(function(data) {
				EmbargoRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		} else {
			EmbargoRepo.data = new EmbargoRepo(newAllEmbargoRepoPromise);
		}

		EmbargoRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/embargo', 
			method: '',
		});

		EmbargoRepo.listener.then(function(data) {
			debugger;
		});
				
		EmbargoRepo.set(EmbargoRepo.listener);

		return EmbargoRepo.data;

	};

	EmbargoRepo.create = function(embargo) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/embargo',
			method:'create',
			data: embargo
		}).then(function(response) {
		    var responseType = angular.fromJson(response.body).meta.type;
            var responseMessage = angular.fromJson(response.body).meta.message;
            if(responseType != 'SUCCESS') {
                AlertService.add({type: responseType, message: responseMessage}, "/settings/embargo");  
            }
		});		

	};

	EmbargoRepo.update = function(customAction) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/embargo',
			method:'update',
			data: customAction
		}).then(function(response) {
		    var responseType = angular.fromJson(response.body).meta.type;
            var responseMessage = angular.fromJson(response.body).meta.message;
            if(responseType != 'SUCCESS') {
                AlertService.add({type: responseType, message: responseMessage}, "/settings/embargo");  
            }
		});
	};
	
	EmbargoRepo.reorder = function(src, dest) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/embargo', 
			'method': 'reorder/' + src + '/' + dest
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/embargo");  
			}
		});
	};
	
	EmbargoRepo.sort = function(column, where) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/embargo', 
			'method': 'sort/' + column + '/' + where
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/embargo");  
			}
		});
	};
	
	EmbargoRepo.remove = function(index) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/embargo', 
			'method': 'remove/' + index
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/embargo");  
			}
		});
	};

//	EmbargoRepo.reset = function(type,setting) {
//		WsApi.fetch({
//			endpoint:'/private/queue',
//			controller:'settings/configurable',
//			method:'reset',
//			data: {'type':type, 'setting':setting}
//		});
//	};

	EmbargoRepo.ready = function() {
		return EmbargoRepo.promise;
	};

	EmbargoRepo.listen = function() {
		return EmbargoRepo.listener;
	};

	return EmbargoRepo;
});