package com.hector.csprojectprogramc.MachineLearningModels;

public class FlashcardToSentenceModel {

    public static String convertFlashcardToSentence(String flashcardFront, String flashcardBack){
        //return front+" "+back;//TODO: Implement actual model

        String front = flashcardFront.trim();
        String back = flashcardBack.trim();
        String frontToTest = front.toLowerCase();
        if (frontToTest.charAt(frontToTest.length()-1)=='?'){
            frontToTest = frontToTest.substring(0,frontToTest.length()-1);
        }

        if(frontToTest.contains("...")){
            if ( frontToTest.substring( frontToTest.length()-3,frontToTest.length() ).equals("...") ){
                return front.substring(0,frontToTest.length()-3)+flashcardBack;
            }else{
                return front.substring(0,front.indexOf("..."))+" "+back+" "+front.substring(front.indexOf("...")+3);
            }
        }

        if(frontToTest.substring(0,9).equals("what does")){
            if(frontToTest.substring(frontToTest.length()-9,frontToTest.length()).equals("stand for") ){
                return front.substring(9,frontToTest.length()-9)+" stands for "+back;
            }

            if(frontToTest.substring(frontToTest.length()-9,frontToTest.length()).equals("represent") ){
                return front.substring(9,frontToTest.length()-9)+" represents "+back;
            }

            if(frontToTest.substring(frontToTest.length()-4,frontToTest.length()).equals("mean") ){
                return front.substring(9,frontToTest.length()-4)+" means "+back;
            }
        }

        if(frontToTest.substring(0,8).equals("how does")){
            return front.substring(8,front.length())+"s by "+back;
        }

        if(frontToTest.substring(0,6).equals("define")){
            return front.substring(6,front.length())+": "+back;
        }

        if(!frontToTest.contains(" ")){
            return front+" means "+back;
        }

        if (frontToTest.substring(0,7).equals("what is") ){
            return front.substring(7)+" is "+back;
        }

        if (frontToTest.substring(0,16).equals("give examples of")){
            return "Examples of "+front.substring(16)+" are "+back;
        }

        if (frontToTest.substring(0,8).equals("what are") ){
            return front.substring(8)+" are "+back;
        }

        if (frontToTest.substring(0,18).equals("give advantages of")){
            return "Advantages of "+front.substring(18)+" are "+back;
        }

        if (frontToTest.substring(0,4).equals("give")){
            return front.substring(4)+": "+back;
        }

        if (frontToTest.substring(0,5).equals("state")){
            return front.substring(5)+": "+back;
        }


        if (frontToTest.substring(0,4).equals("name")){
            return front.substring(4)+": "+back;
        }


        if (frontToTest.substring(0,4).equals("list")){
            return front.substring(4)+": "+back;
        }


        if (frontToTest.substring(0,8).equals("describe")){
            return front.substring(8)+": "+back;
        }

        if(frontToTest.substring(0,7).equals("what do")){
            if(frontToTest.substring(frontToTest.length()-4,frontToTest.length()).equals("for") ){
                return front.substring(7,frontToTest.length()-4)+" for "+back;
            }
        }

        if (frontToTest.substring(0,6).equals("how is")){
            return front.substring(6)+" by "+back;
        }

        if (frontToTest.substring(0,7).equals("how are")){
            return front.substring(7)+" by "+back;
        }

        if (frontToTest.substring(0,7).equals("how can")){
            return front.substring(7)+" by "+back;
        }

        if (frontToTest.substring(0,17).equals("what happens when")){
            return "When "+front.substring(17)+", "+back;
        }

        if (frontToTest.substring(0,15).equals("what happens at")){
            return "At "+front.substring(15)+", "+back;
        }

        if (frontToTest.substring(0,6).equals("how do")){
            return front.substring(6)+" because "+back;
        }

        if (frontToTest.substring(0,8).equals("how does")){
            return front.substring(8)+" because "+back;
        }

        if (frontToTest.substring(0,9).equals("how would")){
            return front.substring(9)+" because "+back;
        }

        if (frontToTest.substring(0,8).equals("where do")){
            return front.substring(8)+" in "+back;
        }

        if (frontToTest.substring(0,13).equals("How would you")){
            return "You would "+front.substring(13)+" "+back;
        }

        if (frontToTest.substring(0,7).equals("when do")){
            return front.substring(7)+" when "+back;
        }

        if(frontToTest.substring(frontToTest.length()-12,frontToTest.length()).equals("explain why") ){
            return front.substring(7,frontToTest.length()-12)+" because "+back;
        }

        if(frontToTest.substring(frontToTest.length()-17,frontToTest.length()).equals("by which factors") ){
            return "The factors affecting "+front.substring(7,frontToTest.length()-17)+" are "+back;
        }

        String input = flashcardFront+"<F_B_S>"+flashcardBack;
        String extra= (input.length()>100)? input.substring(100):"";

        double[] inputArray = DataPreparation.stringToDoubleArray(input);
        double[] outputArray = LinearLayer.forward(inputArray);

        return DataPreparation.doubleArrayToString(outputArray)+extra;


    }


}
