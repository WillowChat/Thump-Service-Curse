package engineer.carrot.warren.thump.cursevoice;

import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;
import net.minecraft.util.StringUtils;

/**
 * Created by progwml6 on 7/4/16.
 */
public class CurseIntegration {
    protected String username;
    protected String password;
    protected String serverName;
    protected String channelName;
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
            thread.webSocket.sendMessage(thread.channel, message);
        } else {
            LogHelper.error("curse thread not configured. Could not send message {} to curse", message);
        }
    }

    public void shutdown () {
        //TODO logout??
        thread.interrupt();
    }

}
