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

package sparksoniq.jsoniq.compiler.translator.expr.control;

import sparksoniq.jsoniq.compiler.translator.expr.Expression;
import sparksoniq.jsoniq.compiler.translator.expr.ExpressionOrClause;
import sparksoniq.jsoniq.compiler.translator.metadata.ExpressionMetadata;
import sparksoniq.semantics.visitor.AbstractExpressionOrClauseVisitor;

import java.util.ArrayList;
import java.util.List;

public class SwitchCaseExpression extends Expression {

    private final Expression returnExpression;
    private final Expression condition;

    public SwitchCaseExpression(Expression condition, Expression returnExpression,
                                ExpressionMetadata metadataFromContext) {
        super(metadataFromContext);
        this.condition = condition;
        this.returnExpression = returnExpression;
    }

    @Override
    public List<ExpressionOrClause> getDescendants(boolean depthSearch) {
        List<ExpressionOrClause> result = new ArrayList<>();
        result.add(condition);
        result.add(returnExpression);
        return getDescendantsFromChildren(result, depthSearch);
    }

    public Expression getReturnExpression() {
        return returnExpression;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public <T> T accept(AbstractExpressionOrClauseVisitor<T> visitor, T argument) {
        return visitor.visitSwitchCaseExpression(this, argument);
    }

    @Override
    //TODO implement serialization for switch expr
    public String serializationString(boolean prefix) {
        return "";
    }
}
