package com.dotmarketing.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.dotcms.IntegrationTestBase;
import com.dotcms.datagen.FolderDataGen;
import com.dotcms.datagen.SiteDataGen;
import com.dotcms.util.IntegrationTestInitService;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.model.Folder;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class PermissionBitFactoryImplTest extends IntegrationTestBase {

    @BeforeClass
    public static void prepare() throws Exception {
        //Setting web app environment
        IntegrationTestInitService.getInstance().init();

    }

    @Test
    public void Test_Load_Permissions_Recursive_Call() throws DotDataException, DotSecurityException {
        final Host host = new SiteDataGen().nextPersisted();
        final PermissionFactory permissionFactory = FactoryLocator.getPermissionFactory();
        final PermissionBitFactoryImpl impl = PermissionBitFactoryImpl.class.cast(permissionFactory);
        Folder parent = null;
        for(int i=0; i <= 3; i++) {
           final String name = String.format("depth-%d",i);
           if(null == parent){
               parent = new FolderDataGen().site(host).name(name).nextPersisted();
           }
           else {
               parent = new FolderDataGen().parent(parent).name(name).nextPersisted();
           }
        }

        Permissionable pp = parent.getParentPermissionable();
        while(pp != null){
           //System.out.println(pp);
           pp = pp.getParentPermissionable();
        }

       impl.permissionCache.clearCache();
        System.out.println("Now lets get the permissions.");
        final List<Permission> permissions = impl.loadPermissions(parent);
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());

    }

}
