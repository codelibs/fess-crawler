/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.client.http.webdriver;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.PooledObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * @author shinsuke
 *
 */
public class CrawlerWebDriver implements WebDriver, JavascriptExecutor, FindsById,
        FindsByClassName, FindsByLinkText, FindsByName, FindsByCssSelector,
        FindsByTagName, FindsByXPath, HasInputDevices, HasCapabilities,
        TakesScreenshot {

    public Capabilities capabilities;

    protected WebDriver webDriver;

    public String phantomjsBinaryPath;

    public String phantomjsGhostdriverPath;

    public String[] phantomjsCliArgs;

    public String[] phantomjsGhostdriverCliArgs;

    public String webdriverChromeDriver;

    public URL remoteAddress;

    @PostConstruct
    public void phantomjs() {
        if (capabilities == null) {
            capabilities = DesiredCapabilities.phantomjs();
        }
        if (capabilities instanceof DesiredCapabilities) {
            if (phantomjsBinaryPath == null) {
                phantomjsBinaryPath = System.getProperty("phantomjs.binary.path");
            }
            if (phantomjsBinaryPath != null) {
                ((DesiredCapabilities) capabilities).setCapability("phantomjs.binary.path", phantomjsBinaryPath);
            }

            if (phantomjsGhostdriverPath == null) {
                phantomjsGhostdriverPath = System.getProperty("phantomjs.ghostdriver.path");
            }
            if (phantomjsGhostdriverPath != null) {
                ((DesiredCapabilities) capabilities).setCapability("phantomjs.ghostdriver.path", phantomjsGhostdriverPath);
            }

            if (phantomjsCliArgs == null) {
                final String value = System.getProperty("phantomjs.cli.args");
                if (value != null) {
                    phantomjsCliArgs = value.split(" ");
                }
            }
            if (phantomjsCliArgs != null) {
                ((DesiredCapabilities) capabilities).setCapability("phantomjs.cli.args", phantomjsCliArgs);
            }

            if (phantomjsGhostdriverCliArgs == null) {
                final String value = System.getProperty("phantomjs.ghostdriver.cli.args");
                if (value != null) {
                    phantomjsGhostdriverCliArgs = value.split(" ");
                }
            }
            if (phantomjsGhostdriverCliArgs != null) {
                ((DesiredCapabilities) capabilities).setCapability("phantomjs.ghostdriver.cli.args", phantomjsGhostdriverCliArgs);
            }
        }
        webDriver = new PhantomJSDriver(capabilities);
    }

    public void chrome() {
        if (capabilities == null) {
            capabilities = DesiredCapabilities.chrome();
        }
        if (capabilities instanceof DesiredCapabilities) {
            if (webdriverChromeDriver != null) {
                ((DesiredCapabilities) capabilities).setCapability(
                        "webdriver.chrome.driver", webdriverChromeDriver);
            }
        }
        webDriver = new RemoteWebDriver(remoteAddress,
                DesiredCapabilities.chrome());
    }

    public static class OnDestroyListener
            implements
            org.codelibs.fess.crawler.pool.CrawlerPooledObjectFactory.OnDestroyListener<CrawlerWebDriver> {
        @Override
        public void onDestroy(final PooledObject<CrawlerWebDriver> p) {
            final CrawlerWebDriver driver = p.getObject();
            driver.quit();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.TakesScreenshot#getScreenshotAs(org.openqa.selenium
     * .OutputType)
     */
    @Override
    public <X> X getScreenshotAs(final OutputType<X> target)
            throws WebDriverException {
        return ((TakesScreenshot) webDriver).getScreenshotAs(target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.HasCapabilities#getCapabilities()
     */
    @Override
    public Capabilities getCapabilities() {
        return ((HasCapabilities) webDriver).getCapabilities();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.interactions.HasInputDevices#getKeyboard()
     */
    @Override
    public Keyboard getKeyboard() {
        return ((HasInputDevices) webDriver).getKeyboard();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.interactions.HasInputDevices#getMouse()
     */
    @Override
    public Mouse getMouse() {
        return ((HasInputDevices) webDriver).getMouse();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByXPath#findElementByXPath(java.lang
     * .String)
     */
    @Override
    public WebElement findElementByXPath(final String using) {
        return ((FindsByXPath) webDriver).findElementByXPath(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByXPath#findElementsByXPath(java.lang
     * .String)
     */
    @Override
    public List<WebElement> findElementsByXPath(final String using) {
        return ((FindsByXPath) webDriver).findElementsByXPath(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByTagName#findElementByTagName(java
     * .lang.String)
     */
    @Override
    public WebElement findElementByTagName(final String using) {
        return ((FindsByTagName) webDriver).findElementByTagName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByTagName#findElementsByTagName(java
     * .lang.String)
     */
    @Override
    public List<WebElement> findElementsByTagName(final String using) {
        return ((FindsByTagName) webDriver).findElementsByTagName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByCssSelector#findElementByCssSelector
     * (java.lang.String)
     */
    @Override
    public WebElement findElementByCssSelector(final String using) {
        return ((FindsByCssSelector) webDriver).findElementByCssSelector(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByCssSelector#findElementsByCssSelector
     * (java.lang.String)
     */
    @Override
    public List<WebElement> findElementsByCssSelector(final String using) {
        return ((FindsByCssSelector) webDriver)
                .findElementsByCssSelector(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByName#findElementByName(java.lang.
     * String)
     */
    @Override
    public WebElement findElementByName(final String using) {
        return ((FindsByName) webDriver).findElementByName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByName#findElementsByName(java.lang
     * .String)
     */
    @Override
    public List<WebElement> findElementsByName(final String using) {
        return ((FindsByName) webDriver).findElementsByName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByLinkText#findElementByLinkText(java
     * .lang.String)
     */
    @Override
    public WebElement findElementByLinkText(final String using) {
        return ((FindsByLinkText) webDriver).findElementByLinkText(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByLinkText#findElementsByLinkText(java
     * .lang.String)
     */
    @Override
    public List<WebElement> findElementsByLinkText(final String using) {
        return ((FindsByLinkText) webDriver).findElementsByLinkText(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByLinkText#findElementByPartialLinkText
     * (java.lang.String)
     */
    @Override
    public WebElement findElementByPartialLinkText(final String using) {
        return ((FindsByLinkText) webDriver)
                .findElementByPartialLinkText(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByLinkText#findElementsByPartialLinkText
     * (java.lang.String)
     */
    @Override
    public List<WebElement> findElementsByPartialLinkText(final String using) {
        return ((FindsByLinkText) webDriver)
                .findElementsByPartialLinkText(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByClassName#findElementByClassName(
     * java.lang.String)
     */
    @Override
    public WebElement findElementByClassName(final String using) {
        return ((FindsByClassName) webDriver).findElementByClassName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsByClassName#findElementsByClassName
     * (java.lang.String)
     */
    @Override
    public List<WebElement> findElementsByClassName(final String using) {
        return ((FindsByClassName) webDriver).findElementsByClassName(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsById#findElementById(java.lang.String)
     */
    @Override
    public WebElement findElementById(final String using) {
        return ((FindsById) webDriver).findElementById(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.internal.FindsById#findElementsById(java.lang.String)
     */
    @Override
    public List<WebElement> findElementsById(final String using) {
        return ((FindsById) webDriver).findElementsById(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.JavascriptExecutor#executeScript(java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public Object executeScript(final String script, final Object... args) {
        return ((JavascriptExecutor) webDriver).executeScript(script, args);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openqa.selenium.JavascriptExecutor#executeAsyncScript(java.lang.String
     * , java.lang.Object[])
     */
    @Override
    public Object executeAsyncScript(final String script, final Object... args) {
        return ((JavascriptExecutor) webDriver)
                .executeAsyncScript(script, args);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#get(java.lang.String)
     */
    @Override
    public void get(final String url) {
        webDriver.get(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#getCurrentUrl()
     */
    @Override
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#getTitle()
     */
    @Override
    public String getTitle() {
        return webDriver.getTitle();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#findElements(org.openqa.selenium.By)
     */
    @Override
    public List<WebElement> findElements(final By by) {
        return webDriver.findElements(by);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#findElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findElement(final By by) {
        return webDriver.findElement(by);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#getPageSource()
     */
    @Override
    public String getPageSource() {
        return webDriver.getPageSource();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#close()
     */
    @Override
    public void close() {
        webDriver.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#quit()
     */
    @Override
    public void quit() {
        webDriver.quit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#getWindowHandles()
     */
    @Override
    public Set<String> getWindowHandles() {
        return webDriver.getWindowHandles();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#getWindowHandle()
     */
    @Override
    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#switchTo()
     */
    @Override
    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#navigate()
     */
    @Override
    public Navigation navigate() {
        return webDriver.navigate();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.WebDriver#manage()
     */
    @Override
    public Options manage() {
        return webDriver.manage();
    }
}
