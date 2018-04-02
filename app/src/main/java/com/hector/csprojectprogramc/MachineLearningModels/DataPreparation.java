package com.hector.csprojectprogramc.MachineLearningModels;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Hector - New on 18/03/2018.
 */

public class DataPreparation {

    private static final int FIXED_VECTOR_SIZE = 100;
    private static final Random rand = new Random();
    private static final int phrasesSize = 121;
    private static final double positionForEmptyString = -0.41029280712921684;
    private static final ArrayList<String> sortedPhrases = new ArrayList<>(Arrays.asList("–","‘","’","“","”"," ","!","\"","$","…","\'","(",")","+",",","-",".","/","0","1","2","3","4","5","6","9",":",";","<","=","<F_B_S>",">","?","@","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","œ","S","T","U","V","W","Y","Z","^","_","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","~","¡","€","¿","Á","Ä","Ç","É","Ö","Ú","Ü","ß","à","á","â","ä", "ç", "è","é","ê","ë","ì","í","î","ï","ñ","ó","ô","ö","ù","ú","û","ü","" ));
    private static final ArrayList<String> phrasesSortedInSize = new ArrayList<>(Arrays.asList("<F_B_S>","–","‘","’","“","”"," ","!","\"","$","…","\'","(",")","+",",","-",".","/","0","1","2","3","4","5","6","9",":",";","<","=",">","?","@","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","œ","S","T","U","V","W","Y","Z","^","_","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","~","¡","€","¿","Á","Ä","Ç","É","Ö","Ú","Ü","ß","à","á","â","ä", "ç", "è","é","ê","ë","ì","í","î","ï","ñ","ó","ô","ö","ù","ú","û","ü" ));
    private static final double[] valuesForSortedPhrases = {-1.0,-0.9999987269255252,-0.9999974538510503,-0.9999236155315087,-0.999921069382559, -0.9999185232336093,-0.952357733927435, -0.9522533418204967,-0.9121438574156591,-0.9121387651177596,-0.9121311266709105,-0.9114971355824317,-0.9082520687460218,-0.9050044557606621,-0.904996817313813, -0.9049140674729473,-0.9045728835136857,-0.9043360916613623,-0.9025474220241886,-0.9025219605346915,-0.902507956715468, -0.9025003182686189,-0.9024824952259709,-0.9024748567791218,-0.9024723106301721,-0.9024672183322726,-0.9024621260343731,-0.9024595798854235,-0.9024583068109486,-0.8924583068109486,-0.8924557606619989,-0.8824557606619989,-0.8724557606619989,-0.8723348185868876,-0.8723322724379379,-0.8713774665817953,-0.870491406747295, -0.8695620623806495,-0.8682100572883515,-0.8673036282622535,-0.8565779758115851,-0.8559898154042013,-0.8554729471674094,-0.855094844048377, -0.8549497135582433,-0.8545295989815406,-0.8533825588796946,-0.8526454487587525,-0.852329726288988, -0.8520687460216423,-0.8512145130490134,-0.8511355824315723,-0.8506301718650542,-0.8506174411203056,-0.8292425206874603,-0.8284264799490771,-0.8282520687460218,-0.8279236155315087,-0.8275124124761299,-0.827476766390834, -0.8273443666454489,-0.8273316359007002,-0.8073316359007002,-0.7701133036282622,-0.7651432208784213,-0.743208147676639, -0.7298395926161679,-0.674238064926798, -0.6645130490133672,-0.6580814767663907,-0.6444672183322724,-0.619252705283259, -0.6184875875238701,-0.6161616804583068,-0.5960241884150222,-0.5768873329089751,-0.5508593252705283,-0.5261667727562062,-0.5191304901336727,-0.5184404837683003,-0.4893647358370463,-0.4640420114576701,-0.43765754296626336,-0.42694462126034355,-0.4241273074474855,-0.4192463399108846, -0.41853596435391455,-0.4152157861234881, -0.4144621260343729,-0.4144468491406746, -0.4144264799490769, -0.41442393380012715,-0.41440865690642886,-0.414407383831954, -0.4144048376830043, -0.41440356460852945,-0.41437301082113287,-0.41436155315085915,-0.4143590070019094,-0.41434118395926145,-0.41422915340547406,-0.41421387651177577,-0.41400763844684896,-0.4139567154678547, -0.41367154678548673,-0.41362698917886676,-0.41339528962444283,-0.4119592616168044, -0.41185741565881584,-0.4118497772119667, -0.41184723106301696,-0.4115429662635262, -0.4114996817313811, -0.41149713558243134,-0.4113469127943982, -0.41089624443029893,-0.41084277530235497,-0.4107014640356459, -0.41069382558879675,-0.41063144493952874,-0.4105983450031825,-0.41029280712921684};

    public static double[] stringToDoubleArray(String message) {
        //Log.w("Got this far","started this");
        double[] output = new double[FIXED_VECTOR_SIZE];
        int index =0;
        for(int i=0;i<message.length();) {
            if (!sortedPhrases.contains( Character.toString(message.charAt(i))) ){
                i++;
                continue;
            }
            //Log.w("got this far","is included");
            for(int j=0;j<phrasesSize;j++) {
                String phrase = phrasesSortedInSize.get(j);
                if(i+phrase.length()<=message.length()) {
                    if(phrase.equals(message.substring(i, i+phrase.length()))) {
                        output[index++] = valuesForSortedPhrases[sortedPhrases.indexOf(phrase)]+0.0000001*rand.nextDouble()*(valuesForSortedPhrases[sortedPhrases.indexOf(phrase)+1]-valuesForSortedPhrases[sortedPhrases.indexOf(phrase)]);
                        i+= phrase.length();
                        //Log.w("found",phrase);
                        break;
                    }
                }
            }
            if(index>=FIXED_VECTOR_SIZE) {
                break;
            }
        }
        for(int i = index;i<FIXED_VECTOR_SIZE;i++) {
            output[i] = positionForEmptyString+0.0000001*rand.nextDouble()*(1.0-positionForEmptyString);
        }
        //Log.w("Got this far","finished this");


        return output;

    }


    public static String doubleArrayToString(double[] input) {

        StringBuilder builder = new StringBuilder();
        //Log.w("Got this far","double array to string");
        for(double value : input){
            if(value>=positionForEmptyString || value<=-1.0) {
                continue;
            }


            int low = 0;
            int high = phrasesSize-1;
            boolean found = false;
            while(!found) {
                int middle = (low+high)/2;
                //Log.w(Integer.toString(middle),Integer.toString(low)+" "+Integer.toString(high));
                if(value>=valuesForSortedPhrases[middle] && value<valuesForSortedPhrases[middle+1]) {
                    if(!sortedPhrases.get(middle).equals("<F_B_S>")){
                        builder.append(sortedPhrases.get(middle));
                    }

                    found = true;
                } else if(value < valuesForSortedPhrases[middle]) {
                    high = middle -1;
                }else {
                    low = middle+1;
                }
            }

        }
        return builder.toString();
    }


}
