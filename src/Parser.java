import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.*;

public class Parser{
    /* each Document has a name
     *  its url
     *  its source code maybe
     *  result part:
     *  more children url;
     *  more images;
     *  more articles
     *  more titles
     * */
    public static void main (String [] args) throws IOException, ParseException{
        Document doc = Jsoup.connect("http://www.bbc.com/news/").get();

        /* to find the news link in the main page: FAIL
           Elements news = doc.select("a[href*=news]");
           for(Element ele : news){
           System.out.println(ele.attr("href"));
           }
        */

        /* in most cases, there is a rss link in news page; if there is no rss link, we analyze the whole links
         * get the rss link from the main news page
        */

        Element rss = doc.select("a[href*=rss]").first();
        if (rss != null) {
            //get the content of the rss page
            Document newsDoc = Jsoup.connect(rss.attr("href")).get();

            //to see the xml tags not the htmk tags : System.out.println(newsDoc.html());
            //get the news items from rss page
            Elements items = newsDoc.select("item");

            //get the first news item
            Element test = items.first();
            String title = test.select("title").text();
            //get the news item page link
            String link = test.select("guid").text();

            //get the content of the news item page
            Document page = Jsoup.connect(link).get();
            System.out.println(link);
            System.out.println(title);

            //get the title of the news item page
            String newTitle = page.select("h1.story-header").text();
            System.out.println(newTitle);

            //get the latest update time of the news item page
            Element date = page.select("div.story-body span.date").first();
            System.out.println(date.text());
            Element time = page.select("div.story-body span.time").first();
            System.out.println(time.text());
            String updateTime = date.text() + " " + time.text();
            SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy HH:mm");
            Date d = f.parse(updateTime);
            int updateSec = (int)(d.getTime() / 1000); //ms to s
            System.out.println(updateSec);

            //get the article of the news item page
            StringBuilder content = new StringBuilder();
            Elements ps = page.select("div.story-body p");
            for (Element p : ps) {
                content.append(p.text());
            }
            String contentStr = content.toString();

            //get images of the article from the news item page
            Elements imgs = page.select("div.story-body div.caption.full-width img[src]");//use dot to replace the whitespace in the name
            System.out.println("image size: " + imgs.size());
            for (Element img : imgs) {
                String imgLink = img.attr("src");
                //System.out.println(imgLink);
                //get the image name
                int imgNameIndex = imgLink.lastIndexOf("/");
                if(imgNameIndex == imgLink.length()){//if the link end with '/'
                    imgLink = imgLink.substring(1, imgNameIndex);
                }
                imgNameIndex =  imgLink.lastIndexOf("/");
                String imgName = imgLink.substring(imgNameIndex + 1, imgLink.length());
                System.out.println(imgName);

                //download img to local and save it to database
                URL imgUrl = new URL(imgLink);
                InputStream in = imgUrl.openStream();
                OutputStream out = new BufferedOutputStream(new FileOutputStream("../download/" + imgName));
                for(int b; (b = in.read()) != -1;){
                    out.write(b);
                }
                out.close();
                in.close();
            }


            //get the related links from the news item page
            Elements related = page.select("div.story-related div.hyper-related-assets a.story[href]");
            System.out.println(related.size());
            for(Element child : related){
                String childLink = child.attr("abs:href");

                //use these links we can create more Document
                //and do deeper crawler
            }

            /*
            //get every news item from the rss content
            for(Element item : items){
            //get news item page title
            String title = item.select("title").text();
            //get news item page link
            String link = item.select("guid").text();
            System.out.println(link);
            //get the content of the news item page
            Document page = Jsoup.connect(link).get();

            }
            //here
            */
        }


        // Elements storys = doc.select("a.story");
        //for(Element ele : links){
        //   System.out.println(ele.attr("href"));
        // }
    }
}
