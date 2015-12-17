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
package org.jboss.dashboard.provider;

import org.jboss.dashboard.dataset.DataSet;

public interface DataLoader {

    /**
     * The provider type.
     */
    DataProviderType getDataProviderType();
    void setDataProviderType(DataProviderType type);

    /**
     * Returns whether the loader is ready to gather data. This implies must be well configured.
     */
    boolean isReady();

    /**
     * Load the data set.
     */
    DataSet load(DataProvider provider) throws Exception;

    /**
     * Maximum memory in bytes a data set load operation may consume.
     */
    Long getMaxMemoryUsedInDataLoad();
    void setMaxMemoryUsedInDataLoad(Long maxMemoryUsedInDataLoad);

    /**
     * Maximum size in bytes a data set may have.
     */
    Long getMaxDataSetSizeInBytes();
    void setMaxDataSetSizeInBytes(Long maxDataSetSizeInBytes);

    /**
     * Maximum time in milliseconds a data set load operation may last.
     */
    Long getMaxDataSetLoadTimeInMillis();
    void setMaxDataSetLoadTimeInMillis(Long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set filter operation may last.
     */
    Long getMaxDataSetFilterTimeInMillis();
    void setMaxDataSetFilterTimeInMillis(Long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set group operation may last.
     */
    Long getMaxDataSetGroupTimeInMillis();
    void setMaxDataSetGroupTimeInMillis(Long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set sort operation may last.
     */
    Long getMaxDataSetSortTimeInMillis();
    void setMaxDataSetSortTimeInMillis(Long maxDataSetSortTimeInMillis);

}
