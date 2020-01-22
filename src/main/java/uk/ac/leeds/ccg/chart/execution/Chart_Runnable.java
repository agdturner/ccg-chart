/*
 * Copyright (C) 2016 Centre for Computational Geography, University of Leeds.
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
package uk.ac.leeds.ccg.chart.execution;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andy Turner
 */
public class Chart_Runnable extends Thread implements Runnable {

    Chart_EventListenerImpl listener;
    int runID;

    public Chart_Runnable() {
        this(0);
    }

    public Chart_Runnable(int runID) {
        this.runID = runID;
    }

    public static void main(String[] args) {
        new Chart_Runnable(0).start();
    }

    @Override
    public void run() {
        long timeSleepInMillis = 10000;
        sleepABit(timeSleepInMillis);
        if (listener != null) {
            listener.renderingComplete(new Chart_RenderingCompleteEvent(this));
        }
        sleepABit(timeSleepInMillis * timeSleepInMillis);
    }

    protected void sleepABit(long timeSleepInMillis) {
        System.out.println("" + runID + " sleeping for " + timeSleepInMillis + " milliseconds...");
        try {
            Thread.sleep(timeSleepInMillis);
        } catch (InterruptedException ex) {
            Logger.getLogger(Chart_Runnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("..." + runID + " done sleeping");
    }
}
