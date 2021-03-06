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
public class ReviewTest {
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
  public void review() {
    driver.get("https://feup-food-client.herokuapp.com/auth/login");
    driver.manage().window().setSize(new Dimension(1920, 1053));
    driver.findElement(By.id("input-email")).sendKeys("francisco@gmail.com");
    driver.findElement(By.id("input-password")).sendKeys("password");
    driver.findElement(By.id("input-password")).sendKeys(Keys.ENTER);
    driver.findElement(By.cssSelector(".ng-tns-c98-2:nth-child(2)")).click();
    driver.findElement(By.cssSelector(".col-4:nth-child(1) .w-100")).click();
    driver.findElement(By.cssSelector(".ng-tns-c98-6:nth-child(2)")).click();
    assertThat(driver.findElement(By.cssSelector(".row:nth-child(1) > #review-information > p:nth-child(4)")).getText(), is("Lamentável! Hoje o prato era arroz com molho de tomate."));
    driver.findElement(By.cssSelector(".btnToggle:nth-child(4)")).click();
    driver.findElement(By.id("input-comment")).click();
    driver.findElement(By.id("input-comment")).sendKeys("Boa perna de frango.");
    driver.findElement(By.cssSelector(".status-success")).click();
    driver.findElement(By.cssSelector(".context-menu-host .user-name")).click();
    driver.findElement(By.cssSelector(".ng-tns-c98-9 > .menu-title")).click();
  }
}
