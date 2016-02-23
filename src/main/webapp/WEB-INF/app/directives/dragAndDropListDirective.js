vireo.directive("draganddroplist", function() {
	return {
		templateUrl: 'views/directives/dragAndDropList.html',
		restrict: 'E',
		scope: {
			'scopeValue': '=',
			'reorder': '&',
			'itemView': '@'
		},
		controller: function($scope) {
			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html'
			}
			$scope.dragControlListeners = {
			    accept: function (sourceItemHandleScope, destSortableScope) {
			     	return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
			    },
			    itemMoved: function (event) {

			    },
			    orderChanged: function(event) {
			    	var from = event.source.index + 1;
			    	var to = event.dest.index + 1;
			    	$scope.reorder({'from': from, 'to': to});
			    }
			};
		},
		link: function($scope, elem, attr) {
			$scope.properties = angular.fromJson(attr.properties);
		}	
	};
});
