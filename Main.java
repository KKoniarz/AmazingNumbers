package numbers;
import java.util.*;

public class Main {
    public static void main(String[] args) {
       MagicNumbers magicNumbers = new MagicNumbers();
       while(magicNumbers.isRunning()){
           magicNumbers.doMagic();
       }
    }
}

class MagicNumbers {
    private boolean running;
    private final Scanner scanner = new Scanner(System.in);
    private long number;
    private int quantity;
    private LinkedList<String> properties;
    private String errorMessage;

    public MagicNumbers() {
        String greetings = "Welcome to Amazing Numbers!\n\n" + "Supported requests:\n" +
                "- enter a natural number to know its properties;\n" +
                "- enter two natural numbers to obtain the properties of the list:\n" +
                "  * the first parameter represents a starting number;\n" +
                "  * the second parameter shows how many consecutive numbers are to be processed;\n" +
                "- two natural numbers and properties to search for;\n" +
                "- a property preceded by minus must not be present in numbers;\n" +
                "- separate the parameters with one space;\n" +
                "- enter 0 to exit.\n";
        System.out.print(greetings);
        number = 1;
        quantity = 1;
        properties = new LinkedList<>();
        errorMessage = "Invalid input!\n";
        running = true;
    }

    public void doMagic() {
        State state = chooseRequest();
        switch (state) {
            case SINGLE:
                singleNumber();
                break;
            case CONSECUTIVE:
                consecutiveNumbers();
                break;
            case MATCH:
                matchNumbers();
                break;
            case MATCH_MORE:
                matchMoreNumbers();
                break;
            case EXIT:
                running = false;
                break;
            case ERROR:
                System.out.println(errorMessage);
                break;
        }
    }
    //
    private State chooseRequest(){
        System.out.print("Enter a request: ");
        properties.clear();
        try {
            String[] line = scanner.nextLine().split(" ");
            switch (line.length > 3 ? 4 : line.length) {
                case 4:
                    for (int i = line.length - 1; i > 2; i--) {
                        properties.add(line[i].toLowerCase());
                    }
                case 3:
                    properties.add(line[2].toLowerCase());
                    //if property is unsupported by MagicNumber class
                    if(!checkIfValidProperties(properties) || checkIfMutuallyExclusive(properties)) {
                        return State.ERROR;
                    }
                case 2:
                    quantity = Integer.parseInt(line[1]);
                    if(quantity < 1) {
                        errorMessage = "The second parameter should be a natural number.";
                        return State.ERROR;
                    }
                case 1:
                    number = Long.parseLong(line[0]);
                    if(line.length == 1 && number == 0) return State.EXIT;
                    if(number < 0) {
                        errorMessage = "The first parameter should be a natural number or zero.";
                        return State.ERROR;
                    }
                    return State.values()[line.length > 3 ? 3 : line.length - 1];
                default:
                    return State.ERROR;
            }
        }
        catch (Exception ex) {
            errorMessage = "Invalid input!\n";
            return State.ERROR;
        }
    }

    private enum State {
        SINGLE,CONSECUTIVE,MATCH,MATCH_MORE,EXIT,ERROR
    }

    private void singleNumber() {
        new MagicNumber(number).printProperties();
    }

    private void consecutiveNumbers() {
        for (int i = 0; i < quantity; i++) {
            System.out.println(new MagicNumber(number + i));
        }
        System.out.println();
    }

    private void matchNumbers() {
        int numbersFound = 0;
        long nextNumber = number;
        while (numbersFound < quantity) {
            MagicNumber currentNumber = new MagicNumber(nextNumber);
            if(currentNumber.checkProperty(properties.get(0))) {
                System.out.println(currentNumber);
                numbersFound++;
            }
            nextNumber++;
        }
    }

    private void matchMoreNumbers() {
        int numbersFound = 0;
        long nextNumber = number;
        while (numbersFound < quantity) {
            MagicNumber currentNumber = new MagicNumber(nextNumber);
            boolean matched = true;
            for(String property : properties) {
                if(!currentNumber.checkProperty(property)) {
                    matched = false;
                }
            }
            if(matched) {
                System.out.println(currentNumber);
                numbersFound++;
            }
            nextNumber++;
        }
    }

    public boolean isRunning() {
        return running;
    }

    private boolean checkIfValidProperties(List<String> properties) {
        boolean isValid = true;
        LinkedList<String> unsupportedProperties = new LinkedList<>();
        String supportedProperties = Arrays.toString(MagicNumber.getPropertiesList());
        for (String property : properties) {
            if (!supportedProperties.contains(property.replace("-",""))) {
                unsupportedProperties.add(property.toUpperCase());
                isValid = false;
            }
        }
        if (unsupportedProperties.size() == 1) {
            errorMessage = "The property " + unsupportedProperties + " is wrong.\n" +
            "Available properties: " + Arrays.toString(MagicNumber.getPropertiesList()).toUpperCase();
        }
        if (unsupportedProperties.size() > 1) {
            errorMessage = "The properties " + unsupportedProperties + " are wrong.\n" +
            "Available properties: " + Arrays.toString(MagicNumber.getPropertiesList()).toUpperCase();
        }
        return isValid;
    }

