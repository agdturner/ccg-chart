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

import java.math.BigDecimal;
import java.util.TreeMap;

/**
 *
 * @author Andy Turner
 */
public class Chart_AgeGenderBoxPlotData extends Chart_AgeGenderData {

    /**
     * Key is ID value is an age population
     */
    public TreeMap<Integer, TreeMap<Long, BigDecimal>> femalePops;
    public TreeMap<Integer, TreeMap<Long, BigDecimal>> malePops;
            
    public Chart_AgeGenderBoxPlotData() {
        super();
        femalePops = new TreeMap<>();
        malePops = new TreeMap<>();
    }
}
