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

package sparksoniq.jsoniq.runtime.iterator.functions.object;

import sparksoniq.exceptions.IteratorFlowException;
import sparksoniq.jsoniq.item.ItemFactory;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.runtime.iterator.functions.base.LocalFunctionCallIterator;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.rumbledb.api.Item;

public class ObjectAccumulateFunctionIterator extends LocalFunctionCallIterator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObjectAccumulateFunctionIterator(List<RuntimeIterator> arguments, IteratorMetadata iteratorMetadata) {
        super(arguments, iteratorMetadata);
    }

    @Override
    public Item next() {
        if (this._hasNext) {
            RuntimeIterator sequenceIterator = this._children.get(0);
            List<Item> items = getItemsFromIteratorWithCurrentContext(sequenceIterator);
            LinkedHashMap<String, List<Item>> keyValuePairs = new LinkedHashMap<>();
            for (Item item : items) {
                // ignore non-object items
                if (item.isObject()) {
                    for (String key : item.getKeys()) {
                        Item value = item.getItemByKey(key);
                        if (!keyValuePairs.containsKey(key)) {
                            List<Item> valueList = new ArrayList<>();
                            valueList.add(value);
                            keyValuePairs.put(key, valueList);
                        }
                        // store values for key collisions in a list
                        else {
                            keyValuePairs.get(key).add(value);
                        }
                    }
                }
            }

            Item result = ItemFactory.getInstance().createObjectItem(keyValuePairs);

            this._hasNext = false;
            return result;
        }
        throw new IteratorFlowException(RuntimeIterator.FLOW_EXCEPTION_MESSAGE + " ACCUMULATE function",
                getMetadata());
    }
}
