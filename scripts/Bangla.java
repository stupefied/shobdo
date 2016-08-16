import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
import java.util.Random;

public class Bangla{

    public static void main(String args[]){

        String start = "995"; //ক
        String end = "9A8"; //ন

        int WORDS_TO_GENERATE = 5;

        //printAllInRange(start,end);

        for(int i = 0 ; i < WORDS_TO_GENERATE ; i++){

            int number = randomInRange( 2 , 9);
            String word = getString(start, end, number);
            print("Word " + i + " : " + word);
        }
    }

    public static void printAllInRange(String startHex, String endHex){

        int start = Integer.parseInt(startHex, 16);
        int end = Integer.parseInt(endHex, 16);

        String retString = "";

        while(start <= end){

            char c = (char) start;
            String single_char = "Number: " + start + " Char:" + c;
            print(single_char);
            start++;
        }
    }

    public static String getString(String startHex, String endHex, int wordLength){

        int start = Integer.parseInt(startHex, 16);
        int end = Integer.parseInt(endHex, 16);

        String retString = "";

        for(int i = 0 ; i < wordLength ; i++){

            int number = randomInRange(start, end);
            char c = (char) number;
            String single_char = "" + c;
            //print(single_char);
            retString += single_char;
        }

        return retString;
    }

    public static void print(int i){
        System.out.println(""+i);
    }

    public static void print(String str){
        System.out.println(str);
    }

    public static int randomInRange(int lowest, int highest){
        return new Random().nextInt( highest - lowest + 1) + lowest;
    }

}
