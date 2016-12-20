package engineer.carrot.warren.thump.curse;

import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;
import net.minecraft.util.StringUtils;

import java.util.List;

/**
 * Created by progwml6 on 7/4/16.
 */
public class CurseIntegration {
    protected String username;
    protected String password;
    protected String serverName;
    protected String channelName;
    protected int id;
    protected String curseUsername;
    protected IThumpMinecraftSink sink;
    private CurseThread thread;

    public CurseIntegration (String username, String password, String serverName, String channelName, IThumpMinecraftSink sink) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
        this.channelName = channelName;
        this.sink = sink;

    }

    public void setUsername (String user) {
        this.username = username;
    }

    public void setPassword (String pass) {
        this.password = pass;
    }

    public void setChannelName (String name) {
        this.channelName = name;
    }

    public void setServerName (String name) {
        this.serverName = name;
    }

    public void setSink (IThumpMinecraftSink sink) {
        this.sink = sink;
    }

    public void init () {
        LogHelper.info("starting up curse integration");
        if (!StringUtils.isNullOrEmpty(username) && !StringUtils.isNullOrEmpty(password)) {
            thread = new CurseThread(this);
            thread.setName("Thump - Curse");
            thread.start();
            LogHelper.info("CV plugin has started up");
        } else {
            LogHelper.info("not starting curse plugin as user/password was null or empty");
        }
    }

    public void sendToCurse (String message) {
        if (thread != null && thread.webSocket != null && thread.channel != null) {
            if (message == null) {
                LogHelper.info("Don't attempt to send null messages!!!!!");
            } else {
                thread.webSocket.sendMessage(thread.channel, message);
            }
        } else {
            LogHelper.error("curse thread not configured. Could not send message {} to curse", message);
        }
    }

    public List<String> status () {
        List<String> ret = Lists.newArrayList();
        if (StringUtils.isNullOrEmpty(serverName) || StringUtils.isNullOrEmpty(channelName)) {
            ret.add("Curse Plugin not configured");
        } else if (!thread.isAlive()) {
            ret.add("Curse Plugin is not connected");
        } else {
            ret.add("Curse Plugin connected to " + serverName + " " + channelName);
        }

        return ret;
    }

    public void shutdown () {
        //TODO logout??
        if (thread != null) {
            thread.interrupt();
        }
    }

}
