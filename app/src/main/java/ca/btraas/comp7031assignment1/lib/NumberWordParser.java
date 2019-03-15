package ca.btraas.comp7031assignment1.lib;

import java.util.Arrays;
import java.util.List;

public class NumberWordParser {
    public static Long parse(String input) {

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Not a long!");
        }

        try {
            String str = input;
            if(str.endsWith("th")) str = str.substring(0, str.length() - 2);
            if(str.endsWith("st")) str = str.substring(0, str.length() - 2);
            if(str.endsWith("rd")) str = str.substring(0, str.length() - 2);
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            System.out.println("Still not a long!");
        }


        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;
        List<String> allowedStrings = Arrays.asList
                (
                        "zero","one","two","three","four","five","six","seven",
                        "eight","nine","ten","eleven","twelve","thirteen","fourteen",
                        "fifteen","sixteen","seventeen","eighteen","nineteen","twenty",
                        "thirty","forty","fifty","sixty","seventy","eighty","ninety",
                        "hundred","thousand","million","billion","trillion", "third", "second", "first"

                );

//        String input="One hundred two thousand and thirty four";

        if(input != null && input.length()> 0)
        {
            input = input.replaceAll("-", " ");
            input = input.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = input.trim().split("\\s+");

            for(String str : splittedParts)
            {


                if(!allowedStrings.contains(str))
                {
                    // first try again without the th
                    if(str.endsWith("th")) str = str.substring(0, str.length() - 2);
                    if(str.endsWith("st")) str = str.substring(0, str.length() - 2);
                    if(str.endsWith("rd")) str = str.substring(0, str.length() - 2);
                    if(!allowedStrings.contains(str)) {
                        isValidInput = false;
                        System.out.println("Invalid word found : " + str);
                        break;
                    }
                }
            }
            if(isValidInput)
            {
                for(String str : splittedParts)
                {
                    if(str.equalsIgnoreCase("zero")) {
                        result += 0;
                    }
                    else if(str.equalsIgnoreCase("one") || str.equalsIgnoreCase("first")) {
                        result += 1;
                    }
                    else if(str.equalsIgnoreCase("two") || str.equalsIgnoreCase("second")) {
                        result += 2;
                    }
                    else if(str.equalsIgnoreCase("three") || str.equalsIgnoreCase("third")) {
                        result += 3;
                    }
                    else if(str.equalsIgnoreCase("four") || str.equalsIgnoreCase("fourth")) {
                        result += 4;
                    }
                    else if(str.equalsIgnoreCase("five") || str.equalsIgnoreCase("fifth")) {
                        result += 5;
                    }
                    else if(str.equalsIgnoreCase("six") || str.equalsIgnoreCase("sixth")) {
                        result += 6;
                    }
                    else if(str.equalsIgnoreCase("seven") || str.equalsIgnoreCase("seventh")) {
                        result += 7;
                    }
                    else if(str.equalsIgnoreCase("eight") || str.equalsIgnoreCase("eighth")) {
                        result += 8;
                    }
                    else if(str.equalsIgnoreCase("nine") || str.equalsIgnoreCase("nineth")) {
                        result += 9;
                    }
                    else if(str.equalsIgnoreCase("ten") || str.equalsIgnoreCase("tenth")) {
                        result += 10;
                    }
                    else if(str.equalsIgnoreCase("eleven") || str.equalsIgnoreCase("eleventh")) {
                        result += 11;
                    }
                    else if(str.equalsIgnoreCase("twelve") || str.equalsIgnoreCase("twelfth")) {
                        result += 12;
                    }
                    else if(str.equalsIgnoreCase("thirteen") || str.equalsIgnoreCase("thirteenth")) {
                        result += 13;
                    }
                    else if(str.equalsIgnoreCase("fourteen") || str.equalsIgnoreCase("fourteenth")) {
                        result += 14;
                    }
                    else if(str.equalsIgnoreCase("fifteen") || str.equalsIgnoreCase("fifteenth")) {
                        result += 15;
                    }
                    else if(str.equalsIgnoreCase("sixteen") || str.equalsIgnoreCase("sixteenth")) {
                        result += 16;
                    }
                    else if(str.equalsIgnoreCase("seventeen") || str.equalsIgnoreCase("seventeenth")) {
                        result += 17;
                    }
                    else if(str.equalsIgnoreCase("eighteen") || str.equalsIgnoreCase("eighteenth")) {
                        result += 18;
                    }
                    else if(str.equalsIgnoreCase("nineteen") || str.equalsIgnoreCase("nineteenth")) {
                        result += 19;
                    }
                    else if(str.equalsIgnoreCase("twenty") || str.equalsIgnoreCase("twentieth")) {
                        result += 20;
                    }
                    else if(str.equalsIgnoreCase("thirty") || str.equalsIgnoreCase("thirtieth")) {
                        result += 30;
                    }
                    else if(str.equalsIgnoreCase("forty")) {
                        result += 40;
                    }
                    else if(str.equalsIgnoreCase("fifty")) {
                        result += 50;
                    }
                    else if(str.equalsIgnoreCase("sixty")) {
                        result += 60;
                    }
                    else if(str.equalsIgnoreCase("seventy")) {
                        result += 70;
                    }
                    else if(str.equalsIgnoreCase("eighty")) {
                        result += 80;
                    }
                    else if(str.equalsIgnoreCase("ninety")) {
                        result += 90;
                    }
                    else if(str.equalsIgnoreCase("hundred")) {
                        result *= 100;
                    }
                    else if(str.equalsIgnoreCase("thousand")) {
                        result *= 1000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("million")) {
                        result *= 1000000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("billion")) {
                        result *= 1000000000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("trillion")) {
                        result *= 1000000000000L;
                        finalResult += result;
                        result=0;
                    } else {
                        // try again removing th,st,rd
                        if(str.endsWith("th")) str = str.substring(0, str.length() - 2);
                        if(str.endsWith("st")) str = str.substring(0, str.length() - 2);
                        if(str.endsWith("rd")) str = str.substring(0, str.length() - 2);
                        if(str.equalsIgnoreCase("zero")) {
                            result += 0;
                        }
                        else if(str.equalsIgnoreCase("one") || str.equalsIgnoreCase("first")) {
                            result += 1;
                        }
                        else if(str.equalsIgnoreCase("two") || str.equalsIgnoreCase("second")) {
                            result += 2;
                        }
                        else if(str.equalsIgnoreCase("three") || str.equalsIgnoreCase("third")) {
                            result += 3;
                        }
                        else if(str.equalsIgnoreCase("four") || str.equalsIgnoreCase("fourth")) {
                            result += 4;
                        }
                        else if(str.equalsIgnoreCase("five") || str.equalsIgnoreCase("fifth")) {
                            result += 5;
                        }
                        else if(str.equalsIgnoreCase("six") || str.equalsIgnoreCase("sixth")) {
                            result += 6;
                        }
                        else if(str.equalsIgnoreCase("seven") || str.equalsIgnoreCase("seventh")) {
                            result += 7;
                        }
                        else if(str.equalsIgnoreCase("eight") || str.equalsIgnoreCase("eighth")) {
                            result += 8;
                        }
                        else if(str.equalsIgnoreCase("nine") || str.equalsIgnoreCase("nineth")) {
                            result += 9;
                        }
                        else if(str.equalsIgnoreCase("ten") || str.equalsIgnoreCase("tenth")) {
                            result += 10;
                        }
                        else if(str.equalsIgnoreCase("eleven") || str.equalsIgnoreCase("eleventh")) {
                            result += 11;
                        }
                        else if(str.equalsIgnoreCase("twelve") || str.equalsIgnoreCase("twelfth")) {
                            result += 12;
                        }
                        else if(str.equalsIgnoreCase("thirteen") || str.equalsIgnoreCase("thirteenth")) {
                            result += 13;
                        }
                        else if(str.equalsIgnoreCase("fourteen") || str.equalsIgnoreCase("fourteenth")) {
                            result += 14;
                        }
                        else if(str.equalsIgnoreCase("fifteen") || str.equalsIgnoreCase("fifteenth")) {
                            result += 15;
                        }
                        else if(str.equalsIgnoreCase("sixteen") || str.equalsIgnoreCase("sixteenth")) {
                            result += 16;
                        }
                        else if(str.equalsIgnoreCase("seventeen") || str.equalsIgnoreCase("seventeenth")) {
                            result += 17;
                        }
                        else if(str.equalsIgnoreCase("eighteen") || str.equalsIgnoreCase("eighteenth")) {
                            result += 18;
                        }
                        else if(str.equalsIgnoreCase("nineteen") || str.equalsIgnoreCase("nineteenth")) {
                            result += 19;
                        }
                        else if(str.equalsIgnoreCase("twenty") || str.equalsIgnoreCase("twentieth")) {
                            result += 20;
                        }
                        else if(str.equalsIgnoreCase("thirty") || str.equalsIgnoreCase("thirtieth")) {
                            result += 30;
                        }
                        else if(str.equalsIgnoreCase("forty")) {
                            result += 40;
                        }
                        else if(str.equalsIgnoreCase("fifty")) {
                            result += 50;
                        }
                        else if(str.equalsIgnoreCase("sixty")) {
                            result += 60;
                        }
                        else if(str.equalsIgnoreCase("seventy")) {
                            result += 70;
                        }
                        else if(str.equalsIgnoreCase("eighty")) {
                            result += 80;
                        }
                        else if(str.equalsIgnoreCase("ninety")) {
                            result += 90;
                        }
                        else if(str.equalsIgnoreCase("hundred")) {
                            result *= 100;
                        }
                        else if(str.equalsIgnoreCase("thousand")) {
                            result *= 1000;
                            finalResult += result;
                            result=0;
                        }
                        else if(str.equalsIgnoreCase("million")) {
                            result *= 1000000;
                            finalResult += result;
                            result=0;
                        }
                        else if(str.equalsIgnoreCase("billion")) {
                            result *= 1000000000;
                            finalResult += result;
                            result=0;
                        }
                        else if(str.equalsIgnoreCase("trillion")) {
                            result *= 1000000000000L;
                            finalResult += result;
                            result=0;
                        }
                    }
                }

                finalResult += result;
                result=0;
                return (finalResult);
            }
        }
        return null;
    }
}
