package org.seasar.robot;

import java.io.File;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.service.DataService;
import org.seasar.robot.transformer.impl.FileTransformer;

public class S2RobotTest extends S2TestCase {

    public S2Robot s2Robot;

    public DataService dataService;

    public FileTransformer fileTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_executeTx() throws Exception {
        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.path = file.getAbsolutePath();
        s2Robot.addUrl("http://www.n2sm.net/");
        s2Robot.setMaxAccessCount(50);
        s2Robot.setNumOfThread(10);
        s2Robot.urlFilter.addInclude("http://www.n2sm.net/.*");
        String sessionId = s2Robot.execute();
        assertEquals(50, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }
}
