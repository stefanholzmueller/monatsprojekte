var module = angular.module("direction", ['ngMap']);

module.controller("directionCtrl", function ($scope, $timeout) {
    $scope.$on('mapInitialized', function (event, map) {
        navigator.geolocation.watchPosition(function (position) {

            var currentPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            map.setCenter(currentPosition)
            map.markers.arrow.setPosition(currentPosition);

            if (position.coords.heading) {
                map.markers.arrow.icon.rotation = 45;
                map.markers.arrow.setIcon(map.markers.arrow.icon);
            }

        }, function (error) {
            alert(error);
        }, {timeout: 5000});
    });

});