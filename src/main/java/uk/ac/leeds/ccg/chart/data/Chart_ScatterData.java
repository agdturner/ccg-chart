/*
 * Copyright 2021 Centre for Computational Geography, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.chart.data;

import java.util.HashMap;

/**
 *
 * @author Andy Turner
 */
public class Chart_ScatterData extends Chart_Data {

    /**
     * For storing the 2D data.
     */
    public HashMap<Chart_ID, Chart_Point> data;

    /**
     * Create a new instance.
     */
    public Chart_ScatterData() {
        super();
        data = new HashMap();
    }

    /**
     * @param id The ID for the data point.
     * @param xy The data point.
     */
    public void add(Chart_ID id, Chart_Point xy) {
        if (data.containsKey(id)) {
            System.out.println("Warning id already exists!");
        } else {
            if (maxX == null) {
                maxX = xy.x;
                minX = xy.x;
                minY = xy.y;
                maxY = xy.y;
            }
        }        
        if (xy.x.compareTo(maxX) == 1) {
            maxX = xy.x;
        }
        if (xy.x.compareTo(minX) == -1) {
            minX = xy.x;
        }
        if (xy.y.compareTo(maxY) == 1) {
            maxY = xy.y;
        }
        if (xy.y.compareTo(minY) == -1) {
            minY = xy.y;
        }
        data.put(id, xy);
    }
}
