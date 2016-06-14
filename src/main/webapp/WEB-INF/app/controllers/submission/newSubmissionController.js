vireo.controller('NewSubmissionController', function ($controller, $scope, OrganizationRepo, SubmissionRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizations = OrganizationRepo.get();

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();

	$scope.setSelectedOrganization = function(organization) {
		OrganizationRepo.setSelectedOrganization(organization);
	};

	$scope.getSelectedOrganization = function() {
		return $scope.selectedOrganization;
	};

	$scope.createSubmission = function() {
		SubmissionRepo.create($scope.getSelectedOrganization().id);
	};
	
});