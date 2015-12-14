package es.smartidea.android.legalalerts.boehandler;

/**
 * @BoeXMLHandler
 *
 * Manages downloading BOE´s XML summary parse it looking for other links,
 * then downloads all BOE´s announcements & dispositions found in XML format too.
 * Also extracts documents paragraphs (<p> tags) to a HashMap<>,
 * and holds it to offer a query by containing text method.
 *
 * BOE´s summary XML tag structure:
 * Diario >
 * Seccion >
 * Departamento >
 * Epigrafe >
 * Item >
 * <urlXml>
 * */

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoeXMLHandler {

    final String boeBaseURL = "http://www.boe.es";
    final String boeID = "/diario_boe/xml.php?id=BOE-S-";


    // String List where url of xml´s are stored
    private List<String> urlXMLs = new ArrayList<>();
    private String boeBaseURLString;
    private String currentBoeSummaryURLString;
    // HashMap where raw data are stored
    private Map<String, String> boeXmlTodayRawData = new HashMap<>();

    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public volatile boolean fetchCompleted = false;

    // Public Constructor with empty arguments
    // Creates new BoeXMLHandler object with current date (yyyyMMdd)
    public BoeXMLHandler() {
        Date curDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayDateString = dateFormat.format(curDate);
        final String currentBoeURL = this.boeID + todayDateString;

        this.boeBaseURLString = this.boeBaseURL;
        this.currentBoeSummaryURLString = this.boeBaseURL + currentBoeURL;

        Log.d("BOE", "BaseURL: " + boeBaseURLString);
        Log.d("BOE", "SummaryURL: " + currentBoeSummaryURLString);
    }

    // Returns number of urlXml tags found
    public int getURLXMLsCount() {
        return urlXMLs.size();
    }

    // Returns number of rawXmls parsed and stored
    public int getRawDataHashMapCount() {
        return boeXmlTodayRawData.size();
    }

    /*
    parseXMLSumAndGetRawData is a method to parse summary and get urlXml´s.
    Data is stored on List<String> urlXMLs.
    */
    public void parseXMLSumAndGetRawData(XmlPullParser boeParser) {
        int event;
        String text = null;

        try {
            event = boeParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = boeParser.getName();

                switch (event) {

                    case XmlPullParser.START_TAG:
                        Log.d("BOE", "Start TAG: " + name);
                        break;

                    case XmlPullParser.TEXT:
                        text = boeParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d("BOE", "End TAG: " + name);
                        if (name.equals("urlXml")) {
                            Log.d("BOE", "BOE´s summary XML urlXml tag found!!!.");
                            urlXMLs.add(text);
                        }
                        break;
                }
                event = boeParser.next();
            }
            parsingComplete = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    parseXMLAndStoreIt is a method to parse and store xmls data.
    Data is stored on HashMap<String, String> boeXmlTodayRawData.
    */
    public void parseXMLAndStoreIt(XmlPullParser boeParser, String id) {
        int event;
        String text, rawText;
        text = null;
        rawText = "";

        try {
            event = boeParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = boeParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = boeParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d("BOE", "BOE item´s XML end tag found.");
                        if (name.equals("p")) {
                            Log.d("BOE", "BOE item´s XML <p> tag found!!! Adding it to rawText.");
                            rawText += text;
                        }
                        break;

                }
                event = boeParser.next();
            }
            boeXmlTodayRawData.put(id, rawText);
            Log.d("BOE", "BOE item´s rawText stored on th HashMap.");
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     boeRawDataQuery is a method to query/search data in the object.
     It returns a list<> with matching BOE´s ids.
    */
    public List<String> boeRawDataQuery(String searchQuery) {
        List<String> queryResults = new ArrayList<>();
        try {
            // TODO Review/re-do search query execution
            for (Map.Entry<String, String> eachBoe : boeXmlTodayRawData.entrySet()) {
                Log.d("BOE", "Iterating inside rawData HashMap.");
                if (eachBoe.getValue().contains(searchQuery)) {
                    queryResults.add(eachBoe.getKey());
                    Log.d("BOE", "Coincidence found!");
                }
            }
            return queryResults;
        } catch (Exception e) {
            e.printStackTrace();
            queryResults.add("Errors found during search query!");
            return queryResults;
        }
    }

    /*
     fetchXML runs a thread to fetch URLs for summary and others
    */
    public void fetchXML() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetches XML´s summary URL and sends it to parse rawData URLs
                try {
                    URL summaryURL = new URL(currentBoeSummaryURLString);
                    HttpURLConnection summaryConn = (HttpURLConnection) summaryURL.openConnection();

                    summaryConn.setReadTimeout(10000 /* milliseconds */);
                    summaryConn.setConnectTimeout(15000 /* milliseconds */);
                    summaryConn.setRequestMethod("GET");
                    summaryConn.setDoInput(true);
                    summaryConn.connect();

                    InputStream boeSummaryStream = summaryConn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser boeSummaryParser = xmlFactoryObject.newPullParser();

                    boeSummaryParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                    // Set xml´s encoding to latin1
                    boeSummaryParser.setInput(boeSummaryStream, "ISO-8859-1");

                    parseXMLSumAndGetRawData(boeSummaryParser);
                    boeSummaryStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Fetches each rawXML and passes each one to parse and store data
                for (String eachUrlXML : urlXMLs) {
                    Log.d("BOE", "Iterating through urlXMLs<>.");
                    try {
                        URL itemURL = new URL(boeBaseURLString + eachUrlXML);
                        HttpURLConnection itemConn = (HttpURLConnection) itemURL.openConnection();

                        itemConn.setReadTimeout(10000 /* milliseconds */);
                        itemConn.setConnectTimeout(15000 /* milliseconds */);
                        itemConn.setRequestMethod("GET");
                        itemConn.setDoInput(true);
                        itemConn.connect();

                        InputStream boeStream = itemConn.getInputStream();
                        xmlFactoryObject = XmlPullParserFactory.newInstance();
                        XmlPullParser boeParser = xmlFactoryObject.newPullParser();

                        boeParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                        // Set xml´s encoding to latin1
                        boeParser.setInput(boeStream, "ISO-8859-1");

                        // Send parser and BOE´s id.
                        Log.d("BOE", "BOE id:" + eachUrlXML.substring(eachUrlXML.indexOf("=")));
                        parseXMLAndStoreIt(boeParser, eachUrlXML.substring(eachUrlXML.indexOf("=")));
                        boeStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Fetching completed
                Log.d("BOE", "Fetching BOE documents complete.");
                fetchCompleted = true;
            }
        });
        thread.start();
    }
}