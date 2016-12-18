package engineer.carrot.warren.thump.curse;

import com.feed_the_beast.javacurselib.utils.CurseGUID;
import com.feed_the_beast.javacurselib.websocket.WebSocket;
import com.feed_the_beast.javacurselib.websocket.messages.handler.tasks.Task;
import com.feed_the_beast.javacurselib.websocket.messages.notifications.ConversationMessageNotification;
import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;
import engineer.carrot.warren.thump.helper.StringHelper;
import engineer.carrot.warren.thump.helper.TokenHelper;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CurseMessage implements Task<ConversationMessageNotification> {
    private IThumpMinecraftSink sink;
    private String serverName;
    private String channelName;
    private CurseGUID channelGuid;
    private int configId;
    private String configUser;

    public CurseMessage (IThumpMinecraftSink sink, String serverName, String channelName, CurseGUID channelGuid, int configId, String configUser) {
        this.sink = sink;
        this.serverName = serverName;
        this.channelName = channelName;
        this.channelGuid = channelGuid;
        this.configId = configId;
        this.configUser = configUser;
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
                        .applyTokens(CursePlugin.curseToMcMessageFormat);
                s = StringHelper.INSTANCE.stripBlacklistedIRCCharacters(s);
                String un = response.senderName;
                if (configId == response.senderID) {//we only need to swap this if its the user from the config
                    un = configUser;
                }
                sink.sendToAllPlayers(un, new TextComponentString(s));
                break;
            }
            }
        }
    }

}
