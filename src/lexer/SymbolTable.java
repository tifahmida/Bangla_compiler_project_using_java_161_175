package lexer;

import java.util.HashMap;
import java.util.Map;

class Symbol {
    private String name;
    private String type;
    private int line;

    public Symbol(String name, String type, int line) {
        this.name = name;
        this.type = type;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLine() {
        return line;
    }
}

public class SymbolTable {

    private Map<String, Symbol> table = new HashMap<>();

    public boolean insert(String name, String type, int line) {
        if (table.containsKey(name)) {
            System.err.println("Error at line " + line + ": Variable '" + name + "' is already declared.");
            return false;
        }

        table.put(name, new Symbol(name, type, line));
        return true;
    }

    public Symbol lookup(String name) {
        return table.get(name);
    }

    public boolean contains(String name) {
        return table.containsKey(name);
    }

    public void print() {
        System.out.println("\n========== SYMBOL TABLE ==========");
        System.out.println("Name\t\tType\t\tLine");
        System.out.println("----------------------------------");
        for (Symbol s : table.values()) {
            System.out.println(s.getName() + "\t\t" + s.getType() + "\t\t" + s.getLine());
        }
        System.out.println("==================================\n");
    }
}