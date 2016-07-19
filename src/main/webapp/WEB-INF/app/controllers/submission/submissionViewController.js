vireo.controller("SubmissionViewController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionViewColumnRepo, ManagerSubmissionViewColumnRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

  	$scope.submissions = SubmissionRepo.getAll();

  	$scope.columns = [];

  	$scope.userColumns = [];

  	$scope.resultsPerPageOptions = [20, 40, 60, 100, 200, 400, 500, 1000];

  	$scope.resultsPerPage = 100;

  	SubmissionRepo.listen(function() {
		$scope.tableParams.reload();
  	});

  	var updateColumns = function() {
  		$scope.userColumns = ManagerSubmissionViewColumnRepo.getAll();
		$scope.columns = $filter('exclude')(SubmissionViewColumnRepo.getAll(), $scope.userColumns, 'title');
  	};

  	$q.all([SubmissionViewColumnRepo.ready(), ManagerSubmissionViewColumnRepo.ready(), SubmissionRepo.ready()]).then(function(data) {
		updateColumns();

  		$scope.tableParams = new NgTableParams({ }, 
  		{
  			filterDelay: 0, 
  			dataset: $scope.submissions
  		});

  		$scope.tableParams.reload();
	});

  	var listenForManagersSubmissionColumns = function() {
  		return $q(function(resolve) {
  			ManagerSubmissionViewColumnRepo.listen(function() {
  				resolve();
  			});
  		});
  	};

  	var listenForAllSubmissionColumns = function() {
  		return $q(function(resolve) {
  			SubmissionViewColumnRepo.listen(function() {
  				resolve();
  			});
  		});
  	};

	$scope.resetColumns = function() {

		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {
	  		updateColumns();
	  	});

		SubmissionViewColumnRepo.reset();
		ManagerSubmissionViewColumnRepo.reset();

		$scope.closeModal();
	};

	$scope.resetColumnsToDefault = function() {
		ManagerSubmissionViewColumnRepo.resetSubmissionViewColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.saveColumns = function() {
		ManagerSubmissionViewColumnRepo.updateSubmissionViewColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.getSubmissionProperty = function(row, col) {
		var value;
		for(var i in col.path) {
			if(value === undefined) {
				value = row[col.path[i]];
			}
			else {
				value = value[col.path[i]];
			}
		}
		return value;
	};

	$scope.columnOptions = {
		accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
			return true;
		},
		itemMoved: function (event) {			
			if(event.source.sortableScope.$id < event.dest.sortableScope.$id) {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : undefined;
				
			}
			else {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'pervisoulyDisabled' : undefined;
			}
		},
		orderChanged: function (event) {

		},
		containment: '#column-modal',
		additionalPlaceholderClass: 'column-placeholder'
	};

});

vireo.filter('exclude', function() {
    return function(input, exclude, prop) {
        if (!angular.isArray(input)) {
            return input;
		}
        if (!angular.isArray(exclude)) {
        	exclude = [];
        }
        if (prop) {
            exclude = exclude.map(function byProp(item) {
                return item[prop];
            });
        }
        return input.filter(function byExclude(item) {
            return exclude.indexOf(prop ? item[prop] : item) === -1;
        });
    };
});
