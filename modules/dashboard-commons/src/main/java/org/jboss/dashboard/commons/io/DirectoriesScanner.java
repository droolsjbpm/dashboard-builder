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
package org.jboss.dashboard.commons.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoriesScanner {

    private String extension;
    private List<File> currentFiles = new ArrayList<File>();

    public DirectoriesScanner(String extension) {
        this.extension = "." + extension;
    }

    public File[] findFiles(File directory) {
        currentFiles.clear();
        scan(directory);
        return currentFiles.toArray(new File[currentFiles.size()]);
    }

    private void scan(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    scan(f);
                } else {
                    if (f.getName().endsWith(extension)) {
                        currentFiles.add(f);
                    }
                }
            }
        }
    }
}
