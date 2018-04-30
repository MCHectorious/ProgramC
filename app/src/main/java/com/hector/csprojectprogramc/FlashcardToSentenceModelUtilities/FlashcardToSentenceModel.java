package com.hector.csprojectprogramc.FlashcardToSentenceModelUtilities;

import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtilities;

public class FlashcardToSentenceModel {

    public static String convertFlashcardToSentence(String flashcardFront, String flashcardBack){

        String front = flashcardFront.trim();
        if(front.contains("(") && front.contains(")")) {
            if ( front.substring(front.lastIndexOf("(")+1, front.lastIndexOf(")")).matches("[0-9]+") ){
                front = front.substring(0,front.length()-3).trim();

            }
        }
        if (front.charAt(front.length()-1)=='?'){
            front = front.substring(0,front.length()-1);
        }

        String back = Character.toLowerCase(flashcardBack.charAt(0)) +flashcardBack.trim().substring(1);
        String frontToTest = front.toLowerCase();


        if(frontToTest.contains("...")){
            if ( frontToTest.substring( frontToTest.length()-3,frontToTest.length() ).equals("...") ){
                return front.substring(0,frontToTest.length()-3)+" "+flashcardBack;
            }else{
                return front.substring(0,front.indexOf("..."))+" "+back+" "+front.substring(front.indexOf("...")+3);
            }
        }

        if(!frontToTest.contains(" ")){
            return front+" means "+back;
        }else if(GeneralStringUtilities.countOfCharacterInString(' ',frontToTest)==1){
            return front+" means "+back;
        }

        if(frontToTest.length()>19) {
            if (frontToTest.substring(0,19).equals("give advantages of ")){
                return "Advantages of "+Character.toLowerCase(front.charAt(19))+front.substring(20)+" are that "+back;
            }
        }

        if(frontToTest.length()>18) {
            if (frontToTest.substring(0,17).equals("what happens when")){
                return "When "+Character.toLowerCase(front.charAt(18))+front.substring(19)+", "+back;
            }
        }

        if(frontToTest.length()>17) {
            if (frontToTest.substring(0,17).equals("give examples of ")){
                return "Examples of "+Character.toLowerCase(front.charAt(17))+front.substring(18)+" are "+back;
            }

        }


        if(frontToTest.length()>16) {
            if (frontToTest.substring(0,16).equals("what happens at ")){
                return "At "+Character.toLowerCase(front.charAt(16))+front.substring(17)+", "+back;
            }

            if(frontToTest.substring(frontToTest.length()-16).equals("by which factors") ){
                return "The factors affecting "+Character.toLowerCase(front.charAt(0))+front.substring(1,frontToTest.length()-16)+"are "+back;
            }
        }

        if(frontToTest.length()>14) {
            if (frontToTest.substring(0,13).equals("how would you")){
                return "You would "+Character.toLowerCase(front.charAt(14))+front.substring(15)+" "+back;
            }
        }

        if(frontToTest.length()>11) {
            if(frontToTest.substring(frontToTest.length()-11).equals("explain why") ){
                return front.substring(0,frontToTest.length()-11)+"because "+back;
            }
        }

        if(frontToTest.length()>10) {
            String start = frontToTest.substring(0,10);
            if (start.equals("how would ")){
                return front.substring(10)+" by "+back;
            }

            if(start.equals("what does ")){
                if(frontToTest.substring(frontToTest.length()-9,frontToTest.length()).equals("stand for") ){
                    return Character.toUpperCase(front.charAt(10))+front.substring(11,frontToTest.length()-10)+" stands for "+back;
                }

                if(frontToTest.substring(frontToTest.length()-9,frontToTest.length()).equals("represent") ){
                    return Character.toUpperCase(front.charAt(10))+front.substring(11,frontToTest.length()-10)+" represents "+back;
                }

                if(frontToTest.substring(frontToTest.length()-4,frontToTest.length()).equals("mean") ){
                    return Character.toUpperCase(front.charAt(10))+front.substring(11,frontToTest.length()-5)+" means "+back;
                }

                if(frontToTest.substring(frontToTest.length()-7,frontToTest.length()).equals("contain") ){
                    return Character.toUpperCase(front.charAt(10))+front.substring(11,frontToTest.length()-5)+" contains "+back;
                }

            }

        }

        if(frontToTest.length()>9) {
            String start = frontToTest.substring(0,9);

            if(start.equals("how does ")){
                return Character.toUpperCase(front.charAt(9))+front.substring(10)+"s by "+back;
            }

            if (start.equals("what are ") ){
                return Character.toUpperCase(front.charAt(9))+front.substring(10)+" are "+back;
            }

            if (start.equals("describe ")){
                return Character.toUpperCase(front.charAt(9))+front.substring(10)+": "+back;
            }

            if (start.equals("how does ")){
                return Character.toUpperCase(front.charAt(9))+front.substring(10)+" by "+back;
            }

            if (start.equals("where do ")){
                return Character.toUpperCase(front.charAt(9))+front.substring(10)+" in "+back;
            }

        }

        if(frontToTest.length()>8) {
            String start = frontToTest.substring(0,8);

            if (start.equals("what is ") ){
                return Character.toUpperCase(front.charAt(8))+front.substring(9)+" is "+back;
            }

            if(start.equals("what do ")){
                if(frontToTest.substring(frontToTest.length()-3).equals("for") ){
                    return Character.toUpperCase(front.charAt(8))+front.substring(9,frontToTest.length()-4)+" for "+back;
                }
            }

            if (start.equals("how can ")){
                return Character.toUpperCase(front.charAt(8))+front.substring(9)+" by "+back;
            }

            if (start.equals("when do ")){
                return Character.toUpperCase(front.charAt(8))+front.substring(9)+" when "+back;
            }

            if(start.equals("why are ")){
                return Character.toUpperCase(front.charAt(8))+front.substring(9,front.lastIndexOf(" "))+" are "+front.substring(front.lastIndexOf(" "))+" because "+back;
            }
        }

        if(frontToTest.length()>7) {
            String start = frontToTest.substring(0,7);

            if(start.equals("define ")){
                return Character.toUpperCase(front.charAt(7))+front.substring(8)+": "+back;
            }

            if (start.equals("how do ")){
                return Character.toUpperCase(front.charAt(7))+front.substring(8)+" by "+back;
            }

            if(start.equals("why is ")){
                return Character.toUpperCase(front.charAt(7))+front.substring(8,front.lastIndexOf(" "))+" is "+front.substring(front.lastIndexOf(" "))+" because "+back;
            }
        }

        if(frontToTest.length()>6) {
            if (frontToTest.substring(0,6).equals("state ")){
                return Character.toUpperCase(front.charAt(6))+front.substring(7)+": "+back;
            }
        }

        if(frontToTest.length()>5) {
            String start = frontToTest.substring(0,5);
            if (start.equals("give ")){
                return Character.toUpperCase(front.charAt(5))+front.substring(6)+": "+back;
            }

            if (start.equals("name ")){
                return Character.toUpperCase(front.charAt(5))+front.substring(6)+": "+back;
            }

            if (start.equals("list ")){
                return Character.toUpperCase(front.charAt(5))+front.substring(6)+": "+back;
            }

            if (start.equals("what ") && front.contains(" is ")){
                return Character.toUpperCase(back.charAt(0))+back.substring(1)+" is the "+front.substring(5,front.indexOf(" is "))+" that is "+front.substring(front.indexOf(" is "));
            }
        }

        String input = flashcardFront+"<F_B_S>"+flashcardBack;
        String inputText, extra;
        if (input.length()<107){
            inputText = input;
            extra = "";
        }else{
            inputText = input.substring(0,107);
            extra = input.substring(107);
        }

        double[] inputArray = DataProcessing.stringToDoubleArray(inputText);
        double[] outputArray = LinearLayer.run(inputArray);

        return DataProcessing.doubleArrayToString(outputArray)+extra;
    }

}
