package models;

import java.util.List;

import play.test.Fixtures;

/**
 * 零售商渠道描述
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月8日 下午3:14:12
 */
public class RetailerChannel {

    public int id;
    public String channel;
   
    static List<RetailerChannel> channels = (List<RetailerChannel>) Fixtures.loadYamlAsList("static-items/retailer-channel.yml");
    
    public static List<RetailerChannel> getChannelList() {
        //List<RetailerChannel> channels = (List<RetailerChannel>) Fixtures.loadYamlAsList("static-items/retailer-channel.yml");
        return channels;
    }
}
