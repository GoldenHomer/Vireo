vireo.directive("field",  function() {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {

			$scope.saving = 0;

			$scope.submission = $scope.$parent.submission;

			$scope.values = [];

			$scope.getValues = function() {
				angular.extend($scope.values, $scope.submission.findFieldValuesByFieldPredicate($scope.profile.fieldPredicate));
				if ($scope.values.length === 0) {
					$scope.values.push({
						id: null,
						value: "",
						fieldPredicate: $scope.profile.fieldPredicate
					});
				}
				return $scope.values;
			};

			$scope.save = function(value) {

				if ($scope.fieldProfileForm.$dirty) {
					$scope.saving = value.id;
					$scope.submission.saveFieldValue(value).then(function() {
						$scope.saving = 0;
						$scope.fieldProfileForm.$setPristine();
					});
				}

			};

			$scope.addFieldValue = function() {
				$scope.submission.addFieldValue($scope.profile.fieldPredicate);
			};

			$scope.removeFieldValue = function(value) {
				var indexOfValue = $scope.submission.fieldValues.indexOf(value);
				$scope.submission.fieldValues.splice(indexOfValue, 1);
			};

			$scope.filterValuesByFieldPredicate = function(value) {
				return $scope.profile.fieldPredicate.id === value.fieldPredicate.id;
			};

			$scope.showRemove = function(value) {
				return $scope.profile.repeatable && !$scope.first(value);
			};

			$scope.showAdd = function(value) {
				return $scope.profile.repeatable && $scope.first(value);
			};

			$scope.first = function(value) {
				return $scope.submission.findFieldValuesByPredicate($scope.profile.fieldPredicate).indexOf(value) === 0;
			};

			$scope.getPatern = function() {
				var patern = "";
				var cv = $scope.profile.controlledVocabularies[0];
				//console.log(cv);
				return patern;
			};

			$scope.begineUpload = function(file) {
				$scope.file = file;
			};

			$scope.getPreview = function(index) {
				var preview = null;
				if($scope.file.type.includes("image/")) preview = $scope.values[index].value;
				return preview;
			};

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});