var submissionModel = function (WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables


		submission.findFieldValuesByPredicate = function(predicate) {

			var foundFieldValues = [];

			for(var i in submission.fieldValues) {

				var fieldValue = submission.fieldValues[i];

				if(fieldValue.predicate.value == predicate.value) {
					foundFieldValues.push(fieldValue);
				}

			}

			return foundFieldValues;

		};

		submission.findFieldValueById = function(id) {

			var foundFieldValue = null;

			for(var i in submission.fieldValues) {

				var fieldValue = submission.fieldValues[i];

				if(fieldValue.id == id) {
					foundFieldValue = fieldValue;
					break;
				}

			}

			return foundFieldValue;

		};

		submission.addFieldValue = function(predicate) {
			var fieldValue = {
				id: null,
				value: "",
				predicate: predicate
			};

			submission.fieldValues.push(fieldValue);

			return fieldValue;

		};

		submission.saveFieldValue = function(fieldValue) {
			angular.extend(this.mapping().saveFieldValue, {
				method: submission.id+"/update-field-value",
				data: fieldValue
			});
			return WsApi.fetch(this.mapping().saveFieldValue);
		};

		return submission;
	}

}

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);