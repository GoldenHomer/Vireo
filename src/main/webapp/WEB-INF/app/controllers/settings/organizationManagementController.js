vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, OrganizationRepo, OrganizationCategoryRepo, WorkflowStepRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationCategories = OrganizationCategoryRepo.get();

	$scope.ready = $q.all([OrganizationRepo.ready(),OrganizationCategoryRepo.ready()]);

	$scope.managedOrganization = null;

	$scope.ready.then(function() {

		$scope.updateOrganization = function(organization) {
			OrganizationRepo.update(organization).then(function() {
				//update the parent scoped selected organization
				// TODO: confirm this is necessary 
				$scope.setSelectedOrganization(organization);
			});
		};

		$scope.getManagedOrganization = function() {
			var currentOrganization = $scope.getSelectedOrganization();
			if (currentOrganization !== undefined && currentOrganization) {
				if (!$scope.managedOrganization || $scope.managedOrganization.id != currentOrganization.id) {
					$scope.managedOrganization = angular.copy(currentOrganization);
				}
			}
			return $scope.managedOrganization;
		};

		$scope.resetManagedOrganization = function() {
			$scope.managedOrganization = angular.copy($scope.getSelectedOrganization());
		};

		$scope.addWorkflowStep = function(newWorkflowStepName) {
			OrganizationRepo.addWorkflowStep($scope.selectedOrganization, newWorkflowStepName).then(function() {
				angular.element("#addWorkflowStepModal").modal("hide");		
			}); 
		};

		$scope.deleteWorkflowStep = function(workflowStepID) {
			OrganizationRepo.deleteWorkflowStep(workflowStepID);
		};
		
		$scope.updateWorkflowStep = function(workflowStepToUpdate) {
			OrganizationRepo.updateWorkflowStep(workflowStepToUpdate);
		};

		$scope.reorderWorkflowStepUp = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("up", workflowStepID);
		};

		$scope.reorderWorkflowStepDown = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("down", workflowStepID);
		};

	});
});
