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

package sparksoniq.spark.udf;

import org.apache.spark.sql.api.java.UDF1;
import org.rumbledb.api.Item;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import scala.collection.mutable.WrappedArray;
import sparksoniq.spark.DataFrameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupClauseSerializeAggregateResultsUDF implements UDF1<WrappedArray<byte[]>, byte[]> {


	private static final long serialVersionUID = 1L;
	private List<Item> _nextResult;
    private List<List<Item>> _deserializedParams;
    
    private transient Kryo _kryo;
    private transient Output _output;
    private transient Input _input;

    public GroupClauseSerializeAggregateResultsUDF() {
        _nextResult = new ArrayList<>();
        _deserializedParams = new ArrayList<>();
        
        _kryo = new Kryo();
        _kryo.setReferences(false);
        DataFrameUtils.registerKryoClassesKryo(_kryo);
        _output = new Output(128, -1);
        _input = new Input();
    }

    @Override
    public byte[] call(WrappedArray<byte[]> wrappedParameters) {
        _nextResult.clear();
        _deserializedParams.clear();
        DataFrameUtils.deserializeWrappedParameters(wrappedParameters, _deserializedParams, _kryo, _input);

        for (List<Item> deserializedParam : _deserializedParams) {
            _nextResult.addAll(deserializedParam);
        }
        return DataFrameUtils.serializeItemList(_nextResult, _kryo, _output);
    }
    
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        _kryo = new Kryo();
        _kryo.setReferences(false);
        DataFrameUtils.registerKryoClassesKryo(_kryo);
        _output = new Output(128, -1);
        _input = new Input();
    }
}
