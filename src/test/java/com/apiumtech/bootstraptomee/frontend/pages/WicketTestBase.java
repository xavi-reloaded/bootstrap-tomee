package com.apiumtech.bootstraptomee.frontend.pages;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: xavi
 * Date: 11/3/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class WicketTestBase {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    protected WicketTester tester;
//    protected EnhancedWicketTester enhancedTester;
//    protected Clicker clicker;
//    protected Enterer enterer;
//    protected FormFiller formFiller;
//
//    public void init() {
//        populateData();
//        createTester();
//        initHelpers();
//    }
//
//    private void createTester() {
//        tester = new WicketTester((WebApplication);
//        applicationContext.getBean("wicketApplication"));
//        tester.setupRequestAndResponse();
//        tester.getWicketSession().setLocale(getLocale());
//        tester.getApplication().addComponentInstantiationListener(
//                new SpringComponentInjector(tester.getApplication(),
//                        applicationContext));
//        enhancedTester = new EnhancedWicketTester(tester);
//        initSessionBeforeTest((UserSession) tester.getWicketSession());
//    }
//
//    private void initHelpers() {
//        clicker = new Clicker(tester, enhancedTester);
//        formFiller = new FormFiller(tester, enhancedTester);
//        enterer = new Enterer(tester, enhancedTester, clicker, formFiller);
//    }
//
//    /**
//     * Override to change locale
//     * @return locale, default EN
//     */
//    protected Locale getLocale() {
//        return new Locale("EN");
//    }
//
//    /**
//     * Insert application specific properties to session
//     * @param session
//     */
//    protected void initSessionBeforeTest(UserSession session) {
//        // override in test if necessary
//    }
//
//    /**
//     * Override to populate data in database
//     * for each test
//     */
//    protected void populateData() {
//        // override in test if necessary
//    }
}