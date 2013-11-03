package com.apiumtech.bootstraptomee.frontend.pages;

import com.apiumtech.bootstraptomee.frontend.CommaFeedApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: xavi
 * Date: 11/3/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class WicketTest {

    private WicketTester tester;

    @BeforeMethod
    public void setUp() throws Exception {
        tester = new WicketTester(new CommaFeedApplication());
    }

    @Test(enabled = false)
    public void render()
    {
        //start and render the test page
        tester.startPage(HomePage.class);

        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);
    }

//    @Test
//    public void whenClickingOnCheckBoxLinkThenGoToCheckBoxPage() {
//        tester.startPage(HomePage.class);
//
//        tester.clickLink(HomePage.CHECK_BOX_LINK);
//
//        tester.assertRenderedPage(CheckBoxPage.class);
//    }
//
//    @Test
//    public void whenClickingOnDropDownLinkThenGotoDropDownPage() {
//        tester.startPage(HomePage.class);
//
//        tester.clickLink(HomePage.DROP_DOWN_LINK);
//
//        tester.assertRenderedPage(DropDownPage.class);
//    }
//
//    @Test
//    public void whenClickingOnRadioGroupLinkThenGotoRadioGroupPage() {
//        tester.startPage(HomePage.class);
//
//        tester.clickLink(HomePage.RADIO_GROUP_LINK);
//
//        tester.assertRenderedPage(RadioGroupPage.class);
//    }
}