    private boolean checkIfMutuallyExclusive(List<String> properties) {
        boolean isMutuallyExclusive = false;
        StringBuilder error = new StringBuilder("The request contains mutually exclusive properties: ");
        if (properties.contains("odd") && properties.contains("even")) {
            error.append("[ODD, EVEN]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("duck") && properties.contains("spy")) {
            error.append("[DUCK, SPY]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("sunny") && properties.contains("square")) {
            error.append("[SUNNY, SQUARE]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("happy") && properties.contains("sad")) {
            error.append("[HAPPY, SAD]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("-odd") && properties.contains("-even")) {
            error.append("[-ODD, -EVEN]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("-duck") && properties.contains("-spy")) {
            error.append("[-DUCK, -SPY]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("-sunny") && properties.contains("-square")) {
            error.append("[-SUNNY, -SQUARE]");
            isMutuallyExclusive = true;
        }
        if (properties.contains("-happy") && properties.contains("-sad")) {
            error.append("[-HAPPY, -SAD]");
            isMutuallyExclusive = true;
        }
        for(String property : properties) {
            if(properties.contains("-" + property)) {
                error.append("[").append(property).append(", -").append(property).append("]");
                isMutuallyExclusive = true;
            }
        }
        error.append("\nThere are no numbers with these properties.");
        if(isMutuallyExclusive) errorMessage = error.toString();
        return isMutuallyExclusive;
    }
}

class MagicNumber {
    
    private final HashMap<String, Boolean> properties;
    private final long value;
    
    public MagicNumber(long value) {
        this.value = value;
        properties = new HashMap<>();

        properties.put("even", isEven());
        properties.put("-even", !properties.get("even"));

        properties.put("odd", !isEven());
        properties.put("-odd", !properties.get("odd"));

        properties.put("buzz", isBuzz());
        properties.put("-buzz", !properties.get("buzz"));

        properties.put("duck", isDuck());
        properties.put("-duck", !properties.get("duck"));

        properties.put("palindromic", isPalindromic());
        properties.put("-palindromic", !properties.get("palindromic"));

        properties.put("gapful", isGapful());
        properties.put("-gapful", !properties.get("gapful"));

        properties.put("spy", isSpy());
        properties.put("-spy", !properties.get("spy"));

        properties.put("sunny", isSunny());
        properties.put("-sunny", !properties.get("sunny"));

        properties.put("square", isSquare());
        properties.put("-square", !properties.get("square"));

        properties.put("jumping", isJumping());
        properties.put("-jumping", !properties.get("jumping"));

        properties.put("happy", isHappy());
        properties.put("-happy", !properties.get("happy"));

        properties.put("sad", isSad());
        properties.put("-sad", !properties.get("sad"));
    }

    private boolean isEven()  {
        return value % 2 == 0;
    }

    private boolean isBuzz() {
        return (value % 10 == 7 || value % 7 == 0);
    }
    
    private boolean isDuck() {
        char[] digits = Long.toString(value).toCharArray();
        for (char digit : digits) {
            if (digit == '0') {
                return true;
            }         
        }
        return false;
    }
    
    private boolean isPalindromic() {
        char[] digits = Long.toString(value).toCharArray();
        for (int i = 0; i < digits.length/2; i++) {
            if (digits[i] != digits[digits.length - 1 - i]) {
                return false;
            }
        }
        return true;       
    }

    private boolean isGapful() {
        if(value < 100) return false;
        StringBuilder number = new StringBuilder();
        char[] digits = Long.toString(value).toCharArray();
        number.append(digits[0]).append(digits[digits.length - 1]);
        long divider = Long.parseLong(number.toString());
        return (value % divider) == 0;
    }

    private boolean isSpy() {
        char[] digits = Long.toString(value).toCharArray();
        long sum = 0;
        long product = 1;
        for (char ch : digits) {
            sum += (long) ch - '0';
            product *= (long) ch - '0';
        }
        return sum == product;
    }

    private boolean isSquare() {
        return Math.sqrt((double) value) % 1 == 0;
    }

    private boolean isSunny() {
        return Math.sqrt((double) value + 1) % 1 == 0;
    }

    private boolean isJumping() {
        if(value < 10) return true;
        char[] digits = Long.toString(value).toCharArray();
        for (int i = 1; i < digits.length; i++) {
            if(digits[i - 1] + 1 != digits[i] && digits[i - 1] - 1 != digits[i]) return false;
        }
        return true;
    }

    private boolean isHappy() {
        long computedValue = value;
        ArrayList<Long> previousValues = new ArrayList<>();
        do {
            previousValues.add(computedValue);
            char[] digitsAsChars = Long.toString(computedValue).toCharArray();
            long[] digits = new long[digitsAsChars.length];
            for (int i = 0; i < digitsAsChars.length; i++) {
                digits[i] = (long)digitsAsChars[i] - '0';
            }
            for (int i = 0; i < digits.length; i++) {
                digits[i] *= digits[i];
            }
            computedValue = 0;
            for(long digit : digits) {
                computedValue += digit;
            }
            if (computedValue == 1) return true;
        } while (!previousValues.contains(computedValue));
        return false;
    }

    private boolean isSad() {
        return !isHappy();
    }



    public static String[] getPropertiesList(){
        return new String[]{"even", "odd", "buzz", "duck", "palindromic", "gapful",
                            "spy", "sunny", "square", "jumping", "happy", "sad"};
    }
    public boolean checkProperty(String property) {
        return properties.get(property);
    }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("          " + value + " is");
        for (String property : properties.keySet()) {
            if(properties.get(property) && !property.startsWith("-")) output.append(" ").append(property).append(",");
        }
        return output.deleteCharAt(output.length() -1).toString();
    }

    public void printProperties() {
        System.out.println("Properties of " + value);
        for (String property : properties.keySet()) {
            if (!property.startsWith("-")) {
                System.out.println(property + ": " + properties.get(property));
            }
        }
        System.out.println();
    }
}
