package engineer.carrot.warren.thump.cursevoice;

import com.feed_the_beast.javacurselib.websocket.WebSocket;
import com.feed_the_beast.javacurselib.websocket.messages.handler.tasks.Task;
import com.feed_the_beast.javacurselib.websocket.messages.notifications.ConversationMessageNotification;
import engineer.carrot.warren.thump.api.IThumpMinecraftSink;
import engineer.carrot.warren.thump.helper.LogHelper;

import javax.annotation.Nonnull;

public class CurseMessage implements Task<ConversationMessageNotification> {
    private IThumpMinecraftSink sink;
    public CurseMessage(IThumpMinecraftSink sink) {
        this.sink = sink;
    }
    @Override
    public void execute (@Nonnull WebSocket webSocket, @Nonnull ConversationMessageNotification response) {
        if(!response.isDeleted && response.editedUsername != null)
        switch (response.conversationType) {
        case GROUP: {
            //TODO put code here to send to MC this is the info needed to identify where it came from etc.
            //contacts.getGroupNamebyId(response.rootConversationID).orElse("unknown")
            //contacts.getChannelNamebyId(response.conversationID).orElse("unknown")
            LogHelper.info("serverID {} channelID {} from {}, Msg: {}", response.rootConversationID, response.conversationID, response.senderName, response.body);
            sink.sendToAllPlayers(response.senderName, response.body);
            break;
        }
        }
    }

}
