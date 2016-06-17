vireo.service("FieldProfileModel", function($q, WsApi, VireoAbstractModel) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'workflow-step',
			method    : ''
		}
	};

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		VireoAbstractModel.getAllPromise(api, cache);
		return cache.list;
	};

	this.addFieldProfile = function(profile){
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, profile.requestingOrganizationId + '/add-field-profile', profile));
	};

});
