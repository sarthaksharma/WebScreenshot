import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverScreenshot {

    private static String userName = "guest";
    private static String password = "guest";

    public static String chromeDriverPath = "/usr/local/bin/chromedriver";

    static {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    public static ChromeOptions options = new ChromeOptions();

    static {
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-setuid-sandbox", "--hide-scrollbars", "--window-size=2560,1600", "--ignore-certificate-errors", "--silent");
    }

    public static WebDriver driver = new ChromeDriver(options);


    public static void screenshot(String dashboard_name) throws IOException, InterruptedException {

        Properties prop = new Properties();
        InputStream input = null;
        String dashboard_link = null;
        try {
            input = new FileInputStream("src/main/resources/config.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            dashboard_link = (prop.getProperty(dashboard_name));
            System.out.println(dashboard_link);

            // Get the  page
            driver.get(dashboard_link);

            // assuming NAME_2 also has a login required with guest:guest credentials
            if (dashboard_name == "NAME_2") {
                driver.findElement(By.xpath("//input[@id='username']")).sendKeys(userName);
                driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);

                // Locate the login button and click on it
                driver.findElement(By.xpath("//button[@type='submit']")).click();

                if (driver.getCurrentUrl().equals("<URL_2>/login")) {
                    System.out.println("Incorrect credentials");
                    driver.quit();
                    System.exit(1);
                } else {
                    System.out.println("Successfuly logged in");
                }
            }

            TimeUnit.SECONDS.sleep(4);
            // Take a screenshot of the current page
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("src/main/resources/screenshot.png"));

            //////////
            driver.quit();
            //////////

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void grafana(String dashboard_name) throws IOException, InterruptedException {
        String inline = null;
        URL url = new URL("<GRAFANA_URL>");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();
        if(responsecode != 200)

            throw new RuntimeException("HttpResponseCode: "+responsecode);

        else
                {
                    Scanner sc = new Scanner(url.openStream());
                    while(sc.hasNext())
                    {
                        inline+=sc.nextLine();
                    }
                    System.out.println("\nJSON data in string format");
                    System.out.println(inline);
                    sc.close();

                    JSONParser parse = new JSONParser();
                    JSONObject jobj = (JSONObject)parse.parse(inline);
                    JSONArray jsonarr_1 = (JSONArray) jobj.get(“results”);

                }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        screenshot("<DEFAULT_NAME>");
    }
}