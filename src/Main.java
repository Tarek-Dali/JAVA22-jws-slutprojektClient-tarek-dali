import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client är nu redo");

        //Init stuff. Set as null to be initialized as "something"
        Socket socket = null;
        InputStreamReader inputSR = null;
        OutputStreamWriter outputSW = null;
        BufferedReader bReader = null;
        BufferedWriter bWriter = null;

        //Starta Klienten
        try {
            //Init Socket med specifik port
            socket = new Socket("localhost", 4321);

            //Initiera Reader och Writer och koppla dem till socket
            inputSR = new InputStreamReader(socket.getInputStream());
            outputSW = new OutputStreamWriter(socket.getOutputStream());
            bReader = new BufferedReader(inputSR);
            bWriter = new BufferedWriter(outputSW);

            //Initiera Scanner för att skriva i konsol
            Scanner scan = new Scanner(System.in);

            while (true) {
                JSONParser parser = new JSONParser();
                String[] message = userInput();

                //Skicka meddelande till server
                bWriter.write(message[1]);
                bWriter.newLine();
                bWriter.flush();

                //Hämta response från server
                String resp = bReader.readLine();

                //Hämtar response från server och skapa JSON objekt
                JSONObject serverResponse = (JSONObject) parser.parse(resp);

                //Kollar om respons lyckas
                if ("200".equals(serverResponse.get("httpStatusCode").toString())) {
                    //TODO Kolla vad som har returnerats

                    //Bygger upp ett JSONObject av den returnerade datan
                    JSONObject data = (JSONObject) parser.parse((String) serverResponse.get("data"));

                    Object[] newKyes = data.keySet().toArray();

                    //Två val som låter använder antingen hämta data på en specifik person eller alla personer
                    if(message[0].equals("1")){
                        System.out.println("--------------------------------");
                        System.out.println("Det finns 3 personer. Välj vilken persons info du vill ha genom att ange en siffra som representerar platsen.");
                        System.out.println("Skriv in ditt val: ");
                        int val = scan.nextInt();
                        scan.nextLine();

                            JSONObject person = (JSONObject) data.get(newKyes[val - 1]);

                            //Skriv ut namnen, ålder eller favoritfärg för personer

                        System.out.println("Info id " + person.get("id"));
                        System.out.println("Namn: " + person.get("name"));
                        System.out.println("Ålder: " + person.get("age"));
                        System.out.println("Favoritfärg: " + person.get("favoriteColor"));


                    } else if(message[0].equals("2")){
                        System.out.println("--------------------------");
                        System.out.println("1. Hämta namn på alla personer");
                        System.out.println("2. Hämta ålder på alla personer");
                        System.out.println("3. Hämta favorit färg på alla personer");
                        System.out.println("Skriv in ditt menyval: ");
                        String val = scan.nextLine();
                        System.out.println("Info:");
                        for (Object x : newKyes) {
                            JSONObject person = (JSONObject) data.get(x);

                            //Skriv ut namnen, ålder eller favoritfärg för personer

                            if (val.equals("1")) {
                                System.out.print("id " + person.get("id") + " namn: ");
                                System.out.println(person.get("name"));
                            } else if (val.equals("2")) {
                                System.out.print("id " + person.get("id") + " ålder: ");
                                System.out.println(person.get("age"));
                            } else if (val.equals("3")) {
                                System.out.print("id " + person.get("id") + " favoritfärg: ");
                                System.out.println(person.get("favoriteColor"));
                            }
                        }
                    }


                }
            }

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                //Stäng kopplingar
                if (socket != null) socket.close();
                if (inputSR != null) inputSR.close();
                if (outputSW != null) outputSW.close();
                if (bWriter != null) bWriter.close();
                if (bReader != null) bReader.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Client Avslutas");
        }
    }

    static String[] userInput() {
        //Steg 1. Skriv ut en meny för användaren
        System.out.println("-----------------------------------");
        System.out.println("1. Hämta info på en specifik person");
        System.out.println("2. Hämta info på alla personer");

        //Steg 2. Låta användaren göra ett val
        Scanner scan = new Scanner(System.in);
        System.out.println("Skriv in ditt menyval: ");

        String val = scan.nextLine();

        //Steg 3. Bearbeta användarens val
        //Skapa JSON objekt för att hämta data om alla personer. Stringifiera objektet och returnera det
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("httpURL", "persons");
        jsonReturn.put("httpMethod", "get");

        String[] returnedData = {val, jsonReturn.toJSONString()};

        //Returnera JSON objekt och val
        return returnedData;
        //break;
    }
}