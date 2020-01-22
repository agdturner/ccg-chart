/*
 * Copyright (C) 2018 Centre for Computational Geography, University of Leeds.
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andy Turner
 */
public class Chart_ScheduledExecutor implements Runnable {

    public Chart_ScheduledExecutor se;
    ScheduledExecutorService ses;

    public static void main(String[] args) {
        Chart_ScheduledExecutor se;
        se = new Chart_ScheduledExecutor();
        se.run(args);
    }

    public void run(String[] args) {
        int n;
        n = 2;
        // Create a pool with n threads
        ses = Executors.newScheduledThreadPool(n);
        // Schedule
        int initialDelay;
        initialDelay = 1;
        long delay;
        delay = 3;
        ses.scheduleWithFixedDelay(this, initialDelay, delay, TimeUnit.SECONDS);
        try {
            // Awaits for termination for 10 seconds
//            ses.awaitTermination(10, TimeUnit.SECONDS);
//            ses.awaitTermination(20, TimeUnit.SECONDS);
            ses.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Chart_ScheduledExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
        // shutdown now.
        ses.shutdownNow();
        System.out.println("Shutdown Complete");
    }

    @Override
    public void run() {
        for (int i = 0; i < 2; i++) {
            System.out.println("Running " + i + "...");
            wait(this, (long) (Math.random() * 10000));
            System.out.println("... comnpleted " + i + ".");
        }
        System.out.println("Done");
    }

    public void wait(Object o, long timeDelay) {
        synchronized (o) {
            try {
                o.wait(timeDelay);
            } catch (InterruptedException ex) {
                Logger.getLogger(Chart_ScheduledExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Waited " + uk.ac.leeds.ccg.generic.util.Generic_Time.getTime(timeDelay) + ".");
        }
    }
}
