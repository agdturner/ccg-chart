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
public class Generic_ImageWriter extends Generic_Executor {

    private final Set<Generic_EventListener> listeners;

    public Generic_ImageWriter() {
        listeners = new HashSet<>();
    }
    
    public static void main(String[] args) {
//                Generic_ImageWriter i = new Generic_ImageWriter();
//                Generic_EventListenerImpl listener = new Generic_EventListenerImpl();
//                i.addGeneric_EventListener(listener);
//                i.start();
        Generic_ImageWriter i = new Generic_ImageWriter();
        Generic_EventListenerImpl listener = new Generic_EventListenerImpl();
        i.addGeneric_EventListener(listener);
        i.start();
        }

    public void addGeneric_EventListener(Generic_EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeGeneric_EventListener(Generic_EventListener listener) {
        this.listeners.remove(listener);
    }

    public void start() {
        run();
        notifyListenersOfRenderingComplete();
        run();
        notifyListenersOfRenderingComplete();
    }

    private void notifyListenersOfRenderingComplete() {
//        for (Generic_EventListener e : listeners) {
//            e.renderingComplete(new Generic_RenderingCompleteEvent(this));
//        }
        listeners.forEach((e) -> {
            e.renderingComplete(new Generic_RenderingCompleteEvent(this));
        });
    }
}
