package com.apiumtech.bootstraptomee.frontend.pages;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: xavi
 * Date: 11/3/13
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class WelcomePageTest {

    private WicketTester tester;

    @BeforeMethod
    public void setUp() throws Exception {
        tester = new WicketTester();

    }

    @Test(enabled = false) //TODO: inject in basepage
    public void test_link() throws Exception {
        tester.startPage(HomePage.class);
        // click link and render
        tester.clickLink("toYourPage");
        tester.assertRenderedPage(WelcomePage.class);
        tester.assertLabel("yourMessage", "Hi!");
    }

}
