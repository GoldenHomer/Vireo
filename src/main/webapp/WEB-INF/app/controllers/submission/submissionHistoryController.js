vireo.controller('SubmissionHistoryController', function ($controller, $scope, NgTableParams, StudentSubmissionRepo) {

  	$scope.submissions = StudentSubmissionRepo.getAll();

  	StudentSubmissionRepo.ready().then(function() {
  		$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
  		$scope.tableParams.reload();
  	})

  	StudentSubmissionRepo.listen(function() {
		$scope.tableParams.reload();
  	});

});
