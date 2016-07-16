package engineer.carrot.warren.thump.cursevoice

import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.api.IThumpServicePlugin
import engineer.carrot.warren.thump.api.ThumpPluginContext
import engineer.carrot.warren.thump.api.ThumpServicePlugin
import engineer.carrot.warren.thump.cursevoice.command.CurseVoicePluginCommandHandler
import engineer.carrot.warren.thump.helper.LogHelper

@ThumpServicePlugin
object CurseVoicePlugin : IThumpServicePlugin {
    override val id = "cursevoice"
    override val commandHandler = CurseVoicePluginCommandHandler()
    private lateinit var sink: IThumpMinecraftSink
    private lateinit var username: String
    private lateinit var integration: CurseIntegration
    public lateinit var curseToMcMessageFormat: String

    override fun configure(context: ThumpPluginContext) {
        sink = context.minecraftSink
        val config = context.configuration
        config.load()
        val CREDENTIALS_CATEGORY = "credentials"
        val CONNECTION_CATEGORY = "connection"
        val CONFIG_CATEGORY = "config"
        username = config.get(CREDENTIALS_CATEGORY, "username", "", "username for curseapp").string
        val password = config.get(CREDENTIALS_CATEGORY, "password", "", "password for curseapp").string
        val server = config.get(CONNECTION_CATEGORY, "server", "server", "server to use for curseapp").string
        val channel = config.get(CONNECTION_CATEGORY, "channel", "channel", "channel for curseapp").string
        config.setCategoryComment(CONFIG_CATEGORY, "Formatting tokens: {u} -> user, {s} -> server, {c} -> channel, {m} -> message\nNote that only tokens listed in the defaults are supported for each message!")
        curseToMcMessageFormat = config.get(CONFIG_CATEGORY, "curseToMCFormat", "{s} {c}: <{u}> {m}", "[default: {s} {c}: <{u}> {m}]").string

        if (config.hasChanged())
            config.save()


        integration = CurseIntegration(username, password, server, channel, sink)

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
        return name.equals(username)
    }

}