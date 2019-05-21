package com.dotmarketing.webdav;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.dotcms.repackage.com.bradmcevoy.http.Resource;
import com.dotcms.repackage.com.bradmcevoy.http.ResourceFactory;
import com.dotcms.util.IntegrationTestInitService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResourceFactoryTest {

    private String HOST = "localhost:8008";

    @BeforeClass
    public static void initialize() throws Exception {
        IntegrationTestInitService.getInstance().init();
    }

    @Test
    public void Test_Input_Meaningless_TempFolder() throws Exception{
        final ResourceFactory resourceFactory = new ResourceFactorytImpl();
        final Resource resource1 = resourceFactory.getResource(HOST,"/webdav/live/1/demo.dotcms.com/application/vtl/test.vtl.sb-810a7a8f-5qunmm");
        Assert.assertNull(resource1);

        final Resource resource2 = resourceFactory.getResource(HOST,"/webdav/live/1/demo.dotcms.com/application/vtl/test.vtl.sb-810a7a8f-b5kc2w");
        Assert.assertNull(resource2);
    }
//  /robots.txt.sb-810a7a8f-pjrhOj/robots.txt.sb-810a7a8f-pjrhOj
//  /robots.txt.txt.sb-810a7a8f-aIBd9Y/robots.txt.txt.sb-810a7a8f-aIBd9Y

    @Test
    public void test_Input_Existing_Folder_URL() throws Exception{
        final String inputURL = "/webdav/live/1/demo.dotcms.com/application/vtl";
        final ResourceFactory resourceFactory = new ResourceFactorytImpl();

        final Resource resource1 = resourceFactory.getResource(HOST,inputURL);

        Assert.assertNotNull(resource1);
        assertThat(resource1, instanceOf(FolderResourceImpl.class));

        final Resource resource2 = resourceFactory.getResource(HOST,inputURL + "/");
        assertThat(resource2, instanceOf(FolderResourceImpl.class));
    }

    @Test
    public void Test_Temp_Resource_Input_Expcet_TempResource() throws Exception{
        final ResourceFactory resourceFactory = new ResourceFactorytImpl();

        final String inputURL1 =  "/webdav/live/1/demo.dotcms.com/application/vtl/.test.vtl";
        final Resource resource1 = resourceFactory.getResource(HOST,inputURL1);
        Assert.assertNull(resource1);
        assertThat(resource1, instanceOf(TempFolderResourceImpl.class));

        final String inputURL2 = "/webdav/live/1/demo.dotcms.com/application/vtl/._test.vtl";
        final Resource resource2 = resourceFactory.getResource(HOST,inputURL2);
        Assert.assertNull(resource2);
        assertThat(resource2, instanceOf(TempFolderResourceImpl.class));
    }

    @Test
    public void Test_Match_Temp_Resource_Names_Then_Test_Non_Temp_Resource_Names(){
        final DotWebdavHelper helper = new DotWebdavHelper();
        final String [] tempResourceStrings = {
            ".",
            ".something",
            ".something.xyz",
            "/.",
            "/.something",
            "/.something.xyz",
            "._.DS_Store",
            "/._.DS_Store"
        };

        for(final String s:tempResourceStrings){
            Assert.assertTrue("failed validating tmp file '" + s + "'",helper.matchGeneralTempResource(s));
        }

        final String [] nonTempResourceStrings = {
                "something",
                "/something.xyz",
                "/",
        };

        for(final String s:nonTempResourceStrings){
            Assert.assertFalse("failed validating tmp file '" + s + "'",helper.matchGeneralTempResource(s));
        }
    }

    @Test
    public void Test_Get_File_Name_From_Temp_File_Name(){
       final DotWebdavHelper helper = new DotWebdavHelper();
       final String filePath1 = "robots.txt.sb-810a7a8f-gUPc71";
       final String expected1 = "robots.txt";
       assertEquals(expected1, helper.getFileName(filePath1));

       final String filePath2 = "robots-lol.txt.sb-810a7a8f-gUPc71";
       final String expected2 = "robots-lol.txt";
       assertEquals(expected2, helper.getFileName(filePath2));

       final String filePath3 = "/index-lowecase-stuff.txt.sb-810a7a8f-h6qBhF/index-lowecase-stuff.txt.sb-810a7a8f-h6qBhF";
       final String expected3 = "index-lowecase-stuff.txt";
       assertEquals(expected3, helper.getFileName(filePath3));
    }


    @Test
    public void Test_Temp_Files_Detection(){
        final DotWebdavHelper helper = new DotWebdavHelper();

        final String [] tempResourceStrings = {
               "/webdav/live/1/demo.dotcms.com/application/vtl/containers/._screen shot 2019-05-15 at 10.24.14 am.png",
               "/Users/fabrizzio/code/tomcat8/temp/dotwebdav/demo.dotcms.com/application/vtl/containers/.ds_store",
               "/test.vtl.sb-810a7a8f-b5kc2w",
               "test.vtl.sb-810a7a8f-b5kc2w",
               "/application/vtl/test.vtl.sb-810a7a8f-b5kc2w/test.vtl.sb-810a7a8f-b5kc2w",
               "/._robots.txt.txt/._robots.txt.txt",
               "/application/vtl/._test.vtl.sb-810a7a8f-5qUNmM/._test.vtl.sb-810a7a8f-5qUNmM"
        };

        for(final String s:tempResourceStrings){
          Assert.assertTrue("failed validating tmp file '" + s + "'",helper.isTempResource(s));
        }
    }

}
