package com.example.std.gettingstarted;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class if the LocalService IS needed for objectify
 */
public abstract class LocalDataStoreTest {

    private static final Logger log = LoggerFactory.getLogger(LocalDataStoreTest.class);
    private static final LocalServiceTestHelper helper = new LocalServiceTestHelper( new LocalDatastoreServiceTestConfig());

    @Before
    public void setup()  throws  Exception {
        setupHook();
    }

    protected void setupHook(){
    }

    @After
    public void teardown()throws Exception {
        teardownHook();
    }

    protected void teardownHook() {
    }

    @BeforeClass
    public static void init(){
        log.info("local data store starting");
        helper.setUp();
    }

    @AfterClass
    public static void after(){

        log.info("local data store stopping");
        helper.tearDown();
    }
}
