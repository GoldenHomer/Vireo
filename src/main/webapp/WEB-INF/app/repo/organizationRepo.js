vireo.repo("OrganizationRepo", function OrganizationRepo($q, WsApi) {

	var selectedOrganization = {};

	// additional repo methods and variables

	this.newOrganization = {};

	var extendWithOverwrite = function(targetObj, srcObj) {
		var srcKeys = Object.keys(srcObj);
		angular.forEach(srcKeys, function(key){
			targetObj[key] = srcObj[key];
		});
		
		var targetKeys = Object.keys(targetObj);
		angular.forEach(targetKeys, function(key){
			if(typeof srcObj[key] === undefined) {
				delete targetObj[key];
			}
		});
	};


	this.selectiveListen = function() {
		var organizationRepo = this;
		WsApi.listen(this.mapping.selectiveListen).then(null, null, function(rawApiResponse){
			var broadcastedOrg = JSON.parse(rawApiResponse.body).payload.Organization;

			console.log(broadcastedOrg)
			if (broadcastedOrg.id == selectedOrganization.id) {
				organizationRepo.setSelectedOrganization(broadcastedOrg);
			}
		});
	};

	this.selectiveListen();


	this.resetNewOrganization = function() {
		for(var key in this.newOrganization) {
			delete this.newOrganization[key];
		}
	};

	this.getNewOrganization = function() {
		return this.newOrganization;
	};

	this.getSelectedOrganization = function() {
		return selectedOrganization;
	}

	this.setSelectedOrganization = function(organization){
		this.lazyFetch(organization.id).then(function(fetchedOrg) {
			extendWithOverwrite(selectedOrganization, fetchedOrg);
		});
		return selectedOrganization;
	}

	// TODO: replace with abstract findById
	this.findOrganizationById = function(id) {

		var matchedOrganization = null;

		angular.forEach(this.data.list, function(orgToCompare) {
			if(orgToCompare.id === id) {
				matchedOrganization = orgToCompare;
			}
		});

		return matchedOrganization;
	};

	this.lazyFetch = function(orgId) {

		var fetchedOrgDefer = new $q.defer();

		angular.extend(this.mapping.get, {
			'method': 'get/' + orgId
		});

		var getOrgPromise = WsApi.fetch(this.mapping.get);


		var organizationRepo = this;

		getOrgPromise.then(function(rawApiResponse) {
			
			var fetchedOrg = JSON.parse(rawApiResponse.body).payload.Organization;

			angular.extend(organizationRepo.mapping.workflow, {
				'method': fetchedOrg.id + '/workflow'
			});

			var workflowStepsPromise = WsApi.fetch(organizationRepo.mapping.workflow);

			workflowStepsPromise.then(function(data) {
				var aggregateWorkflowSteps = JSON.parse(data.body).payload.PersistentList;
				
				if(aggregateWorkflowSteps !== undefined) {
					fetchedOrg.aggregateWorkflowSteps = aggregateWorkflowSteps;
				}
				fetchedOrgDefer.resolve(fetchedOrg);
			});

		});

		return fetchedOrgDefer.promise;
	};

	this.getChildren = function(id) {
		angular.extend(this.mapping.children, {
			'method': 'get-children/' + id
		});
		return WsApi.fetch(this.mapping.children);		
	};

	this.addWorkflowStep = function(newWorkflowStepName) {
		angular.extend(this.mapping.addWorkflowStep, {
			'method': this.getSelectedOrganization().id + '/create-workflow-step/' + newWorkflowStepName
		});
		return WsApi.fetch(this.mapping.addWorkflowStep);
	};

	this.updateWorkflowStep = function(workflowStepToUpdate) {
		angular.extend(this.mapping.addWorkflowStep, {
			'method': this.getSelectedOrganization().id + '/update-workflow-step',
			'data': workflowStepToUpdate
		});
		return WsApi.fetch(this.mapping.addWorkflowStep);
	};

	this.reorderWorkflowStep = function(upOrDown, workflowStepID) {
		angular.extend(this.mapping.addWorkflowStep, {
			'method': this.getSelectedOrganization().id + '/' + 'shift-workflow-step-' + upOrDown + '/' + workflowStepID
		});
		return WsApi.fetch(this.mapping.addWorkflowStep);
	};

	this.deleteWorkflowStep = function(workflowStepID) {
		angular.extend(this.mapping.addWorkflowStep, {
			'method': this.getSelectedOrganization().id + '/' + 'delete-workflow-step/' + workflowStepID
		});
		return WsApi.fetch(this.mapping.addWorkflowStep);
	};

	return this;

});