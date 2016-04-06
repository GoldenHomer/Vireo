vireo.service("ConfigurableSettings", function($sanitize, AbstractModel, WsApi) {
	var self;

	var ConfigurableSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	ConfigurableSettings.data = null;

	ConfigurableSettings.promise = null;

	ConfigurableSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};
	

	ConfigurableSettings.get = function() {
		if(ConfigurableSettings.promise) return ConfigurableSettings.data;
		
		var newAllConfigurableSettingsPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/configurable', 
			method: 'all'
		});
		
		ConfigurableSettings.promise = newAllConfigurableSettingsPromise;
		ConfigurableSettings.data = new ConfigurableSettings(newAllConfigurableSettingsPromise);

		ConfigurableSettings.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/configurable', 
			method: '',
		});
				
		ConfigurableSettings.set(ConfigurableSettings.listener);

		return ConfigurableSettings.data;

	};

	// TODO: test typing for windows control characters
	// TODO: investigate way to maintain css format
	ConfigurableSettings.update = function(type, name, value) {	
		return WsApi.fetch({
				endpoint:'/private/queue',
				controller:'settings/configurable',
				method:'update',
				data: {'type':type, 'name':name,'value': $sanitize(value).replace(new RegExp("&#10;", 'g'), "")
			}
		});
	};

	ConfigurableSettings.reset = function(type,name) {
		return WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/configurable',
			method:'reset',
			data: {'type':type, 'name':name}
		});
	};

	ConfigurableSettings.ready = function() {
		return ConfigurableSettings.promise;
	};

	ConfigurableSettings.listen = function() {
		return ConfigurableSettings.listener;
	};

	return ConfigurableSettings;
});
