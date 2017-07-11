package jobs;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.job.BaseJob;

import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends BaseJob {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public void doJob() throws Exception {
        log.info("=====App Started=====");
//
//      //配置C3P0日志实现
//        Properties p = new Properties(System.getProperties());
//
//        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.slf4j.Slf4jMLog");
//        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "INFO"); //日志实现不是FallbackMLog时.这个值不生效.
//
//        System.setProperties(p);
    }

}
