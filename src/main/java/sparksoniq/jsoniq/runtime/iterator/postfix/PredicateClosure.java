/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Stefan Irimescu, Can Berker Cikis
 *
 */

package sparksoniq.jsoniq.runtime.iterator.postfix;

import org.apache.spark.api.java.function.Function;
import org.rumbledb.api.Item;

import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.semantics.DynamicContext;

import java.util.ArrayList;
import java.util.List;

public class PredicateClosure implements Function<Item, Boolean> {

	private static final long serialVersionUID = 1L;
	private final RuntimeIterator _expression;
    private final DynamicContext _dynamicContext;

    public PredicateClosure(RuntimeIterator expression, DynamicContext dynamicContext) {
        _expression = expression;
        _dynamicContext = dynamicContext;
    }

    @Override
    public Boolean call(Item v1) throws Exception {
        List<Item> currentItems = new ArrayList<>();
        currentItems.add(v1);
        DynamicContext dynamicContext = new DynamicContext(_dynamicContext);
        dynamicContext.addVariableValue("$$", currentItems);

        _expression.open(dynamicContext);
        Item result = _expression.next();
        _expression.close();
        return result.getBooleanValue();
    }

};