/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.android.project.api.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.project.Project;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author arsi
 */
public interface MultiNodeFactoryProvider {

    public static List<MultiNodeFactoryProvider> findAll() {
        Collection<? extends MultiNodeFactoryProvider> lookupAll = Lookups.forPath("Android/Project/NodeFactory").lookupAll(MultiNodeFactoryProvider.class);
        return new ArrayList<>(lookupAll);
    }

    public static List<MultiNodeFactoryProvider> findAll(String path) {
        Collection<? extends MultiNodeFactoryProvider> lookupAll = Lookups.forPath("Android/Project/" + path).lookupAll(MultiNodeFactoryProvider.class);
        return new ArrayList<>(lookupAll);
    }

    public static List<MultiNodeFactoryProvider> findAllForRoot() {
        Collection<? extends MultiNodeFactoryProvider> lookupAll = Lookups.forPath("Android/RootProject/NodeFactory").lookupAll(MultiNodeFactoryProvider.class);
        return new ArrayList<>(lookupAll);
    }

    public static List<MultiNodeFactoryProvider> findAllForRoot(String path) {
        Collection<? extends MultiNodeFactoryProvider> lookupAll = Lookups.forPath("Android/RootProject/" + path).lookupAll(MultiNodeFactoryProvider.class);
        return new ArrayList<>(lookupAll);
    }

    public MultiNodeFactory createMultiNodeFactory(Project p);

}
