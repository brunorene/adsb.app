var map;

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

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
    if(getParameterByName("client")) {
        xhttp.open("GET", "poll/" + getParameterByName("client"), true);
        xhttp.send();
    }
  }
  setInterval(repeat, 5000);
}