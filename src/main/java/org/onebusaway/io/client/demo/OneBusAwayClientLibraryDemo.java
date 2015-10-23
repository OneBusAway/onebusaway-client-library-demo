/**
 * Copyright (C) 2015 Sean J. Barbeau
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.io.client.demo;

import org.onebusaway.io.client.ObaApi;
import org.onebusaway.io.client.elements.ObaRegion;
import org.onebusaway.io.client.elements.ObaStop;
import org.onebusaway.io.client.request.ObaRegionsRequest;
import org.onebusaway.io.client.request.ObaRegionsResponse;
import org.onebusaway.io.client.request.ObaStopsForLocationRequest;
import org.onebusaway.io.client.request.ObaStopsForLocationResponse;
import org.onebusaway.location.Location;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

public class OneBusAwayClientLibraryDemo {

    public static void main(String[] args) throws IOException {
        // Set the API key to be used - should be changed to your API key
        ObaApi.getDefaultContext().setApiKey("TEST");
        ObaRegionsResponse response = null;

        try {
            // Call the OBA Regions API (http://regions.onebusaway.org/regions-v3.json)
            response = ObaRegionsRequest.newRequest().call();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ArrayList<ObaRegion> regions = new ArrayList<ObaRegion>(Arrays.asList(response.getRegions()));
        for (ObaRegion r : regions) {
            if (r.getName().equalsIgnoreCase("Tampa")) {
                ObaApi.getDefaultContext().setRegion(r);
                // Get the stops for the region named "Tampa"
                callGetStops();
            }
        }

        /**
         * An example of setting a custom API server
         */

        // First, clear the region, if it was already set
        ObaApi.getDefaultContext().setRegion(null);

        // Set the custom API
        String url = "http://api.tampa.onebusaway.org/api/";
        ObaApi.getDefaultContext().setBaseUrl(url);

        // Get the stops from a custom API
        callGetStops();
    }

    private static void callGetStops() throws IOException {
        Location l = new Location("Test");
        l.setLatitude(28.0664191);
        l.setLongitude(-82.4298721);
        ObaStopsForLocationResponse response2 = null;
        // Call the OBA stops-for-location API (http://developer.onebusaway.org/modules/onebusaway-application-modules/current/api/where/methods/stops-for-location.html)
        try {
            response2 = new ObaStopsForLocationRequest.Builder(l)
                    .setQuery("3105")  // Request info for stop ID 3105
                    .build()
                    .call();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final ObaStop[] list = response2.getStops();
        for (ObaStop s : list) {
            System.out.println(s.getName() + "\n");
        }
    }
}