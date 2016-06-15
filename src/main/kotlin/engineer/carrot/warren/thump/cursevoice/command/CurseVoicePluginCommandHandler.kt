package engineer.carrot.warren.thump.cursevoice.command

import engineer.carrot.warren.thump.api.ICommandHandler
import net.minecraft.command.ICommandSender

class CurseVoicePluginCommandHandler : ICommandHandler {
    override val command = "cursevoice"
    override val usage = "$command ???"

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {

    }

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return listOf()
    }

}