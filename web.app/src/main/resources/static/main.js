var map;
function initMap() {
  var markers = [];
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 38.651518, lng: -9.185236},
    zoom: 11
  });
  function repeat() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        for(i=0; i<markers.length; i++) {
          markers[i].setMap(null);
        }
        markers = [];
        flights = JSON.parse(this.responseText)
        for(i=0; i<flights.length; i++) {
          markers.push(new google.maps.Marker({
            position: {lat: flights[i].info[0].latitude, lng: flights[i].info[0].longitude},
            map: map,
            title: flights[i].flightId + ' (' + flights[i].info[0].distanceFromHome + 'km)'
          }));
        }
      }
    };
    xhttp.open("GET", "poll/571efcc0-53a4-11e7-b457-a434d936d7dd", true);
    xhttp.send();
  }
  setInterval(repeat, 5000);
}