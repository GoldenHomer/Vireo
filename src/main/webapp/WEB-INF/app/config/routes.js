vireo.config(function($locationProvider, $routeProvider) {
	
	$locationProvider.html5Mode(true);
	
	$routeProvider.
		when('/myprofile', {
			templateUrl: 'views/myprofile.html',
			controller: 'SettingsController'
		}).
		when('/submission/history', {
			templateUrl: 'views/submission/submissionHistory.html',
			access: ["ADMINISTRATOR", "MANAGER", "STUDENT"]
		}).
		when('/submission/new', {
			templateUrl: 'views/submission/newSubmission.html',
			controller: 'NewSubmissionController',
			access: ["ADMINISTRATOR", "MANAGER", "STUDENT"]
		}).
		when('/submission/complete', {
			templateUrl: 'views/submission/submissionComplete.html',
			controller: 'SubmissionCompleteController',
			access: ["ADMINISTRATOR", "MANAGER", "STUDENT"]
		}).
		when('/submission/:submissionId/:workflowStep', {
			templateUrl: 'views/submission/submission.html',
			controller: 'StudentSubmissionController',
			access: ["ADMINISTRATOR", "MANAGER", "STUDENT"]
		}).
		when('/submission/:submissionId', {
			templateUrl: 'views/submission/submission.html',
			controller: 'StudentSubmissionController',
			access: ["ADMINISTRATOR", "MANAGER", "STUDENT"]
		}).
		when('/users', {
			templateUrl: 'views/users.html'
		}).
		when('/register', {
			templateUrl: 'views/register.html'
		}).
		when('/admin', {
			redirectTo: '/admin/list',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/list', {
			templateUrl: 'views/admin/admin.html',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/view/:tab/:id', {
			templateUrl: 'views/admin/admin.html',
			access: ["ADMINISTRATOR"],
			reloadOnSearch: false
		}).
		when('/admin/log', {
			templateUrl: 'views/admin/admin.html',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/settings', {
			redirectTo: '/admin/settings/application',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/settings/:tab', {
			templateUrl: 'views/admin/admin.html',
			access: ["ADMINISTRATOR"],
			controller: 'SettingsController',
			reloadOnSearch: false
		}).
		when('/home', {
			templateUrl: 'views/home.html'
		}).
		when('/', {
			redirectTo: '/home'
		}).

		// Error Routes
		when('/error/403', {
			templateUrl: 'views/errors/403.html',
			controller: 'ErrorPageController'
		}).
		when('/error/404', {
			templateUrl: 'views/errors/404.html',
			controller: 'ErrorPageController'

		}).
		when('/error/500', {
			templateUrl: 'views/errors/500.html',
			controller: 'ErrorPageController'
		}).
		otherwise({
			redirectTo: '/error/404'
		});

});