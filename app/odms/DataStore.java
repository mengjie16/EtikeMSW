package odms;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.AppMode;
import com.aton.config.Config;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.base.Objects;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * 从Mongodb中进行最原始的数据交互
 * 
 * @author tr0j4n
 * @since v0.4.7
 * @created 2013-12-21 下午5:11:02
 */
public class DataStore {

    private static final Logger log = LoggerFactory.getLogger(DataStore.class);
    // 数据库会话对象
    private static String MDB_HOST = Config.getProperty("mdb.host");
    private static int MDB_PORT = NumberUtils.toInt(Config.getProperty("mdb.port"));
    private static String MDB_DB = Config.getProperty("mdb.db", "eitak");
    private static String MDB_USER = Config.getProperty("mdb.db.user");
    private static String MDB_PASS = Config.getProperty("mdb.db.pass");
    private static boolean MDB_AUTH = false;
    // 初始化
    static {
        String auth =  Config.getProperty("mdb.auth","close");
        if(Objects.equal(auth, "open")){
            MDB_AUTH = true;
            MDB_HOST = Config.getProperty("mdb.auth.host");
            MDB_PORT = NumberUtils.toInt(Config.getProperty("mdb.auth.port"));
            MDB_DB = Config.getProperty("mdb.auth.db", "eitak");
            MDB_USER = Config.getProperty("mdb.auth.db.user");
            MDB_PASS = Config.getProperty("mdb.auth.db.pass");
        }
    }

    // 每张表维护一个会话
    private static Map<String, MongoClient> _clients = new HashMap<String, MongoClient>();
    
    /**
     * 获取db操作的表对象(根据auth情况，获取不同对象)
     *
     * @param tableName
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月7日 下午2:29:17
     */
    public static DBCollection dbCollection(String tableName){
        
        return MDB_AUTH?getCollection(tableName):table(tableName);
    }
    
    /**
     * 
     * 获得操作的表对象
     * 
     * @param tableName 表名
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-21 下午6:01:53
     */
    public static DBCollection table(String tableName) {
        MongoClient client = _clients.get(tableName);
        if (client == null) {
            try {
                client = new MongoClient(MDB_HOST, MDB_PORT);
                log.info("Mongodb 会话建立成功，Host={},Port={}", MDB_HOST, MDB_PORT);
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
            _clients.put(tableName, client);
        }
        DB jdpDb = client.getDB(MDB_DB);
        return jdpDb.getCollection(tableName);
    }

    /**
     * 
     * 获得操作的表对象.<br>
     * 该方法用于连接远程mongoDB.
     * 
     * @param collection
     * @return
     * @since v0.6.4
     * @author youblade
     * @created 2014年4月10日 下午5:47:40
     */
    public static DBCollection getCollection(String collection) {
        // Using "host:db:collection" as key
        String key = MDB_HOST + StringUtils.COLON + MDB_DB + StringUtils.COLON + collection;
        MongoClient client = _clients.get(key);
        if (client == null) {
            try {
                client = new MongoClient(MDB_HOST, MDB_PORT);
                log.info("Mongodb 会话建立成功，Host={},Port={}", MDB_HOST, MDB_PORT);
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
            _clients.put(key, client);
        }
        DB mongodb = client.getDB(MDB_DB);
        boolean auth = mongodb.authenticate(MDB_USER, MDB_PASS.toCharArray());
        if (!auth) {
            log.error("Auth failed, Host={},Port={}", MDB_HOST, MDB_PORT);
        }
        return mongodb.getCollection(collection);
    }
}
