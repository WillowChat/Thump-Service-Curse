package engineer.carrot.warren.thump.cursevoice

import com.feed_the_beast.javacurselib.common.enums.DevicePlatform
import com.feed_the_beast.javacurselib.rest.REST
import com.feed_the_beast.javacurselib.service.logins.login.LoginRequest
import com.feed_the_beast.javacurselib.service.logins.login.LoginResponse
import com.feed_the_beast.javacurselib.service.sessions.sessions.CreateSessionRequest
import com.feed_the_beast.javacurselib.utils.CurseGUID
import engineer.carrot.warren.thump.api.*
import engineer.carrot.warren.thump.cursevoice.command.CurseVoicePluginCommandHandler
import engineer.carrot.warren.thump.helper.LogHelper
import retrofit2.adapter.java8.HttpException

@ThumpServicePlugin
object CurseVoicePlugin : IThumpServicePlugin {
    override val id = "cursevoice"
    override val commandHandler = CurseVoicePluginCommandHandler()
    private lateinit var sink: IThumpMinecraftSink
    private lateinit var integration : CurseIntegration

    override fun configure(context: ThumpPluginContext) {
        sink = context.minecraftSink
        integration = CurseIntegration("username", "password")

        LogHelper.info("Curse Voice plugin configured")
    }

    override fun start() {
        integration.init()
        LogHelper.info("Curse Voice plugin started")
    }

    override fun stop() {
        integration.shutdown()
        LogHelper.info("Curse Voice plugin stopped")
    }

    override fun status(): List<String> {
        return listOf()
    }

    override fun onMinecraftMessage(message: String) {
        integration.sendToCurse(message)
        LogHelper.info("Curse Voice plugin got message: $message")
    }

    override fun anyConnectionsMatch(name: String): Boolean {
        return false
    }

}