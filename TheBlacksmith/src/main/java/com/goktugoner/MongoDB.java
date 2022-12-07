package com.goktugoner;

import com.mongodb.client.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import twitter4j.v1.Status;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class MongoDB {
    public static MongoDatabase connectToDB(String dbname){
        //connect to DB
        MongoClient client = MongoClients.create("mongodb+srv://goktugoner:goktugoner123@cluster0.h0zwq.mongodb.net/?retryWrites=true&w=majority");
        return client.getDatabase(dbname);
    }
    public String addToDB(List<Status> tweetList, String username, String dbname){
        //add a DB for tweets
        MongoCollection<Document> col = connectToDB(dbname).getCollection(username); //add username as collection
        //add variables to the collection
        if(collectionExists(dbname, username)){
            return "DBExists";
        }
        if(tweetList.size() != 0){
            List<Document> docList = new ArrayList<>();
            for(Status status : tweetList){
                //check the tweet in order to get the full text if it's retweeted and the user it's retweeted from
                String statusText = status.getText();
                String retweetedFrom = "";
                if(status.isRetweet()) {
                    statusText = status.getRetweetedStatus().getText();
                    retweetedFrom = status.getRetweetedStatus().getUser().getScreenName();
                }
                docList.add(new Document("_id", tweetList.indexOf(status))
                        .append("Username", status.getUser().getScreenName())
                        .append("Tweet", statusText)
                        .append("Retweeted From", retweetedFrom)
                        .append("Date", status.getCreatedAt().getYear() + "/" + status.getCreatedAt().getMonthValue() + "/" + status.getCreatedAt().getDayOfMonth())
                        .append("Time", status.getCreatedAt().getHour() + ":" + status.getCreatedAt().getMinute() + ":" + status.getCreatedAt().getSecond())
                        .append("Fav Count", status.getFavoriteCount())
                        .append("RT Count", status.getRetweetCount())
                        .append("Replied To", status.getInReplyToScreenName())
                        .append("Tweet URL", "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId()));
            }
            col.insertMany(docList);
            return "DBSuccess";
        }
        return "DBFail";
    }

    public static List<Document> exportDB(String username, String dbname){
        //check if the collections in tweetDB has the requested collection
        if(!collectionExists(dbname, username)){
            return null;
        }
        MongoCollection<Document> col = connectToDB(dbname).getCollection(username);
        List<Document> docList = new LinkedList<>();
        for(Document doc : col.find()){
            docList.add(doc);
        }
        return docList;
    }

    public static ReturnType createExcelfromDB(List<Document> list, String username){
        if(list == null){
            return new ReturnType("Query not found on database.", null, false);
        }
        //create an Excel file to store the DB and then download it
        //Blank workbook
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            XSSFSheet sheet = workbook.createSheet(username);
            //Create a blank sheet

            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, list.get(0).size() - 1)); //title row must be filtered for easy navigation
            //write the data to an Object[] array
            Map<Integer, Object[]> data = new TreeMap<>();
            for(int i = 0; i < list.size(); i++){
                data.put(i + 1, new Object[] {list.get(i).get("_id"), list.get(i).get("Username"),
                        list.get(i).get("Tweet"), list.get(i).get("Retweeted From"), list.get(i).get("Date"),
                        list.get(i).get("Time"), list.get(i).get("Fav Count"), list.get(i).get("RT Count"),
                        list.get(i).get("Replied To"), list.get(i).get("Tweet URL")});
            }
            //create the title row first
            String[] columns = new String[]{"id", "Username", "Tweet", "Retweeted From", "Date", "Time", "Fav Count", "RT Count", "Replied To", "Tweet URL"};
            Row initialRow = sheet.createRow(0);
            int initialcellnum = 0;
            for (String column : columns) {
                Cell initialCell = initialRow.createCell(initialcellnum++);
                initialCell.setCellValue(column);
            }
            //Iterate over data and write to sheet
            Set<Integer> keyset = data.keySet();
            int rownum = 1;
            for (Integer key : keyset)
            {
                Row row = sheet.createRow(rownum++);
                Object [] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr)
                {
                    Cell cell = row.createCell(cellnum++);
                    if(obj instanceof String)
                        cell.setCellValue((String)obj);
                    else if(obj instanceof Integer)
                        cell.setCellValue((Integer)obj);
                }
            }
            //Write the workbook in file system
            File file = new File("tweetCluster_TheBlacksmith(" + username + ").xlsx");
            try(FileOutputStream out = new FileOutputStream(file)){
                workbook.write(out);
            }
            System.out.println("tweetCluster_TheBlacksmith(" + username + ").xlsx generated successfully.");
            return new ReturnType("tweetCluster_TheBlacksmith(" + username + ").xlsx generated successfully.", file, true);
        }catch (IOException io){
            io.printStackTrace();
            return new ReturnType("null", null, false);
        }
    }

    public List<String> listCollectionsDB(String dbname){
        List<String> userList = connectToDB(dbname).listCollectionNames().into(new ArrayList<>());
        if(userList.isEmpty()){
           return null;
        }
        return userList;
    }
    public String dropCollection(String query, String dbname){
        if(!collectionExists(dbname, query)){
            return "Query: " + query + " not found on database.";
        }
        MongoCollection<Document> col = connectToDB(dbname).getCollection(query);
        System.out.println(col.listIndexes().into(new ArrayList<>()));
        col.drop();
        return "Query: " + query + " removed from database successfully.";
    }

    public static boolean collectionExists(String dbname, String query){
        return connectToDB(dbname).listCollectionNames().into(new ArrayList<>()).contains(query);
    }
}
