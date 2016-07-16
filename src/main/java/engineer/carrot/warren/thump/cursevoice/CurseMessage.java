package engineer.carrot.warren.thump.cursevoice;

import com.feed_the_beast.javacurselib.utils.CurseGUID;
import com.feed_the_beast.javacurselib.websocket.WebSocket;
import com.feed_the_beast.javacurselib.websocket.messages.handler.tasks.Task;
import com.feed_the_beast.javacurselib.websocket.messages.notifications.ConversationMessageNotification;
import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;
import engineer.carrot.warren.thump.helper.StringHelper;
import engineer.carrot.warren.thump.helper.TokenHelper;

import javax.annotation.Nonnull;

public class CurseMessage implements Task<ConversationMessageNotification> {
    private IThumpMinecraftSink sink;
    private String serverName;
    private String channelName;
    private CurseGUID channelGuid;

    public CurseMessage (IThumpMinecraftSink sink, String serverName, String channelName, CurseGUID channelGuid) {
        this.sink = sink;
        this.serverName = serverName;
        this.channelName = channelName;
        this.channelGuid = channelGuid;
    }

    @Override
    public void execute (@Nonnull WebSocket webSocket, @Nonnull ConversationMessageNotification response) {
        if (!response.isDeleted && response.conversationID.equals(channelGuid))//do we want edited messages to not go through again?
        {
            switch (response.conversationType) {
            case GROUP: {
                LogHelper.info("serverID {} channelID {} from {}, Msg: {}", response.rootConversationID, response.conversationID, response.senderName, response.body);
                TokenHelper t = new TokenHelper();
                String s = t.addUserToken(response.senderName).addMessageToken(response.body).addChannelToken(channelName).addServerToken(serverName)
                        .applyTokens(CurseVoicePlugin.curseToMcMessageFormat);
                StringHelper.INSTANCE.stripBlacklistedIRCCharacters(s);
                sink.sendToAllPlayers(response.senderName, s);
                break;
            }
            }
        }
    }

}
