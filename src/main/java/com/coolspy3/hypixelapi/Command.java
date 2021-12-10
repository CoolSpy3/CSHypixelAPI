package com.coolspy3.hypixelapi;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.coolspy3.csmodloader.network.PacketHandler;
import com.coolspy3.csmodloader.network.SubscribeToPacketStream;
import com.coolspy3.cspackets.datatypes.MCColor;
import com.coolspy3.cspackets.packets.ClientChatSendPacket;
import com.coolspy3.util.ModUtil;

public class Command
{

    public static final String regex = "/linkhypixelapi " + HypixelAPI.uuidRegex + "( .*)?";
    public static final Pattern pattern = Pattern.compile(regex);
    private HypixelAPI mod;

    public Command(HypixelAPI mod)
    {
        this.mod = mod;
    }

    @SubscribeToPacketStream
    public boolean register(ClientChatSendPacket event)
    {
        String msg = event.msg;
        if (msg.matches("/linkhypixelapi( .*)?"))
        {
            Matcher matcher = pattern.matcher(msg);
            if (msg.matches("/linkhypixelapi new(.*)?"))
            {
                ModUtil.sendMessage(MCColor.AQUA + "Generating New API Key...");
                mod.awaitAPIKey = true;
                PacketHandler.getLocal().sendPacket(new ClientChatSendPacket("/api new"));
            }
            else if (matcher.matches())
            {
                try
                {
                    String apiKey = matcher.group(1);
                    APIConfig.getInstance().apiKey = UUID.fromString(apiKey);
                    ModUtil.sendMessage(MCColor.AQUA + "API key set to: \"" + apiKey + "\"");
                    APIConfig.save();
                }
                catch (IllegalArgumentException e)
                {
                    ModUtil.sendMessage(MCColor.RED + e.getMessage());
                }
                catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }
            }
            else if (msg.matches("/linkhypixelapi ?"))
            {
                ModUtil.sendMessage(
                        MCColor.YELLOW + "WARNING: This will overwrite any existing API keys");
                ModUtil.sendMessage(MCColor.AQUA + "Type \"/linkhypixelapi new\" to continue");
                ModUtil.sendMessage(MCColor.AQUA
                        + "Or type \"/linkhypixelapi <API key>\" to link an existing API key");
            }
            else
            {
                ModUtil.sendMessage(MCColor.RED + "Invalid API key: \""
                        + msg.substring("/linkhypixelapi ".length()) + "\"");
            }

            return true;
        }

        return false;
    }

}
