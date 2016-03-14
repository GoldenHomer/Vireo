vireo.controller("AvailableDocumentTypesController", function ($controller, $scope, AvailableDocumentTypesRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.documentTypes = AvailableDocumentTypesRepo.get();
	
	$scope.ready = AvailableDocumentTypesRepo.ready();

	$scope.dragging = false;

	$scope.trashCanId = 'available-document-types-trash';
	
	$scope.sortAction = "confirm";

        $scope.modalData = {};

        $scope.degreeLevels = { 'UNDERGRADUATE' : 'Undergraduate',
                                'MASTERS'       : 'Masters'      ,
                                'DOCTORAL'      : 'Doctoral'     };

        $scope.modalData = {};
        $scope.modalData.name = '';
        $scope.modalData.degreeLevel = 'UNDERGRADUATE';

        $scope.clearModalData = function(){
            $scope.modalData = {};
            $scope.modalData.name = '';
            $scope.modalData.degreeLevel = 'UNDERGRADUATE';
        }

        $scope.createNewDocumentType = function(documentType) {
            AvailableDocumentTypesRepo.add(documentType);
	};	

        $scope.launchEditModal = function(index) {
            $scope.modalData = $scope.documentTypes.list[index];
            angular.element('#availableDocumentTypesEditModal').modal('show');
	};	

        $scope.updateDocumentType = function(){
            AvailableDocumentTypesRepo.update($scope.modalData);
            $scope.clearModalData();
        }

        $scope.removeDocumentType = function(index){
            console.info('idx to remove: ' + index);
            AvailableDocumentTypesRepo.remove(index);
        }

        $scope.reorderDocumentTypes = function(src, dest) {
            AvailableDocumentTypesRepo.reorder(src, dest);
        };

        $scope.selectDocumentType = function(index) {
                // $scope.resetMonthOptions();
                $scope.modalData = $scope.documentTypes.list[index];
        };

        $scope.sortDocumentTypes = function(column) {
        if($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
        }
        else if($scope.sortAction == 'sort') {
                AvailableDocumentTypesRepo.sort(column);
                $scope.sortAction = 'confirm';
        }};

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
                trashId: $scope.trashCanId,
                dragging: $scope.dragging,
                select: $scope.selectDocumentType,			
                list: $scope.documentTypes.list,
                confirm: '#availableDocumentTypesConfirmRemoveModal',
                reorder: $scope.reorderDocumentTypes,
                container: '#available-document-types'
        });

});
