// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
public class RestaurantCRUDreviewsTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    driver = new ChromeDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void restaurantCRUDreviews() {
    driver.get("https://feup-food-backoffice.herokuapp.com/auth/login");
    driver.manage().window().setSize(new Dimension(1792, 1056));
    driver.findElement(By.id("input-email")).click();
    driver.findElement(By.id("input-email")).sendKeys("cantinaDeEngenharia@gmail.com");
    driver.findElement(By.id("input-password")).click();
    driver.findElement(By.id("input-password")).sendKeys("password");
    driver.findElement(By.id("input-password")).sendKeys(Keys.ENTER);
    js.executeScript("window.scrollTo(0,0)");
    driver.findElement(By.xpath("//span[contains(.,\'Reviews\')]")).click();
    driver.findElement(By.xpath("(//div[@id=\'review-information\']/button)[2]")).click();
    driver.findElement(By.xpath("//div[@id=\'cdk-overlay-0\']/nb-dialog-container/nb-card")).click();
    driver.findElement(By.id("input-answer")).click();
    driver.findElement(By.xpath("//div[@id=\'cdk-overlay-0\']/nb-dialog-container/nb-card/nb-card-body/form/div/textarea")).sendKeys("In soviet russia you eat potatoes!");
    driver.findElement(By.xpath("//div[@id=\'cdk-overlay-0\']/nb-dialog-container/nb-card/nb-card-body/form/div[2]/button")).click();
    driver.findElement(By.xpath("(//div[@id=\'review-information\']/button)[2]")).click();
    {
      WebElement element = driver.findElement(By.xpath("(//div[@id=\'review-information\']/button)[2]"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.id("input-answer")).sendKeys("In soviet russia you eat potatoes! And potatoes eat you!");
    driver.findElement(By.xpath("//div[@id=\'cdk-overlay-2\']/nb-dialog-container/nb-card/nb-card-body/form/div[2]/button")).click();
    driver.findElement(By.cssSelector(".context-menu-host .user-name")).click();
    driver.findElement(By.cssSelector(".ng-tns-c102-61 > .menu-title")).click();
  }
}
