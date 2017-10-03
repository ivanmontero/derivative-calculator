import java.util.Scanner;

// TODO: Parenthesis, negatives
public class DerivativeCalculator {
    public static final String ARITHMETIC = "+-";
    public static final String ALGEBRAIC = "*/";
    public static final String EXPONENTIAL = "^";

    public static double evaluate(String expr, double x) {
        return evaluate(parse(expr), x);
    }

    private static double evaluate(ExprNode root, double x) {
//        if(root.data.indexOf('(') == 0 && root.data.indexOf(')') == root.data.length() - 1)
//            root = parse(root.data);
        if(root.data.equals("+")) return evaluate(root.left, x) + evaluate(root.right, x);
        if(root.data.equals("-")) return evaluate(root.left, x) - evaluate(root.right, x);
        if(root.data.equals("*")) return evaluate(root.left, x) * evaluate(root.right, x);
        if(root.data.equals("/")) return evaluate(root.left, x) / evaluate(root.right, x);
        if(root.data.equals("^")) return Math.pow(evaluate(root.left, x), evaluate(root.right, x));
        if(root.data.equals("x")) return x;
        return Double.parseDouble(root.data);
    }

    public static double derive(String expr, double x) {
        return evaluate(derive(expr), x);
    }

    public static String derive(String expr) {
        return toString(simplify(derive(parse(expr))));
    }

    // TODO: SUPPORT PARENTHESIS
    public static ExprNode derive(ExprNode root) {
//        if(root.data.indexOf('(') == 0 && root.data.indexOf(')') == root.data.length() - 1)
//            root = parse(root.data);
        ExprNode a = root.left;
        ExprNode b = root.right;
        if(ARITHMETIC.contains(root.data)) {
            // d/dx(A+B)=A'+B'
            root.left = derive(a);
            root.right = derive(b);
        } else if(root.data.equals("*")) {
            // d/dx(nA)=nA'
            if(isDouble(a.data)) {
                root.left = a;
                root.right = derive(b);
            } else if (isDouble(b.data)) {
                root.left = derive(a);
                root.right = b;
            } else {
                // d/dx(AB)=AB'+A'B
                root.data = "+";
                root.left = new ExprNode("*");
                root.left.left = derive(a);
                root.left.right = b;
                root.right = new ExprNode("*");
                root.right.left = a;
                root.right.right = derive(b);
            }
        } else if(root.data.equals("/")) {
            // d/dx(A/B)=(A'B-AB')/B^2
            // TODO: Division
        } else if (root.data.equals("^")) { // TODO: Make compatible with variables in exponent
            // Assumes A is X and B is a number
            if(isDouble(b.data)) {
                // d/dx(A^n)=nA^(n-1)
                root.data = "*";
                root.left = b;
                root.right = new ExprNode("^");
                root.right.left = a;
                root.right.right = new ExprNode("" + (Double.parseDouble(b.data) - 1.0));
            }
        } else if (isDouble(root.data)) {
            // d/dx(n)=0
            root.data = "0.0";
        } else if (root.data.equals("x")) {
            // d/dx(x)=1
            root.data = "1.0";
        }
//        if(root.data.equals("-")) return derive(root.left) - derive(root.right);
//        if(root.data.equals("*")) return derive(root.left) * derive(root.right);
//        if(root.data.equals("/")) return derive(root.left) / derive(root.right);
//        if(root.data.equals("^")) return Math.pow(derive(root.left), derive(root.right));
////        if(root.data.equals("x")) return x;
////        return Double.parseDouble(root.data);
        return root;
    }

