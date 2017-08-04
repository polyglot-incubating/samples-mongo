function () {
    var lat  = parseFloat(this.latitude);
    var long = parseFloat(this.longitude);
    if( lat >= 40.3 && lat <= 45.3
        && long >= -80.0 && long <= -71.7 ) {
        emit(this.adminName1, 1);
    }
    else {
      emit('OTHERS', 1);
    }
}