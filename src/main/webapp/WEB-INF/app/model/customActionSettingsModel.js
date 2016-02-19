vireo.service("CustomActionSettings", function(AbstractModel, WsApi) {
	var self;

	var CustomActionSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	CustomActionSettings.data = null;

	CustomActionSettings.promise = null;

	CustomActionSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};
	

	CustomActionSettings.get = function() {
		if(CustomActionSettings.promise) return CustomActionSettings.data;
		
		var newAllCustomActionSettingsPromise = WsApi.fetch({
								endpoint: '/private/queue', 
								controller: 'settings/custom-action', 
								method: 'all'
		});
		CustomActionSettings.promise = newAllCustomActionSettingsPromise;
		CustomActionSettings.data = new CustomActionSettings(newAllCustomActionSettingsPromise);

		CustomActionSettings.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/custom-action', 
			method: '',
		});
				
		CustomActionSettings.set(CustomActionSettings.listener);

		return CustomActionSettings.data;

	};

	CustomActionSettings.create = function(label, isStudentVisible) {		
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/custom-action',
			method:'create',
			data: {'label':label, 'isStudentVisible':isStudentVisible}
		}).then(function(response) {
			console.log(response);
			console.log(JSON.parse(response.body).payload);
		});		
	};

//	CustomActionSettings.reset = function(type,setting) {
//		WsApi.fetch({
//			endpoint:'/private/queue',
//			controller:'settings/configurable',
//			method:'reset',
//			data: {'type':type, 'setting':setting}
//		});
//	};

	CustomActionSettings.ready = function() {
		return CustomActionSettings.promise;
	};

	CustomActionSettings.listen = function() {
		return CustomActionSettings.listener;
	};

	return CustomActionSettings;
});