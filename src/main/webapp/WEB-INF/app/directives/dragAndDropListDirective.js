vireo.directive("draganddroplist", function() {
	return {
		templateUrl: 'views/directives/dragAndDropList.html',
		restrict: 'E',
		scope: {
			'dragging': '=',
			'scopeValue': '=',			
			'listeners': '=',
			'itemView': '@'
		},
		controller: function($scope) {
			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html'
			}
		},
		link: function($scope, elem, attr) {
			$scope.properties = angular.fromJson(attr.properties);
		}	
	};
});
