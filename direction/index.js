var module = angular.module("direction", ['ngMap']);

module.controller("directionCtrl", function ($scope) {
    $scope.$on('mapInitialized', function (event, map) {
        console.log("mapInitialized");

        navigator.geolocation.getCurrentPosition(function (position) {
            var latitude = position.coords.latitude;
            var longitude = position.coords.longitude;
            var path = [new google.maps.LatLng(latitude, longitude), new google.maps.LatLng(118.291, 153.027)];

            map.shapes.directionArrow.setPath(path);
        }, function (error) {
            alert(error);
        }, {timeout: 10000});
    });

});