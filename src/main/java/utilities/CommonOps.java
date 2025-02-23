package utilities;

import com.google.common.util.concurrent.Uninterruptibles;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.windows.WindowsDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.sikuli.script.Screen;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.MalformedURLException;

import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class CommonOps extends BasePage {

  @Step("Open Web Session")
  @Parameters({"BrowserName"})
  public void openWebSession(String BrowserName) {
    switch (BrowserName) {
      case "chrome":
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        break;
      case "firefox":
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        break;
      case "edge":
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        break;
      case "opera":
        WebDriverManager.operadriver().setup();
        driver = new OperaDriver();
        break;
      case "safari":
        WebDriverManager.safaridriver().setup();
        driver = new SafariDriver();
        break;
    }
    driver.manage().window().maximize();
    driver.get(getData("URL"));
    ManageWebPages.buildPages();
    action = new Actions(driver);
    screen = new Screen();
  }

  @Step("open Mobile Session")
  public void openMobileSession() throws MalformedURLException {
    capabilities = new DesiredCapabilities();
    capabilities.setCapability("reportDirectory", getData("reportDirectory"));
    capabilities.setCapability("reportFormat", getData("reportFormat"));
    capabilities.setCapability("testName", getData("testName"));
    capabilities.setCapability(MobileCapabilityType.UDID, "R58R34SLXBD");
    capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "kr.sira.unit");
    capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".Intro");
    mobileDriver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), capabilities);
    ManageMobilePages.buildPagesAppium();
  }

  @Step("Open API Session")
  public void openAPISession() {
    RestAssured.baseURI = getData("URL");
    request = RestAssured.given().auth().preemptive().basic("admin", "admin");
    request.header("Content-Type", "application/json");
    params = new JSONObject();
  }

  @Step("Open Electron Session")
  @Parameters({"BrowserName"})
  public void openElectronSession(String BrowserName) {
    System.setProperty("webdriver.chrome.driver", "./electrondriver.exe");
    chromeOptions = new ChromeOptions();
    chromeOptions.setBinary("C:/Users/exoli/AppData/Local/Programs/todolist/Todolist.exe");
    capabilities = new DesiredCapabilities();
    capabilities.setCapability("chromeOptions", chromeOptions);
    capabilities.setBrowserName(BrowserName);
    chromeOptions.merge(capabilities);
    driver = new ChromeDriver(chromeOptions);
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    ManageElectronPages.buildPages();
  }

  @Step("Open Desktop Session")
  public void openDesktopSession() throws MalformedURLException {
    calcApp = "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App";
    capabilities = new DesiredCapabilities();
    capabilities.setCapability("app", calcApp);
    desktopDriver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
    softAssert = new SoftAssert();
    desktopDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    ManageDesktopPages.buildPages();
  }

  @Step("Open DB session")
  public void openDBSession() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");  //Load mysql jdbc driver
    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
    con = DriverManager.getConnection(getData("dbUrl"), getData("user"), getData("pass")); //Create DB connection
    stmt = con.createStatement(); //Create Statement Object
    ManageDB.buildPages();
    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
    query = "select * from UsersGrafana";
    rs = stmt.executeQuery(query); //Execute the SQL Query.Store results in ResultSe
  }

  @Step("Close Web Session")
  public void closeWebSession() {
    driver.quit();
  }

  @Step("close Mobile Session")
  public void closeMobileSession() {
    mobileDriver.quit();
  }

  @Step("close DB Session")
  public void closeDBSession() throws SQLException {
    con.close();
  }

  @Step("open desktop Session")
  public void closeDesktopSession() {
    desktopDriver.quit();
  }

  @BeforeClass
  @Parameters({"PlatformName","BrowserName"})
  public void startup(String PlatformName,@Optional String BrowserName) throws MalformedURLException, SQLException, ClassNotFoundException {
    switch (PlatformName) {
      case "web":
        openWebSession(BrowserName);
        break;
      case "electron":
        openElectronSession(BrowserName);
        break;
      case "mobile":
        openMobileSession();
        break;
      case "api":
        openAPISession();
        break;
      case "desktop":
        openDesktopSession();
        break;
      case "db":
        openWebSession(BrowserName);
        openDBSession();
        break;
    }

    softAssert = new SoftAssert();
  }

  @AfterClass
  @Parameters({"PlatformName"})
  public void teardown(String PlatformName) throws SQLException {
    switch (PlatformName) {
      case "web":
      case "electron":
        closeWebSession();
        break;
      case "mobile":
        closeMobileSession();
        break;
      case "desktop":
        closeDesktopSession();
        break;
      case "db":
        closeDBSession();
        closeWebSession();
        break;
    }
  }

  @Step("Save Screenshot")
  @Attachment(value = "Page Screenshot", type = "image/png")
  public byte[] saveScreenshot() {
    switch (getData("PlatformName")) {
      case "web":
      case "electron":
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      case "mobile":
        return ((TakesScreenshot) mobileDriver).getScreenshotAs(OutputType.BYTES);
      default:
        return null;
    }
  }

  @Step("Read From XML")
  @Description("Read XML from file path")
  public String getData(String nodeName) {
    DocumentBuilder dBuilder;
    Document doc = null;
    File fXmlFile = new File("./ConfigFiles/config.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    try {
      dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(fXmlFile);
    } catch (Exception e) {
      System.out.println("Exception in reading XML file: " + e);
    }
    doc.getDocumentElement().normalize();
    return doc.getElementsByTagName(nodeName).item(0).getTextContent();
  }
}
