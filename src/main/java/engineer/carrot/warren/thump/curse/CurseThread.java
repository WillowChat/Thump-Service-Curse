package engineer.carrot.warren.thump.curse;

import com.feed_the_beast.javacurselib.common.enums.DevicePlatform;
import com.feed_the_beast.javacurselib.data.Apis;
import com.feed_the_beast.javacurselib.rest.RestUserEndpoints;
import com.feed_the_beast.javacurselib.service.contacts.contacts.ContactsResponse;
import com.feed_the_beast.javacurselib.service.groups.groups.GroupInvitationRedeemResponse;
import com.feed_the_beast.javacurselib.service.logins.login.LoginRequest;
import com.feed_the_beast.javacurselib.service.logins.login.LoginResponse;
import com.feed_the_beast.javacurselib.service.sessions.sessions.CreateSessionRequest;
import com.feed_the_beast.javacurselib.service.sessions.sessions.CreateSessionResponse;
import com.feed_the_beast.javacurselib.utils.CurseGUID;
import com.feed_the_beast.javacurselib.websocket.WebSocket;
import com.feed_the_beast.javacurselib.websocket.messages.notifications.NotificationsServiceContractType;
import engineer.carrot.warren.thump.helper.LogHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class CurseThread extends Thread {
    WebSocket webSocket;
    CurseGUID channel;
    private RestUserEndpoints endpoints;
    private ContactsResponse contacts;
    private CreateSessionResponse session;
    private CurseIntegration integration;

    public CurseThread (CurseIntegration integration) {
        this.integration = integration;
    }

    @Override
    public void run () {
        try {
            endpoints = new RestUserEndpoints();
            endpoints.setupEndpoints();

            CountDownLatch latch = new CountDownLatch(1);
            LoginResponse loginResponse = endpoints.doLogin(new LoginRequest(integration.username, integration.password));
            endpoints.setAuthToken(loginResponse.session.token);
            endpoints.setupEndpoints();
            contacts = endpoints.contacts.get().get();
            session = endpoints.session.create(new CreateSessionRequest(CurseGUID.newRandomUUID(), DevicePlatform.UNKNOWN)).get();
            Optional<CurseGUID> id = contacts.getChannelIdbyNames(integration.serverName, integration.channelName, true);
            if (id.isPresent()) {
                joinServer(id.get(), loginResponse);
            } else {
                GroupInvitationRedeemResponse redeem = endpoints.servers.redeemInvite(integration.link.substring(integration.link.lastIndexOf("/") + 1)).get();
                if (redeem.channelID != null && redeem.group != null) {
                    contacts = endpoints.contacts.get().get();
                    id = contacts.getChannelIdbyNames(integration.serverName, integration.channelName, true);
                    LogHelper.info("invited to server {}/{} {}/{}.", redeem.group.groupTitle, redeem.group.getChannelNamebyId(redeem.channelID), redeem.group.groupID, redeem.channelID);
                }
                if (id.isPresent()) {
                    joinServer(id.get(), loginResponse);
                } else {
                    LogHelper.error("could not find channel ID for {}/{}. Not starting CV plugin", integration.serverName, integration.channelName);
                }
                latch.await();
                return;
            }
            latch.await();
        } catch (InterruptedException | ExecutionException | URISyntaxException e) {
            try {
                webSocket.sendMessage(channel, "Thump Curse bridge Disconnecting");
            } catch (Exception e2) {
                //noop
            }
            LogHelper.error("curse plugin thread shutting down", e);
        }
    }

    private void joinServer (CurseGUID id, LoginResponse loginResponse) throws URISyntaxException {
        channel = id;
        integration.id = session.user.userID;
        integration.curseUsername = session.user.username;
        webSocket = new WebSocket(loginResponse, session, new URI(Apis.NOTIFICATIONS));
        webSocket.addTask(new CurseMessage(integration.sink, integration.serverName, integration.channelName, channel, integration.id, integration.username),
                NotificationsServiceContractType.CONVERSATION_MESSAGE_NOTIFICATION);
        LogHelper.info("starting curse websocket connection");
        webSocket.start();
        LogHelper.info("curse websocket connection started");
        webSocket.sendMessage(channel, "Thump Curse bridge Connected!");
    }
}
