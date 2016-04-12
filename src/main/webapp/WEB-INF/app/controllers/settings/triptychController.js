vireo.controller("TriptychController", function ($controller, $scope, $q, $timeout, OrganizationRepo, OrganizationCategoryRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {

        $scope.triptych = new Triptych($scope.organizations.list[0]);

        $scope.shiftPanels = function(panel, organization) {

            var panelIndex = $scope.triptych.openPanels.indexOf(panel);
            var nextPanelIndex = panelIndex + 1;
            var hasHistory = $scope.triptych.panelHistory.length > 0;
            var orgHasChildren = organization.childrenOrganizations.length > 0;
            var isFirstPanel = panelIndex == 0;
            var isLastPanel = panelIndex == 2;

            //TODO: this should be done on lcick of the edit button only?
            $scope.setSelectedOrganization(organization);

            panel.selectedOrganization = organization;

    		$scope.triptych.setActivePanel(panel);

            if(orgHasChildren || !isLastPanel) {
            	$scope.triptych.addPanel(organization, nextPanelIndex).show();  
            } 

            if(orgHasChildren && isLastPanel) {
                var newPanel = $scope.triptych.addPanel(organization, nextPanelIndex);

                $scope.triptych.openPanels[0].close().then(function() {
                    newPanel.show();
                });
            } 

            if(isFirstPanel) {
            	if(hasHistory) {
                    $scope.triptych.retrievePanel(); 
                } else {
                    $scope.triptych.removePanels();
            	}
            }

        } 

        $scope.filterPanelByParent = function(panel, organization) {

            if(!panel) return false;

            var panelParentOrganization = panel.parentOrganization

            if(organization.parentOrganizations.indexOf(panelParentOrganization.id) != -1) {
            	panel.organizationCatagories.push($scope.getCategoryById(organization.category))
            	return true;
            } 
            
            return false;
            
        }

        $scope.getCategoryById = function(id) {
        	return OrganizationCategoryRepo.findById(id);
        }

    });

    var Triptych = function(organization) {

        var Triptych = this;

        Triptych.activePanel;
        Triptych.panelHistory = [];
        Triptych.openPanels = [];
        Triptych.rootPanel = Triptych.addPanel(organization);
        Triptych.rootPanel.show();

        return Triptych;
    }

    Triptych.prototype = {
        resetPanels: function() {
            var Triptych = this;
            Triptych.activePanel;
            Triptych.panelHistory = [];
            Triptych.openPanels = [Triptych.rootPanel];
        },
        addPanel: function(organization, index) {
            var Triptych = this;

            var panelToAdd = new Panel(organization);
            panelToAdd.triptych = Triptych;

            if(!index) {
                Triptych.openPanels.unshift(panelToAdd);    
            } else {
                Triptych.openPanels[index] = panelToAdd;
            }

            return panelToAdd;
            
        },
        removePanels: function() {
            var Triptych = this;
            Triptych.openPanels.splice(2, 1);  
        },
        storePanel: function(panel) {
            var Triptych = this;
            Triptych.panelHistory.push(panel);
        },
        retrievePanel: function(panel) {

            var Triptych = this;

            Triptych.openPanels.pop();
                    
            var oldPanel = !panel ? Triptych.panelHistory.pop() : panel;
            oldPanel.closing = true;
            Triptych.openPanels.unshift(oldPanel);
            
            $timeout(function() {
                oldPanel.open();
            });

        },
        rewindPanels: function(panel) {

            var Triptych = this;

            var indexOfPanel = Triptych.panelHistory.indexOf(panel);
            var numberToRemove = Triptych.panelHistory.length - indexOfPanel;
            var removedEntries = Triptych.panelHistory.splice(indexOfPanel, numberToRemove);

            for(var i in removedEntries.reverse()) {
                var panelToAdd = removedEntries[i];
                Triptych.openPanels.unshift(panelToAdd);
                Triptych.openPanels.pop();
            } 

        },
        setActivePanel: function(panel) {
            for(var i in $scope.triptych.openPanels) {
                if($scope.triptych.openPanels[i].active) $scope.triptych.openPanels[i].previouslyActive = true;
                $scope.triptych.openPanels[i].active = false;
            }

            panel.previouslyActive = false;
            panel.active = true;
        }
    }

    var Panel = function(parentOrganization) {
        var Panel = this;
        Panel.triptych;
        Panel.parentOrganization = parentOrganization;
        Panel.organizationCatagories = [];
        Panel.selectedOrganization;
        Panel.previouslyActive = false;
        Panel.active = false;
        Panel.closing = false;
        Panel.opening = false;
        Panel.visible = false;
        return this;
    }

    Panel.prototype = {
        open: function() {
            var Panel = this;
            var defer = $q.defer();
    
            Panel.closing = false;
            Panel.opening = true;

            setTimeout(function() {
                Panel.opening = false;
                defer.resolve();
            }, 355);
            
            return defer.promise;
        },
        close: function() {
            var Panel = this;
            var defer = $q.defer();
        
            Panel.closing = true;

            setTimeout(function() {
                Panel.closing = false;
                defer.resolve();
            }, 355);

            defer.promise.then(function() {
                Panel.triptych.storePanel($scope.triptych.openPanels[0]);
                Panel.triptych.openPanels.shift();
            });
            
            return defer.promise;
        },
        show: function() {
            var Panel = this;
            if(!Panel.visible) {
                $timeout(function(){
                   Panel.visible = true; 
                });    
            }
        },
        hide: function() {
            var Panel = this;
            Panel.visible = false;
        },
        getPanelCatagories: function() {
            var Panel = this;
            return  Panel.organizationCatagories.filter(function(item, pos) {
                return Panel.organizationCatagories.indexOf(item) == pos;
            });
        }
    }

});
