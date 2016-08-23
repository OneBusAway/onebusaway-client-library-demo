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
import org.onebusaway.io.client.request.*;
import org.onebusaway.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.onebusaway.io.client.ObaApi.OBA_OK;

public class OneBusAwayClientLibraryDemo {

    static final int LOOP_COUNT = 10000;
    static final long SLEEP_TIME_MS = 100;
    static final String API_KEY = "TEST";

    public static void main(String[] args) throws IOException, InterruptedException {
        // Set the API key to be used - should be changed to your API key
        ObaApi.getDefaultContext().setApiKey(API_KEY);
        ObaRegionsResponse response = null;

        // Call the OBA Regions API (http://regions.onebusaway.org/regions-v3.json)
        response = ObaRegionsRequest.newRequest().call();
        ArrayList<ObaRegion> regions = new ArrayList<ObaRegion>(Arrays.asList(response.getRegions()));
        for (ObaRegion r : regions) {
            if (r.getExperimental() || !r.getSupportsObaRealtimeApis() || !r.getSupportsObaDiscoveryApis() || r.getId() != 1) {
                continue;
            }
            ObaApi.getDefaultContext().setRegion(r);
            long after = 0;

            for (int i = 0; i < LOOP_COUNT; i++) {
                Thread.sleep(SLEEP_TIME_MS);
                if (i != 0) {
                    long elapsed = System.currentTimeMillis() - after;
                    System.out.println("Elapsed time = " + elapsed + "ms");
                }
                callGetStopsForLocationPugetSound();
                after = System.currentTimeMillis();
            }
        }
    }

    private static void callGetAgenciesWithCoverage(ObaRegion r) throws IOException {
        ObaAgenciesWithCoverageResponse response = new ObaAgenciesWithCoverageRequest.Builder()
                .build()
                .call();
        if (response.getCode() != OBA_OK) {
            System.out.println(r.getName() + " - response code = " + response.getCode() + ", text = " + response.getText());
            throw new IOException(r.getName() + " - response code = " + response.getCode() + ", text = " + response.getText());
        }
//        ObaAgencyWithCoverage[] agencies = response.getAgencies();
//        for (int i = 0; i < agencies.length; i++) {
//            System.out.println("Agency = " + response.getAgency(agencies[i].getId()).getName());
//        }
    }

    private static void callGetStopsForLocationPugetSound() throws IOException {
        Location l = new Location("test");
        l.setLatitude(47.61854184);
        l.setLongitude(122.32673695);
        ObaStopsForLocationResponse response = new ObaStopsForLocationRequest.Builder(l)
                .build()
                .call();
        if (response.getCode() != OBA_OK) {
            System.out.println("Stops-for-location() response code = " + response.getCode() + ", text = " + response.getText());
            throw new IOException("Stops-for-location() response code = " + response.getCode() + ", text = " + response.getText());
        }
    }
}