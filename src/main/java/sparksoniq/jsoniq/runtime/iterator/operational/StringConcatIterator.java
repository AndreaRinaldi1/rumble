/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Stefan Irimescu
 *
 */
 package sparksoniq.jsoniq.runtime.iterator.operational;

import sparksoniq.jsoniq.compiler.translator.expr.operational.base.OperationalExpressionBase;
import sparksoniq.exceptions.IteratorFlowException;
import sparksoniq.exceptions.UnexpectedTypeException;
import sparksoniq.jsoniq.item.AtomicItem;
import sparksoniq.jsoniq.item.Item;
import sparksoniq.jsoniq.item.StringItem;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.runtime.iterator.operational.base.BinaryOperationBaseIterator;

public class StringConcatIterator extends BinaryOperationBaseIterator {
    public StringConcatIterator(RuntimeIterator left, RuntimeIterator right) {
        super(left, right, OperationalExpressionBase.Operator.CONCAT);
    }

    @Override
    public AtomicItem next() {
        if(this.hasNext()) {
            _leftIterator.open(_currentDynamicContext);
            _rightIterator.open(_currentDynamicContext);
            Item left = _leftIterator.next();
            Item right = _rightIterator.next();
            if(!(left instanceof StringItem) || !(right instanceof StringItem))
                throw new UnexpectedTypeException("String concat expression has non strings args " +
                        left.serialize() + ", " + right.serialize());
            StringItem leftString = (StringItem)left;
            StringItem rightString = (StringItem)right;
            _leftIterator.close();
            _rightIterator.close();
            this._hasNext = false;
            return new StringItem(leftString.getStringValue().concat(rightString.getStringValue()));
        }
        throw new IteratorFlowException(RuntimeIterator.FLOW_EXCEPTION_MESSAGE);

    }
}