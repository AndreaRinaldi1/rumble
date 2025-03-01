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

package sparksoniq.spark.closures;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.rumbledb.api.Item;

import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.tuple.FlworTuple;
import sparksoniq.semantics.DynamicContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForClauseClosure implements FlatMapFunction<FlworTuple, FlworTuple> {

	private static final long serialVersionUID = 1L;
	private final String _variableName;
    private final RuntimeIterator _expression;


    public ForClauseClosure(RuntimeIterator expression, String variableName) {
        this._variableName = variableName;
        this._expression = expression;
    }

    @Override
    public Iterator<FlworTuple> call(FlworTuple tuple) throws Exception {
        List<FlworTuple> results = new ArrayList<>();

        // create a new tuple for each result from the expression
        _expression.open(new DynamicContext(tuple));
        while (_expression.hasNext()) {
            List<Item> values = new ArrayList<>();
            values.add(_expression.next());
            FlworTuple newTuple = new FlworTuple(tuple, _variableName, values);
            results.add(newTuple);
        }
        _expression.close();
        return results.iterator();
    }
}
