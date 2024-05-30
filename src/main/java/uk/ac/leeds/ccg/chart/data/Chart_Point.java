/**
 * Copyright 2012 Andy Turner, The University of Leeds, UK
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.leeds.ccg.chart.data;

import ch.obermuhlner.math.big.BigRational;

public class Chart_Point {

    /**
     * The x coordinate.
     */
    protected BigRational x;
    
    /**
     * The y coordinate.
     */
    protected BigRational y;

    /**
     * Create a new instance
     */
    protected Chart_Point() {
    }

    /**
     * @param x What {@link x} is set to.
     * @param y What {@link y} is set to.
     */
    public Chart_Point(BigRational x, BigRational y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return {@link x}. 
     */
    public BigRational getX() {
        return x;
    }

    /**
     * @return {@link y}. 
     */
    public BigRational getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Chart_Point other = (Chart_Point) obj;
        if (this.x != other.x && (this.x == null || !this.x.equals(other.x))) {
            return false;
        }
        return !(this.y != other.y && (this.y == null || !this.y.equals(other.y)));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.x != null ? this.x.hashCode() : 0);
        hash = 19 * hash + (this.y != null ? this.y.hashCode() : 0);
        return hash;
    }
}
