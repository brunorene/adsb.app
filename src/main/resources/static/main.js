var map;
function initMap() {
  var markers = [];
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 38.651518, lng: -9.185236},
    zoom: 16
  });
  function repeat() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        for(marker in markers) {
          marker.setMap(null);
        }
        markers = [];
        flights = JSON.parse(this.responseText)
        for(flight in flights) {
          markers.push(new google.maps.Marker({
            position: {lat: flight.info[0].latitude, lng: flight.info[0].longitude},
            map: map,
            title: flight.flightId
          }));
        }
      }
    };
    xhttp.open("GET", "poll/571efcc0-53a4-11e7-b457-a434d936d7dd", true);
    xhttp.send();
  }
  setInterval(repeat, 5000);
}



        var myLatLng = ;

        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 4,
          center: myLatLng
        });

