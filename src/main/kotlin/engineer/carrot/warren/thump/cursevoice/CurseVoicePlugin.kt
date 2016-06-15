package engineer.carrot.warren.thump.cursevoice

import engineer.carrot.warren.thump.api.*
import engineer.carrot.warren.thump.cursevoice.command.CurseVoicePluginCommandHandler
import engineer.carrot.warren.thump.helper.LogHelper

@ThumpServicePlugin
object CurseVoicePlugin : IThumpServicePlugin {
    override val id = "cursevoice"
    override val commandHandler = CurseVoicePluginCommandHandler()

    private lateinit var sink: IThumpMinecraftSink

    override fun configure(context: ThumpPluginContext) {
        sink = context.minecraftSink

        LogHelper.info("Curse Voice plugin configured")
    }

    override fun start() {
        LogHelper.info("Curse Voice plugin started")
    }

    override fun stop() {
        LogHelper.info("Curse Voice plugin stopped")
    }

    override fun status(): List<String> {
        return listOf()
    }

    override fun onMinecraftMessage(message: String) {
        LogHelper.info("Curse Voice plugin got message: $message")
    }

    override fun anyConnectionsMatch(name: String): Boolean {
        return false
    }

}