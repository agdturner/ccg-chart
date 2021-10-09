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
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.stats.summary.Stats_BigDecimal1;

/**
 *
 * @author Andy Turner
 */
public class Chart_AgeGenderData extends Chart_Data {

    // Female Age_In_Years Population Counts
    public TreeMap<Long, BigDecimal> female;

    // Male Age_In_Years Population Counts
    public TreeMap<Long, BigDecimal> male;
    
    public TreeMap<Long, Stats_BigDecimal1> fss;
    
    public TreeMap<Long, Stats_BigDecimal1> mss;

    //public BigDecimal min;
    public Math_BigRational min;

    //public BigDecimal max;
    public Math_BigRational max;

    public Chart_AgeGenderData() {
        super();
        female = new TreeMap<>();
        male = new TreeMap<>();
        fss = new TreeMap<>();
        mss = new TreeMap<>();
    }
}
