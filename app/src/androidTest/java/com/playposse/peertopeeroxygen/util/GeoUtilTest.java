package com.playposse.peertopeeroxygen.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.util.GeoUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A unit test for {@link GeoUtil}.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class GeoUtilTest {

    public static final double DELTA_DOUBLE = 0.0002;
    private Context context;
    private Geocoder geocoder;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        geocoder = new Geocoder(context);
    }

    @Test
    public void toStr() throws IOException {
        assertToString("34.05223, -118.24368", "Los Angeles, CA");
        assertToString("34.00975, -118.46596", "Santa Monica, CA 90405");
        assertToString("34.00807, -118.48572", "4th Street, Santa Monica, CA 90405");
        assertToString("34.00033, -118.44093", "12811 Venice Blvd, Los Angeles, CA 90066");
        assertToString("34.00797, -118.48546", "2121 4th Street, Santa Monica, CA 90405");
    }

    private void assertToString(String expectedGpsCoordinates, String address) throws IOException {
        assertTrue(Geocoder.isPresent());
        List<Address> addressList = geocoder.getFromLocationName(address, 10);
        assertNotNull(addressList);
        assertEquals(1, addressList.size());
        Assert.assertEquals(expectedGpsCoordinates, GeoUtil.toStr(addressList.get(0)));
    }

    @Test
    public void fromStr() throws IOException {
        assertFromString(34.00033, -118.44093, "34.00033, -118.44093");
        assertFromString(34.00797, -118.48546, "34.00797, -118.48546");
    }

    private void assertFromString(
            double expectedLatitude,
            double expectedLongitude,
            String gpsLocation) throws IOException {

        Address address = GeoUtil.fromStr(context, gpsLocation);
        Log.i("bla", gpsLocation + " -> " + address.getLatitude() + ", " + address.getLongitude());
        Log.e("bla", "dadsf");
        assertEquals(expectedLatitude, address.getLatitude(), DELTA_DOUBLE);
        assertEquals(expectedLongitude, address.getLongitude(), DELTA_DOUBLE);
    }
}
