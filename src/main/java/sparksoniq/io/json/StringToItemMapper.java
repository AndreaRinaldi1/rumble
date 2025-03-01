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

package sparksoniq.io.json;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.rumbledb.api.Item;

import com.jsoniter.JsonIterator;

import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;

import java.util.Iterator;

public class StringToItemMapper implements FlatMapFunction<Iterator<String>, Item> {

	private static final long serialVersionUID = 1L;
	private final IteratorMetadata metadata;

    public StringToItemMapper(IteratorMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Iterator<Item> call(Iterator<String> stringIterator) throws Exception {
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return stringIterator.hasNext();
            }

            @Override
            public Item next() {
                JsonIterator object = JsonIterator.parse(stringIterator.next());
                return JiqsItemParser.getItemFromObject(object, metadata);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
