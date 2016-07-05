vireo.controller('SubmissionHistoryController', function ($controller, $scope, NgTableParams, SubmissionRepo) {

  	$scope.submissions = SubmissionRepo.getAll();

  	SubmissionRepo.ready().then(function() {
  		$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
  		$scope.tableParams.reload();
  		console.log($scope.submissions);
  	})

  	SubmissionRepo.listen(function() {
		$scope.tableParams.reload();
  	});

});
