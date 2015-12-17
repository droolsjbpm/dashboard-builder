/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.commons.comparator;

import java.util.Comparator;

import org.jboss.dashboard.profiler.ProfilerHelper;

public class RuntimeConstrainedComparator implements Comparator {

    protected Comparator comp;
    protected int index;
    protected int threshold;

    public RuntimeConstrainedComparator(Comparator comp, int threshold) {
        this.comp = comp;
        this.index = 0;
        this.threshold = threshold;
    }

    public int compare(Object o1, Object o2) {
        if (++index == threshold) {
            index = 0;
            ProfilerHelper.checkRuntimeConstraints();
        }
        return comp.compare(o1, o2);

    }
}

