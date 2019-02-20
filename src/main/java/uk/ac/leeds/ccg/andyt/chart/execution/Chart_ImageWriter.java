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
package uk.ac.leeds.ccg.andyt.chart.execution;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Andy Turner
 */
public class Chart_ImageWriter extends Chart_Executor {

    private final Set<Chart_EventListener> listeners;

    public Chart_ImageWriter() {
        listeners = new HashSet<>();
    }
    
    public static void main(String[] args) {
//                Chart_ImageWriter i = new Chart_ImageWriter();
//                Chart_EventListenerImpl listener = new Chart_EventListenerImpl();
//                i.addGeneric_EventListener(listener);
//                i.start();
        Chart_ImageWriter i = new Chart_ImageWriter();
        Chart_EventListenerImpl listener = new Chart_EventListenerImpl();
        i.addGeneric_EventListener(listener);
        i.start();
        }

    public void addGeneric_EventListener(Chart_EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeGeneric_EventListener(Chart_EventListener listener) {
        this.listeners.remove(listener);
    }

    public void start() {
        run();
        notifyListenersOfRenderingComplete();
        run();
        notifyListenersOfRenderingComplete();
    }

    private void notifyListenersOfRenderingComplete() {
//        for (Chart_EventListener e : listeners) {
//            e.renderingComplete(new Chart_RenderingCompleteEvent(this));
//        }
        listeners.forEach((e) -> {
            e.renderingComplete(new Chart_RenderingCompleteEvent(this));
        });
    }
}
