import java.io.*;
import java.net.*;
import java.util.*;

public class Crawl {

    /* Do not change this base URL. All URLs for ths assignment are relative to this address */
    private static String baseURL = "https://www.csd.uwo.ca/faculty/solis/cs9668/test/";

    public static void main (String[] args) {

        /* Write your code here for the simple search engine */
        URL url;
        BufferedReader page = null;
        String line; // line in the page
        String[] inputWords;  // User specified query
        String[] query; // filtered to unique words in lower case
        Queue<URL> queue = new LinkedList<>(); // Queue to store all URLs that are reachable from seed url
        Vector<URL> matchUrls = new Vector<>(); // store the urls that contain ALL THE WORDS in a user-specified query.
        Vector<URL> processedUrls = new Vector<>(); // store the urls that have been processed
        Vector<String> foundQuery; // store query words that have been found in the page

        try {
            URL seedUrl = new URL(baseURL+"test.html");  // seed page url
            queue.add(seedUrl); // Store seed url in the queue

            inputWords = InOut.readQuery(); // Read user input and save queries into a String array

            // make words to lower case and remove the duplicated ones
            String str= String.join(",", inputWords);
            inputWords = str.toLowerCase().split(",");  // to lower case
            // remove duplicated ones
            query = new LinkedHashSet<String>(Arrays.asList(inputWords)).toArray(new String[0]);

            while(!queue.isEmpty()) { // process items in the queue
                url = queue.remove(); // dequeue

                if(url != null && !processedUrls.contains(url)) // filter out processed url
                {
                    processedUrls.add(url);
                    page = new BufferedReader(new InputStreamReader(url.openStream())); // download page
                    foundQuery = new Vector<String>(); // store query word that found in the page
                    while ((line = page.readLine()) != null)  // process page line by line
                    {
                        URL inPageUrl = extractURL(line); // try to extract URL from this line
                        // Add inPageUrl to queue if it is not already existing in the queue
                        if (inPageUrl != null && !queue.contains(inPageUrl))
                        {
                            queue.add(inPageUrl);
                        }
                        // Check if the query words is in the line of the page
                        for (int i = 0; i < query.length; i++)
                        {
                            String word = query[i];
                            if ((line.toLowerCase()).indexOf(word) != -1 && !foundQuery.contains((word))) {
                                foundQuery.add(word);
                            }
                        } // end of for
                        // save page into marchUrls list if it matches all the query words
                        if(foundQuery.size() == query.length && !matchUrls.contains(url))
                        {
                            matchUrls.add((url));
                        }
                    } // end of while (line processing)
                }
            }// end of while (url processing)

            for(URL u : matchUrls)
            {
                InOut.printFileName(u);  // Invoke InOut function to print file names
            }
            page.close();  // close processed page
            InOut.endListFiles(); //  Invoke InOut function to print out the number of files found
            System.out.println(("Number of documents: " + (processedUrls.size() - 1))); // exclude the seed page

        } catch (MalformedURLException mue) {
            System.out.println("Malformed URL");

        } catch (IOException ioe) {
            System.out.println("IOException "+ioe.getMessage());
        }
    }

    /* If there is an URL embedded in the text passed as parameter, the URL will be extracted and
       returned; if there is no URL in the text, the value null will be returned              */
    public static URL extractURL(String text) throws MalformedURLException {
        String textUrl;
        int index = text.lastIndexOf("a href=");
        if (index > -1) {
            textUrl = baseURL+text.substring(index+8,text.length()-2);   // Form the complete URL
            return new URL(textUrl);
        }
        else return null;
    }
} 


