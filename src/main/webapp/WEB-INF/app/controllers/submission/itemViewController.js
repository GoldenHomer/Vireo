vireo.controller("ItemViewController", function ($controller, $routeParams, $scope, ItemViewService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	ItemViewService.selectSubmissionById($routeParams.id).then(function(submission) {

		$scope.submission = submission;

		var firstName = $scope.submission.submitter.firstName;
		var lastName = $scope.submission.submitter.lastName;
		var organization = $scope.submission.organization.name;
			
		$scope.title = lastName + ', ' + firstName + ' (' + organization + ')';
		
		$scope.showTab = function(workflowStep) {
			var show = false;
			for(var i in workflowStep.aggregateFieldProfiles) {
				if(workflowStep.aggregateFieldProfiles[i].inputType.name !== 'INPUT_FILE') {
					show = true;
					break;
				}
			}
			return show;
		};
		
		$scope.getTabPath = function(path) {
			return path + "/" + $scope.submission.id;
		};
		
		
		$scope.editReviewerNotes = function() {
			$scope.editingReviewerNotes = true;
		};
		
		$scope.saveReviewerNotes = function() {
			$scope.savingReviewerNotes = true;
			$scope.editingReviewerNotes = false;
			$scope.submission.saveReviewerNotes($scope.submission.reviewerNotes).then(function(response) {
				$scope.savingReviewerNotes = false;
			});
		};
		
		$scope.cancelReviewerNotes = function() {
			$scope.editingReviewerNotes = false;
			$scope.submission.refresh();
		};

		
	});

});
