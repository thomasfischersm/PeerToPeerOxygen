package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * A utility for handling the formatting of GPS locations.
 */

public class GpsLocationUtil {

    public static final String COORDINATE_SEPARATOR = ", ";

    public static String toStr(Address address) {
        String latitude = Location.convert(address.getLatitude(), Location.FORMAT_DEGREES);
        String longitude = Location.convert(address.getLongitude(), Location.FORMAT_DEGREES);
        return latitude +  COORDINATE_SEPARATOR + longitude;
    }

    @Nullable
    public static Address fromStr(Context context, String gpsLocation) throws IOException {
        String[] split = gpsLocation.split(COORDINATE_SEPARATOR);
        double latitude = Location.convert(split[0]);
        double longitude = Location.convert(split[1]);
        List<Address> addressList = new Geocoder(context).getFromLocation(latitude, longitude, 1);

        if ((addressList != null) && (addressList.size() == 1)) {
            return addressList.get(0);
        } else {
            return null;
        }
    }
}
