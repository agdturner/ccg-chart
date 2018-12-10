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
package uk.ac.leeds.ccg.andyt.chart.execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andy Turner
 */
public class Generic_Executor implements Runnable {

    public Generic_ImageWriter imageWriter;

    public Generic_Executor() {
    }

    public Generic_Executor(Generic_ImageWriter i) {
    }

    public static void main(String[] args) {
        new Generic_Executor().run();
    }

    @Override
    public void run() {
        int runID = 0;
        int poolSize = 5;
        ExecutorService es;
        es = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize * 2; i++) {
            final Generic_Runnable r = new Generic_Runnable(runID);
            es.execute(r::start);
            runID++;
        }
        es.shutdown();
        try {
            // wait for them to finish for up to one minute.
            es.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(Generic_Executor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