    // TODO: Make compatible with parenthesises
    // Possibly create a string with the parenthesized sections having a placeholder
    public static ExprNode parse(String expr) {
        ExprNode root;
        // Get rid of spaces
        expr = expr.replace(" ", "");
//        // Check if there are parentheses
//        if ((expr.indexOf('(') == 0 && expr.indexOf(')') == expr.length() - 1))
//            root = new ExprNode(expr);
        // Operations
        // parentheses: make a temporary string where everything in the parentheses
        // are an seperate character, then pass in the actual stuff
        // TODO: Order of operations*****
        if (expr.contains("+")) root = new ExprNode("+");
        else if (expr.contains("-")) root = new ExprNode("+");
        else if (expr.contains("*")) root = new ExprNode("*");
        else if (expr.contains("/")) root = new ExprNode("/");
        else if (expr.contains("^")) root = new ExprNode("^");
        // Numeric
        else                         return new ExprNode(expr);
        if(root.data.length() != expr.length()) {
            root.left = parse(expr.substring(0, expr.indexOf(root.data)));
            root.right = parse(expr.substring(expr.indexOf(root.data) + 1));
        }
        return root;
    }

    // TODO: Combine like terms
    public static ExprNode simplify(ExprNode root) {
        if(root != null) {
            // Recursion
            root.left = simplify(root.left);
            root.right = simplify(root.right);
            if(root.left != null && root.right != null) {
                ExprNode a = root.left;
                ExprNode b = root.right;
                // Operation simplification
                if (root.data.equals("*")) {
                    if (isDouble(a.data)) {
                        double num = Double.parseDouble(a.data);
                        if (num == 0.0)
                            return new ExprNode("0");
                        if (num == 1.0)
                            return b;
                    }
                    if (isDouble(b.data)) {
                        double num = Double.parseDouble(b.data);
                        if (num == 0.0)
                            return new ExprNode("0");
                        if (num == 1.0)
                            return a;
                    }
                }
                if (root.data.equals("^")) {
                    if (isDouble(b.data)) {
                        double num = Double.parseDouble(b.data);
                        if (num == 0.0)
                            return new ExprNode("1");
                        if (num == 1.0)
                            return a;
                    }
                }
                if (root.data.equals("+") || root.data.equals("-")) {
                    if (isDouble(a.data)) {
                        double num = Double.parseDouble(a.data);
                        if (num == 0.0)
                            return b;
                    }
                    if (isDouble(b.data)) {
                        double num = Double.parseDouble(b.data);
                        if (num == 0.0)
                            return a;
                    }
                }
            }
            // cast to int if possible
            if(isDouble(root.data)) {
                double num = Double.parseDouble(root.data);
                if(num == Math.floor(num)) root.data = "" + ((int) num);
            }
        }
        return root;
    }

    public static String toString(ExprNode root) {
        if(root.left == null || root.right == null)
            return root.data;
        return toString(root.left) + root.data + toString(root.right);
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    private static class ExprNode {
        public String data;
        public ExprNode left;
        public ExprNode right;

        public ExprNode(String data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        System.out.println("------------------------------\n"
                        +  "Derivative calculator\n"
                        +  "Commands:\n"
                        +  "evaluate [expression] at [value]\n"
                        +  "derive [expression]\n"
                        +  "derive [expression] at [value]\n"
                        +  "exit\n"
                        +  "------------------------------\n");
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.print(" > ");
            String[] input = in.nextLine().split(" ");
            if(input[0].equals("evaluate")) {
                if(input.length != 4 || !isDouble(input[3])) {
                    System.out.println("Invalid format");
                    continue;
                }
                System.out.println(input[1] + "|x=" + input[3] + " is "
                        + evaluate(input[1], Double.parseDouble(input[3])));
            } else if (input[0].equals("derive")) {
                if(input.length == 2) {
                    System.out.println("d(" + input[1] + ") is " + derive(input[1]));
                } else if (input.length == 4) {
                    if(!isDouble(input[3])) {
                        System.out.println("Invalid format");
                        continue;
                    }
                    System.out.println("d(" + input[1] + ")|x=" + input[3] + " is "
                            + derive(input[1], Double.parseDouble(input[3])));
                } else {
                    System.out.println("Invalid format");
                }
            } else if (input[0].equals("exit")) {
                break;
            }
        }
    }
}
