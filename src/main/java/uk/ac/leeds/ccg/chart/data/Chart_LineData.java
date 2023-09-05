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

import ch.obermuhlner.math.big.BigRational;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 *
 * @author Andy Turner
 */
public class Chart_LineData extends Chart_Data {
    
    /**
     * Labels added to the xAxis.
     */
    public TreeMap<BigRational, String> xAxisLabels;
    
    /**
     * Keys are labels, values are plot coordinates. 
     */
    public TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps;
    
    public TreeMap<String, Boolean> nonZero;
    
    public TreeMap<String, Boolean> nonZero2;
    
    public Chart_LineData(){
        super();
        xAxisLabels = new TreeMap<>();
        maps = new TreeMap<>();
        //nonZero = new TreeMap<>();
        //nonZero2 = new TreeMap<>();
    }
}
