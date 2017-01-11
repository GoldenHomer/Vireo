vireo.controller("StudentSubmissionController", function ($controller, $scope, $routeParams, StudentSubmissionRepo, Submission) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.onLastStep = function() {
		return true;
	};
	
	StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {
		$scope.submission = new Submission(angular.fromJson(data.body).payload.Submission);
		$scope.setAcitveStep($scope.submission.submissionWorkflowSteps[0]);


		$scope.onLastStep = function() {
			var currentStepIndex = $scope.submission.submissionWorkflowSteps.indexOf($scope.nextStep);
			return currentStepIndex === -1;
		};

	});

    // TODO - typo; "AcitveStep" should be "ActiveStep" -- needs refactoring in other places
	$scope.setAcitveStep = function(step) {
		if(step) {
			var stepIndex = $scope.submission.submissionWorkflowSteps.indexOf(step);
			$scope.nextStep = $scope.submission.submissionWorkflowSteps[stepIndex+1];
			$scope.activeStep = step;
		}
	};

});