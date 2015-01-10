var module = angular.module("direction", ['ngMap']);

module.controller("directionCtrl", function ($scope) {
    $scope.$on('mapInitialized', function (event, map) {
        console.log("mapInitialized");

    });

    function getPosition() {
        navigator.geolocation.getCurrentPosition(function (position) {
            var latitude = position.coords.latitude;
            var longitude = position.coords.longitude;
            var center = [latitude, longitude];
            console.log("center=" + center);
            return center;
        }, function (error) {
            alert(error);
        });
    }

});