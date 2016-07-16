package engineer.carrot.warren.thump.cursevoice;

import com.feed_the_beast.javacurselib.common.enums.DevicePlatform;
import com.feed_the_beast.javacurselib.data.Apis;
import com.feed_the_beast.javacurselib.rest.RestUserEndpoints;
import com.feed_the_beast.javacurselib.service.contacts.contacts.ChannelContract;
import com.feed_the_beast.javacurselib.service.contacts.contacts.ContactsResponse;
import com.feed_the_beast.javacurselib.service.contacts.contacts.GroupNotification;
import com.feed_the_beast.javacurselib.service.logins.login.LoginRequest;
import com.feed_the_beast.javacurselib.service.logins.login.LoginResponse;
import com.feed_the_beast.javacurselib.service.sessions.sessions.CreateSessionRequest;
import com.feed_the_beast.javacurselib.service.sessions.sessions.CreateSessionResponse;
import com.feed_the_beast.javacurselib.utils.CurseGUID;
import com.feed_the_beast.javacurselib.websocket.WebSocket;
import com.feed_the_beast.javacurselib.websocket.messages.notifications.NotificationsServiceContractType;
import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by progwml6 on 7/4/16.
 */
public class CurseIntegration {
    private String username;
    private String password;
    private RestUserEndpoints endpoints;
    private ContactsResponse contacts;
    private CreateSessionResponse session;
    private WebSocket webSocket;
    private String serverName;
    private String channelName;
    private CurseGUID channel;
    private IThumpMinecraftSink sink;

    public CurseIntegration (String username, String password, String serverName, String channelName, IThumpMinecraftSink sink) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
        this.channelName = channelName;
        endpoints = new RestUserEndpoints();
        endpoints.setupEndpoints();
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
        try {
            CountDownLatch latch = new CountDownLatch(1);
            LoginResponse loginResponse = endpoints.doLogin(new LoginRequest(username, password));
            endpoints.setAuthToken(loginResponse.session.token);
            endpoints.setupEndpoints();
            contacts = endpoints.contacts.get().get();
            session = endpoints.session.create(new CreateSessionRequest(CurseGUID.newRandomUUID(), DevicePlatform.UNKNOWN)).get();
            Optional<CurseGUID> id = contacts.getChannelIdbyNames(serverName, channelName, true);
            if (id.isPresent()) {
                channel = id.get();
                webSocket = new WebSocket(loginResponse, session, new URI(Apis.NOTIFICATIONS));
                webSocket.addTask(new CurseMessage(sink), NotificationsServiceContractType.CONVERSATION_MESSAGE_NOTIFICATION);
                webSocket.start();
            } else {
                LogHelper.error("could not find channel ID for {}/{}. Not starting CV plugin", serverName, channelName);
                latch.await();
                return;
            }
            latch.await();
        } catch (InterruptedException | ExecutionException | URISyntaxException e) {
            LogHelper.error("error setting up curse plugin", e);
        }
        LogHelper.info("CV plugin has started up");
    }

    public void shutdown () {
        //TODO logout??
        endpoints = new RestUserEndpoints();
        endpoints.setupEndpoints();
    }

    public void sendToCurse (String message) {
        webSocket.sendMessage(channel, message);
    }
}
