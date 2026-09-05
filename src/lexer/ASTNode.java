package lexer;

import java.util.List;

public abstract class ASTNode {
    public abstract void print(String indent);
}

class ProgNode extends ASTNode {
    private final List<ASTNode> statements;

    public ProgNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Program");
        for (ASTNode statement : statements) {
            if (statement != null) {
                statement.print(indent + "  ");
            }
        }
    }
}

class IntNode extends ASTNode {
    private final String value;

    public IntNode(String value) {
        this.value = value;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Integer: " + value);
    }
}

class DecimalNode extends ASTNode {
    private final String value;

    public DecimalNode(String value) {
        this.value = value;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Decimal: " + value);
    }
}

class StringNode extends ASTNode {
    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "String: " + value);
    }
}

class IdNode extends ASTNode {
    private final String name;

    public IdNode(String name) {
        this.name = name;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Identifier: " + name);
    }
}

class BinOpNode extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public BinOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Binary Operation: " + operator);
        System.out.println(indent + "  Left:");
        if (left != null)
            left.print(indent + "    ");
        System.out.println(indent + "  Right:");
        if (right != null)
            right.print(indent + "    ");
    }
}

class DecNode extends ASTNode {
    private final String type;
    private final String name;
    private final ASTNode value;
    private final int line;

    public DecNode(String type, String name, ASTNode value, int line) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Declaration: " + type + " " + name + " (Line " + line + ")");
        if (value != null) {
            System.out.println(indent + "  Value:");
            value.print(indent + "    ");
        }
    }
}

class AssignNode extends ASTNode {
    private final String name;
    private final ASTNode value;

    public AssignNode(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Assignment: " + name);
        if (value != null) {
            System.out.println(indent + "  Value:");
            value.print(indent + "    ");
        }
    }
}

class PrintNode extends ASTNode {
    private final ASTNode value;

    public PrintNode(ASTNode value) {
        this.value = value;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Print");
        if (value != null) {
            value.print(indent + "  ");
        }
    }
}

class IfNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> thenStatements;
    private final List<ASTNode> elseStatements;

    public IfNode(ASTNode condition, List<ASTNode> thenStatements, List<ASTNode> elseStatements) {
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elseStatements = elseStatements;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "If Statement");
        System.out.println(indent + "  Condition:");
        if (condition != null)
            condition.print(indent + "    ");
        System.out.println(indent + "  Then:");
        for (ASTNode statement : thenStatements) {
            if (statement != null)
                statement.print(indent + "    ");
        }
        if (elseStatements != null && !elseStatements.isEmpty()) {
            System.out.println(indent + "  Else:");
            for (ASTNode statement : elseStatements) {
                if (statement != null)
                    statement.print(indent + "    ");
            }
        }
    }
}

class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> statements;

    public WhileNode(ASTNode condition, List<ASTNode> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "While Statement");
        System.out.println(indent + "  Condition:");
        if (condition != null)
            condition.print(indent + "    ");
        System.out.println(indent + "  Body:");
        for (ASTNode statement : statements) {
            if (statement != null)
                statement.print(indent + "    ");
        }
    }
}