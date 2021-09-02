/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.srtapp;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author Marcin
 */
public class MainClass {
    
    
    public static void main(String[] args) throws JSAPException, IOException{
        
        
        JSAP jsap = new JSAP();             /* Used for command line inputs */
       
        jsap = initializeJSAP(jsap);        /* Function initializing input flags */
        
        JSAPResult config = jsap.parse(args);  /* Encapsulates the results of JSAP's parse() methods. */
        
        String inputString;
        
        if(!config.success()){
            System.out.println("\nThere was an error found within command line arguments, try again\n");        
            helpInfo();         
        } else {
            if(config.getBoolean("help")){
                    helpInfo();
                }
            else { 
        
                if (!config.getString("IN").equals("none") && !(config.getInt("NUMBER")==1337)){

                    String filepath = "./" + config.getString("IN");

                    inputString = readFile(filepath, StandardCharsets.UTF_8);

                    String outputString = "";

                    createBashScript(config, inputString, outputString, config.getInt("NUMBER"));

                } else {

                    System.out.println();
                    System.out.println("Something went wrong with the command line arguments.");
                    System.out.println();
                    helpInfo();

                }
            }
        }
        
        
        
    }
    
    /*****************************************************************************************************************************************************************/
    
    private static JSAP initializeJSAP(JSAP jsap) throws JSAPException{
        
        FlaggedOption opt1 = new FlaggedOption("OUT")         
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("out") 
                                .setRequired(true) 
                                .setShortFlag('o') 
                                .setLongFlag("out");

        jsap.registerParameter(opt1);
        
        FlaggedOption opt2 = new FlaggedOption("IN")         
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("none") 
                                .setRequired(true) 
                                .setShortFlag('i') 
                                .setLongFlag("in");

        jsap.registerParameter(opt2);
        
        FlaggedOption opt3 = new FlaggedOption("NUMBER")         
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setDefault("1337") 
                                .setRequired(true) 
                                .setShortFlag('n') 
                                .setLongFlag("nr");

        jsap.registerParameter(opt3);

        Switch sw1 = new Switch("help")                     /* help information flag */
                        .setShortFlag('h')
                        .setLongFlag("help");

        jsap.registerParameter(sw1);
        
        
        return jsap;
    }
    
    
    
    /*****************************************************************************************************************************************************************/
    
    public static void createBashScript(JSAPResult config, String inputString, String outputString, int number){
        
        String tmpString, tmpString2;
        String outName;
        
        try (Scanner sc = new Scanner(inputString))
        {
            
            while (sc.hasNext()){
                
                tmpString = sc.nextLine();
                tmpString2 = tmpString.replace(".mkv", ".srt");
                
                if (sc.hasNextLine()){
                    
                    outputString += "mkvextract tracks '" + tmpString + "' " + number + ":'" + tmpString2 + "'; \\" + "\n";
                
                } else {
                    
                    outputString += "mkvextract tracks '" + tmpString + "' " + number + ":'" + tmpString2 + "'" + "\n";
                    
                }
                
            }
            
        }
        
        //System.out.println(outputString);
        
        if (config.getString("OUT").equals("out")){
            outName = "out.txt";
        } else {
            outName = config.getString("OUT") + ".txt";
        }
        
        try {
            File myObj = new File(outName);                              /* Writing the content of outputString into the output file */
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
         } else {
                System.out.println("File already exists.");
         }
         } catch (IOException e) {
             System.out.println("An error occurred.");
                //e.printStackTrace();
         }
        
        try {
        try (FileWriter myWriter = new FileWriter(outName)) {
            myWriter.write(outputString);
        }
            System.out.println("Successfully wrote to the file " + outName);
            } catch (IOException e) {
                System.out.println("An error occurred.");
               // e.printStackTrace();
            }
        
        
        
        
    }
    
    
    /*****************************************************************************************************************************************************************/
    
    public static String readFile(String path, Charset encoding)           /*Simple filereader with charset encoding, need UTF-8*/
    throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    
    
    
    /*****************************************************************************************************************************************************************/
    
    private static void helpInfo(){
        
        System.out.println();
        System.out.println("MKVSRTEXTRACT Help :");
        System.out.println("-i (--in) : input file name");
        System.out.println("-o (--out) : output file name");
        System.out.println("-n (--nr) : number of subtitle stream");
        System.out.println();
        System.out.println("Example: java -jar mkvsrtextract.jar -n 24 -i in.txt -o out");
        System.out.println();

    }
    
    
}
