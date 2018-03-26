package com.github.fmarmar.cucumber.tools.jcommander;

import java.util.List;

import com.beust.jcommander.IStringConverter;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;

public class TagExpressionConverter implements IStringConverter<Expression> {
	
	public static final Expression NO_EXPRESSION = new True();
	
	private TagExpressionParser parser = new TagExpressionParser();

	@Override
	public Expression convert(String value) {
		return parser.parse(value);
	}
	
	private static final class True implements Expression {
        @Override
        public boolean evaluate(List<String> variables) {
            return true;
        }
    }
	
}