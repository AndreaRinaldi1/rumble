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

package sparksoniq.jsoniq.runtime.iterator.operational;

import sparksoniq.exceptions.IteratorFlowException;
import sparksoniq.exceptions.UnexpectedTypeException;
import sparksoniq.jsoniq.compiler.translator.expr.operational.base.OperationalExpressionBase;
import sparksoniq.jsoniq.item.DecimalItem;
import sparksoniq.jsoniq.item.DoubleItem;
import sparksoniq.jsoniq.item.IntegerItem;
import sparksoniq.jsoniq.item.ItemFactory;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.runtime.iterator.operational.base.BinaryOperationBaseIterator;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;
import sparksoniq.semantics.DynamicContext;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.rumbledb.api.Item;

public class MultiplicativeOperationIterator extends BinaryOperationBaseIterator {


	private static final long serialVersionUID = 1L;
	Item _left;
    Item _right;

    public MultiplicativeOperationIterator(RuntimeIterator left, RuntimeIterator right,
                                           OperationalExpressionBase.Operator operator, IteratorMetadata iteratorMetadata) {
        super(left, right, operator, iteratorMetadata);
    }

    @Override
    public void open(DynamicContext context) {
        super.open(context);

        _leftIterator.open(_currentDynamicContext);
        _rightIterator.open(_currentDynamicContext);

        // if left or right equals empty sequence, return empty sequence
        if (!_leftIterator.hasNext() || !_rightIterator.hasNext()) {
            this._hasNext = false;
        } else {
            _left = _leftIterator.next();
            _right = _rightIterator.next();
            if (_leftIterator.hasNext() || _rightIterator.hasNext() || !_left.isNumeric() || !_right.isNumeric())
                throw new UnexpectedTypeException("Multiplicative expression has non numeric args " +
                        _left.serialize() + ", " + _right.serialize(), getMetadata());

            this._hasNext = true;
        }
        _leftIterator.close();
        _rightIterator.close();
    }

    @Override
    public Item next() {
        if (this._hasNext) {
            this._hasNext = false;

            Type returnType = getNumericResultType(_left, _right);
            if (returnType.equals(IntegerItem.class)) {
                try {
                    int l = _left.castToIntegerValue();
                    int r = _right.castToIntegerValue();
                    switch (this._operator) {
                        case MUL:
                            return ItemFactory.getInstance().createIntegerItem(l * r);
                        case DIV:
                            BigDecimal decLeft = _left.castToDecimalValue();
                            BigDecimal decRight = _right.castToDecimalValue();
                            BigDecimal bdResult = decLeft.divide(decRight, 10, BigDecimal.ROUND_HALF_UP);
                            // if the result contains no decimal part, convert to integer
                            if (bdResult.stripTrailingZeros().scale() <= 0) {
                                try {
                                    // exception is thrown if information is lost during conversion to integer
                                    // this happens if the bigdecimal has a decimal part, or if it can't be fit to an integer
                                    return ItemFactory.getInstance().createIntegerItem(bdResult.intValueExact());
                                } catch (ArithmeticException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                return ItemFactory.getInstance().createDecimalItem(bdResult);
                            }
                        case MOD:
                            return ItemFactory.getInstance().createIntegerItem(l % r);
                        case IDIV:
                            return ItemFactory.getInstance().createIntegerItem(l / r);
                        default:
                            new IteratorFlowException("Non recognized multicative operator.", getMetadata());
                    }
                } catch (IteratorFlowException e) {
                    throw new IteratorFlowException(e.getJSONiqErrorMessage(), getMetadata());
                }
            } else if (returnType.equals(DoubleItem.class)) {
                double l = _left.castToDoubleValue();
                double r = _right.castToDoubleValue();
                switch (this._operator) {
                    case MUL:
                        return ItemFactory.getInstance().createDoubleItem(l * r);
                    case DIV:
                        return ItemFactory.getInstance().createDoubleItem(l / r);
                    case MOD:
                        return ItemFactory.getInstance().createDoubleItem(l % r);
                    case IDIV:
                        return ItemFactory.getInstance().createIntegerItem((int) (l / r));
                    default:
                        new IteratorFlowException("Non recognized multicative operator.", getMetadata());
                }
            } else if (returnType.equals(DecimalItem.class)) {
                BigDecimal l = _left.castToDecimalValue();
                BigDecimal r = _right.castToDecimalValue();
                switch (this._operator) {
                    case MUL:
                        return ItemFactory.getInstance().createDecimalItem(l.multiply(r));
                    case DIV:
                        return ItemFactory.getInstance().createDecimalItem(l.divide(r, 10, BigDecimal.ROUND_HALF_UP));
                    case MOD:
                        return ItemFactory.getInstance().createDecimalItem(l.remainder(r));
                    case IDIV:
                        return ItemFactory.getInstance().createIntegerItem(l.divideToIntegralValue(r).intValueExact());
                    default:
                        new IteratorFlowException("Non recognized multicative operator.", getMetadata());
                }
            }
        }
        throw new IteratorFlowException("Multiplicative expression has non numeric args", getMetadata());
    }

}
